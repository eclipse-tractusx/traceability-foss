/********************************************************************************
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
import { Part } from '@page/parts/model/parts.model';
import { Notification } from '@shared/model/notification.model';
import { State } from '@shared/model/state';
import { View } from '@shared/model/view.model';
import { Observable } from 'rxjs';

@Injectable()
export class NotificationDetailState {
  private readonly _notificationPartsInformation$ = new State<View<Part[]>>({ loader: true });
  private readonly _supplierPartsInformation$ = new State<View<Part[]>>({ loader: true });

  private readonly _selected$ = new State<View<Notification>>({ loader: true });

  // Detailed information for parts assigned to a notification
  public get notificationPartsInformation$(): Observable<View<Part[]>> {
    return this._notificationPartsInformation$.observable;
  }

  public get notificationPartsInformation(): View<Part[]> {
    return this._notificationPartsInformation$.snapshot;
  }

  public set notificationPartsInformation(view: View<Part[]>) {
    this._notificationPartsInformation$.update(view);
  }

  // Detailed information for child parts assigned to a notification
  public get supplierPartsInformation$(): Observable<View<Part[]>> {
    return this._supplierPartsInformation$.observable;
  }

  public get supplierPartsInformation(): View<Part[]> {
    return this._supplierPartsInformation$.snapshot;
  }

  public set supplierPartsInformation(view: View<Part[]>) {
    this._supplierPartsInformation$.update(view);
  }

  // Selected Notification
  public get selected$(): Observable<View<Notification>> {
    return this._selected$.observable;
  }

  public set selected({ data, loader, error }: View<Notification>) {
    const view: View<Notification> = { data, loader, error };
    this._selected$.update(view);
  }

  get selected(): View<Notification> {
    return this._selected$.snapshot;
  }
}
