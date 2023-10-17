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
import { enrichFilterAndGetUpdatedParams } from './filter-helper'

describe('enrichFilterAndGetUpdatedParams', () => {
    it('should append filter parameters for non-date filters', () => {
        const filter = {
            otherKey: ['value3']
        };
        const params = new HttpParams();
        // @ts-ignore
        const result = enrichFilterAndGetUpdatedParams(filter, params);

        expect(result.toString()).toContain('filter=otherKey,STARTS_WITH,value3');
    });

    it('should append filter parameters for date filters', () => {
        const filter = {
            functionValidUntil: ['2023-10-13'],
            functionValidFrom: ['2023-10-14'],
            validityPeriodFrom: ['2023-10-15'],
            validityPeriodTo: ['2023-10-17']
        };
        const params = new HttpParams();
        // @ts-ignore
        const result = enrichFilterAndGetUpdatedParams(filter, params);
        expect(result.toString()).toContain('filter=functionValidUntil,AT_LOCAL_DATE,2023-10-13');
        expect(result.toString()).toContain('filter=functionValidFrom,AT_LOCAL_DATE,2023-10-14');
        expect(result.toString()).toContain('filter=validityPeriodFrom,AT_LOCAL_DATE,2023-10-15');
        expect(result.toString()).toContain('filter=validityPeriodTo,AT_LOCAL_DATE,2023-10-17');
    });

    it('should append filter parameters for semanticDataModelKey', () => {
        const filter = {
            semanticDataModel: ['value1', 'value2'],
        };
        const params = new HttpParams();
        const result = enrichFilterAndGetUpdatedParams(filter, params);
        expect(result.toString()).toContain('filter=semanticDataModel,EQUAL,value1');
        expect(result.toString()).toContain('filter=semanticDataModel,EQUAL,value2');
    });

    it('should handle single value for semanticDataModelKey', () => {
        const filter = {
            semanticDataModel: 'value1',
        };
        const params = new HttpParams();
        // @ts-ignore
        const result = enrichFilterAndGetUpdatedParams(filter, params);
        expect(result.toString()).toContain('filter=semanticDataModel,EQUAL,value1');
    });

    it('should handle empty filter values', () => {
        const filter = {
            emptyFilter: [],
        };
        const params = new HttpParams();
        // @ts-ignore
        const result = enrichFilterAndGetUpdatedParams(filter, params);
        expect(result.toString()).not.toContain('filter=emptyFilter');
    });
});
