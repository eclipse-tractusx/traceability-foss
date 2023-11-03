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


import { OtherPartsState } from '@page/other-parts/core/other-parts.state';
import { OtherPartsModule } from '@page/other-parts/other-parts.module';
import { PartsState } from '@page/parts/core/parts.state';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { toGlobalSearchAssetFilter } from '@shared/helper/filter-helper';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { getTableCheckbox, renderComponent } from '@tests/test-render.utils';
import { OTHER_PARTS_MOCK_6 } from '../../../../../mocks/services/otherParts-mock/otherParts.test.model';

import { SupplierPartsComponent } from './supplier-parts.component';

describe('SupplierPartsComponent', () => {
  let otherPartsState: OtherPartsState;
  beforeEach(() => (otherPartsState = new OtherPartsState()));

  const renderSupplierParts = ({ roles = [] } = {}) =>
    renderComponent(SupplierPartsComponent, {
      imports: [OtherPartsModule],
      providers: [{ provide: OtherPartsState, useFactory: () => otherPartsState }, { provide: PartsState }],
      roles,
      componentInputs: {
        bomLifecycle: MainAspectType.AS_BUILT,
      },
    });

  it('should render part table', async () => {
    await renderSupplierParts();

    const tableElements = await waitFor(() => screen.getAllByTestId('table-component--test-id'));
    expect(tableElements.length).toEqual(1);
  });

  it('should render table and display correct amount of rows', async () => {
    await renderSupplierParts();

    const tableElement = await waitFor(() => screen.getByTestId('table-component--test-id'));
    expect(tableElement).toBeInTheDocument();
    expect(tableElement.children[1].childElementCount).toEqual(4);
  });

  // it('should add item to current list and then remove', async () => {
  //   const { fixture } = await renderSupplierParts({ roles: [ 'user' ] });
  //   const expectedPart = PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_6, MainAspectType.AS_BUILT);

  //   // first click to check checkbox
  //   fireEvent.click(await getTableCheckbox(screen, 0));

  //   const selectedText_1 = await waitFor(() => screen.getByText('page.selectedParts.info'));
  //   expect(selectedText_1).toBeInTheDocument();
  //   expect(fixture.componentInstance.currentSelectedItems).toEqual([ expectedPart ]);

  //   // second click to uncheck checkbox
  //   fireEvent.click(await getTableCheckbox(screen, 0));

  //   const selectedText_2 = await waitFor(() => screen.getByText('page.selectedParts.info'));
  //   expect(selectedText_2).toBeInTheDocument();
  //   expect(fixture.componentInstance.currentSelectedItems).toEqual([]);
  // });

  it('test addItemToSelection method', async () => {
    const { fixture } = await renderSupplierParts();

    const expectedPart = PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_6, MainAspectType.AS_BUILT);

    fixture.componentInstance.addItemToSelection(expectedPart);
    expect(fixture.componentInstance.currentSelectedItems).toEqual([expectedPart]);
  });

  it('test removeItemFromSelection method', async () => {
    const { fixture } = await renderSupplierParts();

    const expectedPart = PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_6, MainAspectType.AS_BUILT);

    fixture.componentInstance.currentSelectedItems = [expectedPart];

    fixture.componentInstance.removeItemFromSelection(expectedPart);
    expect(fixture.componentInstance.currentSelectedItems).toEqual([]);
  });

  it('test clearSelected method', async () => {
    const { fixture } = await renderSupplierParts();

    const expectedPart = PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_6, MainAspectType.AS_BUILT);

    fixture.componentInstance.currentSelectedItems = [expectedPart];

    fixture.componentInstance.clearSelected();
    expect(fixture.componentInstance.currentSelectedItems).toEqual([]);
  });

  it('sort supplier parts after name column', async () => {
    const { fixture } = await renderSupplierParts({ roles: ['admin'] });
    const supplierPartsComponent = fixture.componentInstance;

    let nameHeader = await screen.findByText('table.column.name');
    fireEvent.click(nameHeader);

    expect(supplierPartsComponent['tableSupplierAsBuiltSortList']).toEqual([['name', 'asc']]);

  });

  it('should multisort after column name and semanticModelId', async () => {
    const { fixture } = await renderSupplierParts({ roles: ['admin'] });
    const supplierPartsComponent = fixture.componentInstance;

    let nameHeader = await screen.findByText('table.column.name');
    fireEvent.click(nameHeader);
    let semanticModelIdHeader = await screen.findByText('table.column.semanticModelId');

    await waitFor(() => {
      fireEvent.keyDown(semanticModelIdHeader, {
        ctrlKey: true,
        charCode: 17,
      });
    });
    expect(supplierPartsComponent['ctrlKeyState']).toBeTruthy();
    await waitFor(() => {
      fireEvent.click(semanticModelIdHeader);
    });

    await waitFor(() => {
      fireEvent.keyUp(semanticModelIdHeader, {
        ctrlKey: true,
        charCode: 17,
      });
    });

    await waitFor(() => {
      fireEvent.click(semanticModelIdHeader);
    });
    expect(supplierPartsComponent['tableSupplierAsBuiltSortList']).toEqual([['name', 'asc'], ['semanticModelId', 'desc']]);
  });

  it('should reset sorting on third click', async () => {
    const { fixture } = await renderSupplierParts({ roles: ['admin'] });
    const supplierPartsComponent = fixture.componentInstance;

    let nameHeader = await screen.findByText('table.column.name');
    fireEvent.click(nameHeader);
    let semanticModelIdHeader = await screen.findByText('table.column.semanticModelId');

    await waitFor(() => {
      fireEvent.keyDown(semanticModelIdHeader, {
        ctrlKey: true,
        charCode: 17,
      });
    });
    expect(supplierPartsComponent['ctrlKeyState']).toBeTruthy();
    await waitFor(() => {
      fireEvent.click(semanticModelIdHeader);
    });

    await waitFor(() => {
      fireEvent.keyUp(semanticModelIdHeader, {
        ctrlKey: true,
        charCode: 17,
      });
    });

    await waitFor(() => {
      fireEvent.click(semanticModelIdHeader);
    });
    await waitFor(() => {
      fireEvent.click(semanticModelIdHeader);
    });
    expect(supplierPartsComponent['tableSupplierAsBuiltSortList']).toEqual([]);
  });


  it('should handle updateSupplierParts null', async () => {
    const { fixture } = await renderSupplierParts();
    const supplierPartsComponent = fixture.componentInstance;

    const otherPartsFacade = (supplierPartsComponent as any)['otherPartsFacade'];
    const updateSupplierPartAsBuiltSpy = spyOn(otherPartsFacade, 'setSupplierPartsAsBuilt');
    const updateSupplierPartAsPlannedSpy = spyOn(otherPartsFacade, 'setSupplierPartsAsPlanned');

    supplierPartsComponent.updateSupplierParts();


    expect(updateSupplierPartAsBuiltSpy).toHaveBeenCalledWith();
    expect(updateSupplierPartAsPlannedSpy).toHaveBeenCalledWith();

  });

  it('should handle updateCustomerParts including search', async () => {
    const { fixture } = await renderSupplierParts();
    const supplierPartsComponent = fixture.componentInstance;

    const otherPartsFacade = (supplierPartsComponent as any)['otherPartsFacade'];
    const updateSupplierPartAsBuiltSpy = spyOn(otherPartsFacade, 'setSupplierPartsAsBuilt');
    const updateSupplierPartAsPlannedSpy = spyOn(otherPartsFacade, 'setSupplierPartsAsPlanned');


    const search = 'test';
    supplierPartsComponent.updateSupplierParts(search);


    expect(updateSupplierPartAsBuiltSpy).toHaveBeenCalledWith(0, 50, [], toGlobalSearchAssetFilter(search, true), true);
    expect(updateSupplierPartAsPlannedSpy).toHaveBeenCalledWith(0, 50, [], toGlobalSearchAssetFilter(search, false), true);

  });


});
