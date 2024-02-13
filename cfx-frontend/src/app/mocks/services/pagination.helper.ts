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

import _deepClone from 'lodash-es/cloneDeep';
import { RestRequest } from 'msw';

export type SortDirection = 'asc' | 'desc' | '';

export interface PaginationFilters {
  page: number;
  size: number;
  sortItem: string;
  sortDirection: SortDirection;
}

export const extractPagination = (req: RestRequest): PaginationFilters => {
  const page = parseInt(req.url.searchParams.get('page') ?? '0', 10);
  const size = parseInt(req.url.searchParams.get('size') ?? '5', 10);
  const [sortItem, sortDirection] = (req.url.searchParams.get('sort') ?? ',').split(',');

  return {
    page,
    size,
    sortItem,
    sortDirection,
  } as PaginationFilters;
};

export const applyPagination = (items: unknown[], filters: PaginationFilters) => {
  const currentItems = _deepClone(items);
  const { size, page, sortItem, sortDirection } = filters;
  const offset = filters.page * filters.size;

  const sortAsc = (a, b) => (a === b ? 0 : a < b ? -1 : 1);
  const sortDesc = (a, b) => (a === b ? 0 : a > b ? -1 : 1);

  if (!!sortDirection) {
    const sortFunction = sortDirection === 'asc' ? sortAsc : sortDesc;
    currentItems.sort((partA, partB) => sortFunction(partA[sortItem], partB[sortItem]));
  }

  return {
    content: currentItems.slice(offset, offset + size),
    page: page,
    pageCount: Math.ceil(currentItems.length / size),
    pageSize: size,
    totalItems: currentItems.length,
  };
};
