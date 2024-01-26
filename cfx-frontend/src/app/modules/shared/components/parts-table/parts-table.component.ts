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
import { FormControl, FormGroup } from '@angular/forms';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Pagination } from '@core/model/pagination.model';
import { MultiSelectAutocompleteComponent } from '@shared/components/multi-select-autocomplete/multi-select-autocomplete.component';
import {
  CreateHeaderFromColumns,
  FilterConfig,
  PartTableType,
  TableConfig,
  TableEventConfig,
  TableHeaderSort,
} from '@shared/components/table/table.model';
import { addSelectedValues, removeSelectedValues } from '@shared/helper/table-helper';
import { isDateFilter } from '@shared/helper/filter-helper';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { TableSettingsService } from '@core/user/table-settings.service';
import { TableViewConfig } from './table-view-config.model';
import { TableSettingsComponent } from '../table-settings/table-settings.component';
import { FilterCongigOptions } from '@shared/model/filter-config';
import { RoleService } from '@core/user/role.service';
import { Role } from '@core/user/role.model';

@Component({
  selector: 'app-parts-table',
  templateUrl: './parts-table.component.html',
  styleUrls: ['parts-table.component.scss'],
})
export class PartsTableComponent implements OnInit {
  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild('tableElement', { read: ElementRef }) tableElementRef: ElementRef<HTMLElement>;
  @ViewChildren(MultiSelectAutocompleteComponent)
  multiSelectAutocompleteComponents: QueryList<MultiSelectAutocompleteComponent>;
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

  @Input() tableType: PartTableType;

  public tableConfig: TableConfig;

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
  public readonly filterConfigOptions = new FilterCongigOptions();
  public semanticDataModelOptions = [...this.filterConfigOptions.filterKeyOptionsAssets.semanticDataModel.option];
  public totalItems: number;
  public pageIndex: number;
  public isDataLoading: boolean;
  public selectedRow: Record<string, unknown>;
  public isMenuOpen: boolean;
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

  private readonly displayedColumnsAsBuilt: string[] = [
    'Filter',
    'filterId',
    'filterIdShort',
    'filterName', // nameAtManufacturer
    'filterManufacturer',
    'filterManufacturerPartId',
    'filterCustomerPartId', // --> semanticModel.customerPartId
    'filterClassification',
    'filterNameAtCustomer', // --> semanticModel.nameAtCustomer
    'filterSemanticModelId',
    'filterSemanticDataModel',
    'filterManufacturingDate',
    'filterManufacturingCountry',
    'filterActiveAlerts',
    'filterActiveInvestigations',
  ];

  private readonly displayedColumnsAsPlanned: string[] = [
    'Filter',
    'filterId',
    'filterIdShort',
    'filterName',
    'filterManufacturer',
    'filterManufacturerPartId',
    'filterClassification',
    'filterSemanticDataModel',
    'filterSemanticModelId',
    'filterValidityPeriodFrom',
    'filterValidityPeriodTo',
    'filterPsFunction',
    'filterCatenaXSiteId',
    'filterFunctionValidFrom',
    'filterFunctionValidUntil',
  ];

  private readonly displayedColumnsAsBuiltForTable: string[] = [
    '!',
    'id',
    'idShort',
    'name',
    'manufacturer',
    'manufacturerPartId',
    'customerPartId',
    'classification',
    'nameAtCustomer',
    'semanticModelId',
    'semanticDataModel',
    'manufacturingDate',
    'manufacturingCountry',
    'activeAlerts',
    'activeInvestigations',
    'menu',
  ];

  private readonly displayedColumnsAsPlannedForTable: string[] = [
    'id',
    'idShort',
    'name',
    'manufacturer',
    'manufacturerPartId',
    'classification',
    'semanticDataModel',
    'semanticModelId',
    'validityPeriodFrom',
    'validityPeriodTo',
    'psFunction',
    'catenaXSiteId',
    'functionValidFrom',
    'functionValidUntil',
    'menu',
  ];

  private readonly sortableColumnsAsBuilt: Record<string, boolean> = {
    id: true,
    idShort: true,
    name: true,
    manufacturer: true,
    manufacturerPartId: true,
    customerPartId: true,
    classification: true,
    nameAtCustomer: true,
    semanticModelId: true,
    semanticDataModel: true,
    manufacturingDate: true,
    manufacturingCountry: true,
    activeAlerts: true,
    activeInvestigations: true,
  };

