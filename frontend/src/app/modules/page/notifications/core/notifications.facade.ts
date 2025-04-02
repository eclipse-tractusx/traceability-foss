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
import { NotificationsState } from '@page/notifications/core/notifications.state';
import { provideDataObject } from '@page/parts/core/parts.helper';
import { DateTimeString } from '@shared/components/dateTime/dateTime.component';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { NotificationFilter } from '@shared/model/filter.model';
import { Notification, Notifications, NotificationStatus } from '@shared/model/notification.model';
import { Severity } from '@shared/model/severity.model';
import { View } from '@shared/model/view.model';
import { NotificationService } from '@shared/service/notification.service';
import { Observable, Subscription } from 'rxjs';

@Injectable()
export class NotificationsFacade {
  private notificationReceivedSubscription: Subscription;
  private notificationQueuedAndRequestedSubscription: Subscription;

  constructor(
    private readonly notificationService: NotificationService,
    private readonly notificationsState: NotificationsState,
  ) {
  }

  public get notificationsReceived$(): Observable<View<Notifications>> {
    return this.notificationsState.notificationsReceived$;
  }

  public get notificationsQueuedAndRequested$(): Observable<View<Notifications>> {
    return this.notificationsState.notificationsQueuedAndRequested$;
  }

  public getNotificationById(id: string): Observable<Notification> {
    return this.notificationService.getNotificationById(id);
  }

  public setReceivedNotifications(page = 0, pageSize = 50, sorting: TableHeaderSort[] = [], notificationFilter?: NotificationFilter): void {
    this.notificationReceivedSubscription?.unsubscribe();
    this.notificationReceivedSubscription = this.notificationService
      .getNotifications(page, pageSize, sorting, notificationFilter)
      .subscribe({
        next: data => (this.notificationsState.notificationsReceived = { data: provideDataObject(data) }),
        error: (error: Error) => (this.notificationsState.notificationsReceived = { error }),
      });
  }

  public setQueuedAndRequestedNotifications(page = 0, pageSize = 50, sorting: TableHeaderSort[] = [], notificationFilter?: NotificationFilter): void {
    this.notificationQueuedAndRequestedSubscription?.unsubscribe();
    this.notificationQueuedAndRequestedSubscription = this.notificationService
      .getNotifications(page, pageSize, sorting, notificationFilter)
      .subscribe({
        next: data => (this.notificationsState.notificationsQueuedAndRequested = { data: provideDataObject(data) }),
        error: (error: Error) => (this.notificationsState.notificationsQueuedAndRequested = { error }),
      });
  }

  public stopNotifications(): void {
    this.notificationReceivedSubscription?.unsubscribe();
    this.notificationQueuedAndRequestedSubscription?.unsubscribe();
  }


  public closeNotification(notificationId: string, reason: string): Observable<void> {
    return this.notificationService.closeNotification(notificationId, reason);
  }

  public approveNotification(notificationId: string): Observable<void> {
    return this.notificationService.approveNotification(notificationId);
  }

  public cancelNotification(notificationId: string): Observable<void> {
    return this.notificationService.cancelNotification(notificationId);
  }

  public acknowledgeNotification(notificationId: string): Observable<void> {
    return this.notificationService.updateNotification(notificationId, NotificationStatus.ACKNOWLEDGED, null);
  }

  public acceptNotification(notificationId: string, reason: string): Observable<void> {
    return this.notificationService.updateNotification(notificationId, NotificationStatus.ACCEPTED, reason);
  }

  public declineNotification(notificationId: string, reason: string): Observable<void> {
    return this.notificationService.updateNotification(notificationId, NotificationStatus.DECLINED, reason);
  }

  public editNotification(notificationId: string, title: string, bpn: string, severity: string, targetDate: string, description: string, affectedPartIds: string[]): Observable<void> {
    return this.notificationService.editNotification(notificationId, title, bpn, severity, targetDate, description, affectedPartIds);
  }

  public createNotification(partIds: string[], type: string, title: string, bpn: string, severity: Severity, targetDate: DateTimeString, description: string): Observable<string> {
    return this.notificationService.createNotification(partIds, description, severity, bpn, type, title, targetDate);
  }

}
