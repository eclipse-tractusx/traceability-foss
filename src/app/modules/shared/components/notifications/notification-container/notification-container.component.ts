/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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
