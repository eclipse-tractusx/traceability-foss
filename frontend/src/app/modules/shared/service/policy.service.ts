import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService } from '@core/api/api.service';
import { Pagination } from '@core/model/pagination.model';
import { environment } from '@env';
import { FilterOperator, getFilterOperatorValue } from '@page/parts/model/parts.model';
import { Policy } from '@page/policies/model/policy.model';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { isDateFilter, isDateRangeFilter, isSameDate, isStartsWithFilter } from '@shared/helper/filter-helper';
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
    const sort = sorting.length ? sorting.map(array => `${ array[0] },${ array[1] }`) : [ 'createdOn,desc' ];
    const body = {
      pageAble: {
        page: page,
        size: pageSize,
        sorting: sort,
      },
      searchCriteria: {},
    };

    if (filter) {
      body.searchCriteria = { filter: this.createFilterList(filter, 'AND') };
    }

    return this.apiService.post<Pagination<Policy>>(`${ this.url }/policies`, body);
  }

  publishAssets(assetIds: string[],policyId: string) {
    return this.apiService.post(`${this.url}/assets/publish`, {assetIds, policyId});
  }


  private createFilterList(filter: Object, filterOperator: string) {

    let filterList = [];

    for (const key in filter) {
      let operator: string;
      const filterValues: string = filter[key];
      if (!filterValues) {
        continue;
      }
      // has date
      if (isDateFilter(key)) {
        if (isDateRangeFilter(filterValues)) {
          const [ startDate, endDate ] = filterValues.split(',');
          if (isSameDate(startDate, endDate)) {
            operator = getFilterOperatorValue(FilterOperator.AT_LOCAL_DATE);
            filterList.push(`${ key },${ operator },${ startDate },${ filterOperator }`);
            continue;
          }
          let endDateOperator = getFilterOperatorValue(FilterOperator.BEFORE_LOCAL_DATE);
          operator = getFilterOperatorValue((FilterOperator.AFTER_LOCAL_DATE));
          filterList.push(`${ key },${ operator },${ startDate },${ filterOperator }`);
          filterList.push(`${ key },${ endDateOperator },${ endDate },${ filterOperator }`);
          continue;
        } else if (filterValues && filterValues.length != 0) {
          operator = getFilterOperatorValue(FilterOperator.AT_LOCAL_DATE);
          filterList.push(`${ key },${ operator },${ filterValues },${ filterOperator }`);
        }
      }

      // has multiple values
      if (isStartsWithFilter(key) && Array.isArray(filter[key])) {
        operator = getFilterOperatorValue(FilterOperator.EQUAL);

        for (const value of filter[key]) {
          filterList.push(`${ key },${ operator },${ value },${ filterOperator }`);
        }
      }

      // has single value
      if (isStartsWithFilter(key) && !Array.isArray(filter[key])) {
        operator = getFilterOperatorValue(FilterOperator.STARTS_WITH);
        filterList.push(`${ key },${ operator },${ filterValues },${ filterOperator }`);
      }

    }
    return filterList;
  }

  getDistinctFilterValues(filterColumns: string, searchElement: string) {
    let params = new HttpParams()
      .set('fieldName', filterColumns)
      .set('startWith', searchElement)
      .set('size', 200);

    return this.apiService.getBy<any>(`${ this.url }/policies/distinctFilterValues`, params);
  }

  deletePolicies(policyIds: string[]) {
    return this.apiService.delete(`${ this.url }/policies`, new HttpParams().set('policyIds', policyIds.toString()));
  }
}
