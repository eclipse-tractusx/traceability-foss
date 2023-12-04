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
import { Component, ElementRef, EventEmitter, Input, Output, QueryList, ViewChild, ViewChildren, ViewEncapsulation } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { FormControl, FormGroup } from '@angular/forms';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Pagination } from '@core/model/pagination.model';
import { RoleService } from '@core/user/role.service';
import { TableSettingsService } from '@core/user/table-settings.service';
import {
  CreateHeaderFromColumns,
  MenuActionConfig,
  PartTableType,
  TableConfig,
  TableEventConfig,
  TableHeaderSort,
  TableFilter,
  FilterMethod,
  FilterInfo,
} from '@shared/components/table/table.model';
import { addSelectedValues, clearAllRows, clearCurrentRows, removeSelectedValues } from '@shared/helper/table-helper';
import { TableViewConfig } from '../parts-table/table-view-config.model';
import { TableSettingsComponent } from '../table-settings/table-settings.component';
import { FilterOperator } from '@page/parts/model/parts.model';
import { MultiSelectAutocompleteComponent } from '../multi-select-autocomplete/multi-select-autocomplete.component';

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
  @ViewChildren(MultiSelectAutocompleteComponent) multiSelectAutocompleteComponents: QueryList<MultiSelectAutocompleteComponent>;
  @Input()
  filter = false;

  @Input()
  set tableConfig(tableConfig: TableConfig) {
    if (!tableConfig) {
      return;
    }

    const { menuActionsConfig: menuActions, displayedColumns: dc, columnRoles, hasPagination } = tableConfig;
    const displayedColumns = dc.filter(column => this.roleService.hasAccess(columnRoles?.[column] ?? 'user'));

    const cellRenderers = this._tableConfig?.cellRenderers ?? tableConfig.cellRenderers;

    const viewDetailsLabel = 'actions.viewDetails';

    const viewDetailsMenuAction: MenuActionConfig<unknown> = {
      label: viewDetailsLabel,
      icon: 'remove_red_eye',
      action: (data: Record<string, unknown>) => this.selected.emit(data),
    };

    let menuActionsConfig: MenuActionConfig<unknown>[] = null;

    if (menuActions) {
      if (
        !menuActions.some(action => {
          return action.label === viewDetailsLabel;
        })
      ) {
        menuActionsConfig = [viewDetailsMenuAction, ...menuActions];
      } else {
        menuActionsConfig = menuActions;
      }
    }

    this._tableConfig = { ...tableConfig, cellRenderers, displayedColumns, hasPagination, menuActionsConfig };
  }

  get tableConfig(): TableConfig {
    return this._tableConfig;
  }

  @Input() labelId: string;
  @Input() noShadow = false;
  @Input() showHover = true;
  @Input() tableType: PartTableType;

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
  @Output() filterChange = new EventEmitter<void>();
  @Output() onPaginationPageSizeChange = new EventEmitter<number>();

  public readonly dataSource = new MatTableDataSource<unknown>();
  public readonly selection = new SelectionModel<unknown>(true, []);

  public totalItems: number;
  public pageIndex: number;
  public isDataLoading: boolean;
  public selectedRow: Record<string, unknown>;
  public isMenuOpen: boolean;

  public filterConfiguration: any[];
  public displayedColumns: string[];
  public defaultColumns: string[];
  public filterFormGroup = new FormGroup({});

  private pageSize: number;
  private sorting: TableHeaderSort;
  private filtering: TableFilter = { filterMethod: FilterMethod.AND };
  public filterActive: any = {};

  private _tableConfig: TableConfig;
  private tableViewConfig: TableViewConfig;

  constructor(
    private readonly roleService: RoleService,
    private readonly tableSettingsService: TableSettingsService,
    private dialog: MatDialog,
  ) { }

  ngOnInit() {
    this.initializeTableViewSettings();

    if (this.tableConfig?.filterConfig?.length > 0) {
      this.setupFilter();
    }

    this.tableSettingsService.getEvent().subscribe(() => {
      this.setupTableViewSettings();
    });
    this.setupTableViewSettings();
  }

  setupFilter(): void {
    this.tableConfig.filterConfig.forEach(filter => {
      this.filterActive[filter.filterKey] = false;
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
    this.onPaginationPageSizeChange.emit(pageSize);
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

  private setupTableViewSettings() {
    if (!this.tableType && this.tableViewConfig) {
      this.setupTableConfigurations(
        this.tableViewConfig.displayedColumnsForTable,
        this.tableViewConfig.displayedColumns,
        this.tableViewConfig.sortableColumns,
        this.tableViewConfig.filterConfiguration,
        this.tableViewConfig.filterFormGroup,
      );
      return;
    }

    const tableSettingsList = this.tableSettingsService.getStoredTableSettings();
    // check if there are table settings list
    if (tableSettingsList) {
      // if yes, check if there is a table-setting for this table type
      if (tableSettingsList[this.tableType]) {
        // if yes, get the effective displayedcolumns from the settings and set the tableconfig after it.
        this.setupTableConfigurations(
          tableSettingsList[this.tableType].columnsForTable,
          tableSettingsList[this.tableType].filterColumnsForTable,
          this.tableViewConfig.sortableColumns,
          this.tableViewConfig.filterConfiguration,
          this.tableViewConfig.filterFormGroup,
        );
      } else {
        // if no, create new a table setting for this.tabletype and put it into the list. Additionally, intitialize default table configuration
        tableSettingsList[this.tableType] = this.createSettingsList();
        this.tableSettingsService.storeTableSettings(this.tableType, tableSettingsList);
        this.setupTableConfigurations(
          this.tableViewConfig.displayedColumnsForTable,
          this.tableViewConfig.displayedColumns,
          this.tableViewConfig.sortableColumns,
          this.tableViewConfig.filterConfiguration,
          this.tableViewConfig.filterFormGroup,
        );
      }
    } else {
      // if no, create new list and a settings entry for this.tabletype with default values and set correspondingly the tableconfig
      const newTableSettingsList = {
        [this.tableType]: {
          columnsForDialog: this.tableViewConfig.displayedColumnsForTable,
          columnSettingsOptions: this.getDefaultColumnVisibilityMap(),
          columnsForTable: this.tableViewConfig.displayedColumnsForTable,
          filterColumnsForTable: this.tableViewConfig.displayedColumns,
        },
      };
      this.tableSettingsService.storeTableSettings(this.tableType, newTableSettingsList);
      this.setupTableConfigurations(
        this.tableViewConfig.displayedColumnsForTable,
        this.tableViewConfig.displayedColumns,
        this.tableViewConfig.sortableColumns,
        this.tableViewConfig.filterConfiguration,
        this.tableViewConfig.filterFormGroup,
      );
    }
  }

  private createSettingsList(): any {
    return {
      columnsForDialog: this.tableViewConfig.displayedColumnsForTable,
      columnSettingsOptions: this.getDefaultColumnVisibilityMap(),
      columnsForTable: this.tableViewConfig.displayedColumnsForTable,
      filterColumnsForTable: this.tableViewConfig.displayedColumns,
    };
  }

  private setupTableConfigurations(
    displayedColumnsForTable: string[],
    displayedColumns: string[],
    sortableColumns: Record<string, boolean>,
    filterConfiguration: any[],
    filterFormGroup: any,
  ): any {
    const headerKey = 'table.column';

    this.tableConfig = {
      hasPagination: this.tableConfig.hasPagination,
      filterConfig: this.tableConfig.filterConfig,
      menuActionsConfig: this.tableConfig.menuActionsConfig,
      displayedColumns: displayedColumnsForTable,
      header: CreateHeaderFromColumns(displayedColumnsForTable, headerKey),
      sortableColumns: sortableColumns,
    };

    this.filterConfiguration = filterConfiguration;
    this.displayedColumns = displayedColumns;
    for (const controlName in filterFormGroup) {
      // eslint-disable-next-line no-prototype-builtins
      if (filterFormGroup.hasOwnProperty(controlName)) {
        this.filterFormGroup.addControl(controlName, filterFormGroup[controlName]);
      }
    }
  }

  private getDefaultColumnVisibilityMap(): Map<string, boolean> {
    const initialColumnMap = new Map<string, boolean>();
    for (const column of this.tableViewConfig.displayedColumnsForTable) {
      initialColumnMap.set(column, true);
    }
    return initialColumnMap;
  }

  private initializeTableViewSettings(): void {
    if (this.tableConfig) {
      this.tableViewConfig = {
        displayedColumns: this.tableConfig.displayedColumns,
        displayedColumnsForTable: this.tableConfig.displayedColumns,
        filterConfiguration: this.tableConfig.filterConfig,
        filterFormGroup: undefined,
        sortableColumns: this.tableConfig.sortableColumns,
      };
    }
  }

  public triggerFilterAdding(filterName: string, isDate: boolean): void {
    //Should the filtering be reset every time? then add the following line:
    // this.filtering = { filterMethod: FilterMethod.AND };
    let filterAdded: FilterInfo | FilterInfo[];
    const filterSettings = this.tableConfig.filterConfig.filter(filter => filter.filterKey === filterName)[0];

    if (filterSettings.option.length > 0 && !isDate) {
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
      this.filterActive[filterName] = filterAdded.length > 0;
    } else if (isDate) {
      filterAdded = {
        filterValue: this.filterFormGroup.get(filterName).value,
        filterOperator: FilterOperator.AT_LOCAL_DATE,
      };
      this.filterActive[filterName] = filterAdded.filterValue !== null;
    } else {
      filterAdded = {
        filterValue: this.filterFormGroup.get(filterName).value,
        filterOperator: FilterOperator.STARTS_WITH,
      };
      this.filterActive[filterName] = filterAdded.filterValue !== '';
    }
    this.filtering[filterName] = filterAdded;
    this.filterChange.emit();
    this.configChanged.emit({ page: 0, pageSize: this.pageSize, sorting: this.sorting, filtering: this.filtering });
  }

  public isMultipleSearch(filter: any): boolean {
    return !(filter.isDate || filter.isTextSearch);
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

  openDialog(): void {
    const config = new MatDialogConfig();
    config.autoFocus = false;
    config.data = {
      title: 'table.tableSettings.title',
      panelClass: 'custom',
      tableType: this.tableType,
      defaultColumns: this.tableViewConfig.displayedColumnsForTable,
      defaultFilterColumns: this.tableViewConfig.displayedColumns,
    };
    this.dialog.open(TableSettingsComponent, config);
  }

  public resetFilter(): void {
    const filterNames = Object.keys(this.filterActive);
    for (const filterName of filterNames) {
      this.filterActive[filterName] = false;
    }
    this.filtering = { filterMethod: FilterMethod.AND };
  }
}
