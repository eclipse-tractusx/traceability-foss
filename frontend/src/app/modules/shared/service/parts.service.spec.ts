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

import { PartsService } from "@shared/service/parts.service";
import { HttpClientTestingModule, HttpTestingController } from "@angular/common/http/testing";
import { TestBed } from "@angular/core/testing";
import { ApiService } from "@core/api/api.service";
import { TableHeaderSort } from "@shared/components/table/table.model";
import { Pagination } from "@core/model/pagination.model";
import { AssetAsBuiltFilter, AssetAsDesignedFilter, AssetAsOrderedFilter, AssetAsPlannedFilter, AssetAsRecycledFilter, AssetAsSupportedFilter, Part } from "@page/parts/model/parts.model";
import { environment } from "@env";
import { KeycloakService } from "keycloak-angular";
import { AuthService } from "@core/auth/auth.service";
import { mockAssets } from "../../../mocks/services/parts-mock/partsAsPlanned/partsAsPlanned.test.model";
import { MOCK_part_1 } from "../../../mocks/services/parts-mock/partsAsBuilt/partsAsBuilt.test.model";

describe('PartsService', () => {
    let service: PartsService;
    let httpMock: HttpTestingController;
    let authService: AuthService;
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [PartsService, ApiService, KeycloakService, AuthService],
        });
        service = TestBed.inject(PartsService);
        httpMock = TestBed.inject(HttpTestingController);
        authService = TestBed.inject(AuthService);
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should call the getPartsAsBuilt API and return parts', () => {

        spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

        const page = 0;
        const pageSize = 10;
        const multiSort: TableHeaderSort[] = [
            ['age', 'asc'],
            ['score', 'desc'],
        ];

        service.getPartsAsBuilt(page, pageSize, multiSort).subscribe((parts: Pagination<Part>) => {
            expect(parts).toBeTruthy();
        });

        const expectedUrl = `${environment.apiUrl}/assets/as-built`;

        const req = httpMock.expectOne((request) => {
            return request.url === expectedUrl && request.method === 'GET';
        });

        req.flush(mockAssets);

        httpMock.verify();
    });

    it('should call the getPartById API and return parts', () => {

        spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

        const id = "1";
        service.getPart(id).subscribe((parts: Part) => {
            expect(parts).toBeTruthy();
        });

        const expectedUrl = `${environment.apiUrl}/assets/as-built/` + id;

        const req = httpMock.expectOne((request) => {
            return request.url === expectedUrl && request.method === 'GET';
        });

        req.flush(MOCK_part_1);

        httpMock.verify();
    });

    it('should call the getPartsAsBuilt API and return parts filtered', () => {

        spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

        const page = 0;
        const pageSize = 10;
        const multiSort: TableHeaderSort[] = [
            ['age', 'asc'],
            ['score', 'desc'],
        ];
        const filter = { id: "123" } as AssetAsBuiltFilter;

        service.getPartsAsBuilt(page, pageSize, multiSort, filter).subscribe((parts: Pagination<Part>) => {
            expect(parts).toBeTruthy();
        });

        const expectedUrl = `${environment.apiUrl}/assets/as-built`;

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
            ['age', 'asc'],
            ['score', 'desc'],
        ];

        service.getPartsAsPlanned(page, pageSize, multiSort).subscribe((parts: Pagination<Part>) => {
            expect(parts).toBeTruthy();
        });

        const expectedUrl = `${environment.apiUrl}/assets/as-planned`;

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
            ['age', 'asc'],
            ['score', 'desc'],
        ];
        const filter = { id: "123" } as AssetAsPlannedFilter;

        service.getPartsAsPlanned(page, pageSize, multiSort, filter).subscribe((parts: Pagination<Part>) => {
            expect(parts).toBeTruthy();
        });

        const expectedUrl = `${environment.apiUrl}/assets/as-planned`;

        const req = httpMock.expectOne((request) => {
            return request.url === expectedUrl && request.method === 'GET';
        });

        req.flush(mockAssets);

        httpMock.verify();
    });

    it('should call the getPartsAsDesigned API and return parts', () => {

        spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

        const page = 0;
        const pageSize = 10;
        const multiSort: TableHeaderSort[] = [
            ['age', 'asc'],
            ['score', 'desc'],
        ];

        service.getPartsAsDesigned(page, pageSize, multiSort).subscribe((parts: Pagination<Part>) => {
            expect(parts).toBeTruthy();
        });

        const expectedUrl = `${environment.apiUrl}/assets/as-designed`;

        const req = httpMock.expectOne((request) => {
            return request.url === expectedUrl && request.method === 'GET';
        });

        req.flush(mockAssets);

        httpMock.verify();
    });

    it('should call the getPartsAsDesigned API and return parts filtered', () => {

        spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

        const page = 0;
        const pageSize = 10;
        const multiSort: TableHeaderSort[] = [
            ['age', 'asc'],
            ['score', 'desc'],
        ];
        const filter = { id: "123" } as AssetAsDesignedFilter;

        service.getPartsAsDesigned(page, pageSize, multiSort, filter).subscribe((parts: Pagination<Part>) => {
            expect(parts).toBeTruthy();
        });

        const expectedUrl = `${environment.apiUrl}/assets/as-designed`;

        const req = httpMock.expectOne((request) => {
            return request.url === expectedUrl && request.method === 'GET';
        });

        req.flush(mockAssets);

        httpMock.verify();
    });

    it('should call the getPartsAsSupported API and return parts', () => {

        spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

        const page = 0;
        const pageSize = 10;
        const multiSort: TableHeaderSort[] = [
            ['age', 'asc'],
            ['score', 'desc'],
        ];

        service.getPartsAsSupported(page, pageSize, multiSort).subscribe((parts: Pagination<Part>) => {
            expect(parts).toBeTruthy();
        });

        const expectedUrl = `${environment.apiUrl}/assets/as-supported`;

        const req = httpMock.expectOne((request) => {
            return request.url === expectedUrl && request.method === 'GET';
        });

        req.flush(mockAssets);

        httpMock.verify();
    });

    it('should call the getPartsAsSupported API and return parts filtered', () => {

        spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

        const page = 0;
        const pageSize = 10;
        const multiSort: TableHeaderSort[] = [
            ['age', 'asc'],
            ['score', 'desc'],
        ];
        const filter = { id: "123" } as AssetAsSupportedFilter;

        service.getPartsAsSupported(page, pageSize, multiSort, filter).subscribe((parts: Pagination<Part>) => {
            expect(parts).toBeTruthy();
        });

        const expectedUrl = `${environment.apiUrl}/assets/as-supported`;

        const req = httpMock.expectOne((request) => {
            return request.url === expectedUrl && request.method === 'GET';
        });

        req.flush(mockAssets);

        httpMock.verify();
    });

    it('should call the getPartsAsOrdered API and return parts', () => {

        spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

        const page = 0;
        const pageSize = 10;
        const multiSort: TableHeaderSort[] = [
            ['age', 'asc'],
            ['score', 'desc'],
        ];

        service.getPartsAsOrdered(page, pageSize, multiSort).subscribe((parts: Pagination<Part>) => {
            expect(parts).toBeTruthy();
        });

        const expectedUrl = `${environment.apiUrl}/assets/as-ordered`;

        const req = httpMock.expectOne((request) => {
            return request.url === expectedUrl && request.method === 'GET';
        });

        req.flush(mockAssets);

        httpMock.verify();
    });

    it('should call the getPartsAsOrdered API and return parts filtered', () => {

        spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

        const page = 0;
        const pageSize = 10;
        const multiSort: TableHeaderSort[] = [
            ['age', 'asc'],
            ['score', 'desc'],
        ];
        const filter = { id: "123" } as AssetAsOrderedFilter;

        service.getPartsAsOrdered(page, pageSize, multiSort, filter).subscribe((parts: Pagination<Part>) => {
            expect(parts).toBeTruthy();
        });

        const expectedUrl = `${environment.apiUrl}/assets/as-ordered`;

        const req = httpMock.expectOne((request) => {
            return request.url === expectedUrl && request.method === 'GET';
        });

        req.flush(mockAssets);

        httpMock.verify();
    });

    it('should call the getPartsAsRecycled API and return parts', () => {
        spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

        const page = 0;
        const pageSize = 10;
        const multiSort: TableHeaderSort[] = [
            ['age', 'asc'],
            ['score', 'desc'],
        ];

        service.getPartsAsRecycled(page, pageSize, multiSort).subscribe((parts: Pagination<Part>) => {
            expect(parts).toBeTruthy();
        });

        const expectedUrl = `${environment.apiUrl}/assets/as-recycled`;

        const req = httpMock.expectOne((request) => {
            return request.url === expectedUrl && request.method === 'GET';
        });

        req.flush(mockAssets);

        httpMock.verify();
    });

    it('should call the getPartsAsRecycled API and return parts filtered', () => {

        spyOn(authService, 'getBearerToken').and.returnValue('your_mocked_token');

        const page = 0;
        const pageSize = 10;
        const multiSort: TableHeaderSort[] = [
            ['age', 'asc'],
            ['score', 'desc'],
        ];
        const filter = { id: "123" } as AssetAsRecycledFilter;

        service.getPartsAsRecycled(page, pageSize, multiSort, filter).subscribe((parts: Pagination<Part>) => {
            expect(parts).toBeTruthy();
        });

        const expectedUrl = `${environment.apiUrl}/assets/as-recycled`;

        const req = httpMock.expectOne((request) => {
            return request.url === expectedUrl && request.method === 'GET';
        });

        req.flush(mockAssets);

        httpMock.verify();
    });

});
