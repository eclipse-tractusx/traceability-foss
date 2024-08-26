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

import { environment } from '@env';
import { rest } from 'msw';
import { applyPagination, extractPagination } from '../../pagination.helper';
import { getAssetAsPlannedById, mockBmwAsPlannedAssets } from './partsAsPlanned.model';

export const partsAsPlannedHandlers = [

  rest.get(`*${ environment.apiUrl }/assets/as-planned`, (req, res, ctx) => {
    const pagination = extractPagination(req);
    return res(ctx.status(200), ctx.json(applyPagination(mockBmwAsPlannedAssets, pagination)));
  }),

  rest.post(`*${ environment.apiUrl }/assets/as-planned/detail-information`, async (req, res, ctx) => {
    const { assetIds } = await req.json();

    const response = assetIds.map(id => getAssetAsPlannedById(id));
    return res(ctx.status(200), ctx.json(response.filter(data => !!data)));
  }),

  rest.get(`*${ environment.apiUrl }/assets/as-planned/:partId`, (req, res, ctx) => {
    const { partId } = req.params;
    const currentAsset = getAssetAsPlannedById(partId as string);
    return res(ctx.status(200), ctx.json(currentAsset));
  }),

  rest.patch(`*${ environment.apiUrl }/assets/as-planned/:partId`, (req, res, ctx) => {
    const { partId } = req.params;
    const currentPart = getAssetAsPlannedById(partId as string);
    return res(ctx.status(200), ctx.json({ ...currentPart, ...req.json() }));
  }),

  rest.get(`*${ environment.apiUrl }/assets/as-planned/:assetId/children/:childId`, (req, res, ctx) => {
    const { childId } = req.params;
    const currentAsset = getAssetAsPlannedById(childId as string);
    return res(ctx.status(200), ctx.json(currentAsset));
  }),
];
