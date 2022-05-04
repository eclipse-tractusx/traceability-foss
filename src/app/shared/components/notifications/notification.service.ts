/*
 * Copyright 2021 The PartChain Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Injectable } from '@angular/core';
import { NotificationText } from './notification-message/notification-text';
import { NotificationStatus } from './notification-message/notification-status';
import { NotificationMessage } from './notification-message/notification-message';
import { Observable, Subject } from 'rxjs';

/**
 *
 *
 * @export
 * @class NotificationService
 */
@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  /**
   * Notification subject
   *
   * @private
   * @type {Subject<NotificationMessage>}
   * @memberof NotificationService
   */
  private notificationSubject = new Subject<NotificationMessage>();

  /**
   * Notification index
   *
   * @private
   * @type {number}
   * @memberof NotificationService
   */
  private idx = 0;

  /**
   * Get notification
   *
   * @return {Observable<NotificationMessage>}
   * @memberof NotificationService
   */
  public getNotificationObservable(): Observable<NotificationMessage> {
    return this.notificationSubject.asObservable();
  }

  /**
   * Success notification
   *
   * @param {(NotificationText | string)} message
   * @param {number} [timeout=3000]
   * @return {void}
   * @memberof NotificationService
   */
  public success(message: NotificationText | string, timeout = 3000): void {
    this.notificationSubject.next(new NotificationMessage(this.idx++, message, NotificationStatus.Success, timeout));
  }

  /**
   * Info notification
   *
   * @param {(NotificationText | string)} message
   * @param {number} [timeout=3000]
   * @return {void}
   * @memberof NotificationService
   */
  public info(message: NotificationText | string, timeout = 3000): void {
    this.notificationSubject.next(
      new NotificationMessage(this.idx++, message, NotificationStatus.Informative, timeout),
    );
  }

  /**
   * Error notification
   *
   * @param {(NotificationText | string)} message
   * @param {number} [timeout=3000]
   * @return {void}
   * @memberof NotificationService
   */
  public error(message: NotificationText | string, timeout = 3000): void {
    this.notificationSubject.next(new NotificationMessage(this.idx++, message, NotificationStatus.Error, timeout));
  }

  /**
   * Warning notification
   *
   * @param {(NotificationText | string)} message
   * @param {number} [timeout=3000]
   * @return {void}
   * @memberof NotificationService
   */
  public warning(message: NotificationText | string, timeout = 3000): void {
    this.notificationSubject.next(new NotificationMessage(this.idx++, message, NotificationStatus.Warning, timeout));
  }
}
