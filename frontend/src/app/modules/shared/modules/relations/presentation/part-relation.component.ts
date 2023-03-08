/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

import { AfterViewInit, Component, Input, NgZone, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Part } from '@page/parts/model/parts.model';
import { State } from '@shared/model/state';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { RelationComponentState } from '@shared/modules/relations/core/component.state';
import { LoadedElementsFacade } from '@shared/modules/relations/core/loaded-elements.facade';
import { RelationsAssembler } from '@shared/modules/relations/core/relations.assembler';
import { RelationsFacade } from '@shared/modules/relations/core/relations.facade';
import { OpenElements, TreeData, TreeElement, TreeStructure } from '@shared/modules/relations/model/relations.model';
import { StaticIdService } from '@shared/service/staticId.service';
import { combineLatest, Observable, Subscription } from 'rxjs';
import { debounceTime, filter, map, switchMap, takeWhile, tap } from 'rxjs/operators';
import Minimap from './minimap/minimap.d3';
import Tree from './tree/tree.d3';

@Component({
  selector: 'app-part-relation',
  templateUrl: './part-relation.component.html',
  styleUrls: ['./part-relation.component.scss'],
  encapsulation: ViewEncapsulation.None,
  providers: [RelationComponentState, RelationsFacade],
  host: {
    'class.app-part-relation-host': 'isStandalone',
  },
})
export class PartRelationComponent implements OnInit, OnDestroy, AfterViewInit {
  @Input() isStandalone = true;
  @Input() showMiniMap = true;

  public readonly htmlIdBase = 'app-part-relation-';
  public readonly subscriptions = new Subscription();
  public readonly rootPart$: Observable<View<Part>>;
  public readonly htmlId: string;

  private _rootPart$ = new State<View<Part>>({ loader: true });
  private tree: Tree;
  private minimap: Minimap;
  private treeData: TreeStructure;

  constructor(
    private readonly partDetailsFacade: PartDetailsFacade,
    private readonly relationsFacade: RelationsFacade,
    private readonly loadedElementsFacade: LoadedElementsFacade,
    private readonly route: ActivatedRoute,
    private readonly ngZone: NgZone,
    staticIdService: StaticIdService,
  ) {
    this.rootPart$ = this._rootPart$.observable;
    this.htmlId = staticIdService.generateId(this.htmlIdBase);
  }

  public ngOnInit(): void {
    const initSubscription = this.route.paramMap
      .pipe(
        switchMap(params => {
          if (this.partDetailsFacade.selectedPart) {
            return this.partDetailsFacade.selectedPart$;
          }

          const partId = params.get('partId');
          return partId ? this.relationsFacade.getRootPart(partId) : this.partDetailsFacade.selectedPart$;
        }),
        tap(viewData => this._rootPart$.update(viewData)),
        takeWhile(({ data }) => !data, true),
      )
      .subscribe();
    this.subscriptions.add(initSubscription);
  }

  public ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
    this.tree = undefined;
  }

  public ngAfterViewInit(): void {
    this.initListeners();
  }

  private initListeners(): void {
    const selectSubscription = this.rootPart$
      .pipe(
        tap(_ => this.relationsFacade.resetRelationState()),
        filter(({ data }) => !!data),
        map(({ data }) => RelationsAssembler.assemblePartForRelation(data)),
      )
      .subscribe({
        next: rootPart => {
          this.loadedElementsFacade.addLoadedElement(rootPart);
          this.relationsFacade.openElementWithChildren(rootPart);
        },
      });

    const combined = combineLatest([this.relationsFacade.openElements$, this.loadedElementsFacade.loadedElements$]);
    const openElementsSubscription = combined
      .pipe(
        debounceTime(100),
        tap(([openElements]) => this.renderTreeWithOpenElements(openElements)),
      )
      .subscribe();

    this.subscriptions.add(selectSubscription);
    this.subscriptions.add(openElementsSubscription);
  }

  private initTree(): void {
    const treeConfig: TreeData = {
      id: this.htmlId,
      openDetails: this.isStandalone ? this.openDetails.bind(this) : _ => null,
      defaultZoom: this.isStandalone ? 1 : 0.7,
      updateChildren: this.updateChildren.bind(this),
    };

    this.tree = new Tree(treeConfig);

    if (!this.showMiniMap) {
      return;
    }

    this.minimap = new Minimap(this.tree);
  }

  private updateChildren({ id }: TreeElement): void {
    // as d3.js handles rendering of relations, we can get some performance boost by avoiding
    // all impure pipe computations as side effects for this operation
    this.ngZone.runOutsideAngular(() => {
      !this.relationsFacade.isElementOpen(id)
        ? this.relationsFacade.openElementById(id)
        : this.relationsFacade.closeElementById(id);
    });
  }

  private openDetails({ id }: TreeElement): void {
    this.subscriptions.add(this.partDetailsFacade.setPartFromTree(id).subscribe());
  }

  private renderTreeWithOpenElements(openElements: OpenElements): void {
    const treeData = this.relationsFacade.formatOpenElementsToTreeData(openElements);
    if (!treeData || !treeData.id) {
      return;
    }

    if (!this.tree) {
      this.initTree();
    }

    this.treeData = treeData;
    this.renderTree(treeData);
  }

  private renderTree(treeData: TreeStructure): void {
    this.tree.renderTree(treeData);
    this.renderMinimap(treeData);
  }

  private renderMinimap(treeData: TreeStructure): void {
    if (!this.showMiniMap) {
      return;
    }
    this.minimap.renderMinimap(treeData);
  }

  public increaseSize(): void {
    this.tree.changeSize(0.1);
  }

  public decreaseSize(): void {
    this.tree.changeSize(-0.1);
  }
}
