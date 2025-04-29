/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/


import { AfterViewInit, Component, inject, Input, NgZone, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { Part } from '@page/parts/model/parts.model';
import { State } from '@shared/model/state';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { RelationComponentState } from '@shared/modules/relations/core/component.state';
import { LoadedElementsFacade } from '@shared/modules/relations/core/loaded-elements.facade';
import { RelationsAssembler } from '@shared/modules/relations/core/relations.assembler';
import { RelationsFacade } from '@shared/modules/relations/core/relations.facade';
import {
  OpenElements,
  TreeData,
  TreeDirection,
  TreeElement,
  TreeStructure,
} from '@shared/modules/relations/model/relations.model';
import Minimap from '@shared/modules/relations/presentation/minimap/minimap.d3';
import Tree from '@shared/modules/relations/presentation/tree/tree.d3';
import { combineLatest, Observable, Subscription } from 'rxjs';
import { debounceTime, tap } from 'rxjs/operators';

@Component({
  selector: 'app-tree',
  template: '',
  encapsulation: ViewEncapsulation.None,
  providers: [ RelationComponentState, RelationsFacade ],
})
export class TreeComponent implements OnInit, OnDestroy, AfterViewInit {
  @Input() showMiniMap = false;
  @Input() shouldRenderParents = false;
  @Input() isStandalone = true;
  @Input() htmlId: string;
  @Input() overwriteContext: string = undefined;

  public readonly subscriptions = new Subscription();
  public readonly rootPart$: Observable<View<Part>>;
  private isComponentInitialized = false;
  private resizeSub: Subscription;
  private treeDirection: TreeDirection;
  private _rootPart$ = new State<View<Part>>({ loader: true });
  private tree: Tree;
  private minimap: Minimap;
  private activatedRoute = inject(ActivatedRoute);
  private context: string;
  private mainAspectType: MainAspectType = null;


  constructor(
    private readonly partDetailsFacade: PartDetailsFacade,
    private readonly relationsFacade: RelationsFacade,
    private readonly loadedElementsFacade: LoadedElementsFacade,
    private readonly router: Router,
    private readonly ngZone: NgZone,
  ) {
    this.rootPart$ = this._rootPart$.observable;
    this.context = this.activatedRoute?.parent?.toString().split('\'')[1];
  }

  @Input() set direction(_direction: 'LEFT' | 'RIGHT') {
    this.treeDirection = TreeDirection[_direction];

    if (this.mainAspectType !== null) {
      this.relationsFacade.isParentRelationTree = _direction === 'LEFT';
      const sub = this.relationsFacade.initRequestPartDetailQueue(this.mainAspectType).subscribe();
      this.subscriptions.add(sub);
    }
  }

  @Input() set rootPart(data: View<Part>) {
    if (data) {
      this.mainAspectType = data.data.mainAspectType;
      this.direction = this.treeDirection;
    }
    this.resetTree(data || {});
  }

  @Input() set resizeTrigger$(resize$: Observable<number>) {
    this.resizeSub?.unsubscribe();
    this.resizeSub = resize$.subscribe(resize => this.tree?.changeSize?.(resize));
  }

  public ngOnInit(): void {
    if (this.overwriteContext) {
      this.context = this.overwriteContext;
    }
  }

  public ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
    this.resizeSub.unsubscribe();
    this.tree = undefined;
  }

  public ngAfterViewInit(): void {
    this.isComponentInitialized = true;
    const combined = combineLatest([ this.relationsFacade.openElements$, this.loadedElementsFacade.loadedElements$ ]);
    const openElementsSubscription = combined
      .pipe(
        debounceTime(100),
        tap(([ openElements ]) => this.renderTreeWithOpenElements(openElements)),
      )
      .subscribe();

    this.subscriptions.add(openElementsSubscription);
  }

  private resetTree({ data }: View<Part>): void {
    if (!this.isComponentInitialized) return;

    this.relationsFacade.resetRelationState();
    if (!data) return;

    const rootPart = RelationsAssembler.assemblePartForRelation(data);
    this.loadedElementsFacade.addLoadedElement(rootPart);
    this.relationsFacade.openElementWithChildren(rootPart);
  }

  private initTree(): void {
    const id = this.htmlId + '--' + this.treeDirection;

    const treeConfigRight: TreeData = {
      id,
      mainId: this.htmlId,
      openDetails: this.openDetails.bind(this),
      defaultZoom: this.isStandalone ? 1 : 0.7,
      updateChildren: this.updateChildren.bind(this),
    };

    this.tree = !this.tree ? new Tree(treeConfigRight) : this.tree;

    if (!this.showMiniMap) return;
    this.minimap = new Minimap(this.tree, this.treeDirection);
  }

  private updateChildren({ id }: TreeElement): void {
    // as d3.js handles rendering of relations, we can get some performances boost by avoiding
    // all impure pipe computations as side effects for this operation
    this.ngZone.runOutsideAngular(() => {
      !this.relationsFacade.isElementOpen(id)
        ? this.relationsFacade.openElementById(id)
        : this.relationsFacade.closeElementById(id);
    });
  }

  private openDetails({ id }: TreeElement): void {
    this.subscriptions.add(this.partDetailsFacade.setPartFromTree(id).subscribe());
    this.router.navigate([ `/${ this.context }/${ id }` ], { queryParams: { isAsBuilt: this.partDetailsFacade.mainAspectType === MainAspectType.AS_BUILT } });
  }

  private renderTreeWithOpenElements(openElements: OpenElements): void {
    if (!openElements) return;

    const treeData = this.relationsFacade.formatOpenElementsToTreeData(openElements);
    if (!treeData?.id) return;

    this.initTree();
    this.renderTree(treeData);
  }

  private renderTree(treeData: TreeStructure): void {
    this.tree.renderTree(treeData, this.treeDirection);
    this.renderMinimap(treeData);
  }

  private renderMinimap(treeData: TreeStructure): void {
    if (!this.showMiniMap) return;

    this.minimap.renderMinimap(treeData);
  }
}
