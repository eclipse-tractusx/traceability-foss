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
import { NotificationHelperService } from '@page/notifications/core/notification-helper.service';
import { NotificationsFacade } from '@page/notifications/core/notifications.facade';
import { NotificationsState } from '@page/notifications/core/notifications.state';
import { NotificationActionHelperService } from '@shared/assembler/notification-action-helper.service';
import { NotificationMenuActionsAssembler } from '@shared/assembler/notificationMenuActions.assembler';
import { NotificationCommonModalComponent } from '@shared/components/notification-common-modal/notification-common-modal.component';
import { Notification, NotificationStatus, NotificationType } from '@shared/model/notification.model';
import { Severity } from '@shared/model/severity.model';
import { NotificationProcessingService } from '@shared/service/notification-processing.service';
import { KeycloakService } from 'keycloak-angular';

describe('NotificationMenuActionsAssembler', () => {
  let helperService: NotificationHelperService;
  let notificationCommonModalComponent: NotificationCommonModalComponent;
  let notificationActionHelperService: NotificationActionHelperService;
  let notificationProcessingService: NotificationProcessingService;

  beforeEach(function() {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
      ],
      providers: [
        {
          provide: KeycloakService,
          useClass: MockedKeycloakService,
        },
        NotificationHelperService,
        NotificationsFacade,
        NotificationsState,
        NotificationActionHelperService,
        NotificationCommonModalComponent,
        NotificationMenuActionsAssembler,
      ],
    });
    notificationCommonModalComponent = TestBed.inject(NotificationCommonModalComponent);
    helperService = TestBed.inject(NotificationHelperService);
    notificationActionHelperService = TestBed.inject(NotificationActionHelperService);
    notificationProcessingService = TestBed.inject(NotificationProcessingService);
  });

  it('should return menuActions', function() {
    // Arrange
    let showSpy = spyOn(notificationCommonModalComponent, 'show').and.returnValue(undefined);
    let isLoadingSpy = spyOn(notificationProcessingService, 'isInLoadingProcess');

    const notificationTemplate: Notification = {
      id: 'id-1',
      title: 'Title',
      type: NotificationType.INVESTIGATION,
      status: NotificationStatus.ACKNOWLEDGED,
      description: 'Investigation No 1',
      createdBy: '',
      createdByName: '',
      createdDate: new Date('2022-05-01T10:34:12.000Z'),
      assetIds: [ 'MOCK_part_1' ],
      sendTo: '',
      sendToName: '',
      severity: Severity.MINOR,
      messages: [],
      isFromSender: true,
    };

    // Act
    let menuActions = NotificationMenuActionsAssembler.getMenuActions(notificationActionHelperService, notificationCommonModalComponent, notificationProcessingService);

    // Assert
    menuActions.map(item => {
      expect(item.action).toBeDefined();
      item.action(notificationTemplate);
      expect(showSpy).toHaveBeenCalled();
      let conditionAction = item.condition(notificationTemplate);
      expect(conditionAction).not.toBe(undefined);
      expect(item.isLoading).toBeDefined();
      item.isLoading(notificationTemplate);
      expect(isLoadingSpy).toHaveBeenCalled();

    });
  });


});
