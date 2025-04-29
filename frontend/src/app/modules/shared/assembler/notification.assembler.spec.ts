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

import {
  Notification,
  NotificationResponse,
  NotificationStatus,
  NotificationType,
  NotificationTypeResponse,
} from '@shared/model/notification.model';
import { Severity } from '@shared/model/severity.model';
import { NotificationAssembler } from './notification.assembler';

describe('InvestigationsAssembler', () => {
  it('should handle null response', () => {
    const emptyPage = { content: [], page: 0, pageCount: 0, pageSize: 50, totalItems: 0 };
    expect(NotificationAssembler.assembleNotifications(null)).toEqual(emptyPage);
    expect(NotificationAssembler.assembleNotifications(undefined)).toEqual(emptyPage);
  });

  it('should handle empty values', () => {
    const expected: Notification = {
      id: null,
      title: '',
      type: undefined,
      status: null,
      description: '',
      createdBy: '',
      createdByName: '',
      createdDate: new Date(null),
      updatedDate: new Date(null),
      assetIds: null,
      sendTo: '',
      sendToName: '',
      severity: null,
      targetDate: new Date(null),
      messages: [],
      isFromSender: false,
    };

    expect(NotificationAssembler.assembleNotification({} as NotificationResponse)).toEqual(expected);
  });

  it('should map properly response', () => {

    let actual = NotificationAssembler.assembleNotifications({
        page: 0,
        pageCount: 1,
        pageSize: 5,
        totalItems: 2,
        content: [
          {
            id: 'test-1',
            title: 'Title',
            type: NotificationTypeResponse.INVESTIGATION,
            status: NotificationStatus.SENT,
            description: 'test descr',
            createdBy: 'BPNL000000TESTRE',
            createdByName: '',
            createdDate: '2022-07-26T15:09:39.419Z',
            updatedDate: '2022-07-26T15:09:39.419Z',
            assetIds: [],
            channel: 'SENDER',
            sendTo: '',
            sendToName: '',
            severity: Severity.MINOR,
            targetDate: '2022-07-26T15:09:39.419Z',
            messages: [],

          },
          {
            id: 'test-2',
            title: 'Title',
            type: NotificationTypeResponse.INVESTIGATION,
            status: 'unknown' as unknown as NotificationStatus,
            description: 'test descr',
            createdBy: 'BPNL000000TESTRE',
            createdByName: '',
            createdDate: '2022-07-26T15:09:39.419Z',
            updatedDate: '2022-07-26T15:09:39.419Z',
            assetIds: [],
            channel: 'SENDER',
            sendTo: '',
            sendToName: '',
            severity: Severity.MAJOR,
            targetDate: '2022-07-26T15:09:39.419Z',
            messages: [],
          },
        ],
      },
    );

    const response = {
      page: 0,
      pageCount: 1,
      pageSize: 5,
      totalItems: 2,
      content: [
        {
          id: 'test-1',
          title: 'Title',
          type: NotificationType.INVESTIGATION,
          status: NotificationStatus.SENT,
          description: 'test descr',
          createdBy: 'BPNL000000TESTRE',
          createdByName: '',
          createdDate: new Date('2022-07-26T15:09:39.419Z'),
          updatedDate: new Date('2022-07-26T15:09:39.419Z'),
          assetIds: [],
          sendTo: '',
          sendToName: '',
          severity: Severity.MINOR,
          targetDate: new Date('2022-07-26T15:09:39.419Z'),
          messages: [],
          isFromSender: true,

        },
        {
          id: 'test-2',
          title: 'Title',
          type: NotificationType.INVESTIGATION,
          status: null,
          description: 'test descr',
          createdBy: 'BPNL000000TESTRE',
          createdByName: '',
          createdDate: new Date('2022-07-26T15:09:39.419Z'),
          updatedDate: new Date('2022-07-26T15:09:39.419Z'),
          assetIds: [],
          sendTo: '',
          sendToName: '',
          severity: Severity.MAJOR,
          targetDate: new Date('2022-07-26T15:09:39.419Z'),
          messages: [],
          isFromSender: true,
        },
      ],
    };


    expect(actual)
      .toEqual(response);
  });
});
