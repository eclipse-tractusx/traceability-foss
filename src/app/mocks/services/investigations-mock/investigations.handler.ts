/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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

  rest.post(`${environment.apiUrl}/investigations`, (req, res, ctx) => {
    const investigations = buildMockInvestigations(['queued']);
    return res(ctx.status(200), ctx.json(investigations[0]));
  }),
];