  private readonly sortableColumnsAsPlanned: Record<string, boolean> = {
    id: true,
    idShort: true,
    name: true,
    manufacturer: true,
    manufacturerPartId: true,
    classification: true,
    semanticDataModel: true,
    semanticModelId: true,
    validityPeriodFrom: true,
    validityPeriodTo: true,
    psFunction: true,
    catenaXSiteId: true,
    functionValidFrom: true,
    functionValidUntil: true,
  };

  private readonly displayedColumnsAsPlannedCustomer: string[] = [
    'Filter',
    'filterSemanticDataModel',
    'filterName',
    'filterManufacturer',
    'filterManufacturerPartId',
    'filterSemanticModelId',
  ];

  private readonly displayedColumnsAsPlannedSupplier: string[] = [
    'Filter',
    'filterSemanticDataModel',
    'filterName',
    'filterManufacturer',
    'filterManufacturerPartId',
    'filterSemanticModelId',
  ];

  private readonly displayedColumnsAsBuiltCustomer: string[] = [
    'Filter',
    'filterSemanticDataModel',
    'filterName',
    'filterManufacturer',
    'filterManufacturerPartId',
    'filterSemanticModelId',
    'filterManufacturingDate',
    'filterActiveAlerts',
    'filterActiveInvestigations',
  ];

  private readonly displayedColumnsAsBuiltSupplier: string[] = [
    'Filter',
    'filterSemanticDataModel',
    'filterName',
    'filterManufacturer',
    'filterManufacturerPartId',
    'filterSemanticModelId',
    'filterManufacturingDate',
    'filterActiveAlerts',
    'filterActiveInvestigations',
  ];

  private readonly displayedColumnsAsBuiltCustomerForTable: string[] = [
    'semanticDataModel',
    'name',
    'manufacturer',
    'manufacturerPartId',
    'semanticModelId',
    'manufacturingDate',
    'activeAlerts',
    'activeInvestigations',
    'menu'
  ];

  private readonly sortableColumnsAsBuiltCustomer: Record<string, boolean> = {
    semanticDataModel: true,
    name: true,
    manufacturer: true,
    manufacturerPartId: true,
    semanticModelId: true,
    manufacturingDate: true,
    activeAlerts: true,
    activeInvestigations: true,
  };

  private readonly displayedColumnsAsPlannedCustomerForTable: string[] = [
    'semanticDataModel',
    'name',
    'manufacturer',
    'manufacturerPartId',
    'semanticModelId',
    'menu'
  ];

  private readonly sortableColumnsAsPlannedCustomer: Record<string, boolean> = {
    semanticDataModel: true,
    name: true,
    manufacturer: true,
    manufacturerPartId: true,
    semanticModelId: true,
    manufacturingDate: true,
  };

  private readonly displayedColumnsAsBuiltSupplierForTable: string[] = [
    'semanticDataModel',
    'name',
    'manufacturer',
    'manufacturerPartId',
    'semanticModelId',
    'manufacturingDate',
    'activeAlerts',
    'activeInvestigations',
    'menu'
  ];

  private readonly sortableColumnsAsBuiltSupplier: Record<string, boolean> = {
    semanticDataModel: true,
    name: true,
    manufacturer: true,
    manufacturerPartId: true,
    semanticModelId: true,
    manufacturingDate: true,
    activeAlerts: true,
    activeInvestigations: true,
  };

  private readonly displayedColumnsAsPlannedSupplierForTable: string[] = [
    'semanticDataModel',
    'name',
    'manufacturer',
    'manufacturerPartId',
    'semanticModelId',
    'menu'
  ];

  private readonly sortableColumnsAsPlannedSupplier: Record<string, boolean> = {
    semanticDataModel: true,
    name: true,
    manufacturer: true,
    manufacturerPartId: true,
    semanticModelId: true,
  };

  private sorting: TableHeaderSort;

  protected readonly Role = Role;

  ngAfterViewInit() {
    this.paginator._intl.itemsPerPageLabel = 'Show';
  }

  public triggerFilterAdding(): void {
    const filterValues: any = { ...this.filterFormGroup.value };
    const selectedSemanticDataModelOptions: string[] = [];
    for (const option of this.semanticDataModelOptions) {
      if (option.checked) {
        selectedSemanticDataModelOptions.push(option.value);
      }
    }
    filterValues['semanticDataModel'] = selectedSemanticDataModelOptions;
    this.filterConfiguration.forEach(filter => {
      if (filter.filterKey !== 'Filter') {
        let filterName: string;
        if (filter.column) {
          filterName = filter.column;
        } else {
          filterName = filter.filterKey;
        }

        this.filterActive[filterName] =
          filterValues[filter.filterKey] !== null && filterValues[filter.filterKey].length !== 0;
      }
    });
    this.filterActivated.emit(filterValues);
  }

