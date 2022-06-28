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

  @Input() set data({ page, pageSize, totalItems, content }: Pagination<unknown>) {
    this.totalItems = totalItems;
    this.pageSize = pageSize;
    this.dataSource.data = content;
    this.isDataLoading = false;
    this.pageIndex = page;
  }

  @Output() selected = new EventEmitter<Record<string, unknown>>();
  @Output() configChanged = new EventEmitter<TableEventConfig>();

  public dataSource: MatTableDataSource<unknown>;
  public totalItems: number;
  public pageIndex: number;
  public isDataLoading: boolean;

  private pageSize: number;
  private sorting: TableHeaderSort;

  constructor() {
    this.dataSource = new MatTableDataSource();
  }

  getPaginatorData({ pageIndex, pageSize }: PageEvent) {
    this.pageIndex = pageIndex;
    this.isDataLoading = true;
    this.configChanged.emit({ page: pageIndex, pageSize: pageSize, sorting: this.sorting });
  }

  updateSortingOfData({ active, direction }: Sort): void {
    this.sorting = !direction ? null : ([active, direction] as TableHeaderSort);
    this.isDataLoading = true;
    this.configChanged.emit({ page: 0, pageSize: this.pageSize, sorting: this.sorting });
  }
}
