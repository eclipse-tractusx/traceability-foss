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

import { OtherPartsState } from '@page/other-parts/core/other-parts.state';
import { PartsState } from '@page/parts/core/parts.state';
import { Part } from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { firstValueFrom } from 'rxjs';
import {
  OTHER_PARTS_MOCK_1,
  OTHER_PARTS_MOCK_2,
  OTHER_PARTS_MOCK_3,
  OTHER_PARTS_MOCK_4,
  OTHER_PARTS_MOCK_5,
  OTHER_PARTS_MOCK_6,
  OTHER_PARTS_MOCK_7,
  OTHER_PARTS_MOCK_8,
  OTHER_PARTS_MOCK_9,
} from '../../../../mocks/services/otherParts-mock/otherParts.test.model';
import { OtherPartsModule } from '../other-parts.module';
import { OtherPartsComponent } from './other-parts.component';

describe('Other Parts', () => {
  let otherPartsState: OtherPartsState;
  beforeEach(() => (otherPartsState = new OtherPartsState()));

  const renderOtherParts = ({ roles = [] } = {}) =>
    renderComponent(OtherPartsComponent, {
      imports: [OtherPartsModule],
      providers: [{ provide: OtherPartsState, useFactory: () => otherPartsState }, { provide: PartsState }],
      roles,
    });

  it('should render part header', async () => {
    await renderOtherParts();

    expect(screen.getByText('pageOtherParts.title')).toBeInTheDocument();
  });

  it('should render part table', async () => {
    await renderOtherParts();

    const tableElements = await waitFor(() => screen.getAllByTestId('table-component--test-id'));
    expect(tableElements.length).toEqual(1);
  });

  it('should render table and display correct amount of rows', async () => {
    await renderOtherParts();

    const tableElement = await waitFor(() => screen.getByTestId('table-component--test-id'));
    expect(tableElement).toBeInTheDocument();
    expect(tableElement.children[1].childElementCount).toEqual(4);
  });

  it('should render tabs', async () => {
    await renderOtherParts();
    const tabElements = await screen.findAllByRole('tab');

    expect(tabElements.length).toEqual(1);
  });

  it('should render selected parts information', async () => {
    await renderOtherParts({ roles: ['user'] });
    await screen.findByTestId('table-component--test-id');
    const selectedPartsInfo = await screen.getByText('page.selectedParts.info');

    expect(selectedPartsInfo).toBeInTheDocument();
  });

  it('should set currentSelectedParts correctly', async () => {
    const renderedComponent = await renderOtherParts();
    const expected = ['test', 'test2'] as unknown as Part[];

    renderedComponent.fixture.componentInstance.onMultiSelect(expected);
    expect(renderedComponent.fixture.componentInstance.selectedItems).toEqual([expected]);

    renderedComponent.fixture.componentInstance.onTabChange({ index: 1 } as any);
    renderedComponent.fixture.componentInstance.onMultiSelect(expected);
    expect(renderedComponent.fixture.componentInstance.selectedItems).toEqual([expected, expected]);
  });

  it('should clear currentSelectedParts', async () => {
    const { fixture } = await renderOtherParts();
    const { componentInstance } = fixture;

    const expected = ['test', 'test2'] as unknown as Part[];

    componentInstance.onMultiSelect(expected);
    componentInstance.onTabChange({ index: 1 } as any);
    componentInstance.onMultiSelect(expected);
    expect(componentInstance.selectedItems).toEqual([expected, expected]);

    componentInstance.onTabChange({ index: 0 } as any);
    componentInstance.clearSelected();
    expect(componentInstance.selectedItems).toEqual([[], expected]);
  });

  it('should remove only one item from current selection', async () => {
    const { fixture } = await renderOtherParts();
    const { componentInstance } = fixture;

    const expected = [{ id: 'test' }, { id: 'test2' }] as Part[];

    componentInstance.onMultiSelect(expected);
    componentInstance.onTabChange({ index: 1 } as any);
    componentInstance.onMultiSelect(expected);
    expect(componentInstance.selectedItems).toEqual([expected, expected]);

    componentInstance.onTabChange({ index: 0 } as any);
    componentInstance.removeItemFromSelection({ id: 'test' } as any);
    expect(componentInstance.selectedItems).toEqual([[{ id: 'test2' }] as Part[], expected]);
  });

  describe('onTableConfigChange', () => {
    it('should request supplier parts if first tab is selected', async () => {
      await renderOtherParts({ roles: ['user'] });
      fireEvent.click(screen.getByText('pageOtherParts.tab.supplier'));

      await waitFor(() => expect(screen.getByText('table.partsColumn.manufacturer')).toBeInTheDocument());
      fireEvent.click(screen.getByText('table.partsColumn.manufacturer'));

      const supplierParts = await firstValueFrom(otherPartsState.supplierParts$);
      await waitFor(() =>
        expect(supplierParts).toEqual({
          data: {
            content: [
              PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_6),
              PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_7),
              PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_8),
              PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_9),
            ],
            page: 0,
            pageCount: 1,
            pageSize: 10,
            totalItems: 5,
          },
          error: undefined,
          loader: undefined,
        }),
      );
    });

    // Disabled until BE catches up WIP
    xit('should request customer parts if second tab is selected', async () => {
      const fixture = await renderOtherParts({ roles: ['user'] });

      fireEvent.click(screen.getByText('pageOtherParts.tab.customer'));

      await waitFor(() => expect(screen.getByText('partDetail.manufacturer')).toBeInTheDocument());
      fireEvent.click(screen.getByText('partDetail.manufacturer'));

      const customerParts = await firstValueFrom(otherPartsState.customerParts$);
      await waitFor(() =>
        expect(customerParts).toEqual({
          data: {
            content: [
              PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_1),
              PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_2),
              PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_3),
              PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_4),
              PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_5),
            ],
            page: 0,
            pageCount: 1,
            pageSize: 10,
            totalItems: 5,
          },
          error: undefined,
          loader: undefined,
        }),
      );
    });
  });

  it('should add item to current list', async () => {
    const { fixture } = await renderOtherParts({ roles: ['user'] });
    const expectedPart = PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_6);

    fireEvent.click(screen.getByText('pageOtherParts.tab.supplier'));

    const checkbox = await waitFor(() => screen.getAllByTestId('select-one--test-id')[0]);
    fireEvent.click(checkbox.firstChild);

    const selectedText_1 = await waitFor(() => screen.getByText('page.selectedParts.info'));
    expect(selectedText_1).toBeInTheDocument();
    expect(fixture.componentInstance.currentSelectedItems).toEqual([expectedPart]);

    // Disabled until BE catches up WIP
    /* fireEvent.click(screen.getByText('pageOtherParts.tab.customer'));

    const selectedText_2 = await waitFor(() => screen.getByText('page.selectedParts.info'));
    expect(selectedText_2).toBeInTheDocument();
    fixture.componentInstance.clearSelected();
    await waitFor(() => expect(fixture.componentInstance.currentSelectedItems).toEqual([]));*/
  });
});
