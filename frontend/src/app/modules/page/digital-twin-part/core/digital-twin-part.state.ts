/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
import { View } from '@shared/model/view.model';
import { State } from '@shared/model/state';
import { Observable } from 'rxjs';
import { DigitalTwinPartResponse } from '@page/digital-twin-part/model/digitalTwinPart.model';

@Injectable()
export class DigitalTwinPartState {
  private readonly _digitalTwinParts$ = new State<View<Pagination<DigitalTwinPartResponse>>>({ loader: true });

  public get digitalTwinParts$(): Observable<View<Pagination<DigitalTwinPartResponse>>> {
    return this._digitalTwinParts$.observable;
  }

  public get digitalTwinParts(): View<Pagination<DigitalTwinPartResponse>> {
    return this._digitalTwinParts$.snapshot;
  }

  public set digitalTwinParts(view: View<Pagination<DigitalTwinPartResponse>>) {
    this._digitalTwinParts$.update(view);
  }
}
