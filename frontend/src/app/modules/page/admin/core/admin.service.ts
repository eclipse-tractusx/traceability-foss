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
import { AdminAssembler } from '@page/admin/core/admin.assembler';
import {
  assembleContract,
  BpnConfig,
  BpnConfigResponse,
  Contract,
  ContractResponse,
} from '@page/admin/core/admin.model';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class AdminService {
  public readonly url = environment.apiUrl;

  constructor(private readonly apiService: ApiService) {
  }


  public createBpnFallbackConfig(bpnConfig: BpnConfig[]): Observable<BpnConfig[]> {
    return this.apiService.post<BpnConfig[]>(`${ this.url }/bpn-config`, bpnConfig);
  }

  public readBpnFallbackConfig(): Observable<BpnConfig[]> {
    return this.apiService
      .get<BpnConfigResponse[]>(`${ this.url }/bpn-config`)
      .pipe(map(data => AdminAssembler.assembleBpnConfig(data)));
  }

  public updateBpnFallbackConfig(bpnConfig: BpnConfig[]): Observable<BpnConfig[]> {
    return this.apiService.put<BpnConfig[]>(`${ this.url }/bpn-config`, bpnConfig);
  }

  public deleteBpnFallbackConfig(bpn: string): Observable<void> {
    return this.apiService.delete<void>(`${ this.url }/bpn-config/${ bpn }`);
  }

  public postJsonFile(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.apiService.postFile(`${ this.url }/assets/import`, formData);
  }

  public getContracts(page: number, pageSize: number, sorting?: TableHeaderSort[], filter?: Object): Observable<Pagination<Contract>> {

    const body = {
      pageAble: {
        page: page,
        size: pageSize,
        sorting: undefined
      },
      searchCriteria: {},
    };

    if (filter) {
      body.searchCriteria = { filter: this.createFilterList(filter) };
    }

    if(sorting) {
      body.pageAble.sorting = sorting;
    }

    return this.apiService.post<Pagination<ContractResponse>>(`${ this.url }/contracts`, body).pipe(
      map(response => {
        const assembled = response.content.map(contract => assembleContract(contract));
        return {...response, content: assembled} as Pagination<Contract>;
      })
    )
  }

  getDistinctFilterValues(filterColumns: string, searchElement: string) {
    let params = new HttpParams()
      .set('fieldName', filterColumns)
      .set('startWith', searchElement)
      .set('size', 200);

    return this.apiService.getBy<any>(`${ this.url }/contracts/distinctFilterValues`, params);
  }

  private createFilterList(filter: Object) {
    let filterList = [];
    Object.entries(filter).forEach(([ entry, values ]) => {
      if (values.length) {
        values.forEach(value => {
          filterList.push(`${ entry },EQUAL,${ value },OR`);
        });
      }
    });
    return filterList;
  }
}
