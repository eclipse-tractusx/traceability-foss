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

import { TitleCasePipe } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { CalendarDateModel } from '@core/model/calendar-date.model';
import { NotificationDetailFacade } from '@page/notifications/core/notification-detail.facade';
import { NotificationDetailState } from '@page/notifications/core/notification-detail.state';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { Part } from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { Severity } from '@shared/model/severity.model';
import { FormatPartlistSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-partlist-semantic-data-model-to-camelcase.pipe';
import { PartsService } from '@shared/service/parts.service';
import { KeycloakService } from 'keycloak-angular';
import { BehaviorSubject, of, throwError } from 'rxjs';
import { MOCK_part_1 } from '../../../../mocks/services/parts-mock/partsAsPlanned/partsAsPlanned.test.model';
import { Notification, NotificationStatus, NotificationType } from '@shared/model/notification.model';
describe('NotificationDetailFacade', () => {
  let notificationDetailFacade: NotificationDetailFacade;
  let notificationDetailState: NotificationDetailState;
  let partService: PartsService;
  let formatPipe: FormatPartlistSemanticDataModelToCamelCasePipe;
  let notificationPartsInformationSubject: BehaviorSubject<any>;

  beforeEach(() => {

    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        KeycloakService,
        PartsService,
        TitleCasePipe,
        NotificationDetailFacade,
        NotificationDetailState,
        FormatPartlistSemanticDataModelToCamelCasePipe,
      ],
    });

    notificationDetailFacade = TestBed.inject(NotificationDetailFacade);

    notificationDetailState = TestBed.inject(NotificationDetailState);

    partService = TestBed.inject(PartsService);

    formatPipe = TestBed.inject(FormatPartlistSemanticDataModelToCamelCasePipe);

    notificationPartsInformationSubject = new BehaviorSubject<any>({});
    spyOnProperty(notificationDetailState, 'notificationPartsInformation$', 'get').and.returnValue(notificationPartsInformationSubject.asObservable());

  });

  describe('setNotificationPartsInformation', () => {
    let notification: Notification;
    const createdDate = new CalendarDateModel('2023-01-01T00:00:00Z');
    beforeEach(() => {
      notification = {
        id: '1',
        title: 'Test Notification',
        type: NotificationType.ALERT,
        status: NotificationStatus.CREATED,
        description: 'Test Description',
        createdBy: 'User',
        createdDate: createdDate,
        assetIds: ['asset1', 'asset2'],
        sendTo: 'Receiver',
        severity: Severity.MAJOR,
        messages: []
      };
    });

  it('should set notification parts information correctly when assetIds are present', () => {
    const parts: Part[] = [PartsAssembler.assemblePart(MOCK_part_1, MainAspectType.AS_BUILT)];
    spyOn(partService, 'getPartDetailOfIds').and.returnValue(of(parts));
    spyOn(formatPipe, 'transform');

    notificationDetailFacade.setNotificationPartsInformation(notification);

    expect(partService.getPartDetailOfIds).toHaveBeenCalledWith(notification.assetIds);
    expect(formatPipe.transform).toHaveBeenCalledWith(parts);
  });

  it('should handle error when service fails', () => {
    const error = new Error('Service failed');
    spyOn(partService, 'getPartDetailOfIds').and.returnValue(throwError(error));

    notificationDetailFacade.setNotificationPartsInformation(notification);

    expect(partService.getPartDetailOfIds).toHaveBeenCalledWith(notification.assetIds);
    expect(notificationDetailState.notificationPartsInformation.error).toBe(error);
  });

  it('should set empty data when no assetIds are present', () => {
    notification.assetIds = [];

    notificationDetailFacade.setNotificationPartsInformation(notification);

    expect(notificationDetailState.notificationPartsInformation.data).toEqual([]);
  });
});


  describe('setAndSupplierPartsInformation', () => {
    let notification: Notification;
    const createdDate = new CalendarDateModel('2023-01-01T00:00:00Z');

    beforeEach(() => {
      notification = {
        id: '1',
        title: 'Test Notification',
        type: NotificationType.ALERT,
        status: NotificationStatus.CREATED,
        description: 'Test Description',
        createdBy: 'User',
        createdDate: createdDate,
        assetIds: ['asset1', 'asset2'],
        sendTo: 'Receiver',
        severity: Severity.MAJOR,
        messages: []
      };
      notificationPartsInformationSubject.next({ data: [PartsAssembler.assemblePart(MOCK_part_1, MainAspectType.AS_BUILT)] });
    });

    it('should set supplier parts information correctly when data is present', () => {
      const parts: Part[] = [PartsAssembler.assemblePart(MOCK_part_1, MainAspectType.AS_BUILT)];
      spyOn(partService, 'getPartDetailOfIds').and.returnValue(of(parts));
      spyOn(formatPipe, 'transform');

      notificationDetailFacade.setAndSupplierPartsInformation();

      expect(partService.getPartDetailOfIds).toHaveBeenCalled();
      expect(formatPipe.transform).toHaveBeenCalledWith(parts);
    });

    it('should handle error when service fails', () => {
      const error = new Error('Service failed');
      spyOn(partService, 'getPartDetailOfIds').and.returnValue(throwError(error));

      notificationDetailFacade.setAndSupplierPartsInformation();

      expect(partService.getPartDetailOfIds).toHaveBeenCalled();
      expect(notificationDetailState.supplierPartsInformation.error).toBe(error);
    });

    it('should set empty data when no partIds are present', () => {
      spyOn(partService, 'getPartDetailOfIds').and.returnValue(of([]));

      notificationDetailFacade.setAndSupplierPartsInformation();

      expect(partService.getPartDetailOfIds).toHaveBeenCalled();
      expect(notificationDetailState.supplierPartsInformation.data).toEqual([]);
    });
  });
  [
    {
      method: 'sortNotificationParts',
      prop: 'notificationPartsInformation' as any,
    },
    {
      method: 'sortSupplierParts',
      prop: 'supplierPartsInformation' as any,
    },
  ].forEach(object => {

    describe(`${ object.method }()`, () => {

      let part: Part;

      beforeEach(function() {

        part = PartsAssembler.assemblePart(MOCK_part_1, MainAspectType.AS_BUILT);

        this.spy = spyOn(partService, 'sortParts').and.callFake(() => [ part ]);
      });

      [ [ part ], null, undefined ].forEach((fallacy, index) => {

        it('should pass sortParts', function() {

          spyOnProperty(notificationDetailState, object.prop, 'get').and.returnValue({
            data: fallacy,
          });

          notificationDetailFacade[object.method]('', '');

          index == 0
            ? (() => {
              expect(this.spy).toHaveBeenCalled();
              expect(this.spy).toHaveBeenCalledWith(jasmine.any(Object), jasmine.any(String), jasmine.any(String));
            })()
            : (() => {
              expect(this.spy).not.toHaveBeenCalled();
              expect(this.spy).not.toHaveBeenCalledWith(jasmine.any(Object), jasmine.any(String), jasmine.any(String));
            })();
        });

        it('should set part infos after sort', function() {

          spyOnProperty(notificationDetailState, object.prop, 'get').and.returnValue({
            data: fallacy,
          });

          this.spyPropSet = spyOnProperty(notificationDetailState, object.prop, 'set');

          notificationDetailFacade[object.method]('', '');

          index == 0
            ? (() => {
              expect(this.spyPropSet).toHaveBeenCalledTimes(1);
            })()
            : (() => {
              expect(this.spyPropSet).not.toHaveBeenCalledTimes(1);
            })();
        });
      });
    });
  });
});
