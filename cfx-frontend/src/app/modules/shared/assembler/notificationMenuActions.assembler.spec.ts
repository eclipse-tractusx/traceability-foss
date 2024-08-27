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
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { MockedKeycloakService } from '@core/auth/mocked-keycloak.service';
import { CalendarDateModel } from '@core/model/calendar-date.model';
import { AlertHelperService } from '@page/alerts/core/alert-helper.service';
import { InvestigationHelperService } from '@page/investigations/core/investigation-helper.service';
import { InvestigationsFacade } from '@page/investigations/core/investigations.facade';
import { InvestigationsState } from '@page/investigations/core/investigations.state';
import { NotificationActionHelperService } from '@shared/assembler/notification-action-helper.service';
import { NotificationMenuActionsAssembler } from '@shared/assembler/notificationMenuActions.assembler';
import { NotificationCommonModalComponent } from '@shared/components/notification-common-modal/notification-common-modal.component';
import { Notification, NotificationStatus } from '@shared/model/notification.model';
import { Severity } from '@shared/model/severity.model';
import { CloseNotificationModalComponent } from '@shared/modules/notification/modal/close/close-notification-modal.component';
import { KeycloakService } from 'keycloak-angular';

describe('NotificationMenuActionsAssembler', () => {
  let helperService: AlertHelperService | InvestigationHelperService;
  let notificationCommonModalComponent: NotificationCommonModalComponent;
  let notificationActionHelperService: NotificationActionHelperService

  beforeEach(function () {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        {
          provide: KeycloakService,
          useClass: MockedKeycloakService,
        },
        AlertHelperService,
        InvestigationHelperService,
        NotificationActionHelperService,
        InvestigationsFacade,
        InvestigationsState,
        NotificationCommonModalComponent,
        NotificationMenuActionsAssembler,
        CloseNotificationModalComponent,
      ],
    });
    notificationCommonModalComponent = TestBed.inject(NotificationCommonModalComponent);
    helperService = TestBed.inject(InvestigationHelperService);
    notificationActionHelperService = TestBed.inject(NotificationActionHelperService)
  });

  it('should return menuActions', function () {
    // Arrange
    let showSpy = spyOn(notificationCommonModalComponent, 'show').and.returnValue(undefined);

    const notificationTemplate: Notification = {
      id: 'id-1',
      description: 'Investigation No 1',
      createdBy: '',
      title: 'Title',
      createdByName: '',
      sendTo: '',
      sendToName: '',
      reason: { close: '', accept: '', decline: '' },
      isFromSender: true,
      assetIds: ['MOCK_part_1'],
      status: NotificationStatus.ACKNOWLEDGED,
      severity: Severity.MINOR,
      createdDate: new CalendarDateModel('2022-05-01T10:34:12.000Z'),
    };

    // Act
    let menuActions = NotificationMenuActionsAssembler.getMenuActions(notificationActionHelperService, notificationCommonModalComponent);

    // Assert
    menuActions.map(item => {
      expect(item.action).toBeDefined();
      item.action(notificationTemplate);
      expect(showSpy).toHaveBeenCalled();
      let conditionAction = item.condition(notificationTemplate);
      expect(conditionAction).not.toBe(undefined);
    });
  });


});
