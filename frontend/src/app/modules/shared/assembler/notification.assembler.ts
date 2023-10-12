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

import { CalendarDateModel } from '@core/model/calendar-date.model';
import { PaginationAssembler } from '@core/pagination/pagination.assembler';
import { Severity } from '@shared/model/severity.model';
import {
  Notification,
  NotificationResponse,
  Notifications,
  NotificationsResponse,
  NotificationStatus,
} from '../model/notification.model';

export class NotificationAssembler {
  public static assembleNotifications(response: NotificationsResponse): Notifications {
    return PaginationAssembler.assemblePagination(NotificationAssembler.assembleNotification, response);
  }

  public static assembleNotification(response: NotificationResponse): Notification {
    const {
      id = null,
      assetIds = null,
      channel = null,

      reason = { accept: '', close: '', decline: '' },
      description = '',
      bpn = '',
      status: _status,
      severity: _severity,
      createdDate: _createdDate = '',
      createdBy: _createdBy = '',
      createdByName: _createdByName = '',
      sendTo: _sendTo = '',
      sendToName: _sendToName = '',
      targetDate: _targetDate = '',
      errorMessage: _errorMessage = '',
    } = response;

    const isFromSender = channel === 'SENDER';
    const status = NotificationStatus[_status] ?? null;
    const severity = Object.values(Severity).find(element => element == _severity) ?? null;
    const createdDate = new CalendarDateModel(_createdDate);
    const targetDate = new CalendarDateModel(_targetDate);
    const createdBy = { bpn: _createdBy, name: '' };
    const sendTo = { bpn: _sendTo, name: _sendToName };
    const errorMessage = _errorMessage || undefined;

    let assembled = {
      id,
      description,
      createdBy,
      sendTo,
      reason,
      assetIds,
      isFromSender,
      status,
      severity,
      createdDate,
      targetDate,
      bpn,
    };

    return errorMessage ? { ...assembled, errorMessage: errorMessage } : assembled;
  }
}
