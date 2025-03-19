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
import { Pagination } from '@core/model/pagination.model';
import { Part } from '@page/parts/model/parts.model';
import { State } from '@shared/model/state';
import { View } from '@shared/model/view.model';
import { Observable } from 'rxjs';

@Injectable()
export class PartsState {
  private readonly _partsAsBuilt$ = new State<View<Pagination<Part>>>({ loader: true });
  private readonly _partsAsBuiltSecond$ = new State<View<Pagination<Part>>>({ loader: true });
  private readonly _partsAsPlanned$ = new State<View<Pagination<Part>>>({ loader: true });
  private _globalSearchData: Pagination<Part>;


  get globalSearchData(): Pagination<Part> {
    return this._globalSearchData;
  }

  set globalSearchData(data: Pagination<Part>){
    this._globalSearchData = data;
  }

  public get partsAsBuilt$(): Observable<View<Pagination<Part>>> {
    return this._partsAsBuilt$.observable;
  }

  public get partsAsBuiltSecond$(): Observable<View<Pagination<Part>>> {
    return this._partsAsBuiltSecond$.observable;
  }

  public set partsAsBuiltSecond({ data, loader, error }: View<Pagination<Part>>) {
    const partsView: View<Pagination<Part>> = { data, loader, error };
    this._partsAsBuiltSecond$.update(partsView);
  }

  public get partsAsBuilt(): View<Pagination<Part>> {
    return this._partsAsBuilt$.snapshot;
  }

  public set partsAsBuilt({ data, loader, error }: View<Pagination<Part>>) {
    const partsView: View<Pagination<Part>> = { data, loader, error };
    this._partsAsBuilt$.update(partsView);
  }

  public get partsAsPlanned$(): Observable<View<Pagination<Part>>> {
    return this._partsAsPlanned$.observable;
  }

  public get partsAsPlanned(): View<Pagination<Part>> {
    return this._partsAsPlanned$.snapshot;
  }

  public set partsAsPlanned({ data, loader, error }: View<Pagination<Part>>) {
    const partsView: View<Pagination<Part>> = { data, loader, error };
    this._partsAsPlanned$.update(partsView);
  }


}
