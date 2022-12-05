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
import { OpenElements } from '@shared/modules/relations/model/relations.model';
import { Observable } from 'rxjs';

@Injectable()
export class RelationComponentState {
  private readonly _openElements$: State<OpenElements> = new State<OpenElements>({});

  public get openElements$(): Observable<OpenElements> {
    return this._openElements$.observable;
  }

  public get openElements() {
    return this._openElements$.snapshot;
  }

  public set openElements(data: OpenElements) {
    this._openElements$.update(data);
  }

  public resetOpenElements(): void {
    this._openElements$.reset();
  }
}
