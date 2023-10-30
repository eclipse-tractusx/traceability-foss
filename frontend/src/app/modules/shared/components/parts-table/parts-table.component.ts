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
import { MenuStack } from '@angular/cdk/menu';
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
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatSort, Sort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Pagination } from '@core/model/pagination.model';
import { TableSettingsService } from '@core/user/table-settings.service';
import { SemanticDataModel } from '@page/parts/model/parts.model';
import { MultiSelectAutocompleteComponent } from '@shared/components/multi-select-autocomplete/multi-select-autocomplete.component';
import { TableViewConfig } from '@shared/components/parts-table/table-view-config.model';
import { TableSettingsComponent } from '@shared/components/table-settings/table-settings.component';
import {
  CreateHeaderFromColumns,
  PartTableType,
  TableConfig,
  TableEventConfig,
  TableHeaderSort,
} from '@shared/components/table/table.model';
import { isDateFilter } from '@shared/helper/filter-helper';
import { addSelectedValues, removeSelectedValues } from '@shared/helper/table-helper';


@Component({
    selector: 'app-parts-table',
    templateUrl: './parts-table.component.html',
    styleUrls: ['parts-table.component.scss']
})
export class PartsTableComponent implements OnInit {
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild('tableElement', {read: ElementRef}) tableElementRef: ElementRef<HTMLElement>;
    @ViewChildren(MultiSelectAutocompleteComponent) multiSelectAutocompleteComponents: QueryList<MultiSelectAutocompleteComponent>;
    @Input() multiSelectActive = false;

    @Input() labelId: string;
    @Input() noShadow = false;
    @Input() showHover = true;

    @Input() selectedPartsInfoLabel: string;
    @Input() selectedPartsActionLabel: string;

    @Input() tableHeader: string;
    @Input() multiSortList: TableHeaderSort[];

    @Input() tableType: PartTableType;

    public tableConfig: TableConfig;

    filterKey = 'Filter';

