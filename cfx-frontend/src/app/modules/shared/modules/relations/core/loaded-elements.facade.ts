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

import { Injectable } from '@angular/core';
import { LoadedElementsState } from '@shared/modules/relations/core/loaded-elements.state';
import { RelationsAssembler } from '@shared/modules/relations/core/relations.assembler';
import { LoadedElements, TreeElement } from '@shared/modules/relations/model/relations.model';
import { Observable } from 'rxjs';

@Injectable()
export class LoadedElementsFacade {
  constructor(private readonly loadedElementsState: LoadedElementsState) {
  }

  public get loadedElements(): LoadedElements {
    return this.loadedElementsState.loadedElements;
  }

  public get loadedElements$(): Observable<LoadedElements> {
    return this.loadedElementsState.loadedElements$;
  }

  public addLoadedElement(element: TreeElement): void {
    const { id, children } = element;

    const loadingChildren = children?.reduce((p: LoadedElements, childId: string) => {
      return { ...p, [childId]: this.loadedElements[childId] || RelationsAssembler.createLoadingElement(childId) };
    }, {} as LoadedElements);

    this.loadedElementsState.loadedElements = {
      ...this.loadedElementsState.loadedElements,
      [id]: element,
      ...loadingChildren,
    };
  }
}
