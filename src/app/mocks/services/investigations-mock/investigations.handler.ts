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

import { environment } from '@env';
import { rest } from 'msw';
import { applyPagination, extractPagination } from '../pagination.helper';
import { buildMockInvestigations } from './investigations.model';

export const investigationsHandlers = [
  rest.get(`${environment.apiUrl}/investigations/received`, (req, res, ctx) => {
    const pagination = extractPagination(req);
    return res(ctx.status(200), ctx.json(applyPagination(buildMockInvestigations(['received']), pagination)));
  }),

  rest.get(`${environment.apiUrl}/investigations/queued-and-requested`, (req, res, ctx) => {
    const pagination = extractPagination(req);
    return res(
      ctx.status(200),
      ctx.json(applyPagination(buildMockInvestigations(['requested', 'queued']), pagination)),
    );
  }),

  rest.post(`${environment.apiUrl}/investigations`, (_, res, ctx) => {
    const investigations = buildMockInvestigations(['queued']);

    const response = { investigationId: investigations[0].id };
    return res(ctx.status(200), ctx.json(response));
  }),
];
