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

import { LayoutModule } from '@layout/layout.module';
import { SidenavComponent } from '@layout/sidenav/sidenav.component';
import { SidenavService } from '@layout/sidenav/sidenav.service';
import { Owner } from '@page/parts/model/owner.enum';
import { AssetAsBuiltFilter, AssetAsPlannedFilter, FilterOperator } from '@page/parts/model/parts.model';
import { PartsComponent } from '@page/parts/presentation/parts.component';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { toAssetFilter, toGlobalSearchAssetFilter } from '@shared/helper/filter-helper';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { QuickfilterService } from '@shared/service/quickfilter.service';
import { SharedModule } from '@shared/shared.module';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { of } from 'rxjs';
import { PartsModule } from '../parts.module';

const mockQuickfilterService = {
  setOwner: jasmine.createSpy('setOwner'),
  getOwner: jasmine.createSpy('getOwner').and.returnValue(Owner.UNKNOWN),
  isQuickFilterSet: jasmine.createSpy('isQuickFilterSet').and.returnValue(false),
  // For completeness, if the component ever subscribes to owner$:
  owner$: of(Owner.UNKNOWN),
} as Partial<QuickfilterService> as QuickfilterService;

describe('Parts', () => {

  const renderParts = () => {

    return renderComponent(PartsComponent, {
      declarations: [ SidenavComponent ],
      imports: [ PartsModule, SharedModule, LayoutModule ],
      providers: [ { provide: SidenavService }, { provide: PartDetailsFacade } ],
      roles: [ 'admin', 'wip' ],
    });
  };

  it('should render split areas', async () => {
    await renderParts();
    expect(await waitFor(() => screen.getByTestId('as-split-area-1-component--test-id'))).toBeInTheDocument();
    expect(await waitFor(() => screen.getByTestId('as-split-area-2-component--test-id'))).toBeInTheDocument();
  });


  it('should have correct sizes for split areas', async () => {
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;
    expect(componentInstance.bomLifecycleSize.asPlannedSize).toBe(50);
    expect(componentInstance.bomLifecycleSize.asBuiltSize).toBe(50);
  });


  it('should call partsFacade.setPartsAsBuiltWithFilter when filter is set', async () => {

    const { fixture } = await renderParts();
    const { componentInstance } = fixture;
    // Arrange
    const assetAsBuiltFilter: AssetAsBuiltFilter = {
      id: { value: [ { value: '123', strategy: FilterOperator.EQUAL } ], operator: 'OR' },
    };
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsBuilt');

    componentInstance.asBuiltFilterActivated(assetAsBuiltFilter);


    expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], assetAsBuiltFilter);
  });

  it('should call partsFacade.setPartsAsPlannedWithFilter when filter is set', async () => {

    const { fixture } = await renderParts();
    const { componentInstance } = fixture;
    // Arrange
    const assetAsPlannedFilter: AssetAsPlannedFilter = {
      id: { value: [ { value: '123', strategy: FilterOperator.EQUAL } ], operator: 'OR' },
    };
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsPlanned');

    componentInstance.asPlannedFilterActivated(assetAsPlannedFilter);

    expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], assetAsPlannedFilter);
  });

  it('should call partsFacade.setPartsAsBuilt when filter is not set', async () => {

    const { fixture } = await renderParts();
    const { componentInstance } = fixture;

    const assetAsBuiltFilter: AssetAsBuiltFilter = {};
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsBuilt');

    // Act
    componentInstance.asBuiltFilterActivated(assetAsBuiltFilter);

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], null);
  });

  it('should call partsFacade.setPartsAsBuilt with the correct parameters', async () => {
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;

    const page = 1; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = [ 'id', 'asc' ] as TableHeaderSort;
    componentInstance.ctrlKeyState = true;
    // Access the private partsFacade property
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsBuilt');

    // Act
    componentInstance['onAsBuiltTableConfigChange']({ page, pageSize, sorting }); // Access private method

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsBuiltSortList']);
  });

  it('should call partsFacade.setPartsAsBuilt with the correct parameters and page 0 no ctrlkey pressed', async () => {
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;

    const page = 0; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = [ 'id', 'asc' ] as TableHeaderSort;
    componentInstance.assetAsBuiltFilter = {
      id: { value: [ { value: 'urn', strategy: FilterOperator.EQUAL } ], operator: 'OR' },
    };
    componentInstance.ctrlKeyState = false;

    // Access the private partsFacade property
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsBuilt');

    // Act
    componentInstance['onAsBuiltTableConfigChange']({ page, pageSize, sorting }); // Access private method

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(0, pageSize, componentInstance['tableAsBuiltSortList'], toAssetFilter(componentInstance.assetAsBuiltFilter, true));

  });


  it('should call partsFacade.setPartsAsPlanned with the correct parameters and page 0 no ctrlkey pressed', async () => {
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;

    const page = 0; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = [ 'id', 'asc' ] as TableHeaderSort;

    componentInstance.assetsAsPlannedFilter = {
      id: { value: [ { value: 'urn', strategy: FilterOperator.EQUAL } ], operator: 'OR' },

    };
    componentInstance.ctrlKeyState = false;

    // Access the private partsFacade property
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsPlanned');

    // Act
    componentInstance['onAsPlannedTableConfigChange']({ page, pageSize, sorting }); // Access private method

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(0, pageSize, componentInstance['tableAsPlannedSortList'], toAssetFilter(componentInstance.assetsAsPlannedFilter, true));

  });

  it('should call partsFacade.setPartsAsBuilt with the correct parameters no ctrlkey pressed', async () => {
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;

    const page = 1; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = [ 'id', 'asc' ] as TableHeaderSort;
    componentInstance.ctrlKeyState = false;

    // Access the private partsFacade property
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsBuilt');
    // Set up QueryList of QuickFilterComponent
    // Act
    componentInstance['onAsPlannedTableConfigChange']({ page, pageSize, sorting }); // Access private method
    // Act
    componentInstance['onAsBuiltTableConfigChange']({ page, pageSize, sorting }); // Access private method

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsBuiltSortList']);
  });


  it('should call partsFacade.setPartsAsPlanned with the correct parameters', async () => {
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;

    const page = 1; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = [ 'id', 'asc' ] as TableHeaderSort;
    componentInstance.ctrlKeyState = true;

    // Access the private partsFacade property
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsPlanned');

    // Act
    componentInstance['onAsPlannedTableConfigChange']({ page, pageSize, sorting }); // Access private method

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsPlannedSortList']);
  });

  it('should call partsFacade.setPartsAsPlanned with the correct parameters  and ctrlkey not pressed', async () => {
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;

    const page = 1; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = [ 'id', 'asc' ] as TableHeaderSort;
    componentInstance.ctrlKeyState = false;

    // Access the private partsFacade property
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsPlanned');

    // Act
    componentInstance['onAsPlannedTableConfigChange']({ page, pageSize, sorting }); // Access private method

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsPlannedSortList']);
  });


  it('should set selectedPart in PartDetailsFacade correctly', async () => {
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;
    const sampleEvent: Record<string, unknown> = { id: 123, name: 'Sample Part' };

    componentInstance.onSelectItem(sampleEvent);
    const partDetailsFacade = (componentInstance as any)['partDetailsFacade'];
    expect(partDetailsFacade.selectedPart).toEqual(sampleEvent);
  });

  it('should resetTableSortingList on correct values', async () => {
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;

    const page = 1; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = null;

    // Access the private partsFacade property
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsBuilt');

    // Act
    componentInstance['onAsBuiltTableConfigChange']({ page, pageSize, sorting }); // Access private method

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsBuiltSortList']);
  });

  it('should clear filters and call partsFacade methods with search value', async () => {

    const { fixture } = await renderParts();
    const { componentInstance } = fixture;
    // Arrange
    const searchValue = [ 'searchTerm' ];

    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsBuilt');
    const partsFacadeAsPlannedSpy = spyOn(partsFacade, 'setPartsAsPlanned');
    componentInstance.searchControl.setValue(searchValue[0]);


    // Act
    componentInstance.triggerPartSearch();

    // Assert
    expect(partsFacadeAsPlannedSpy).toHaveBeenCalledWith(0, 50, [], toGlobalSearchAssetFilter(searchValue, false), true);
    expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], toGlobalSearchAssetFilter(searchValue, true), true);
  });

  it('should updatePartsByOwner', async () => {

    const { fixture } = await renderParts();
    const { componentInstance } = fixture;
    // Arrange
    const searchValue = 'searchTerm';

    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsBuilt');
    const partsFacadeAsPlannedSpy = spyOn(partsFacade, 'setPartsAsPlanned');
    componentInstance.searchControl.setValue(searchValue);


    // Act
    componentInstance.updatePartsByOwner(Owner.OWN);

    let filter = {
      owner: { value: [ { value: Owner.OWN, strategy: FilterOperator.EQUAL } ], operator: 'OR' },
    };

    // Assert
    expect(partsFacadeAsPlannedSpy).toHaveBeenCalledWith(0, 50, [], filter, true);
    expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], filter, true);
  });

  it('should reset asBuilt to half size when already on full width and save user settings', async () => {

    // Arrange
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;
    const userSettingsService = (componentInstance as any)['userSettingService'];
    const setUserSettingsSpy = spyOn(userSettingsService, 'setUserSettings');

    componentInstance.bomLifecycleSize.asBuiltSize = 100;
    // Act
    componentInstance.maximizeClicked(TableType.AS_BUILT_OWN);

    // Assert

    const expectedBomLifeCycle = { asBuiltSize: 50, asPlannedSize: 50 };
    expect(setUserSettingsSpy).toHaveBeenCalledWith(expectedBomLifeCycle);
  });

  it('should set asBuilt to full size when and save user settings', async () => {

    // Arrange
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;
    const userSettingsService = (componentInstance as any)['userSettingService'];
    const setUserSettingsSpy = spyOn(userSettingsService, 'setUserSettings');

    componentInstance.bomLifecycleSize.asBuiltSize = 50;
    // Act
    componentInstance.maximizeClicked(TableType.AS_BUILT_OWN);

    // Assert
    const expectedBomLifeCycle = { asBuiltSize: 100, asPlannedSize: 0 };
    expect(setUserSettingsSpy).toHaveBeenCalledWith(expectedBomLifeCycle);
  });

  it('should reset asPlanned to half size when already on full width and save user settings', async () => {

    // Arrange
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;
    const userSettingsService = (componentInstance as any)['userSettingService'];
    const setUserSettingsSpy = spyOn(userSettingsService, 'setUserSettings');

    componentInstance.bomLifecycleSize.asPlannedSize = 100;
    // Act
    componentInstance.maximizeClicked(TableType.AS_PLANNED_OWN);

    // Assert
    const expectedBomLifeCycle = { asBuiltSize: 50, asPlannedSize: 50 };
    expect(setUserSettingsSpy).toHaveBeenCalledWith(expectedBomLifeCycle);
  });

  it('should set asPlanned to full size when and save user settings', async () => {

    // Arrange
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;
    const userSettingsService = (componentInstance as any)['userSettingService'];
    const setUserSettingsSpy = spyOn(userSettingsService, 'setUserSettings');

    componentInstance.bomLifecycleSize.asPlannedSize = 50;
    // Act
    componentInstance.maximizeClicked(TableType.AS_PLANNED_OWN);

    // Assert
    const expectedBomLifeCycle = { asBuiltSize: 0, asPlannedSize: 100 };
    expect(setUserSettingsSpy).toHaveBeenCalledWith(expectedBomLifeCycle);
  });

  it('should not filter if filter search is unset', async () => {

    const { fixture } = await renderParts();
    const { componentInstance } = fixture;
    // Arrange
    const searchValue = '';

    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsBuilt');
    const partsFacadeAsPlannedSpy = spyOn(partsFacade, 'setPartsAsPlanned');
    componentInstance.searchControl.setValue(searchValue);


    // Act
    componentInstance.triggerPartSearch();

    // Assert
    expect(partsFacadeAsPlannedSpy).toHaveBeenCalledWith();
    expect(partsFacadeSpy).toHaveBeenCalledWith();
  });

  it('shouldRefreshPartsOnPublishAction', async function() {
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;


  });

  it('should show success toast and refresh parts on successful publish', async () => {
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;
    const partsFacade = (componentInstance as any)['partsFacade'];
    const toastService = componentInstance.toastService;
    spyOn(toastService, 'success');
    spyOn(partsFacade, 'setPartsAsBuilt');
    spyOn(partsFacade, 'setPartsAsPlanned');

    componentInstance.refreshPartsOnPublish('');

    expect(toastService.success).toHaveBeenCalledWith('requestPublishAssets.success');
    expect(partsFacade.setPartsAsBuilt).toHaveBeenCalled();
    expect(partsFacade.setPartsAsPlanned).toHaveBeenCalled();
  });

  it('should show error toast and not refresh parts on failed publish', async () => {
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;
    const partsFacade = (componentInstance as any)['partsFacade'];
    const toastService = componentInstance.toastService;
    spyOn(toastService, 'error');
    spyOn(partsFacade, 'setPartsAsBuilt');
    spyOn(partsFacade, 'setPartsAsPlanned');

    componentInstance.refreshPartsOnPublish('Error message');

    expect(toastService.error).toHaveBeenCalledWith('Error message');
    expect(partsFacade.setPartsAsBuilt).not.toHaveBeenCalled();
    expect(partsFacade.setPartsAsPlanned).not.toHaveBeenCalled();
  });


  it('should clear input and reset filter', async () => {

    const { fixture } = await renderParts();
    const { componentInstance } = fixture;
    const searchValue = [ 'SO' ];

    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsBuilt');
    const partsFacadeAsPlannedSpy = spyOn(partsFacade, 'setPartsAsPlanned');
    componentInstance.searchControl.setValue(searchValue[0]);

    componentInstance.triggerPartSearch();
    expect(partsFacadeAsPlannedSpy).toHaveBeenCalledWith(0, 50, [], toGlobalSearchAssetFilter(searchValue, false), true);
    expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], toGlobalSearchAssetFilter(searchValue, true), true);
    expect(componentInstance.chipItems.length).toBeGreaterThan(0);
    expect(componentInstance.visibleChips.length).toBeGreaterThan(0);

    componentInstance.clearInput();

    expect(componentInstance.searchControl.value).toEqual('');
    expect(componentInstance.chipItems).toEqual([]);
    expect(componentInstance.visibleChips).toEqual([]);
    expect(partsFacadeAsPlannedSpy).toHaveBeenCalledWith();
    expect(partsFacadeSpy).toHaveBeenCalledWith();

  });


  it('should remove chip', async () => {

    const { fixture } = await renderParts();
    const { componentInstance } = fixture;
    const searchValue = [ 'SO PO' ];
    componentInstance.searchControl.setValue(searchValue[0]);

    componentInstance.triggerPartSearch();
    expect(componentInstance.chipItems.length).toEqual(2);
    expect(componentInstance.visibleChips.length).toEqual(2);

    componentInstance.remove('SO');

    expect(componentInstance.chipItems).toContain('PO');
    expect(componentInstance.visibleChips).toContain('PO');

  });

  it('should return true if the value is a valid record',  async() => {
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;
    const value = { key1: ['value1', 'value2'], key2: ['value3'] };
    const result = componentInstance.isRecord(value);
    expect(result).toBeTrue();
  });

  it('should return false if the value is an object but not a valid record',  async() => {
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;
    const value = { key1: 'value1', key2: 'value2' };
    const result = componentInstance.isRecord(value);
    expect(result).toBeFalse();
  });

  it('should return false if the value is not an object',  async() => {
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;
    const value = 'some string';
    const result = componentInstance.isRecord(value);
    expect(result).toBeFalse();
  });

  it('should return false if the value is null',  async() => {
    const { fixture } = await renderParts();
    const { componentInstance } = fixture;
    const value = null;
    const result = componentInstance.isRecord(value);
    expect(result).toBeFalse();
  });

});
