/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

import {Pagination, PaginationResponse} from '@core/model/pagination.model';
import {PaginationAssembler} from '@core/pagination/pagination.assembler';
import {MainAspectType} from '@page/parts/model/mainAspectType.enum';
import {Ess, EssResponse} from '@page/ess/model/ess.model';
import { TableHeaderSort } from '@shared/components/table/table.model';

export class EssAssembler {

  public static assembleEss(essResponse: EssResponse, mainAspectType: MainAspectType): Ess {
    if (!essResponse) {
      return null;
    }
    let mappedPart = {
      id: essResponse.id,
      essStatus: essResponse.essStatus,
      message: essResponse.message,
      rowNumber: essResponse.rowNumber,
      manufacturerPartId: essResponse.manufacturerPartId,
      nameAtManufacturer: essResponse.nameAtManufacturer,
      catenaxSiteId: essResponse.catenaxSiteId,
      bpns: essResponse.bpns,
      companyName: essResponse.companyName || '--',
      jobId: essResponse.jobId,
      status: essResponse.status,
      impacted: essResponse.impacted || '--',
      response: essResponse.response || '--',
      created: essResponse.created,
      updated: essResponse.updated || '--'
    }
    return mappedPart;
  }

  public static assembleEsss(esss: PaginationResponse<EssResponse>, mainAspectType: MainAspectType): Pagination<Ess> {
    return PaginationAssembler.assemblePagination(EssAssembler.assembleEss, esss, mainAspectType);
  }

  public static mapSortToApiSort(sorting: TableHeaderSort): string {
    if (!sorting) {
      return '';
    }
    const localToApiMapping = new Map<string, string>([
      ['id', 'id'],
      ['essStatus', 'essStatus'],
      ['message', 'message'],
      ['rowNumber', 'rowNumber'],
      ['manufacturerPartId', 'manufacturerPartId'],
      ['nameAtManufacturer', 'nameAtManufacturer'],
      ['catenaxSiteId', 'catenaxSiteId'],
      ['bpns', 'bpns'],
      ['companyName', "companyName"],
      ['jobId', 'jobId'],
      ['status', 'status'],
      ['impacted', 'impacted'],
      ['response', 'response'],
      ['created', 'created'],
      ['updated', 'updated'],
    ]);
    return `${localToApiMapping.get(sorting[0]) || sorting},${sorting[1]}`;
  }

}
