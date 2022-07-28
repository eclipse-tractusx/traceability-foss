/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import type { CalendarDateModel } from '@core/model/calendar-date.model';
import type { PaginationResponse, Pagination } from '@core/model/pagination.model';

export enum InvestigationStatus {
  RECEIVED = 'received',
  QUEUED = 'queued',
  REQUESTED = 'requested',
}

export enum InvestigationStatusGroup {
  RECEIVED = 'received',
  QUEUED_AND_REQUESTED = 'queued-and-requested',
}

export interface InvestigationResponse {
  id: string;
  description: string;
  status: string;
  createDate: string;
}

export interface Investigation {
  id: string;
  description: string;
  status: InvestigationStatus | null;
  created: CalendarDateModel;
}

export type InvestigationsResponse = PaginationResponse<InvestigationResponse>;
export type Investigations = Pagination<Investigation>;
