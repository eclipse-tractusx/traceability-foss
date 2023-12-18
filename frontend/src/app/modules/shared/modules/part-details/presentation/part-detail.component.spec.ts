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
import { PartsState } from '@page/parts/core/parts.state';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { PartDetailsState } from '@shared/modules/part-details/core/partDetails.state';
import { PartDetailsModule } from '@shared/modules/part-details/partDetails.module';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { MOCK_part_1 } from '../../../../../mocks/services/parts-mock/partsAsBuilt/partsAsBuilt.test.model';
import { PartDetailComponent } from './part-detail.component';

let PartsStateMock: PartsState;
let PartDetailsStateMock: PartDetailsState;

const part = PartsAssembler.assemblePart(MOCK_part_1, MainAspectType.AS_BUILT);

describe('PartDetailComponent', () => {
  beforeEach(() => {
    PartDetailsStateMock = new PartDetailsState();
    PartDetailsStateMock.selectedPart = { data: part };

    PartsStateMock = new PartsState();
  });

  const renderPartDetailComponent = async ({ roles = [] } = {}) => {
    return await renderComponent(`<app-sidenav></app-sidenav><app-part-detail></app-part-detail>`, {
      declarations: [ SidenavComponent, PartDetailComponent ],
      imports: [ PartDetailsModule, LayoutModule ],
      providers: [
        PartDetailsFacade,
        { provide: PartsState, useFactory: () => PartsStateMock },
        { provide: PartDetailsState, useFactory: () => PartDetailsStateMock },
        SidenavService,
      ],
      roles,
    });
  };

  it('should render side nav', async () => {
    await renderPartDetailComponent();

    const sideNavElement = await waitFor(() => screen.getByTestId('sidenav--test-id'));
    expect(sideNavElement).toBeInTheDocument();
  });

  it('should render an open sidenav with part details', async () => {
    await renderPartDetailComponent();

    const sideNavElement = await waitFor(() => screen.getByTestId('sidenav--test-id'));
    const nameElement = await screen.findByText('BMW AG');
    const productionDateElement = await screen.findByText('2022-02-04T13:48:54');

    expect(sideNavElement).toBeInTheDocument();
    await waitFor(() => expect(sideNavElement).toHaveClass('sidenav--container__open'));

    expect(nameElement).toBeInTheDocument();
    expect(productionDateElement).toBeInTheDocument();
  });

  it('should render child-component table', async () => {
    await renderPartDetailComponent({ roles: [ 'user' ] });

    const childTableHeadline = await screen.findByText('partDetail.investigation.headline');
    expect(childTableHeadline).toBeInTheDocument();
    expect(await screen.findByText('partDetail.investigation.noSelection.header')).toBeInTheDocument();
  });
});
