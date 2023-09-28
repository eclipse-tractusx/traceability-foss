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

import {SelectionModel} from '@angular/cdk/collections';
import {Component, ElementRef, EventEmitter, Input, Output, ViewChild, ViewEncapsulation} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {MatSort, Sort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {Pagination} from '@core/model/pagination.model';
import {RoleService} from '@core/user/role.service';
import {AssetAsBuiltFilter, SemanticDataModel} from '@page/parts/model/parts.model';
import {
    MultiSelectAutocompleteComponent
} from '@shared/components/multi-select-autocomplete/multi-select-autocomplete.component';
import {MenuActionConfig, TableConfig, TableEventConfig, TableHeaderSort} from '@shared/components/table/table.model';
import {FlattenObjectPipe} from '@shared/pipes/flatten-object.pipe';


@Component({
    selector: 'app-parts-table',
    templateUrl: './parts-table.component.html',
    styleUrls: ['parts-table.component.scss'],
    encapsulation: ViewEncapsulation.None,
})
export class PartsTableComponent {
    @ViewChild(MatSort) sort: MatSort;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild('tableElement', {read: ElementRef}) tableElementRef: ElementRef<HTMLElement>;

    @Input()
    set tableConfig(tableConfig: TableConfig) {
        if (!tableConfig) {
            return;
        }

        const {menuActionsConfig: menuActions, displayedColumns: dc, columnRoles, hasPagination = true} = tableConfig;
        const displayedColumns = dc.filter(column => this.roleService.hasAccess(columnRoles?.[column] ?? 'user'));

        const viewDetailsMenuAction: MenuActionConfig<unknown> = {
            label: 'actions.viewDetails',
            icon: 'remove_red_eye',
            action: (data: Record<string, unknown>) => this.selected.emit(data),
        };

        const menuActionsConfig = menuActions ? [viewDetailsMenuAction, ...menuActions] : null;
        this._tableConfig = {...tableConfig, displayedColumns, hasPagination, menuActionsConfig};
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


    public readonly displayedColumnsAsBuilt: string[] = [
        'Filter',
        'filterId',
        'filterIdShort',
        'filterName', // nameAtManufacturer
        'filterManufacturer',
        'filterPartId', // Part number / Batch Number / JIS Number
        'filterManufacturerPartId',
        'filterCustomerPartId', // --> semanticModel.customerPartId
        'filterClassification',
        //'nameAtManufacturer', --> already in name
        'filterNameAtCustomer', // --> semanticModel.nameAtCustomer
        'filterSemanticModelId',
        'filterSemanticDataModel',
        'filterManufacturingDate',
        'filterManufacturingCountry',
    ];

    @Input() set paginationData({page, pageSize, totalItems, content}: Pagination<unknown>) {
        this.totalItems = totalItems;
        this.pageSize = pageSize;
        this.dataSource.data = content;
        this.isDataLoading = false;
        this.pageIndex = page;
    }

    @Input() set PartsPaginationData({page, pageSize, totalItems, content}: Pagination<unknown>) {
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
    @Output() configChanged = new EventEmitter<TableEventConfig>();
    @Output() multiSelect = new EventEmitter<any[]>();
    @Output() clickSelectAction = new EventEmitter<void>();
    @Output() filterActivatedAsBuilt = new EventEmitter<AssetAsBuiltFilter>();
    @Output() filterActivatedAsPlanned = new EventEmitter<any>();

    public readonly dataSource = new MatTableDataSource<unknown>();
    public readonly selection = new SelectionModel<unknown>(true, []);
    public filterValues = {};
    public totalItems: number;
    public pageIndex: number;
    public isDataLoading: boolean;
    public selectedRow: Record<string, unknown>;
    public isMenuOpen: boolean;
    public displayedFilter: boolean;

    private pageSize: number;
    private sorting: TableHeaderSort;

    private _tableConfig: TableConfig;

    filterFormGroup = new FormGroup({
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
    });

    optionTextSearch = [];
    semanticDataModelOptions = [
        {
            display: 'Batch',
            value: SemanticDataModel.BATCH,
        }, {
            display: 'JustInSequence',
            value: SemanticDataModel.JUSTINSEQUENCEPART,
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

    public readonly filterConfigurations: any[] = [
        {filterKey: '', headerKey: 'Filter', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'id', headerKey: 'filterId', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'idShort', headerKey: 'filterIdShort', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'nameAtManufacturer', headerKey: 'filterName', isTextSearch: true, option: this.optionTextSearch}, // nameAtManufacturer
        {filterKey: 'businessPartner', headerKey: 'filterManufacturer', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'manufacturerPartId', headerKey: 'filterPartId', isTextSearch: true, option: this.optionTextSearch}, // Part number / Batch Number / JIS Number
        {filterKey: 'manufacturerPartId', headerKey: 'filterManufacturerPartId', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'customerPartId', headerKey: 'filterCustomerPartId', isTextSearch: true, option: this.optionTextSearch}, // --> semanticModel.customerPartId
        {filterKey: 'classification', headerKey: 'filterClassification', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'nameAtCustomer', headerKey: 'filterNameAtCustomer', isTextSearch: true, option: this.optionTextSearch}, // --> semanticModel.nameAtCustomer
        {filterKey: 'semanticModelId', headerKey: 'filterSemanticModelId', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'semanticDataModel', headerKey: 'filterSemanticDataModel', isTextSearch: false, option: this.semanticDataModelOptions},
        {filterKey: 'manufacturingDate', headerKey: 'filterManufacturingDate', isTextSearch: true, option: this.optionTextSearch},
        {filterKey: 'manufacturingCountry', headerKey: 'filterManufacturingCountry', isTextSearch: true, option: this.optionTextSearch},
    ];


    constructor(private readonly roleService: RoleService) {
        this.filterFormGroup.valueChanges.subscribe((formValues) => {
            const transformedFilter: AssetAsBuiltFilter = {};

            // Loop through each form control and add it to the transformedFilter if it has a non-null and non-undefined value
            for (const key in formValues) {
                if (formValues[key] !== null && formValues[key] !== undefined) {
                    transformedFilter[key] = formValues[key];
                }
            }

            // Default value for semanticDataModel if not provided
            if (!transformedFilter.semanticDataModel) {
                transformedFilter.semanticDataModel = SemanticDataModel.UNKNOWN;
            }


            this.filterActivatedAsBuilt.emit(transformedFilter);

        });
    }

    @ViewChild(MultiSelectAutocompleteComponent) multiSelection: MultiSelectAutocompleteComponent;


    onToggleDropdown() {
        this.multiSelection.toggleDropdown();
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
        console.log(this.displayedFilter);
    }

    public isSelected(row: unknown): boolean {
        return !!this.selection.selected.find(data => JSON.stringify(data) === JSON.stringify(row));
    }

    private addSelectedValues(newData: unknown[]): void {
        const newValues = newData.filter(data => !this.isSelected(data));
        this.selection.select(...newValues);
    }

    private removeSelectedValues(itemsToRemove: unknown[]): void {
        const shouldDelete = (row: unknown) => !!itemsToRemove.find(data => JSON.stringify(data) === JSON.stringify(row));
        const rowsToDelete = this.selection.selected.filter(data => shouldDelete(data));

        this.selection.deselect(...rowsToDelete);
    }

    protected readonly name = name;
}
