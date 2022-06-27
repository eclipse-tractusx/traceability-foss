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
import { LoadedElementsState } from '@page/parts/relations/core/loaded-elements.state';
import { RelationsAssembler } from '@page/parts/relations/core/relations.assembler';
import { LoadedElements, TreeElement } from '@page/parts/relations/model/relations.model';
import { Observable } from 'rxjs';

@Injectable()
export class LoadedElementsFacade {
  constructor(private loadedElementsState: LoadedElementsState) {}

  get loadedElements(): LoadedElements {
    return this.loadedElementsState.loadedElements;
  }

  get loadedElements$(): Observable<LoadedElements> {
    return this.loadedElementsState.loadedElements$;
  }

  public addLoadedElement(element: TreeElement) {
    const { id, children } = element;
    const loadingChildren = children?.reduce((p: LoadedElements, c: string) => {
      return { ...p, [c]: this.loadedElements[c] || RelationsAssembler.createLoadingElement(c) };
    }, {} as LoadedElements);

    this.loadedElementsState.loadedElements = {
      ...this.loadedElementsState.loadedElements,
      [id]: element,
      ...loadingChildren,
    };
  }
}
