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
import { createDeeplinkNotificationFilter } from '@shared/helper/notification-helper';


describe('NotificationHelper', () => {
  it('should create received deeplink notification filter', () => {


    const params = {
      deeplink: 'true',
      received: 'true',
      notificationIds: [ '1' ],
    };

    // @ts-ignore
    const result = createDeeplinkNotificationFilter(params);

    expect(result.receivedFilter).toEqual({ notificationIds: [ '1' ] });
    expect(result.sentFilter).toBeNull();
  });
  it('should create sent deeplink notification filter', () => {


    const params = {
      deeplink: 'true',
      received: 'false',
      notificationIds: [ '2' ],
    };

    // @ts-ignore
    const result = createDeeplinkNotificationFilter(params);

    expect(result.sentFilter).toEqual({ notificationIds: [ '2' ] });
    expect(result.receivedFilter).toBeNull();
  });

  it('should not create deeplink notification filter', () => {

    const params = {
      deeplink: 'false',
    };

    // @ts-ignore
    const result = createDeeplinkNotificationFilter(params);

    expect(result.sentFilter).toBeNull();
    expect(result.receivedFilter).toBeNull();
  });


});
