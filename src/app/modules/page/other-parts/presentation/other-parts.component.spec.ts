/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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
import { screen, waitFor } from '@testing-library/angular';
import { server } from '@tests/mock-test-server';
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
  beforeAll(() => server.start());
  afterEach(() => server.resetHandlers());
  afterAll(() => server.stop());

  let otherPartsState: OtherPartsState;
  beforeEach(() => (otherPartsState = new OtherPartsState()));

  const renderOtherParts = () =>
    renderComponent(OtherPartsComponent, {
      imports: [OtherPartsModule],
      providers: [{ provide: OtherPartsState, useFactory: () => otherPartsState }, { provide: PartsState }],
      translations: ['page.otherParts'],
    });

  it('should render part header', async () => {
    await renderOtherParts();

    expect(screen.getByText('Other Parts')).toBeInTheDocument();
  });

  it('should render part table', async () => {
    await renderOtherParts();

    expect((await screen.findAllByTestId('table-component--test-id')).length).toEqual(1);
  });

  it('should render table and display correct amount of rows', async () => {
    await renderOtherParts();

    const tableElement = await screen.findByTestId('table-component--test-id');
    expect(tableElement).toBeInTheDocument();
    expect(tableElement.children[1].childElementCount).toEqual(4);
  });

  it('should render tabs', async () => {
    await renderOtherParts();
    const tabElements = await screen.findAllByRole('tab');

    expect(tabElements.length).toEqual(2);
  });

  it('should render selected parts information', async () => {
    await renderOtherParts();
    await screen.findByTestId('table-component--test-id');
    const selectedPartsInfo = await screen.getByText('0 Parts selected for this page.');

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
      await renderOtherParts();

      const supplierTabElement = screen.getByText('Supplier Parts');
      supplierTabElement.click();

      await waitFor(() => expect(screen.getByText('Manufacturer')).toBeInTheDocument());
      const tableHeaderElement = screen.getByText('Manufacturer');
      tableHeaderElement.click();

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

    it('should request customer parts if second tab is selected', async () => {
      await renderOtherParts();

      const customerTabElement = screen.getByText('Customer Parts');
      customerTabElement.click();

      await waitFor(() => expect(screen.getByText('Manufacturer')).toBeInTheDocument());
      const tableHeaderElement = screen.getByText('Manufacturer');
      tableHeaderElement.click();

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
    const { fixture } = await renderOtherParts();
    const expectedPart = PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_6);

    const supplierTabElement = screen.getByText('Supplier Parts');
    supplierTabElement.click();

    const selectAllElement = await waitFor(() => screen.getAllByTestId('select-one--test-id')[0]);
    (selectAllElement.firstChild as HTMLElement).click();

    const selectedText_1 = await waitFor(() => screen.getByText('1 Part selected for this page.'));
    expect(selectedText_1).toBeInTheDocument();
    expect(fixture.componentInstance.currentSelectedItems).toEqual([expectedPart]);

    const customerTabElement = screen.getByText('Customer Parts');
    customerTabElement.click();

    const selectedText_2 = await waitFor(() => screen.getByText('0 Parts selected for this page.'));
    expect(selectedText_2).toBeInTheDocument();
    fixture.componentInstance.clearSelected();
    await waitFor(() => expect(fixture.componentInstance.currentSelectedItems).toEqual([]));
  });
});
