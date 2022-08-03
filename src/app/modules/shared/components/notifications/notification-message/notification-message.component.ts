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

import { Component, EventEmitter, Input, OnChanges, Output } from '@angular/core';
import { NotificationMessage } from './notification-message';
import { NotificationStatus } from './notification-status';

@Component({
  selector: 'app-notification-message',
  templateUrl: './notification-message.component.html',
  styleUrls: ['./notification-message.component.scss'],
})
export class NotificationMessageComponent implements OnChanges {
  @Input() notifierMessage: NotificationMessage;
  @Output() removeNotification: EventEmitter<NotificationMessage> = new EventEmitter<NotificationMessage>();

  public statusBarCss: string;

  ngOnChanges(): void {
    this.changeStatusBarCss();
  }

  public remove(): void {
    this.removeNotification.emit(this.notifierMessage);
  }

  private changeStatusBarCss(): void {
    const cssMapping = new Map([
      [NotificationStatus.Success, 'status-bar-success'],
      [NotificationStatus.Warning, 'status-bar-warning'],
      [NotificationStatus.Error, 'status-bar-error'],
      [NotificationStatus.Informative, 'status-bar-informative'],
    ]);

    this.statusBarCss = cssMapping.get(this.notifierMessage.status);
  }
}
