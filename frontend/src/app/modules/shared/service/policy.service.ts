import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService } from '@core/api/api.service';
import { Pagination } from '@core/model/pagination.model';
import { environment } from '@env';
import { Policy } from '@page/policies/model/policy.model';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PolicyService {
  private readonly url = environment.apiUrl;
  constructor(private readonly apiService: ApiService) {}

  getPolicies(): Observable<Policy[]> {
    return this.apiService
        .getBy<Policy[]>(`${this.url}/policies`);
  }

  getPaginatedPolicies(page: number, pageSize: number, sorting?: TableHeaderSort[], filter?: any): Observable<Pagination<Policy>> {

    const body = {
      pageAble: {
        page: page,
        size: pageSize,
        sorting: sorting ? sorting : undefined,
      },
      searchCriteria: {},
    };

    if (filter) {
      body.searchCriteria = { filter: this.createFilterList(filter) };
    }

    return this.apiService.post<Pagination<Policy>>(`${ this.url }/policies`, body);
  }

  publishAssets(assetIds: string[],policyId: string) {
    return this.apiService.post(`${this.url}/assets/publish`, {assetIds, policyId});
  }


  private createFilterList(filter: Object) {
    let filterList = [];
    Object.entries(filter).forEach(([ entry, values ]) => {
      if (values.length) {
        values.forEach(value => {
          filterList.push(`${ entry },EQUAL,${ value },AND`);
        });
      }
    });
    return filterList;
  }

  getDistinctFilterValues(filterColumns: string, searchElement: string) {
    let params = new HttpParams()
      .set('fieldName', filterColumns)
      .set('startWith', searchElement)
      .set('size', 200);

    return this.apiService.getBy<any>(`${ this.url }/policies/distinctFilterValues`, params);
  }
}
