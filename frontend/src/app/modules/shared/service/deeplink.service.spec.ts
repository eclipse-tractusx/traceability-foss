/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

import { ALERT_BASE_ROUTE, INVESTIGATION_BASE_ROUTE } from '@core/known-route';
import { DeeplinkModel } from '@shared/model/deeplink.model';
import { TestBed } from '@angular/core/testing';
import { DeeplinkService } from '@shared/service/deeplink.service';
import { NotificationColumn } from '@shared/model/notification.model';

describe('DeeplinkService', () => {
  let service: DeeplinkService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [ DeeplinkService ],
    });
    service = TestBed.inject(DeeplinkService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should handle received alert deeplink', () => {

    const received = true;
    const tabIndex = 0;
    const data = [ '123' ];
    const route = ALERT_BASE_ROUTE;
    const expected: DeeplinkModel = { received, tabIndex, data, route };

    const column = NotificationColumn.RECEIVED_ALERT;
    const ids = [ '123' ];

    const actual = service.getDeeplink(column, ids);
    expect(actual).toEqual(expected);

  });

  it('should handle received investigation deeplink', () => {

    const received = true;
    const tabIndex = 0;
    const data = [ '123' ];
    const route = INVESTIGATION_BASE_ROUTE;
    const expected: DeeplinkModel = { received, tabIndex, data, route };

    const column = NotificationColumn.RECEIVED_INVESTIGATION;
    const ids = [ '123' ];

    const actual = service.getDeeplink(column, ids);
    expect(actual).toEqual(expected);

  });

  it('should handle sent alert deeplink', () => {

    const received = false;
    const tabIndex = 1;
    const data = [ '123' ];
    const route = ALERT_BASE_ROUTE;
    const expected: DeeplinkModel = { received, tabIndex, data, route };

    const column = NotificationColumn.SENT_ALERT;
    const ids = [ '123' ];

    const actual = service.getDeeplink(column, ids);
    expect(actual).toEqual(expected);

  });

  it('should handle sent investigation deeplink', () => {

    const received = false;
    const tabIndex = 1;
    const data = [ '123' ];
    const route = INVESTIGATION_BASE_ROUTE;
    const expected: DeeplinkModel = { received, tabIndex, data, route };

    const column = NotificationColumn.SENT_INVESTIGATION;
    const ids = [ '123' ];

    const actual = service.getDeeplink(column, ids);
    expect(actual).toEqual(expected);

  });
});
