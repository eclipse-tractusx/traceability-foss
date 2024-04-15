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

import { PaginationBpdm } from '@core/model/pagination.bpdm.model';
import { PaginationBpdmAssembler } from '@core/pagination/pagination.bpdm.assembler';
import { LegalEntitiesResponse, LegalEntity, LegalEntityResponse } from '@page/ess/model/bpdm.model';

export class BpdmAssembler {

  public static assembleLegalEntity(legalEntityResponse: LegalEntityResponse): LegalEntity {
    if (!legalEntityResponse) {
      return null;
    }
    let mappedPart
      = {
      score: legalEntityResponse.score,
      legalName: legalEntityResponse.legalName,
      bpnl: legalEntityResponse.bpnl,
      legalShortName: legalEntityResponse.legalShortName,
      currentness: legalEntityResponse.currentness,
      createdAt: legalEntityResponse.createdAt,
      updatedAt: legalEntityResponse.updatedAt,
      legalAddress: legalEntityResponse.legalAddress
    }
    return mappedPart;
  }

  public static assembleLegalEntities(legalEntitiesResponse: LegalEntitiesResponse): PaginationBpdm<LegalEntity> {
    return PaginationBpdmAssembler.assemblePaginationBpdm(BpdmAssembler.assembleLegalEntity, legalEntitiesResponse);
  }

}
