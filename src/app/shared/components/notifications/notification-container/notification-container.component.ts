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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { notifyAnimation } from './animation';
import { NotificationService } from '../notification.service';
import { Subscription } from 'rxjs';
import { NotificationMessage } from '../notification-message/notification-message';

/**
 *
 *
 * @export
 * @class NotificationContainerComponent
 * @implements {OnInit}
 * @implements {OnDestroy}
 */
@Component({
  selector: 'app-notification-container',
  templateUrl: './notification-container.component.html',
  styleUrls: ['./notification-container.component.scss'],
  animations: [notifyAnimation],
})
export class NotificationContainerComponent implements OnInit, OnDestroy {
  /**
   * Notifications list
   *
   * @type {NotificationMessage[]}
   * @memberof NotificationContainerComponent
   */
  public notifications: NotificationMessage[] = [];

  /**
   * Notification subscription
   *
   * @private
   * @type {Subscription}
   * @memberof NotificationContainerComponent
   */
  private subscription: Subscription;

  /**
   * @constructor NotificationContainerComponent
   * @param {NotificationService} notifierService
   * @memberof NotificationContainerComponent
   */
  constructor(private notifierService: NotificationService) {}

  /**
   * Angular lifecycle method - Ng On Init
   *
   * @return {void}
   * @memberof NotificationContainerComponent
   */
  ngOnInit(): void {
    this.subscription = this.notifierService
      .getNotificationObservable()
      .subscribe(notification => this.add(notification));
  }

  /**
   * Angular lifecycle method - Ng On Destroy
   *
   * @return {void}
   * @memberof NotificationContainerComponent
   */
  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  /**
   * Remove notification
   *
   * @param {NotificationMessage} notification
   * @return {void}
   * @memberof NotificationContainerComponent
   */
  public remove(notification: NotificationMessage): void {
    this.notifications = this.notifications.filter(notif => notif.id !== notification.id);
  }

  /**
   * Add notification
   *
   * @param {NotificationMessage} notification
   * @return {void}
   * @memberof NotificationContainerComponent
   */
  public add(notification: NotificationMessage): void {
    this.notifications.unshift(notification);

    if (notification.timeout !== 0) {
      setTimeout(() => this.remove(notification), notification.timeout);
    }
  }
}
