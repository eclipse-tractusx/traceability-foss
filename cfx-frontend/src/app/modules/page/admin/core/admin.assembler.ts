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
import { Pagination, PaginationResponse } from '@core/model/pagination.model';
import { PaginationAssembler } from '@core/pagination/pagination.assembler';
import { BpnConfig, BpnConfigResponse, RegistryProcess, RegistryProcessResponse } from '@page/admin/core/admin.model';
import _deepClone from 'lodash-es/cloneDeep';

export class AdminAssembler {
  public static AssembleRegistryProcess(registryProcess: RegistryProcessResponse): RegistryProcess {
    const clonedProcess = _deepClone(registryProcess);
    const { startDate, endDate } = clonedProcess;
    return { ...clonedProcess, startDate: new CalendarDateModel(startDate), endDate: new CalendarDateModel(endDate) };
  }

  public static assemblePaginationRegistryProcess(
    data: PaginationResponse<RegistryProcessResponse>,
  ): Pagination<RegistryProcess> {
    return PaginationAssembler.assemblePagination(AdminAssembler.AssembleRegistryProcess, data);
  }

  public static assembleBpnConfig(data: BpnConfigResponse[]): BpnConfig[] {
    return data.map(({ bpn, url }) => ({ bpn, url }));
  }
}
