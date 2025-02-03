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
import { CustomerPartsComponent } from '@page/other-parts/presentation/customer-parts/customer-parts.component';
import { SupplierPartsComponent } from '@page/other-parts/presentation/supplier-parts/supplier-parts.component';
import { PartsState } from '@page/parts/core/parts.state';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { FormatPartSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-part-semantic-data-model-to-camelcase.pipe';
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
import { Role } from '@core/user/role.model';
import { RequestStepperComponent } from '@shared/components/request-notification/request-stepper/request-stepper.component';

describe('Other Parts', () => {
  let otherPartsState: OtherPartsState;
  let formatPartSemanticToCamelCase: FormatPartSemanticDataModelToCamelCasePipe;
  beforeEach(() => {
    otherPartsState = new OtherPartsState();
    formatPartSemanticToCamelCase = new FormatPartSemanticDataModelToCamelCasePipe();
  });

  const renderOtherParts = ({ roles = [] } = {}) =>
    renderComponent(OtherPartsComponent, {
      declarations: [RequestStepperComponent, SupplierPartsComponent, CustomerPartsComponent],
      imports: [OtherPartsModule],
      providers: [{ provide: OtherPartsState, useFactory: () => otherPartsState }, { provide: PartsState }],
      roles,
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
    expect(tableElement.children[1].childElementCount).toEqual(5);
  });

  it('should render tabs', async () => {
    await renderOtherParts();
    const tabElements = await screen.findAllByRole('tab');

    expect(tabElements.length).toEqual(4);
  });

  // it('should render selected parts information', async () => {
  //   await renderOtherParts({ roles: ['user'] });
  //   await screen.findByTestId('table-component--test-id');
  //   const selectedPartsInfo = await screen.getByText('page.selectedParts.info');

  //   expect(selectedPartsInfo).toBeInTheDocument();
  // });

  it('should set selectedTab correctly', async () => {
    const { fixture } = await renderOtherParts();
    const { componentInstance } = fixture;

    expect(componentInstance.selectedTab).toEqual(0);

    componentInstance.onTabChange({ index: 1 } as any);
    expect(componentInstance.selectedTab).toEqual(1);

    componentInstance.onTabChange({ index: 0 } as any);
    expect(componentInstance.selectedTab).toEqual(0);
  });

  describe('onAsBuiltTableConfigChange', () => {
    let formatPartSemanticToCamelCase: FormatPartSemanticDataModelToCamelCasePipe;
    beforeEach(() => {
      formatPartSemanticToCamelCase = new FormatPartSemanticDataModelToCamelCasePipe();
    });

    it('should request supplier parts if first tab is selected', async () => {
      await renderOtherParts({ roles: [Role.USER] });
      fireEvent.click(screen.getAllByText('pageOtherParts.tab.supplier')[0]);

      await waitFor(() => expect(screen.getByText('table.column.manufacturerName')).toBeInTheDocument());
      fireEvent.click(screen.getByText('table.column.manufacturerName'));

      const supplierParts = await firstValueFrom(otherPartsState.supplierPartsAsBuilt$);
      await waitFor(() =>
        expect(supplierParts).toEqual({
          data: {
            content: [
              formatPartSemanticToCamelCase.transform(PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_1, MainAspectType.AS_BUILT)),
              formatPartSemanticToCamelCase.transform(PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_2, MainAspectType.AS_BUILT)),
              formatPartSemanticToCamelCase.transform(PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_3, MainAspectType.AS_BUILT)),
              formatPartSemanticToCamelCase.transform(PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_4, MainAspectType.AS_BUILT)),
              formatPartSemanticToCamelCase.transform(PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_5, MainAspectType.AS_BUILT)),
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
      const fixture = await renderOtherParts({ roles: [Role.USER] });
      let tabs = screen.getAllByText('pageOtherParts.tab.customer');
      fireEvent.click(tabs[0]);

      await waitFor(() => expect(screen.getByText('table.column.manufacturerName')).toBeInTheDocument());
      fireEvent.click(screen.getByText('table.column.manufacturerName'));

      const customerParts = await firstValueFrom(otherPartsState.customerPartsAsBuilt$);
      await waitFor(() =>
        expect(customerParts).toEqual({
          data: {
            content: [
              formatPartSemanticToCamelCase.transform(PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_1, MainAspectType.AS_BUILT)),
              formatPartSemanticToCamelCase.transform(PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_2, MainAspectType.AS_BUILT)),
              formatPartSemanticToCamelCase.transform(PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_3, MainAspectType.AS_BUILT)),
              formatPartSemanticToCamelCase.transform(PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_4, MainAspectType.AS_BUILT)),
              formatPartSemanticToCamelCase.transform(PartsAssembler.assembleOtherPart(OTHER_PARTS_MOCK_5, MainAspectType.AS_BUILT)),
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

    it('should clear filters and call partsFacade methods with search value', async () => {

      const { fixture } = await renderOtherParts();
      const { componentInstance } = fixture;
      // Arrange
      const searchValue = 'searchTerm';

      const updateSupplierPartsSpy = spyOn(
        SupplierPartsComponent.prototype,
        'updateSupplierParts',
      );

      const updateCustomerPartsSpy = spyOn(
        CustomerPartsComponent.prototype,
        'updateCustomerParts',
      );

      componentInstance.searchControl.setValue(searchValue);


      // Act
      componentInstance.triggerPartSearch();

      // Assert
      expect(updateSupplierPartsSpy).toHaveBeenCalledWith(searchValue);
      expect(updateCustomerPartsSpy).toHaveBeenCalledWith(searchValue);
    });

    it('should trigger part search and reset filter', async () => {
      const { fixture } = await renderOtherParts();
      const { componentInstance } = fixture;
      const searchValue = 'testSearchValue';


      const updateSupplierPartsSpy = spyOn(
        SupplierPartsComponent.prototype,
        'updateSupplierParts',
      );

      const updateCustomerPartsSpy = spyOn(
        CustomerPartsComponent.prototype,
        'updateCustomerParts',
      );

      componentInstance.searchControl.setValue(searchValue);

      // Spy on the private method without calling it directly
      const resetFilterAndShowToastSpy = spyOn<any>(componentInstance, 'resetFilterAndShowToast');

      // Act
      componentInstance.triggerPartSearch();

      // Assert
      expect(updateSupplierPartsSpy).toHaveBeenCalledWith('testSearchValue');
      expect(updateCustomerPartsSpy).toHaveBeenCalledWith('testSearchValue');
      expect(resetFilterAndShowToastSpy).toHaveBeenCalledOnceWith();

    });
  });
});

