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

import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService } from '@core/api/api.service';
import { PaginationBpdm } from '@core/model/pagination.bpdm.model';
import { Pagination } from '@core/model/pagination.model';
import { environment } from '@env';
import { LegalEntitiesResponse, LegalEntity } from '@page/ess/model/bpdm.model';
import { BpdmAssembler } from '@shared/assembler/bpdm.assembler';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class BpdmService {

  private readonly url = environment.apiUrl;

  constructor(private readonly apiService: ApiService) {
  }

  public getLegalEntities(company_name: string): Observable<PaginationBpdm<LegalEntity>> {
    let params = new HttpParams()
      .set('legalName', company_name);
    return this.apiService
      .getBy<LegalEntitiesResponse>(`${this.url}/catena/legal-entities`, params)
      .pipe(map(legalEntities => BpdmAssembler.assembleLegalEntities(legalEntities)));
  }

}
