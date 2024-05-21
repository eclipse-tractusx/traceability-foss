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

import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ApiService } from '@core/api/api.service';
import { AuthService } from '@core/auth/auth.service';
import { Pagination } from '@core/model/pagination.model';
import { environment } from '@env';
import { AssetAsBuiltFilter, AssetAsPlannedFilter, Part } from '@page/parts/model/parts.model';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { PartsService } from '@shared/service/parts.service';
import { KeycloakService } from 'keycloak-angular';
import { MOCK_part_1 } from '../../../mocks/services/parts-mock/partsAsBuilt/partsAsBuilt.test.model';
import { mockAssets } from '../../../mocks/services/parts-mock/partsAsPlanned/partsAsPlanned.test.model';

describe('PartsService', () => {
  let service: PartsService;
  let httpMock: HttpTestingController;
  let authService: AuthService;
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ],
      providers: [ PartsService, ApiService, KeycloakService, AuthService ],
    });
    service = TestBed.inject(PartsService);
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call the getPartsAsBuilt API and return parts', () => {

    spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

    const page = 0;
    const pageSize = 10;
    const multiSort: TableHeaderSort[] = [
      [ 'age', 'asc' ],
      [ 'score', 'desc' ],
    ];

    service.getPartsAsBuilt(page, pageSize, multiSort).subscribe((parts: Pagination<Part>) => {
      expect(parts).toBeTruthy();
    });

    const expectedUrl = `${ environment.apiUrl }/assets/as-built`;

    const req = httpMock.expectOne((request) => {
      return request.url === expectedUrl && request.method === 'GET';
    });

    req.flush(mockAssets);

    httpMock.verify();
  });

  it('should call the getPartById API and return parts', () => {

    spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

    const id = '1';
    service.getPart(id).subscribe((parts: Part) => {
      expect(parts).toBeTruthy();
    });

    const expectedUrl = `${ environment.apiUrl }/assets/as-built/` + id;

    const req = httpMock.expectOne((request) => {
      return request.url === expectedUrl && request.method === 'GET';
    });

    req.flush(MOCK_part_1);

  });

  it('should call the getPartsAsBuilt API and return parts filtered', () => {

    spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

    const page = 0;
    const pageSize = 10;
    const multiSort: TableHeaderSort[] = [
      [ 'age', 'asc' ],
      [ 'score', 'desc' ],
    ];
    const filter = { id: '123' } as AssetAsBuiltFilter;

    service.getPartsAsBuilt(page, pageSize, multiSort, filter).subscribe((parts: Pagination<Part>) => {
      expect(parts).toBeTruthy();
    });

    const expectedUrl = `${ environment.apiUrl }/assets/as-built`;

    const req = httpMock.expectOne((request) => {
      return request.url === expectedUrl && request.method === 'GET';
    });

    req.flush(mockAssets);

    httpMock.verify();
  });

  it('should call the getPartsAsPlanned API and return parts', () => {

    spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

    const page = 0;
    const pageSize = 10;
    const multiSort: TableHeaderSort[] = [
      [ 'age', 'asc' ],
      [ 'score', 'desc' ],
    ];

    service.getPartsAsPlanned(page, pageSize, multiSort).subscribe((parts: Pagination<Part>) => {
      expect(parts).toBeTruthy();
    });

    const expectedUrl = `${ environment.apiUrl }/assets/as-planned`;

    const req = httpMock.expectOne((request) => {
      return request.url === expectedUrl && request.method === 'GET';
    });

    req.flush(mockAssets);

    httpMock.verify();
  });

  it('should call the getPartsAsPlanned API and return parts filtered', () => {

    spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

    const page = 0;
    const pageSize = 10;
    const multiSort: TableHeaderSort[] = [
      [ 'age', 'asc' ],
      [ 'score', 'desc' ],
    ];
    const filter = { id: '123' } as AssetAsPlannedFilter;

    service.getPartsAsPlanned(page, pageSize, multiSort, filter).subscribe((parts: Pagination<Part>) => {
      expect(parts).toBeTruthy();
    });

    const expectedUrl = `${ environment.apiUrl }/assets/as-planned`;

    const req = httpMock.expectOne((request) => {
      return request.url === expectedUrl && request.method === 'GET';
    });

    req.flush(mockAssets);

    httpMock.verify();
  });

  it('should request partDetails of as built Id', () => {

    spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

    const asBuiltId = 'MOCK_part_1';

    service.getPartDetailOfIds([ asBuiltId ], true).subscribe((parts) => {
      console.warn(parts);
      expect(parts).toBeTruthy();
    });

    const expectedUrl = `${ environment.apiUrl }/assets/as-built/detail-information`;

    const req = httpMock.expectOne((request) => {
      return request.url === expectedUrl && request.method === 'POST';
    });

    req.flush(mockAssets.content.filter(part => part.id === asBuiltId));

    httpMock.verify();
  });

  it('should request partDetails of as built Id', () => {

    spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

    const asPlannedId = 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa01';

    service.getPartDetailOfIds([ asPlannedId ], false).subscribe((parts) => {
      expect(parts).toBeTruthy();
    });

    const expectedUrl = `${ environment.apiUrl }/assets/as-planned/detail-information`;

    const req = httpMock.expectOne((request) => {
      return request.url === expectedUrl && request.method === 'POST';
    });

    req.flush(mockAssets.content.filter(part => part.id === asPlannedId));

    httpMock.verify();
  });


});
