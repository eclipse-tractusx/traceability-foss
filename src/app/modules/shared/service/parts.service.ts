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

import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService } from '@core/api/api.service';
import { Pagination } from '@core/model/pagination.model';
import { environment } from '@env';
import {
  Part,
  PartResponse,
  PartsCountriesMap,
  PartsCountriesMapResponse,
  PartsResponse,
} from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class PartsService {
  private readonly url = environment.apiUrl;
  constructor(private readonly apiService: ApiService) {}

  public getParts(page: number, pageSize: number, sorting: TableHeaderSort): Observable<Pagination<Part>> {
    const sort = PartsAssembler.mapSortToApiSort(sorting);
    const params = new HttpParams().set('page', page).set('size', pageSize).set('sort', sort);

    return this.apiService
      .getBy<PartsResponse>(`${this.url}/assets`, params)
      .pipe(map(parts => PartsAssembler.assembleParts(parts)));
  }

  public getMyParts(page: number, pageSize: number, sorting: TableHeaderSort): Observable<Pagination<Part>> {
    const sort = PartsAssembler.mapSortToApiSort(sorting);
    const params = new HttpParams().set('page', page).set('size', pageSize).set('sort', sort);

    return this.apiService
      .getBy<PartsResponse>(`${this.url}/assets/my`, params)
      .pipe(map(parts => PartsAssembler.assembleParts(parts)));
  }

  public getPartsPerCountry(): Observable<PartsCountriesMap> {
    return this.apiService
      .getBy<PartsCountriesMapResponse>(`${this.url}/assets/countries`)
      .pipe(map(partsCountriesMap => PartsAssembler.assembleAssetsCountryMap(partsCountriesMap)));
  }

  public getPart(id: string): Observable<Part> {
    return this.apiService
      .get<PartResponse>(`${this.url}/assets/${id}`)
      .pipe(map(part => PartsAssembler.assemblePart(part)));
  }

  public patchPart({ qualityType, id }: Part): Observable<Part> {
    const patchBody = { qualityType };

    return this.apiService.patch<Part>(`${this.url}/assets/${id}`, patchBody);
  }

  public getPartDetailOfIds(assetIds: string[]): Observable<Part[]> {
    return this.apiService
      .post<PartResponse[]>(`${this.url}/assets/detail-information`, { assetIds })
      .pipe(map(parts => PartsAssembler.assemblePartList(parts)));
  }
}
