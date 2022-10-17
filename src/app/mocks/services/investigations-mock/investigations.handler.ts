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
import { NotificationStatus } from '@shared/model/notification.model';
import { rest } from 'msw';
import { applyPagination, extractPagination } from '../pagination.helper';
import { buildMockInvestigations, getInvestigationById, InvestigationIdPrefix } from './investigations.model';

export const investigationsHandlers = [
  rest.get(`${environment.apiUrl}/investigations/:investigationId`, (req, res, ctx) => {
    const { investigationId } = req.params;

    const indexFromId = parseInt((investigationId as string).replace('id-', ''), 10);

    const statusCollection = [NotificationStatus.SENT, NotificationStatus.CREATED, NotificationStatus.RECEIVED];
    const randomNotification = buildMockInvestigations([statusCollection[indexFromId]])[0];

    return res(ctx.status(200), ctx.json({ ...randomNotification, id: investigationId }));
  }),

  rest.get(`${environment.apiUrl}/investigations`, (req, res, ctx) => {
    const pagination = extractPagination(req);
    const status = req.url.searchParams.get('status') ?? '';

    const currentStatus = status.split(',') as NotificationStatus[];
    return res(ctx.status(200), ctx.json(applyPagination(buildMockInvestigations(currentStatus), pagination)));
  }),

  rest.post(`${environment.apiUrl}/investigations`, (_, res, ctx) => {
    return res(ctx.status(200), ctx.json({ id: InvestigationIdPrefix + 1 }));
  }),

  rest.put(`${environment.apiUrl}/investigations/:investigationId/status`, (req, res, ctx) => {
    const { investigationId } = req.params;
    const { status } = req.body as Record<string, unknown>;

    const investigation = getInvestigationById(investigationId as string);
    return res(ctx.status(200), ctx.json({ ...investigation, status }));
  }),
];
