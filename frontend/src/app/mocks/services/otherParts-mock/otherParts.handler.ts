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
import { applyPagination, extractPagination } from '../pagination.helper';
import { customerPartsAsPlannedModel } from './customerPartsAsPlanned.model';
import { mockCustomerAssets, mockSupplierAssets } from './otherParts.test.model';
import { supplierPartsAsPlannedAssets } from './supplierPartsAsPlanned.model';

export const otherPartsAsBuiltHandlers = [
  rest.get(`*${ environment.apiUrl }/assets/as-built`, (req, res, ctx) => {
    const pagination = extractPagination(req);
    const ownerSearchQuery = req.url.searchParams.get('filter').replace('owner,EQUAL,', '');
    switch (ownerSearchQuery) {
      case 'SUPPLIER,AND':
        return res(ctx.status(200), ctx.json(mockSupplierAssets));

      case 'CUSTOMER,AND':
        return res(ctx.status(200), ctx.json(mockCustomerAssets));
    }

  }),
];

export const otherPartsAsBuiltHandlersTest = [
  rest.get(`*${ environment.apiUrl }/assets/as-built`, (req, res, ctx) => {
    const owner = req.url.searchParams.get('filter').replace('owner,EQUAL,', '');

    switch (owner) {
      case 'SUPPLIER,AND':
        return res(ctx.status(200), ctx.json(mockSupplierAssets));

      case 'CUSTOMER,AND':
        return res(ctx.status(200), ctx.json(mockCustomerAssets));
    }

  }),
];

export const otherPartsAsPlannedHandlers = [
  rest.get(`*${ environment.apiUrl }/assets/as-planned`, (req, res, ctx) => {
    const pagination = extractPagination(req);
    const owner = req.url.searchParams.get('filter').replace('owner,EQUAL,', '');

    switch (owner) {
      case 'SUPPLIER,AND':
        return res(ctx.status(200), ctx.json(applyPagination(supplierPartsAsPlannedAssets, pagination)));

      case 'CUSTOMER,AND':
        return res(ctx.status(200), ctx.json(applyPagination(customerPartsAsPlannedModel, pagination)));
    }

  }),
];

export const otherPartsAsPlannedHandlersTest = [
  rest.get(`*${ environment.apiUrl }/assets/as-planned`, (req, res, ctx) => {
    const pagination = extractPagination(req);
    const owner = req.url.searchParams.get('filter').replace('owner,EQUAL,', '');

    switch (owner) {
      case 'SUPPLIER,AND':
        return res(ctx.status(200), ctx.json(supplierPartsAsPlannedAssets));

      case 'CUSTOMER,AND':
        return res(ctx.status(200), ctx.json(applyPagination(customerPartsAsPlannedModel, pagination)));
    }

  }),
];
