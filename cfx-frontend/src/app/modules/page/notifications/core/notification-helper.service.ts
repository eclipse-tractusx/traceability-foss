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
import { NotificationsFacade } from '@page/notifications/core/notifications.facade';
import { NotificationStatus } from '@shared/model/notification.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class NotificationHelperService {
  constructor(private readonly notificationsFacade: NotificationsFacade) {
  }

  public approve(id: string): Observable<void> {
    return this.notificationsFacade.approveNotification(id);
  }

  public cancel(id: string): Observable<void> {
    return this.notificationsFacade.cancelNotification(id);
  }

  public close(id: string, reason: string): Observable<void> {
    return this.notificationsFacade.closeNotification(id, reason);
  }

  public acknowledge(id: string): Observable<void> {
    return this.notificationsFacade.acknowledgeNotification(id);
  }

  public accept(id: string, reason: string): Observable<void> {
    return this.notificationsFacade.acceptNotification(id, reason);
  }

  public decline(id: string, reason: string): Observable<void> {
    return this.notificationsFacade.declineNotification(id, reason);
  }

  modalCallback(status: NotificationStatus, id: string, reason?: string): Observable<void> {
    switch (status) {
      case NotificationStatus.ACKNOWLEDGED:
        return this.notificationsFacade.acknowledgeNotification(id);
      case NotificationStatus.APPROVED:
        return this.notificationsFacade.approveNotification(id);
      case NotificationStatus.CANCELED:
        return this.notificationsFacade.cancelNotification(id);
      case NotificationStatus.CLOSED:
        return this.notificationsFacade.closeNotification(id, reason);
      case NotificationStatus.ACCEPTED:
        return this.notificationsFacade.acceptNotification(id, reason);
      case NotificationStatus.DECLINED:
        return this.notificationsFacade.declineNotification(id, reason);
      default:
        return null;
    }
  }


}
