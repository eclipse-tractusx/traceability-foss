/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { AfterViewInit, Component, Input, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PartsFacade } from '@page/parts/core/parts.facade';
import { Part } from '@page/parts/model/parts.model';
import { RelationComponentState } from '@page/parts/relations/core/component.state';
import { LoadedElementsFacade } from '@page/parts/relations/core/loaded-elements.facade';
import { RelationsAssembler } from '@page/parts/relations/core/relations.assembler';
import { RelationsFacade } from '@page/parts/relations/core/relations.facade';
import { OpenElements, TreeData, TreeElement } from '@page/parts/relations/model/relations.model';
import { State, View } from '@shared';
import { StaticIdService } from '@shared/service/staticId.service';
import * as d3 from 'd3';
import { combineLatest, Observable, Subscription } from 'rxjs';
import { debounceTime, delay, filter, map, switchMap, takeWhile, tap } from 'rxjs/operators';
import RelationTree from './d3.tree';

@Component({
  selector: 'app-part-relation',
  templateUrl: './part-relation.component.html',
  styleUrls: ['./part-relation.component.scss'],
  encapsulation: ViewEncapsulation.None,
  providers: [RelationComponentState, RelationsFacade],
})
export class PartRelationComponent implements OnInit, OnDestroy, AfterViewInit {
  @Input() isStandalone = true;
  @Input() scale: number;

  public readonly htmlIdBase = 'app-part-relation-';
  public subscriptions = new Subscription();
  public rootPart$: Observable<View<Part>>;
  public htmlId: string;

  private _rootPart$ = new State<View<Part>>({ loader: true });
  private tree: RelationTree;

  constructor(
    private readonly partsFacade: PartsFacade,
    private readonly relationsFacade: RelationsFacade,
    private readonly loadedElementsFacade: LoadedElementsFacade,
    private readonly route: ActivatedRoute,
    staticIdService: StaticIdService,
  ) {
    this.rootPart$ = this._rootPart$.observable.pipe(delay(0), debounceTime(100));
    this.htmlId = staticIdService.generateId(this.htmlIdBase);
  }

  ngOnInit() {
    const initSubscription = this.route.paramMap
      .pipe(
        switchMap(params => {
          if (this.partsFacade.selectedPart) {
            return this.partsFacade.selectedPart$;
          }

          const partId = params.get('partId');
          return partId ? this.relationsFacade.getRootPart(partId) : this.partsFacade.selectedPart$;
        }),
        tap(viewData => this._rootPart$.update(viewData)),
        takeWhile(({ data }) => !data, true),
      )
      .subscribe();
    this.subscriptions.add(initSubscription);
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
    this.tree = undefined;
  }

  ngAfterViewInit() {
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
        tap(([openElements]) => this.renderTree(openElements)),
      )
      .subscribe();

    this.subscriptions.add(selectSubscription);
    this.subscriptions.add(openElementsSubscription);
  }

  private initTree(): void {
    const treeConfig: TreeData = {
      id: this.htmlId,
      mainElement: d3.select(`#${this.htmlId}`),
      openDetails: this.isStandalone ? this.openDetails.bind(this) : _ => null,
      updateChildren: this.updateChildren.bind(this),
      scale: this.scale,
    };

    this.tree = new RelationTree(treeConfig);
  }

  private updateChildren({ id }: TreeElement): void {
    !this.relationsFacade.isElementOpen(id)
      ? this.relationsFacade.openElementById(id)
      : this.relationsFacade.closeElementById(id);
  }

  private openDetails({ id }: TreeElement): void {
    this.subscriptions.add(this.partsFacade.setPart(id).subscribe());
  }

  private renderTree(openElements: OpenElements): void {
    const treeData = this.relationsFacade.formatOpenElementsToTreeData(openElements);
    if (!treeData || !treeData.id) {
      return;
    }

    if (!this.tree) {
      this.initTree();
    }

    d3.select(`#${this.htmlId}-svg`).remove();
    this.tree.renderTree(treeData);
  }
}
