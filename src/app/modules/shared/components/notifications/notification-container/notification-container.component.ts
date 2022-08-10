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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { NotificationMessage } from '../notification-message/notification-message.model';
import { NotificationService } from '../notification.service';
import { notifyAnimation } from './animation';

@Component({
  selector: 'app-notification-container',
  templateUrl: './notification-container.component.html',
  styleUrls: ['./notification-container.component.scss'],
  animations: [notifyAnimation],
})
export class NotificationContainerComponent implements OnInit, OnDestroy {
  public notifications: NotificationMessage[] = [];

  private subscription: Subscription;

  constructor(private readonly notifierService: NotificationService) {}

  public ngOnInit(): void {
    this.subscription = this.notifierService
      .getNotificationObservable()
      .subscribe(notification => this.add(notification));
  }

  public ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  public remove({ id }: NotificationMessage): void {
    this.notifications = this.notifications.filter(notification => notification.id !== id);
  }

  public add(notification: NotificationMessage): void {
    this.notifications.unshift(notification);
    setTimeout(() => this.remove(notification), notification.timeout);
  }
}
