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

import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { DEFAULT_PAGE_SIZE, FIRST_PAGE } from '@core/pagination/pagination.model';
import { bpnRegex } from '@page/admin/presentation/bpn-configuration/bpn-configuration.component';
import { NotificationsFacade } from '@page/notifications/core/notifications.facade';
import { NotificationEditComponent } from '@page/notifications/detail/edit/notification-edit.component';
import { NotificationsModule } from '@page/notifications/notifications.module';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { Owner } from '@page/parts/model/owner.enum';
import { BaseInputHelper } from '@shared/abstraction/baseInput/baseInput.helper';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { toAssetFilter } from '@shared/helper/filter-helper';
import { Notification, NotificationType } from '@shared/model/notification.model';
import { Severity } from '@shared/model/severity.model';
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
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: paramMapValue,
              queryParams: NotificationType.INVESTIGATION,
              url: "https://test.net/inbox/97/edit"
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
      id: 'id-1',
      title: '',
      type: NotificationType.INVESTIGATION,
      status: undefined,
      description: '',
      createdBy: '',
      createdByName: '',
      createdDate: undefined,
      updatedDate: undefined,
      assetIds: [],
      channel: 'SENDER',
      sendTo: '',
      sendToName: '',
      severity: undefined,
      targetDate: null,
      messages: [],
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
      id: 'id-1',
      title: '',
      type: NotificationType.INVESTIGATION,
      status: undefined,
      description: '',
      createdBy: '',
      createdByName: '',
      createdDate: undefined,
      updatedDate: undefined,
      assetIds: [],
      channel: 'SENDER',
      sendTo: '',
      sendToName: '',
      severity: undefined,
      targetDate: null,
      messages: [],
    };

    const notificationsFacadeMock = jasmine.createSpyObj('notificationsFacade', [ 'getNotification' ]);
    notificationsFacadeMock.getNotification.and.returnValue(of({ notification }));

    const { fixture } = await renderNotificationEditComponent(true, notificationsFacadeMock, 'id-1');
    const { componentInstance } = fixture;

    let firstPart = PartsAssembler.assemblePart(MOCK_part_1, MainAspectType.AS_BUILT);
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
      id: 'id-1',
      title: '',
      type: NotificationType.INVESTIGATION,
      status: undefined,
      description: '',
      createdBy: '',
      createdByName: '',
      createdDate: undefined,
      updatedDate: undefined,
      assetIds: [],
      channel: 'SENDER',
      sendTo: '',
      sendToName: '',
      severity: undefined,
      targetDate: null,
      messages: [],
    };

    const notificationsFacadeMock = jasmine.createSpyObj('notificationsFacade', [ 'getNotification' ]);
    notificationsFacadeMock.getNotification.and.returnValue(of({ notification }));

    const { fixture } = await renderNotificationEditComponent(true, notificationsFacadeMock, 'id-1');
    const { componentInstance } = fixture;


    let firstPart = PartsAssembler.assemblePart(MOCK_part_1, MainAspectType.AS_BUILT);
    let secondPart = PartsAssembler.assemblePart(MOCK_part_2, MainAspectType.AS_BUILT);
    let thirdPart = PartsAssembler.assemblePart(MOCK_part_3, MainAspectType.AS_BUILT);
    firstPart.id = 'part2';
    secondPart.id = 'part3';
    thirdPart.id = 'part1';

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
      id: 'id-1',
      title: '',
      type: NotificationType.INVESTIGATION,
      status: undefined,
      description: '',
      createdBy: '',
      createdByName: '',
      createdDate: undefined,
      updatedDate: undefined,
      assetIds: [],
      channel: 'SENDER',
      sendTo: '',
      sendToName: '',
      severity: undefined,
      targetDate: null,
      messages: [],
    };
    const notificationsFacadeMock = jasmine.createSpyObj('notificationsFacade', [ 'getNotification' ]);
    notificationsFacadeMock.getNotification.and.returnValue(of({ notification }));
    const { fixture } = await renderNotificationEditComponent(true, notificationsFacadeMock, 'id-1');
    const { componentInstance } = fixture;

    const assetFilterAffected = {excludeIds: [], ids: ['1'], owner: Owner.SUPPLIER};
    const assetFilterAvailable = {excludeIds: ['1'], ids: [], owner: Owner.SUPPLIER};

    componentInstance.affectedPartIds= ['1'];


    spyOn(componentInstance['ownPartsFacade'], 'setPartsAsBuilt');
    spyOn(componentInstance['ownPartsFacade'], 'setPartsAsBuiltSecond');
    componentInstance['setAvailablePartsBasedOnNotificationType'](notification, assetFilterAvailable);
    componentInstance['setAffectedPartsBasedOnNotificationType'](notification, assetFilterAffected);

    expect(componentInstance['ownPartsFacade'].setPartsAsBuilt).toHaveBeenCalledWith(FIRST_PAGE, DEFAULT_PAGE_SIZE, componentInstance.tableAsBuiltSortList, toAssetFilter(assetFilterAvailable, true));
    expect(componentInstance['ownPartsFacade'].setPartsAsBuiltSecond).toHaveBeenCalledWith(FIRST_PAGE, DEFAULT_PAGE_SIZE, componentInstance.tableAsBuiltSortList, toAssetFilter(assetFilterAffected, true));

  });


  it('should set own parts as built for available part subscription with alerts', async () => {

    const notification: Notification = {
      id: 'id-1',
      title: '',
      type: NotificationType.ALERT,
      status: undefined,
      description: '',
      createdBy: '',
      createdByName: '',
      createdDate: undefined,
      updatedDate: undefined,
      assetIds: [],
      channel: 'SENDER',
      sendTo: '',
      sendToName: '',
      severity: undefined,
      targetDate: null,
      messages: [],
    };

    const notificationsFacadeMock = jasmine.createSpyObj('notificationsFacade', [ 'getNotification' ]);
    notificationsFacadeMock.getNotification.and.returnValue(of({ notification }));

    const { fixture } = await renderNotificationEditComponent(true, notificationsFacadeMock, 'id-1');
    const { componentInstance } = fixture;

    const assetFilterAffected = {excludeIds: [], ids: ['1'], owner: Owner.OWN};
    const assetFilterAvailable = {excludeIds: ['1'], ids: [], owner: Owner.OWN};

    componentInstance.affectedPartIds= ['1'];

    spyOn(componentInstance['ownPartsFacade'], 'setPartsAsBuilt');
    spyOn(componentInstance['ownPartsFacade'], 'setPartsAsBuiltSecond');
    componentInstance['setAvailablePartsBasedOnNotificationType'](notification, assetFilterAvailable);
    componentInstance['setAffectedPartsBasedOnNotificationType'](notification, assetFilterAffected);

    expect(componentInstance['ownPartsFacade'].setPartsAsBuilt).toHaveBeenCalledWith(FIRST_PAGE, DEFAULT_PAGE_SIZE, componentInstance.tableAsBuiltSortList, toAssetFilter(assetFilterAvailable, true));
    expect(componentInstance['ownPartsFacade'].setPartsAsBuiltSecond).toHaveBeenCalledWith(FIRST_PAGE, DEFAULT_PAGE_SIZE, componentInstance.tableAsBuiltSortList, toAssetFilter(assetFilterAffected, true));

  });

  it('should correctly update form', async () => {

    const notification: Notification = {
      id: 'id-1',
      title: '',
      type: NotificationType.ALERT,
      status: undefined,
      description: '',
      createdBy: '',
      createdByName: '',
      createdDate: undefined,
      updatedDate: undefined,
      assetIds: [],
      channel: 'SENDER',
      sendTo: '',
      sendToName: '',
      severity: undefined,
      targetDate: null,
      messages: [],
    };

    const notificationsFacadeMock = jasmine.createSpyObj('notificationsFacade', [ 'getNotification' ]);
    notificationsFacadeMock.getNotification.and.returnValue(of({ notification }));

    const { fixture } = await renderNotificationEditComponent(true, notificationsFacadeMock, 'id-1');
    const { componentInstance } = fixture;


    const formGroup = new FormGroup<any>({
      'title': new FormControl('', [ Validators.maxLength(30), Validators.minLength(0) ]),
      'description': new FormControl('', [ Validators.required, Validators.maxLength(1000), Validators.minLength(15) ]),
      'severity': new FormControl(Severity.MINOR, [ Validators.required ]),
      'targetDate': new FormControl(null),
      'bpn': new FormControl(null, [ Validators.required, BaseInputHelper.getCustomPatternValidator(bpnRegex, 'bpn') ]),
      'type': new FormControl(NotificationType.INVESTIGATION, [ Validators.required ]),
    });


    formGroup.patchValue({
      ...formGroup.value,
      bpn: 'BPNL00000003CML1',
      description: 'This is a test description with min 15 characters',
    });
    formGroup.markAsDirty();
    componentInstance.affectedPartIds = [ MOCK_part_1.id ];
    componentInstance.notificationFormGroupChange(formGroup);
    expect(componentInstance.isSaveButtonDisabled).toEqual(false);
    expect(componentInstance.notificationFormGroup.value['bpn']).toEqual('BPNL00000003CML1');

  });

  it('should call setPartsAsBuiltSecondEmpty and setPartsAsBuilt when affectedPartIds is empty', async() => {

    const notification: Notification = {
      id: 'id-1',
      title: '',
      type: NotificationType.ALERT,
      status: undefined,
      description: '',
      createdBy: '',
      createdByName: '',
      createdDate: undefined,
      updatedDate: undefined,
      assetIds: [],
      channel: 'SENDER',
      sendTo: '',
      sendToName: '',
      severity: undefined,
      targetDate: null,
      messages: [],
    };

    const notificationsFacadeMock = jasmine.createSpyObj('notificationsFacade', [ 'getNotification' ]);
    notificationsFacadeMock.getNotification.and.returnValue(of({ notification }));

    const { fixture } = await renderNotificationEditComponent(true, notificationsFacadeMock, 'id-1');
    const { componentInstance } = fixture;

    spyOn(componentInstance['ownPartsFacade'], 'setPartsAsBuilt');
    spyOn(componentInstance['ownPartsFacade'], 'setPartsAsBuiltSecondEmpty');

    componentInstance.removeAffectedParts();

    expect(componentInstance['ownPartsFacade'].setPartsAsBuiltSecondEmpty).toHaveBeenCalled();
    expect(componentInstance['ownPartsFacade'].setPartsAsBuilt).toHaveBeenCalledWith(
      FIRST_PAGE,
      DEFAULT_PAGE_SIZE,
      componentInstance.tableAsBuiltSortList,
      [{
        excludeIds: [],
        ids: [],
        owner: Owner.SUPPLIER,
      }]
    );
    expect(componentInstance.isSaveButtonDisabled).toBeTrue();
  });
});
