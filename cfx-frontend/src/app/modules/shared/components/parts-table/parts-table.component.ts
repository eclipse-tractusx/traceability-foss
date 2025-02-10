/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Output,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Pagination } from '@core/model/pagination.model';
import { MultiSelectAutocompleteComponent } from '@shared/components/multi-select-autocomplete/multi-select-autocomplete.component';
import {
  CreateHeaderFromColumns,
  FilterConfig,
  TableConfig,
  TableEventConfig,
  TableHeaderSort,
} from '@shared/components/table/table.model';
import { addSelectedValues, removeSelectedValues } from '@shared/helper/table-helper';
import { isDateFilter } from '@shared/helper/filter-helper';
import { TableSettingsService } from '@core/user/table-settings.service';
import { TableViewConfig } from './table-view-config.model';
import { FilterConfigOptions } from '@shared/model/filter-config';
import { RoleService } from '@core/user/role.service';
import { Role } from '@core/user/role.model';
import { ToastService } from '../toasts/toast.service';
import { TableSortingUtil } from '@shared/components/table/tableSortingUtil';
import { TableType } from '../multi-select-autocomplete/table-type.model';
import { PartsAsBuiltConfigurationModel } from './parts-as-built-configuration.model';
import { PartsAsBuiltCustomerConfigurationModel } from './parts-as-built-customer-configuration.model';
import { PartsAsBuiltSupplierConfigurationModel } from './parts-as-built-supplier-configuration.model';
import { PartsAsPlannedConfigurationModel } from './parts-as-planned-configuration.model';
import { PartsAsPlannedCustomerConfigurationModel } from './parts-as-planned-customer-configuration.model';
import { PartsAsPlannedSupplierConfigurationModel } from './parts-as-planned-supplier-configuration.model';

@Component({
  selector: 'app-parts-table',
  templateUrl: './parts-table.component.html',
  styleUrls: ['parts-table.component.scss'],
})
export class PartsTableComponent implements OnInit {
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild('tableElement', { read: ElementRef }) tableElementRef: ElementRef<HTMLElement>;
  @ViewChildren(MultiSelectAutocompleteComponent) multiSelectAutocompleteComponents: QueryList<MultiSelectAutocompleteComponent>;
  @Input() multiSelectActive = false;

  @Input() labelId: string;
  @Input() noShadow = false;
  @Input() showHover = true;
  @Input() canClickSelect = true;
  @Input() actionIcon;
  @Input() actionIconAlternative;
  @Input() showActionButton = false;

  @Input() selectedPartsInfoLabel: string;
  @Input() selectedPartsActionLabel: string;

  @Input() tableHeader: string;
  @Input() showHeader = true;
  @Input() multiSortList: TableHeaderSort[];

  @Input() tableType: TableType;

  public tableConfig: TableConfig;

  isSorted = TableSortingUtil.isSorted;

  @ViewChildren(MultiSelectAutocompleteComponent) multiSelectAutocomplete: QueryList<MultiSelectAutocompleteComponent>;

  filterKey = 'Filter';

