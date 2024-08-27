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
import { AlertDetailFacade } from '@page/alerts/core/alert-detail.facade';
import { AlertDetailState } from '@page/alerts/core/alert-detail.state';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { Part } from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { FormatPartlistSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-partlist-semantic-data-model-to-camelcase.pipe';
import { PartsService } from '@shared/service/parts.service';
import { KeycloakService } from 'keycloak-angular';
import { MOCK_part_1 } from '../../../../mocks/services/parts-mock/partsAsPlanned/partsAsPlanned.test.model';

describe('AlertDetailFacade', () => {
  let alertDetailFacade: AlertDetailFacade;
  let alertDetailState: AlertDetailState;
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
        AlertDetailFacade,
        AlertDetailState,
        FormatPartlistSemanticDataModelToCamelCasePipe,
      ],
    });

    alertDetailFacade = TestBed.inject(AlertDetailFacade);

    alertDetailState = TestBed.inject(AlertDetailState);

    partService = TestBed.inject(PartsService);
  });

  [
    {
      method: 'sortNotificationParts',
      prop: 'alertPartsInformation' as any,
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

          spyOnProperty(alertDetailState, object.prop, 'get').and.returnValue({
            data: fallacy,
          });

          alertDetailFacade[object.method]('', '');

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

          spyOnProperty(alertDetailState, object.prop, 'get').and.returnValue({
            data: fallacy,
          });

          this.spyPropSet = spyOnProperty(alertDetailState, object.prop, 'set');

          alertDetailFacade[object.method]('', '');

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
