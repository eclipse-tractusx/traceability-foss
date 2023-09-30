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

import {SharedModule} from '@shared/shared.module';
import {screen, waitFor} from '@testing-library/angular';
import {renderComponent} from '@tests/test-render.utils';
import {PartsTableComponent} from "@shared/components/parts-table/parts-table.component";
import {Pagination} from "@core/model/pagination.model";
import {TableConfig} from "@shared/components/table/table.model";

describe('PartsTableComponent', () => {

    const generateTableContent = (size: number) => {
        return Array.apply(null, Array(size)).map((_, i) => ({name: 'name_' + i, test: 'test'}));
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
        header = {name: 'Name'},
        selected = jasmine.createSpy(),
    ) => {
        const content = generateTableContent(size);
        const data = {page: 0, pageSize: 10, totalItems: 100, content} as Pagination<unknown>;

        const tableConfig: TableConfig = {displayedColumns, header};

        const multiSelectActive = true;
        return renderComponent(
            `<app-parts-table [multiSelectActive]='multiSelectActive' [paginationData]='data' [tableConfig]='tableConfig' (selected)='selected($event)'></app-parts-table>`,
            {
                declarations: [PartsTableComponent],
                imports: [SharedModule],
                componentProperties: {
                    data,
                    tableConfig,
                    selected,
                    multiSelectActive
                },
            },
        );
    };


    it('should render parts table', async () => {
        const tableSize = 7;
        await renderPartsTable(tableSize);

        expect(await waitFor(() => screen.getByTestId('table-component--test-id'))).toBeInTheDocument();
    });


    /*    it('should have correct sizes for split areas', async () => {
            const {fixture} = await renderPartsTable();
            const {componentInstance} = fixture;
            expect(componentInstance.bomLifecycleSize.asPlannedSize).toBe(50);
            expect(componentInstance.bomLifecycleSize.asBuiltSize).toBe(50);
        });*/


    /*    const data = { page: 0, pageSize: 10, totalItems: 100, content } as Pagination<unknown>;
        const options = [SemanticDataModel.BATCH, SemanticDataModel.PARTASPLANNED]

        const tableConfig: TableConfig = { displayedColumns, header };
       let  filterFormGroup = new FormGroup({
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
          semanticDataModel: new FormControl([SemanticDataModel.BATCH, SemanticDataModel.PARTASPLANNED]),
          manufacturingDate: new FormControl([]),
          manufacturingCountry: new FormControl([]),
        });

          const optionTextSearch = [];
         const  semanticDataModelOptions = [
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
      const filterConfigurations: any[] = [
              {filterKey: '', headerKey: 'Filter', isTextSearch: true, option: optionTextSearch},
              {filterKey: 'id', headerKey: 'filterId', isTextSearch: true, option: optionTextSearch},
              {filterKey: 'idShort', headerKey: 'filterIdShort', isTextSearch: true, option: optionTextSearch},
              {filterKey: 'nameAtManufacturer', headerKey: 'filterName', isTextSearch: true, option: optionTextSearch}, // nameAtManufacturer
              {filterKey: 'businessPartner', headerKey: 'filterManufacturer', isTextSearch: true, option: optionTextSearch},
              {filterKey: 'manufacturerPartId', headerKey: 'filterPartId', isTextSearch: true, option: optionTextSearch}, // Part number / Batch Number / JIS Number
              {filterKey: 'manufacturerPartId', headerKey: 'filterManufacturerPartId', isTextSearch: true,  option: optionTextSearch},
              {filterKey: 'customerPartId', headerKey: 'filterCustomerPartId', isTextSearch: true, option: optionTextSearch}, // --> semanticModel.customerPartId
              {filterKey: 'classification', headerKey: 'filterClassification', isTextSearch: true, option: optionTextSearch},
              {filterKey: 'nameAtCustomer', headerKey: 'filterNameAtCustomer', isTextSearch: true, option: optionTextSearch}, // --> semanticModel.nameAtCustomer
              {filterKey: 'semanticModelId', headerKey: 'filterSemanticModelId', isTextSearch: true, option: optionTextSearch},
              {filterKey: 'semanticDataModel', headerKey: 'filterSemanticDataModel', isTextSearch: false, option: semanticDataModelOptions},
              {filterKey: 'manufacturingDate', headerKey: 'filterManufacturingDate', isTextSearch: true, option: optionTextSearch},
              {filterKey: 'manufacturingCountry', headerKey: 'filterManufacturingCountry', isTextSearch: true, option: optionTextSearch},
          ];

      const filter = filterConfigurations[1];

       const formControl = filterFormGroup.controls[filter.filterKey];*/


});
