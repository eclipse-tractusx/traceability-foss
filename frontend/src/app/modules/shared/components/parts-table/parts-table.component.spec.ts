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

import { SharedModule } from '@shared/shared.module';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { PartsTableComponent } from "@shared/components/parts-table/parts-table.component";
import { Pagination } from "@core/model/pagination.model";
import { PartTableType, TableConfig } from "@shared/components/table/table.model";
import { PartsFacade } from "@page/parts/core/parts.facade";
import { Sort } from "@angular/material/sort";

describe('PartsTableComponent', () => {
    const renderPartsTableComponent = (
        size: number,
        tableType: PartTableType = PartTableType.AS_BUILT_OWN
    ) => {
        const multiSelectActive = true;
        const content = generateTableContent(size);
        const paginationData = { page: 0, pageSize: 10, totalItems: 100, content } as Pagination<unknown>;
        return renderComponent(PartsTableComponent, {
            imports: [SharedModule],
            providers: [
                // Provide the PartsFacade mock as a value for the PartsFacade token
                { provide: PartsFacade },
            ],
            componentProperties: { multiSelectActive, paginationData, tableType },
        });
    };

    const generateTableContent = (size: number) => {
        return Array.apply(null, Array(size)).map((_, i) => ({ name: 'name_' + i, test: 'test' }));
    };
    const renderPartsTable = (
        size: number,
        displayedColumns = [
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
        ],
        header = { name: 'Name' },
        selected = jasmine.createSpy(),
        tableType: PartTableType = PartTableType.AS_BUILT_OWN
    ) => {
        const content = generateTableContent(size);
        const data = { page: 0, pageSize: 10, totalItems: 100, content } as Pagination<unknown>;

        const tableConfig: TableConfig = { displayedColumns, header };

        const multiSelectActive = true;


        return renderComponent(
            `<app-parts-table [tableType]='tableType' [multiSelectActive]='multiSelectActive' [paginationData]='data' (selected)='selected($event)'></app-parts-table>`,
            {
                declarations: [PartsTableComponent],
                imports: [SharedModule],
                componentProperties: {
                    data,
                    tableConfig,
                    selected,
                    multiSelectActive,
                    tableType
                },
            },
        );
    };


    it('should render parts asbuilt table', async () => {
        const tableSize = 7;
        await renderPartsTable(tableSize);

        expect(await waitFor(() => screen.getByTestId('table-component--test-id'))).toBeInTheDocument();
    });


    it('should have correct sizes for split areas', async () => {
        const { fixture } = await renderPartsTableComponent(1, PartTableType.AS_BUILT_OWN);
        const { componentInstance } = fixture;
        expect(componentInstance.tableType).toEqual(PartTableType.AS_BUILT_OWN);
    });

    it('should init the correct columns for asBuilt', async () => {
        const { fixture } = await renderPartsTableComponent(1, PartTableType.AS_BUILT_OWN);
        const { componentInstance } = fixture;

        componentInstance.ngOnInit();

        // Expect that the event was emitted with the correct data
        expect(componentInstance.displayedColumns).toEqual([
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
            'filterActiveAlerts',
            'filterActiveInvestigations',
        ]);
    });

    it('should init the correct columns for asPlanned own', async () => {
        const { fixture } = await renderPartsTableComponent(1, PartTableType.AS_PLANNED_OWN);
        const { componentInstance } = fixture;

        componentInstance.ngOnInit();

        expect(componentInstance.displayedColumns).toEqual([
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
        ]);
    });

    it('should init the correct columns for asDesigned own', async () => {
        const { fixture } = await renderPartsTableComponent(1, PartTableType.AS_DESIGNED_OWN);
        const { componentInstance } = fixture;

        componentInstance.ngOnInit();

        expect(componentInstance.displayedColumns).toEqual([
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
        ]);
    });

    it('should init the correct columns for asOrdered own', async () => {
        const { fixture } = await renderPartsTableComponent(1, PartTableType.AS_ORDERED_OWN);
        const { componentInstance } = fixture;

        componentInstance.ngOnInit();

        expect(componentInstance.displayedColumns).toEqual([
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
        ]);
    });

    it('should init the correct columns for asSupported own', async () => {
        const { fixture } = await renderPartsTableComponent(1, PartTableType.AS_SUPPORTED_OWN);
        const { componentInstance } = fixture;

        componentInstance.ngOnInit();

        expect(componentInstance.displayedColumns).toEqual([
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
        ]);
    });

    it('should init the correct columns for asRecycled own', async () => {
        const { fixture } = await renderPartsTableComponent(1, PartTableType.AS_RECYCLED_OWN);
        const { componentInstance } = fixture;

        componentInstance.ngOnInit();

        expect(componentInstance.displayedColumns).toEqual([
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
        ]);
    });

    it('should update sorting data and emit configChanged event', async () => {

        const { fixture } = await renderPartsTableComponent(1, PartTableType.AS_PLANNED_OWN);
        const { componentInstance } = fixture;

        componentInstance.selection.select({ id: 1, name: 'Item 1' }); // Mock a selected item

        componentInstance.isDataLoading = false;
        const sortEvent: Sort = { active: 'name', direction: 'asc' };

        const configChangedSpy = spyOn(componentInstance.configChanged, 'emit');

        componentInstance.updateSortingOfData(sortEvent);

        expect(componentInstance.selection.isEmpty()).toBe(true); // Selection should be cleared
        expect(componentInstance.isDataLoading).toBe(true); // isDataLoading should be set to true
        expect(configChangedSpy).toHaveBeenCalledWith({
            page: 0,
            pageSize: componentInstance.paginationData.pageSize,
            sorting: ['name', 'asc'],
        });
    });

    it('should update component properties and data source when PartsPaginationData is set', async () => {
        const { fixture } = await renderPartsTableComponent(1, PartTableType.AS_PLANNED_OWN);
        const { componentInstance } = fixture;

        const paginationData: Pagination<unknown> = {
            pageCount: 10,
            page: 2,
            pageSize: 10,
            totalItems: 100,
            content: [
                { id: 1, name: 'Item 1' },
                { id: 2, name: 'Item 2' }
            ],
        };


        componentInstance.paginationData = paginationData;

        expect(componentInstance.totalItems).toEqual(paginationData.totalItems);
        expect(componentInstance.paginationData.pageSize).toEqual(paginationData.pageSize);
        expect(componentInstance.pageIndex).toEqual(paginationData.page);
        expect(componentInstance.isDataLoading).toBe(false);

    });

    it('should select or deselect a row and emit selected event if menuActionsConfig is not defined', async () => {
        const { fixture } = await renderPartsTableComponent(1, PartTableType.AS_PLANNED_OWN);
        const { componentInstance } = fixture;

        const row1 = { id: 1, name: 'Item 1' };

        spyOn(componentInstance.selected, 'emit');

        componentInstance.selectElement(row1);

        expect(componentInstance.selected.emit).toHaveBeenCalledWith(row1);
        expect(componentInstance.selectedRow).toEqual(row1);

    });

    it('should remove selected values and emit multiSelect', async () => {
        const { fixture } = await renderPartsTableComponent(1, PartTableType.AS_PLANNED_OWN);
        const { componentInstance } = fixture;

        componentInstance.selection.select({ id: 1 }, { id: 2 }, { id: 3 });

        componentInstance.deselectTrigger = [{ id: 2 }, { id: 3 }];

        expect(componentInstance.selection.selected).toEqual([{ id: 1 }]);
    });

    it('should not remove selected values if deselectItem is not provided', async () => {
        const { fixture } = await renderPartsTableComponent(1, PartTableType.AS_PLANNED_OWN);
        const { componentInstance } = fixture;

        componentInstance.selection.select({ id: 1 }, { id: 2 }, { id: 3 });

        componentInstance.deselectTrigger = null;

        expect(componentInstance.selection.selected).toEqual([{ id: 1 }, { id: 2 }, { id: 3 }]);
    });

    it('should emit multiSelect event', async () => {
        const { fixture } = await renderPartsTableComponent(1, PartTableType.AS_PLANNED_OWN);
        const { componentInstance } = fixture;

        componentInstance.selection.select({ id: 1 }, { id: 2 }, { id: 3 });

        const multiSelectSpy = spyOn(componentInstance.multiSelect, 'emit');

        componentInstance.deselectTrigger = [{ id: 2 }, { id: 3 }];

        expect(multiSelectSpy).toHaveBeenCalledWith([{ id: 1 }]);
    });

    it('should toggle all rows correctly', async () => {

        const { fixture } = await renderPartsTableComponent(1, PartTableType.AS_PLANNED_OWN);
        const { componentInstance } = fixture;

        componentInstance.selection.select({ id: 1 }, { id: 2 }, { id: 3 });

        componentInstance.toggleAllRows();

        expect(componentInstance.selection.selected).toEqual([{ id: 1 }, { id: 2 }, { id: 3 }, { name: 'name_0', test: 'test' }]);
    });

    it('should clear all rows correctly', async () => {

        const { fixture } = await renderPartsTableComponent(1, PartTableType.AS_PLANNED_OWN);
        const { componentInstance } = fixture;
        componentInstance.selection.select({ id: 1 }, { id: 2 }, { id: 3 });

        componentInstance.clearAllRows();

        expect(componentInstance.selection.selected).toEqual([]);
    });

    it('should clear current rows correctly', async () => {
        const { fixture } = await renderPartsTableComponent(1, PartTableType.AS_PLANNED_OWN);
        const { componentInstance } = fixture;

        const emitMultiSelectSpy = spyOn(componentInstance.multiSelect, 'emit');

        componentInstance.clearCurrentRows();

        expect(emitMultiSelectSpy).toHaveBeenCalled();
        expect(componentInstance.selection.selected).toEqual([]);
    });

    it('should return the tooltip string for the given part', async () => {
        const { fixture } = await renderPartsTableComponent(1, PartTableType.AS_PLANNED_OWN);
        const { componentInstance } = fixture;

        expect(componentInstance.getTooltip("!")).not.toBeNull();
    });

});

