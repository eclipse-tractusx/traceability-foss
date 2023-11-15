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

import { Component, Input } from '@angular/core';
import { CalendarDateModel } from '@core/model/calendar-date.model';
import { Notification, NotificationUser, NotificationStatus } from '@shared/model/notification.model';

type TextMessageDirection = 'left' | 'right' | 'none';
interface TextMessage {
  reason: string;
  direction: TextMessageDirection;
  user: NotificationUser;
  status: NotificationStatus;
  date?: CalendarDateModel;
}

@Component({
  selector: 'app-notification-reason',
  templateUrl: './notification-reason.component.html',
  styleUrls: ['./notification-reason.component.scss'],
})
export class NotificationReasonComponent {
  public textMessages: TextMessage[];

  @Input() set notification({
    description,
    reason,
    status,
    isFromSender,
    createdDate,
    createdBy,
    sendTo,
  }: Notification) {
    const { ACCEPTED, SENT, CLOSED, CREATED, DECLINED } = NotificationStatus;
    const { accept, close, decline } = reason;

    const senderDirection: TextMessageDirection = 'none';
    const receiverDirection: TextMessageDirection = 'none';

    const createdMessage = {
      reason: description,
      direction: senderDirection,
      user: createdBy,
      status: [CREATED, SENT].includes(status) ? status : SENT,
      date: createdDate,
    };

    const acceptedMessage = { reason: accept, direction: receiverDirection, user: sendTo, status: ACCEPTED };
    const declinedMessage = { reason: decline, direction: receiverDirection, user: sendTo, status: DECLINED };
    const closedMessage = { reason: close, direction: senderDirection, user: createdBy, status: CLOSED };
    this.textMessages = [createdMessage, acceptedMessage, declinedMessage, closedMessage];
  }
}
