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
export class OtherPartsState {
  private readonly _customerPartsAsBuilt$: State<View<Pagination<Part>>> = new State<View<Pagination<Part>>>({ loader: true });
  private readonly _customerPartsAsPlanned$: State<View<Pagination<Part>>> = new State<View<Pagination<Part>>>({ loader: true });


  private readonly _supplierPartsAsBuilt$: State<View<Pagination<Part>>> = new State<View<Pagination<Part>>>({ loader: true });
  private readonly _supplierPartsAsPlanned$: State<View<Pagination<Part>>> = new State<View<Pagination<Part>>>({ loader: true });
  public get customerPartsAsBuilt$(): Observable<View<Pagination<Part>>> {
    return this._customerPartsAsBuilt$.observable;
  }

  public get customerPartsAsPlanned$(): Observable<View<Pagination<Part>>> {
    return this._customerPartsAsPlanned$.observable;
  }

  public get supplierPartsAsBuilt$(): Observable<View<Pagination<Part>>> {
    return this._supplierPartsAsBuilt$.observable;
  }

  public get supplierPartsAsPlanned$(): Observable<View<Pagination<Part>>> {
    return this._supplierPartsAsPlanned$.observable;
  }

  public set customerPartsAsBuilt({ data, loader, error }: View<Pagination<Part>>) {
    const partsView: View<Pagination<Part>> = { data, loader, error };
    this._customerPartsAsBuilt$.update(partsView);
  }

  public set customerPartsAsPlanned({ data, loader, error }: View<Pagination<Part>>) {
    const partsView: View<Pagination<Part>> = { data, loader, error };
    this._customerPartsAsPlanned$.update(partsView);
  }

  public set supplierPartsAsBuilt({ data, loader, error }: View<Pagination<Part>>) {
    const partsView: View<Pagination<Part>> = { data, loader, error };
    this._supplierPartsAsBuilt$.update(partsView);
  }

  public get supplierPartsAsBuilt(): View<Pagination<Part>> {
    return this._supplierPartsAsBuilt$.snapshot;
  }

  public set supplierPartsAsPlanned({ data, loader, error }: View<Pagination<Part>>) {
    const partsView: View<Pagination<Part>> = { data, loader, error };
    this._supplierPartsAsPlanned$.update(partsView);
  }

  public get supplierPartsAsPlanned(): View<Pagination<Part>> {
    return this._supplierPartsAsPlanned$.snapshot;
  }

}
