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

import { CalendarDateModel } from '@core/model/calendar-date.model';
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
      assetIds: null,
      description: '',
      createdBy: '',
      createdByName: '',
      sendTo: '',
      sendToName: '',
      reason: { accept: '', close: '', decline: '' },
      isFromSender: false,
      status: null,
      severity: null,
      createdDate: new CalendarDateModel(null),
      targetDate: new CalendarDateModel(null),
      bpn: '',
      type: undefined,
      title: '',
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
            description: 'test descr',
            createdDate: '2022-07-26T15:09:39.419Z',
            targetDate: '2022-06-26T15:09:39.419Z',
            bpn: 'BPNL000000TESTRE',
            status: NotificationStatus.SENT,
            severity: Severity.MINOR,
            channel: 'SENDER',
            createdBy: '',
            createdByName: '',
            sendTo: '',
            sendToName: '',
            reason: { close: '', accept: '', decline: '' },
            assetIds: [],
            errorMessage: '',
            title: 'Title',
            type: NotificationTypeResponse.INVESTIGATION,
          },
          {
            id: 'test-2',
            description: 'test descr',
            createdDate: '2022-07-26T15:09:39.419Z',
            targetDate: '2022-06-26T15:09:39.419Z',
            bpn: 'BPNL000000TESTRE',
            status: 'unknown' as unknown as NotificationStatus,
            severity: Severity.MAJOR,
            createdBy: '',
            createdByName: '',
            sendTo: '',
            sendToName: '',
            reason: { close: '', accept: '', decline: '' },
            channel: 'SENDER',
            assetIds: [],
            title: 'Title',
            type: NotificationTypeResponse.INVESTIGATION,
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
          description: 'test descr',
          status: NotificationStatus.SENT,
          severity: Severity.MINOR,
          createdDate: new CalendarDateModel('2022-07-26T15:09:39.419Z'),
          targetDate: new CalendarDateModel('2022-06-26T15:09:39.419Z'),
          bpn: 'BPNL000000TESTRE',
          createdBy: '',
          createdByName: '',
          sendTo: '',
          sendToName: '',
          reason: { close: '', accept: '', decline: '' },
          isFromSender: true,
          assetIds: [],
          type: NotificationType.INVESTIGATION,
          title: 'Title',
        },
        {
          id: 'test-2',
          description: 'test descr',
          status: null,
          severity: Severity.MAJOR,
          createdDate: new CalendarDateModel('2022-07-26T15:09:39.419Z'),
          targetDate: new CalendarDateModel('2022-06-26T15:09:39.419Z'),
          bpn: 'BPNL000000TESTRE',
          createdBy: '',
          createdByName: '',
          sendTo: '',
          sendToName: '',
          reason: { close: '', accept: '', decline: '' },
          isFromSender: true,
          assetIds: [],
          type: NotificationType.INVESTIGATION,
          title: 'Title',
        },
      ],
    };


    expect(actual)
      .toEqual(response);
  });
});
