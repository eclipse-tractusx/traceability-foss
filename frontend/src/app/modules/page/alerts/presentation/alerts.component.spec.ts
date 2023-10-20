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
import { NotificationTabInformation } from '@shared/model/notification-tab-information';
import { AlertsService } from '@shared/service/alerts.service';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

import { AlertsComponent } from './alerts.component';


describe('AlertsComponent', () => {
  const renderAlerts = async () => {
    return await renderComponent(AlertsComponent, {
      imports: [AlertsModule],
      providers: [AlertsService],
      translations: ['page.alert'],
    });
  };

  // it('should call detail page with correct ID', async () => {
  //   const { fixture } = await renderAlerts();
  //   fireEvent.click((await waitFor(() => screen.getAllByTestId('table-menu-button')))[0]);

  //   const spy = spyOn((fixture.componentInstance as any).router, 'navigate');
  //   spy.and.returnValue(new Promise(null));

  //   fireEvent.click(await waitFor(() => screen.getByTestId('table-menu-button--actions.viewDetails')));
  //   const tabInformation: NotificationTabInformation = { tabIndex: null, pageNumber: undefined }
  //   expect(spy).toHaveBeenCalledWith(['/alerts/id-84'], { queryParams: tabInformation });
  // });

  it('should call change pagination of received alerts', async () => {
    await renderAlerts();
    fireEvent.click(await waitFor(() => screen.getByLabelText('pagination.nextPageLabel', { selector: 'button' })));

    expect(await waitFor(() => screen.getByText('Alert No 84'))).toBeInTheDocument();
  });

  it('should call change pagination of queued & requested alerts', async () => {
    await renderAlerts();

    fireEvent.click(await waitFor(() => screen.getByText('commonAlert.tabs.queuedAndRequested')));

    fireEvent.click(await waitFor(() => screen.getByLabelText('pagination.nextPageLabel', { selector: 'button' })));

    expect(await waitFor(() => screen.getByText('Alert No 84'))).toBeInTheDocument();
  });

  it('should sort received alerts after column status', async () => {
    const { fixture } = await renderAlerts();
    const alertsComponent = fixture.componentInstance;

    let setTableFunctionSpy = spyOn<any>(alertsComponent, "setTableSortingList").and.callThrough();
    let statusColumnHeader = await screen.findByText('table.column.status');
    await waitFor(() => { fireEvent.click(statusColumnHeader); }, { timeout: 3000 });


    expect(setTableFunctionSpy).toHaveBeenCalledWith(['status', 'asc'], "received");

    expect(alertsComponent['alertReceivedSortList']).toEqual([["status", "asc"]]);
  });

  it('should sort queued and requested alerts after column status', async () => {
    const { fixture } = await renderAlerts();
    const alertsComponent = fixture.componentInstance;

    fireEvent.click(await waitFor(() => screen.getByText('commonAlert.tabs.queuedAndRequested')));

    let setTableFunctionSpy = spyOn<any>(alertsComponent, "setTableSortingList").and.callThrough();
    let statusColumnHeader = await screen.findByText('table.column.status');
    await waitFor(() => { fireEvent.click(statusColumnHeader); }, { timeout: 3000 });


    expect(setTableFunctionSpy).toHaveBeenCalledWith(['status', 'asc'], "queued-and-requested");

    expect(alertsComponent['alertQueuedAndRequestedSortList']).toEqual([["status", "asc"]]);
  });


  it('should multisort after column description and status', async () => {
    const { fixture } = await renderAlerts();
    const alertsComponent = fixture.componentInstance;

    let setTableFunctionSpy = spyOn<any>(alertsComponent, "setTableSortingList").and.callThrough();
    let descriptionColumnHeader = await screen.findByText('table.column.description');
    await waitFor(() => { fireEvent.click(descriptionColumnHeader); }, { timeout: 3000 });
    let statusHeader = await screen.findByText('table.column.status')

    await waitFor(() => {
      fireEvent.keyDown(statusHeader, {
        ctrlKey: true,
        charCode: 17
      })
    })
    expect(alertsComponent['ctrlKeyState']).toBeTruthy();
    await waitFor(() => {
      fireEvent.click(statusHeader)
    });

    await waitFor(() => {
      fireEvent.keyUp(statusHeader, {
        ctrlKey: true,
        charCode: 17
      })
    })

    await waitFor(() => { fireEvent.click(statusHeader) });


    expect(setTableFunctionSpy).toHaveBeenCalledWith(['description', 'asc'], "received");
    expect(setTableFunctionSpy).toHaveBeenCalledWith(['status', 'asc'], "received");
    expect(alertsComponent['alertReceivedSortList']).toEqual([["description", "asc"], ["status", "desc"]]);
  });

  it('should reset sorting after third click', async () => {
    const { fixture } = await renderAlerts();
    const alertsComponent = fixture.componentInstance;

    let descriptionColumnHeader = await screen.findByText('table.column.description');
    await waitFor(() => { fireEvent.click(descriptionColumnHeader); }, { timeout: 3000 });
    let statusColumnHeader = await screen.findByText('table.column.status')

    await waitFor(() => {
      fireEvent.keyDown(statusColumnHeader, {
        ctrlKey: true,
        charCode: 17
      })
    })

    await waitFor(() => {
      fireEvent.click(statusColumnHeader)
    });

    await waitFor(() => {
      fireEvent.keyUp(statusColumnHeader, {
        ctrlKey: true,
        charCode: 17
      })
    })

    await waitFor(() => { fireEvent.click(statusColumnHeader) });

    await waitFor(() => { fireEvent.click(statusColumnHeader) });

    expect(alertsComponent['alertReceivedSortList']).toEqual([]);
  });

});
