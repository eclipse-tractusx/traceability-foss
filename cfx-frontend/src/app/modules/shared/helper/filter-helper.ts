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
import { HttpParams } from '@angular/common/http';
import {
  AssetAsBuiltFilter,
  AssetAsPlannedFilter,
  FilterOperator,
  getFilterOperatorValue,
} from '@page/parts/model/parts.model';
import { NotificationFilter } from '../../../mocks/services/investigations-mock/investigations.model';
import { FilterMethod, TableFilter } from '@shared/components/table/table.model';

export const DATE_FILTER_KEYS = ['manufacturingDate', 'functionValidFrom', 'functionValidUntil', 'validityPeriodFrom', 'validityPeriodTo', 'createdDate', 'targetDate'];

export function isNotificationCountFilter(key: string): boolean {
  return 'receivedQualityAlertIdsInStatusActive' === key || 'sentQualityAlertIdsInStatusActive' === key || 'receivedQualityInvestigationIdsInStatusActive' === key || 'sentQualityInvestigationIdsInStatusActive' === key;
}

export function isDateFilter(key: string): boolean {
  return DATE_FILTER_KEYS.includes(key);
}

export function isStartsWithFilter(key: string): boolean {
  return !isDateFilter(key) && !isNotificationCountFilter(key);
}

export function isDateRangeFilter(filterValues: string): boolean {
  return filterValues.includes(',');
}

export function isSameDate(startDate: string, endDate: string): boolean {
  return startDate === endDate;
}

export function toAssetFilter(formValues: any, isAsBuilt: boolean): AssetAsPlannedFilter | AssetAsBuiltFilter {

  const transformedFilter: any = {};

  // Loop through each form control and add it to the transformedFilter if it has a non-null and non-undefined value
  for (const key in formValues) {
    if (formValues[key] !== null && formValues[key] !== undefined) {
      if ('receivedActiveAlerts' === key) {
        transformedFilter['receivedQualityAlertIdsInStatusActive'] = formValues[key];
        continue;
      }
      if ('sentActiveAlerts' === key) {
        transformedFilter['sentQualityAlertIdsInStatusActive'] = formValues[key];
        continue;
      }
      if ('receivedActiveInvestigations' === key) {
        transformedFilter['receivedQualityInvestigationIdsInStatusActive'] = formValues[key];
        continue;
      }
      if ('sentActiveInvestigations' === key) {
        transformedFilter['sentQualityInvestigationIdsInStatusActive'] = formValues[key];
        continue;
      }
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

export function enrichDeeplinkFilterAndGetUpdatedFilter(filter: any): string[] {
  const filterList: string[] = [];
  if (filter?.notificationIds) {

    if (Array.isArray(filter.notificationIds)) {
      filter.notificationIds.forEach(notificationId => {
        filterList.push('status,EQUAL,' + notificationId + ',OR');
      });
    } else {
      filterList.push('status,EQUAL,' + filter.notificationIds + ',OR');
    }

  }
  return filterList;
}

export function toGlobalSearchAssetFilter(formValues: string, isAsBuilt: boolean) {
  let filter;
  if (isAsBuilt) {
    filter = {
      id: formValues,
      idShort: formValues,
      semanticModelId: formValues,
      customerPartId: formValues,
      manufacturerName: formValues,
      nameAtManufacturer: formValues,
      businessPartner: formValues,
      classification: formValues,
      manufacturerPartId: formValues,
      nameAtCustomer: formValues,
      manufacturingCountry: formValues
    } as AssetAsBuiltFilter;
  } else {
    filter = {
      id: formValues,
      idShort: formValues,
      semanticModelId: formValues,
      manufacturerPartId: formValues,
      nameAtManufacturer: formValues,
      manufacturerName: formValues,
      businessPartner: formValues,
      classification: formValues,
      catenaxSiteId: formValues,
    } as AssetAsPlannedFilter;
  }

  return filter;
}

export function enrichFilterAndGetUpdatedParams(filter: AssetAsBuiltFilter, params: HttpParams, filterOperator: string): HttpParams {

  for (const key in filter) {
    let operator: string;
    const filterValues: string = filter[key];
    if (!filterValues) {
      continue;
    }
    // has date
    if (isDateFilter(key)) {
      if (isDateRangeFilter(filterValues)) {
        const [startDate, endDate] = filterValues.split(',');
        if (isSameDate(startDate, endDate)) {
          operator = getFilterOperatorValue(FilterOperator.AT_LOCAL_DATE);
          params = params.append('filter', `${key},${operator},${startDate},${filterOperator}`);
          continue;
        }
        const endDateOperator = getFilterOperatorValue(FilterOperator.BEFORE_LOCAL_DATE);
        operator = getFilterOperatorValue((FilterOperator.AFTER_LOCAL_DATE));
        params = params.append('filter', `${key},${operator},${startDate},${filterOperator}`);
        params = params.append('filter', `${key},${endDateOperator},${endDate},${filterOperator}`);
        continue;
      } else if (filterValues && filterValues.length != 0) {
        operator = getFilterOperatorValue(FilterOperator.AT_LOCAL_DATE);
        params = params.append('filter', `${key},${operator},${filterValues},${filterOperator}`);
      }
    }

    // has multiple values
    if (isStartsWithFilter(key) && Array.isArray(filter[key])) {
      operator = getFilterOperatorValue(FilterOperator.EQUAL);

      for (const value of filter[key]) {
        params = params.append('filter', `${key},${operator},${value},${filterOperator}`);
      }
    }

    // has single value
    if (isStartsWithFilter(key) && !Array.isArray(filter[key])) {
      operator = getFilterOperatorValue(FilterOperator.STARTS_WITH);
      params = params.append('filter', `${key},${operator},${filterValues},${filterOperator}`);
    }

    if (isNotificationCountFilter(key) && filterValues && filterValues.length != 0) {
      operator = getFilterOperatorValue(FilterOperator.NOTIFICATION_COUNT_EQUAL);
      params = params.append('filter', `${key},${operator},${filterValues},${filterOperator}`);
    }

  }

  return params;
}

export function provideFilterListForNotifications(filter?: NotificationFilter, fullFilter?: any, filterMethod = FilterMethod.AND): string[] {
  let filterList: string[] = [];

  if (filter && !fullFilter) {
    filterList = enrichDeeplinkFilterAndGetUpdatedFilter(filter);
  }

  if (!filter && fullFilter) {
    const params = enrichFilterAndGetUpdatedParams(fullFilter, new HttpParams(), filterMethod);
    const filterParams = params.getAll('filter');
    if (filterParams) {
      filterParams.forEach(filter => { filterList.push(filter); });
    }
  }
  return filterList;
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
