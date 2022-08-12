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
import { rest, RestRequest } from 'msw';
import { buildMockInvestigations } from './investigations.model';

interface PaginationFilters {
  page: number;
  size: number;
}

const extractPagination = (req: RestRequest) => {
  const page = parseInt(req.url.searchParams.get('page') ?? '0', 10);
  const size = parseInt(req.url.searchParams.get('size') ?? '5', 10);

  return {
    page,
    size,
  };
};

const applyPagination = (items: unknown[], filters: PaginationFilters) => {
  const offset = filters.page * filters.size;
  return {
    content: items.slice(offset, offset + filters.size),
    page: filters.page,
    pageCount: Math.ceil(items.length / filters.size),
    pageSize: filters.size,
    totalItems: items.length,
  };
};

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
