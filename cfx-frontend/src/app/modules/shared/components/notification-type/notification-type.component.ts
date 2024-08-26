/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

import { Component, Input } from '@angular/core';
import { NotificationType } from '@shared/model/notification.model';

@Component({
  selector: 'app-notification-type',
  templateUrl: './notification-type.component.html',
  styleUrls: [ './notification-type.component.scss' ],
})
export class NotificationTypeComponent {
  @Input() notificationType: NotificationType;

  public getIconByNotificationType(notificationType: NotificationType): string {
    const iconMap = new Map<NotificationType, string>([
      [ NotificationType.INVESTIGATION, 'search' ],
      [ NotificationType.ALERT, 'warning' ]
    ]);
    return iconMap.get(notificationType) || '';
  }
}
