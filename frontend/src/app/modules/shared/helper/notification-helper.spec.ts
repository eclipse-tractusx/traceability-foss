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
import { createNotificationFilterFromDeeplink, getTranslationContext } from '@shared/helper/notification-helper';
import { FilterOperator } from '@shared/model/filter.model';
import { NotificationStatus, NotificationType } from '@shared/model/notification.model';
import { Severity } from '@shared/model/severity.model';

describe('createNotificationFilterFromDeeplink', () => {
  it('should return a valid NotificationFilter when params are correct', () => {
    const params = { deeplink: true, notificationIds: ['id1', 'id2'] };
    const result = createNotificationFilterFromDeeplink(params);
    expect(result).toEqual({
      id: {
        value: [
          { value: 'id1', strategy: FilterOperator.EQUAL },
          { value: 'id2', strategy: FilterOperator.EQUAL }
        ],
        operator: 'OR'
      }
    });
  });

  it('should return undefined if params are incorrect', () => {
    const params = { deeplink: false, notificationIds: [] };
    expect(createNotificationFilterFromDeeplink(params)).toBeUndefined();
  });
});

describe('getTranslationContext', () => {
  it('should return commonAlert for alert notifications', () => {
    const notification = {
      id: '1',
      type: NotificationType.ALERT,
      title: 'Test1',
      status: NotificationStatus.ACCEPTED,
      createdDate: new Date('27.01.2026'),
      description: '',
      createdBy: '',
      assetIds: [],
      sendTo: '',
      severity: Severity.LIFE_THREATENING,
      messages: [],
    };
    expect(getTranslationContext(notification)).toBe('commonAlert');
  });

  it('should return commonInvestigation for other notifications', () => {
    const notification = {
      id: '2',
      type: NotificationType.INVESTIGATION,
      title: 'Test2',
      status: NotificationStatus.SENT,
      createdDate: new Date('27.01.2026'),
      description: '',
      createdBy: '',
      assetIds: [],
      sendTo: '',
      severity: Severity.CRITICAL,
      messages: [],
    };
    expect(getTranslationContext(notification)).toBe('commonInvestigation');
  });
});

