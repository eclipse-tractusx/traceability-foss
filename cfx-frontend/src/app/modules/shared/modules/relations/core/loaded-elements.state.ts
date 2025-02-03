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
import { State } from '@shared/model/state';
import { LoadedElements } from '@shared/modules/relations/model/relations.model';
import { Observable } from 'rxjs';

@Injectable()
export class LoadedElementsState {
  private readonly _loadedElements$: State<LoadedElements> = new State<LoadedElements>({});

  public get loadedElements(): LoadedElements {
    return this._loadedElements$.snapshot;
  }

  public set loadedElements(data: LoadedElements) {
    this._loadedElements$.update(data);
  }

  public get loadedElements$(): Observable<LoadedElements> {
    return this._loadedElements$.observable;
  }

  public resetLoadedElements(): void {
    this._loadedElements$.reset();
  }
}
