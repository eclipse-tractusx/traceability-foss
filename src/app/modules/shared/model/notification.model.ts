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

import type { CalendarDateModel } from '@core/model/calendar-date.model';
import type { Pagination, PaginationResponse } from '@core/model/pagination.model';

export enum NotificationStatus {
  ACCEPTED = 'ACCEPTED',
  ACKNOWLEDGED = 'ACKNOWLEDGED',
  APPROVED = 'APPROVED',
  CLOSED = 'CLOSED',
  CREATED = 'CREATED',
  DECLINED = 'DECLINED',
  RECEIVED = 'RECEIVED',
  SENT = 'SENT',
}

export enum NotificationStatusGroup {
  RECEIVED = 'received',
  QUEUED_AND_REQUESTED = 'queued-and-requested',
}

export interface NotificationCreateResponse {
  id: string;
}

export interface NotificationResponse {
  id: string;
  description: string;
  status: NotificationStatus;

  createdDate: string;
  createdBy: string;
  assetIds: string[];
}

export interface Notification {
  id: string;
  description: string;
  status: NotificationStatus | null;

  createdDate: CalendarDateModel;
  createdBy: string;
  assetIds: string[];
}

export type NotificationsResponse = PaginationResponse<NotificationResponse>;
export type Notifications = Pagination<Notification>;
