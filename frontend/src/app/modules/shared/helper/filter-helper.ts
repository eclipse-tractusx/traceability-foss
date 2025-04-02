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
import { AssetAsBuiltFilter, AssetAsPlannedFilter, FilterOperator, FilterValue } from '@shared/model/filter.model';

export const DATE_FILTER_KEYS = [ 'manufacturingDate', 'functionValidFrom', 'functionValidUntil', 'validityPeriodFrom', 'validityPeriodTo', 'createdDate', 'targetDate', 'creationDate', 'endDate', 'createdOn', 'validUntil' ];

export function isDateFilter(key: string): boolean {
  return DATE_FILTER_KEYS.includes(key);
}

export function toAssetFilter(formValues: any, isAsBuilt: boolean): AssetAsPlannedFilter | AssetAsBuiltFilter {

  const transformedFilter: any = {};

  // Loop through each form control and add it to the transformedFilter if it has a non-null and non-undefined value
  for (const key in formValues) {
    if (formValues[key] !== null && formValues[key] !== undefined && !Array.isArray(formValues[key])) {
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

export function toGlobalSearchAssetFilter(values: string[] | Record<string, string[]>, isAsBuilt: boolean): AssetAsPlannedFilter | AssetAsBuiltFilter {

  let filter: AssetAsPlannedFilter | AssetAsBuiltFilter;
  let strategy = FilterOperator.GLOBAL;
  let filterValues: FilterValue[];

  if (Array.isArray(values)) {
    filterValues = values.map(value => ({ value, strategy }));
  } else {
    filterValues = Object.values(values).flat().map(value => ({ value, strategy }));
  }
  if (isAsBuilt) {
    filter = {
      id: { value: filterValues, operator: 'OR' },
      semanticModelId: { value: filterValues, operator: 'OR' },
      idShort: { value: filterValues, operator: 'OR' },
      customerPartId: { value: filterValues, operator: 'OR' },
      manufacturerPartId: { value: filterValues, operator: 'OR' },
      businessPartner: { value: filterValues, operator: 'OR' },
    } as AssetAsBuiltFilter;
  } else {
    filter = {
      id: { value: filterValues, operator: 'OR' },
      idShort: { value: filterValues, operator: 'OR' },
      semanticModelId: { value: filterValues, operator: 'OR' },
      manufacturerPartId: { value: filterValues, operator: 'OR' },
      businessPartner: { value: filterValues, operator: 'OR' },
    } as AssetAsPlannedFilter;
  }
  return filter;
}

export function containsAtLeastOneFilterEntry(filter: AssetAsBuiltFilter | AssetAsPlannedFilter): boolean {
  return Object.values(filter).some((attr) =>
    attr?.value?.some((filterValue: FilterValue) => Boolean(filterValue.value?.trim())),
  );
}
