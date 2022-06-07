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

import { AfterViewInit, Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PartsFacade } from '@page/parts/core/parts.facade';
import { Part } from '@page/parts/model/parts.model';
import { RelationsAssembler } from '@page/parts/relations/core/relations.assembler';
import { RelationsFacade } from '@page/parts/relations/core/relations.facade';
import { OpenElements, TreeElement, TreeStructure } from '@page/parts/relations/model/relations.model';
import { View } from '@shared';
import * as d3 from 'd3';
import { Observable, Subscription } from 'rxjs';
import { filter, first, map, switchMap, tap } from 'rxjs/operators';
import RelationTree from './d3.tree';

@Component({
  selector: 'app-part-relation',
  templateUrl: './part-relation.component.html',
  styleUrls: ['./part-relation.component.scss'],
})
export class PartRelationComponent implements OnInit, OnDestroy, AfterViewInit {
  constructor(
    private readonly partsFacade: PartsFacade,
    private readonly relationsFacade: RelationsFacade,
    private readonly route: ActivatedRoute,
  ) {
    this.selectedPart$ = this.partsFacade.selectedPart$;
  }

  @ViewChild('treeBase') testElement: ElementRef<HTMLElement>;
  public subscriptions = new Subscription();
  public selectedPart$: Observable<View<Part>>;

  ngOnInit() {
    this.partsFacade.selectedPart$
      .pipe(
        first(),
        filter(({ data }) => !data),
        switchMap(_ => {
          const partId = this.route.snapshot.paramMap.get('partId');
          return this.partsFacade.setPart(partId);
        }),
      )
      .subscribe();
  }

  ngOnDestroy() {
    this.subscriptions.unsubscribe();
  }

  ngAfterViewInit() {
    this.initTree();
  }

  public initTree(): void {
    // ToDo: Error handling for loading open elements (viewContainer?)
    const selectSubscription = this.partsFacade.selectedPart$
      .pipe(
        tap(_ => this.relationsFacade.resetRelationState()),
        filter(({ data }) => !!data),
        map(({ data }) => RelationsAssembler.assemblePartForRelation(data)),
      )
      .subscribe({
        next: selectedPart => {
          this.relationsFacade.addLoadedElement(selectedPart);
          this.relationsFacade.openElementWithChildren(selectedPart);
        },
      });

    const openElementsSubscription = this.relationsFacade.openElements$
      .pipe(tap(openElements => this.renderTree(openElements)))
      .pipe(tap(openElements => this.renderTree(openElements)))
      .subscribe();

    this.subscriptions.add(selectSubscription);
    this.subscriptions.add(openElementsSubscription);
  }

  private onUpdateData({ id }: TreeElement): void {
    const currentElement = this.relationsFacade.loadedElements[id];

    if (!currentElement.children) {
      console.warn('clicked element is loading!');
      return;
    }

    !this.relationsFacade.isElementOpen(id)
      ? this.relationsFacade.openElementById(id)
      : this.relationsFacade.closeElementById(id);
  }

  private renderTree(openElements: OpenElements): void {
    const treeData = this.relationsFacade.formatOpenElementsToTreeData(openElements);
    if (!treeData) {
      return;
    }

    d3.select('svg').remove();
    RelationTree(treeData, {
      label: ({ id }) => id,
      title: ({ id, name }) => name || id,
      mainElement: d3.select('#treeBase'),
      onClick: this.onUpdateData.bind(this),
    });
  }
}
