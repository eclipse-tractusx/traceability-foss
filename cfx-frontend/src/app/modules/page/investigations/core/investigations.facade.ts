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
import { provideDataObject } from '@page/parts/core/parts.helper';
import { FilterMethod, TableHeaderSort } from '@shared/components/table/table.model';
import { Notification, Notifications, NotificationStatus } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { NotificationService } from '@shared/service/notification.service';
import { Observable, Subscription } from 'rxjs';
import { NotificationFilter } from '../../../../mocks/services/investigations-mock/investigations.model';
import { InvestigationsState } from './investigations.state';

@Injectable()
export class InvestigationsFacade {
  private investigationReceivedSubscription: Subscription;
  private investigationQueuedAndRequestedSubscription: Subscription;

  constructor(
    private readonly notificationService: NotificationService,
    private readonly investigationsState: InvestigationsState,
  ) {
  }

  public get investigationsReceived$(): Observable<View<Notifications>> {
    return this.investigationsState.investigationsReceived$;
  }

  public get investigationsQueuedAndRequested$(): Observable<View<Notifications>> {
    return this.investigationsState.investigationsQueuedAndRequested$;
  }

  public getInvestigation(id: string): Observable<Notification> {
    return this.notificationService.getNotificationById(id, true);
  }

  public setReceivedInvestigation(page = 0, pageSize = 50, sorting: TableHeaderSort[] = [], filter?: NotificationFilter, fullFilter?: any, filterMethod = FilterMethod.AND): void {
    this.investigationReceivedSubscription?.unsubscribe();
    this.investigationReceivedSubscription = this.notificationService
      .getNotificationByFilter(page, pageSize, sorting, filter, fullFilter, true, filterMethod, true)
      .subscribe({
        next: data => (this.investigationsState.investigationsReceived = { data: provideDataObject(data) }),
        error: (error: Error) => (this.investigationsState.investigationsReceived = { error }),
      });
  }

  public setQueuedAndRequestedInvestigations(page = 0, pageSize = 50, sorting: TableHeaderSort[] = [], filter?: NotificationFilter, fullFilter?: any, filterMethod = FilterMethod.AND): void {
    this.investigationQueuedAndRequestedSubscription?.unsubscribe();
    this.investigationQueuedAndRequestedSubscription = this.notificationService
      .getNotificationByFilter(page, pageSize, sorting, filter, fullFilter, true, filterMethod, false)
      .subscribe({
        next: data => (this.investigationsState.investigationsQueuedAndRequested = { data: provideDataObject(data) }),
        error: (error: Error) => (this.investigationsState.investigationsQueuedAndRequested = { error }),
      });
  }

  public stopInvestigations(): void {
    this.investigationReceivedSubscription?.unsubscribe();
    this.investigationQueuedAndRequestedSubscription?.unsubscribe();
  }

  public closeInvestigation(investigationId: string, reason: string): Observable<void> {
    return this.notificationService.closeNotification(investigationId, reason);
  }

  public approveInvestigation(investigationId: string): Observable<void> {
    return this.notificationService.approveNotification(investigationId);
  }

  public cancelInvestigation(investigationId: string): Observable<void> {
    return this.notificationService.cancelNotification(investigationId);
  }

  public acknowledgeInvestigation(investigationId: string): Observable<void> {
    return this.notificationService.updateNotification(investigationId, NotificationStatus.ACKNOWLEDGED, null);
  }

  public acceptInvestigation(investigationId: string, reason: string): Observable<void> {
    return this.notificationService.updateNotification(investigationId, NotificationStatus.ACCEPTED, reason);
  }

  public declineInvestigation(investigationId: string, reason: string): Observable<void> {
    return this.notificationService.updateNotification(investigationId, NotificationStatus.DECLINED, reason);
  }
}
