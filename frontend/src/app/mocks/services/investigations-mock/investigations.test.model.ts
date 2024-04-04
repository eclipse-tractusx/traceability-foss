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

export const InvestigationIdPrefix = 'id-';

// TODO: rethink this approach
const severities = [ Severity.MINOR, Severity.MAJOR, Severity.CRITICAL, Severity.LIFE_THREATENING ];
export const buildMockInvestigations = (
  statuses: NotificationStatus[],
  channel: 'SENDER' | 'RECEIVER',
): NotificationResponse[] =>
  new Array(101).fill(null).map((_, index) => {
    const status = statuses[index % statuses.length];
    const severity = severities[index % severities.length];
    const numberToString = (i: number) => i.toString().padStart(2, '0');
    const month = (index % 12) + 1;
    const day = (index % 28) + 1;
    // every 10th alert should have an error
    const errorInvestigation = (index + 1) % 10 === 0 ? 'The Services returned an Error while processing this Investigation' : undefined;

    return {
      id: `${ InvestigationIdPrefix }${ index + 1 }`,
      description: `Investigation No ${ index + 1 }`,
      status,
      severity,
      channel,
      createdBy: 'BPN10000000OEM0A',
      createdByName: 'OEM xxxxxxxxxxxxxxx A',
      sendTo: 'BPN20000000OEM0B',
      sendToName: 'OEM xxxxxxxxxxxxxxx B',
      reason: { close: '', accept: '', decline: '' },
      createdDate: `2022-${ numberToString(month) }-${ numberToString(day) }T12:34:12`,
      assetIds: [ MOCK_part_1.id, getRandomAsset().id, getRandomAsset().id, getRandomAsset().id ],
      errorMessage: errorInvestigation,
      title: 'Title',
      type: NotificationTypeResponse.INVESTIGATION,
    };
  });

