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
import { Notifications } from '@shared/model/notification.model';
import { State } from '@shared/model/state';
import { View } from '@shared/model/view.model';
import { Observable } from 'rxjs';

@Injectable()
export class InvestigationsState {
  private readonly _investigationsReceived$ = new State<View<Notifications>>({ loader: true });
  private readonly _investigationsQueuedAndRequested$ = new State<View<Notifications>>({ loader: true });

  public get investigationsReceived$(): Observable<View<Notifications>> {
    return this._investigationsReceived$.observable;
  }

  public set investigationsReceived({ data, loader, error }: View<Notifications>) {
    const investigationsView: View<Notifications> = { data, loader, error };
    this._investigationsReceived$.update(investigationsView);
  }

  public get investigationsQueuedAndRequested$(): Observable<View<Notifications>> {
    return this._investigationsQueuedAndRequested$.observable;
  }

  public set investigationsQueuedAndRequested({ data, loader, error }: View<Notifications>) {
    const investigationsView: View<Notifications> = { data, loader, error };
    this._investigationsQueuedAndRequested$.update(investigationsView);
  }
}
