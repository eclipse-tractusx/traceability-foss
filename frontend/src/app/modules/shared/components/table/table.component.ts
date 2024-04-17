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
import { FormGroup } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { EmptyPagination, Pagination } from '@core/model/pagination.model';
import { RoleService } from '@core/user/role.service';
import { TableSettingsService } from '@core/user/table-settings.service';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { NotificationsReceivedConfigurationModel } from '@shared/components/parts-table/notifications-received-configuration.model';
import { NotificationsSentConfigurationModel } from '@shared/components/parts-table/notifications-sent-configuration.model';
import { PartsAsBuiltConfigurationModel } from '@shared/components/parts-table/parts-as-built-configuration.model';
import { PartsAsBuiltCustomerConfigurationModel } from '@shared/components/parts-table/parts-as-built-customer-configuration.model';
import { PartsAsBuiltSupplierConfigurationModel } from '@shared/components/parts-table/parts-as-built-supplier-configuration.model';
import { PartsAsPlannedConfigurationModel } from '@shared/components/parts-table/parts-as-planned-configuration.model';
import { PartsAsPlannedCustomerConfigurationModel } from '@shared/components/parts-table/parts-as-planned-customer-configuration.model';
import { PartsAsPlannedSupplierConfigurationModel } from '@shared/components/parts-table/parts-as-planned-supplier-configuration.model';
import { TableViewConfig } from '@shared/components/parts-table/table-view-config.model';
import { TableSettingsComponent } from '@shared/components/table-settings/table-settings.component';
import {
  CreateHeaderFromColumns,
  MenuActionConfig,
  TableConfig,
  TableEventConfig,
  TableHeaderSort,
} from '@shared/components/table/table.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import { addSelectedValues, clearAllRows, clearCurrentRows, removeSelectedValues } from '@shared/helper/table-helper';
import { NotificationStatus } from '@shared/model/notification.model';
import { FlattenObjectPipe } from '@shared/pipes/flatten-object.pipe';

@Component({
  selector: 'app-table',
  templateUrl: './table.component.html',
  styleUrls: [ 'table.component.scss' ],
  encapsulation: ViewEncapsulation.None,
})
export class TableComponent {

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild('tableElement', { read: ElementRef }) tableElementRef: ElementRef<HTMLElement>;
  @Input() additionalTableHeader = false;

  @Input()
  set tableConfig(tableConfig: TableConfig) {
    if (!tableConfig) {
      return;
    }

    const { menuActionsConfig: menuActions, displayedColumns: dc, columnRoles, hasPagination = true } = tableConfig;
    const displayedColumns = dc.filter(column => this.roleService.hasAccess(columnRoles?.[column] ?? 'user') || this.roleService.hasAccess('admin'));

    const viewDetailsMenuAction: MenuActionConfig<unknown> = {
      label: 'actions.viewDetails',
      icon: 'remove_red_eye',
      action: (data: Record<string, unknown>) => this.selected.emit(data),
    };

    const editDetailsMenuAction: MenuActionConfig<unknown> = {
      label: 'actions.edit',
      icon: 'edit',
      action: (data: Record<string, unknown>) => this.editClicked.emit(data),
      condition: data => this.isEditable(data),
      isAuthorized: this.roleService.isSupervisor(),
    };

    const menuActionsConfig = menuActions ? [ viewDetailsMenuAction, editDetailsMenuAction, ...menuActions ] : null;
    this._tableConfig = { ...tableConfig, displayedColumns, hasPagination, menuActionsConfig };
  }

