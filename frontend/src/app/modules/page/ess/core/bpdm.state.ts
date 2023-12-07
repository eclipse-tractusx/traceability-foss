/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
import { PaginationBpdm } from '@core/model/pagination.bpdm.model';
import { LegalEntity } from '@page/ess/model/bpdm.model';
import { State } from '@shared/model/state';
import { View } from '@shared/model/view.model';
import { Observable } from 'rxjs';

@Injectable()
export class BpdmState {

  private readonly _legalEntities$ = new State<View<PaginationBpdm<LegalEntity>>>({ loader: true });

  public get legalEntities$(): Observable<View<PaginationBpdm<LegalEntity>>> {
    return this._legalEntities$.observable;
  }

  public set legalEntities({ data, loader, error }: View<PaginationBpdm<LegalEntity>>) {
    const partsView: View<PaginationBpdm<LegalEntity>> = { data, loader, error };
    this._legalEntities$.update(partsView);
  }

  public get legalEntities(): View<PaginationBpdm<LegalEntity>> {
    return this._legalEntities$.snapshot;
  }

  /* private readonly _countries$ = new State<View<Pagination<Country>>>({ loader: true });

  private readonly _siteBPNSs$ = new State<View<Site[]>>({ loader: true });

  public get legalEntities$(): Observable<View<PaginationBpdm<LegalEntity>>> {
    return this._legalEntities$.observable;
  }

  public set sites({ data, loader, error }: View<PaginationBpdm<LegalEntity>>) {
    const partsView: View<PaginationBpdm<LegalEntity>> = { data, loader, error };
    this._legalEntities$.update(partsView);
  }

  public get legalEntities(): View<PaginationBpdm<LegalEntity>> {
    return this._legalEntities$.snapshot;
  } */

  // ----------------------------------

  /* public get siteBPNSs$(): Observable<View<Site[]>> {
    return this._siteBPNSs$.observable;
  }

  public set siteBPNSs({ data, loader, error }: View<Site[]>) {
    const partsView: View<Site[]> = { data, loader, error };
    this._siteBPNSs$.update(partsView);
  }

  public get siteBPNSs(): View<Site[]> {
    return this._siteBPNSs$.snapshot;
  } */

  // ----------------------------------

  /* public get countries$(): Observable<View<Pagination<Country>>> {
    return this._countries$.observable;
  }

  public set countries({ data, loader, error }: View<Pagination<Country>>) {
    const countriesView: View<Pagination<Country>> = { data, loader, error };
    this._countries$.update(countriesView);
  }

  public get countries(): View<Pagination<Country>> {
    return this._countries$.snapshot;
  } */

}
