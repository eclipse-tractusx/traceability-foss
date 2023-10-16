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
    getFilterOperatorValue
} from "@page/parts/model/parts.model";
import {HttpParams} from "@angular/common/http";

export const FILTER_KEYS = ['manufacturingDate', 'functionValidFrom', 'functionValidUntil', 'validityPeriodFrom', 'validityPeriodTo'];

export function enrichFilterAndGetUpdatedParams(filter: AssetAsBuiltFilter, params: HttpParams): HttpParams {
    const semanticDataModelKey = "semanticDataModel";
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
                for (let value of filterValues) {
                    params = params.append('filter', `${key},${operator},${value}`);
                }
            } else {
                params = params.append('filter', `${key},${operator},${filterValues}`);
            }

        }
    }
    return params;
}

export function isDateFilter(key: string): boolean {
    return FILTER_KEYS.includes(key);
}

export function toAssetFilter(formValues: any, isAsBuilt: boolean): AssetAsPlannedFilter | AssetAsBuiltFilter {

    const transformedFilter: any = {};

    // Loop through each form control and add it to the transformedFilter if it has a non-null and non-undefined value
    for (const key in formValues) {
        if (formValues[key] !== null && formValues[key] !== undefined) {
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

export function toGlobalSearchAssetFilter(formValues: string, isAsBuilt: boolean) {
    let filter;
    if (isAsBuilt) {
        filter = {
            id: formValues,
            semanticModelId: formValues,
            idShort: formValues,
            customerPartId: formValues,
            manufacturerPartId: formValues
        } as AssetAsBuiltFilter;
    } else {
        filter = {
            id: formValues,
            idShort: formValues,
            semanticModelId: formValues,
            manufacturerPartId: formValues
        } as AssetAsPlannedFilter;
    }

    return filter;
}
