/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

import { Component, ElementRef, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { View } from '@shared/model/view.model';
import { Pagination } from '@core/model/pagination.model';
import { TableConfig, TableHeaderSort, CreateHeaderFromColumns, TableEventConfig } from '@shared/components/table/table.model';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { DigitalTwinPartFacade } from '../core/digital-twin-part.facade';
import { DigitalTwinPartResponse } from '@page/digital-twin-part/model/digitalTwinPart.model';
import { FormControl } from '@angular/forms';
import { DigitalTwinPartFilter } from '@shared/model/filter.model';
import { toGlobalSearchDigitalTwinFilter, toStructuredDigitalTwinFilter } from '@shared/helper/filter-helper';
import { Router } from '@angular/router';

@Component({
  selector: 'app-digital-twin-part',
  templateUrl: './digital-twin-part.component.html',
  styleUrls: ['./digital-twin-part.component.scss'],
})
export class DigitalTwinPartComponent implements OnInit, OnDestroy {
  digitalTwinPartsView$: Observable<View<Pagination<DigitalTwinPartResponse>>>;
  tableConfig: TableConfig;
  tableType = TableType.DIGITAL_TWIN_PART;
  searchControl = new FormControl('');
  chipItems: string[] = [];
  searchTerms: string[] = [];
  visibleChips: string[] = [];
  hiddenCount = 0;
  @ViewChild('chipContainer', { static: false }) chipContainer!: ElementRef;
  private readonly maxLength = 20;
  currentPage = 0;
  pageSize = 50;
  currentSort: TableHeaderSort[] = [];
  currentFilters: DigitalTwinPartFilter[] = [];

  private readonly destroy$ = new Subject<void>();

  constructor(private readonly facade: DigitalTwinPartFacade, private readonly router: Router
  ) { }

  ngOnInit(): void {
    this.digitalTwinPartsView$ = this.facade.digitalTwinParts$;
    const displayedColumns = ['select', 'aasId', 'globalAssetId', 'bpn', 'digitalTwinType', 'assetExpirationDate', 'menu'];

    this.tableConfig = {
      displayedColumns: displayedColumns,
      header: CreateHeaderFromColumns(displayedColumns, 'digitalTwinPart'),
      sortableColumns: {
        aasId: true,
        globalAssetId: true,
      },
      hasPagination: true,

    };

    this.loadParts();
  }

  onViewDetails(row: Record<string, unknown>): void {
    const part = row as unknown as DigitalTwinPartResponse;
    this.router.navigate(['/admin/digital-twin-part/detail', part.aasId]);
  }

  loadParts(): void {
    this.facade.setDigitalTwinParts(
      this.currentPage,
      this.pageSize,
      this.currentSort,
      this.currentFilters
    );
  }

  onPageChange(newPage: number): void {
    this.currentPage = newPage;
    this.loadParts();
  }

  onSortChange({ page, pageSize, sorting }: TableEventConfig): void {
    this.currentPage = page;
    this.pageSize = pageSize;
    this.currentSort = sorting ? [sorting] : [];
    this.loadParts();
  }


  onFilterChange(filters: Record<string, string[]>) {
    this.currentFilters = [toStructuredDigitalTwinFilter(filters)];
    this.currentPage = 0;
    this.loadParts();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  protected readonly TableType = TableType;

  triggerSearch(): void {
    const rawValue = this.searchControl.value;

    if (!rawValue && !this.chipItems.length) {
      this.currentFilters = [];
      this.loadParts();
      return;
    }

    const regex = /[\p{L}\p{N}:_+\\/=#'-]+/gu;
    const terms = rawValue?.match(regex) ?? [];

    terms.forEach(term => {
      if (!this.searchTerms.includes(term)) {
        this.searchTerms.push(term);
        this.chipItems.push(this.truncateText(term));
      }
    });

    this.currentFilters = [toGlobalSearchDigitalTwinFilter(this.searchTerms)];

    this.loadParts();
    this.searchControl.setValue('');
    this.updateVisibleChips();
  }

  clearInput(): void {
    this.searchControl.setValue('');
    this.chipItems = [];
    this.searchTerms = [];
    this.triggerSearch();
    this.updateVisibleChips();
    this.loadParts();
  }

  remove(item: string): void {
    const originalTerm = this.searchTerms.find(term => this.truncateText(term) === item);
    if (originalTerm) {
      this.searchTerms = this.searchTerms.filter(term => term !== originalTerm);
      this.chipItems = this.chipItems.filter(chip => chip !== item);
      this.triggerSearch();
      this.updateVisibleChips();
    }
  }

  truncateText(text: string): string {
    return text.length > this.maxLength ? text.substring(0, this.maxLength) + '...' : text;
  }

  updateVisibleChips(): void {
    if (!this.chipContainer) return;
    const containerWidth = this.chipContainer.nativeElement.offsetWidth;
    let totalWidth = 0;
    let visibleCount = 0;
    this.chipItems.forEach(() => {
      const chipWidth = 200;
      totalWidth += chipWidth;
      if (totalWidth < containerWidth - 50) visibleCount++;
    });
    this.visibleChips = this.chipItems.slice(0, visibleCount);
    this.hiddenCount = this.chipItems.length - visibleCount;
  }
  onOpenConfigurationDialog(): void {
    this.facade.openConfigurationDialog();
  }

}
