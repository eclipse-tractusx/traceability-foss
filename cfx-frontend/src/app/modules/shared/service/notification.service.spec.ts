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
import {NotificationService} from "@shared/service/notification.service";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";
import {TestBed} from "@angular/core/testing";
import {NotificationStatus} from "@shared/model/notification.model";
import {NotificationChannel} from "@shared/components/multi-select-autocomplete/table-type.model";
import {AuthService} from "@core/auth/auth.service";
import {ApiService} from "@core/api/api.service";
import {KeycloakService} from "keycloak-angular";

describe('NotificationService', () => {
    let service: NotificationService;
    let httpTestingController: HttpTestingController;
    let authService: AuthService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [NotificationService, ApiService, KeycloakService, AuthService],
        });
        service = TestBed.inject(NotificationService);
        httpTestingController = TestBed.inject(HttpTestingController);
        authService = TestBed.inject(AuthService);
    });

    afterEach(() => {
        httpTestingController.verify();
    });

    it('should close a notification', () => {
        const notificationId = '123';
        const reason = 'Test reason';
        const isInvestigation = true;
        spyOn(authService, 'getBearerToken').and.returnValue('testtoken');


        service.closeNotification(notificationId, reason).subscribe();

        const req = httpTestingController.expectOne(`${service.getRequestUrl()}/${notificationId}/close`);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual( '{"reason":"Test reason"}');
        req.flush({});
    });

    it('should approve a notification', () => {
        const notificationId = '123';
        const isInvestigation = true;
        spyOn(authService, 'getBearerToken').and.returnValue('testtoken');


        service.approveNotification(notificationId).subscribe();

        const req = httpTestingController.expectOne(`${service.getRequestUrl()}/${notificationId}/approve`);
        expect(req.request.method).toBe('POST');
        req.flush({});
    });

    it('should cancel a notification', () => {
        const notificationId = '123';
        const isInvestigation = true;
        spyOn(authService, 'getBearerToken').and.returnValue('testtoken');

        service.cancelNotification(notificationId).subscribe();

        const req = httpTestingController.expectOne(`${service.getRequestUrl()}/${notificationId}/cancel`);
        expect(req.request.method).toBe('POST');
        req.flush({});
    });

    it('should update a notification', () => {
        const notificationId = '123';
        const status = NotificationStatus.ACKNOWLEDGED;
        const reason = 'Test reason';
        const isInvestigation = true;
        spyOn(authService, 'getBearerToken').and.returnValue('testtoken');

        service.updateNotification(notificationId, status, reason).subscribe();

        const req = httpTestingController.expectOne(`${service.getRequestUrl()}/${notificationId}/update`);
        expect(req.request.method).toBe('POST');
        expect(req.request.body).toEqual('{"reason":"Test reason","status":"ACKNOWLEDGED"}');
        req.flush({});
    });

    it('should get distinct filter values', () => {
        const channel: NotificationChannel = NotificationChannel.SENDER;
        const fieldNames = 'SomeField';
        const startsWith = 'Test';
        const isInvestigation = true;
        spyOn(authService, 'getBearerToken').and.returnValue('testtoken');

        service.getDistinctFilterValues(channel, fieldNames, startsWith).subscribe();

        const req = httpTestingController.expectOne(
            `${service.getRequestUrl()}/distinctFilterValues?fieldName=SomeField&startWith=Test&size=200&channel=SENDER`
        );
        expect(req.request.method).toBe('GET');
        req.flush({});
    });
});
