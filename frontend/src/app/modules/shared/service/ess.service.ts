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
import { Pagination } from '@core/model/pagination.model';
import { environment } from '@env';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { EssFilter, Ess, EssListResponse } from '@page/ess/model/ess.model';
import { EssAssembler } from '@shared/assembler/ess.assembler';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { enrichFilterAndGetUpdatedParams } from '@shared/helper/filter-helper';
import { NotificationCreateResponse } from '@shared/model/notification.model';
import _deepClone from 'lodash-es/cloneDeep';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SortDirection } from '../../../mocks/services/pagination.helper';

@Injectable()
export class EssService {

  private readonly url = environment.apiUrl;

  constructor(private readonly apiService: ApiService) {
  }

  public getEssList(page: number, pageSize: number, sorting: TableHeaderSort[],
           essFilter?: EssFilter, isOrSearch?: boolean): Observable<Pagination<Ess>> {
    let sort = sorting.map(sortingItem => EssAssembler.mapSortToApiSort(sortingItem));
    let filterOperator = isOrSearch ? "OR" : "AND";
    let params = new HttpParams()
      .set('page', page)
      .set('size', pageSize)
    sort.forEach(sortingItem => {
      params = params.append('sort', sortingItem);
    })
    if (essFilter) {
      params = enrichFilterAndGetUpdatedParams(essFilter, params, filterOperator);
    }
    return this.apiService
      .getBy<EssListResponse>(`${this.url}/ess/v`, params)
      .pipe(map(essList => EssAssembler.assembleEssList(essList, MainAspectType.ESS)));
  }

  public sortParts(data: Ess[], key: string, direction: SortDirection): Ess[] {
    const clonedData: Ess[] = _deepClone(data);
    return clonedData.sort((partA, partB) => {
      const a = direction === 'desc' ? partA[key] : partB[key];
      const b = direction === 'desc' ? partB[key] : partA[key];
      if (a > b) return -1;
      if (a < b) return 1;
      return 0;
    });
  }

  public postEss(partIds: string[], bpns: string): Observable<string> {
    const body = { partIds, bpns };
    return this.apiService
      .post<NotificationCreateResponse>(`${this.url}/ess`, body)
      .pipe(map(({ id }) => id));
  }

}
