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

import { SelectionModel } from '@angular/cdk/collections';
import { Component, ElementRef, EventEmitter, Input, Output, ViewChild, ViewEncapsulation } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Pagination } from '@core/model/pagination.model';
import { RoleService } from '@core/user/role.service';
import { FilterOperator } from '@page/parts/model/parts.model';
import {
  MenuActionConfig,
  TableConfig,
  TableEventConfig,
  TableFilter,
  FilterMethod,
  TableHeaderSort,
  FilterInfo,
  SortingOptions,
} from '@shared/components/table/table.model';
import { addSelectedValues, clearAllRows, clearCurrentRows, removeSelectedValues } from '@shared/helper/table-helper';

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['table.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class TableComponent {
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild('tableElement', { read: ElementRef }) tableElementRef: ElementRef<HTMLElement>;

  @Input()
  filter = false;

  @Input()
  set tableConfig(tableConfig: TableConfig) {
    if (!tableConfig) {
      return;
    }

    const { menuActionsConfig: menuActions, displayedColumns: dc, columnRoles, hasPagination = true } = tableConfig;
    const displayedColumns = dc.filter(column => this.roleService.hasAccess(columnRoles?.[column] ?? 'user'));

    const viewDetailsMenuAction: MenuActionConfig<unknown> = {
      label: 'actions.viewDetails',
      icon: 'remove_red_eye',
      action: (data: Record<string, unknown>) => this.selected.emit(data),
    };

    const menuActionsConfig = menuActions ? [viewDetailsMenuAction, ...menuActions] : null;
    this._tableConfig = { ...tableConfig, displayedColumns, hasPagination, menuActionsConfig };
  }

  get tableConfig(): TableConfig {
    return this._tableConfig;
  }

  @Input() labelId: string;
  @Input() noShadow = false;
  @Input() showHover = true;

  @Input() selectedPartsInfoLabel: string;
  @Input() selectedPartsActionLabel: string;

  @Input() tableHeader: string;
  @Input() enableScroll: boolean;
  @Input() multiSortList: TableHeaderSort[];

  @Input() set paginationData({ page, pageSize, totalItems, content }: Pagination<unknown>) {
    this.totalItems = totalItems;
    this.pageSize = pageSize;
    this.dataSource.data = content;
    this.isDataLoading = false;
    this.pageIndex = page;
  }

  @Input() set data(content: unknown[]) {
    this.dataSource.data = content;
    this.isDataLoading = false;
  }

  @Input() set deselectTrigger(deselectItem: unknown[]) {
    if (!deselectItem) {
      return;
    }

    this.removeSelectedValues(deselectItem);
    this.emitMultiSelect();
  }

  @Input() set addTrigger(newItem: unknown) {
    if (!newItem) {
      return;
    }

    this.selection.select(newItem);
    this.emitMultiSelect();
  }

  @Output() selected = new EventEmitter<Record<string, unknown>>();
  @Output() configChanged = new EventEmitter<TableEventConfig>();
  @Output() multiSelect = new EventEmitter<any[]>();
  @Output() clickSelectAction = new EventEmitter<void>();

  public readonly dataSource = new MatTableDataSource<unknown>();
  public readonly selection = new SelectionModel<unknown>(true, []);

  public totalItems: number;
  public pageIndex: number;
  public isDataLoading: boolean;
  public selectedRow: Record<string, unknown>;
  public isMenuOpen: boolean;
  public sortingEvent: Record<string, SortingOptions> = {};

  private pageSize: number;
  private sorting: TableHeaderSort;
  private filtering: TableFilter = { filterMethod: FilterMethod.AND };

  private _tableConfig: TableConfig;

  filterFormGroup = new FormGroup({});

  constructor(private readonly roleService: RoleService) {}

  ngOnInit() {
    if (this.tableConfig?.filterConfig?.length > 0) {
      this.setupFilterFormGroup();
    }
    if (this.tableConfig?.sortableColumns) {
      this.setupSortingEvent();
    }
  }

  setupSortingEvent(): void {
    const sortingNames = Object.keys(this.tableConfig.sortableColumns);
    sortingNames.forEach(sortName => (this.sortingEvent[sortName] = SortingOptions.NONE));
  }

  setupFilterFormGroup(): void {
    this.tableConfig.filterConfig.forEach(filter => {
      this.filterFormGroup.addControl(filter.filterKey, new FormControl([]));
    });
  }

  public areAllRowsSelected(): boolean {
    return this.dataSource.data.every(data => this.isSelected(data));
  }

  public clearAllRows(): void {
    clearAllRows(this.selection, this.multiSelect);
  }

  public clearCurrentRows(): void {
    clearCurrentRows(this.selection, this.dataSource.data, this.multiSelect);
  }

  public toggleAllRows(): void {
    this.areAllRowsSelected()
      ? this.removeSelectedValues(this.dataSource.data)
      : this.addSelectedValues(this.dataSource.data);

    this.emitMultiSelect();
  }

  public onPaginationChange({ pageIndex, pageSize }: PageEvent): void {
    this.pageIndex = pageIndex;
    this.isDataLoading = true;
    this.configChanged.emit({ page: pageIndex, pageSize: pageSize, sorting: this.sorting, filtering: this.filtering });
  }

  public updateSortingOfData({ active, direction }: Sort): void {
    this.selection.clear();
    this.emitMultiSelect();
    this.sorting = !direction ? null : ([active, direction] as TableHeaderSort);
    this.isDataLoading = true;
    this.configChanged.emit({ page: 0, pageSize: this.pageSize, sorting: this.sorting, filtering: this.filtering });
  }

  public toggleSelection(row: unknown): void {
    this.isSelected(row) ? this.removeSelectedValues([row]) : this.addSelectedValues([row]);
    this.emitMultiSelect();
  }

  public selectElement(row: Record<string, unknown>) {
    this.selectedRow = this.selectedRow === row ? null : row;

    if (!this.tableConfig.menuActionsConfig) {
      this.selected.emit(row);
    }
  }

  public triggerFilterAdding(filterName: string, isDate: boolean): void {
    //Should the filtering be reset every time? Else remove the following line:
    this.filtering = { filterMethod: FilterMethod.AND };
    let filterAdded: FilterInfo | FilterInfo[];
    const filterSettings = this.tableConfig.filterConfig.filter(filter => filter.filterKey === filterName)[0];

    if (filterSettings.option.length > 0 && !isDate) {
      this.filtering.filterMethod = FilterMethod.OR;
      const filterOptions: FilterInfo[] = [];
      filterSettings.option.forEach(option => {
        if (option.checked) {
          filterOptions.push({
            filterValue: option.value,
            filterOperator: FilterOperator.EQUAL,
          });
        }
      });
      filterAdded = filterOptions;
    } else if (isDate) {
      filterAdded = {
        filterValue: this.filterFormGroup.get(filterName).value,
        filterOperator: FilterOperator.AT_LOCAL_DATE,
      };
    } else {
      filterAdded = {
        filterValue: this.filterFormGroup.get(filterName).value,
        filterOperator: FilterOperator.STARTS_WITH,
      };
    }
    this.filtering[filterName] = filterAdded;
    this.configChanged.emit({ page: 0, pageSize: this.pageSize, sorting: this.sorting, filtering: this.filtering });
  }

  public isMultipleSearch(filter: any): boolean {
    return !(filter.isDate || filter.isTextSearch);
  }

  public sortingEventTrigger(column: string): void {
    if (!this.sortingEvent[column]) {
      return;
    }
    if (this.sortingEvent[column] === SortingOptions.NONE) {
      this.sortingEvent[column] = SortingOptions.ASC;
    } else if (this.sortingEvent[column] === SortingOptions.ASC) {
      this.sortingEvent[column] = SortingOptions.DSC;
    } else {
      this.sortingEvent[column] = SortingOptions.NONE;
    }
  }

  private emitMultiSelect(): void {
    this.multiSelect.emit(this.selection.selected);
  }

  public isSelected(row: unknown): boolean {
    return !!this.selection.selected.find(data => JSON.stringify(data) === JSON.stringify(row));
  }

  private addSelectedValues(newData: unknown[]): void {
    addSelectedValues(this.selection, newData);
  }

  private removeSelectedValues(itemsToRemove: unknown[]): void {
    removeSelectedValues(this.selection, itemsToRemove);
  }
}
