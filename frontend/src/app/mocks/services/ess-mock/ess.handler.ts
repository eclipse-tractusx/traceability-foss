/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
import { getEssById, mockEsss } from './ess.model';
import { mockPartResponses4Ess } from './parts4ess.model';

export const essHandler = [

  rest.get(`*${environment.apiUrl}/assets/as-planned-4ess`, (req, res, ctx) => {
    const pagination = extractPagination(req);

    return res(ctx.status(200), ctx.json(applyPagination(mockPartResponses4Ess, pagination)));
  }),

  /* rest.get(`*${environment.apiUrl}/assets/as-planned-4essnew`, (req, res, ctx) => {
    const pagination = extractPagination(req);

    return res(ctx.status(200), ctx.json(applyPagination(mockPartResponses4EssNew, pagination)));
  }), */

  rest.get(`*${environment.apiUrl}/ess/v`, (req, res, ctx) => {
    const pagination = extractPagination(req);

    return res(ctx.status(200), ctx.json(applyPagination(mockEsss, pagination)));
  }),

  /* rest.post(`*${environment.apiUrl}/ess`, async (req, res, ctx) => {
    const { partIds, bpns, bpnss } = await req.json();
    console.log("ESS HANDLER - POST: " + partIds + "," + bpns + "," + bpnss);
    const response = partIds.map(id => getEssById(id));

    return res(ctx.status(200), ctx.json(response.filter(data => !!data)));
  }), */

];
