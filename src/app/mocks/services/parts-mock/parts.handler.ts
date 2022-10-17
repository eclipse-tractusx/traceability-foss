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
import { getAssetById, mockAssetsCountriesMap, mockBmwAssets } from './parts.model';
import { mockAssetList, mockAssets } from './parts.test.model';

export const partsHandlers = [
  rest.get(`${environment.apiUrl}/assets`, (req, res, ctx) => {
    const pagination = extractPagination(req);

    return res(ctx.status(200), ctx.json(applyPagination(mockBmwAssets, pagination)));
  }),

  rest.get(`${environment.apiUrl}/assets/countries`, (_req, res, ctx) => {
    return res(ctx.status(200), ctx.json(mockAssetsCountriesMap));
  }),

  rest.get(`${environment.apiUrl}/assets/my`, (req, res, ctx) => {
    const pagination = extractPagination(req);

    return res(ctx.status(200), ctx.json(applyPagination(mockBmwAssets, pagination)));
  }),

  rest.post(`${environment.apiUrl}/assets/detailInformation`, (req, res, ctx) => {
    const { ids } = JSON.parse(req.body as string);
    return res(ctx.status(200), ctx.json(ids.map(id => getAssetById(id))));
  }),

  rest.get(`${environment.apiUrl}/assets/:partId`, (req, res, ctx) => {
    const { partId } = req.params;
    const currentAsset = getAssetById(partId as string);
    return res(ctx.status(200), ctx.json(currentAsset));
  }),

  rest.patch(`${environment.apiUrl}/assets/:partId`, (req, res, ctx) => {
    const { partId } = req.params;
    const currentPart = getAssetById(partId as string);
    return res(ctx.status(200), ctx.json({ ...currentPart, ...(req.body as Record<string, any>) }));
  }),

  rest.get(`${environment.apiUrl}/assets/:assetId/children/:childId`, (req, res, ctx) => {
    const { childId } = req.params;
    const currentAsset = getAssetById(childId as string);
    return res(ctx.status(200), ctx.json(currentAsset));
  }),
];

export const partsHandlersTest = [
  rest.get(`${environment.apiUrl}/assets`, (req, res, ctx) => {
    return res(ctx.status(200), ctx.json(mockAssets));
  }),

  rest.get(`${environment.apiUrl}/assets/countries`, (_req, res, ctx) => {
    return res(ctx.status(200), ctx.json(mockAssetsCountriesMap));
  }),

  rest.get(`${environment.apiUrl}/assets/my`, (req, res, ctx) => {
    return res(ctx.status(200), ctx.json(mockAssets));
  }),

  rest.get(`${environment.apiUrl}/assets/:partId`, (req, res, ctx) => {
    const { partId } = req.params;
    const currentAsset = mockAssetList[partId as string];
    return res(ctx.status(200), ctx.json(currentAsset));
  }),

  rest.patch(`${environment.apiUrl}/assets/:partId`, (req, res, ctx) => {
    const { partId } = req.params;
    const currentPart = mockAssetList[partId as string];
    return res(ctx.status(200), ctx.json({ ...currentPart, ...(req.body as Record<string, any>) }));
  }),

  rest.get(`${environment.apiUrl}/assets/:assetId/children/:childId`, (req, res, ctx) => {
    const { childId } = req.params;
    const currentAsset = mockAssetList[childId as string];
    return res(ctx.status(200), ctx.json(currentAsset));
  }),
];
