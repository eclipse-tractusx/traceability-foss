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

import { Injectable } from '@angular/core';
import { PartsService } from '@page/parts/core/parts.service';
import { Part } from '@page/parts/model/parts.model';
import { RelationsAssembler } from '@page/parts/relations/core/relations.assembler';
import { RelationsState } from '@page/parts/relations/core/relations.state';
import { LoadedElements, OpenElements, TreeElement, TreeStructure } from '@page/parts/relations/model/relations.model';
import { merge, Observable, of } from 'rxjs';
import { debounceTime, delay, filter, first, map, switchMap, tap, toArray } from 'rxjs/operators';
import _deepClone from 'lodash-es/cloneDeep';

@Injectable()
export class RelationsFacade {
  constructor(private partsService: PartsService, private relationsState: RelationsState) {}

  get openElements$(): Observable<OpenElements> {
    return this.relationsState.openElements$.pipe(delay(0), debounceTime(100));
  }

  get openElements(): OpenElements {
    return this.relationsState.openElements;
  }

  // This is used to add an element with its children to the opened list
  public openElementWithChildren({ id, children }: TreeElement): void {
    const emptyChildren: OpenElements = {};
    const childElements =
      children?.reduce((p: OpenElements, c: string) => ({ ...p, [c]: null }), emptyChildren) || emptyChildren;

    this.relationsState.openElements = { ...this.relationsState.openElements, [id]: children, ...childElements };
    this.loadChildrenInformation(id, children).subscribe();
  }

  // This is only to update already opened elements.
  public updateOpenElement({ id, children }: TreeElement): void {
    if (this.openElements[id] === undefined) {
      return;
    }

    this.relationsState.openElements = { ...this.relationsState.openElements, [id]: children };
    this.loadChildrenInformation(id, children).subscribe();
  }

  public deleteOpenElement(id: string): void {
    this.relationsState.openElements = this._deleteOpenElement(id, this.relationsState.openElements);
  }

  /*
   * Loaded elements section
   * */

  get loadedElements(): LoadedElements {
    return this.relationsState.loadedElements;
  }

  public addLoadedElement(element: TreeElement) {
    const { id, children } = element;
    const loadingChildren = children?.reduce((p: LoadedElements, c: string) => {
      return { ...p, [c]: this.loadedElements[c] || RelationsAssembler.createLoadingElement(c) };
    }, {} as LoadedElements);

    this.relationsState.loadedElements = { ...this.relationsState.loadedElements, [id]: element, ...loadingChildren };
  }

  public getRelation(partId: string, childId: string): Observable<Part> {
    return this.partsService.getRelation(partId, childId);
  }

  // ToDO: UGLY!!!
  public formatOpenElementsToTreeData(openElements: OpenElements): TreeStructure {
    const loadedData = this.relationsState.loadedElements;
    const mappedData: Record<string, TreeStructure> = {};
    let treeStructure: TreeStructure;
    const keyList = Object.keys(openElements);

    keyList.reverse().forEach(key => {
      mappedData[key] = RelationsAssembler.elementToTreeStructure(loadedData[key]);

      mappedData[key].children =
        openElements[key]?.map(childId => mappedData[childId] || null).filter(element => !!element) || [];
      treeStructure = mappedData[key];
    });

    return treeStructure;
  }

  public isElementOpen(id: string): boolean {
    const currentElement = this.openElements[id];
    if (!currentElement) {
      return !!currentElement;
    }

    // Checks if the children, of the current element, are open
    return currentElement.some(childId => Object.keys(this.openElements).includes(childId));
  }

  public resetRelationState(): void {
    this.relationsState.resetOpenElements();

    // Not resetting already loaded data keep the requests to a minimum.
    // this.relationsState.resetLoadedElements();
  }

  public openElementById(elementId: string): void {
    const elementToOpen = this.loadedElements[elementId];
    this.openElementWithChildren(elementToOpen);
  }

  public closeElementById(elementId: string): void {
    const elementToClose = this.loadedElements[elementId];
    elementToClose.children.forEach(childId => this.deleteOpenElement(childId));
  }

  private _deleteOpenElement(id: string, openElements: OpenElements): OpenElements {
    let clonedElements = _deepClone(openElements);
    if (clonedElements[id]?.length) {
      clonedElements[id].forEach(childId => (clonedElements = this._deleteOpenElement(childId, clonedElements)));
    }

    delete clonedElements[id];
    return clonedElements;
  }

  // TODO: Massiv!!!
  private loadChildrenInformation(
    parentId: string,
    children: string[],
    shouldLazyLoad = false,
  ): Observable<TreeElement[]> {
    if (!children) {
      return of(null).pipe(first());
    }

    if (children.every(childId => this.loadedElements[childId] && this.loadedElements[childId]?.state != 'loading')) {
      const mappedChildren = children.map(childId => this.loadedElements[childId]);
      this.addLoadedElements(mappedChildren);
      return of(mappedChildren).pipe(first());
    }

    const relationRequest = children.map(childId => this.getRelation(parentId, childId));
    return merge(...relationRequest).pipe(
      toArray(),
      first(),
      map(childrenData => childrenData.map(child => RelationsAssembler.assemblePartForRelation(child))),
      tap(childrenData => this.addLoadedElements(childrenData)),
      filter(_ => shouldLazyLoad),
      switchMap(childrenData =>
        merge(...childrenData.map(child => this.loadChildrenInformation(child.id, child.children))),
      ),
    );
  }

  private addLoadedElements(elements: TreeElement[]): void {
    elements.forEach(element => {
      this.addLoadedElement(element);
      this.updateOpenElement(element);
    });
  }
}
