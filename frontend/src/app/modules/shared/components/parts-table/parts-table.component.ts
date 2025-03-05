/********************************************************************************
 * Copyright (c) 2023, 2025 Contributors to the Eclipse Foundation
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

import { animate, state, style, transition, trigger } from '@angular/animations';
import { SelectionChange, SelectionModel } from '@angular/cdk/collections';
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
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { EmptyPagination, Pagination } from '@core/model/pagination.model';
import { RoleService } from '@core/user/role.service';
import { TableSettingsService } from '@core/user/table-settings.service';
import { UserService } from '@core/user/user.service';
import { PartsFacade } from '@page/parts/core/parts.facade';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { Owner } from '@page/parts/model/owner.enum';
import { PartReloadOperation } from '@page/parts/model/partReloadOperation.enum';
import { ImportState, Part } from '@page/parts/model/parts.model';
import { MultiSelectAutocompleteComponent } from '@shared/components/multi-select-autocomplete/multi-select-autocomplete.component';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { PartsTableConfigUtils } from '@shared/components/parts-table/parts-table-config.utils';
import { TableViewConfig } from '@shared/components/parts-table/table-view-config.model';
import { TableSettingsComponent } from '@shared/components/table-settings/table-settings.component';
import {
  CreateHeaderFromColumns,
  TableConfig,
  TableEventConfig,
  TableHeaderSort,
} from '@shared/components/table/table.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import { isDateFilter } from '@shared/helper/filter-helper';
import { addSelectedValues, removeSelectedValues } from '@shared/helper/table-helper';
import { NotificationColumn, NotificationType } from '@shared/model/notification.model';
import { BomLifecycleSettingsService } from '@shared/service/bom-lifecycle-settings.service';
import { DeeplinkService } from '@shared/service/deeplink.service';
import { FilterService } from '@shared/service/filter.service';
import { QuickfilterService } from '@shared/service/quickfilter.service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-parts-table',
  templateUrl: './parts-table.component.html',
  styleUrls: [ 'parts-table.component.scss' ],
  animations: [
    trigger('tableFilterExpand', [
      state('collapsed', style({ height: '0px', minHeight: '0', display: 'none' })),
      state('expanded', style({ height: '*' })),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
    trigger('advancedFilterExpand', [
      state('collapsed', style({ height: '0px', minHeight: '0', display: 'none' })),
      state('expanded', style({ height: '*' })),
    ]),
  ],
})
export class PartsTableComponent implements OnInit {
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild('tableElement', { read: ElementRef }) tableElementRef: ElementRef<HTMLElement>;
  @ViewChildren(MultiSelectAutocompleteComponent) multiSelectAutocompleteComponents: QueryList<MultiSelectAutocompleteComponent>;

  @Output() publishIconClickedEvent = new EventEmitter<void>();
  @Output() partReloadClickedEvent = new EventEmitter<PartReloadOperation>();
  @Input() labelId: string;
  @Input() noShadow = false;
  @Input() showHover = true;
  @Input() menuActivated = true;

  @Input() selectedPartsInfoLabel: string;
  @Input() selectedPartsActionLabel: string;

  @Input() tableHeader: string;
  @Input() multiSortList: TableHeaderSort[];

  @Input() tableType: TableType;
  @Input() mainAspectType: MainAspectType;

  @Input() assetIdsForAutoCompleteFilter: string[];

  preFilter: string;
  public tableConfig: TableConfig;
  toggleTableFilter: boolean = true;
  isMaximized = false;
  toggleAdvancedFilter: boolean = false;
  activeFiltersCount = 0;
  @Output() selected = new EventEmitter<Record<string, unknown>>();
  @Output() createQualityNotificationClickedEvent = new EventEmitter<NotificationType>();
  @Output() configChanged = new EventEmitter<TableEventConfig>();
  @Output() multiSelect = new EventEmitter<any[]>();
  @Output() clickSelectAction = new EventEmitter<void>();
  @Output() filterActivated = new EventEmitter<any>();
  @Output() maximizeClicked = new EventEmitter<TableType>();
  filterKeys = [
    'owner',
    'id',
    'idShort',
    'nameAtManufacturer',
    'businessPartner',
    'manufacturerName',
    'manufacturerPartId',
    'customerPartId',
    'classification',
    'nameAtCustomer',
    'semanticModelId',
    'semanticDataModel',
    'manufacturingDate',
    'manufacturingCountry',
    'receivedActiveAlerts',
    'receivedActiveInvestigations',
    'sentActiveAlerts',
    'sentActiveInvestigations',
    'importState',
    'importNote',
  ];
  public readonly dataSource = new MatTableDataSource<unknown>();
  public readonly selection = new SelectionModel<unknown>(true, []);
  public totalItems: number;
  public pageIndex: number;
  public isDataLoading: boolean;
  public isMenuOpen: boolean;
  public notificationType: NotificationType;
  // TODO remove it and set only in tableViewConfig
  public displayedColumns: string[];
  // TODO remove it and set only in tableViewConfig
  public defaultColumns: string[];
  // TODO remove it and set only in tableViewConfig
  filterFormGroup = new FormGroup({});
  public tableViewConfig: TableViewConfig;
  protected readonly TableType = TableType;
  protected readonly MainAspectType = MainAspectType;
  protected readonly NotificationType = NotificationType;
  protected readonly UserService = UserService;
  private pageSize: number;
  public sorting: TableHeaderSort;

  constructor(
    private readonly tableSettingsService: TableSettingsService,
    private dialog: MatDialog,
    private router: Router,
    private route: ActivatedRoute,
    private deeplinkService: DeeplinkService,
    public roleService: RoleService,
    private readonly partsFacade: PartsFacade,
    private readonly toastService: ToastService,
    private readonly quickFilterService: QuickfilterService,
    private filterService: FilterService,
    private bomLifeCycleSettingsService: BomLifecycleSettingsService,
  ) {
    this.preFilter = this.route.snapshot.queryParams['contractId'];
  }

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

  public isAllowedToCreateInvestigation(): boolean {
    const selected = this.selection.selected as Part[];
    const hasDifferentOwner = selected.some(value => value.owner !== Owner.SUPPLIER);
    return !hasDifferentOwner;
  }

  public isAllowedToCreateAlert(): boolean {
    const selected = this.selection.selected as Part[];
    const hasDifferentOwner = selected.some(value => value.owner !== Owner.OWN);
    return !hasDifferentOwner;
  }

  public isBusinessPartnerValid(): boolean {
    const selected = this.selection.selected as Part[];
    const hasNullBusinessPartner = selected.some(value => value.businessPartner === null || value.businessPartner === '');
    return !hasNullBusinessPartner;
  }

  public isActionButtonDisabled(): boolean {
    return (!this.isAllowedToCreateAlert() && !this.isAllowedToCreateInvestigation()) || this.roleService.isAdmin() || !this.isBusinessPartnerValid();
  }

  getTooltipText(): string {
    if (this.roleService.isAdmin()) {
      return 'routing.unauthorized';
    }
    if (!this.isAllowedToCreateAlert() && !this.isAllowedToCreateInvestigation() && this.isBusinessPartnerValid()) {
      return this.selectionContainsCustomerPart()
        ? 'routing.hasCustomerPart'
        : 'routing.partMismatch';
    }
    if (!this.isBusinessPartnerValid()) {
      return 'table.missingBusinessPartner';
    }
    return 'table.createNotification';
  }

  handleKeyDownOpenDialog(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.openDialog();
    }
  }

  handleKeyDownMaximizedClickedMethod(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.maximizeClickedMethod();
    }
  }

  public maximizeClickedMethod(): void {
    this.isMaximized = !this.isMaximized;
    this.maximizeClicked.emit(this.tableType);
  }

  public atLeastOneSelected(): boolean {
    return this.selection.selected?.length > 0;
  }

  isValidOwnerSelection(): boolean {
    const selected = this.selection.selected as Part[];
    if (this.tableType === TableType.AS_PLANNED_OWN || this.tableType === TableType.AS_BUILT_OWN ) {
      return selected.every(part => part.owner === Owner.OWN);
    }
    return true;
  }

  selectionContainsCustomerPart(): boolean {
    const selected = this.selection.selected as Part[];
    return selected.some(part => part.owner === Owner.CUSTOMER);
  }

  handleKeyDownQualityNotificationClicked(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.createQualityNotificationClicked();
    }
  }

  public createQualityNotificationClicked(): void {
    this.createQualityNotificationClickedEvent.emit(this.notificationType);
  }

  public isAdmin(): boolean {
    return this.roleService.hasAccess([ 'admin' ]);
  }

  isIllegalSelectionToPublish(): boolean {
    return this.selection.selected.some((part: Part) => {
      return part?.importState !== ImportState.TRANSIENT && part?.importState !== ImportState.ERROR;
    });
  }

  handleKeyDownPublishIconClicked(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      this.publishIconClicked();
    }
  }

  handleKeyDownPartReloadIconClicked(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      this.partReloadIconClicked();
    }
  }

  public publishIconClicked() {
    this.publishIconClickedEvent.emit();
  }

  public partReloadIconClicked() {
    if (!this.atLeastOneSelected()) {
      this.partReloadClickedEvent.emit(PartReloadOperation.RELOAD_REGISTRY);
    } else {
      switch (this.mainAspectType) {
        case MainAspectType.AS_BUILT:
          this.partReloadClickedEvent.emit(PartReloadOperation.SYNC_PARTS_AS_BUILT);
          break;
        case MainAspectType.AS_PLANNED:
          this.partReloadClickedEvent.emit(PartReloadOperation.SYNC_PARTS_AS_PLANNED);
          break;
      }
    }
  }

  toggleAdvancedSearch() {
    this.toggleAdvancedFilter = !this.toggleAdvancedFilter;
  }

  handleKeyDownToggleAdvancedSearch(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      this.toggleAdvancedFilter = !this.toggleAdvancedFilter;
    }
  }

  public deeplinkToNotification(column: any, notificationId: string[]) {
    const deeplinkModel = this.deeplinkService.getDeeplink(column, notificationId);
    this.router.navigate([ deeplinkModel.route ], {
      queryParams: {
        tabIndex: deeplinkModel.tabIndex,
        deeplink: true,
        received: deeplinkModel.received,
        notificationIds: deeplinkModel.data,
      },
    });
  }

  public isNotificationCountColumn(column: NotificationColumn) {
    return column === NotificationColumn.RECEIVED_ALERT || column === NotificationColumn.SENT_ALERT || column === NotificationColumn.RECEIVED_INVESTIGATION || column === NotificationColumn.SENT_INVESTIGATION;
  }

  public isDateElement(key: string) {
    return isDateFilter(key);
  }

  public isOwner(key: string) {
    return key === 'owner';
  }

  public isSemanticDataModel(key: string) {
    return key === 'semanticDataModel';
  }

  ngOnInit() {
    this.tableViewConfig = this.tableSettingsService.initializeTableViewSettings(this.tableType);
    this.tableSettingsService.getEvent().subscribe(() => {
      this.setupTableViewSettings();
    });
    this.setupTableViewSettings();
    this.selection.changed.subscribe((change: SelectionChange<Part>) => {
      // Handle selection change here
      if (this.isAllowedToCreateInvestigation()) {
        this.notificationType = NotificationType.INVESTIGATION;
      } else if (this.isAllowedToCreateAlert()) {
        this.notificationType = NotificationType.ALERT;
      } else {
        this.notificationType = null;
      }

    });
    this.quickFilterService.owner$.subscribe((currentOwner: Owner) => {
      this.updateOwnerFilter(currentOwner);
    });

    const filter = this.filterService.getFilter(this.tableType);

    this.filterKeys.forEach((key) => {
      if (filter[key]) {
        this.filterFormGroup.get(key)?.setValue(filter[key]);
      }
    });

    this.filterService.filterState$
      .pipe(
        map(filterState =>
          this.tableType === TableType.AS_BUILT_OWN
            ? filterState.asBuilt
            : filterState.asPlanned,
        ),
      )
      .subscribe(filterForThisTable => {
        this.filterKeys.forEach(key => {
          const value = filterForThisTable[key] ?? null;
          this.filterFormGroup.get(key)?.setValue(value, { emitEvent: false });
        });
        this.activeFiltersCount = this.countActiveFilters(filterForThisTable);
        this.filterActivated.emit(filterForThisTable);
      });


    this.bomLifeCycleSettingsService.settings$.subscribe((settings) => {
      if (this.tableType === TableType.AS_BUILT_OWN) {
        this.isMaximized = (settings.asBuiltSize === 100);
      } else {
        this.isMaximized = (settings.asPlannedSize === 100);
      }
    });
  }

  public areAllRowsSelected(): boolean {
    return this.dataSource.data.every(data => this.isSelected(data));
  }

  public clearAllRows(): void {
    this.selection.clear();
    this.emitMultiSelect();
  }

  public onRowDoubleClick(row: Record<string, unknown>) {
    this.selected.emit(row);
  }

  public deleteItem(data: Part) {
    const deleteObservable = data.mainAspectType === MainAspectType.AS_PLANNED
      ? this.partsFacade.deletePartByIdAsPlanned(data.id)
      : this.partsFacade.deletePartByIdAsBuilt(data.id);

    deleteObservable.subscribe({
      next: () => {
        this.toastService.success('actions.deletePartMessageSuccess');
        this.reloadTableData();
      },
    });
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
  }

  public onPaginationChange({ pageIndex, pageSize }: PageEvent): void {
    this.pageIndex = pageIndex;
    this.isDataLoading = true;
    this.configChanged.emit({ page: pageIndex, pageSize: pageSize, sorting: this.sorting });
  }

  public updateSortingOfData({ active, direction }: Sort): void {
    this.pageSize = this.pageSize === 0 ? EmptyPagination.pageSize : this.pageSize;
    this.selection.clear();
    this.emitMultiSelect();
    this.sorting = !direction ? null : ([ active, direction ] as TableHeaderSort);
    this.isDataLoading = true;
    this.configChanged.emit({ page: 0, pageSize: this.pageSize, sorting: this.sorting });
  }

  public toggleSelection(row: unknown): void {
    this.isSelected(row) ? this.removeSelectedValues([ row ]) : this.addSelectedValues([ row ]);
    this.emitMultiSelect();
  }

  public isSelected(row: unknown): boolean {
    return !!this.selection.selected.find(data => JSON.stringify(data) === JSON.stringify(row));
  }

  openDialog(): void {
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

  private countActiveFilters(filterObject: Record<string, any>): number {
    if (!filterObject) {
      return 0;
    }

    return Object.keys(filterObject).reduce((count, key) => {
      const value = filterObject[key];

      if (Array.isArray(value) && value.length > 0) {
        return count + 1;
      }

      if (typeof value === 'string' && value.trim() !== '') {
        return count + 1;
      }

      if (value && typeof value === 'object' && Object.keys(value).length > 0) {
        return count + 1;
      }
      if (value && typeof value !== 'object') {
        return count + 1;
      }

      return count;
    }, 0);
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
      displayedColumns: displayedColumnsForTable,
      header: CreateHeaderFromColumns(displayedColumnsForTable, headerKey),
      sortableColumns: sortableColumns,
      menuActionsConfig: [ {
        label: 'actions.viewDetails',
        icon: 'remove_red_eye',
        action: (data: Record<string, unknown>) => this.selected.emit(data),
      }, {
        label: 'actions.deletePart',
        icon: 'delete',
        action: (data: Record<string, Part>) => this.deleteItem(data as unknown as Part),
        disabled: !this.roleService.isAdmin(),
      } ],
    };
    this.displayedColumns = displayedColumns;

    for (const controlName in filterFormGroup) {
      if (filterFormGroup.hasOwnProperty(controlName)) {
        this.filterFormGroup.addControl(controlName, filterFormGroup[controlName]);
      }
    }
  }

  private reloadTableData(): void {
    this.configChanged.emit({ page: this.pageIndex, pageSize: this.pageSize, sorting: this.sorting });
  }

  private emitMultiSelect(): void {
    this.multiSelect.emit(this.selection.selected);
  }

  private addSelectedValues(newData: unknown[]): void {
    addSelectedValues(this.selection, newData);
  }

  private removeSelectedValues(itemsToRemove: unknown[]): void {
    removeSelectedValues(this.selection, itemsToRemove);
  }

  private updateOwnerFilter(ownerValue: Owner): void {
    const ownerControl = this.filterFormGroup.get('owner');
    if (!ownerControl) {
      return;
    }

    if (ownerValue !== Owner.UNKNOWN) {
      // Set `owner` form control to array containing the new value
      // @ts-ignore
      ownerControl.setValue([ ownerValue ]);
    } else {
      // Clear it if the ownerValue is UNKNOWN
      // @ts-ignore
      ownerControl.setValue([]);
    }
  }
}
