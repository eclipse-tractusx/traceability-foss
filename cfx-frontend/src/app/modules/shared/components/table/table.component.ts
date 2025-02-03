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
import { Component, ElementRef, EventEmitter, Input, Output, QueryList, ViewChild, ViewChildren, } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { EmptyPagination, Pagination } from '@core/model/pagination.model';
import { RoleService } from '@core/user/role.service';
import { TableSettingsService } from '@core/user/table-settings.service';
import {
  CreateHeaderFromColumns,
  MenuActionConfig,
  TableConfig,
  TableEventConfig,
  TableHeaderSort,
} from '@shared/components/table/table.model';
import { addSelectedValues, clearAllRows, clearCurrentRows, removeSelectedValues } from '@shared/helper/table-helper';
import { TableViewConfig } from '../parts-table/table-view-config.model';
import { MultiSelectAutocompleteComponent } from '../multi-select-autocomplete/multi-select-autocomplete.component';
import { Role } from '@core/user/role.model';
import { TableType } from '../multi-select-autocomplete/table-type.model';
import { PartsTableConfigUtils } from '../parts-table/parts-table-config.utils';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { TranslationContext } from '@shared/model/translation-context.model';
import { TableSortingUtil } from './tableSortingUtil';

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: ['table.component.scss'],
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
    const displayedColumns = dc.filter(column => this.roleService.hasAccess(columnRoles?.[column] ?? [Role.USER, Role.ADMIN]));

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
  @Input() tableType: TableType;

  @Input() selectedPartsInfoLabel: string;
  @Input() selectedPartsActionLabel: string;

  @Input() tableHeader: string;
  @Input() enableScroll: boolean;
  @Input() multiSortList: TableHeaderSort[];
  @Input() autocompleteEnabled = false;

  @Input() set paginationData({ page, pageSize, totalItems, content, pageCount }: Pagination<unknown>) {
    this.totalItems = totalItems;
    this.pageSize = pageSize;
    this.dataSource.data = content;
    this.isDataLoading = false;
    this.pageIndex = page;
    this.pageCount = pageCount;
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
  @Output() filterActivated = new EventEmitter<any>();

  public readonly dataSource = new MatTableDataSource<unknown>();
  public readonly selection = new SelectionModel<unknown>(true, []);

  public totalItems: number;
  public pageCount: number;
  public pageIndex: number;
  public isDataLoading: boolean;
  public selectedRow: Record<string, unknown>;
  public isMenuOpen: boolean;
  public isSettingsOpen = false;

  public filterConfiguration: any[];
  public displayedColumns: string[];
  public defaultColumns: string[];
  public filterFormGroup = new FormGroup({});

  public pageSize: number;
  private sorting: TableHeaderSort;
  public filterActive: any = {};

  private _tableConfig: TableConfig;
  public tableViewConfig: TableViewConfig;

  isSorted = TableSortingUtil.isSorted;

  protected readonly Role = Role;

  constructor(
    private readonly roleService: RoleService,
    private readonly tableSettingsService: TableSettingsService,
  ) { }

  ngOnInit() {
    const isReceivedTable = this.tableType === TableType.RECEIVED_ALERT || this.tableType === TableType.RECEIVED_INVESTIGATION;
    const translationContext = this.tableType === TableType.RECEIVED_ALERT || this.tableType === TableType.CREATED_ALERT ? TranslationContext.COMMONALERT : TranslationContext.COMMONINVESTIGATION;

    this.tableViewConfig = {
      displayedColumns: this.tableConfig?.displayedColumns,
      filterFormGroup: PartsTableConfigUtils.createFormGroup(this.tableConfig?.displayedColumns),
      filterColumns: PartsTableConfigUtils.createFilterColumns(this.tableConfig?.displayedColumns, false, true),
      sortableColumns: this.tableConfig?.sortableColumns,
      displayFilterColumnMappings: PartsTableConfigUtils.generateFilterColumnsMapping(this.tableConfig?.sortableColumns, ['createdDate', 'targetDate'], [], false, true, isReceivedTable, translationContext),
    };

    for (const controlName in this.tableViewConfig.filterFormGroup) {
      // eslint-disable-next-line no-prototype-builtins
      if (this.tableViewConfig.filterFormGroup.hasOwnProperty(controlName)) {
        this.filterFormGroup.addControl(controlName, this.tableViewConfig.filterFormGroup[controlName]);
      }
    }

    this.tableSettingsService.getEvent().subscribe(() => {
      this.setupTableViewSettings();
    });
    this.setupTableViewSettings();

  }

  private setupTableViewSettings() {
    if (!this.tableViewConfig) {
      return;
    }

    if (!this.tableType && this.tableViewConfig) {
      this.setupTableConfigurations(
        this.tableViewConfig.displayedColumns,
        this.tableViewConfig.filterColumns,
        this.tableViewConfig.sortableColumns,
        this.tableViewConfig.displayFilterColumnMappings,
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
          this.tableViewConfig.displayFilterColumnMappings,
          this.tableViewConfig.filterFormGroup,
        );
      } else {
        // if no, create new a table setting for this.tabletype and put it into the list. Additionally, intitialize default table configuration
        tableSettingsList[this.tableType] = this.createSettingsList();
        this.tableSettingsService.storeTableSettings(tableSettingsList);
        this.setupTableConfigurations(
          this.tableViewConfig.displayedColumns,
          this.tableViewConfig.filterColumns,
          this.tableViewConfig.sortableColumns,
          this.tableViewConfig.displayFilterColumnMappings,
          this.tableViewConfig.filterFormGroup,
        );
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
      this.setupTableConfigurations(
        this.tableViewConfig.displayedColumns,
        this.tableViewConfig.filterColumns,
        this.tableViewConfig.sortableColumns,
        this.tableViewConfig.displayFilterColumnMappings,
        this.tableViewConfig.filterFormGroup,
      );
    }
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
    if (this.tableViewConfig) {
      for (const column of this.tableViewConfig.displayedColumns) {
        initialColumnMap.set(column, true);
      }
    }

    return initialColumnMap;
  }

  private createSettingsList(): any {
    return {
      columnsForDialog: this.tableViewConfig?.displayedColumns,
      columnSettingsOptions: this.getDefaultColumnVisibilityMap(),
      columnsForTable: this.tableViewConfig?.displayedColumns,
      filterColumnsForTable: this.tableViewConfig?.displayedColumns,
    };
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

    this.removeFocus();
  }

  public onFilterValueChanged(formValues: { filterKey: string, values: any[] }) {
    const { filterKey, values } = formValues;
    this.filterFormGroup.controls[filterKey].patchValue(values);

    this.filterActivated.emit(this.filterFormGroup.value);
  }

  public onPaginationChange({ pageIndex, pageSize }: PageEvent): void {
    this.pageIndex = pageIndex;
    this.isDataLoading = true;
    this.configChanged.emit({ page: pageIndex, pageSize: pageSize, sorting: this.sorting });
  }

  public updateSortingOfData({ active, direction }: Sort): void {
    this.selection.clear();
    this.emitMultiSelect();
    this.sorting = !direction ? null : ([active, direction] as TableHeaderSort);
    this.isDataLoading = true;
    if (this.pageSize === 0) {
      this.pageSize = EmptyPagination.pageSize;
    }
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

  public isSelected(row: unknown): boolean {
    return !!this.selection.selected.find(data => JSON.stringify(data) === JSON.stringify(row));
  }

  private addSelectedValues(newData: unknown[]): void {
    addSelectedValues(this.selection, newData);
  }

  private removeSelectedValues(itemsToRemove: unknown[]): void {
    removeSelectedValues(this.selection, itemsToRemove);
  }

  protected readonly MainAspectType = MainAspectType;

  public resetFilter(): void {
    const filterNames = Object.keys(this.filterActive);
    for (const filterName of filterNames) {
      this.filterActive[filterName] = false;
    }
  }

  public onFiltersReset() {
    for (const multiSelect of this.multiSelectAutocompleteComponents) {
      multiSelect.clickClear();
    }
    this.multiSortList = [];
    this.updateSortingOfData({ active: null, direction: null });
  }

  public settingsOpened(state: boolean): void {
    this.isSettingsOpen = state;
  }

  private removeFocus() {
    (document.activeElement as HTMLElement).blur();
  }
}
