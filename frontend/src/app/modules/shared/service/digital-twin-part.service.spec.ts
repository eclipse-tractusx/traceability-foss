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

import { TestBed } from '@angular/core/testing';
import { ApiService } from '@core/api/api.service';
import { of } from 'rxjs';
import { DigitalTwinPartAssembler } from '@shared/assembler/digital-twin-part.assembler';
import {
    DigitalTwinPartResponse,
    DigitalTwinPartsResponse,
    DigitalTwinPartDetailResponse
} from '@page/digital-twin-part/model/digitalTwinPart.model';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { DigitalTwinPartService } from './digitalTwinPart.service';
import { DigitalTwinPartFilter } from '@shared/model/filter.model';

describe('DigitalTwinPartService', () => {
    let service: DigitalTwinPartService;
    let apiServiceSpy: jasmine.SpyObj<ApiService>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                DigitalTwinPartService,
                { provide: ApiService, useValue: jasmine.createSpyObj('ApiService', ['post']) }
            ]
        });

        service = TestBed.inject(DigitalTwinPartService);
        apiServiceSpy = TestBed.inject(ApiService) as jasmine.SpyObj<ApiService>;
    });

    describe('getDigitalTwinParts', () => {
        it('should call API and assemble parts correctly', (done) => {
            const mockResponse: DigitalTwinPartsResponse = {
                page: 0,
                pageCount: 1,
                pageSize: 10,
                totalItems: 1,
                content: [{
                    aasId: '123',
                    aasExpirationDate: new Date(2025, 4, 15, 9, 0, 0),
                    globalAssetId: 'GAID123',
                    assetExpirationDate: new Date(2025, 3, 20, 8, 0, 0), // Month is 0-based
                    bpn: 'BPNL12345',
                    digitalTwinType: 'PART_INSTANCE' as any
                }]
            };

            spyOn(DigitalTwinPartAssembler, 'mapSortToApiSort').and.callFake(([key, dir]) => `${key},${dir}`);
            spyOn(DigitalTwinPartAssembler, 'assembleParts').and.callThrough();

            apiServiceSpy.post.and.returnValue(of(mockResponse));

            const sorting: TableHeaderSort[] = [['aasId', 'asc']];
            const filters: DigitalTwinPartFilter[] = [];

            service.getDigitalTwinParts(0, 10, sorting, filters).subscribe(result => {
                expect(apiServiceSpy.post).toHaveBeenCalledWith(
                    jasmine.stringMatching(/digitalTwinPart$/),
                    jasmine.objectContaining({
                        page: 0,
                        size: 10,
                        sort: ['aasId,asc'],
                        filters: []
                    })
                );
                expect(DigitalTwinPartAssembler.mapSortToApiSort).toHaveBeenCalled();
                expect(DigitalTwinPartAssembler.assembleParts).toHaveBeenCalledWith(mockResponse);
                expect(result.pageSize).toBe(10);
                done();
            });
        });
    });

    describe('getDigitalTwinPartDetail', () => {
        it('should call API with correct aasId and return detail', (done) => {
            const mockDetail: DigitalTwinPartDetailResponse = {
                aasId: 'aas123',
                aasTTL: 123,
                nextLookup: new Date(2025, 3, 15, 10, 0, 0), // Month is 0-based
                aasExpirationDate: new Date(2025, 3, 16, 10, 0, 0), // Month is 0-based
                globalAssetId: 'g123',
                assetTTL: 321,
                nextSync: new Date(2025, 3, 20, 10, 0, 0), // Month is 0-based
                assetExpirationDate: new Date(2025, 3, 22, 10, 0, 0), // Month is 0-based
                actor: 'actorA',
                bpn: 'BPNL12345',
                digitalTwinType: 'PART_INSTANCE' as any
            };

            apiServiceSpy.post.and.returnValue(of(mockDetail));

            service.getDigitalTwinPartDetail('aas123').subscribe(res => {
                expect(apiServiceSpy.post).toHaveBeenCalledWith(
                    jasmine.stringMatching(/digitalTwinPart\/detail$/),
                    { aasId: 'aas123' }
                );
                expect(res.aasId).toBe('aas123');
                expect(res.actor).toBe('actorA');
                done();
            });
        });
    });

    describe('getDistinctFilterValues', () => {
        it('should call API with correct fieldName and startWith', (done) => {
            const mockValues = ['foo', 'bar'];
            apiServiceSpy.post.and.returnValue(of(mockValues));

            service.getDistinctFilterValues('bpn', 'BP').subscribe(values => {
                expect(apiServiceSpy.post).toHaveBeenCalledWith(
                    jasmine.stringMatching(/searchable-values$/),
                    {
                        fieldName: 'bpn',
                        startWith: 'BP',
                        size: 10
                    }
                );
                expect(values).toEqual(mockValues);
                done();
            });
        });
    });
});
