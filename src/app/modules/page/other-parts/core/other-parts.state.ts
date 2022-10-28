/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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
  private readonly _customerParts$: State<View<Pagination<Part>>> = new State<View<Pagination<Part>>>({ loader: true });
  private readonly _supplierParts$: State<View<Pagination<Part>>> = new State<View<Pagination<Part>>>({ loader: true });

  public get customerParts$(): Observable<View<Pagination<Part>>> {
    return this._customerParts$.observable;
  }

  public get supplierParts$(): Observable<View<Pagination<Part>>> {
    return this._supplierParts$.observable;
  }

  public set customerParts({ data, loader, error }: View<Pagination<Part>>) {
    const partsView: View<Pagination<Part>> = { data, loader, error };
    this._customerParts$.update(partsView);
  }

  public set supplierParts({ data, loader, error }: View<Pagination<Part>>) {
    const partsView: View<Pagination<Part>> = { data, loader, error };
    this._supplierParts$.update(partsView);
  }

  public get supplierParts(): View<Pagination<Part>> {
    return this._supplierParts$.snapshot;
  }
}
