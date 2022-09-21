/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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
import { InvestigationStatus } from '@shared/model/investigations.model';
import { InvestigationsAssembler } from './investigations.assembler';

describe('InvestigationsAssembler', () => {
  describe('assembleInvestigations', () => {
    it('should handle null response', () => {
      const emptyPage = { content: [], page: 0, pageCount: 0, pageSize: 0, totalItems: 0 };
      expect(InvestigationsAssembler.assembleInvestigations(null)).toEqual(emptyPage);
      expect(InvestigationsAssembler.assembleInvestigations(undefined)).toEqual(emptyPage);
    });

    it('should map properly response', () => {
      expect(
        InvestigationsAssembler.assembleInvestigations({
          page: 0,
          pageCount: 1,
          pageSize: 5,
          totalItems: 2,
          content: [
            {
              id: 'test-1',
              description: 'test descr',
              createDate: '2022-07-26T15:09:39.419Z',
              status: InvestigationStatus.SENT,
              createdBy: '',
              parts: [],
            },
            {
              id: 'test-2',
              description: 'test descr',
              createDate: '2022-07-26T15:09:39.419Z',
              status: 'unknown' as unknown as InvestigationStatus,
              createdBy: '',
              parts: [],
            },
          ],
        }),
      ).toEqual({
        page: 0,
        pageCount: 1,
        pageSize: 5,
        totalItems: 2,
        content: [
          {
            id: 'test-1',
            description: 'test descr',
            status: InvestigationStatus.SENT,
            createDate: new CalendarDateModel('2022-07-26T15:09:39.419Z'),
            createdBy: '',
            parts: [],
          },
          {
            id: 'test-2',
            description: 'test descr',
            status: null,
            createDate: new CalendarDateModel('2022-07-26T15:09:39.419Z'),
            createdBy: '',
            parts: [],
          },
        ],
      });
    });
  });
});