  isEditable(data: any): boolean {
    return data.status === NotificationStatus.CREATED;
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
  @Input() multiSortList: TableHeaderSort[];

  @Input() set paginationData(paginationData: Pagination<unknown>) {
    if (!paginationData) {
      return;
    }

    const { page, pageSize, totalItems, content } = paginationData;
    this.totalItems = totalItems;
    this.pageSize = pageSize;
    this.dataSource.data = content;
    this.isDataLoading = false;
    this.pageIndex = page;
  }

  @Input() set PartsPaginationData({ page, pageSize, totalItems, content }: Pagination<unknown>) {
    let flatter = new FlattenObjectPipe();
    // modify the content of the partlist so that there are no subobjects
    let newContent = content.map(part => flatter.transform(part));
    this.totalItems = totalItems;
    this.pageSize = pageSize;
    this.dataSource.data = newContent;
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
  @Output() editClicked = new EventEmitter<Record<string, unknown>>();
  @Output() configChanged = new EventEmitter<TableEventConfig>();
  @Output() multiSelect = new EventEmitter<any[]>();
  @Output() clickSelectAction = new EventEmitter<void>();
  @Output() filterActivated = new EventEmitter<any>();
  @Input()
  public autocompleteEnabled = false;
  @Input() tableSettingsEnabled: boolean = false;

  public readonly dataSource = new MatTableDataSource<unknown>();
  public readonly selection = new SelectionModel<unknown>(true, []);

  public totalItems: number;
  public pageIndex: number;
  public isDataLoading: boolean;
  public selectedRow: Record<string, unknown>;
  public isMenuOpen: boolean;

  private pageSize: number;
  private sorting: TableHeaderSort;

  private _tableConfig: TableConfig;

  public tableViewConfig: TableViewConfig;

  filterFormGroup = new FormGroup({});

  // input notification type map to parttable type,
  @Input() tableType: TableType = TableType.AS_BUILT_OWN;

  public displayedColumns: string[];
  public defaultColumns: string[];

  constructor(
    private readonly roleService: RoleService,
    private dialog: MatDialog,
    private tableSettingsService: TableSettingsService,
    private toastService: ToastService,
  ) {

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
      case TableType.SENT_NOTIFICATION:
        this.tableViewConfig = new NotificationsSentConfigurationModel().filterConfiguration();
        break;
      case TableType.RECEIVED_NOTIFICATION:
        this.tableViewConfig = new NotificationsReceivedConfigurationModel().filterConfiguration();
        break;
    }
  }

  ngOnInit(): void {

    if (this.tableSettingsEnabled) {
      this.initializeTableViewSettings();
      this.tableSettingsService.getEvent().subscribe(() => {
        this.setupTableViewSettings();
      });
      this.setupTableViewSettings();
    }

    this.filterFormGroup.valueChanges.subscribe((formValues) => {
      this.filterActivated.emit(formValues);
    });
  }

  private setupTableViewSettings() {
    if (this.tableSettingsService.storedTableSettingsInvalid(this.tableViewConfig, this.tableType)) {
      this.toastService.warning('table.tableSettings.invalid', 10000);
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

  private getDefaultColumnVisibilityMap(): Map<string, boolean> {
    const initialColumnMap = new Map<string, boolean>();
    for (const column of this.tableViewConfig.displayedColumns) {
      initialColumnMap.set(column, true);
    }
    return initialColumnMap;
  }


  private setupTableConfigurations(displayedColumnsForTable: string[], displayedColumns: string[], sortableColumns: Record<string, boolean>, filterConfiguration: any[], filterFormGroup: any): any {
    const headerKey = 'table.column';
    this.tableConfig = {
      displayedColumns: displayedColumnsForTable,
      header: CreateHeaderFromColumns(displayedColumnsForTable, headerKey),
      sortableColumns: sortableColumns,
      menuActionsConfig: [ {
        label: 'actions.viewDetails',
        icon: 'remove_red_eye',
        action: (data: Record<string, unknown>) => this.selected.emit(data),
      } ],
    };
    this.displayedColumns = displayedColumns;

    for (const controlName in filterFormGroup) {
      if (filterFormGroup.hasOwnProperty(controlName)) {
        this.filterFormGroup.addControl(controlName, filterFormGroup[controlName]);
      }
    }

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
    this.configChanged.emit({ page: pageIndex, pageSize: pageSize, sorting: this.sorting });
  }

  public updateSortingOfData({ active, direction }: Sort): void {
    this.selection.clear();
    this.emitMultiSelect();
    this.sorting = !direction ? null : ([ active, direction ] as TableHeaderSort);
    this.isDataLoading = true;
    if (this.pageSize === 0) {
      this.pageSize = EmptyPagination.pageSize;
    }
    this.configChanged.emit({ page: 0, pageSize: this.pageSize, sorting: this.sorting });
  }

  public toggleSelection(row: unknown): void {
    this.isSelected(row) ? this.removeSelectedValues([ row ]) : this.addSelectedValues([ row ]);
    this.emitMultiSelect();
  }

  public selectElement(row: Record<string, unknown>) {
    this.selectedRow = this.selectedRow === row ? null : row;

    if (!this.tableConfig.menuActionsConfig) {
      this.selected.emit(row);
    }
  }

  private emitMultiSelect(): void {
    this.multiSelect.emit(this.selection.selected);
  }

  public isSelected(row: unknown): boolean {
    return !!this.selection.selected.find(data => JSON.stringify(data) === JSON.stringify(row));
  }

  shouldDisplayFilter(filterKey: string) {
    switch (filterKey) {
      case 'filtercreationDate':
      case 'filtercounterpartyAddress':
      case 'filterendDate':
      case 'filterstate':
      case 'Menu':
      case 'Filter':
        return false;

      default:
        return true;

    }
  }

  private addSelectedValues(newData: unknown[]): void {
    addSelectedValues(this.selection, newData);
  }

  private removeSelectedValues(itemsToRemove: unknown[]): void {
    removeSelectedValues(this.selection, itemsToRemove);
  }

  openDialog() {
    const config = new MatDialogConfig();
    config.data = {
      title: 'table.tableSettings.title',
      panelClass: 'custom',
      tableType: this.tableType,
      defaultColumns: this.tableViewConfig.displayedColumns,
      defaultFilterColumns: this.tableViewConfig.filterColumns,
    };
    this.dialog.open(TableSettingsComponent, config);
  }

  protected readonly MainAspectType = MainAspectType;
}
