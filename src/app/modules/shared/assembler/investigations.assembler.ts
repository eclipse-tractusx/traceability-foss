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

import { CalendarDateModel } from '@core/model/calendar-date.model';
import { PaginationAssembler } from '@core/pagination/pagination.assembler';
import {
  Investigation,
  InvestigationResponse,
  Investigations,
  InvestigationsResponse,
  InvestigationStatus,
} from '../model/investigations.model';

export class InvestigationsAssembler {
  private static ALL_POSSIBLE_STATUSES = new Set<string>(Object.values(InvestigationStatus));

  public static assembleInvestigations(response: InvestigationsResponse): Investigations {
    if (!response) {
      return null;
    }

    return PaginationAssembler.assemblePagination(response, InvestigationsAssembler.assembleInvestigation);
  }

  public static assembleInvestigation(response: InvestigationResponse): Investigation {
    return {
      id: response.id,
      description: response.description ?? '',
      status: InvestigationsAssembler.ALL_POSSIBLE_STATUSES.has(response.status)
        ? (response.status as InvestigationStatus)
        : null,
      created: new CalendarDateModel(response.createDate),
    };
  }
}
