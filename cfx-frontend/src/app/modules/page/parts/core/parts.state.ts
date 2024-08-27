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
  private readonly _partsAsDesigned$ = new State<View<Pagination<Part>>>({ loader: true });
  private readonly _partsAsBuilt$ = new State<View<Pagination<Part>>>({ loader: true });
  private readonly _partsAsOrdered$ = new State<View<Pagination<Part>>>({ loader: true });
  private readonly _partsAsPlanned$ = new State<View<Pagination<Part>>>({ loader: true });
  private readonly _partsAsSupported$ = new State<View<Pagination<Part>>>({ loader: true });
  private readonly _partsAsRecycled$ = new State<View<Pagination<Part>>>({ loader: true });

  public get partsAsBuilt$(): Observable<View<Pagination<Part>>> {
    return this._partsAsBuilt$.observable;
  }

  public set partsAsBuilt({ data, loader, error }: View<Pagination<Part>>) {
    const partsView: View<Pagination<Part>> = { data, loader, error };
    this._partsAsBuilt$.update(partsView);
  }

  public get partsAsBuilt(): View<Pagination<Part>> {
    return this._partsAsBuilt$.snapshot;
  }

  public get partsAsPlanned$(): Observable<View<Pagination<Part>>> {
    return this._partsAsPlanned$.observable;
  }

  public set partsAsPlanned({ data, loader, error }: View<Pagination<Part>>) {
    const partsView: View<Pagination<Part>> = { data, loader, error };
    this._partsAsPlanned$.update(partsView);
  }

  public get partsAsPlanned(): View<Pagination<Part>> {
    return this._partsAsPlanned$.snapshot;
  }

  public get partsAsDesigned$(): Observable<View<Pagination<Part>>> {
    return this._partsAsDesigned$.observable;
  }

  public set partsAsDesigned({ data, loader, error }: View<Pagination<Part>>) {
    const partsView: View<Pagination<Part>> = { data, loader, error };
    this._partsAsDesigned$.update(partsView);
  }

  public get partsAsDesigned(): View<Pagination<Part>> {
    return this._partsAsDesigned$.snapshot;
  }

  public get partsAsOrdered$(): Observable<View<Pagination<Part>>> {
    return this._partsAsOrdered$.observable;
  }

  public set partsAsOrdered({ data, loader, error }: View<Pagination<Part>>) {
    const partsView: View<Pagination<Part>> = { data, loader, error };
    this._partsAsOrdered$.update(partsView);
  }

  public get partsAsOrdered(): View<Pagination<Part>> {
    return this._partsAsOrdered$.snapshot;
  }

  public get partsAsSupported$(): Observable<View<Pagination<Part>>> {
    return this._partsAsSupported$.observable;
  }

  public set partsAsSupported({ data, loader, error }: View<Pagination<Part>>) {
    const partsView: View<Pagination<Part>> = { data, loader, error };
    this._partsAsSupported$.update(partsView);
  }

  public get partsAsSupported(): View<Pagination<Part>> {
    return this._partsAsSupported$.snapshot;
  }

  public get partsAsRecycled$(): Observable<View<Pagination<Part>>> {
    return this._partsAsRecycled$.observable;
  }

  public set partsAsRecycled({ data, loader, error }: View<Pagination<Part>>) {
    const partsView: View<Pagination<Part>> = { data, loader, error };
    this._partsAsRecycled$.update(partsView);
  }

  public get partsAsRecycled(): View<Pagination<Part>> {
    return this._partsAsRecycled$.snapshot;
  }
}
