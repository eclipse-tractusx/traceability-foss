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
  containsAtLeastOneFilterEntry,
  isDateFilter,
  toAssetFilter,
  toGlobalSearchAssetFilter,
  toGlobalSearchDigitalTwinFilter,
  toStructuredDigitalTwinFilter
} from '@shared/helper/filter-helper';
import { FilterOperator } from '@shared/model/filter.model';

describe('Asset Filter Functions', () => {

  describe('isDateFilter', () => {
    it('should return true for a valid date filter key', () => {
      expect(isDateFilter('manufacturingDate')).toBeTrue();
    });

    it('should return false for an invalid date filter key', () => {
      expect(isDateFilter('invalidKey')).toBeFalse();
    });
  });

  describe('toAssetFilter', () => {
    it('should transform form values to AssetAsBuiltFilter', () => {
      const formValues = { id: {
          value: [ { value: 'value1', strategy: FilterOperator.GLOBAL }, {
            value: 'value2',
            strategy: FilterOperator.GLOBAL,
          } ],
          operator: 'OR',
        } };
      const result = toAssetFilter(formValues, true);
      expect(result).toEqual({ id: {
          value: [ { value: 'value1', strategy: FilterOperator.GLOBAL }, {
            value: 'value2',
            strategy: FilterOperator.GLOBAL,
          } ],
          operator: 'OR',
        } });
    });

    it('should return null if no valid filters are set', () => {
      const formValues = { key1: null, key2: undefined };
      expect(toAssetFilter(formValues, true)).toBeNull();
    });
  });

  describe('toGlobalSearchAssetFilter', () => {
    it('should create a global search filter for AssetAsBuilt', () => {
      const values = [ 'value1', 'value2' ];
      const result = toGlobalSearchAssetFilter(values, true);
      expect(result).toEqual({
        id: {
          value: [ { value: 'value1', strategy: FilterOperator.GLOBAL }, {
            value: 'value2',
            strategy: FilterOperator.GLOBAL,
          } ],
          operator: 'OR',
        },
        semanticModelId: {
          value: [ { value: 'value1', strategy: FilterOperator.GLOBAL }, {
            value: 'value2',
            strategy: FilterOperator.GLOBAL,
          } ],
          operator: 'OR',
        },
        idShort: {
          value: [ { value: 'value1', strategy: FilterOperator.GLOBAL }, {
            value: 'value2',
            strategy: FilterOperator.GLOBAL,
          } ],
          operator: 'OR',
        },
        customerPartId: {
          value: [ { value: 'value1', strategy: FilterOperator.GLOBAL }, {
            value: 'value2',
            strategy: FilterOperator.GLOBAL,
          } ],
          operator: 'OR',
        },
        manufacturerPartId: {
          value: [ { value: 'value1', strategy: FilterOperator.GLOBAL }, {
            value: 'value2',
            strategy: FilterOperator.GLOBAL,
          } ], operator: 'OR',
        },
        businessPartner: {
          value: [ { value: 'value1', strategy: FilterOperator.GLOBAL }, {
            value: 'value2',
            strategy: FilterOperator.GLOBAL,
          } ],
          operator: 'OR',
        },
      });
    });

    it('should handle object input values correctly', () => {
      const values = { key1: [ 'value1' ], key2: [ 'value2' ] };
      const result = toGlobalSearchAssetFilter(values, false);
      expect(result.id.value.length).toBe(2);
    });
  });

  describe('containsAtLeastOneFilterEntry', () => {
    it('should return true if at least one filter entry is non-empty', () => {
      const filter = { id: { value: [ { value: 'value1', strategy: FilterOperator.EQUAL } ], operator: 'OR' } };
      expect(containsAtLeastOneFilterEntry(filter)).toBeTrue();
    });

    it('should return false if all filter entries are empty', () => {
      const filter = { id: { value: [ { value: '', strategy: FilterOperator.EQUAL } ], operator: 'OR' } };
      expect(containsAtLeastOneFilterEntry(filter)).toBeFalse();
    });
  });

  describe('toGlobalSearchDigitalTwinFilter', () => {
    it('should create digital twin global search filter from array', () => {
      const values = ['aas1', 'asset123'];
      const result = toGlobalSearchDigitalTwinFilter(values);
  
      expect(result).toEqual({
        aasId: {
          value: [
            { value: 'aas1', strategy: FilterOperator.GLOBAL },
            { value: 'asset123', strategy: FilterOperator.GLOBAL }
          ],
          operator: 'OR'
        },
        globalAssetId: {
          value: [
            { value: 'aas1', strategy: FilterOperator.GLOBAL },
            { value: 'asset123', strategy: FilterOperator.GLOBAL }
          ],
          operator: 'OR'
        }
      });
    });
  
    it('should create digital twin global search filter from object', () => {
      const values = { randomKey: ['aasIdX', 'gaidX'] };
      const result = toGlobalSearchDigitalTwinFilter(values);
  
      expect(result.aasId.value.length).toBe(2);
      expect(result.globalAssetId.value[0].value).toBe('aasIdX');
    });
  });
  
  describe('toStructuredDigitalTwinFilter', () => {
    it('should return a structured filter with STARTS_WITH strategy for known non-date keys', () => {
      const values = {
        digitalTwinType: ['instance']
      };
  
      const result = toStructuredDigitalTwinFilter(values);
  
      expect(result.digitalTwinType).toBeDefined();
      expect(result.digitalTwinType.value[0].strategy).toBe(FilterOperator.STARTS_WITH);
      expect(result.digitalTwinType.value[0].value).toBe('instance');
    });
  
    it('should return a structured filter with AT_LOCAL_DATE strategy for date keys', () => {
      const values = {
        aasExpirationDate: ['2025-01-01']
      };
  
      const result = toStructuredDigitalTwinFilter(values);
  
      expect(result.aasExpirationDate).toBeDefined();
      expect(result.aasExpirationDate.value[0].strategy).toBe(FilterOperator.AT_LOCAL_DATE);
      expect(result.aasExpirationDate.value[0].value).toBe('2025-01-01');
    });
  
    it('should ignore keys with empty arrays', () => {
      const values = {
        globalAssetId: []
      };
  
      const result = toStructuredDigitalTwinFilter(values);
  
      expect(result.globalAssetId).toBeUndefined();
    });
  });
});
