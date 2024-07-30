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
import {
  Component,
  ElementRef,
  EventEmitter,
  Input,
  Optional,
  Output,
  ViewChild,
  ViewEncapsulation,
} from '@angular/core';
import { FormGroup } from '@angular/forms';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Router } from '@angular/router';
import { EmptyPagination, Pagination } from '@core/model/pagination.model';
import { RoleService } from '@core/user/role.service';
import { TableSettingsService } from '@core/user/table-settings.service';
import { ContractType } from '@page/admin/core/admin.model';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { PartsTableConfigUtils } from '@shared/components/parts-table/parts-table-config.utils';
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
import { isDateFilter } from '@shared/helper/filter-helper';
import { addSelectedValues, clearAllRows, clearCurrentRows, removeSelectedValues } from '@shared/helper/table-helper';
import { Notification, NotificationStatus } from '@shared/model/notification.model';
import { FlattenObjectPipe } from '@shared/pipes/flatten-object.pipe';
import { NotificationProcessingService } from '@shared/service/notification-processing.service';

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
  @Input() tableHeaderMenuEnabled = false;
  @Input() basicTableHeaderMenuEnabled = false;

  @Input()
  set tableConfig(tableConfig: TableConfig) {
    if (!tableConfig) {
      return;
    }

    const { menuActionsConfig: menuActions, displayedColumns: dc, columnRoles, hasPagination = true } = tableConfig;
    const displayedColumns = dc.filter(column => this.roleService.hasAccess(columnRoles?.[column] ?? 'user') || this.roleService.hasAccess('admin'));
    const menuActionsConfig = this.menuActionsWithAddedDefaultActions(menuActions);
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
  @Input() selectedPoliciesInfoLabel: string;
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
    this.handleSelectionChange();
  }

  @Input() set addTrigger(newItem: unknown) {
    if (!newItem) {
      return;
    }

    this.selection.select(newItem);
    this.handleSelectionChange();
  }

  @Output() selected = new EventEmitter<Record<string, unknown>>();
  @Output() editClicked = new EventEmitter<Record<string, unknown>>();
  @Output() configChanged = new EventEmitter<TableEventConfig>();
  @Output() multiSelect = new EventEmitter<any[]>();
  @Output() clickSelectAction = new EventEmitter<void>();
  @Output() filterActivated = new EventEmitter<any>();
  @Output() deletionClicked = new EventEmitter();
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

  public notificationsSelectedOnlyInStatusCreated: boolean;

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
    public readonly roleService: RoleService,
    private dialog: MatDialog,
    private tableSettingsService: TableSettingsService,
    public toastService: ToastService,
    private readonly router: Router,
    @Optional() public readonly notificationProcessingService: NotificationProcessingService,
  ) {

  }

  ngOnInit(): void {

    if (this.tableSettingsEnabled) {
      this.tableViewConfig = this.tableSettingsService.initializeTableViewSettings(this.tableType);
      this.tableSettingsService.getEvent().subscribe(() => {
        this.setupTableViewSettings();
      });
      this.setupTableViewSettings();
    } else {
      const displayFilterColumnMappings = this.tableType === TableType.CONTRACTS ?
        PartsTableConfigUtils.generateFilterColumnsMapping(this.tableConfig?.sortableColumns, [ 'creationDate', 'endDate' ], [], true, false)
        : PartsTableConfigUtils.generateFilterColumnsMapping(this.tableConfig?.sortableColumns, [ 'createdDate', 'targetDate' ], [], false, true);

      const filterColumns = this.tableType === TableType.CONTRACTS ?
        PartsTableConfigUtils.createFilterColumns(this.tableConfig?.displayedColumns, true, false)
        : PartsTableConfigUtils.createFilterColumns(this.tableConfig?.displayedColumns, false, true);

      this.tableViewConfig = {
        displayedColumns: this.tableConfig?.sortableColumns ? Object.keys(this.tableConfig?.sortableColumns) : [],
        filterFormGroup: PartsTableConfigUtils.createFormGroup(this.tableConfig?.displayedColumns),
        filterColumns: filterColumns,
        sortableColumns: this.tableConfig?.sortableColumns,
        displayFilterColumnMappings: displayFilterColumnMappings,
      };
      for (const controlName in this.tableViewConfig.filterFormGroup) {
        if (this.tableViewConfig.filterFormGroup.hasOwnProperty(controlName)) {
          this.filterFormGroup.addControl(controlName, this.tableViewConfig.filterFormGroup[controlName]);
        }
      }
    }

    this.filterFormGroup.valueChanges.subscribe((formValues) => {
      this.filterActivated.emit(formValues);
    });

  }

  private setupTableViewSettings() {

    this.tableSettingsService.storedTableSettingsInvalid(this.tableViewConfig, this.tableType);

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
          columnSettingsOptions: PartsTableConfigUtils.getDefaultColumnVisibilityMap(this.tableViewConfig.displayedColumns),
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
          columnSettingsOptions: PartsTableConfigUtils.getDefaultColumnVisibilityMap(this.tableViewConfig.displayedColumns),
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
      ...this.tableConfig,
      displayedColumns: displayedColumnsForTable,
      header: CreateHeaderFromColumns(displayedColumnsForTable, headerKey),
      sortableColumns: sortableColumns,
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

    this.handleSelectionChange();
  }

  public onPaginationChange({ pageIndex, pageSize }: PageEvent): void {
    this.pageIndex = pageIndex;
    this.isDataLoading = true;
    this.configChanged.emit({ page: pageIndex, pageSize: pageSize, sorting: this.sorting });
  }

  public updateSortingOfData({ active, direction }: Sort): void {
    this.selection.clear();
    this.handleSelectionChange();
    this.sorting = !direction ? null : ([ active, direction ] as TableHeaderSort);
    this.isDataLoading = true;
    if (this.pageSize === 0) {
      this.pageSize = EmptyPagination.pageSize;
    }
    this.configChanged.emit({ page: 0, pageSize: this.pageSize, sorting: this.sorting });
  }

  public toggleSelection(row: unknown): void {
    this.isSelected(row) ? this.removeSelectedValues([ row ]) : this.addSelectedValues([ row ]);
    this.handleSelectionChange();
  }

  public selectElement(row: Record<string, unknown>) {
    this.selectedRow = this.selectedRow === row ? null : row;

    if (!this.tableConfig.menuActionsConfig) {
      this.selected.emit(row);
    }
  }

  private handleSelectionChange(): void {
    this.notificationsSelectedOnlyInStatusCreated = this.selection.selected.every(notification => notification?.['status'] === NotificationStatus.CREATED);
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

  navigateToNotificationCreationView() {
    this.router.navigate([ 'inbox/create' ]);
  }

  navigateToCreationPath() {
    this.router.navigate([ this.router.url, 'create' ]);
  }

  private menuActionsWithAddedDefaultActions(menuActionsConfig: MenuActionConfig<unknown>[] = []): MenuActionConfig<unknown>[] {
    const viewDetailsMenuAction: MenuActionConfig<unknown> = {
      label: 'actions.viewDetails',
      icon: 'remove_red_eye',
      action: (data: Record<string, unknown>) => this.selected.emit(data),
      isLoading: (data) => false, // detailed view is always active while loading
    };

    const editDetailsMenuAction: MenuActionConfig<unknown> = {
      label: 'actions.edit',
      icon: 'edit',
      action: (data: Record<string, unknown>) => this.editClicked.emit(data),
      condition: data => this.isEditable(data),
      isAuthorized: this.roleService.isSupervisor() || this.roleService.isUser(),
      isLoading: (data) => this.notificationProcessingService.isInLoadingProcess(data as Notification),
    };
    const defaultActionsToAdd: MenuActionConfig<unknown>[] = [ viewDetailsMenuAction, editDetailsMenuAction ]
      .filter(action => !menuActionsConfig.some(a => a.label === action.label));

    return [ ...defaultActionsToAdd, ...menuActionsConfig ];
  };

  handleItemDeletion() {
    this.deletionClicked.emit();
  }

  public isDateElement(key: string) {
    return isDateFilter(key);
  }

  public isOwnerElement(key: string) {
    const OWNER_KEY = [ 'owner'];
    return OWNER_KEY.includes(key);
  }

  protected readonly MainAspectType = MainAspectType;
  protected readonly ContractType = ContractType;
}
