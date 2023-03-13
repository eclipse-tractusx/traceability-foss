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
import {
  Notification,
  NotificationResponse,
  Notifications,
  NotificationsResponse,
  NotificationStatus,
} from '../model/notification.model';

export class InvestigationsAssembler {
  public static assembleInvestigations(response: NotificationsResponse): Notifications {
    return PaginationAssembler.assemblePagination(InvestigationsAssembler.assembleInvestigation, response);
  }

  public static assembleInvestigation(response: NotificationResponse): Notification {
    const {
      id,
      assetIds,
      channel,
      sendTo,

      reason = { accept: '', close: '', decline: '' },
      description = '',
      status: _status,
      createdDate: _createdDate = '',
      createdBy = '',
      targetDate: _targetDate = '',
    } = response;

    const isFromSender = channel === 'SENDER';
    const status = NotificationStatus[_status] ?? null;
    const createdDate = new CalendarDateModel(_createdDate);
    const targetDate = new CalendarDateModel(_targetDate);

    return { id, description, createdBy, sendTo, reason, assetIds, isFromSender, status, createdDate, targetDate };
  }
}
