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
import { TableHeaderSort } from '@shared/components/table/table.model';
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
    } = response;

    const isFromSender = channel === 'SENDER';
    const status = NotificationStatus[_status] ?? null;
    const severity = Object.values(Severity).find(element => element == _severity) ?? null;
    const createdDate = new CalendarDateModel(_createdDate);
    const targetDate = new CalendarDateModel(_targetDate);
    const createdBy = { bpn: _createdBy, name: _createdByName };
    const sendTo = { bpn: _sendTo, name: _sendToName };

    return {
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
  }

  public static mapSortToApiSort(sorting: TableHeaderSort) {
    if (!sorting) {
      return '';
    }

    // TODO: need to refactor SortableHeaders - use from notification.model.ts ?
    // TODO: do we need this mapping ??
    const localToApiMapping = new Map<string, string>([
      ['id', 'id'],
      ['description', 'description'],
      ['status', 'status'],
      ['severity', 'severity'],
      ['createdDate', 'createdDate'],
      ['createdBy', 'createdBy'],
      ['sendTo', 'sendTo'],
    ]);

    return `${localToApiMapping.get(sorting[0]) || sorting},${sorting[1]}`;
  }
}
