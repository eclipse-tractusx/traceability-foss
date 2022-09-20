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
  public static assembleInvestigations(response: InvestigationsResponse): Investigations {
    return PaginationAssembler.assemblePagination(response, InvestigationsAssembler.assembleInvestigation);
  }

  public static assembleInvestigation(response: InvestigationResponse): Investigation {
    const { id, description = '', status, createDate, createdBy, parts } = response;
    return {
      id,
      description,
      createdBy,
      parts,

      status: InvestigationStatus[status] ?? null,
      createDate: new CalendarDateModel(createDate),
    };
  }
}
