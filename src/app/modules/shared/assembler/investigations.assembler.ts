/********************************************************************************
 * Copyright (c) 2022,2023
 *        2022: Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *        2022: ZF Friedrichshafen AG
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
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
    return PaginationAssembler.assemblePagination(response, InvestigationsAssembler.assembleInvestigation);
  }

  public static assembleInvestigation(response: NotificationResponse): Notification {
    const { id, description = '', status, createDate, createdBy, parts } = response;
    return {
      id,
      description,
      createdBy,
      parts,

      status: NotificationStatus[status] ?? null,
      createDate: new CalendarDateModel(createDate),
    };
  }
}
