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
import { PartsState } from '@page/parts/core/parts.state';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { PartDetailsState } from '@shared/modules/part-details/core/partDetails.state';
import { PartDetailsModule } from '@shared/modules/part-details/partDetails.module';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { MOCK_part_1 } from '../../../../../mocks/services/parts-mock/partsAsBuilt/partsAsBuilt.test.model';
import { PartDetailComponent } from './part-detail.component';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { Role } from '@core/user/role.model';

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
    return await renderComponent(PartDetailComponent, {
      declarations: [PartDetailComponent],
      imports: [PartDetailsModule, LayoutModule],
      providers: [
        PartDetailsFacade,
        { provide: PartsState, useFactory: () => PartsStateMock },
        { provide: PartDetailsState, useFactory: () => PartDetailsStateMock },
      ],
      roles,
    });
  };

  it('should render child-component table', async () => {
    await renderPartDetailComponent({ roles: [Role.USER] });

    try {
      const childTableHeadline = await screen.findByText('partDetail.investigation.tab.header');
      expect(childTableHeadline).toBeInTheDocument();
      expect(await screen.findByText('partDetail.tab.header')).toBeInTheDocument();
    } catch (error) { }
  });

  it('should render tabs', async () => {
    await renderPartDetailComponent({ roles: [Role.USER] });
    const tabElements = await screen.findAllByRole('tab');

    expect(tabElements.length).toEqual(2);
  });

  it('should change tab index when onTabChange is called', async () => {
    const { fixture } = await renderPartDetailComponent({ roles: [Role.USER] });

    const { componentInstance } = fixture;
    expect(componentInstance.selectedTab).toEqual(0);

    componentInstance.onTabChange({ index: 1 } as MatTabChangeEvent);

    fixture.detectChanges();
    expect(componentInstance.selectedTab).toEqual(1);
  });

  it('should call navigateBackToParts when close button is clicked', async () => {
    const { fixture } = await renderPartDetailComponent({ roles: [Role.USER] });

    const { componentInstance } = fixture;
    spyOn(componentInstance, 'navigateBackToParts');

    const closeButton = (await waitFor(() => screen.getByTestId('Part-details-back-button-test-id'))) as HTMLInputElement;
    fireEvent.click(closeButton);

    expect(componentInstance.navigateBackToParts).toHaveBeenCalled();

    componentInstance.navigateBackToParts();
  });

  it('should navigate back to parts', async () => {
    const { fixture } = await renderPartDetailComponent({ roles: [Role.USER] });
    const router = TestBed.inject(Router);
    const { componentInstance } = fixture;
    const partDetailsFacade = (componentInstance as any)['partDetailsFacade'];
    const context = 'some-context';

    spyOn(router, 'navigate').and.stub();

    componentInstance.context = context;
    componentInstance.navigateBackToParts();

    expect(partDetailsFacade.selectedPart).toEqual(null);
    expect(router.navigate).toHaveBeenCalledWith([`/${context}`]);
  });

  it('should navigate to relations page with correct parameters and reload', async () => {
    const { fixture } = await renderPartDetailComponent({ roles: [Role.USER] });

    const part: any = { id: 123, mainAspectType: MainAspectType.AS_BUILT };
    const context = 'some-context';
    const { componentInstance } = fixture;

    spyOn(componentInstance.dialog, 'open');
    componentInstance.context = context;

    fixture.componentInstance.openRelationPage(part);

    expect(componentInstance.dialog.open).toHaveBeenCalled();
  });

});
