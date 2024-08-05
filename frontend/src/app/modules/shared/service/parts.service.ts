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
import {
  AssetAsBuiltFilter,
  AssetAsPlannedFilter,
  Part,
  PartResponse,
  PartsResponse,
} from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { enrichFilterAndGetUpdatedParams } from '@shared/helper/filter-helper';
import _deepClone from 'lodash-es/cloneDeep';
import { forkJoin, Observable, of } from 'rxjs';
import { catchError, filter, map } from 'rxjs/operators';
import { SortDirection } from '../../../mocks/services/pagination.helper';

@Injectable()
export class PartsService {
  private readonly url = environment.apiUrl;

  constructor(private readonly apiService: ApiService) {
  }

  public reloadRegistry() {
    return this.apiService.get(`${ this.url }/registry/reload`);
  }

  public syncPartsAsBuilt(globalAssetIds: string[]) {
    return this.apiService.post(`${ this.url }/assets/as-built/sync`, { globalAssetIds });
  }

  public syncPartsAsPlanned(globalAssetIds: string[]) {
    return this.apiService.post(`${ this.url }/assets/as-planned/sync`, { globalAssetIds });
  }

  public getPartsAsBuilt(page: number, pageSize: number, sorting: TableHeaderSort[], assetAsBuiltFilter?: AssetAsBuiltFilter, isOrSearch?: boolean): Observable<Pagination<Part>> {

    let sort = sorting.map(sortingItem => PartsAssembler.mapSortToApiSort(sortingItem));
    let filterOperator = isOrSearch ? 'OR' : 'AND';

    let params = new HttpParams()
      .set('page', page)
      .set('size', pageSize);

    sort.forEach(sortingItem => {
      params = params.append('sort', sortingItem);
    });

    if (assetAsBuiltFilter) {
      params = enrichFilterAndGetUpdatedParams(assetAsBuiltFilter, params, filterOperator);
    }

    return this.apiService
      .getBy<PartsResponse>(`${ this.url }/assets/as-built`, params)
      .pipe(map(parts => PartsAssembler.assembleParts(parts, MainAspectType.AS_BUILT)));
  }


  public getPartsAsPlanned(page: number, pageSize: number, sorting: TableHeaderSort[], assetAsPlannedFilter?: AssetAsPlannedFilter, isOrSearch?: boolean): Observable<Pagination<Part>> {
    let sort = sorting.map(sortingItem => PartsAssembler.mapSortToApiSort(sortingItem));
    let filterOperator = isOrSearch ? 'OR' : 'AND';
    let params = new HttpParams()
      .set('page', page)
      .set('size', pageSize);

    sort.forEach(sortingItem => {
      params = params.append('sort', sortingItem);
    });

    if (assetAsPlannedFilter) {
      params = enrichFilterAndGetUpdatedParams(assetAsPlannedFilter, params, filterOperator);
    }

    return this.apiService
      .getBy<PartsResponse>(`${ this.url }/assets/as-planned`, params)
      .pipe(map(parts => PartsAssembler.assembleParts(parts, MainAspectType.AS_PLANNED)));
  }

  public getPart(id: string): Observable<Part> {
    if (!id || typeof id !== 'string') {
      throw new Error('invalid ID');
    }

    const encodedId = encodeURIComponent(id);

    const resultsAsBuilt = this.apiService.get<PartResponse>(`${ this.url }/assets/as-built/${ encodedId }`).pipe(
      map(part => PartsAssembler.assemblePart(part, MainAspectType.AS_BUILT)),
      catchError(() => of(null)),
    );
    const resultsAsPlanned = this.apiService.get<PartResponse>(`${ this.url }/assets/as-planned/${ encodedId }`).pipe(
      map(part => PartsAssembler.assemblePart(part, MainAspectType.AS_PLANNED)),
      catchError(() => of(null)),
    );

    // Combine both observables and filter out null values from the array
    return forkJoin([ resultsAsBuilt, resultsAsPlanned ]).pipe(
      filter(([ partAsBuilt, partAsPlanned ]) => partAsBuilt !== null || partAsPlanned !== null),
      map(([ partAsBuilt, partAsPlanned ]) => partAsBuilt || partAsPlanned),
    );
  }

