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
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { PartsModule } from '@page/parts/parts.module';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { StaticIdService } from '@shared/service/staticId.service';
import { screen } from '@testing-library/angular';
import {  renderComponent } from '@tests/test-render.utils';
import {
  MOCK_part_1,
  MOCK_part_2,
} from '../../../../../../mocks/services/parts-mock/partsAsBuilt/partsAsBuilt.test.model';
import { RelatedPartsTableComponent } from './related-parts-table.component';
import { of } from 'rxjs';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { RoleService } from '@core/user/role.service';
import { TableEventConfig, TableHeaderSort } from '@shared/components/table/table.model';
import { By } from '@angular/platform-browser';
import { Owner } from '@page/parts/model/owner.enum';

describe('RelatedPartsTableComponent', () => {
  const part = { data: PartsAssembler.assemblePart(MOCK_part_1, MainAspectType.AS_BUILT) };
  const firstChild = PartsAssembler.assemblePart(MOCK_part_2, MainAspectType.AS_BUILT);
  const mockChildPart = PartsAssembler.assemblePart(MOCK_part_2, MainAspectType.AS_BUILT);
  const mockParentPart = PartsAssembler.assemblePart(MOCK_part_1, MainAspectType.AS_BUILT);

  const mockFacade = {
    getChildPartDetails: jasmine.createSpy().and.returnValue(of([mockChildPart])),
    getParentPartDetails: jasmine.createSpy().and.returnValue(of([mockParentPart])),
    sortChildParts: (data, field, direction) => data,
    sortParentParts: (data, field, direction) => data,
  };

  const renderRelatedPartsTableComponent = async () => {
    const { fixture } = await renderComponent(RelatedPartsTableComponent, {
      declarations: [RelatedPartsTableComponent],
      imports: [PartsModule, LayoutModule],
      providers: [StaticIdService, { provide: PartDetailsFacade, useValue: mockFacade }],
    });

    fixture.componentInstance.part = {
      data: PartsAssembler.assemblePart(MOCK_part_1, MainAspectType.AS_BUILT),
    };

    fixture.detectChanges();
    await fixture.whenStable();
    return fixture;
  };

  it('should render component with child and parent parts tabs', async () => {
    await renderRelatedPartsTableComponent();

    expect(await screen.findByRole('tab', { name: /partDetail.childParts/i })).toBeInTheDocument();
    expect(await screen.findByRole('tab', { name: /partDetail.parentParts/i })).toBeInTheDocument();
  });

  it('should not render tabs if no child or parent part data is available', async () => {
    const emptyFacadeMock = {
      getChildPartDetails: jasmine.createSpy().and.returnValue(of([])),
      getParentPartDetails: jasmine.createSpy().and.returnValue(of([])),
      sortChildParts: (data, field, direction) => data,
      sortParentParts: (data, field, direction) => data,
    };

    const { fixture } = await renderComponent(RelatedPartsTableComponent, {
      declarations: [RelatedPartsTableComponent],
      imports: [PartsModule, LayoutModule],
      providers: [StaticIdService, { provide: PartDetailsFacade, useValue: emptyFacadeMock }],
    });

    fixture.componentInstance.part = part;
    fixture.detectChanges();
    await fixture.whenStable();

    // Now check that no tabs are rendered
    const tabs = screen.queryAllByRole('tab');
    expect(tabs.length).toBe(0);
  });

  it('should render only parent tab if child parts are empty', async () => {
    const facadeMock = {
      getChildPartDetails: jasmine.createSpy().and.returnValue(of([])), // child = empty
      getParentPartDetails: jasmine.createSpy().and.returnValue(of([mockParentPart])), // parent = present
      sortChildParts: (data, field, direction) => data,
      sortParentParts: (data, field, direction) => data,
    };

    const { fixture } = await renderComponent(RelatedPartsTableComponent, {
      declarations: [RelatedPartsTableComponent],
      imports: [PartsModule, LayoutModule],
      providers: [StaticIdService, { provide: PartDetailsFacade, useValue: facadeMock }],
    });

    fixture.componentInstance.part = part;
    fixture.detectChanges();
    await fixture.whenStable();

    const tabs = await screen.findAllByRole('tab');
    expect(tabs.length).toBe(1);
    expect(tabs[0].textContent).toMatch(/partDetail\.parentParts/i);
  });

  it('should render only child tab if parent parts are empty and disable "Request Investigation" button when no parts are selected', async () => {
    const facadeMock = {
      getChildPartDetails: jasmine.createSpy().and.returnValue(of([mockChildPart])),
      getParentPartDetails: jasmine.createSpy().and.returnValue(of([])),
      sortChildParts: (data, field, direction) => data,
      sortParentParts: (data, field, direction) => data,
    };

    const roleMock = {
      isUser: () => true,
      isSupervisor: () => false,
      hasAccess: () => true,
    };

    const { fixture } = await renderComponent(RelatedPartsTableComponent, {
      declarations: [RelatedPartsTableComponent],
      imports: [PartsModule, LayoutModule],
      providers: [
        StaticIdService,
        { provide: PartDetailsFacade, useValue: facadeMock },
        { provide: RoleService, useValue: roleMock },
      ],
    });

    fixture.componentInstance.part = { data: mockChildPart };
    fixture.detectChanges();
    await fixture.whenStable();

    const tabs = await screen.findAllByRole('tab');
    expect(tabs.length).toBe(1);
    expect(tabs[0].textContent).toMatch(/partDetail\.childParts/i);

    const wrapper = screen.getByTestId('request-investigation-btn');
    const nativeButton = wrapper.querySelector('button');
    expect(nativeButton).toBeDisabled();
  });

  it('should enable "Request Investigation" button when a part is selected', async () => {
    const facadeMock = {
      getChildPartDetails: jasmine.createSpy().and.returnValue(of([mockChildPart])),
      getParentPartDetails: jasmine.createSpy().and.returnValue(of([])),
      sortChildParts: (data, field, direction) => data,
      sortParentParts: (data, field, direction) => data,
    };

    const roleMock = {
      isUser: () => true,
      isSupervisor: () => false,
      hasAccess: () => true,
    };

    const { fixture } = await renderComponent(RelatedPartsTableComponent, {
      declarations: [RelatedPartsTableComponent],
      imports: [PartsModule, LayoutModule],
      providers: [
        StaticIdService,
        { provide: PartDetailsFacade, useValue: facadeMock },
        { provide: RoleService, useValue: roleMock },
      ],
    });

    fixture.componentInstance.part = { data: mockChildPart };
    fixture.detectChanges();
    await fixture.whenStable();

    fixture.componentInstance['selectedPartsState'].update([mockChildPart]);
    fixture.detectChanges();

    const wrapper = await screen.findByTestId('request-investigation-btn');
    const nativeButton = wrapper.querySelector('button')!;
    expect(nativeButton).toBeEnabled();
  });

  it('should render only child tab and call onChildPartsSort when configChanged event is triggered', async () => {
    const facadeMock = {
      getChildPartDetails: jasmine.createSpy().and.returnValue(of([mockChildPart])),
      getParentPartDetails: jasmine.createSpy().and.returnValue(of([])),
      sortChildParts: jasmine.createSpy().and.returnValue([mockChildPart]),
      sortParentParts: (data, field, direction) => data,
    };

    const { fixture } = await renderComponent(RelatedPartsTableComponent, {
      declarations: [RelatedPartsTableComponent],
      imports: [PartsModule, LayoutModule],
      providers: [StaticIdService, { provide: PartDetailsFacade, useValue: facadeMock }],
    });

    fixture.componentInstance.part = { data: mockChildPart };
    fixture.detectChanges();
    await fixture.whenStable();

    // First, verify only child tab is rendered
    const tabs = await screen.findAllByRole('tab');
    expect(tabs.length).toBe(1);
    expect(tabs[0].textContent).toMatch(/partDetail\.childParts/i);

    const mockSortEvent: TableEventConfig = {
      sorting: ['manufacturerPartId', 'asc'] as TableHeaderSort,
      page: 1,
      pageSize: 10,
    };

    const sortSpy = spyOn(fixture.componentInstance, 'onChildPartsSort').and.callThrough();

    const appTable = await screen.findByTestId('child-app-table');
    expect(appTable).toBeInTheDocument();
    // Get component instance to emit event
    const tableDebug = fixture.debugElement.query(By.css('[data-testid="child-app-table"]'));
    const tableInstance = tableDebug.componentInstance;
    tableInstance.configChanged.emit(mockSortEvent);
    fixture.detectChanges();
    expect(sortSpy).toHaveBeenCalledWith(mockSortEvent);
  });

  it('should render only Parent tab and call onParentPartsSort when configChanged event is triggered', async () => {
    const facadeMock = {
      getChildPartDetails: jasmine.createSpy().and.returnValue(of([])),
      getParentPartDetails: jasmine.createSpy().and.returnValue(of([mockParentPart])),
      sortChildParts: jasmine.createSpy().and.returnValue([mockChildPart]),
      sortParentParts: (data, field, direction) => data,
    };

    const { fixture } = await renderComponent(RelatedPartsTableComponent, {
      declarations: [RelatedPartsTableComponent],
      imports: [PartsModule, LayoutModule],
      providers: [StaticIdService, { provide: PartDetailsFacade, useValue: facadeMock }],
    });

    fixture.componentInstance.part = { data: mockParentPart };
    fixture.detectChanges();
    await fixture.whenStable();

    // First, verify both child/ parent tab is rendered
    const tabs = await screen.findAllByRole('tab');
    expect(tabs.length).toBe(1);
    expect(tabs[0].textContent).toMatch(/partDetail\.parentParts/i);

    const mockSortEvent: TableEventConfig = {
      sorting: ['manufacturerPartId', 'asc'] as TableHeaderSort,
      page: 1,
      pageSize: 10,
    };

    const sortSpy = spyOn(fixture.componentInstance, 'onParentPartsSort').and.callThrough();

    const appTable = await screen.findByTestId('parent-app-table');
    expect(appTable).toBeInTheDocument();
    // Get component instance to emit event
    const tableDebug = fixture.debugElement.query(By.css('[data-testid="parent-app-table"]'));
    const tableInstance = tableDebug.componentInstance;
    tableInstance.configChanged.emit(mockSortEvent);
    fixture.detectChanges();
    expect(sortSpy).toHaveBeenCalledWith(mockSortEvent);
  });

  it('should display correct label and tooltip based on selected parts ownership', async () => {
    const facadeMock = {
      getChildPartDetails: jasmine.createSpy().and.returnValue(of([mockChildPart])),
      getParentPartDetails: jasmine.createSpy().and.returnValue(of([])),
      sortChildParts: (data, field, direction) => data,
      sortParentParts: (data, field, direction) => data,
    };
  
    const roleMock = {
      isUser: () => true,
      isSupervisor: () => false,
      hasAccess: () => true,
    };
  
    const ownPart = { ...mockChildPart, owner: Owner.OWN };
    const supplierPart = { ...mockChildPart,owner: Owner.SUPPLIER };
    const mixedParts = [ownPart, supplierPart];
  
    const { fixture } = await renderComponent(RelatedPartsTableComponent, {
      declarations: [RelatedPartsTableComponent],
      imports: [PartsModule, LayoutModule],
      providers: [
        StaticIdService,
        { provide: PartDetailsFacade, useValue: facadeMock },
        { provide: RoleService, useValue: roleMock },
      ],
    });
  
    // Set part data
    fixture.componentInstance.part = { data: mockChildPart };
    fixture.detectChanges();
    await fixture.whenStable();
  
    // Case 1: OWN part selected
    fixture.componentInstance['selectedPartsState'].update([ownPart]);
    fixture.detectChanges();
  
    let button = await screen.findByTestId('request-investigation-btn');
    expect(button.textContent).toContain('routing.createIncident');
    expect(fixture.componentInstance.requestButtonTooltipKey).toBe('routing.createIncident');
    expect(fixture.componentInstance.showIncidentButton()).toBeTrue();
  
    // Case 2: SUPPLIER part selected
    fixture.componentInstance['selectedPartsState'].update([supplierPart]);
    fixture.detectChanges();
  
    expect(button.textContent).toContain('routing.requestInvestigation');
    expect(fixture.componentInstance.requestButtonTooltipKey).toBe('routing.requestInvestigation');
    expect(fixture.componentInstance.showIncidentButton()).toBeTrue();
  
    // Case 3: Mixed ownership
    fixture.componentInstance['selectedPartsState'].update(mixedParts);
    fixture.detectChanges();
  
    expect(fixture.componentInstance.requestButtonTooltipKey).toBe('routing.partMismatch');
    expect(fixture.componentInstance.showIncidentButton()).toBeFalse();
  
    // Case 4: No selection
    fixture.componentInstance['selectedPartsState'].update([]);
    fixture.detectChanges();
  
    expect(fixture.componentInstance.requestButtonTooltipKey).toBe('routing.noChildPartsForInvestigation');
    expect(fixture.componentInstance.showIncidentButton()).toBeFalse();
  });
  
});
