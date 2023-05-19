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
import { AlertsState } from '@page/alerts/core/alerts.state';
import { Notifications } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { AlertsService } from '@shared/service/alerts.service';
import { Observable, Subscription } from 'rxjs';

@Injectable()
export class AlertsFacade {
  private alertReceivedSubscription: Subscription;
  private alertQueuedAndRequestedSubscription: Subscription;

  constructor(
    private readonly alertsService: AlertsService,
    private readonly alertsState: AlertsState,
  ) {}

  public get alertsReceived$(): Observable<View<Notifications>> {
    return this.alertsState.alertsReceived$;
  }

  public get alertsQueuedAndRequested$(): Observable<View<Notifications>> {
    return this.alertsState.alertsQueuedAndRequested$;
  }

  public setReceivedAlerts(page = 0, pageSize = 50): void {
    this.alertReceivedSubscription?.unsubscribe();
    this.alertReceivedSubscription = this.alertsService
      .getReceivedAlerts(page, pageSize)
      .subscribe({
        next: data => (this.alertsState.alertsReceived = { data }),
        error: (error: Error) => (this.alertsState.alertsReceived = { error }),
      });
  }

  public setQueuedAndRequestedAlerts(page = 0, pageSize = 50): void {
    this.alertQueuedAndRequestedSubscription?.unsubscribe();
    this.alertQueuedAndRequestedSubscription = this.alertsService
      .getCreatedAlerts(page, pageSize)
      .subscribe({
        next: data => (this.alertsState.alertsQueuedAndRequested = { data }),
        error: (error: Error) => (this.alertsState.alertsQueuedAndRequested = { error }),
      });
  }

  public stopAlerts(): void {
    this.alertReceivedSubscription?.unsubscribe();
    this.alertQueuedAndRequestedSubscription?.unsubscribe();
  }
}
