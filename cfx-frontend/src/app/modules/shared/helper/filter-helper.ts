/********************************************************************************
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
import {
  AssetAsBuiltFilter,
  AssetAsPlannedFilter,
  FilterOperator,
  getFilterOperatorValue,
} from '@page/parts/model/parts.model';
import { HttpParams } from '@angular/common/http';
import { TableFilter } from '@shared/components/table/table.model';
import { DatePipe } from '@angular/common';

export const FILTER_KEYS = [
  'manufacturingDate',
  'functionValidFrom',
  'functionValidUntil',
  'validityPeriodFrom',
  'validityPeriodTo',
];

export function isDateFilter(key: string): boolean {
  return FILTER_KEYS.includes(key);
}

export function enrichFilterAndGetUpdatedParams(filter: AssetAsBuiltFilter, params: HttpParams): HttpParams {
  const semanticDataModelKey = 'semanticDataModel';
  for (const key in filter) {
    let operator: string;
    const filterValues = filter[key];
    if (key !== semanticDataModelKey) {
      if (filterValues.length !== 0) {
        if (isDateFilter(key)) {
          operator = getFilterOperatorValue(FilterOperator.AT_LOCAL_DATE);
        } else {
          operator = getFilterOperatorValue(FilterOperator.STARTS_WITH);
        }
        params = params.append('filter', `${key},${operator},${filterValues}`);
      }
    } else {
      operator = getFilterOperatorValue(FilterOperator.EQUAL);
      if (Array.isArray(filterValues)) {
        for (const value of filterValues) {
          params = params.append('filter', `${key},${operator},${value}`);
        }
      } else {
        params = params.append('filter', `${key},${operator},${filterValues}`);
      }
    }
  }
  return params;
}

export function addFilteringParams(filtering: TableFilter, params: HttpParams): HttpParams {
  const filterKeys = Object.keys(filtering);
  let filterApplied = false;
  if (filterKeys.length > 1) {
    for (const index in filterKeys) {
      const key = filterKeys[index];
      if (key !== 'filterMethod') {
        if (Array.isArray(filtering[key])) {
          if (filtering[key].length > 0) {
            filterApplied = true;
            for (const value of filtering[key]) {
              params = params.append(
                'filter',
                `${key},${getFilterOperatorValue(value.filterOperator)},${value.filterValue}`,
              );
            }
          }
        } else {
          const { filterValue } = filtering[key];
          if (filterValue !== '' && filterValue !== null) {
            filterApplied = true;
            params = params.append(
              'filter',
              `${key},${getFilterOperatorValue(filtering[key].filterOperator)},${filterValue}`,
            );
          }
        }
      }
    }
    if (filterApplied) {
      params = params.append('filterOperator', `${filtering['filterMethod']}`);
    }
  }
  return params;
}

export function toAssetFilter(formValues: any, isAsBuilt: boolean): AssetAsPlannedFilter | AssetAsBuiltFilter {
  const transformedFilter: any = {};

  // Loop through each form control and add it to the transformedFilter if it has a non-null and non-undefined value
  for (const key in formValues) {
    if (formValues[key] !== null && formValues[key] !== undefined && formValues[key] !== '') {
      transformedFilter[key] = formValues[key];
    }
  }

  const filterIsSet = Object.values(transformedFilter).some(value => value !== undefined && value !== null);
  if (filterIsSet) {
    if (isAsBuilt) {
      return transformedFilter as AssetAsBuiltFilter;
    } else {
      return transformedFilter as AssetAsPlannedFilter;
    }
  } else {
    return null;
  }
}

function dateValue(dateString: string, datePipe: DatePipe): string {
  const dateArray = dateString.split(/[./-]/);
  let returnDate = '';
  if (dateArray.length === 3 && !isNaN(+dateArray[0]) && !isNaN(+dateArray[1]) && !isNaN(+dateArray[2])) {
    if (dateArray[2].length === 2) {
      dateArray[2] = '20' + dateArray[2];
    }
    if (+dateArray[1] > 12 && +dateArray[0] <= 12) {
      const tmp = dateArray[1];
      dateArray[1] = dateArray[0];
      dateArray[0] = tmp;
    }
    const date = new Date(+dateArray[2], +dateArray[1] - 1, +dateArray[0]);
    returnDate = datePipe.transform(date, 'yyyy-MM-dd');
  }
  return returnDate;
}

export function toGlobalSearchAssetFilter(formValue: string, isAsBuilt: boolean, filterItemList: string[], datePipe?: DatePipe) {
  const filter = {};
  for (const filterItem of filterItemList) {
    if (isDateFilter(filterItem)) {
      if (datePipe) {
        filter[filterItem] = dateValue(formValue, datePipe);
      }
    } else {
      filter[filterItem] = formValue;
    };
  }
  if (isAsBuilt) {
    filter as AssetAsBuiltFilter;
  } else {
    filter as AssetAsPlannedFilter;
  }
  return filter;
}
