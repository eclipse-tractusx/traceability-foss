/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

import { ActivatedRoute } from '@angular/router';
import { DEFAULT_PAGE_SIZE, FIRST_PAGE } from '@core/pagination/pagination.model';
import { NotificationsFacade } from '@page/notifications/core/notifications.facade';
import { NotificationEditComponent } from '@page/notifications/detail/edit/notification-edit.component';
import { NotificationsModule } from '@page/notifications/notifications.module';
import { OtherPartsFacade } from '@page/other-parts/core/other-parts.facade';
import { OtherPartsService } from '@page/other-parts/core/other-parts.service';
import { OtherPartsState } from '@page/other-parts/core/other-parts.state';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { toAssetFilter } from '@shared/helper/filter-helper';
import { Notification, NotificationType } from '@shared/model/notification.model';
import { NotificationService } from '@shared/service/notification.service';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { of } from 'rxjs';
import {
  MOCK_part_1,
  MOCK_part_2,
  MOCK_part_3,
} from '../../../../../mocks/services/parts-mock/partsAsBuilt/partsAsBuilt.test.model';

describe('NotificationEditComponent', () => {

  const renderNotificationEditComponent = async (useParamMap: boolean, mock: any, id?: string) => {

    const paramMapValue = useParamMap ? { get: () => id || 'id-2' } : {
      get: () => {
      },
    };

    return await renderComponent(NotificationEditComponent, {
      imports: [ NotificationsModule ],
      providers: [
        NotificationService,
        OtherPartsFacade,
        OtherPartsService,
        OtherPartsState,
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: paramMapValue,
            },
            queryParams: of({ pageNumber: 0, tabIndex: 0 }),
          },
        },
        {
          provide: NotificationsFacade,
          useValue: mock,
        },

      ],
    });
  };


  it('should render component with form', async () => {

    const notification: Notification = {
      assetIds: [],
      createdBy: '',
      type: NotificationType.INVESTIGATION,
      createdByName: '',
      createdDate: undefined,
      description: '',
      isFromSender: false,
      reason: undefined,
      sendTo: '',
      sendToName: '',
      severity: undefined,
      status: undefined,
      title: '',
      id: 'id-1',
    };

    const notificationsFacadeMock = jasmine.createSpyObj('notificationsFacade', [ 'getNotification' ]);
    notificationsFacadeMock.getNotification.and.returnValue(of({ notification }));

    await renderNotificationEditComponent(true, notificationsFacadeMock, 'id-1');
    const notificationRequestComponent = screen.queryByTestId('app-notification-new-request');
    const affectedParts = screen.queryByTestId('affectedParts');
    expect(notificationRequestComponent).toBeInTheDocument();
    expect(affectedParts).toBeInTheDocument();

  });
  it('should remove affected parts to affectedPartIds and clear temporaryAffectedParts', async () => {

    const notification: Notification = {
      assetIds: [],
      createdBy: '',
      type: NotificationType.INVESTIGATION,
      createdByName: '',
      createdDate: undefined,
      description: '',
      isFromSender: false,
      reason: undefined,
      sendTo: '',
      sendToName: '',
      severity: undefined,
      status: undefined,
      title: '',
      id: 'id-1',
    };

    const notificationsFacadeMock = jasmine.createSpyObj('notificationsFacade', [ 'getNotification' ]);
    notificationsFacadeMock.getNotification.and.returnValue(of({ notification }));

    const { fixture } = await renderNotificationEditComponent(true, notificationsFacadeMock, 'id-1');
    const { componentInstance } = fixture;

    let firstPart  = PartsAssembler.assemblePart(MOCK_part_1, MainAspectType.AS_BUILT);
    let secondPart = PartsAssembler.assemblePart(MOCK_part_2, MainAspectType.AS_BUILT);

    firstPart.id = 'part2';
    secondPart.id = 'part3';

    // Arrange
    componentInstance.temporaryAffectedPartsForRemoval = [ firstPart, secondPart ]; // Initialize temporaryAffectedPartsForRemoval
    componentInstance.affectedPartIds = [ 'part1', 'part2', 'part3', 'part4' ]; // Initialize affectedPartIds


    // Act
    componentInstance.removeAffectedParts(); // Call the method to be tested

    // Assert
    // Check if all parts from temporaryAffectedParts are added to affectedPartIds
    expect(componentInstance.affectedPartIds).toEqual([ 'part1', 'part4' ]);
    // Check if temporaryAffectedParts is cleared
    expect(componentInstance.temporaryAffectedPartsForRemoval).toEqual([]);
  });

  it('should add affected parts to affectedPartIds and clear temporaryAffectedParts', async () => {

    const notification: Notification = {
      assetIds: [],
      createdBy: '',
      type: NotificationType.INVESTIGATION,
      createdByName: '',
      createdDate: undefined,
      description: '',
      isFromSender: false,
      reason: undefined,
      sendTo: '',
      sendToName: '',
      severity: undefined,
      status: undefined,
      title: '',
      id: 'id-1',
    };

    const notificationsFacadeMock = jasmine.createSpyObj('notificationsFacade', [ 'getNotification' ]);
    notificationsFacadeMock.getNotification.and.returnValue(of({ notification }));

    const { fixture } = await renderNotificationEditComponent(true, notificationsFacadeMock, 'id-1');
    const { componentInstance } = fixture;


    let firstPart  = PartsAssembler.assemblePart(MOCK_part_1, MainAspectType.AS_BUILT);
    let secondPart = PartsAssembler.assemblePart(MOCK_part_2, MainAspectType.AS_BUILT);
    let thirdPart = PartsAssembler.assemblePart(MOCK_part_3, MainAspectType.AS_BUILT);
    firstPart.id = 'part2';
    secondPart.id = 'part3';
    thirdPart.id = 'part1'

    // Arrange
    componentInstance.temporaryAffectedParts = [ thirdPart, firstPart, secondPart ]; // Initialize temporaryAffectedParts
    componentInstance.affectedPartIds = [ 'part2' ]; // Initialize affectedPartIds

    // Act
    componentInstance.addAffectedParts(); // Call the method to be tested

    // Assert
    // Check if all parts from temporaryAffectedParts are added to affectedPartIds
    expect(componentInstance.affectedPartIds).toEqual([ 'part2', 'part1', 'part3' ]);
    // Check if temporaryAffectedParts is cleared
    expect(componentInstance.temporaryAffectedParts).toEqual([]);
  });

  it('should set supplier parts for investigation', async () => {

    const notification: Notification = {
      assetIds: [],
      createdBy: '',
      type: NotificationType.INVESTIGATION,
      createdByName: '',
      createdDate: undefined,
      description: '',
      isFromSender: false,
      reason: undefined,
      sendTo: '',
      sendToName: '',
      severity: undefined,
      status: undefined,
      title: '',
      id: 'abc',
    };
    const notificationsFacadeMock = jasmine.createSpyObj('notificationsFacade', [ 'getNotification' ]);
    notificationsFacadeMock.getNotification.and.returnValue(of({ notification }));
    const { fixture } = await renderNotificationEditComponent(true, notificationsFacadeMock, 'id-1');
    const { componentInstance } = fixture;


    const isAvailablePartSubscription = true;
    const assetFilter = {};

    spyOn(componentInstance['partsFacade'], 'setSupplierPartsAsBuilt');
    spyOn(componentInstance['partsFacade'], 'setSupplierPartsAsBuiltSecond');
    componentInstance['setPartsBasedOnNotificationType'](notification, isAvailablePartSubscription, assetFilter);
    componentInstance['setPartsBasedOnNotificationType'](notification, !isAvailablePartSubscription, assetFilter);

    expect(componentInstance['partsFacade'].setSupplierPartsAsBuilt).toHaveBeenCalledWith(FIRST_PAGE, DEFAULT_PAGE_SIZE, componentInstance.tableAsBuiltSortList, toAssetFilter(assetFilter, true));
    expect(componentInstance['partsFacade'].setSupplierPartsAsBuiltSecond).toHaveBeenCalledWith(FIRST_PAGE, DEFAULT_PAGE_SIZE, componentInstance.tableAsBuiltSortList, toAssetFilter(assetFilter, true));

  });


  it('should set own parts as built for available part subscription with alerts', async () => {

    const notification: Notification = {
      assetIds: [],
      createdBy: '',
      type: NotificationType.ALERT,
      createdByName: '',
      createdDate: undefined,
      description: '',
      isFromSender: false,
      reason: undefined,
      sendTo: '',
      sendToName: '',
      severity: undefined,
      status: undefined,
      title: '',
      id: 'abc',
    };

    const notificationsFacadeMock = jasmine.createSpyObj('notificationsFacade', [ 'getNotification' ]);
    notificationsFacadeMock.getNotification.and.returnValue(of({ notification }));

    const { fixture } = await renderNotificationEditComponent(true, notificationsFacadeMock, 'id-1');
    const { componentInstance } = fixture;

    const isAvailablePartSubscription = true;
    const assetFilter = {};

    spyOn(componentInstance['ownPartsFacade'], 'setPartsAsBuilt');
    spyOn(componentInstance['ownPartsFacade'], 'setPartsAsBuiltSecond');
    componentInstance['setPartsBasedOnNotificationType'](notification, isAvailablePartSubscription, assetFilter);
    componentInstance['setPartsBasedOnNotificationType'](notification, !isAvailablePartSubscription, assetFilter);

    expect(componentInstance['ownPartsFacade'].setPartsAsBuilt).toHaveBeenCalledWith(FIRST_PAGE, DEFAULT_PAGE_SIZE, componentInstance.tableAsBuiltSortList, toAssetFilter(assetFilter, true));
    expect(componentInstance['ownPartsFacade'].setPartsAsBuiltSecond).toHaveBeenCalledWith(FIRST_PAGE, DEFAULT_PAGE_SIZE, componentInstance.tableAsBuiltSortList, toAssetFilter(assetFilter, true));

  });

});
