/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { NotificationMessage } from './notification-message/notification-message';
import { NotificationStatus } from './notification-message/notification-status';
import { NotificationText } from './notification-message/notification-text';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private notificationSubject = new Subject<NotificationMessage>();
  private idx = 0;

  public getNotificationObservable(): Observable<NotificationMessage> {
    return this.notificationSubject.asObservable();
  }

  public success(message: NotificationText | string, timeout = 3000): void {
    this.notificationSubject.next(new NotificationMessage(this.idx++, message, NotificationStatus.Success, timeout));
  }

  public info(message: NotificationText | string, timeout = 3000): void {
    this.notificationSubject.next(
      new NotificationMessage(this.idx++, message, NotificationStatus.Informative, timeout),
    );
  }

  public error(message: NotificationText | string, timeout = 3000): void {
    this.notificationSubject.next(new NotificationMessage(this.idx++, message, NotificationStatus.Error, timeout));
  }

  public warning(message: NotificationText | string, timeout = 3000): void {
    this.notificationSubject.next(new NotificationMessage(this.idx++, message, NotificationStatus.Warning, timeout));
  }
}
