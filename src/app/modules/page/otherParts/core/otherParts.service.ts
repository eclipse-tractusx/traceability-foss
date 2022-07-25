/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService } from '@core/api/api.service';
import { Pagination } from '@core/model/pagination.model';
import { environment } from '@env';
import { Part, PartsResponse } from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class OtherPartsService {
  private url = environment.apiUrl;

  constructor(private apiService: ApiService) {}

  public getSupplierParts(page: number, pageSize: number, sorting: TableHeaderSort): Observable<Pagination<Part>> {
    const sort = PartsAssembler.mapSortToApiSort(sorting);
    const params = new HttpParams().set('page', page).set('size', pageSize).set('sort', sort);
    return this.apiService
      .getBy<PartsResponse>(`${this.url}/supplier-assets`, params)
      .pipe(map(parts => PartsAssembler.assembleOtherParts(parts)));
  }

  public getCustomerParts(page: number, pageSize: number, sorting: TableHeaderSort): Observable<Pagination<Part>> {
    const sort = PartsAssembler.mapSortToApiSort(sorting);
    const params = new HttpParams().set('page', page).set('size', pageSize).set('sort', sort);
    return this.apiService
      .getBy<PartsResponse>(`${this.url}/customer-assets`, params)
      .pipe(map(parts => PartsAssembler.assembleOtherParts(parts)));
  }
}
