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
import { NotificationDetailFacade } from '@page/notifications/core/notification-detail.facade';
import { NotificationDetailState } from '@page/notifications/core/notification-detail.state';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { Part } from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { FormatPartlistSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-partlist-semantic-data-model-to-camelcase.pipe';
import { PartsService } from '@shared/service/parts.service';
import { KeycloakService } from 'keycloak-angular';
import { MOCK_part_1 } from '../../../../mocks/services/parts-mock/partsAsPlanned/partsAsPlanned.test.model';

describe('NotificationDetailFacade', () => {
  let notificationDetailFacade: NotificationDetailFacade;
  let notificationDetailState: NotificationDetailState;
  let partService: PartsService;

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
