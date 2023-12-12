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

  constructor(@Inject(I18NEXT_SERVICE) private readonly i18NextService: ITranslationService) {
    // unfortunately BehaviorSubject<string | null> cannot be automatically cast to Subject<void>
    // which is required by MatPaginatorIntl
    const languageChanged = i18NextService.events.languageChanged as unknown as Subject<void>;

    languageChanged.subscribe(() => this.setLabels());

    this.setLabels();
    this.changes = languageChanged;
  }

  public getRangeLabel(page: number, pageSize: number, length: number): string {
    if (length == 0 || pageSize == 0) {
      return this.i18NextService.t('pagination.emptyRange', { length } as Record<string, number>);
    }
    length = Math.max(length, 0);
    const startIndex = page * pageSize;
    return this.i18NextService.t('pagination.range', {
      startIndex: startIndex + 1,
      length,
    } as Record<string, number>);
  }

  private setLabels() {
    this.firstPageLabel = this.i18NextService.t('pagination.firstPageLabel');
    this.itemsPerPageLabel = this.i18NextService.t('pagination.itemsPerPageLabel');
    this.lastPageLabel = this.i18NextService.t('pagination.lastPageLabel');
    this.nextPageLabel = this.i18NextService.t('pagination.nextPageLabel');
    this.previousPageLabel = this.i18NextService.t('pagination.previousPageLabel');
  }
}
