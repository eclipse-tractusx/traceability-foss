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
import { Notification, NotificationStatus } from '@shared/model/notification.model';

type TextMessageDirection = 'left' | 'right';

interface TextMessage {
  message: string;
  direction: TextMessageDirection;
  user: string;
  bpn: string;
  status: NotificationStatus;
  errorMessage: string;
  date: string;
}

@Component({
  selector: 'app-notification-reason',
  templateUrl: './notification-reason.component.html',
  styleUrls: [ './notification-reason.component.scss' ],
})
export class NotificationReasonComponent {
  public textMessages: TextMessage[] = [];

  @Input() set notification({
                              description,
                              status,
                              isFromSender,
                              createdDate,
                              createdBy,
                              createdByName,
                              sendTo,
                              sendToName,
                              messages,
                            }: Notification) {

    const sortedMessagesAfterDates = messages.sort((a, b) => new Date(a.messageDate).valueOf() - new Date(b.messageDate).valueOf());

    sortedMessagesAfterDates.forEach(message => {

      this.textMessages.push({
        message: message.message,
        direction: createdBy === message.sentBy ? 'right' : 'left',
        user: message.sentByName,
        bpn: message.sentBy,
        status: message.status,
        date: message.messageDate,
        errorMessage: message.errorMessage,
      });
    });

  }

  isSameDay(date1: string, date2: string): boolean {
    const formDate = new Date(date1);
    const formDate2 = new Date(date2);
    return formDate.getFullYear() === formDate2.getFullYear() &&
      formDate.getMonth() === formDate2.getMonth() &&
      formDate.getDate() === formDate2.getDate();
  }

}
