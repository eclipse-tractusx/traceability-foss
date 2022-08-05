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

import { Inject, Injectable } from '@angular/core';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { I18NEXT_SERVICE, ITranslationService } from 'angular-i18next';
import { Subject } from 'rxjs';

@Injectable()
export class PaginatorIntlService implements MatPaginatorIntl {
  public firstPageLabel: string;
  public itemsPerPageLabel: string;
  public lastPageLabel: string;
  public nextPageLabel: string;
  public previousPageLabel: string;

  public changes: Subject<void>;

  constructor(@Inject(I18NEXT_SERVICE) private i18NextService: ITranslationService) {
    const languageChanged = i18NextService.events.languageChanged as unknown as Subject<void>;

    languageChanged.subscribe(() => {
      this.setLabels();
    });

    this.setLabels();
    this.changes = languageChanged;
  }

  public getRangeLabel(page: number, pageSize: number, length: number): string {
    if (length == 0 || pageSize == 0) {
      return this.i18NextService.t('pagination.emptyRange', { length });
    }
    length = Math.max(length, 0);
    const startIndex = page * pageSize;
    // If the start index exceeds the list length, do not try and fix the end index to the end.
    const endIndex = startIndex < length ? Math.min(startIndex + pageSize, length) : startIndex + pageSize;
    return this.i18NextService.t('pagination.range', {
      startIndex: startIndex + 1,
      endIndex,
      length,
    });
  }

  private setLabels() {
    this.firstPageLabel = this.i18NextService.t('pagination.firstPageLabel');
    this.itemsPerPageLabel = this.i18NextService.t('pagination.itemsPerPageLabel');
    this.lastPageLabel = this.i18NextService.t('pagination.lastPageLabel');
    this.nextPageLabel = this.i18NextService.t('pagination.nextPageLabel');
    this.previousPageLabel = this.i18NextService.t('pagination.previousPageLabel');
  }
}
