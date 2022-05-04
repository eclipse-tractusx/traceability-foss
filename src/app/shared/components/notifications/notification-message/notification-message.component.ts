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

import { Component, EventEmitter, Input, OnChanges, Output } from '@angular/core';
import { NotificationMessage } from './notification-message';

/**
 *
 *
 * @export
 * @class NotificationMessageComponent
 * @implements {OnChanges}
 */
@Component({
  selector: 'app-notification-message',
  templateUrl: './notification-message.component.html',
  styleUrls: ['./notification-message.component.scss'],
})
export class NotificationMessageComponent implements OnChanges {
  /**
   * Notification message
   *
   * @type {NotificationMessage}
   * @memberof NotificationMessageComponent
   */
  @Input() notifierMessage: NotificationMessage;

  /**
   * Remove notification event emitter
   *
   * @type {EventEmitter<NotificationMessage>}
   * @memberof NotificationMessageComponent
   */
  @Output() removeNotification: EventEmitter<NotificationMessage> = new EventEmitter<NotificationMessage>();

  /**
   * Status bar color
   *
   * @type {string}
   * @memberof NotificationMessageComponent
   */
  public statusBarCss: string;

  /**
   * Angular lifecycle method - Ng on Changes
   *
   * @return {void}
   * @memberof NotificationMessageComponent
   */
  ngOnChanges(): void {
    this.changeStatusBarCss();
  }

  /**
   * Remove notification
   *
   * @return {void}
   * @memberof NotificationMessageComponent
   */
  public remove(): void {
    this.removeNotification.emit(this.notifierMessage);
  }

  /**
   * Status bar handler
   *
   * @private
   * @return {void}
   * @memberof NotificationMessageComponent
   */
  private changeStatusBarCss(): void {
    switch (this.notifierMessage.status) {
      case 1:
        this.statusBarCss = 'status-bar-success';
        break;
      case 2:
        this.statusBarCss = 'status-bar-warning';
        break;
      case 3:
        this.statusBarCss = 'status-bar-error';
        break;
      case 4:
        this.statusBarCss = 'status-bar-informative';
        break;
    }
  }
}
