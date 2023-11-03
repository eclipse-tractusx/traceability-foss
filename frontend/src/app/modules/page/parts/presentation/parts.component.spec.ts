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
import { OtherPartsModule } from '@page/other-parts/other-parts.module';
import { PartsComponent } from '@page/parts/presentation/parts.component';
import { SharedModule } from '@shared/shared.module';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { PartsModule } from '../parts.module';
import { AssetAsBuiltFilter, AssetAsDesignedFilter, AssetAsOrderedFilter, AssetAsPlannedFilter, AssetAsRecycledFilter, AssetAsSupportedFilter } from "@page/parts/model/parts.model";
import { TableHeaderSort } from "@shared/components/table/table.model";
import { PartDetailsFacade } from "@shared/modules/part-details/core/partDetails.facade";
import { toGlobalSearchAssetFilter } from "@shared/helper/filter-helper";
import { MainAspectType } from '../model/mainAspectType.enum';

describe('Parts', () => {

    const renderParts = () => {

        return renderComponent(PartsComponent, {
            declarations: [SidenavComponent],
            imports: [PartsModule, SharedModule, LayoutModule, OtherPartsModule],
            providers: [{ provide: SidenavService }, { provide: PartDetailsFacade }],
            roles: ['admin', 'wip'],
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
        expect(componentInstance.bomLifecycleSize.asDesignedSize).toBe(0);
        expect(componentInstance.bomLifecycleSize.asOrderedSize).toBe(0);
        expect(componentInstance.bomLifecycleSize.asSupportedSize).toBe(0);
        expect(componentInstance.bomLifecycleSize.asRecycledSize).toBe(0);
    });


    it('should call partsFacade.setPartsAsBuiltWithFilter when filter is set', async () => {

        const { fixture } = await renderParts();
        const { componentInstance } = fixture;
        // Arrange
        const assetAsBuiltFilter: AssetAsBuiltFilter = {
            id: "123"
        };
        const partsFacade = (componentInstance as any)['partsFacade'];
        const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsBuilt');

        componentInstance.filterActivated(MainAspectType.AS_BUILT, assetAsBuiltFilter);


        expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], assetAsBuiltFilter);
    });

    it('should call partsFacade.setPartsAsPlannedWithFilter when filter is set', async () => {

        const { fixture } = await renderParts();
        const { componentInstance } = fixture;
        // Arrange
        const assetAsPlannedFilter: AssetAsPlannedFilter = {
            id: "123"
        };
        const partsFacade = (componentInstance as any)['partsFacade'];
        const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsPlanned');


        componentInstance.filterActivated(MainAspectType.AS_PLANNED, assetAsPlannedFilter);


        expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], assetAsPlannedFilter);
    });

    it('should call partsFacade.setPartsAsDesignedWithFilter when filter is set', async () => {

        const { fixture } = await renderParts();
        const { componentInstance } = fixture;
        // Arrange
        const assetAsDesignedFilter: AssetAsDesignedFilter = {
            id: "123"
        };
        const partsFacade = (componentInstance as any)['partsFacade'];
        const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsDesigned');

        componentInstance.filterActivated(MainAspectType.AS_DESIGNED, assetAsDesignedFilter);


        expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], assetAsDesignedFilter);
    });

    it('should call partsFacade.setPartsAsOrderedWithFilter when filter is set', async () => {

        const { fixture } = await renderParts();
        const { componentInstance } = fixture;
        // Arrange
        const assetAsOrderedFilter: AssetAsOrderedFilter = {
            id: "123"
        };
        const partsFacade = (componentInstance as any)['partsFacade'];
        const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsOrdered');

        componentInstance.filterActivated(MainAspectType.AS_ORDERED, assetAsOrderedFilter);


        expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], assetAsOrderedFilter);
    });

    it('should call partsFacade.setPartsAsSupportedWithFilter when filter is set', async () => {

        const { fixture } = await renderParts();
        const { componentInstance } = fixture;
        // Arrange
        const assetAsSupportedFilter: AssetAsSupportedFilter = {
            id: "123"
        };
        const partsFacade = (componentInstance as any)['partsFacade'];
        const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsSupported');

        componentInstance.filterActivated(MainAspectType.AS_SUPPORTED, assetAsSupportedFilter);


        expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], assetAsSupportedFilter);
    });

    it('should call partsFacade.setPartsAsRecycledWithFilter when filter is set', async () => {

        const { fixture } = await renderParts();
        const { componentInstance } = fixture;
        // Arrange
        const assetAsRecycledFilter: AssetAsRecycledFilter = {
            id: "123"
        };
        const partsFacade = (componentInstance as any)['partsFacade'];
        const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsRecycled');

        componentInstance.filterActivated(MainAspectType.AS_RECYCLED, assetAsRecycledFilter);


        expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], assetAsRecycledFilter);
    });

    it('should call partsFacade.setPartsAsBuilt when filter is not set', async () => {

        const { fixture } = await renderParts();
        const { componentInstance } = fixture;

        const assetAsBuiltFilter: AssetAsBuiltFilter = {};
        const partsFacade = (componentInstance as any)['partsFacade'];
        const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsBuilt');

        // Act
        componentInstance.filterActivated(MainAspectType.AS_BUILT, assetAsBuiltFilter);

        // Assert
        expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], null);
    });

    it('should call partsFacade.setPartsAsPlanned when filter is not set', async () => {

        const { fixture } = await renderParts();
        const { componentInstance } = fixture;

        const assetAsPlannedFilter: AssetAsPlannedFilter = {};
        const partsFacade = (componentInstance as any)['partsFacade'];
        const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsPlanned');

        // Act
        componentInstance.filterActivated(MainAspectType.AS_PLANNED, assetAsPlannedFilter);

        // Assert
        expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], null);
    });

    it('should call partsFacade.setPartsAsDesigned when filter is not set', async () => {

        const { fixture } = await renderParts();
        const { componentInstance } = fixture;

        const assetFilter: AssetAsDesignedFilter = {};
        const partsFacade = (componentInstance as any)['partsFacade'];
        const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsDesigned');

        // Act
        componentInstance.filterActivated(MainAspectType.AS_DESIGNED, assetFilter);

        // Assert
        expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], null);
    });

    it('should call partsFacade.setPartsAsOrdered when filter is not set', async () => {

        const { fixture } = await renderParts();
        const { componentInstance } = fixture;

        const assetFilter: AssetAsOrderedFilter = {};
        const partsFacade = (componentInstance as any)['partsFacade'];
        const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsOrdered');

        // Act
        componentInstance.filterActivated(MainAspectType.AS_ORDERED, assetFilter);

        // Assert
        expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], null);
    });

    it('should call partsFacade.setPartsAsSupported when filter is not set', async () => {

        const { fixture } = await renderParts();
        const { componentInstance } = fixture;

        const assetFilter: AssetAsSupportedFilter = {};
        const partsFacade = (componentInstance as any)['partsFacade'];
        const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsSupported');

        // Act
        componentInstance.filterActivated(MainAspectType.AS_SUPPORTED, assetFilter);

        // Assert
        expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], null);
    });

    it('should call partsFacade.setPartsAsRecycled when filter is not set', async () => {

        const { fixture } = await renderParts();
        const { componentInstance } = fixture;

        const assetFilter: AssetAsRecycledFilter = {};
        const partsFacade = (componentInstance as any)['partsFacade'];
        const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsRecycled');

        // Act
        componentInstance.filterActivated(MainAspectType.AS_RECYCLED, assetFilter);

        // Assert
        expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], null);
    });

    it('should call partsFacade.setPartsAsBuilt with the correct parameters', async () => {
        const { fixture } = await renderParts();
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
        expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsBuiltSortList']);
    });

    it('should call partsFacade.setPartsAsBuilt with the correct parameters no ctrlkey pressed', async () => {
        const { fixture } = await renderParts();
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
        expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsBuiltSortList']);
    });

    it('should call partsFacade.setPartsAsPlanned with the correct parameters', async () => {
        const { fixture } = await renderParts();
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
        expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsPlannedSortList']);
    });

    it('should call partsFacade.setPartsAsPlanned with the correct parameters  and ctrlkey not pressed', async () => {
        const { fixture } = await renderParts();
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
        expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsPlannedSortList']);
    });

    it('should call partsFacade.setPartsAsDesigned with the correct parameters', async () => {
        const { fixture } = await renderParts();
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
        expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsDesignedSortList']);
    });

    it('should call partsFacade.setPartsAsOrdered with the correct parameters', async () => {
        const { fixture } = await renderParts();
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
        expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsOrderedSortList']);
    });

    it('should call partsFacade.setPartsAsSupported with the correct parameters', async () => {
        const { fixture } = await renderParts();
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
        expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsSupportedSortList']);
    });

    it('should call partsFacade.setPartsAsRecycled with the correct parameters', async () => {
        const { fixture } = await renderParts();
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
        expect(partsFacadeSpy).toHaveBeenCalledWith(page, pageSize, componentInstance['tableAsRecycledSortList']);
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

    it('should reset AsBuilt part table when resetTableSortingList is called', async () => {
        const { fixture } = await renderParts();
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
        const { fixture } = await renderParts();
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
        const { fixture } = await renderParts();
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
        const { fixture } = await renderParts();
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
        const { fixture } = await renderParts();
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
        const { fixture } = await renderParts();
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


    it('should clear filters and call partsFacade methods with search value', async () => {

        const { fixture } = await renderParts();
        const { componentInstance } = fixture;
        // Arrange
        const searchValue = 'searchTerm';

        const partsFacade = (componentInstance as any)['partsFacade'];
        const partsFacadeSpy = spyOn(partsFacade, 'setPartsAsBuilt');
        const partsFacadeAsPlannedSpy = spyOn(partsFacade, 'setPartsAsPlanned');
        componentInstance.searchControl.setValue(searchValue);


        // Act
        componentInstance.triggerPartSearch();

        // Assert
        expect(partsFacadeAsPlannedSpy).toHaveBeenCalledWith(0, 50, [], toGlobalSearchAssetFilter(searchValue, false), true);
        expect(partsFacadeSpy).toHaveBeenCalledWith(0, 50, [], toGlobalSearchAssetFilter(searchValue, true), true);
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

    it('should update the bomLifecycleSize when handleTableActivationEvent is called', async () => {

        const { fixture } = await renderParts();
        const { componentInstance } = fixture;
        // Arrange
        const bomLifecycleSize = {
            asPlannedSize: 10,
            asBuiltSize: 20,
            asDesignedSize: 30,
            asOrderedSize: 40,
            asSupportedSize: 50,
            asRecycledSize: 60
        };

        // Act
        componentInstance.handleTableActivationEvent(bomLifecycleSize);

        // Assert
        expect(componentInstance.bomLifecycleSize).toEqual(bomLifecycleSize);
    });

});
