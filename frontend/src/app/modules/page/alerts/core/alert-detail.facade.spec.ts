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

import { TestBed } from '@angular/core/testing';
import { AlertDetailFacade } from '@page/alerts/core/alert-detail.facade';
import { AlertDetailState } from '@page/alerts/core/alert-detail.state';
import { PartsService } from '@shared/service/parts.service';
import { TitleCasePipe } from '@angular/common';

describe('AlertDetailFacade', () => {
  let componentFacade: AlertDetailFacade;
  let alertDetailStateMock: jasmine.SpyObj<AlertDetailState>;
  let partsService: jasmine.SpyObj<PartsService>;
  let titleCasePipe: jasmine.SpyObj<TitleCasePipe>;

  beforeEach(() => {
    alertDetailStateMock = jasmine.createSpyObj('AlertDetailState', [
      'getIdsFromPartList',
      'setAlertPartsInformation',
      'getAlertPartsInformation',
      'setSupplierPartsInformation',
      'getSupplierPartsInformation'
    ]);
    partsService = jasmine.createSpyObj('PartsService', [ 'getPartDetailOfIds' ]);
    titleCasePipe = jasmine.createSpyObj('TitleCasePipe', [ 'transform' ]);

    TestBed.configureTestingModule({
      providers: [
        AlertDetailFacade,
        { provide: AlertDetailState, useValue: alertDetailStateMock },
        { provide: PartsService, useValue: partsService },
        { provide: TitleCasePipe, useValue: titleCasePipe }
      ]
    });

    componentFacade = TestBed.inject(AlertDetailFacade);
  });

  it('should create the component facade', () => {
    expect(componentFacade).toBeTruthy();
  });
});
