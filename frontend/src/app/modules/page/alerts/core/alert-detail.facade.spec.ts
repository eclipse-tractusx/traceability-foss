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
import { TestBed } from '@angular/core/testing';
import { CalendarDateModel } from '@core/model/calendar-date.model';
import { AlertDetailFacade } from '@page/alerts/core/alert-detail.facade';
import { AlertDetailState } from '@page/alerts/core/alert-detail.state';
import { Notification } from '@shared/model/notification.model';
import { Severity } from '@shared/model/severity.model';
import { PartsService } from '@shared/service/parts.service';

describe('AlertDetailFacade', () => {
  let alertDetailFacade: AlertDetailFacade;
  let alertDetailState: AlertDetailState;
  let partsService: jasmine.SpyObj<PartsService>;
  let titleCasePipe: jasmine.SpyObj<TitleCasePipe>;

  let testNotification: Notification = {
    id: 'id-1',
    description: 'Alert No 1',
    createdBy: { name: 'OEM xxxxxxxxxxxxxxx A', bpn: 'BPN10000000OEM0A' },
    sendTo: { name: 'OEM xxxxxxxxxxxxxxx B', bpn: 'BPN20000000OEM0B' },
    reason: { close: '', accept: '', decline: '' },
    isFromSender: true,
    assetIds: [],
    status: null,
    severity: Severity.MINOR,
    createdDate: new CalendarDateModel('2022-05-01T10:34:12.000Z'),
  }

  beforeEach(() => {
    /*
    alertDetailState = jasmine.createSpyObj('AlertDetailState', [
      'getIdsFromPartList',
      'setAlertPartsInformation',
      'getAlertPartsInformation',
      'setSupplierPartsInformation',
      'getSupplierPartsInformation'
    ]);
    */
    alertDetailState = new AlertDetailState();
    partsService = jasmine.createSpyObj('PartsService', [ 'getPartDetailOfIds' ]);
    titleCasePipe = jasmine.createSpyObj('TitleCasePipe', [ 'transform' ]);

    TestBed.configureTestingModule({
      providers: [
        AlertDetailFacade,
        { provide: AlertDetailState, useValue: alertDetailState },
        { provide: PartsService, useValue: partsService },
        { provide: TitleCasePipe, useValue: titleCasePipe }
      ]
    });

    alertDetailFacade = TestBed.inject(AlertDetailFacade);
  });

  it('should create the component facade', () => {
    expect(alertDetailFacade).toBeTruthy();
  });

  it('should set empty data if no asset ids are provided', () => {

    let callSpy = spyOn(alertDetailFacade, 'setAlertPartsInformation');
    let setSpy = spyOnProperty(alertDetailState, 'alertPartsInformation', 'set').and.callThrough();

    alertDetailFacade.setAlertPartsInformation(testNotification);
    expect(callSpy).toHaveBeenCalledWith(testNotification);

  })
});