  @Input() set paginationData({ page, pageSize, totalItems, content, pageCount }: Pagination<unknown>) {
    this.totalItems = totalItems;
    this.pageSize = pageSize;
    this.dataSource.data = content;
    this.isDataLoading = false;
    this.pageIndex = page;
    this.pageCount = pageCount;
    this.onTotalItemsChanged.emit(totalItems);
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
  @Output() filterActivated = new EventEmitter<any>();
  @Output() onPaginationPageSizeChange = new EventEmitter<number>();
  @Output() onTotalItemsChanged = new EventEmitter<number>();

  public readonly dataSource = new MatTableDataSource<unknown>();
  public readonly selection = new SelectionModel<unknown>(true, []);
  public readonly filterConfigOptions = new FilterConfigOptions();
  public semanticDataModelOptions = [...this.filterConfigOptions.filterKeyOptionsAssets.semanticDataModel.option];
  public totalItems: number;
  public pageIndex: number;
  public isDataLoading: boolean;
  public selectedRow: Record<string, unknown>;
  public isMenuOpen: boolean;
  public isSettingsOpen: boolean;
  public displayedFilter: boolean;
  public filterActive: any = {};
  public filterConfiguration: FilterConfig[];
  public displayedColumns: string[];
  public pageSize: number;
  public pageCount: number;

  filterFormGroup = new FormGroup({});

  public isDateElement(key: string) {
    return isDateFilter(key);
  }
  public isMultipleSearch(filter: any): boolean {
    return !(filter.isDate || filter.isTextSearch);
  }

  private sorting: TableHeaderSort;

  protected readonly Role = Role;

  constructor(private readonly tableSettingsService: TableSettingsService,
    private readonly roleService: RoleService,
    private toastService: ToastService,
  ) {

    //TODO: Need to update role based features
    // if (this.roleService.hasAccess([Role.USER])) {
    //   const selectColumn = 'select';
    //   this.displayedColumnsAsBuiltSupplierForTable.splice(0, 0, selectColumn);
    //   this.displayedColumnsAsBuiltForTable.splice(0, 0, selectColumn);
    // }
  }

  public defaultColumns: string[];

  public tableViewConfig: TableViewConfig;

  public onFilterValueChanged(formValues: { filterKey: string, values: any[] }) {
    const { filterKey, values } = formValues;
    this.filterFormGroup.controls[filterKey].patchValue(values);
    this.filterActivated.emit(this.filterFormGroup.value);
  }

  ngOnInit() {
    this.initializeTableViewSettings();
    this.tableSettingsService.getEvent().subscribe(() => {
      this.setupTableViewSettings();
    });
    this.setupTableViewSettings();
  }

  private setupTableViewSettings() {

    if (this.tableSettingsService.storedTableSettingsInvalid(this.tableViewConfig, this.tableType)) {
      this.toastService.warning('table.tableSettings.invalidTitle', 'table.tableSettings.invalid', 10000);
    }
    const tableSettingsList = this.tableSettingsService.getStoredTableSettings();
    // check if there are table settings list
    if (tableSettingsList) {
      // if yes, check if there is a table-setting for this table type
      if (tableSettingsList[this.tableType]) {
        // if yes, get the effective displayedcolumns from the settings and set the tableconfig after it.
        this.setupTableConfigurations(tableSettingsList[this.tableType].columnsForTable, tableSettingsList[this.tableType].filterColumnsForTable, this.tableViewConfig.sortableColumns, this.tableViewConfig.displayFilterColumnMappings, this.tableViewConfig.filterFormGroup);
      } else {
        // if no, create new a table setting for this.tabletype and put it into the list. Additionally, intitialize default table configuration
        tableSettingsList[this.tableType] = {
          columnsForDialog: this.tableViewConfig.displayedColumns,
          columnSettingsOptions: this.getDefaultColumnVisibilityMap(),
          columnsForTable: this.tableViewConfig.displayedColumns,
          filterColumnsForTable: this.tableViewConfig.filterColumns,
        };
        this.tableSettingsService.storeTableSettings(tableSettingsList);
        this.setupTableConfigurations(this.tableViewConfig.displayedColumns, this.tableViewConfig.filterColumns, this.tableViewConfig.sortableColumns, this.tableViewConfig.displayFilterColumnMappings, this.tableViewConfig.filterFormGroup);
      }
    } else {
      // if no, create new list and a settings entry for this.tabletype with default values and set correspondingly the tableconfig
      const newTableSettingsList = {
        [this.tableType]: {
          columnsForDialog: this.tableViewConfig.displayedColumns,
          columnSettingsOptions: this.getDefaultColumnVisibilityMap(),
          columnsForTable: this.tableViewConfig.displayedColumns,
          filterColumnsForTable: this.tableViewConfig.filterColumns,
        },
      };
      this.tableSettingsService.storeTableSettings(newTableSettingsList);
      this.setupTableConfigurations(this.tableViewConfig.displayedColumns, this.tableViewConfig.filterColumns, this.tableViewConfig.sortableColumns, this.tableViewConfig.displayFilterColumnMappings, this.tableViewConfig.filterFormGroup);
    }
  }

  private setupTableConfigurations(displayedColumnsForTable: string[], displayedColumns: string[], sortableColumns: Record<string, boolean>, filterConfiguration: any[], filterFormGroup: any): any {
    const headerKey = 'table.column';
    this.tableConfig = {
      displayedColumns: displayedColumnsForTable,
      header: CreateHeaderFromColumns(displayedColumnsForTable, headerKey),
      sortableColumns: sortableColumns,
      menuActionsConfig: [{
        label: 'actions.viewDetails',
        icon: 'remove_red_eye',
        action: (data: Record<string, unknown>) => this.selected.emit(data),
      }],
    };
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
    for (const column of this.tableViewConfig.displayedColumns) {
      initialColumnMap.set(column, true);
    }
    return initialColumnMap;
  }

  private initializeTableViewSettings(): void {
    switch (this.tableType) {
      case TableType.AS_PLANNED_CUSTOMER:
        this.tableViewConfig = new PartsAsPlannedCustomerConfigurationModel().filterConfiguration();
        break;
      case TableType.AS_PLANNED_OWN:
        this.tableViewConfig = new PartsAsPlannedConfigurationModel().filterConfiguration();
        break;
      case TableType.AS_PLANNED_SUPPLIER:
        this.tableViewConfig = new PartsAsPlannedSupplierConfigurationModel().filterConfiguration();
        break;
      case TableType.AS_BUILT_OWN:
        this.tableViewConfig = new PartsAsBuiltConfigurationModel().filterConfiguration();
        break;
      case TableType.AS_BUILT_CUSTOMER:
        this.tableViewConfig = new PartsAsBuiltCustomerConfigurationModel().filterConfiguration();
        break;
      case TableType.AS_BUILT_SUPPLIER:
        this.tableViewConfig = new PartsAsBuiltSupplierConfigurationModel().filterConfiguration();
        break;
    }
  }

  public areAllRowsSelected(): boolean {
    return this.dataSource.data.every(data => this.isSelected(data));
  }

  public clearAllRows(): void {
    this.selection.clear();
    this.emitMultiSelect();
  }

  public clearCurrentRows(): void {
    this.removeSelectedValues(this.dataSource.data);
    this.emitMultiSelect();
  }

  public toggleAllRows(): void {
    this.areAllRowsSelected()
      ? this.removeSelectedValues(this.dataSource.data)
      : this.addSelectedValues(this.dataSource.data);

    this.emitMultiSelect();

    this.removeFocus();
  }

  public onPaginationChange({ pageIndex, pageSize }: PageEvent): void {
    this.pageIndex = pageIndex;
    this.isDataLoading = true;
    this.onPaginationPageSizeChange.emit(pageSize);
    this.configChanged.emit({ page: pageIndex, pageSize: pageSize, sorting: this.sorting });
  }

  public updateSortingOfData({ active, direction }: Sort): void {
    this.selection.clear();
    this.emitMultiSelect();
    this.sorting = !direction ? null : ([active, direction] as TableHeaderSort);
    this.isDataLoading = true;
    this.configChanged.emit({ page: 0, pageSize: this.pageSize, sorting: this.sorting });
  }

  public toggleSelection(row: unknown): void {
    this.isSelected(row) ? this.removeSelectedValues([row]) : this.addSelectedValues([row]);
    this.emitMultiSelect();

    this.removeFocus();
  }

  public selectElement(row: Record<string, unknown>) {
    this.selectedRow = this.selectedRow === row ? null : row;

    this.selected.emit(row);
  }

  private emitMultiSelect(): void {
    this.multiSelect.emit(this.selection.selected);
  }

  public toggleFilter(): void {
    this.displayedFilter = !this.displayedFilter;
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

  private removeFocus() {
    (document.activeElement as HTMLElement).blur();
  }

  public resetFilterActive(): void {
    this.filterConfiguration.forEach(filter => {
      if (filter.column) {
        this.filterActive[filter.column] = false;
      } else {
        this.filterActive[filter.filterKey] = false;
      }
    });
  }

  public onFiltersReset(): void {
    for (const multiSelect of this.multiSelectAutocompleteComponents) {
      multiSelect.clickClear();
    }
    this.multiSortList = [];
    this.updateSortingOfData({ active: null, direction: null });
  }

  public settingsOpened(state: boolean): void {
    this.isSettingsOpen = state;
  }

}
