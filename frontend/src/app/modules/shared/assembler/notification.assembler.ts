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

import { PaginationAssembler } from '@core/pagination/pagination.assembler';
import { Severity } from '@shared/model/severity.model';
import {
  Notification,
  NotificationResponse,
  Notifications,
  NotificationsResponse,
  NotificationStatus,
  NotificationType,
} from '../model/notification.model';

export class NotificationAssembler {
  public static assembleNotifications(response: NotificationsResponse): Notifications {
    return PaginationAssembler.assemblePagination(NotificationAssembler.assembleNotification, response);
  }

  public static assembleNotification(response: NotificationResponse): Notification {
    const {
      id = null,
      title: _title = '',
      type: _type = null,
      status: _status,
      description = '',
      createdBy: _createdBy = '',
      createdByName: _createdByName = '',
      createdDate: _createdDate = '',
      updatedDate: _updatedDate = '',
      assetIds = null,
      channel = null,
      sendTo: _sendTo = '',
      sendToName: _sendToName = '',
      severity: _severity,
      targetDate: _targetDate = '',
      messages: _messages = [],
    } = response;

    const title = _title;
    const type = NotificationType[_type];
    const status = NotificationStatus[_status] ?? null;
    const createdBy = _createdBy;
    const createdByName = _createdByName;
    const createdDate = new Date(_createdDate || null);
    const updatedDate = new Date(_updatedDate || null);
    const sendTo = _sendTo;
    const sendToName = _sendToName;
    const severity = Object.values(Severity).find(element => element == _severity) ?? null;
    const targetDate = new Date(_targetDate || null);
    const messages = _messages;

    const isFromSender = channel === 'SENDER';

    let assembled = {
      id,
      title,
      description,
      createdBy,
      createdByName,
      sendTo,
      sendToName,
      assetIds,
      isFromSender,
      status,
      severity,
      createdDate,
      updatedDate,
      targetDate,
      type,
      messages,

    };

    return assembled;
  }
}
