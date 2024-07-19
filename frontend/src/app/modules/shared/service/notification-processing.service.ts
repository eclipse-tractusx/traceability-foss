import { EventEmitter, Injectable } from '@angular/core';
import { Notification } from '@shared/model/notification.model';

/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
@Injectable({
  providedIn: 'root',
})
export class NotificationProcessingService {
  private _notificationIdsInLoadingState: Set<string> = new Set();
  doneEmit = new EventEmitter<any>();

  get notificationIdsInLoadingState(): Set<string> {
    return this._notificationIdsInLoadingState;
  }

  set notificationIdsInLoadingState(ids: Set<string>) {
    this._notificationIdsInLoadingState = ids;
    this.onNotificationIdsInLoadingStateRemoval();
  }

  public addNotificationId(id: string): void {
    this._notificationIdsInLoadingState.add(id);
  }

  public deleteNotificationId(id: string): void {
    this._notificationIdsInLoadingState.delete(id);
    this.onNotificationIdsInLoadingStateRemoval();
  }

  public isInLoadingProcess({ id } = {} as Notification): boolean {
    return this._notificationIdsInLoadingState.has(id);
  }

  private onNotificationIdsInLoadingStateRemoval(): void {
    this.doneEmit.emit();
  }
}
