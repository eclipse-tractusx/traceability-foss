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
      return { page: 0, pageCount: 0, pageSize: 0, totalItems: 0, content: [] };
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
