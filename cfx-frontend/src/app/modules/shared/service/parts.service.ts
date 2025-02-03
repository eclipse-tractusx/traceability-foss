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
    AssetAsDesignedFilter,
    AssetAsOrderedFilter,
    AssetAsPlannedFilter,
    AssetAsRecycledFilter,
    AssetAsSupportedFilter,
    Part,
    PartResponse,
    PartsResponse
} from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { TableHeaderSort } from '@shared/components/table/table.model';
import _deepClone from 'lodash-es/cloneDeep';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { SortDirection } from '../../../mocks/services/pagination.helper';
import { enrichFilterAndGetUpdatedParams } from "@shared/helper/filter-helper";
import { Owner } from '@page/parts/model/owner.enum';

@Injectable()
export class PartsService {
    private readonly url = environment.apiUrl;

    constructor(private readonly apiService: ApiService) {
    }

    public getPartsAsBuilt(page: number, pageSize: number, sorting: TableHeaderSort[], assetAsBuiltFilter?: AssetAsBuiltFilter, isOrSearch?: boolean): Observable<Pagination<Part>> {
        return this.getPagination('as-built', MainAspectType.AS_BUILT, page, pageSize, sorting, assetAsBuiltFilter, isOrSearch);
    }

    public getPartsAsPlanned(page: number, pageSize: number, sorting: TableHeaderSort[], assetAsPlannedFilter?: AssetAsPlannedFilter, isOrSearch?: boolean): Observable<Pagination<Part>> {
        return this.getPagination('as-planned', MainAspectType.AS_PLANNED, page, pageSize, sorting, assetAsPlannedFilter, isOrSearch);
    }

    public getPartsAsDesigned(page: number, pageSize: number, sorting: TableHeaderSort[], assetAsDesignedFilter?: AssetAsDesignedFilter, isOrSearch?: boolean): Observable<Pagination<Part>> {
        return this.getPagination('as-designed', MainAspectType.AS_DESIGNED, page, pageSize, sorting, assetAsDesignedFilter, isOrSearch);
    }

    public getPartsAsSupported(page: number, pageSize: number, sorting: TableHeaderSort[], assetAsSupportedFilter?: AssetAsSupportedFilter, isOrSearch?: boolean): Observable<Pagination<Part>> {
        return this.getPagination('as-supported', MainAspectType.AS_SUPPORTED, page, pageSize, sorting, assetAsSupportedFilter, isOrSearch);
    }

    public getPartsAsOrdered(page: number, pageSize: number, sorting: TableHeaderSort[], assetAsOrderedFilter?: AssetAsOrderedFilter, isOrSearch?: boolean): Observable<Pagination<Part>> {
        return this.getPagination('as-ordered', MainAspectType.AS_ORDERED, page, pageSize, sorting, assetAsOrderedFilter, isOrSearch);
    }

    public getPartsAsRecycled(page: number, pageSize: number, sorting: TableHeaderSort[], assetAsRecycledFilter?: AssetAsRecycledFilter, isOrSearch?: boolean): Observable<Pagination<Part>> {
        return this.getPagination('as-recycled', MainAspectType.AS_RECYCLED, page, pageSize, sorting, assetAsRecycledFilter, isOrSearch);
    }

    public getPart(id: string, type: MainAspectType): Observable<Part> {
        if (type === MainAspectType.AS_PLANNED) {
            return this.apiService.get<PartResponse>(`${this.url}/assets/as-planned/${id}`)
                .pipe(map(part => PartsAssembler.assemblePart(part, type)));
        }

        return this.apiService.get<PartResponse>(`${this.url}/assets/as-built/${id}`)
            .pipe(map(part => PartsAssembler.assemblePart(part, type)));
    }

    public getPartDetailOfIds(assetIds: string[], type = MainAspectType.AS_BUILT): Observable<Part[]> {
        if (type === MainAspectType.AS_BUILT) {
            const resultsAsBuilt = this.apiService
                .post<PartResponse[]>(`${this.url}/assets/as-built/detail-information`, { assetIds })
                .pipe(map(parts => PartsAssembler.assemblePartList(parts, MainAspectType.AS_BUILT)));

            if (resultsAsBuilt) {
                return resultsAsBuilt;
            }
        } else {
            const resultsAsPlanned = this.apiService
                .post<PartResponse[]>(`${this.url}/assets/as-planned/detail-information`, { assetIds })
                .pipe(map(parts => PartsAssembler.assemblePartList(parts, MainAspectType.AS_PLANNED)));

            if (resultsAsPlanned) {
                return resultsAsPlanned;
            }
        }
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

    getPagination(path: string, type: MainAspectType, page: number, pageSize: number, sorting: TableHeaderSort[], filter?: AssetAsBuiltFilter, isOrSearch?: boolean): Observable<Pagination<Part>> {
        const sort = sorting.map(sortingItem => PartsAssembler.mapSortToApiSort(sortingItem));
        const filterOperator = isOrSearch ? "OR" : "AND";
        let params = new HttpParams()
            .set('page', page)
            .set('size', pageSize)
            .set('filter', `owner,EQUAL,OWN,AND`);

        sort.forEach(sortingItem => {
            params = params.append('sort', sortingItem);
        });

        if (filter) {
            params = enrichFilterAndGetUpdatedParams(filter, params, filterOperator);
        }

        return this.apiService
            .getBy<PartsResponse>(`${this.url}/assets/${path}`, params)
            .pipe(map(parts => PartsAssembler.assembleParts(parts, type)));
    }

    public getDistinctFilterValues(isAsBuilt: boolean, owner: Owner, fieldNames: string, startsWith: string) {
        const mappedFieldName = PartsAssembler.mapFieldNameToApi(fieldNames);
        const params = new HttpParams()
            .set('fieldName', mappedFieldName)
            .set('startWith', startsWith)
            .set('size', 200)
            .set('owner', owner);


        if (isAsBuilt) {
            return this.apiService
                .getBy<any>(`${this.url}/assets/as-built/distinctFilterValues`, params);
        } else {
            return this.apiService
                .getBy<any>(`${this.url}/assets/as-planned/distinctFilterValues`, params);


        }
    }
}
