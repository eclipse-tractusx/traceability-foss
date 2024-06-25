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

import { Sort } from '@angular/material/sort';
import { Pagination } from '@core/model/pagination.model';
import { PartsFacade } from '@page/parts/core/parts.facade';
import { PartsTableComponent } from '@shared/components/parts-table/parts-table.component';
import { NotificationType } from '@shared/model/notification.model';
import { FormatPartSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-part-semantic-data-model-to-camelcase.pipe';
import { SharedModule } from '@shared/shared.module';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { TableType } from '../multi-select-autocomplete/table-type.model';

describe('PartsTableComponent', () => {
  const renderPartsTableComponent = (
    size: number,
    tableType: TableType = TableType.AS_BUILT_OWN,
  ) => {
    const multiSelectActive = true;
    const content = generateTableContent(size);
    const paginationData = { page: 0, pageSize: 10, totalItems: 100, content } as Pagination<unknown>;
    return renderComponent(PartsTableComponent, {
      imports: [ SharedModule ],
      providers: [
        // Provide the PartsFacade mock as a value for the PartsFacade token
        { provide: PartsFacade },
        { provide: FormatPartSemanticDataModelToCamelCasePipe },
      ],
      componentProperties: { paginationData, tableType },
    });
  };

  const generateTableContent = (size: number) => {
    return Array.apply(null, Array(size)).map((_, i) => ({ name: 'name_' + i, test: 'test' }));
  };

  it('should render parts asbuilt table', async () => {
    const tableSize = 7;
    await renderPartsTableComponent(tableSize, TableType.AS_BUILT_OWN);

    expect(await waitFor(() => screen.getByTestId('table-component--test-id'))).toBeInTheDocument();
  });

  it('should emit maximizeClicked event when Enter key is pressed', async () => {
    const { fixture } = await renderPartsTableComponent(1, TableType.AS_BUILT_OWN);
    const { componentInstance } = fixture;
    spyOn(componentInstance.maximizeClicked, 'emit');
    componentInstance.tableType = TableType.AS_BUILT_OWN;

    const keyboardEvent = new KeyboardEvent('keydown', { key: 'Enter' });
    componentInstance.handleKeyDownMaximizedClickedMethod(keyboardEvent);

    expect(componentInstance.maximizeClicked.emit).toHaveBeenCalledWith(componentInstance.tableType);
  });

  it('should not emit maximizeClicked event when key other than Enter is pressed', async () => {
    const { fixture } = await renderPartsTableComponent(1, TableType.AS_BUILT_OWN);
    const { componentInstance } = fixture;
    spyOn(componentInstance.maximizeClicked, 'emit');

    const keyboardEvent = new KeyboardEvent('keydown', { key: 'Space' });
    componentInstance.handleKeyDownMaximizedClickedMethod(keyboardEvent);

    expect(componentInstance.maximizeClicked.emit).not.toHaveBeenCalled();
  });

  it('should emit createQualityNotificationClickedEvent when Enter key is pressed', async () => {
    const { fixture } = await renderPartsTableComponent(1, TableType.AS_BUILT_OWN);
    const { componentInstance } = fixture;
    spyOn(componentInstance.createQualityNotificationClickedEvent, 'emit');
    componentInstance.notificationType = NotificationType.ALERT;

    const keyboardEvent = new KeyboardEvent('keydown', { key: 'Enter' });
    componentInstance.handleKeyDownQualityNotificationClicked(keyboardEvent);

    expect(componentInstance.createQualityNotificationClickedEvent.emit).toHaveBeenCalledWith(componentInstance.notificationType);
  });

  it('should not emit createQualityNotificationClickedEvent when key other than Enter is pressed', async () => {
    const { fixture } = await renderPartsTableComponent(1, TableType.AS_BUILT_OWN);
    const { componentInstance } = fixture;
    spyOn(componentInstance.createQualityNotificationClickedEvent, 'emit');

    const keyboardEvent = new KeyboardEvent('keydown', { key: 'Space' });
    componentInstance.handleKeyDownQualityNotificationClicked(keyboardEvent);

    expect(componentInstance.createQualityNotificationClickedEvent.emit).not.toHaveBeenCalled();
  });

  it('should have correct sizes for split areas', async () => {
    const { fixture } = await renderPartsTableComponent(1, TableType.AS_BUILT_OWN);
    const { componentInstance } = fixture;
    expect(componentInstance.tableType).toEqual(TableType.AS_BUILT_OWN);
  });

  it('should init the correct columns for asBuilt', async () => {
    const { fixture } = await renderPartsTableComponent(1, TableType.AS_BUILT_OWN);
    const { componentInstance } = fixture;

    componentInstance.ngOnInit();

    // Expect that the event was emitted with the correct data
    expect(componentInstance.displayedColumns).toEqual([
      'Filter',
      'filterowner',
      'filterid',
      'filteridShort',
      'filternameAtManufacturer', // nameAtManufacturer
      'filterbusinessPartner',
      'filtermanufacturerName',
      'filtermanufacturerPartId',
      'filtercustomerPartId', // --> semanticModel.customerPartId
      'filterclassification',
      //'nameAtManufacturer', --> already in name
      'filternameAtCustomer', // --> semanticModel.nameAtCustomer
      'filtersemanticModelId',
      'filtersemanticDataModel',
      'filtermanufacturingDate',
      'filtermanufacturingCountry',
      'filterreceivedActiveAlerts',
      'filterreceivedActiveInvestigations',
      'filtersentActiveAlerts',
      'filtersentActiveInvestigations',
      'filterimportState',
      'filterimportNote',
      'filtercontractAgreementId',
      'Menu',
    ]);
  });

  it('should init the correct columns for asPlanned own', async () => {
    const { fixture } = await renderPartsTableComponent(1, TableType.AS_PLANNED_OWN);
    const { componentInstance } = fixture;

    componentInstance.ngOnInit();

    expect(componentInstance.displayedColumns).toEqual([
      'Filter',
      'filterowner',
      'filterid',
      'filteridShort',
      'filternameAtManufacturer',
      'filterbusinessPartner',
      'filtermanufacturerName',
      'filtermanufacturerPartId',
      'filterclassification',
      'filtersemanticDataModel',
      'filtersemanticModelId',
      'filtervalidityPeriodFrom',
      'filtervalidityPeriodTo',
      'filterpsFunction',
      'filtercatenaXSiteId',
      'filterfunctionValidFrom',
      'filterfunctionValidUntil',
      'filterimportState',
      'filterimportNote',
      'filtercontractAgreementId',
      'Menu',
    ]);
  });

  it('should update sorting data and emit configChanged event', async () => {

    const { fixture } = await renderPartsTableComponent(1, TableType.AS_PLANNED_OWN);
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
      sorting: [ 'name', 'asc' ],
    });
  });

  it('should update component properties and data source when PartsPaginationData is set', async () => {
    const { fixture } = await renderPartsTableComponent(1, TableType.AS_PLANNED_OWN);
    const { componentInstance } = fixture;

    const paginationData: Pagination<unknown> = {
      pageCount: 10,
      page: 2,
      pageSize: 10,
      totalItems: 100,
      content: [
        { id: 1, name: 'Item 1' },
        { id: 2, name: 'Item 2' },
      ],
    };


    componentInstance.paginationData = paginationData;

    expect(componentInstance.totalItems).toEqual(paginationData.totalItems);
    expect(componentInstance.paginationData.pageSize).toEqual(paginationData.pageSize);
    expect(componentInstance.pageIndex).toEqual(paginationData.page);
    expect(componentInstance.isDataLoading).toBe(false);

  });

  it('should remove selected values and emit multiSelect', async () => {
    const { fixture } = await renderPartsTableComponent(1, TableType.AS_PLANNED_OWN);
    const { componentInstance } = fixture;

    componentInstance.selection.select({ id: 1 }, { id: 2 }, { id: 3 });

    componentInstance.deselectTrigger = [ { id: 2 }, { id: 3 } ];

    expect(componentInstance.selection.selected).toEqual([ { id: 1 } ]);
  });

  it('should not remove selected values if deselectItem is not provided', async () => {
    const { fixture } = await renderPartsTableComponent(1, TableType.AS_PLANNED_OWN);
    const { componentInstance } = fixture;

    componentInstance.selection.select({ id: 1 }, { id: 2 }, { id: 3 });

    componentInstance.deselectTrigger = null;

    expect(componentInstance.selection.selected).toEqual([ { id: 1 }, { id: 2 }, { id: 3 } ]);
  });

  it('should emit multiSelect event', async () => {
    const { fixture } = await renderPartsTableComponent(1, TableType.AS_PLANNED_OWN);
    const { componentInstance } = fixture;

    componentInstance.selection.select({ id: 1 }, { id: 2 }, { id: 3 });

    const multiSelectSpy = spyOn(componentInstance.multiSelect, 'emit');

    componentInstance.deselectTrigger = [ { id: 2 }, { id: 3 } ];

    expect(multiSelectSpy).toHaveBeenCalledWith([ { id: 1 } ]);
  });

  it('should toggle all rows correctly', async () => {

    const { fixture } = await renderPartsTableComponent(1, TableType.AS_PLANNED_OWN);
    const { componentInstance } = fixture;

    componentInstance.selection.select({ id: 1 }, { id: 2 }, { id: 3 });

    componentInstance.toggleAllRows();

    expect(componentInstance.selection.selected).toEqual([ { id: 1 }, { id: 2 }, { id: 3 }, {
      name: 'name_0',
      test: 'test',
    } ]);
  });

  it('should clear all rows correctly', async () => {

    const { fixture } = await renderPartsTableComponent(1, TableType.AS_PLANNED_OWN);
    const { componentInstance } = fixture;
    componentInstance.selection.select({ id: 1 }, { id: 2 }, { id: 3 });

    componentInstance.clearAllRows();

    expect(componentInstance.selection.selected).toEqual([]);
  });

  it('should clear current rows correctly', async () => {
    const { fixture } = await renderPartsTableComponent(1, TableType.AS_PLANNED_OWN);
    const { componentInstance } = fixture;

    const emitMultiSelectSpy = spyOn(componentInstance.multiSelect, 'emit');

    componentInstance.clearCurrentRows();

    expect(emitMultiSelectSpy).toHaveBeenCalled();
    expect(componentInstance.selection.selected).toEqual([]);
  });

});

