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
import { PartDetailsFacade } from "@shared/modules/part-details/core/partDetails.facade";
import { Role } from '@core/user/role.model';

describe('Parts', () => {

    const renderParts = () => {

        return renderComponent(PartsComponent, {
            declarations: [SidenavComponent],
            imports: [PartsModule, SharedModule, LayoutModule, OtherPartsModule],
            providers: [{ provide: SidenavService }, { provide: PartDetailsFacade }],
            roles: [Role.ADMIN, Role.WIP],
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
