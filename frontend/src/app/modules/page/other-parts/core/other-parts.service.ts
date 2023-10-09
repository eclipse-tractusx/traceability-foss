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

import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService } from '@core/api/api.service';
import { Pagination } from '@core/model/pagination.model';
import { environment } from '@env';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { Owner } from '@page/parts/model/owner.enum';
import { AssetAsBuiltFilter, AssetAsPlannedFilter, Part, PartsResponse } from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { enrichFilterAndGetUpdatedParams } from '@shared/helper/filter-helper';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class OtherPartsService {
  private url = environment.apiUrl;

  constructor(private readonly apiService: ApiService) {
  }


  public getOtherPartsAsBuilt(page: number, pageSize: number, sorting: TableHeaderSort[], owner: Owner, filter?: AssetAsBuiltFilter, isOrSearch?: boolean): Observable<Pagination<Part>> {
    let sort = sorting.map(sortingItem => PartsAssembler.mapSortToApiSort(sortingItem));
    let params = this.buildHttpParams(page, pageSize, isOrSearch, owner);

    sort.forEach(sortingItem => {
      params = params.append('sort', sortingItem);
    });

    if (filter) {
      params = enrichFilterAndGetUpdatedParams(filter, params);
    }
    return this.apiService
      .getBy<PartsResponse>(`${ this.url }/assets/as-built`, params)
      .pipe(map(parts => PartsAssembler.assembleOtherParts(parts, MainAspectType.AS_BUILT)));
  }

  public getOtherPartsAsPlanned(page: number, pageSize: number, sorting: TableHeaderSort[], owner: Owner, filter?: AssetAsPlannedFilter, isOrSearch?: boolean): Observable<Pagination<Part>> {
    let sort = sorting.map(sortingItem => PartsAssembler.mapSortToApiSort(sortingItem));


    let params = this.buildHttpParams(page, pageSize, isOrSearch, owner);

    sort.forEach(sortingItem => {
      params = params.append('sort', sortingItem);
    });
    if (filter) {
      params = enrichFilterAndGetUpdatedParams(filter, params);
    }

    return this.apiService
      .getBy<PartsResponse>(`${ this.url }/assets/as-planned`, params)
      .pipe(map(parts => PartsAssembler.assembleOtherParts(parts, MainAspectType.AS_PLANNED)));
  }

  private buildHttpParams(page: number, pageSize: number, isOrSearch: boolean, owner: Owner): HttpParams{
    let filterOperator = isOrSearch ? 'OR' : 'AND';
   return new HttpParams()
      .set('page', page)
      .set('size', pageSize)
      .set('filterOperator', filterOperator)
      .set('filter', 'owner,EQUAL,' + owner);
  }

}
