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
import {HttpParams} from '@angular/common/http';
import {
    AssetAsBuiltFilter,
    AssetAsPlannedFilter,
    FilterOperator,
    getFilterOperatorValue,
} from '@page/parts/model/parts.model';

export const DATE_FILTER_KEYS = [ 'manufacturingDate', 'functionValidFrom', 'functionValidUntil', 'validityPeriodFrom', 'validityPeriodTo' ];

// TODO: Refactor function as soon as multi value filter is supported
export function enrichFilterAndGetUpdatedParams(filter: AssetAsBuiltFilter, params: HttpParams, filterOperator: string): HttpParams {
    for (const key in filter) {
        let operator: string;
        const filterValues: string = filter[key];
            if (filterValues.length !== 0) {
                if (isDateFilter(key)) {
                  if(isDateRangeFilter(filterValues)) {
                    const [startDate, endDate] = filterValues.split(",")
                    if(isSameDate(startDate,endDate)) {
                      operator = getFilterOperatorValue(FilterOperator.AT_LOCAL_DATE);
                      params = params.append('filter', `${key},${operator},${startDate},${filterOperator}`);
                      continue;
                    }
                    let endDateOperator = getFilterOperatorValue(FilterOperator.BEFORE_LOCAL_DATE)
                    operator = getFilterOperatorValue((FilterOperator.AFTER_LOCAL_DATE));
                    params = params.append('filter', `${key},${operator},${startDate},${filterOperator}`);
                    params = params.append('filter', `${key},${endDateOperator},${endDate},${filterOperator}`);
                    continue;
                  } else {
                    operator = getFilterOperatorValue(FilterOperator.AFTER_LOCAL_DATE);
                  }
                }
                if (isNotificationCountFilter(key)) {

                    operator = getFilterOperatorValue(FilterOperator.NOTIFICATION_COUNT_EQUAL);
                } else {
                    operator = getFilterOperatorValue(FilterOperator.STARTS_WITH);
                }
            }

        if (Array.isArray(filterValues)) {
            for (let value of filterValues) {
                params = params.append('filter', `${key},${operator},${value},${filterOperator}`);
            }
        } else {
            params = params.append('filter', `${key},${operator},${filterValues},${filterOperator}`);
        }
    }
    return params;
}

export function isStartsWithFilter(key: string): boolean {
  return !isDateFilter(key) && !isNotificationCountFilter(key);
}

export function isNotificationCountFilter(key: string): boolean {
  return 'qualityInvestigationIdsInStatusActive' === key || 'qualityAlertIdsInStatusActive' === key || 'activeAlerts' === key || 'activeInvestigations' === key;
}

export function isDateFilter(key: string): boolean {
  return DATE_FILTER_KEYS.includes(key);
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
      if ('activeAlerts' === key) {
        transformedFilter['qualityAlertIdsInStatusActive'] = formValues[key];
      }
      if ('activeInvestigations' === key) {
        transformedFilter['qualityInvestigationIdsInStatusActive'] = formValues[key];
      } else {
        transformedFilter[key] = formValues[key];
      }

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

export function toGlobalSearchAssetFilter(formValues: string, isAsBuilt: boolean) {
  let filter;
  if (isAsBuilt) {
    filter = {
      id: formValues,
      semanticModelId: formValues,
      idShort: formValues,
      customerPartId: formValues,
      manufacturerPartId: formValues,
    } as AssetAsBuiltFilter;
  } else {
    filter = {
      id: formValues,
      idShort: formValues,
      semanticModelId: formValues,
      manufacturerPartId: formValues,
    } as AssetAsPlannedFilter;
  }

  return filter;
}
