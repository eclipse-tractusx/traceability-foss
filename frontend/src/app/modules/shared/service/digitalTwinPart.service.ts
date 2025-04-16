/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

import { Injectable } from '@angular/core';
import { ApiService } from '@core/api/api.service';
import { environment } from '@env';
import { Pagination } from '@core/model/pagination.model';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { DigitalTwinPartDetailResponse, DigitalTwinPartRequest, DigitalTwinPartResponse, DigitalTwinPartsResponse } from '@page/digital-twin-part/model/digitalTwinPart.model';
import { DigitalTwinPartAssembler } from '@shared/assembler/digital-twin-part.assembler';
import { DigitalTwinPartFilter } from '@shared/model/filter.model';

@Injectable()
export class DigitalTwinPartService {
  private readonly url = `${ environment.apiUrl }/administration`;

  constructor(private readonly apiService: ApiService) {}

  public getDigitalTwinParts(
    page: number,
    size: number,
    sorting: TableHeaderSort[],
    filters?: DigitalTwinPartFilter[]
  ): Observable<Pagination<DigitalTwinPartResponse>> {
    const sort = sorting.map(item => DigitalTwinPartAssembler.mapSortToApiSort(item));
  
    const requestBody: DigitalTwinPartRequest = {
      page,
      size,
      sort,
      filters: filters ?? [],
    };  
    return this.apiService
    .post<DigitalTwinPartsResponse>(`${this.url}/digitalTwinPart`, requestBody)
    .pipe(map(res => DigitalTwinPartAssembler.assembleParts(res)));

  }
  

  public getDigitalTwinPartDetail(aasId: string): Observable<DigitalTwinPartDetailResponse> {
    const requestBody = { aasId };
    return this.apiService.post<DigitalTwinPartDetailResponse>(
      `${this.url}/digitalTwinPart/detail`,
      requestBody
    );
  }

  public getDistinctFilterValues(fieldName: string, startWith: string): Observable<string[]> {
    const requestBody = {
      fieldName: fieldName,
      startWith: startWith,
      size: 10 
    };
  
    return this.apiService.post<string[]>(
      `${this.url}/digitalTwinPart/searchable-values`,
      requestBody
    );
  }
}