  constructor(private readonly tableViewSettingsService: TableSettingsService,
    private dialog: MatDialog,
    private readonly roleService: RoleService,
  ) {

    if (this.roleService.hasAccess([Role.USER])) {
      const selectColumn = 'select';
      this.displayedColumnsAsBuiltSupplierForTable.splice(0, 0, selectColumn);
      this.displayedColumnsAsBuiltForTable.splice(0, 0, selectColumn);
    }
  }

  public defaultColumns: string[];

  private tableViewConfig: TableViewConfig;

  ngOnInit() {
    this.setupFilterConfig();
    this.initializeTableViewSettings();

    this.tableViewSettingsService.getEvent().subscribe(() => {
      this.setupTableViewSettings();
    });
    this.setupTableViewSettings();
  }

  private setupTableConfigurations(
    displayedColumnsForTable: string[],
    displayedColumns: string[],
    sortableColumns: Record<string, boolean>,
    filterConfiguration: FilterConfig[],
    filterFormGroup: any,
  ): any {
    const headerKey = 'table.column';
    this.tableConfig = {
      ...this.tableConfig,
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
    this.filterConfiguration.forEach(filter => {
      if (filter.column) {
        this.filterActive[filter.column] = false;
      } else {
        this.filterActive[filter.filterKey] = false;
      }
    });
  }

  private setupTableViewSettings() {
    const settingsList = this.tableViewSettingsService.getStoredTableSettings();
    // check if there are table settings list
    if (settingsList) {
      // if yes, check if there is a table-setting for this table type
      if (settingsList[this.tableType]) {
        // if yes, get the effective displayedcolumns from the settings and set the tableconfig after it.
        this.setupTableConfigurations(
          settingsList[this.tableType].columnsForTable,
          settingsList[this.tableType].filterColumnsForTable,
          this.tableViewConfig.sortableColumns,
          this.tableViewConfig.filterConfiguration,
          this.tableViewConfig.filterFormGroup,
        );
      } else {
        // if no, create new a table setting for this.tabletype and put it into the list. Additionally, intitialize default table configuration
        settingsList[this.tableType] = this.getSettingsList();
        this.tableViewSettingsService.storeTableSettings(this.tableType, settingsList);
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
      this.tableViewSettingsService.storeTableSettings(this.tableType, newTableSettingsList);
      this.setupTableConfigurations(
        this.tableViewConfig.displayedColumnsForTable,
        this.tableViewConfig.displayedColumns,
        this.tableViewConfig.sortableColumns,
        this.tableViewConfig.filterConfiguration,
        this.tableViewConfig.filterFormGroup,
      );
    }
  }

  private setupFilterConfig() {
    const {
      filter,
      id,
      idShort,
      nameAtManufacturer,
      manufacturerName,
      manufacturerPartId,
      customerPartId,
      classification,
      nameAtCustomer,
      semanticDataModel,
      semanticModelId,
      manufacturingDate,
      manufacturingCountry,
      activeAlerts,
      activeInvestigations,
      validityPeriodFrom,
      validityPeriodTo,
      psFunction,
      catenaXSiteId,
      functionValidFrom,
      functionValidUntil,
    } = this.filterConfigOptions.filterKeyOptionsAssets;

    switch (this.tableType) {
      case PartTableType.AS_PLANNED_CUSTOMER:
        this.assetAsPlannedCustomerFilterConfiguration = [
          filter,
          semanticDataModel,
          nameAtManufacturer,
          manufacturerName,
          manufacturerPartId,
          semanticModelId,
        ];
        break;

      // TODO add other table view configurations when they are implemented
      case PartTableType.AS_ORDERED_OWN:
      case PartTableType.AS_SUPPORTED_OWN:
      case PartTableType.AS_RECYCLED_OWN:
      case PartTableType.AS_DESIGNED_OWN:
      case PartTableType.AS_PLANNED_OWN:
        this.assetAsPlannedFilterConfiguration = [
          filter,
          id,
          idShort,
          nameAtManufacturer,
          manufacturerName,
          manufacturerPartId,
          classification,
          semanticDataModel,
          semanticModelId,
          validityPeriodFrom,
          validityPeriodTo,
          psFunction,
          catenaXSiteId,
          functionValidFrom,
          functionValidUntil,
        ];
        break;
      case PartTableType.AS_PLANNED_SUPPLIER:
        this.assetAsPlannedSupplierFilterConfiguration = [
          filter,
          semanticDataModel,
          nameAtManufacturer,
          manufacturerName,
          manufacturerPartId,
          semanticModelId,
        ];
        break;
      case PartTableType.AS_BUILT_OWN:
        this.assetAsBuiltFilterConfiguration = [
          filter,
          id,
          idShort,
          nameAtManufacturer,
          manufacturerName,
          manufacturerPartId,
          customerPartId,
          classification,
          nameAtCustomer,
          semanticDataModel,
          semanticModelId,
          manufacturingDate,
          manufacturingCountry,
          activeAlerts,
          activeInvestigations,
        ];
        break;
      case PartTableType.AS_BUILT_CUSTOMER:
        this.assetAsBuiltCustomerFilterConfiguration = [
          filter,
          semanticDataModel,
          nameAtManufacturer,
          manufacturerName,
          manufacturerPartId,
          semanticModelId,
          manufacturingDate,
          activeAlerts,
          activeInvestigations,
        ];
        break;
      case PartTableType.AS_BUILT_SUPPLIER:
        this.assetAsBuiltSupplierFilterConfiguration = [
          filter,
          semanticDataModel,
          nameAtManufacturer,
          manufacturerName,
          manufacturerPartId,
          semanticModelId,
          manufacturingDate,
          activeAlerts,
          activeInvestigations,
        ];
        break;
    }
  }

  private getSettingsList(): any {
    return {
      columnsForDialog: this.tableViewConfig.displayedColumnsForTable,
      columnSettingsOptions: this.getDefaultColumnVisibilityMap(),
      columnsForTable: this.tableViewConfig.displayedColumnsForTable,
      filterColumnsForTable: this.tableViewConfig.displayedColumns,
    };
  }

  private getDefaultColumnVisibilityMap(): Map<string, boolean> {
    const initialColumnMap = new Map<string, boolean>();
    for (const column of this.tableViewConfig.displayedColumnsForTable) {
      initialColumnMap.set(column, true);
    }
    return initialColumnMap;
  }

  private initializeTableViewSettings(): void {
    switch (this.tableType) {
      case PartTableType.AS_PLANNED_CUSTOMER:
        this.tableViewConfig = {
          displayedColumns: this.displayedColumnsAsPlannedCustomer,
          displayedColumnsForTable: this.displayedColumnsAsPlannedCustomerForTable,
          filterConfiguration: this.assetAsPlannedCustomerFilterConfiguration,
          filterFormGroup: this.assetAsPlannedCustomerFilterFormGroup,
          sortableColumns: this.sortableColumnsAsPlannedCustomer,
        };
        break;

      // TODO add other table view configurations when they are implemented
      case PartTableType.AS_ORDERED_OWN:
      case PartTableType.AS_SUPPORTED_OWN:
      case PartTableType.AS_RECYCLED_OWN:
      case PartTableType.AS_DESIGNED_OWN:
      case PartTableType.AS_PLANNED_OWN:
        this.tableViewConfig = {
          displayedColumns: this.displayedColumnsAsPlanned,
          displayedColumnsForTable: this.displayedColumnsAsPlannedForTable,
          filterConfiguration: this.assetAsPlannedFilterConfiguration,
          filterFormGroup: this.assetAsPlannedFilterFormGroup,
          sortableColumns: this.sortableColumnsAsPlanned,
        };
        break;
      case PartTableType.AS_PLANNED_SUPPLIER:
        this.tableViewConfig = {
          displayedColumns: this.displayedColumnsAsPlannedSupplier,
          displayedColumnsForTable: this.displayedColumnsAsPlannedSupplierForTable,
          filterConfiguration: this.assetAsPlannedSupplierFilterConfiguration,
          filterFormGroup: this.assetAsPlannedSupplierFilterFormGroup,
          sortableColumns: this.sortableColumnsAsPlannedSupplier,
        };
        break;
      case PartTableType.AS_BUILT_OWN:
        this.tableViewConfig = {
          displayedColumns: this.displayedColumnsAsBuilt,
          displayedColumnsForTable: this.displayedColumnsAsBuiltForTable,
          filterConfiguration: this.assetAsBuiltFilterConfiguration,
          filterFormGroup: this.assetAsBuiltFilterFormGroup,
          sortableColumns: this.sortableColumnsAsBuilt,
        };
        break;
      case PartTableType.AS_BUILT_CUSTOMER:
        this.tableViewConfig = {
          displayedColumns: this.displayedColumnsAsBuiltCustomer,
          displayedColumnsForTable: this.displayedColumnsAsBuiltCustomerForTable,
          filterConfiguration: this.assetAsBuiltCustomerFilterConfiguration,
          filterFormGroup: this.assetAsBuiltCustomerFilterFormGroup,
          sortableColumns: this.sortableColumnsAsBuiltCustomer,
        };
        break;
      case PartTableType.AS_BUILT_SUPPLIER:
        this.tableViewConfig = {
          displayedColumns: this.displayedColumnsAsBuiltSupplier,
          displayedColumnsForTable: this.displayedColumnsAsBuiltSupplierForTable,
          filterConfiguration: this.assetAsBuiltSupplierFilterConfiguration,
          filterFormGroup: this.assetAsBuiltSupplierFilterFormGroup,
          sortableColumns: this.sortableColumnsAsBuiltSupplier,
        };
        break;
    }
  }

  assetAsBuiltFilterFormGroup = {
    id: new FormControl([]),
    idShort: new FormControl([]),
    nameAtManufacturer: new FormControl([]),
    manufacturerName: new FormControl([]),
    manufacturerPartId: new FormControl([]),
    customerPartId: new FormControl([]),
    classification: new FormControl([]),
    nameAtCustomer: new FormControl([]),
    semanticModelId: new FormControl([]),
    semanticDataModel: new FormControl([]),
    manufacturingDate: new FormControl([]),
    manufacturingCountry: new FormControl([]),
    qualityAlertsInStatusActive: new FormControl([]),
    qualityInvestigationsInStatusActive: new FormControl([]),
  };

  assetAsPlannedFilterFormGroup = {
    id: new FormControl([]),
    idShort: new FormControl([]),
    nameAtManufacturer: new FormControl([]),
    manufacturerName: new FormControl([]),
    manufacturerPartId: new FormControl([]),
    classification: new FormControl([]),
    semanticDataModel: new FormControl([]),
    semanticModelId: new FormControl([]),
    validityPeriodFrom: new FormControl([]),
    validityPeriodTo: new FormControl([]),
    function: new FormControl([]),
    catenaXSiteId: new FormControl([]),
    functionValidFrom: new FormControl([]),
    functionValidUntil: new FormControl([]),
  };

  assetAsPlannedSupplierFilterFormGroup = {
    select: new FormControl([]),
    semanticDataModel: new FormControl([]),
    nameAtManufacturer: new FormControl([]),
    manufacturerName: new FormControl([]),
    manufacturerPartId: new FormControl([]),
    semanticModelId: new FormControl([]),
  };

  assetAsPlannedCustomerFilterFormGroup = {
    select: new FormControl([]),
    semanticDataModel: new FormControl([]),
    nameAtManufacturer: new FormControl([]),
    manufacturerName: new FormControl([]),
    semanticModelId: new FormControl([]),
    manufacturerPartId: new FormControl([]),
  };

  assetAsBuiltSupplierFilterFormGroup = {
    select: new FormControl([]),
    semanticDataModel: new FormControl([]),
    nameAtManufacturer: new FormControl([]),
    manufacturerName: new FormControl([]),
    manufacturerPartId: new FormControl([]),
    semanticModelId: new FormControl([]),
    manufacturingDate: new FormControl([]),
    qualityAlertsInStatusActive: new FormControl([]),
    qualityInvestigationsInStatusActive: new FormControl([]),
  };

  assetAsBuiltCustomerFilterFormGroup = {
    select: new FormControl([]),
    semanticDataModel: new FormControl([]),
    nameAtManufacturer: new FormControl([]),
    manufacturerName: new FormControl([]),
    manufacturerPartId: new FormControl([]),
    semanticModelId: new FormControl([]),
    manufacturingDate: new FormControl([]),
    qualityAlertsInStatusActive: new FormControl([]),
    qualityInvestigationsInStatusActive: new FormControl([]),
  };

  private assetAsBuiltFilterConfiguration: FilterConfig[];

  private assetAsPlannedCustomerFilterConfiguration: FilterConfig[];

  private assetAsPlannedSupplierFilterConfiguration: FilterConfig[];

  private assetAsBuiltCustomerFilterConfiguration: FilterConfig[];

  private assetAsBuiltSupplierFilterConfiguration: FilterConfig[];

  private assetAsPlannedFilterConfiguration: FilterConfig[];

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

  public resetFilterActive(): void {
    this.filterConfiguration.forEach(filter => {
      if (filter.column) {
        this.filterActive[filter.column] = false;
      } else {
        this.filterActive[filter.filterKey] = false;
      }
    });
  }
}
