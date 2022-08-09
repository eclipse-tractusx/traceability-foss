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

import { TestBed } from '@angular/core/testing';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { SharedModule } from '@shared/shared.module';
import { renderComponent } from '@tests/test-render.utils';
import { I18NEXT_SERVICE, ITranslationService } from 'angular-i18next';

describe('PaginatorIntlService', () => {
  const instantinatePaginatorIntlService = async () => {
    await renderComponent('', {
      imports: [SharedModule],
    });

    return TestBed.inject(MatPaginatorIntl);
  };

  it('should set static labels translations', async () => {
    const paginatorIntlService = await instantinatePaginatorIntlService();

    expect(paginatorIntlService.firstPageLabel).toEqual('First page');
    expect(paginatorIntlService.itemsPerPageLabel).toEqual('Items per page:');
    expect(paginatorIntlService.nextPageLabel).toEqual('Next page');
    expect(paginatorIntlService.previousPageLabel).toEqual('Previous page');
    expect(paginatorIntlService.lastPageLabel).toEqual('Last page');
  });

  it('should change labels when language get changed', async () => {
    const paginatorIntlService = await instantinatePaginatorIntlService();

    const i18NextService = TestBed.inject(I18NEXT_SERVICE) as ITranslationService;

    i18NextService.changeLanguage('pl');

    // in test env only EN language registered, so after changing language keys should be returned
    expect(paginatorIntlService.firstPageLabel).toEqual('pagination.firstPageLabel');
    expect(paginatorIntlService.itemsPerPageLabel).toEqual('pagination.itemsPerPageLabel');
    expect(paginatorIntlService.nextPageLabel).toEqual('pagination.nextPageLabel');
    expect(paginatorIntlService.previousPageLabel).toEqual('pagination.previousPageLabel');
    expect(paginatorIntlService.lastPageLabel).toEqual('pagination.lastPageLabel');
  });

  describe('getRangeLabel', () => {
    it('should return empty label when page size equals 0', async () => {
      const paginatorIntlService = await instantinatePaginatorIntlService();
      const page = 1;
      const pageSize = 0;
      const length = 10;

      expect(paginatorIntlService.getRangeLabel(page, pageSize, length)).toEqual('0 of 10');
    });

    it('should return empty label when length equals 0', async () => {
      const paginatorIntlService = await instantinatePaginatorIntlService();
      const page = 1;
      const pageSize = 0;
      const length = 0;

      expect(paginatorIntlService.getRangeLabel(page, pageSize, length)).toEqual('0 of 0');
    });

    it('should return pagination info', async () => {
      const paginatorIntlService = await instantinatePaginatorIntlService();
      const page = 1;
      const pageSize = 5;
      const length = 10;

      expect(paginatorIntlService.getRangeLabel(page, pageSize, length)).toEqual('6 – 10 of 10');
    });

    it('should return pagination info properly when start index out of range', async () => {
      const paginatorIntlService = await instantinatePaginatorIntlService();
      const page = 2;
      const pageSize = 5;
      const length = 10;

      expect(paginatorIntlService.getRangeLabel(page, pageSize, length)).toEqual('11 – 15 of 10');
    });

    it('should handle negative length', async () => {
      const paginatorIntlService = await instantinatePaginatorIntlService();
      const page = 2;
      const pageSize = 5;
      const length = -10;

      expect(paginatorIntlService.getRangeLabel(page, pageSize, length)).toEqual('11 – 15 of 0');
    });
  });
});
