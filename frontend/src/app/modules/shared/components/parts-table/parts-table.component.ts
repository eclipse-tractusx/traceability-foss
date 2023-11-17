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
  ViewEncapsulation,
} from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Pagination } from '@core/model/pagination.model';
import { SemanticDataModel } from '@page/parts/model/parts.model';
import { MultiSelectAutocompleteComponent } from '@shared/components/multi-select-autocomplete/multi-select-autocomplete.component';
import {
  CreateHeaderFromColumns,
  PartTableType,
  SortingOptions,
  TableConfig,
  TableEventConfig,
  TableHeaderSort,
} from '@shared/components/table/table.model';
import { addSelectedValues, removeSelectedValues } from '@shared/helper/table-helper';
import { isDateFilter } from '@shared/helper/filter-helper';
import i18next from 'i18next';

@Component({
  selector: 'app-parts-table',
  templateUrl: './parts-table.component.html',
  styleUrls: ['parts-table.component.scss'],
  encapsulation: ViewEncapsulation.None,
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
  @Input() showAlertButton = false;

  @Input() selectedPartsInfoLabel: string;
  @Input() selectedPartsActionLabel: string;

  @Input() tableHeader: string;
  @Input() multiSortList: TableHeaderSort[];

  @Input() tableType: PartTableType;

  public tableConfig: TableConfig;

  filterKey = 'Filter';

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
  @Output() filterActivated = new EventEmitter<any>();

  public readonly dataSource = new MatTableDataSource<unknown>();
  public readonly selection = new SelectionModel<unknown>(true, []);

  optionTextSearch = [];
  semanticDataModelOptions = [
    {
      display: 'semanticDataModels.' + SemanticDataModel.BATCH,
      value: SemanticDataModel.BATCH,
      checked: false,
    },
    {
      display: 'semanticDataModels.' + SemanticDataModel.JUSTINSEQUENCE,
      value: SemanticDataModel.JUSTINSEQUENCE,
      checked: false,
    },
    {
      display: 'semanticDataModels.' + SemanticDataModel.SERIALPART,
      value: SemanticDataModel.SERIALPART,
      checked: false,
    },
    {
      display: 'semanticDataModels.' + SemanticDataModel.UNKNOWN,
      value: SemanticDataModel.UNKNOWN,
      checked: false,
    },
    {
      display: 'semanticDataModels.' + SemanticDataModel.PARTASPLANNED,
      value: SemanticDataModel.PARTASPLANNED,
      checked: false,
    },
  ];

  public filterKeyOptions = {
    filter: { filterKey: 'Filter', headerKey: 'Filter', isTextSearch: true, option: this.optionTextSearch },
    id: { filterKey: 'id', headerKey: 'filterId', isTextSearch: true, option: this.optionTextSearch },
    idShort: { filterKey: 'idShort', headerKey: 'filterIdShort', isTextSearch: true, option: this.optionTextSearch },
    name: { filterKey: 'name', headerKey: 'filterName', isTextSearch: true, option: this.optionTextSearch },
    nameAtManufacturer: {
      filterKey: 'nameAtManufacturer',
      headerKey: 'filterName',
      isTextSearch: true,
      option: this.optionTextSearch,
    },
    manufacturer: {
      filterKey: 'manufacturer',
      headerKey: 'filterManufacturer',
      isTextSearch: true,
      option: this.optionTextSearch,
    },
    manufacturerName: {
      filterKey: 'manufacturerName',
      headerKey: 'filterManufacturer',
      isTextSearch: true,
      option: this.optionTextSearch,
    },
    partId: { filterKey: 'partId', headerKey: 'filterPartId', isTextSearch: true, option: this.optionTextSearch }, // Part number / Batch Number / JIS Number
    manufacturerPartId: {
      filterKey: 'manufacturerPartId',
      headerKey: 'filterManufacturerPartId',
      isTextSearch: true,
      option: this.optionTextSearch,
    },
    customerPartId: {
      filterKey: 'customerPartId',
      headerKey: 'filterCustomerPartId',
      isTextSearch: true,
      option: this.optionTextSearch,
    }, // --> semanticModel.customerPartId
    classification: {
      filterKey: 'classification',
      headerKey: 'filterClassification',
      isTextSearch: true,
      option: this.optionTextSearch,
    },
    nameAtCustomer: {
      filterKey: 'nameAtCustomer',
      headerKey: 'filterNameAtCustomer',
      isTextSearch: true,
      option: this.optionTextSearch,
    }, // --> semanticModel.nameAtCustomer
    semanticModelId: {
      filterKey: 'semanticModelId',
      headerKey: 'filterSemanticModelId',
      isTextSearch: true,
      option: this.optionTextSearch,
    },
    semanticDataModel: {
      filterKey: 'semanticDataModel',
      headerKey: 'filterSemanticDataModel',
      isTextSearch: false,
      option: this.semanticDataModelOptions,
    },
    manufacturingDate: {
      filterKey: 'manufacturingDate',
      headerKey: 'filterManufacturingDate',
      isTextSearch: false,
      isDate: true,
      option: this.optionTextSearch,
    },
    manufacturingCountry: {
      filterKey: 'manufacturingCountry',
      headerKey: 'filterManufacturingCountry',
      isTextSearch: true,
      option: this.optionTextSearch,
    },
    activeAlerts: {
      filterKey: 'activeAlerts',
      headerKey: 'filterActiveAlerts',
      isTextSearch: true,
      option: this.optionTextSearch,
    },
    activeInvestigations: {
      filterKey: 'activeInvestigations',
      headerKey: 'filterActiveInvestigations',
      isTextSearch: true,
      option: this.optionTextSearch,
    },
    validityPeriodFrom: {
      filterKey: 'validityPeriodFrom',
      headerKey: 'filterValidityPeriodFrom',
      isTextSearch: false,
      isDate: true,
      option: this.optionTextSearch,
    },
    validityPeriodTo: {
      filterKey: 'validityPeriodTo',
      headerKey: 'filterValidityPeriodTo',
      isTextSearch: false,
      isDate: true,
      option: this.optionTextSearch,
    },
    psFunction: {
      filterKey: 'psFunction',
      headerKey: 'filterPsFunction',
      isTextSearch: true,
      option: this.optionTextSearch,
    },
    catenaXSiteId: {
      filterKey: 'catenaXSiteId',
      headerKey: 'filterCatenaXSiteId',
      isTextSearch: true,
      option: this.optionTextSearch,
    },
    functionValidFrom: {
      filterKey: 'functionValidFrom',
      headerKey: 'filterFunctionValidFrom',
      isTextSearch: false,
      isDate: true,
      option: this.optionTextSearch,
    },
    functionValidUntil: {
      filterKey: 'functionValidUntil',
      headerKey: 'filterFunctionValidUntil',
      isTextSearch: false,
      isDate: true,
      option: this.optionTextSearch,
    },
  };

  public totalItems: number;
  public pageIndex: number;
  public isDataLoading: boolean;
  public selectedRow: Record<string, unknown>;
  public isMenuOpen: boolean;
  public displayedFilter: boolean;

  public filterConfiguration: any[];
  public displayedColumns: string[];

  public sortingEvent: Record<string, SortingOptions> = {};

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
    'filterPartId', // Part number / Batch Number / JIS Number
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
    'select',
    '!',
    'id',
    'idShort',
    'name',
    'manufacturer',
    'partId',
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
  ];

  private readonly displayedColumnsAsPlannedForTable: string[] = [
    'select',
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
  ];

  private readonly sortableColumnsAsBuilt: Record<string, boolean> = {
    id: true,
    idShort: true,
    name: true,
    manufacturer: true,
    partId: true,
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
    'filterPartId',
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
    'filterPartId',
    'filterSemanticModelId',
    'filterManufacturingDate',
    'filterActiveAlerts',
    'filterActiveInvestigations',
  ];

  private readonly displayedColumnsAsBuiltCustomerForTable: string[] = [
    'select',
    'semanticDataModel',
    'name',
    'manufacturer',
    'partId',
    'semanticModelId',
    'manufacturingDate',
    'activeAlerts',
    'activeInvestigations',
  ];

  private readonly sortableColumnsAsBuiltCustomer: Record<string, boolean> = {
    semanticDataModel: true,
    name: true,
    manufacturer: true,
    partId: true,
    semanticModelId: true,
    manufacturingDate: true,
    activeAlerts: true,
    activeInvestigations: true,
  };

  private readonly displayedColumnsAsPlannedCustomerForTable: string[] = [
    'select',
    'semanticDataModel',
    'name',
    'manufacturer',
    'manufacturerPartId',
    'semanticModelId',
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
    'select',
    'semanticDataModel',
    'name',
    'manufacturer',
    'partId',
    'semanticModelId',
    'manufacturingDate',
    'activeAlerts',
    'activeInvestigations',
  ];

  private readonly sortableColumnsAsBuiltSupplier: Record<string, boolean> = {
    semanticDataModel: true,
    name: true,
    manufacturer: true,
    partId: true,
    semanticModelId: true,
    manufacturingDate: true,
    activeAlerts: true,
    activeInvestigations: true,
  };

  private readonly displayedColumnsAsPlannedSupplierForTable: string[] = [
    'select',
    'semanticDataModel',
    'name',
    'manufacturer',
    'manufacturerPartId',
    'semanticModelId',
  ];

  private readonly sortableColumnsAsPlannedSupplier: Record<string, boolean> = {
    semanticDataModel: true,
    name: true,
    manufacturer: true,
    manufacturerPartId: true,
    semanticModelId: true,
  };

  private pageSize: number;
  private sorting: TableHeaderSort;

  ngOnInit() {
    this.handleAsBuiltTableType();
    this.handleAsPlannedTableType();
    this.handleAsDesignedTableType();
    this.handleAsOrderedTableType();
    this.handleAsRecycledTableType();
    this.handleAsSupportedTableType();
    if (this.tableConfig.sortableColumns) {
      this.setupSortingEvent();
    }
  }

  ngAfterViewInit() {
    this.paginator._intl.itemsPerPageLabel = 'Show';
  }

  private setupSortingEvent(): void {
    const sortingNames = Object.keys(this.tableConfig.sortableColumns);
    sortingNames.forEach(sortName => (this.sortingEvent[sortName] = SortingOptions.NONE));
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
    this.filterActivated.emit(filterValues);
  }

  private handleAsPlannedTableType(): void {
    switch (this.tableType) {
      case PartTableType.AS_PLANNED_CUSTOMER:
        this.setupTableConfigurations(
          this.displayedColumnsAsPlannedCustomerForTable,
          this.displayedColumnsAsPlannedCustomer,
          this.sortableColumnsAsPlannedCustomer,
          this.assetAsPlannedCustomerFilterConfiguration,
          this.assetAsPlannedCustomerFilterFormGroup,
        );
        break;
      case PartTableType.AS_PLANNED_OWN:
        this.setupTableConfigurations(
          this.displayedColumnsAsPlannedForTable,
          this.displayedColumnsAsPlanned,
          this.sortableColumnsAsPlanned,
          this.assetAsPlannedFilterConfiguration,
          this.assetAsPlannedFilterFormGroup,
        );
        break;
      case PartTableType.AS_PLANNED_SUPPLIER:
        this.setupTableConfigurations(
          this.displayedColumnsAsPlannedSupplierForTable,
          this.displayedColumnsAsPlannedSupplier,
          this.sortableColumnsAsPlannedSupplier,
          this.assetAsPlannedSupplierFilterConfiguration,
          this.assetAsPlannedSupplierFilterFormGroup,
        );
        break;
    }
  }

  public getTooltip(column: string) {
    return column === '!'
      ? i18next.t('parts.openInvestigations')
      : 'First click: sort in ascending order ↑ Second click: sort in descending order ↓ Third click:  reset sorting';
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
      displayedColumns: displayedColumnsForTable,
      header: CreateHeaderFromColumns(displayedColumnsForTable, headerKey),
      sortableColumns: sortableColumns,
    };
    this.filterConfiguration = filterConfiguration;
    this.displayedColumns = displayedColumns;
    for (const controlName in filterFormGroup) {
      if (filterFormGroup.hasOwnProperty(controlName)) {
        this.filterFormGroup.addControl(controlName, filterFormGroup[controlName]);
      }
    }
  }

  private handleAsBuiltTableType(): void {
    switch (this.tableType) {
      case PartTableType.AS_BUILT_OWN:
        this.setupTableConfigurations(
          this.displayedColumnsAsBuiltForTable,
          this.displayedColumnsAsBuilt,
          this.sortableColumnsAsBuilt,
          this.assetAsBuiltFilterConfiguration,
          this.assetAsBuiltFilterFormGroup,
        );
        break;
      case PartTableType.AS_BUILT_CUSTOMER:
        this.setupTableConfigurations(
          this.displayedColumnsAsBuiltCustomerForTable,
          this.displayedColumnsAsBuiltCustomer,
          this.sortableColumnsAsBuiltCustomer,
          this.assetAsBuiltCustomerFilterConfiguration,
          this.assetAsBuiltCustomerFilterFormGroup,
        );
        break;
      case PartTableType.AS_BUILT_SUPPLIER:
        this.setupTableConfigurations(
          this.displayedColumnsAsBuiltSupplierForTable,
          this.displayedColumnsAsBuiltSupplier,
          this.sortableColumnsAsBuiltSupplier,
          this.assetAsBuiltSupplierFilterConfiguration,
          this.assetAsBuiltSupplierFilterFormGroup,
        );
        break;
    }
  }

  private handleAsOrderedTableType(): void {
    switch (this.tableType) {
      case PartTableType.AS_ORDERED_CUSTOMER:
        this.setupTableConfigurations(
          this.displayedColumnsAsPlannedCustomerForTable,
          this.displayedColumnsAsPlannedCustomer,
          this.sortableColumnsAsPlannedCustomer,
          this.assetAsPlannedCustomerFilterConfiguration,
          this.assetAsPlannedCustomerFilterFormGroup,
        );
        break;
      case PartTableType.AS_ORDERED_OWN:
        this.setupTableConfigurations(
          this.displayedColumnsAsPlannedForTable,
          this.displayedColumnsAsPlanned,
          this.sortableColumnsAsPlanned,
          this.assetAsPlannedFilterConfiguration,
          this.assetAsPlannedFilterFormGroup,
        );
        break;
      case PartTableType.AS_ORDERED_SUPPLIER:
        this.setupTableConfigurations(
          this.displayedColumnsAsPlannedSupplierForTable,
          this.displayedColumnsAsPlannedSupplier,
          this.sortableColumnsAsPlannedSupplier,
          this.assetAsPlannedSupplierFilterConfiguration,
          this.assetAsPlannedSupplierFilterFormGroup,
        );
        break;
    }
  }

  private handleAsDesignedTableType(): void {
    switch (this.tableType) {
      case PartTableType.AS_DESIGNED_CUSTOMER:
        this.setupTableConfigurations(
          this.displayedColumnsAsPlannedCustomerForTable,
          this.displayedColumnsAsPlannedCustomer,
          this.sortableColumnsAsPlannedCustomer,
          this.assetAsPlannedCustomerFilterConfiguration,
          this.assetAsPlannedCustomerFilterFormGroup,
        );
        break;
      case PartTableType.AS_DESIGNED_OWN:
        this.setupTableConfigurations(
          this.displayedColumnsAsPlannedForTable,
          this.displayedColumnsAsPlanned,
          this.sortableColumnsAsPlanned,
          this.assetAsPlannedFilterConfiguration,
          this.assetAsPlannedFilterFormGroup,
        );
        break;
      case PartTableType.AS_DESIGNED_SUPPLIER:
        this.setupTableConfigurations(
          this.displayedColumnsAsPlannedSupplierForTable,
          this.displayedColumnsAsPlannedSupplier,
          this.sortableColumnsAsPlannedSupplier,
          this.assetAsPlannedSupplierFilterConfiguration,
          this.assetAsPlannedSupplierFilterFormGroup,
        );
        break;
    }
  }

  private handleAsSupportedTableType(): void {
    switch (this.tableType) {
      case PartTableType.AS_SUPPORTED_CUSTOMER:
        this.setupTableConfigurations(
          this.displayedColumnsAsPlannedCustomerForTable,
          this.displayedColumnsAsPlannedCustomer,
          this.sortableColumnsAsPlannedCustomer,
          this.assetAsPlannedCustomerFilterConfiguration,
          this.assetAsPlannedCustomerFilterFormGroup,
        );
        break;
      case PartTableType.AS_SUPPORTED_OWN:
        this.setupTableConfigurations(
          this.displayedColumnsAsPlannedForTable,
          this.displayedColumnsAsPlanned,
          this.sortableColumnsAsPlanned,
          this.assetAsPlannedFilterConfiguration,
          this.assetAsPlannedFilterFormGroup,
        );
        break;
      case PartTableType.AS_SUPPORTED_SUPPLIER:
        this.setupTableConfigurations(
          this.displayedColumnsAsPlannedSupplierForTable,
          this.displayedColumnsAsPlannedSupplier,
          this.sortableColumnsAsPlannedSupplier,
          this.assetAsPlannedSupplierFilterConfiguration,
          this.assetAsPlannedSupplierFilterFormGroup,
        );
        break;
    }
  }

  private handleAsRecycledTableType(): void {
    switch (this.tableType) {
      case PartTableType.AS_RECYCLED_CUSTOMER:
        this.setupTableConfigurations(
          this.displayedColumnsAsPlannedCustomerForTable,
          this.displayedColumnsAsPlannedCustomer,
          this.sortableColumnsAsPlannedCustomer,
          this.assetAsPlannedCustomerFilterConfiguration,
          this.assetAsPlannedCustomerFilterFormGroup,
        );
        break;
      case PartTableType.AS_RECYCLED_OWN:
        this.setupTableConfigurations(
          this.displayedColumnsAsPlannedForTable,
          this.displayedColumnsAsPlanned,
          this.sortableColumnsAsPlanned,
          this.assetAsPlannedFilterConfiguration,
          this.assetAsPlannedFilterFormGroup,
        );
        break;
      case PartTableType.AS_RECYCLED_SUPPLIER:
        this.setupTableConfigurations(
          this.displayedColumnsAsPlannedSupplierForTable,
          this.displayedColumnsAsPlannedSupplier,
          this.sortableColumnsAsPlannedSupplier,
          this.assetAsPlannedSupplierFilterConfiguration,
          this.assetAsPlannedSupplierFilterFormGroup,
        );
        break;
    }
  }

  public readonly assetAsBuiltFilterConfiguration: any[] = [
    this.filterKeyOptions.filter,
    this.filterKeyOptions.id,
    this.filterKeyOptions.idShort,
    this.filterKeyOptions.name,
    this.filterKeyOptions.manufacturer,
    this.filterKeyOptions.partId,
    this.filterKeyOptions.manufacturerPartId,
    this.filterKeyOptions.customerPartId,
    this.filterKeyOptions.classification,
    this.filterKeyOptions.nameAtCustomer,
    this.filterKeyOptions.semanticModelId,
    this.filterKeyOptions.semanticDataModel,
    this.filterKeyOptions.manufacturingDate,
    this.filterKeyOptions.manufacturingCountry,
    this.filterKeyOptions.activeAlerts,
    this.filterKeyOptions.activeInvestigations,
  ];

  assetAsBuiltFilterFormGroup = {
    id: new FormControl([]),
    idShort: new FormControl([]),
    name: new FormControl([]),
    manufacturer: new FormControl([]),
    partId: new FormControl([]),
    manufacturerPartId: new FormControl([]),
    customerPartId: new FormControl([]),
    classification: new FormControl([]),
    nameAtCustomer: new FormControl([]),
    semanticModelId: new FormControl([]),
    semanticDataModel: new FormControl([]),
    manufacturingDate: new FormControl([]),
    manufacturingCountry: new FormControl([]),
    activeAlerts: new FormControl([]),
    activeInvestigations: new FormControl([]),
  };

  assetAsPlannedFilterFormGroup = {
    id: new FormControl([]),
    idShort: new FormControl([]),
    name: new FormControl([]),
    manufacturer: new FormControl([]),
    manufacturerPartId: new FormControl([]),
    classification: new FormControl([]),
    semanticDataModel: new FormControl([]),
    semanticModelId: new FormControl([]),
    validityPeriodFrom: new FormControl([]),
    validityPeriodTo: new FormControl([]),
    psFunction: new FormControl([]),
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
    activeAlerts: new FormControl([]),
    activeInvestigations: new FormControl([]),
  };

  assetAsBuiltCustomerFilterFormGroup = {
    select: new FormControl([]),
    semanticDataModel: new FormControl([]),
    nameAtManufacturer: new FormControl([]),
    manufacturerName: new FormControl([]),
    manufacturerPartId: new FormControl([]),
    semanticModelId: new FormControl([]),
    manufacturingDate: new FormControl([]),
    activeAlerts: new FormControl([]),
    activeInvestigations: new FormControl([]),
  };

  private readonly assetAsPlannedCustomerFilterConfiguration: any[] = [
    this.filterKeyOptions.filter,
    this.filterKeyOptions.semanticDataModel,
    this.filterKeyOptions.nameAtManufacturer,
    this.filterKeyOptions.manufacturerName,
    this.filterKeyOptions.manufacturerPartId,
    this.filterKeyOptions.semanticModelId,
  ];

  private readonly assetAsPlannedSupplierFilterConfiguration: any[] = [
    this.filterKeyOptions.filter,
    this.filterKeyOptions.semanticDataModel,
    this.filterKeyOptions.nameAtManufacturer,
    this.filterKeyOptions.manufacturerName,
    this.filterKeyOptions.manufacturerPartId,
    this.filterKeyOptions.semanticModelId,
  ];

  private readonly assetAsBuiltCustomerFilterConfiguration: any[] = [
    this.filterKeyOptions.filter,
    this.filterKeyOptions.semanticDataModel,
    this.filterKeyOptions.nameAtManufacturer,
    this.filterKeyOptions.manufacturerName,
    this.filterKeyOptions.manufacturerPartId,
    this.filterKeyOptions.semanticModelId,
    this.filterKeyOptions.manufacturingDate,
    this.filterKeyOptions.activeAlerts,
    this.filterKeyOptions.activeInvestigations,
  ];

  private readonly assetAsBuiltSupplierFilterConfiguration: any[] = [
    this.filterKeyOptions.filter,
    this.filterKeyOptions.semanticDataModel,
    this.filterKeyOptions.nameAtManufacturer,
    this.filterKeyOptions.manufacturerName,
    this.filterKeyOptions.manufacturerPartId,
    this.filterKeyOptions.semanticModelId,
    this.filterKeyOptions.manufacturingDate,
    this.filterKeyOptions.activeAlerts,
    this.filterKeyOptions.activeInvestigations,
  ];

  private readonly assetAsPlannedFilterConfiguration: any[] = [
    this.filterKeyOptions.filter,
    this.filterKeyOptions.id,
    this.filterKeyOptions.idShort,
    this.filterKeyOptions.name,
    this.filterKeyOptions.manufacturer,
    this.filterKeyOptions.manufacturerPartId,
    this.filterKeyOptions.classification,
    this.filterKeyOptions.semanticDataModel,
    this.filterKeyOptions.semanticModelId,
    this.filterKeyOptions.validityPeriodFrom,
    this.filterKeyOptions.validityPeriodTo,
    this.filterKeyOptions.psFunction,
    this.filterKeyOptions.catenaXSiteId,
    this.filterKeyOptions.functionValidFrom,
    this.filterKeyOptions.functionValidUntil,
  ];

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

    if (!this.tableConfig.menuActionsConfig) {
      this.selected.emit(row);
    }
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

  private addSelectedValues(newData: unknown[]): void {
    addSelectedValues(this.selection, newData);
  }

  private removeSelectedValues(itemsToRemove: unknown[]): void {
    removeSelectedValues(this.selection, itemsToRemove);
  }
}
