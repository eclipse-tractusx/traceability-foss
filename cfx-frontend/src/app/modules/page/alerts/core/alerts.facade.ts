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
import { provideDataObject } from '@page/parts/core/parts.helper';
import { FilterMethod, TableHeaderSort } from '@shared/components/table/table.model';
import { Notification, Notifications, NotificationStatus } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { NotificationService } from '@shared/service/notification.service';
import { Observable, Subscription } from 'rxjs';
import { NotificationFilter } from '../../../../mocks/services/investigations-mock/investigations.model';

@Injectable()
export class AlertsFacade {
  private alertReceivedSubscription: Subscription;
  private alertQueuedAndRequestedSubscription: Subscription;

  constructor(
    private readonly notificationService: NotificationService,
    private readonly alertsState: AlertsState,
  ) {
  }

  public get alertsReceived$(): Observable<View<Notifications>> {
    return this.alertsState.alertsReceived$;
  }

  public get alertsQueuedAndRequested$(): Observable<View<Notifications>> {
    return this.alertsState.alertsQueuedAndRequested$;
  }

  public getAlert(id: string): Observable<Notification> {
    return this.notificationService.getNotificationById(id, false);
  }

  public setReceivedAlerts(page = 0, pageSize = 50, sorting: TableHeaderSort[] = [], filter?: NotificationFilter, fullFilter?: any, filterMethod = FilterMethod.AND): void {
    this.alertReceivedSubscription?.unsubscribe();
    this.alertReceivedSubscription = this.notificationService
      .getNotificationByFilter(page, pageSize, sorting, filter, fullFilter, false, filterMethod, true)
      .subscribe({
        next: data => (this.alertsState.alertsReceived = { data: provideDataObject(data) }),
        error: (error: Error) => (this.alertsState.alertsReceived = { error }),
      });
  }

  public setQueuedAndRequestedAlerts(page = 0, pageSize = 50, sorting: TableHeaderSort[] = [], filter?: NotificationFilter, fullFilter?: any, filterMethod = FilterMethod.AND): void {
    this.alertQueuedAndRequestedSubscription?.unsubscribe();
    this.alertQueuedAndRequestedSubscription = this.notificationService
      .getNotificationByFilter(page, pageSize, sorting, filter, fullFilter, false, filterMethod, false)
      .subscribe({
        next: data => (this.alertsState.alertsQueuedAndRequested = { data: provideDataObject(data) }),
        error: (error: Error) => (this.alertsState.alertsQueuedAndRequested = { error }),
      });
  }

  public stopAlerts(): void {
    this.alertReceivedSubscription?.unsubscribe();
    this.alertQueuedAndRequestedSubscription?.unsubscribe();
  }

  public closeAlert(alertId: string, reason: string): Observable<void> {
    return this.notificationService.closeNotification(alertId, reason);
  }

  public approveAlert(alertId: string): Observable<void> {
    return this.notificationService.approveNotification(alertId);
  }

  public cancelAlert(alertId: string): Observable<void> {
    return this.notificationService.cancelNotification(alertId);
  }

  public acknowledgeAlert(alertId: string): Observable<void> {
    return this.notificationService.updateNotification(alertId, NotificationStatus.ACKNOWLEDGED, null);
  }

  public acceptAlert(alertId: string, reason: string): Observable<void> {
    return this.notificationService.updateNotification(alertId, NotificationStatus.ACCEPTED, reason);
  }

  public declineAlert(alertId: string, reason: string): Observable<void> {
    return this.notificationService.updateNotification(alertId, NotificationStatus.DECLINED, reason);
  }
}
