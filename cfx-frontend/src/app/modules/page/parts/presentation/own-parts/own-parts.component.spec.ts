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

import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { toAssetFilter, toGlobalSearchAssetFilter } from '@shared/helper/filter-helper';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

import { OwnPartsComponent } from './own-parts.component';
import { TableEventConfig, TableHeaderSort } from '@shared/components/table/table.model';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { Role } from '@core/user/role.model';
import { PartsModule } from '@page/parts/parts.module';
import { AssetAsDesignedFilter, AssetAsOrderedFilter, AssetAsSupportedFilter, AssetAsRecycledFilter, AssetAsBuiltFilter, AssetAsPlannedFilter } from '@page/parts/model/parts.model';

describe('OwnPartsComponent', () => {
  const renderOwnPartsAsBuilt = ({ roles = [] } = {}) =>
    renderComponent(OwnPartsComponent, {
      imports: [PartsModule],
      roles,
      componentInputs: {
        bomLifecycle: MainAspectType.AS_BUILT,
      },
    });

  const renderOwnPartsAsPlanned = ({ roles = [] } = {}) =>
    renderComponent(OwnPartsComponent, {
      imports: [PartsModule],
      roles,
      componentInputs: {
        bomLifecycle: MainAspectType.AS_PLANNED,
      },
    });

  const renderOwnParts = (type: MainAspectType, { roles = [] } = {}) =>
    renderComponent(OwnPartsComponent, {
      imports: [PartsModule],
      roles,
      componentInputs: {
        bomLifecycle: type,
      },
    });


  it('should render part table', async () => {
    await renderOwnPartsAsBuilt();

    const tableElements = await waitFor(() => screen.getAllByTestId('table-component--test-id'));
    expect(tableElements.length).toEqual(1);
  });

  it('should render table and display correct amount of rows', async () => {
    await renderOwnPartsAsBuilt();

    const tableElement = await waitFor(() => screen.getByTestId('table-component--test-id'));
    expect(tableElement).toBeInTheDocument();
    expect(tableElement.children[1].childElementCount).toEqual(5);
  });

  it('sort parts after id column', async () => {
    const { fixture } = await renderOwnPartsAsBuilt({ roles: [Role.ADMIN] });
    const ownPartsComponent = fixture.componentInstance;

    let nameHeader = await screen.findByText('table.column.id');
    fireEvent.click(nameHeader);

    expect(ownPartsComponent['tableAsBuiltSortList']).toEqual([['id', 'asc']]);
  });

  // TODO: fix test
  it('should multisort after column name and id', async () => {
    const { fixture } = await renderOwnPartsAsBuilt({ roles: [Role.ADMIN] });
    const ownPartsComponent = fixture.componentInstance;

    let nameHeader = await screen.findByText('table.column.nameAtManufacturer');
    fireEvent.click(nameHeader);
    let idHeader = await screen.findByText('table.column.id');

    await waitFor(() => {
      fireEvent.keyDown(idHeader, {
        ctrlKey: true,
        charCode: 17,
      });
    });
    expect(ownPartsComponent['ctrlKeyState']).toBeTruthy();
    await waitFor(() => {
      fireEvent.click(idHeader);
    });

    await waitFor(() => {
      fireEvent.keyUp(idHeader, {
        ctrlKey: true,
        charCode: 17,
      });
    });

    await waitFor(() => {
      fireEvent.click(idHeader);
    });
    expect(ownPartsComponent['tableAsBuiltSortList']).toEqual([
      ['nameAtManufacturer', 'asc'],
      ['id', 'desc'],
    ]);
  });

  it('should handle updateOwnParts null for AsBuilt', async () => {
    const { fixture } = await renderOwnPartsAsBuilt();
    const ownPartsComponent = fixture.componentInstance;

    spyOn(ownPartsComponent.partsFacade, 'setPartsAsBuilt');

    ownPartsComponent.updateOwnParts();

    expect(ownPartsComponent.partsFacade.setPartsAsBuilt).toHaveBeenCalledWith();
  });

  it('should handle updateOwnParts null for AsPlanned', async () => {
    const { fixture } = await renderOwnPartsAsPlanned();
    const ownPartsComponent = fixture.componentInstance;

    spyOn(ownPartsComponent.partsFacade, 'setPartsAsPlanned');

    ownPartsComponent.updateOwnParts();

    expect(ownPartsComponent.partsFacade.setPartsAsPlanned).toHaveBeenCalledWith();
  });

  it('should handle updateOwnParts including search for AsBuilt', async () => {
    const { fixture } = await renderOwnPartsAsBuilt();
    const ownPartsComponent = fixture.componentInstance;

    spyOn(ownPartsComponent.partsFacade, 'setPartsAsBuilt');

    const search = 'test';
    ownPartsComponent.updateOwnParts(search);

    expect(ownPartsComponent.partsFacade.setPartsAsBuilt).toHaveBeenCalledWith(
      0,
      50,
      [],
      toGlobalSearchAssetFilter(search, true),
      true,
    );
  });

  it('should handle updateOwnParts including search for AsPlanned', async () => {
    const { fixture } = await renderOwnPartsAsPlanned();
    const ownPartsComponent = fixture.componentInstance;

    spyOn(ownPartsComponent.partsFacade, 'setPartsAsPlanned');

    const search = 'test';
    ownPartsComponent.updateOwnParts(search);

    expect(ownPartsComponent.partsFacade.setPartsAsPlanned).toHaveBeenCalledWith(
      0,
      50,
      [],
      toGlobalSearchAssetFilter(search, false),
      true,
    );
  });

  it('should set the default Pagination by recieving a size change event', async () => {
    const { fixture } = await renderOwnPartsAsBuilt();
    const ownPartsComponent = fixture.componentInstance;

    ownPartsComponent.onDefaultPaginationSizeChange(100);
    expect(ownPartsComponent.DEFAULT_PAGE_SIZE).toEqual(100);
  });

  it('should use the default page size if the page size in the onAsBuiltTableConfigChange is given as 0', async () => {
    const { fixture } = await renderOwnPartsAsBuilt();
    const ownPartsComponent = fixture.componentInstance;

    const pagination: TableEventConfig = { page: 0, pageSize: 0, sorting: ['name', 'asc'] };
    spyOn(ownPartsComponent.partsFacade, 'setPartsAsBuilt');

    ownPartsComponent.onAsBuiltTableConfigChange(pagination);
    fixture.detectChanges();
    expect(ownPartsComponent.partsFacade.setPartsAsBuilt).toHaveBeenCalledWith(0, 50, [['name', 'asc']], toAssetFilter(null, true), false);

  });

  it('should use the given page size if the page size in the onAsBuiltTableConfigChange is given as not 0', async () => {
    const { fixture } = await renderOwnPartsAsBuilt();
    const ownPartsComponent = fixture.componentInstance;

    const pagination: TableEventConfig = { page: 0, pageSize: 10, sorting: ['name', 'asc'] };
    spyOn(ownPartsComponent.partsFacade, 'setPartsAsBuilt');

    ownPartsComponent.onAsBuiltTableConfigChange(pagination);
    fixture.detectChanges();
    expect(ownPartsComponent.partsFacade.setPartsAsBuilt).toHaveBeenCalledWith(0, 10, [['name', 'asc']], toAssetFilter(null, true), false);

  });

  it('should use the default page size if the page size in the onAsPlannedTableConfigChange is given as 0', async () => {
    const { fixture } = await renderOwnPartsAsPlanned();
    const ownPartsComponent = fixture.componentInstance;

    const pagination: TableEventConfig = { page: 0, pageSize: 0, sorting: ['name', 'asc'] };
    spyOn(ownPartsComponent.partsFacade, 'setPartsAsPlanned');

    ownPartsComponent.onAsPlannedTableConfigChange(pagination);
    fixture.detectChanges();
    expect(ownPartsComponent.partsFacade.setPartsAsPlanned).toHaveBeenCalledWith(0, 50, [['name', 'asc']], toAssetFilter(null, false), false);
  });

  it('should use the given page size if the page size in the onAsPlannedTableConfigChange is given as not 0', async () => {
    const { fixture } = await renderOwnPartsAsPlanned();
    const ownPartsComponent = fixture.componentInstance;

    const pagination: TableEventConfig = { page: 0, pageSize: 10, sorting: ['name', 'asc'] };
    spyOn(ownPartsComponent.partsFacade, 'setPartsAsPlanned');

    ownPartsComponent.onAsPlannedTableConfigChange(pagination);
    fixture.detectChanges();
    expect(ownPartsComponent.partsFacade.setPartsAsPlanned).toHaveBeenCalledWith(0, 10, [['name', 'asc']], toAssetFilter(null, false), false);
  });

  it('should pass on the filtering to the api services for As_Built', async () => {
    const { fixture } = await renderOwnPartsAsBuilt();
    const ownPartsComponent = fixture.componentInstance;

    const assetFilterAsBuilt = {
      "select": [],
      "semanticDataModel": [],
      "nameAtManufacturer": "value1",
      "manufacturerName": [],
      "manufacturerPartId": [],
      "semanticModelId": [],
      "manufacturingDate": [],
      "qualityAlertsInStatusActive": [],
      "qualityInvestigationsInStatusActive": []
    };
    spyOn(ownPartsComponent.partsFacade, 'setPartsAsBuilt');


    ownPartsComponent.filterActivated(MainAspectType.AS_BUILT, assetFilterAsBuilt);
    fixture.detectChanges();
    expect(ownPartsComponent.partsFacade.setPartsAsBuilt).toHaveBeenCalledWith(0, 50, [], toAssetFilter(assetFilterAsBuilt, true), false);
  });

  it('should pass on the filtering to the api services for As_Planned', async () => {
    const { fixture } = await renderOwnPartsAsPlanned();
    const ownPartsComponent = fixture.componentInstance;

    const assetFilterAsPlanned = {
      "select": [],
      "semanticDataModel": [],
      "nameAtManufacturer": "value2",
      "manufacturerName": [],
      "semanticModelId": [],
      "manufacturerPartId": []
    };
    spyOn(ownPartsComponent.partsFacade, 'setPartsAsPlanned');

    ownPartsComponent.filterActivated(MainAspectType.AS_PLANNED, assetFilterAsPlanned);
    fixture.detectChanges();
    expect(ownPartsComponent.partsFacade.setPartsAsPlanned).toHaveBeenCalledWith(0, 50, [], toAssetFilter(assetFilterAsPlanned, false), false);
  });

  it('should set selected part and open detail page when item is selected', async () => {
    const { fixture } = await renderOwnPartsAsBuilt();
    const ownPartsComponent = fixture.componentInstance;
    const router = TestBed.inject(Router);

    const part: any = { id: '1', mainAspectType: MainAspectType.AS_BUILT };

    spyOn(router, 'navigate').and.stub();

    const partDetailsFacade = (ownPartsComponent as any)['partDetailsFacade'];

    ownPartsComponent.onSelectItem(part);

    expect(partDetailsFacade.selectedPart).toEqual(part);
    expect(router.navigate).toHaveBeenCalledWith(['/parts/1'], { queryParams: { type: part.mainAspectType } });
  });

  it('should navigate to the correct detail page with the provided part', async () => {
    const { fixture } = await renderOwnPartsAsBuilt();
    const ownPartsComponent = fixture.componentInstance;
    const router = TestBed.inject(Router);

    const part: any = { id: '2', mainAspectType: MainAspectType.AS_BUILT };

    spyOn(router, 'navigate').and.stub();

    ownPartsComponent.openDetailPage(part);

    const expectedLink = '/parts/2';
    expect(router.navigate).toHaveBeenCalledWith([expectedLink], { queryParams: { type: part.mainAspectType } });
  });

  it('should call partsFacade.setPartsAsDesignedWithFilter when filter is set', async () => {

    const { fixture } = await renderOwnParts(MainAspectType.AS_DESIGNED);
    const { componentInstance } = fixture;
    // Arrange
    const assetAsDesignedFilter: AssetAsDesignedFilter = {
      id: "123"
    };
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsDesigned');

    componentInstance.filterActivated(MainAspectType.AS_DESIGNED, assetAsDesignedFilter);

    expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], assetAsDesignedFilter, false);
  });

  it('should call partsFacade.setPartsAsOrderedWithFilter when filter is set', async () => {

    const { fixture } = await renderOwnParts(MainAspectType.AS_ORDERED);
    const { componentInstance } = fixture;
    // Arrange
    const assetAsOrderedFilter: AssetAsOrderedFilter = {
      id: "123"
    };
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsOrdered');

    componentInstance.filterActivated(MainAspectType.AS_ORDERED, assetAsOrderedFilter);

    expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], assetAsOrderedFilter, false);
  });

  it('should call partsFacade.setPartsAsSupportedWithFilter when filter is set', async () => {

    const { fixture } = await renderOwnParts(MainAspectType.AS_SUPPORTED);
    const { componentInstance } = fixture;
    // Arrange
    const assetAsSupportedFilter: AssetAsSupportedFilter = {
      id: "123"
    };
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsSupported');

    componentInstance.filterActivated(MainAspectType.AS_SUPPORTED, assetAsSupportedFilter);

    expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], assetAsSupportedFilter, false);
  });

  it('should call partsFacade.setPartsAsRecycledWithFilter when filter is set', async () => {

    const { fixture } = await renderOwnParts(MainAspectType.AS_RECYCLED);
    const { componentInstance } = fixture;
    // Arrange
    const assetAsRecycledFilter: AssetAsRecycledFilter = {
      id: "123"
    };
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsRecycled');

    componentInstance.filterActivated(MainAspectType.AS_RECYCLED, assetAsRecycledFilter);


    expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], assetAsRecycledFilter, false);
  });

  it('should call partsFacade.setPartsAsBuilt when filter is not set', async () => {

    const { fixture } = await renderOwnParts(MainAspectType.AS_BUILT);
    const { componentInstance } = fixture;

    const assetAsBuiltFilter: AssetAsBuiltFilter = {};
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsBuilt');

    // Act
    componentInstance.filterActivated(MainAspectType.AS_BUILT, assetAsBuiltFilter);

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], null, false);
  });

  it('should call partsFacade.setPartsAsPlanned when filter is not set', async () => {

    const { fixture } = await renderOwnParts(MainAspectType.AS_PLANNED);
    const { componentInstance } = fixture;

    const assetAsPlannedFilter: AssetAsPlannedFilter = {};
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsPlanned');

    // Act
    componentInstance.filterActivated(MainAspectType.AS_PLANNED, assetAsPlannedFilter);

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], null, false);
  });

  it('should call partsFacade.setPartsAsDesigned when filter is not set', async () => {

    const { fixture } = await renderOwnParts(MainAspectType.AS_DESIGNED);
    const { componentInstance } = fixture;

    const assetFilter: AssetAsDesignedFilter = {};
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsDesigned');

    // Act
    componentInstance.filterActivated(MainAspectType.AS_DESIGNED, assetFilter);

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], null, false);
  });

  it('should call partsFacade.setPartsAsOrdered when filter is not set', async () => {

    const { fixture } = await renderOwnParts(MainAspectType.AS_ORDERED);
    const { componentInstance } = fixture;

    const assetFilter: AssetAsOrderedFilter = {};
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsOrdered');

    // Act
    componentInstance.filterActivated(MainAspectType.AS_ORDERED, assetFilter);

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], null, false);
  });

  it('should call partsFacade.setPartsAsSupported when filter is not set', async () => {

    const { fixture } = await renderOwnParts(MainAspectType.AS_SUPPORTED);
    const { componentInstance } = fixture;

    const assetFilter: AssetAsSupportedFilter = {};
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsSupported');

    // Act
    componentInstance.filterActivated(MainAspectType.AS_SUPPORTED, assetFilter);

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], null, false);
  });

  it('should call partsFacade.setPartsAsRecycled when filter is not set', async () => {

    const { fixture } = await renderOwnParts(MainAspectType.AS_RECYCLED);
    const { componentInstance } = fixture;

    const assetFilter: AssetAsRecycledFilter = {};
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsRecycled');

    // Act
    componentInstance.filterActivated(MainAspectType.AS_RECYCLED, assetFilter);

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], null, false);
  });

  it('should call partsFacade.setPartsAsBuilt with the correct parameters', async () => {
    const { fixture } = await renderOwnPartsAsBuilt();
    const { componentInstance } = fixture;

    const page = 1; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = ['id', 'asc'] as TableHeaderSort;
    componentInstance.ctrlKeyState = true;

    // Access the private partsFacade property
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsBuilt');

    // Act
    componentInstance['onAsBuiltTableConfigChange']({ page, pageSize, sorting }); // Access private method
    expect(componentInstance['tableAsBuiltSortList']).toBeTruthy();

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsBuiltSortList'], null, false);
  });

  it('should call partsFacade.setPartsAsBuilt with the correct parameters no ctrlkey pressed', async () => {
    const { fixture } = await renderOwnPartsAsBuilt();
    const { componentInstance } = fixture;

    const page = 1; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = ['id', 'asc'] as TableHeaderSort;
    componentInstance.ctrlKeyState = false;

    // Access the private partsFacade property
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsBuilt');

    // Act
    componentInstance['onAsBuiltTableConfigChange']({ page, pageSize, sorting }); // Access private method

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsBuiltSortList'], null, false);
  });

  it('should call partsFacade.setPartsAsPlanned with the correct parameters', async () => {
    const { fixture } = await renderOwnPartsAsPlanned();
    const { componentInstance } = fixture;

    const page = 1; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = ['id', 'asc'] as TableHeaderSort;
    componentInstance.ctrlKeyState = true;

    // Access the private partsFacade property
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsPlanned');

    // Act
    componentInstance['onAsPlannedTableConfigChange']({ page, pageSize, sorting }); // Access private method

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsPlannedSortList'], null, false);
  });

  it('should call partsFacade.setPartsAsPlanned with the correct parameters  and ctrlkey not pressed', async () => {
    const { fixture } = await renderOwnPartsAsPlanned();
    const { componentInstance } = fixture;

    const page = 1; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = ['id', 'asc'] as TableHeaderSort;
    componentInstance.ctrlKeyState = false;

    // Access the private partsFacade property
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsPlanned');

    // Act
    componentInstance['onAsPlannedTableConfigChange']({ page, pageSize, sorting }); // Access private method

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsPlannedSortList'], null, false);
  });

  it('should call partsFacade.setPartsAsDesigned with the correct parameters', async () => {
    const { fixture } = await renderOwnParts(MainAspectType.AS_DESIGNED);
    const { componentInstance } = fixture;

    const page = 1; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = ['id', 'asc'] as TableHeaderSort;
    componentInstance.ctrlKeyState = true;

    // Access the private partsFacade property
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsDesigned');

    // Act
    componentInstance['onAsDesignedTableConfigChange']({ page, pageSize, sorting }); // Access private method

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsDesignedSortList'], null, false);
  });

  it('should call partsFacade.setPartsAsOrdered with the correct parameters', async () => {
    const { fixture } = await renderOwnParts(MainAspectType.AS_ORDERED);
    const { componentInstance } = fixture;

    const page = 1; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = ['id', 'asc'] as TableHeaderSort;
    componentInstance.ctrlKeyState = true;

    // Access the private partsFacade property
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsOrdered');

    // Act
    componentInstance['onAsOrderedTableConfigChange']({ page, pageSize, sorting }); // Access private method

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsOrderedSortList'], null, false);
  });

  it('should call partsFacade.setPartsAsSupported with the correct parameters', async () => {
    const { fixture } = await renderOwnParts(MainAspectType.AS_SUPPORTED);
    const { componentInstance } = fixture;

    const page = 1; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = ['id', 'asc'] as TableHeaderSort;
    componentInstance.ctrlKeyState = true;

    // Access the private partsFacade property
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsSupported');

    // Act
    componentInstance['onAsSupportedTableConfigChange']({ page, pageSize, sorting }); // Access private method

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsSupportedSortList'], null, false);
  });

  it('should call partsFacade.setPartsAsRecycled with the correct parameters', async () => {
    const { fixture } = await renderOwnParts(MainAspectType.AS_RECYCLED);
    const { componentInstance } = fixture;

    const page = 1; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = ['id', 'asc'] as TableHeaderSort;
    componentInstance.ctrlKeyState = true;

    // Access the private partsFacade property
    const partsFacade = (componentInstance as any)['partsFacade'];
    const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsRecycled');

    // Act
    componentInstance['onAsRecycledTableConfigChange']({ page, pageSize, sorting }); // Access private method

    // Assert
    expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsRecycledSortList'], null, false);
  });

  it('should resetTableSortingList on correct values', async () => {
    const { fixture } = await renderOwnPartsAsBuilt();
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
    expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsBuiltSortList'], null, false);
  });

  it('should reset AsBuilt part table when resetTableSortingList is called', async () => {
    const { fixture } = await renderOwnPartsAsBuilt();
    const { componentInstance } = fixture;

    const page = 1; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = ['id', 'asc'] as TableHeaderSort;

    // Act
    componentInstance['onAsBuiltTableConfigChange']({ page, pageSize, sorting }); // Access private method
    expect(componentInstance['tableAsBuiltSortList'].length).not.toBe(0);

    // Act
    componentInstance['resetTableSortingList'](MainAspectType.AS_BUILT); // Access private method

    // Assert
    expect(componentInstance['tableAsBuiltSortList'].length).toBe(0);
  });

  it('should reset AsPlanned part table when resetTableSortingList is called', async () => {
    const { fixture } = await renderOwnPartsAsPlanned();
    const { componentInstance } = fixture;

    const page = 1; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = ['id', 'asc'] as TableHeaderSort;

    // Act
    componentInstance['onAsPlannedTableConfigChange']({ page, pageSize, sorting }); // Access private method
    expect(componentInstance['tableAsPlannedSortList'].length).not.toBe(0);

    // Act
    componentInstance['resetTableSortingList'](MainAspectType.AS_PLANNED); // Access private method

    // Assert
    expect(componentInstance['tableAsPlannedSortList'].length).toBe(0);
  });

  it('should reset AsDesigned part table when resetTableSortingList is called', async () => {
    const { fixture } = await renderOwnParts(MainAspectType.AS_DESIGNED);
    const { componentInstance } = fixture;

    const page = 1; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = ['id', 'asc'] as TableHeaderSort;

    // Act
    componentInstance['onAsDesignedTableConfigChange']({ page, pageSize, sorting }); // Access private method
    expect(componentInstance['tableAsDesignedSortList'].length).not.toBe(0);

    // Act
    componentInstance['resetTableSortingList'](MainAspectType.AS_DESIGNED); // Access private method

    // Assert
    expect(componentInstance['tableAsDesignedSortList'].length).toBe(0);
  });

  it('should reset AsOrdered part table when resetTableSortingList is called', async () => {
    const { fixture } = await renderOwnParts(MainAspectType.AS_ORDERED);
    const { componentInstance } = fixture;

    const page = 1; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = ['id', 'asc'] as TableHeaderSort;

    // Act
    componentInstance['onAsOrderedTableConfigChange']({ page, pageSize, sorting }); // Access private method
    expect(componentInstance['tableAsOrderedSortList'].length).not.toBe(0);

    // Act
    componentInstance['resetTableSortingList'](MainAspectType.AS_ORDERED); // Access private method

    // Assert
    expect(componentInstance['tableAsOrderedSortList'].length).toBe(0);
  });

  it('should reset AsSupported part table when resetTableSortingList is called', async () => {
    const { fixture } = await renderOwnParts(MainAspectType.AS_SUPPORTED);
    const { componentInstance } = fixture;

    const page = 1; // Set the page number
    const pageSize = 10; // Set the page size
    const sorting = ['id', 'asc'] as TableHeaderSort;

    // Act
    componentInstance['onAsSupportedTableConfigChange']({ page, pageSize, sorting }); // Access private method
    expect(componentInstance['tableAsSupportedSortList'].length).not.toBe(0);

    // Act
    componentInstance['resetTableSortingList'](MainAspectType.AS_SUPPORTED); // Access private method

    // Assert
    expect(componentInstance['tableAsSupportedSortList'].length).toBe(0);
  });

  it('should reset AsRecycled part table when resetTableSortingList is called', async () => {
    const { fixture } = await renderOwnParts(MainAspectType.AS_RECYCLED);
    const { componentInstance } = fixture;

    const page = 1;
    const pageSize = 10;
    const sorting = ['id', 'asc'] as TableHeaderSort;

    // Act
    componentInstance['onAsRecycledTableConfigChange']({ page, pageSize, sorting }); // Access private method
    expect(componentInstance['tableAsRecycledSortList'].length).not.toBe(0);

    // Act
    componentInstance['resetTableSortingList'](MainAspectType.AS_RECYCLED); // Access private method

    // Assert
    expect(componentInstance['tableAsRecycledSortList'].length).toBe(0);
  });

  // it('should clear filters and call partsFacade methods with search value as a date', async () => {

  //   const { fixture } = await renderOwnPartsAsBuilt();
  //   const { componentInstance } = fixture;
  //   // Arrange
  //   const searchValue = '1.20.23';
  //   // search value should be interpreted as 20.01.2023
  //   const partsFacade = (componentInstance as any)['partsFacade'];
  //   const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsBuilt');
  //   const partsFacadeAsPlannedSpy = spyOn(partsFacade, 'setPartsAsPlanned');
  //   componentInstance.searchControl.setValue(searchValue);


  //   // Act
  //   componentInstance.triggerPartSearch();

  //   // Assert
  //   expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], toGlobalSearchAssetFilter(searchValue, true, componentInstance.searchListAsBuilt, componentInstance.datePipe), true);
  //   expect(partsFacadeAsPlannedSpy).toHaveBeenCalledWith(0, 50, [], toGlobalSearchAssetFilter(searchValue, false, componentInstance.searchListAsPlanned, componentInstance.datePipe), true);
  // });

  // it('should not filter if filter search is unset', async () => {

  //   const { fixture } = await renderParts();
  //   const { componentInstance } = fixture;
  //   // Arrange
  //   const searchValue = '';

  //   const partsFacade = (componentInstance as any)['partsFacade'];
  //   const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsBuilt');
  //   const partsFacadeAsPlannedSpy = spyOn(partsFacade, 'setPartsAsPlanned');
  //   componentInstance.searchControl.setValue(searchValue);

  //   // Act
  //   componentInstance.triggerPartSearch();

  //   // Assert
  //   expect(partsFacadeAsPlannedSpy).toHaveBeenCalledWith(0, 50);
  //   expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50);
  // });

  it('should set the default Pagination by recieving a size change event', async () => {
    const { fixture } = await renderOwnPartsAsBuilt();
    const alertsComponent = fixture.componentInstance;

    alertsComponent.onDefaultPaginationSizeChange(100);
    expect(alertsComponent.DEFAULT_PAGE_SIZE).toEqual(100);
  });


});
