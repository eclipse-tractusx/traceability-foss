/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

import { TableHeaderSort } from '@shared/components/table/table.model';
import { DigitalTwinPartsResponse, DigitalTwinPartDetailResponse } from '@page/digital-twin-part/model/digitalTwinPart.model';
import { DigitalTwinPartAssembler } from './digital-twin-part.assembler';
import { DigitalTwinType } from '@page/digital-twin-part/model/digitalTwinType.enum';

describe('DigitalTwinPartAssembler', () => {
  describe('mapSortToApiSort', () => {
    it('should map local key to API key with direction', () => {
      const sort: TableHeaderSort = ['aasId', 'asc'];
      expect(DigitalTwinPartAssembler.mapSortToApiSort(sort)).toBe('aasId,asc');
    });

    it('should return null if sort is null', () => {
      expect(DigitalTwinPartAssembler.mapSortToApiSort(null)).toBeNull();
    });

    it('should return null if direction is missing', () => {
      expect(DigitalTwinPartAssembler.mapSortToApiSort(['aasId', null] as any)).toBeNull();
    });
  });

  describe('assembleParts', () => {
    it('should map response to pagination object', () => {
      const response: DigitalTwinPartsResponse = {
        page: 1,
        pageCount: 10,
        pageSize: 20,
        totalItems: 200,
        content: [{ aasId: '123' }] as any,
      };

      const result = DigitalTwinPartAssembler.assembleParts(response);
      expect(result.page).toBe(1);
      expect(result.content.length).toBe(1);
    });
  });
  describe('assembleDetailView', () => {
    it('should assemble detail view data correctly', () => {
      const detail: DigitalTwinPartDetailResponse = {
        aasId: 'aas123',
        aasTTL: 500,
        nextLookup: new Date(2025, 3, 15, 10, 30, 0), // Month is 0-indexed in JavaScript
        aasExpirationDate: new Date(2025, 3, 15, 9, 0, 0), // Month is 0-indexed in JavaScript
        globalAssetId: 'asset123',
        assetTTL: 300,
        nextSync: new Date(2025, 3, 16, 12, 0, 0), // Month is 0-indexed in JavaScript
        assetExpirationDate: new Date(2025, 3, 20, 8, 0, 0), // Month is 0-indexed in JavaScript
        actor: 'actor1',
        bpn: 'BPN123',
        digitalTwinType: DigitalTwinType.PART_INSTANCE,
      };

      const result = DigitalTwinPartAssembler.assembleDetailView(detail);
      expect(result.registryData.aasId).toBe('aas123');
      expect(result.registryData.actor).toBe('actor1');
      expect(result.irsData.globalAssetId).toBe('asset123');
    });

    it('should return "N/A" for missing TTL and date values', () => {
      const detail: DigitalTwinPartDetailResponse = {
        aasId: 'aas123',
        aasTTL: null,
        nextLookup: null,
        aasExpirationDate: null,
        globalAssetId: 'asset123',
        assetTTL: null,
        nextSync: null,
        assetExpirationDate: null,
        actor: 'actor1',
        bpn: 'BPN123',
        digitalTwinType: DigitalTwinType.PART_TYPE,
      };

      const result = DigitalTwinPartAssembler.assembleDetailView(detail);
      expect(result.registryData.ttl).toBe('N/A');
      expect(result.registryData.nextLookup).toBe('N/A');
      expect(result.irsData.assetTTl).toBe('N/A');
    });
  });

});