  public getPartByIdAsBuilt(id: string): Observable<Part> {
    if (!id || typeof id !== 'string') {
      throw new Error('invalid ID');
    }

    const encodedId = encodeURIComponent(id);

    return this.apiService.get<PartResponse>(`${ this.url }/assets/as-built/${ encodedId }`).pipe(
      map(part => PartsAssembler.assemblePart(part, MainAspectType.AS_BUILT)),
      catchError(() => of(null)),
    );


  }

  public getPartByIdAsPlanned(id: string): Observable<Part> {
    if (!id || typeof id !== 'string') {
      throw new Error('invalid ID');
    }

    const encodedId = encodeURIComponent(id);

    return this.apiService.get<PartResponse>(`${ this.url }/assets/as-planned/${ encodedId }`).pipe(
      map(part => PartsAssembler.assemblePart(part, MainAspectType.AS_PLANNED)),
      catchError(() => of(null)),
    );

  }
  public getPartDetailOfIds(assetIds: string[], isAsBuilt?: boolean): Observable<Part[]> {

    if (isAsBuilt !== undefined) {
      if (isAsBuilt) {
        return this.apiService
          .post<PartResponse[]>(`${ this.url }/assets/as-built/detail-information`, { assetIds })
          .pipe(map(parts => PartsAssembler.assemblePartList(parts, MainAspectType.AS_BUILT)));
      } else {
        return this.apiService
          .post<PartResponse[]>(`${ this.url }/assets/as-planned/detail-information`, { assetIds })
          .pipe(map(parts => PartsAssembler.assemblePartList(parts, MainAspectType.AS_PLANNED)));
      }

    } else {
      let resultsAsBuilt = this.apiService
        .post<PartResponse[]>(`${ this.url }/assets/as-built/detail-information`, { assetIds })
        .pipe(map(parts => PartsAssembler.assemblePartList(parts, MainAspectType.AS_BUILT)));

      let resultsAsPlanned = this.apiService
        .post<PartResponse[]>(`${ this.url }/assets/as-planned/detail-information`, { assetIds })
        .pipe(map(parts => PartsAssembler.assemblePartList(parts, MainAspectType.AS_PLANNED)));

      if (resultsAsBuilt) {
        return resultsAsBuilt;
      }

      if (resultsAsPlanned) {
        return resultsAsPlanned;
      }
    }

  }

  public getSearchableValues(isAsBuilt: boolean, fieldNames: string, startsWith: string, inAssetIds?: string[]) {
    const mappedFieldName = PartsAssembler.mapFieldNameToApi(fieldNames);

    const body = {
      "fieldName": mappedFieldName,
      "startWith": startsWith,
      "size": 200,
      "inAssetIds": inAssetIds ? inAssetIds : []
    }

    if (isAsBuilt) {
      return this.apiService
        .post(`${ this.url }/assets/as-built/searchable-values`, body);
    } else {
      return this.apiService
        .post(`${ this.url }/assets/as-planned/searchable-values`, body);
    }
  }

  public getPartsByFilter(filter: any, isAsBuilt: boolean): Observable<Pagination<Part>> {
    const params = enrichFilterAndGetUpdatedParams(filter, new HttpParams(), 'OR');
    const mainAspectType = isAsBuilt ? MainAspectType.AS_BUILT : MainAspectType.AS_PLANNED;
    const path = isAsBuilt ? 'as-built' : 'as-planned';

    return this.apiService
      .getBy<PartsResponse>(`${ this.url }/assets/${ path }`, params)
      .pipe(map(parts => PartsAssembler.assembleParts(parts, mainAspectType)));
  }

  public sortParts(data: Part[], key: string, direction: SortDirection): Part[] {
    const clonedData: Part[] = _deepClone(data);
    return clonedData.sort((partA, partB) => {
      const a = direction === 'desc' ? partA[key] : partB[key];
      const b = direction === 'desc' ? partB[key] : partA[key];

      if (a > b) return -1;
      if (a < b) return 1;
      return 0;
    });
  }
}
