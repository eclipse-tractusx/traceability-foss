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

import type { CalendarDateModel } from '@core/model/calendar-date.model';
import type { Pagination, PaginationResponse } from '@core/model/pagination.model';
import { Severity } from '@shared/model/severity.model';

export enum NotificationStatus {
  ACCEPTED = 'ACCEPTED',
  ACKNOWLEDGED = 'ACKNOWLEDGED',
  APPROVED = 'APPROVED',
  CANCELED = 'CANCELED',
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

export interface NotificationReason {
  close: string | null;
  accept: string | null;
  decline: string | null;
}

export interface NotificationUser {
  bpn?: string;
  name?: string;
}

export interface NotificationFilter {
  id?: string;
  description?: string;
  status?: NotificationStatus;
  severity?: Severity;
  createdDate?: string;
  createdBy?: string;
  createdByName?: string;
  sendTo?: string;
  sendToName?: string;
  reason?: NotificationReason;
  assetIds?: string[];
  channel?: 'SENDER' | 'RECEIVER';
  targetDate?: string;
  bpn?: string;
  errorMessage?: string;
  title: string;
}

export enum NotificationType {
  INVESTIGATION = 'Investigation',
  ALERT = 'Alert'
}

export interface NotificationResponse {
  id: string;
  description: string;
  status: NotificationStatus;
  severity: Severity;

  createdDate: string;
  createdBy: string;
  createdByName?: string;
  sendTo: string;
  sendToName?: string;
  reason: NotificationReason;
  assetIds: string[];
  channel: 'SENDER' | 'RECEIVER';
  targetDate?: string;
  bpn?: string;
  errorMessage?: string;
  title: string;
}

export interface Notification {
  id: string;
  description: string;
  status: NotificationStatus | null;
  severity: Severity | null;

  createdDate: CalendarDateModel;
  createdBy: string;
  createdByName: string;
  sendTo: string;
  sendToName: string;
  reason: NotificationReason;
  assetIds: string[];
  isFromSender: boolean;
  targetDate?: CalendarDateModel;
  bpn?: string;
  errorMessage?: string;
  title: string;
  notificationType?: NotificationType;
}

export enum NotificationColumn {
  RECEIVED_ALERT = 'receivedActiveAlerts',
  SENT_ALERT = 'sentActiveAlerts',
  RECEIVED_INVESTIGATION = 'receivedActiveInvestigations',
  SENT_INVESTIGATION = 'sentActiveInvestigations'
}

export type NotificationsResponse = PaginationResponse<NotificationResponse>;
export type Notifications = Pagination<Notification>;
