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
import {AssetAsBuiltFilter, AssetAsPlannedFilter} from "@page/parts/model/parts.model";
import {HttpParams} from "@angular/common/http";


export function enrichFilterAndGetUpdatedParams(filter: AssetAsBuiltFilter, params: any): HttpParams {
    for (const key in filter) {
        const value = filter[key];
        if (value.length !== 0) {
            console.log(value, "value after");
            // Modify this line to format the filter
            let operator;
            if (key === "semanticDataModel") {
                operator = 'EQUAL';
            } else if (key.toLowerCase().includes('date')) {
                operator = 'AT_LOCAL_DATE';
            } else {
                operator = 'STARTS_WITH';
            }
            return params = params.append('filter', `${key},${operator},${value}`);
        }
    }
}


export function toAssetAsBuiltFilter(formValues: any): AssetAsBuiltFilter {
    const transformedFilter: AssetAsBuiltFilter = {};

    // Loop through each form control and add it to the transformedFilter if it has a non-null and non-undefined value
    for (const key in formValues) {
        if (formValues[key] !== null && formValues[key] !== undefined) {
            transformedFilter[key] = formValues[key];
        }
    }

    const filterIsSet = Object.values(transformedFilter).some(value => value !== undefined && value !== null);
    console.log(transformedFilter, "filter");
    console.log(filterIsSet, "set?");
    if (filterIsSet) {
        return transformedFilter;
    } else {
        return null;
    }
}

export function toAssetAsPlannedFilter(formValues: any): AssetAsPlannedFilter {

    const transformedFilter: AssetAsPlannedFilter = {};

    // Loop through each form control and add it to the transformedFilter if it has a non-null and non-undefined value
    for (const key in formValues) {
        if (formValues[key] !== null && formValues[key] !== undefined) {
            transformedFilter[key] = formValues[key];
        }
    }

    const filterIsSet = Object.values(transformedFilter).some(value => value !== undefined && value !== null);
    if (filterIsSet) {
        return transformedFilter;
    } else {
        return null;
    }

}
