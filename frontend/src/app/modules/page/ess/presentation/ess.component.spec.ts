/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

import { LayoutModule } from '@layout/layout.module';
import { SidenavComponent } from '@layout/sidenav/sidenav.component';
import { SidenavService } from '@layout/sidenav/sidenav.service';
import { EssFilter } from '@page/ess/model/ess.model';
import { EssComponent } from '@page/ess/presentation/ess.component';
import { OtherPartsModule } from '@page/other-parts/other-parts.module';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { SharedModule } from '@shared/shared.module';
import { renderComponent } from '@tests/test-render.utils';
import { EssModule } from '../ess.module';

describe('Ess', () => {

    const renderEssList = () => {

        return renderComponent(EssComponent, {
            declarations: [SidenavComponent],
            imports: [EssModule, SharedModule, LayoutModule, OtherPartsModule],
            providers: [{provide: SidenavService}, {provide: PartDetailsFacade}],
            roles: ['admin', 'wip'],
        });
    };

    it('should call essFacade.setEssWithFilter when filter is set', async () => {

        const {fixture} = await renderEssList();
        const {componentInstance} = fixture;
        // Arrange
        const essFilter: EssFilter = {
            id: "123"
        };
      const essFacade = (componentInstance)['essFacade'];
        const essFacadeSpy = spyOn(essFacade, 'setEss');

        componentInstance.filterActivated(true, essFilter);


        expect(essFacadeSpy).toHaveBeenCalledWith(0, 50, [], essFilter);
    });

    it('should call essFacade.setEss when filter is not set', async () => {

        const {fixture} = await renderEssList();
        const {componentInstance} = fixture;

        const essFilter: EssFilter = {};
        const essFacade = (componentInstance)['essFacade'];
        const essFacadeSpy = spyOn(essFacade, 'setEss');

        // Act
        componentInstance.filterActivated(true, essFilter);

        // Assert
        expect(essFacadeSpy).toHaveBeenCalledWith(0, 50, [], null);
    });

});
