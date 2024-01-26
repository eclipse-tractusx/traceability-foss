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

import { TestBed } from '@angular/core/testing';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { SharedModule } from '@shared/shared.module';
import { renderComponent } from '@tests/test-render.utils';

describe('PaginatorIntlService', () => {
  const instantiatePaginationService = async () => {
    await renderComponent('', {
      imports: [ SharedModule ],
    });

    return TestBed.inject(MatPaginatorIntl);
  };

  it('should set static labels translations', async () => {
    const paginatorIntlService = await instantiatePaginationService();

    expect(paginatorIntlService.firstPageLabel).toEqual('pagination.firstPageLabel');
    expect(paginatorIntlService.itemsPerPageLabel).toEqual('pagination.itemsPerPageLabel');
    expect(paginatorIntlService.nextPageLabel).toEqual('pagination.nextPageLabel');
    expect(paginatorIntlService.previousPageLabel).toEqual('pagination.previousPageLabel');
    expect(paginatorIntlService.lastPageLabel).toEqual('pagination.lastPageLabel');
  });

  describe('getRangeLabel', () => {
    it('should return empty label when page size equals 0', async () => {
      const paginatorIntlService = await instantiatePaginationService();
      const page = 1;
      const pageSize = 0;
      const length = 10;

      expect(paginatorIntlService.getRangeLabel(page, pageSize, length)).toEqual('pagination.emptyRange');
    });

    it('should return pagination info', async () => {
      const paginatorIntlService = await instantiatePaginationService();
      const page = 1;
      const pageSize = 50;
      const length = 10;

      expect(paginatorIntlService.getRangeLabel(page, pageSize, length)).toEqual('pagination.range');
    });
  });
});