    @Input() set paginationData({page, pageSize, totalItems, content}: Pagination<unknown>) {
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

    constructor(private readonly tableSettingsService: TableSettingsService, private dialog: MatDialog) {}


    public readonly dataSource = new MatTableDataSource<unknown>();
    public readonly selection = new SelectionModel<unknown>(true, []);

    public totalItems: number;
    public pageIndex: number;
    public isDataLoading: boolean;
    public selectedRow: Record<string, unknown>;
    public isMenuOpen: boolean;
    public displayedFilter: boolean;

    public filterConfiguration: any[];
    public displayedColumns: string[];
    public defaultColumns: string[];

    private tableViewConfig: TableViewConfig

  filterFormGroup = new FormGroup({});

    public isDateElement(key: string){
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
      'menu'
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
      'menu'
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
      'menu'
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
      this.initializeTableViewSettings();
      this.tableSettingsService.getEvent().subscribe(() => {
        this.setupTableViewSettings();
      })
      this.setupTableViewSettings();
        this.filterFormGroup.valueChanges.subscribe((formValues) => {
            this.filterActivated.emit(formValues);
        });
    }

  private initializeTableViewSettings(): void {
    switch (this.tableType) {
      case PartTableType.AS_PLANNED_CUSTOMER:
        this.tableViewConfig = {
          displayedColumns: this.displayedColumnsAsPlannedCustomer,
          displayedColumnsForTable: this.displayedColumnsAsPlannedCustomerForTable,
          filterConfiguration: this.assetAsPlannedCustomerFilterConfiguration,
          filterFormGroup: this.assetAsPlannedCustomerFilterFormGroup,
          sortableColumns: this.sortableColumnsAsPlannedCustomer
        }
        break;
      case PartTableType.AS_PLANNED_OWN:
        this.tableViewConfig = {
          displayedColumns: this.displayedColumnsAsPlanned,
          displayedColumnsForTable: this.displayedColumnsAsPlannedForTable,
          filterConfiguration: this.assetAsPlannedFilterConfiguration,
          filterFormGroup: this.assetAsPlannedFilterFormGroup,
          sortableColumns: this.sortableColumnsAsPlanned
        }
        break;
      case PartTableType.AS_PLANNED_SUPPLIER:
        this.tableViewConfig = {
          displayedColumns: this.displayedColumnsAsPlannedSupplier,
          displayedColumnsForTable: this.displayedColumnsAsPlannedSupplierForTable,
          filterConfiguration: this.assetAsPlannedSupplierFilterConfiguration,
          filterFormGroup: this.assetAsPlannedSupplierFilterFormGroup,
          sortableColumns: this.sortableColumnsAsPlannedSupplier
        }
        break;
      case PartTableType.AS_BUILT_OWN:
        this.tableViewConfig = {
          displayedColumns: this.displayedColumnsAsBuilt,
          displayedColumnsForTable: this.displayedColumnsAsBuiltForTable,
          filterConfiguration: this.assetAsBuiltFilterConfiguration,
          filterFormGroup: this.assetAsBuiltFilterFormGroup,
          sortableColumns: this.sortableColumnsAsBuilt
        }
        break;
      case PartTableType.AS_BUILT_CUSTOMER:
        this.tableViewConfig = {
          displayedColumns: this.displayedColumnsAsBuiltCustomer,
          displayedColumnsForTable: this.displayedColumnsAsBuiltCustomerForTable,
          filterConfiguration: this.assetAsBuiltCustomerFilterConfiguration,
          filterFormGroup: this.assetAsBuiltCustomerFilterFormGroup,
          sortableColumns: this.sortableColumnsAsBuiltCustomer
        }
        break;
      case PartTableType.AS_BUILT_SUPPLIER:
        this.tableViewConfig = {
          displayedColumns: this.displayedColumnsAsBuiltSupplier,
          displayedColumnsForTable: this.displayedColumnsAsBuiltSupplierForTable,
          filterConfiguration: this.assetAsBuiltSupplierFilterConfiguration,
          filterFormGroup: this.assetAsBuiltSupplierFilterFormGroup,
          sortableColumns: this.sortableColumnsAsBuiltSupplier
        }
        break;
    }
  }

    private setupTableViewSettings() {
      const tableSettingsList = this.tableSettingsService.getStoredTableSettings();
      // check if there are table settings list
      if(tableSettingsList) {
        // if yes, check if there is a table-setting for this table type
        if(tableSettingsList[this.tableType]) {
          // if yes, get the effective displayedcolumns from the settings and set the tableconfig after it.
          this.setupTableConfigurations(tableSettingsList[this.tableType].columnsForTable, tableSettingsList[this.tableType].filterColumnsForTable, this.tableViewConfig.sortableColumns, this.tableViewConfig.filterConfiguration, this.tableViewConfig.filterFormGroup);
        } else {
          // if no, create new a table setting for this.tabletype and put it into the list. Additionally, intitialize default table configuration
          tableSettingsList[this.tableType] = {
            columnsForDialog: this.tableViewConfig.displayedColumnsForTable,
            columnSettingsOptions: this.getDefaultColumnVisibilityMap(),
            columnsForTable: this.tableViewConfig.displayedColumnsForTable,
            filterColumnsForTable: this.tableViewConfig.displayedColumns
          };
          this.tableSettingsService.storeTableSettings(this.tableType, tableSettingsList);
          this.setupTableConfigurations(this.tableViewConfig.displayedColumnsForTable, this.tableViewConfig.displayedColumns, this.tableViewConfig.sortableColumns, this.tableViewConfig.filterConfiguration, this.tableViewConfig.filterFormGroup);
        }
      } else {
        // if no, create new list and a settings entry for this.tabletype with default values and set correspondingly the tableconfig
        const newTableSettingsList = {
          [this.tableType]: {
            columnsForDialog: this.tableViewConfig.displayedColumnsForTable,
            columnSettingsOptions: this.getDefaultColumnVisibilityMap(),
            columnsForTable: this.tableViewConfig.displayedColumnsForTable,
            filterColumnsForTable: this.tableViewConfig.displayedColumns
          }
        }
        this.tableSettingsService.storeTableSettings(this.tableType, newTableSettingsList);
        this.setupTableConfigurations(this.tableViewConfig.displayedColumnsForTable, this.tableViewConfig.displayedColumns, this.tableViewConfig.sortableColumns, this.tableViewConfig.filterConfiguration, this.tableViewConfig.filterFormGroup);
      }
    }

    private getDefaultColumnVisibilityMap(): Map<string,boolean> {
      const initialColumnMap = new Map<string,boolean>();
      for(const column of this.tableViewConfig.displayedColumnsForTable) {
        initialColumnMap.set(column,true);
      }
      return initialColumnMap;
    }


  private setupTableConfigurations(displayedColumnsForTable: string[], displayedColumns: string[], sortableColumns: Record<string, boolean>, filterConfiguration: any[], filterFormGroup: any): any {
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

    optionTextSearch = [];
    semanticDataModelOptions = [
        {
            display: 'Batch',
            value: SemanticDataModel.BATCH,
        }, {
            display: 'JustInSequence',
            value: SemanticDataModel.JUSTINSEQUENCE,
        }, {
            display: 'SerialPart',
            value: SemanticDataModel.SERIALPART,
        }, {
            display: 'Unknown',
            value: SemanticDataModel.UNKNOWN,
        }, {
            display: 'PartAsPlanned',
            value: SemanticDataModel.PARTASPLANNED,
        },
    ];

    // TODO: create a type for the filterconfig
    public readonly assetAsBuiltFilterConfiguration: any[] = [
        {filterKey: 'Filter', headerKey: 'Filter', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'id', headerKey: 'filterId', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'idShort', headerKey: 'filterIdShort', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'nameAtManufacturer', headerKey: 'filterName', isTextSearch: true, option: this.optionTextSearch}, // nameAtManufacturer
        {filterKey: 'manufacturerName', headerKey: 'filterManufacturer', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'manufacturerPartId', headerKey: 'filterPartId', isTextSearch: true, option: this.optionTextSearch}, // Part number / Batch Number / JIS Number
        {filterKey: 'manufacturerPartId', headerKey: 'filterManufacturerPartId', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'customerPartId', headerKey: 'filterCustomerPartId', isTextSearch: true, option: this.optionTextSearch}, // --> semanticModel.customerPartId
        {filterKey: 'classification', headerKey: 'filterClassification', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'nameAtCustomer', headerKey: 'filterNameAtCustomer', isTextSearch: true, option: this.optionTextSearch}, // --> semanticModel.nameAtCustomer
        {filterKey: 'semanticModelId', headerKey: 'filterSemanticModelId', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'semanticDataModel', headerKey: 'filterSemanticDataModel', isTextSearch: false, option: this.semanticDataModelOptions},
        {filterKey: 'manufacturingDate', headerKey: 'filterManufacturingDate', isTextSearch: false, isDate: true, option: this.optionTextSearch},
        {filterKey: 'manufacturingCountry', headerKey: 'filterManufacturingCountry', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'activeAlerts', headerKey: 'filterActiveAlerts', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'activeInvestigations', headerKey: 'filterActiveInvestigations', isTextSearch: true, option: this.optionTextSearch},
    ];


    assetAsBuiltFilterFormGroup = {
        id: new FormControl([]),
        idShort: new FormControl([]),
        nameAtManufacturer: new FormControl([]),
        manufacturerName: new FormControl([]),
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
        nameAtManufacturer: new FormControl([]),
        manufacturerName: new FormControl([]),
        manufacturerPartId: new FormControl([]),
        classification: new FormControl([]),
        semanticDataModel: new FormControl([]),
        semanticModelId: new FormControl([]),
        validityPeriodFrom: new FormControl([]),
        validityPeriodTo: new FormControl([]),
        function: new FormControl([]),
        catenaxSiteId: new FormControl([]),
        functionValidFrom: new FormControl([]),
        functionValidUntil: new FormControl([])
    };


    assetAsPlannedSupplierFilterFormGroup = {
        select: new FormControl([]),
        semanticDataModel: new FormControl([]),
        nameAtManufacturer: new FormControl([]),
        manufacturerName: new FormControl([]),
        manufacturerPartId: new FormControl([]),
        semanticModelId: new FormControl([]),
      menu: new FormControl([])
    };

    assetAsPlannedCustomerFilterFormGroup = {
        semanticDataModel: new FormControl([]),
        nameAtManufacturer: new FormControl([]),
        manufacturerName: new FormControl([]),
        semanticModelId: new FormControl([]),
        manufacturerPartId: new FormControl([])
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
      menu: new FormControl([])
    };

    assetAsBuiltCustomerFilterFormGroup = {
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
        {filterKey: 'semanticDataModel', headerKey: 'filterSemanticDataModel', isTextSearch: false, option: this.semanticDataModelOptions},
        {filterKey: 'nameAtManufacturer', headerKey: 'filterName', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'manufacturerName', headerKey: 'filterManufacturer', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'manufacturerPartId', headerKey: 'filterManufacturerPartId', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'semanticModelId', headerKey: 'filterSemanticModelId', isTextSearch: true, option: this.optionTextSearch}
    ];

    private readonly assetAsPlannedSupplierFilterConfiguration: any[] = [
        {filterKey: 'Filter', headerKey: 'Filter', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'semanticDataModel', headerKey: 'filterSemanticDataModel', isTextSearch: false, option: this.semanticDataModelOptions},
        {filterKey: 'nameAtManufacturer', headerKey: 'filterName', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'manufacturerName', headerKey: 'filterManufacturer', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'manufacturerPartId', headerKey: 'filterManufacturerPartId', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'semanticModelId', headerKey: 'filterSemanticModelId', isTextSearch: true, option: this.optionTextSearch}
    ];


    private readonly assetAsBuiltCustomerFilterConfiguration: any[] = [
        {filterKey: 'semanticDataModel', headerKey: 'filterSemanticDataModel', isTextSearch: false, option: this.semanticDataModelOptions},
        {filterKey: 'nameAtManufacturer', headerKey: 'filterName', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'manufacturerName', headerKey: 'filterManufacturer', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'manufacturerPartId', headerKey: 'filterPartId', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'semanticModelId', headerKey: 'filterSemanticModelId', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'manufacturingDate', headerKey: 'filterManufacturingDate', isTextSearch: false, isDate: true, option: this.optionTextSearch},
        {filterKey: 'activeAlerts', headerKey: 'filterActiveAlerts', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'activeInvestigations', headerKey: 'filterActiveInvestigations', isTextSearch: true, option: this.optionTextSearch},
    ];


    private readonly assetAsBuiltSupplierFilterConfiguration: any[] = [
        {filterKey: 'Filter', headerKey: 'Filter', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'semanticDataModel', headerKey: 'filterSemanticDataModel', isTextSearch: false, option: this.semanticDataModelOptions},
        {filterKey: 'nameAtManufacturer', headerKey: 'filterName', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'manufacturerName', headerKey: 'filterManufacturer', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'manufacturerPartId', headerKey: 'filterPartId', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'semanticModelId', headerKey: 'filterSemanticModelId', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'manufacturingDate', headerKey: 'filterManufacturingDate', isTextSearch: false, isDate: true, option: this.optionTextSearch},
        {filterKey: 'activeAlerts', headerKey: 'filterActiveAlerts', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'activeInvestigations', headerKey: 'filterActiveInvestigations', isTextSearch: true, option: this.optionTextSearch},
    ];


    private readonly assetAsPlannedFilterConfiguration: any[] = [
        {filterKey: 'Filter', headerKey: 'Filter', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'id', headerKey: 'filterId', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'idShort', headerKey: 'filterIdShort', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'nameAtManufacturer', headerKey: 'filterName', isTextSearch: true, option: this.optionTextSearch}, // nameAtManufacturer
        {filterKey: 'manufacturerName', headerKey: 'filterManufacturer', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'manufacturerPartId', headerKey: 'filterManufacturerPartId', isTextSearch: true, option: this.optionTextSearch}, // Part number / Batch Number / JIS Number
        {filterKey: 'classification', headerKey: 'filterClassification', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'semanticDataModel', headerKey: 'filterSemanticDataModel', isTextSearch: false, option: this.semanticDataModelOptions},
        {filterKey: 'semanticModelId', headerKey: 'filterSemanticModelId', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'validityPeriodFrom', headerKey: 'filterValidityPeriodFrom', isTextSearch: false, isDate: true, option: this.optionTextSearch},
        {filterKey: 'validityPeriodTo', headerKey: 'filterValidityPeriodTo', isTextSearch: false, isDate: true, option: this.optionTextSearch},
        {filterKey: 'function', headerKey: 'filterPsFunction', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'catenaxSiteId', headerKey: 'filterCatenaXSiteId', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'functionValidFrom', headerKey: 'filterFunctionValidFrom', isTextSearch: false, isDate: true, option: this.optionTextSearch},
        {filterKey: 'functionValidUntil', headerKey: 'filterFunctionValidUntil', isTextSearch: false, isDate: true, option: this.optionTextSearch}
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

    public onPaginationChange({pageIndex, pageSize}: PageEvent): void {
        this.pageIndex = pageIndex;
        this.isDataLoading = true;
        this.configChanged.emit({page: pageIndex, pageSize: pageSize, sorting: this.sorting});
    }

    public updateSortingOfData({active, direction}: Sort): void {
        this.selection.clear();
        this.emitMultiSelect();
        this.sorting = !direction ? null : ([active, direction] as TableHeaderSort);
        this.isDataLoading = true;
        this.configChanged.emit({page: 0, pageSize: this.pageSize, sorting: this.sorting});
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

    private addSelectedValues(newData: unknown[]): void {
        addSelectedValues(this.selection, newData);
    }

    private removeSelectedValues(itemsToRemove: unknown[]): void {
        removeSelectedValues(this.selection, itemsToRemove);
    }

    openDialog(): void {
      const config = new MatDialogConfig();
      config.data = {
        title: "table.tableSettings.title",
        panelClass: "custom",
        tableType: this.tableType,
        defaultColumns: this.tableViewConfig.displayedColumnsForTable,
        defaultFilterColumns: this.tableViewConfig.displayedColumns
      };
      this.dialog.open(TableSettingsComponent, config )
    }


  protected readonly MenuStack = MenuStack;
}
