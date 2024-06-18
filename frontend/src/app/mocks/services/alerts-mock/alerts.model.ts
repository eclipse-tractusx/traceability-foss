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

import type { NotificationResponse } from '@shared/model/notification.model';
import { NotificationStatus, NotificationTypeResponse } from '@shared/model/notification.model';
import { Severity } from '@shared/model/severity.model';
import { getRandomAsset } from '../parts-mock/partsAsPlanned/partsAsPlanned.model';
import { MOCK_part_1 } from '../parts-mock/partsAsPlanned/partsAsPlanned.test.model';
import { getRandomIntFromInterval, getRandomText } from '../text-generator.helper';

export const AlertIdPrefix = 'id-';

// TODO: rethink this approach
const severities = [ Severity.MINOR, Severity.MAJOR, Severity.CRITICAL, Severity.LIFE_THREATENING ];

export const buildMockAlerts = (
  statuses: NotificationStatus[],
  channel: 'SENDER' | 'RECEIVER',
): NotificationResponse[] =>
  new Array(101).fill(null).map((_, index) => {
    const status = statuses[index % statuses.length];
    const severity = severities[index % severities.length];

    const numberToString = (i: number) => i.toString().padStart(2, '0');
    const month = getRandomIntFromInterval(1, 12);
    const day = getRandomIntFromInterval(1, 27);

    return {
      id: `${ AlertIdPrefix }${ index + 1 }`,
      title: 'title',
      type: NotificationTypeResponse.ALERT,
      status,
      description: `Alert No ${ index + 1 } ${ getRandomText(getRandomIntFromInterval(15, 500)) }`,
      createdBy: 'BPN10000000OEM0A',
      createdByName: 'OEM xxxxxxxxxxxxxxx A',
      createdDate: `2022-${ numberToString(month) }-${ numberToString(day) }T12:34:12`,
      updatedDate: `2022-${ numberToString(month) }-${ numberToString(day) }T12:34:12`,
      assetIds: [ MOCK_part_1.id, getRandomAsset().id, getRandomAsset().id, getRandomAsset().id ],
      channel,
      sendTo: 'BPN20000000OEM0B',
      sendToName: 'OEM xxxxxxxxxxxxxxx B',
      severity,
      targetDate: `2022-${ numberToString(month) }-${ numberToString(day + 1) }T12:34:12`,
      messages: [],
    };
  });

const MockEmptyAlert: NotificationResponse = {
  id: `${ AlertIdPrefix }000`,
  title: 'Title',
  type: NotificationTypeResponse.ALERT,
  status: NotificationStatus.CREATED,
  description: `Alert No 000`,
  createdBy: 'BPN10000000OEM0A',
  createdByName: 'OEM xxxxxxxxxxxxxxx A',
  createdDate: `2022-05-01T12:34:12`,
  updatedDate: `2022-05-01T12:34:12`,
  assetIds: [ getRandomAsset().id ],
  channel: 'SENDER',
  sendTo: 'BPN20000000OEM0B',
  sendToName: 'OEM xxxxxxxxxxxxxxx B',
  severity: Severity.MINOR,
  targetDate: `2022-02-01T12:34:12`,
  messages: [],
};

export const getAlertById = (id: string) => {
  return [].find(alert => alert.id === id) || { ...MockEmptyAlert, id };
};
