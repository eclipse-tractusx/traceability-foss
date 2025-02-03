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
import { NotificationStatus, NotificationType } from '@shared/model/notification.model';
import { Severity } from '@shared/model/severity.model';
import { getRandomAsset } from '../parts-mock/partsAsPlanned/partsAsPlanned.model';
import { MOCK_part_1 } from '../parts-mock/partsAsPlanned/partsAsPlanned.test.model';
import { getRandomIntFromInterval, getRandomText } from '../text-generator.helper';

export const InvestigationIdPrefix = 'id-';

// TODO: rethink this approach
const severities = [Severity.MINOR, Severity.MAJOR, Severity.CRITICAL, Severity.LIFE_THREATENING];

export const buildMockInvestigations = (
  statuses: NotificationStatus[],
  channel: 'SENDER' | 'RECEIVER',
): NotificationResponse[] =>
  new Array(101).fill(null).map((_, index) => {
    const status = statuses[index % statuses.length];
    const severity = severities[index % severities.length];
    // every 10th alert should have an error
    const errorInvestigation = (index + 1) % 10 === 0 ? "The Services returned an Error while processing this Investigation" : undefined;

    const close = status === NotificationStatus.CLOSED ? getRandomText(getRandomIntFromInterval(15, 500)) : '';
    const isDeclined = Math.random() >= 0.5;

    const decline =
      status === NotificationStatus.DECLINED || (!!close && isDeclined)
        ? getRandomText(getRandomIntFromInterval(15, 500))
        : '';

    const accept =
      status === NotificationStatus.ACCEPTED || (!!close && !isDeclined)
        ? getRandomText(getRandomIntFromInterval(15, 500))
        : '';

    const numberToString = (i: number) => i.toString().padStart(2, '0');
    const month = getRandomIntFromInterval(1, 12);
    const day = getRandomIntFromInterval(1, 27);

    return {
      id: `${InvestigationIdPrefix}${index + 1}`,
      description: `Investigation No ${index + 1} ${getRandomText(getRandomIntFromInterval(15, 500))}`,
      status,
      severity,
      channel,
      createdBy: 'BPN10000000OEM0A',
      createdByName: 'OEM xxxxxxxxxxxxxxx A',
      sendTo: 'BPN20000000OEM0B',
      sendToName: 'OEM xxxxxxxxxxxxxxx B',
      reason: { close, decline, accept },
      createdDate: `2022-${numberToString(month)}-${numberToString(day)}T12:34:12`,
      targetDate: `2022-${numberToString(month)}-${numberToString(day + 1)}T11:34:12Z`,
      assetIds: [MOCK_part_1.id, getRandomAsset().id, getRandomAsset().id, getRandomAsset().id],
      errorMessage: errorInvestigation,
      notificationType: NotificationType.INVESTIGATION,
      title: 'Title',
    };
  });

const MockEmptyInvestigation: NotificationResponse = {
  id: `${InvestigationIdPrefix}000`,
  description: `Investigation No 000`,
  status: NotificationStatus.CREATED,
  severity: Severity.MINOR,
  createdBy: 'BPN10000000OEM0A',
  createdByName: 'OEM xxxxxxxxxxxxxxx A',
  sendTo: 'BPN20000000OEM0B',
  sendToName: 'OEM xxxxxxxxxxxxxxx B',
  reason: { close: '', decline: '', accept: '' },
  createdDate: `2022-05-01T12:34:12`,
  targetDate: `2022-02-01T12:34:12`,
  assetIds: [getRandomAsset().id],
  channel: 'SENDER',
  title: 'Title',
};

export interface NotificationFilter {
  notificationIds: string[];
}

export const getInvestigationById = (id: string) => {
  return [].find(investigation => investigation.id === id) || { ...MockEmptyInvestigation, id };
};
