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

import { TestBed } from '@angular/core/testing';
import { MatDialog } from '@angular/material/dialog';
import { InvestigationsModule } from '@page/investigations/investigations.module';
import { InvestigationsComponent } from '@page/investigations/presentation/investigations.component';
import { FilterOperator } from '@page/parts/model/parts.model';
import { RequestContext } from '@shared/components/request-notification/request-notification.base';
import { RequestStepperComponent } from '@shared/components/request-notification/request-stepper/request-stepper.component';
import { FilterInfo, FilterMethod, TableEventConfig, TableFilter } from '@shared/components/table/table.model';
import { fireEvent, screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

describe('InvestigationsComponent', () => {
  const renderInvestigations = async () => {
    return await renderComponent(InvestigationsComponent, {
      imports: [InvestigationsModule],
      // providers: [InvestigationsService],
      translations: ['page.investigation'],
    });
  };

  it('should render the component', async () => {
    await renderInvestigations();
    const investigationsHeader = screen.getByText('pageTitle.investigations');
    expect(investigationsHeader).toBeInTheDocument();
  });

  it('should multisort after column description and status', async () => {
    const { fixture } = await renderInvestigations();
    const investigationsComponent = fixture.componentInstance;

    const paginationOne: TableEventConfig = { page: 0, pageSize: 50, sorting: ['description', 'asc'] };
    const paginationTwo: TableEventConfig = { page: 0, pageSize: 50, sorting: ['status', 'asc'] };
    const paginationThree: TableEventConfig = { page: 0, pageSize: 50, sorting: ['status', 'desc'] };

    investigationsComponent.onReceivedTableConfigChanged(paginationOne);

    expect(investigationsComponent.investigationReceivedSortList).toEqual([['description', 'asc']]);

    const investigationsHeader = screen.getByText('pageTitle.investigations');
    fireEvent.keyDown(investigationsHeader, {
      ctrlKey: true,
      charCode: 17,
    });

    investigationsComponent.onReceivedTableConfigChanged(paginationTwo);

    expect(investigationsComponent.investigationReceivedSortList).toEqual([
      ['description', 'asc'],
      ['status', 'asc'],
    ]);

    investigationsComponent.onReceivedTableConfigChanged(paginationThree);

    expect(investigationsComponent.investigationReceivedSortList).toEqual([
      ['description', 'asc'],
      ['status', 'desc'],
    ]);
  });

  it('should reset the multisortList if a selection is done and the ctrl key is not pressed.', async () => {
    const { fixture } = await renderInvestigations();
    const investigationsComponent = fixture.componentInstance;

    const paginationOne: TableEventConfig = { page: 0, pageSize: 50, sorting: ['description', 'asc'] };
    const paginationTwo: TableEventConfig = { page: 0, pageSize: 50, sorting: ['status', 'asc'] };

    investigationsComponent.onReceivedTableConfigChanged(paginationOne);

    expect(investigationsComponent.investigationReceivedSortList).toEqual([['description', 'asc']]);

    const investigationsHeader = screen.getByText('pageTitle.investigations');
    fireEvent.keyDown(investigationsHeader, {
      ctrlKey: true,
      charCode: 17,
    });

    investigationsComponent.onReceivedTableConfigChanged(paginationTwo);

    expect(investigationsComponent.investigationReceivedSortList).toEqual([
      ['description', 'asc'],
      ['status', 'asc'],
    ]);

    fireEvent.keyUp(investigationsHeader, {
      ctrlKey: false,
      charCode: 17,
    });

    investigationsComponent.onReceivedTableConfigChanged(paginationOne);

    expect(investigationsComponent.investigationReceivedSortList).toEqual([['description', 'asc']]);
  });

  it('should set the default Pagination by recieving a size change event', async () => {
    const { fixture } = await renderInvestigations();
    const investigationsComponent = fixture.componentInstance;

    investigationsComponent.onDefaultPaginationSizeChange(100);
    expect(investigationsComponent.DEFAULT_PAGE_SIZE).toEqual(100);
  });

  it('should use the default page size if the page size in the ReceivedConfig is given as 0', async () => {
    const { fixture } = await renderInvestigations();
    const investigationsComponent = fixture.componentInstance;

    const pagination: TableEventConfig = { page: 0, pageSize: 0, sorting: ['description', 'asc'] };
    spyOn(investigationsComponent.investigationsFacade, 'setReceivedInvestigation');

    investigationsComponent.onReceivedTableConfigChanged(pagination);
    fixture.detectChanges();
    expect(investigationsComponent.investigationsFacade.setReceivedInvestigation).toHaveBeenCalledWith(0, 50, [['description', 'asc']], null, undefined);
  });

  it('should use the default page size if the page size in the queuedAndRequestedConfig is given as 0', async () => {
    const { fixture } = await renderInvestigations();
    const investigationsComponent = fixture.componentInstance;

    const pagination: TableEventConfig = { page: 0, pageSize: 0, sorting: ['description', 'asc'] };
    spyOn(investigationsComponent.investigationsFacade, 'setQueuedAndRequestedInvestigations');

    investigationsComponent.onQueuedAndRequestedTableConfigChanged(pagination);
    fixture.detectChanges();
    expect(investigationsComponent.investigationsFacade.setQueuedAndRequestedInvestigations).toHaveBeenCalledWith(0, 50, [['description', 'asc']], null, undefined);
  });

  it('should pass on the filtering to the api services', async () => {
    const { fixture } = await renderInvestigations();
    const investigationsComponent = fixture.componentInstance;

    const pagination: TableEventConfig = {
      page: 0,
      pageSize: 50,
      sorting: ['description', 'asc'],
      filtering: {
        filterMethod: FilterMethod.AND,
        description: { filterOperator: FilterOperator.STARTS_WITH, filterValue: 'value1' },
      },
    };
    spyOn(investigationsComponent.investigationsFacade, 'setQueuedAndRequestedInvestigations');

    investigationsComponent.onQueuedAndRequestedTableConfigChanged(pagination);
    fixture.detectChanges();
    expect(investigationsComponent.investigationsFacade.setQueuedAndRequestedInvestigations).toHaveBeenCalledWith(0, 50, [['description', 'asc']], null, undefined);

    spyOn(investigationsComponent.investigationsFacade, 'setReceivedInvestigation');

    investigationsComponent.onReceivedTableConfigChanged(pagination);
    fixture.detectChanges();
    expect(investigationsComponent.investigationsFacade.setReceivedInvestigation).toHaveBeenCalledWith(0, 50, [['description', 'asc']], null, undefined);
  });

  it('should set the correct filters on triggering the global search', async () => {
    const { fixture } = await renderInvestigations();
    const investigationsComponent = fixture.componentInstance;

    const searchValue = 'value 1';
    spyOn(investigationsComponent.investigationsFacade, 'setReceivedInvestigation');
    spyOn(investigationsComponent.investigationsFacade, 'setQueuedAndRequestedInvestigations');
    spyOn(investigationsComponent.searchHelper, 'resetFilterAndShowToast');

    investigationsComponent.searchControl.patchValue(searchValue);
    investigationsComponent.triggerSearch();

    expect(investigationsComponent.searchHelper.resetFilterAndShowToast).toHaveBeenCalledOnceWith(false, investigationsComponent.notificationComponent, investigationsComponent.toastService);
    expect(investigationsComponent.investigationsFacade.setReceivedInvestigation).toHaveBeenCalledOnceWith(0, 50, [], null, Object({
      description: searchValue,
      createdBy: searchValue,
    }), FilterMethod.OR);
    expect(investigationsComponent.investigationsFacade.setQueuedAndRequestedInvestigations).toHaveBeenCalledOnceWith(0, 50, [], null, Object({
      description: searchValue,
      sendTo: searchValue,
    }), FilterMethod.OR);
  });

  it('should open the RequestStepperComponent with RequestContext.REQUEST_INVESTIGATION', async () => {
    const component = (await renderInvestigations()).fixture.componentInstance;
    const matDialog = TestBed.inject(MatDialog);

    spyOn(matDialog, 'open').and.callThrough();

    component.openRequestDialog();

    expect(matDialog.open).toHaveBeenCalledWith(RequestStepperComponent, {
      autoFocus: false,
      disableClose: true,
      data: { context: RequestContext.REQUEST_INVESTIGATION },
    });
  });
});
