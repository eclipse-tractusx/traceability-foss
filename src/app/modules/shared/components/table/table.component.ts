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

import { SelectionModel } from '@angular/cdk/collections';
import { Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Pagination } from '@core/model/pagination.model';
import { TableConfig, TableEventConfig, TableHeaderSort } from '@shared/components/table/table.model';

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['table.component.scss'],
})
export class TableComponent {
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  @Input() tableConfig: TableConfig;
  @Input() noShadow = false;

  @Input() set data({ page, pageSize, totalItems, content }: Pagination<unknown>) {
    this.totalItems = totalItems;
    this.pageSize = pageSize;
    this.dataSource.data = content;
    this.isDataLoading = false;
    this.pageIndex = page;
  }

  @Output() selected = new EventEmitter<Record<string, unknown>>();
  @Output() configChanged = new EventEmitter<TableEventConfig>();
  @Output() multiSelect = new EventEmitter<unknown[]>();

  public dataSource = new MatTableDataSource<unknown>();
  public selection = new SelectionModel<unknown>(true, []);

  public totalItems: number;
  public pageIndex: number;
  public isDataLoading: boolean;

  private pageSize: number;
  private sorting: TableHeaderSort;

  public areAllRowsSelected(): boolean {
    return this.selection.selected.length === this.dataSource.data.length;
  }

  public toggleAllRows(): void {
    if (this.areAllRowsSelected()) {
      this.selection.clear();
      this.emitMultiSelect();
      return;
    }

    this.selection.select(...this.dataSource.data);
    this.emitMultiSelect();
  }

  public getPaginatorData({ pageIndex, pageSize }: PageEvent): void {
    this.pageIndex = pageIndex;
    this.isDataLoading = true;
    this.configChanged.emit({ page: pageIndex, pageSize: pageSize, sorting: this.sorting });
  }

  public updateSortingOfData({ active, direction }: Sort): void {
    this.selection.clear();
    this.sorting = !direction ? null : ([active, direction] as TableHeaderSort);
    this.isDataLoading = true;
    this.configChanged.emit({ page: 0, pageSize: this.pageSize, sorting: this.sorting });
  }

  public toggleSelection(row: unknown): void {
    this.selection.toggle(row);
    this.emitMultiSelect();
  }

  private emitMultiSelect(): void {
    this.multiSelect.emit(this.selection.selected);
  }
}
