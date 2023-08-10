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
import { AlertHelperService } from '@page/alerts/core/alert-helper.service';
import { InvestigationHelperService } from '@page/investigations/core/investigation-helper.service';
import { InvestigationsFacade } from '@page/investigations/core/investigations.facade';
import { InvestigationsState } from '@page/investigations/core/investigations.state';
import { NotificationMenuActionsAssembler } from '@shared/assembler/notificationMenuActions.assembler';
import { NotificationCommonModalComponent } from '@shared/components/notification-common-modal/notification-common-modal.component';
import { KeycloakService } from 'keycloak-angular';

fdescribe('NotificationMenuActionsAssembler', () => {
  let helperService: AlertHelperService | InvestigationHelperService;
  let notificationCommonModalComponent: NotificationCommonModalComponent;

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
        AlertHelperService,
        InvestigationHelperService,
        InvestigationsFacade,
        InvestigationsState,
        NotificationCommonModalComponent,
        NotificationMenuActionsAssembler,
      ],
    });
    notificationCommonModalComponent = TestBed.inject(NotificationCommonModalComponent);
    helperService = TestBed.inject(InvestigationHelperService);
  });

  it('should return menuActions', function() {
    // Arrange
    let methodSpy = spyOn(NotificationMenuActionsAssembler, 'getMenuActions').and.callThrough();

    // Act
    let menuActions = NotificationMenuActionsAssembler.getMenuActions(helperService, notificationCommonModalComponent);

    // Assert
    expect(methodSpy).toHaveBeenCalled();

    menuActions.map(item => {
      expect(item.action).toBeDefined();
    });
  });


});
