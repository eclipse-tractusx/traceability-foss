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

import { AlertsModule } from '@page/alerts/alerts.module';
import { fireEvent, screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { AlertsComponent } from './alerts.component';
import { FilterInfo, FilterMethod, TableEventConfig, TableFilter } from '@shared/components/table/table.model';
import { FilterOperator } from '@page/parts/model/parts.model';
import { MatDialog } from '@angular/material/dialog';
import { TestBed } from '@angular/core/testing';
import { RequestContext } from '@shared/components/request-notification/request-notification.base';
import { RequestStepperComponent } from '@shared/components/request-notification/request-stepper/request-stepper.component';

describe('AlertsComponent', () => {
  const renderAlerts = async () => {
    return await renderComponent(AlertsComponent, {
      imports: [AlertsModule],
      // providers: [AlertsService],
      translations: ['page.alert'],
    });
  };

  it('should render the component', async () => {
    await renderAlerts();
    const alertsHeader = screen.getByText('pageTitle.alerts');
    expect(alertsHeader).toBeInTheDocument();
  });

  it('should multisort after column description and status', async () => {
    const { fixture } = await renderAlerts();
    const alertsComponent = fixture.componentInstance;

    const paginationOne: TableEventConfig = { page: 0, pageSize: 50, sorting: ['description', 'asc'] };
    const paginationTwo: TableEventConfig = { page: 0, pageSize: 50, sorting: ['status', 'asc'] };
    const paginationThree: TableEventConfig = { page: 0, pageSize: 50, sorting: ['status', 'desc'] };
    alertsComponent.onReceivedTableConfigChange(paginationOne);

    expect(alertsComponent.alertReceivedSortList).toEqual([['description', 'asc']]);

    const alertsHeader = screen.getByText('pageTitle.alerts');
    fireEvent.keyDown(alertsHeader, {
      ctrlKey: true,
      charCode: 17,
    });
    alertsComponent.onReceivedTableConfigChange(paginationTwo);

    expect(alertsComponent.alertReceivedSortList).toEqual([
      ['description', 'asc'],
      ['status', 'asc'],
    ]);
    alertsComponent.onReceivedTableConfigChange(paginationThree);

    expect(alertsComponent.alertReceivedSortList).toEqual([
      ['description', 'asc'],
      ['status', 'desc'],
    ]);
  });
  it('should reset the multisortList if a selection is done and the ctrl key is not pressed.', async () => {
    const { fixture } = await renderAlerts();
    const alertsComponent = fixture.componentInstance;

    const paginationOne: TableEventConfig = { page: 0, pageSize: 50, sorting: ['description', 'asc'] };
    const paginationTwo: TableEventConfig = { page: 0, pageSize: 50, sorting: ['status', 'asc'] };

    alertsComponent.onReceivedTableConfigChange(paginationOne);

    expect(alertsComponent.alertReceivedSortList).toEqual([['description', 'asc']]);

    const alertsHeader = screen.getByText('pageTitle.alerts');
    fireEvent.keyDown(alertsHeader, {
      ctrlKey: true,
      charCode: 17,
    });

    alertsComponent.onReceivedTableConfigChange(paginationTwo);

    expect(alertsComponent.alertReceivedSortList).toEqual([
      ['description', 'asc'],
      ['status', 'asc'],
    ]);

    fireEvent.keyUp(alertsHeader, {
      ctrlKey: false,
      charCode: 17,
    });

    alertsComponent.onReceivedTableConfigChange(paginationOne);

    expect(alertsComponent.alertReceivedSortList).toEqual([['description', 'asc']]);
  });

  it('should set the default Pagination by recieving a size change event', async () => {
    const { fixture } = await renderAlerts();
    const alertsComponent = fixture.componentInstance;

    alertsComponent.onDefaultPaginationSizeChange(100);
    expect(alertsComponent.DEFAULT_PAGE_SIZE).toEqual(100);
  });

  // TODO: fix test
  // it('should use the default page size if the page size in the ReceivedConfig is given as 0', async () => {
  //   const { fixture } = await renderAlerts();
  //   const alertsComponent = fixture.componentInstance;

  //   const pagination: TableEventConfig = { page: 0, pageSize: 0, sorting: ['description', 'asc'] };
  //   spyOn(alertsComponent.alertsFacade, 'setReceivedAlerts');
  //   alertsComponent.onReceivedTableConfigChange(pagination);
  //   fixture.detectChanges();
  //   expect(alertsComponent.alertsFacade.setReceivedAlerts).toHaveBeenCalledWith(0, 50, [['description', 'asc']], Object({ filterMethod: 'AND' }));

  // });

  // TODO: fix test
  // it('should use the default page size if the page size in the QueuedAndRequestedConfig is given as 0', async () => {
  //   const { fixture } = await renderAlerts();
  //   const alertsComponent = fixture.componentInstance;

  //   const pagination: TableEventConfig = { page: 0, pageSize: 0, sorting: ['description', 'asc'] };
  //   spyOn(alertsComponent.alertsFacade, 'setQueuedAndRequestedAlerts');

  //   alertsComponent.onQueuedAndRequestedTableConfigChange(pagination);
  //   fixture.detectChanges();
  //   expect(alertsComponent.alertsFacade.setQueuedAndRequestedAlerts).toHaveBeenCalledWith(0, 50, [['description', 'asc']], Object({ filterMethod: 'AND' }));
  // });

  // TODO: fix tests
  // it('should pass on the filtering to the api services', async () => {
  //   const { fixture } = await renderAlerts();
  //   const alertsComponent = fixture.componentInstance;

  //   const pagination: TableEventConfig = { page: 0, pageSize: 50, sorting: ['description', 'asc'], filtering: { filterMethod: FilterMethod.AND, description: { filterOperator: FilterOperator.STARTS_WITH, filterValue: 'value1' } } };
  //   spyOn(alertsComponent.alertsFacade, 'setQueuedAndRequestedAlerts');

  //   alertsComponent.onQueuedAndRequestedTableConfigChange(pagination);
  //   fixture.detectChanges();
  //   expect(alertsComponent.alertsFacade.setQueuedAndRequestedAlerts).toHaveBeenCalledWith(0, 50, [['description', 'asc']], Object({ filterMethod: 'AND', description: { filterOperator: FilterOperator.STARTS_WITH, filterValue: 'value1' } }));

  //   spyOn(alertsComponent.alertsFacade, 'setReceivedAlerts');

  //   alertsComponent.onReceivedTableConfigChange(pagination);
  //   fixture.detectChanges();
  //   expect(alertsComponent.alertsFacade.setReceivedAlerts).toHaveBeenCalledWith(0, 50, [['description', 'asc']], Object({ filterMethod: 'AND', description: { filterOperator: FilterOperator.STARTS_WITH, filterValue: 'value1' } }));
  // });

  // it('should set the correct filters on triggering the global search', async () => {
  //   const { fixture } = await renderAlerts();
  //   const alertsComponent = fixture.componentInstance;

  //   const searchValue = 'value 1';
  //   const filterInfo: FilterInfo = { filterValue: searchValue, filterOperator: FilterOperator.STARTS_WITH };
  //   const alertsReceivedFilters: TableFilter = {
  //     filterMethod: FilterMethod.OR,
  //     description: filterInfo,
  //     createdBy: filterInfo
  //   };
  //   const alertsCreatedFilters: TableFilter = {
  //     filterMethod: FilterMethod.OR,
  //     description: filterInfo,
  //     sendTo: filterInfo
  //   };
  //   const spy1 = spyOn(alertsComponent.alertsFacade, 'setReceivedAlerts');
  //   const spy2 = spyOn(alertsComponent.alertsFacade, 'setQueuedAndRequestedAlerts');
  //   const spy3 = spyOn(alertsComponent.searchHelper, 'resetFilterAndShowToast');

  //   alertsComponent.searchControl.patchValue(searchValue);
  //   alertsComponent.triggerSearch();

  //   expect(alertsComponent.filterReceived).toEqual(alertsReceivedFilters);
  //   expect(alertsComponent.filterQueuedAndRequested).toEqual(alertsCreatedFilters);
  // });

  it('should open the RequestStepperComponent with RequestContext.REQUEST_ALERT', async () => {
    const component = (await renderAlerts()).fixture.componentInstance;
    const matDialog = TestBed.inject(MatDialog);

    spyOn(matDialog, 'open').and.callThrough();

    component.openRequestDialog();

    expect(matDialog.open).toHaveBeenCalledWith(RequestStepperComponent, {
      autoFocus: false,
      disableClose: true,
      data: { context: RequestContext.REQUEST_ALERT },
    });
  });
});
