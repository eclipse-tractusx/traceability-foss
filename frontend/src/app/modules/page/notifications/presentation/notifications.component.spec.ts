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

import { NotificationsModule } from '@page/notifications/notifications.module';
import { NotificationTabInformation } from '@shared/model/notification-tab-information';
import { NotificationService } from '@shared/service/notification.service';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

import { NotificationsComponent } from './notifications.component';


describe('NotificationsComponent', () => {
  const renderNotifications = async () => {
    return await renderComponent(NotificationsComponent, {
      imports: [ NotificationsModule ],
      providers: [ NotificationService ],
      translations: [ 'page.alert' ],
    });
  };

  it('should call detail page with correct ID', async () => {
    const { fixture } = await renderNotifications();
    fireEvent.click((await waitFor(() => screen.getAllByTestId('table-menu-button')))[0]);

    const spy = spyOn((fixture.componentInstance as any).router, 'navigate');
    spy.and.returnValue(new Promise(null));

    fireEvent.click(await waitFor(() => screen.getByTestId('table-menu-button--actions.viewDetails')));
    const tabInformation: NotificationTabInformation = { tabIndex: null, pageNumber: undefined };
    expect(spy).toHaveBeenCalledWith([ '/inbox/id-84' ], { queryParams: tabInformation });
  });


  it('should sort received notifications after column status', async () => {
    const { fixture } = await renderNotifications();
    const notificationsComponent = fixture.componentInstance;

    let setTableFunctionSpy = spyOn<any>(notificationsComponent, 'setTableSortingList').and.callThrough();

    fireEvent.click(await waitFor(() => screen.getByText('commonAlert.tabs.received')));

    let statusColumnHeader = await screen.findByText('table.column.status');
    await waitFor(() => {
      fireEvent.click(statusColumnHeader);
    }, { timeout: 3000 });


    expect(setTableFunctionSpy).toHaveBeenCalledWith([ 'status', 'asc' ], 'received');

    expect(notificationsComponent['notificationReceivedSortList']).toEqual([ [ 'status', 'asc' ] ]);
  });

  it('should sort queued and requested notifications after column status', async () => {
    const { fixture } = await renderNotifications();
    const notificationsComponent = fixture.componentInstance;

    fireEvent.click(await waitFor(() => screen.getByText('commonAlert.tabs.queuedAndRequested')));

    let setTableFunctionSpy = spyOn<any>(notificationsComponent, 'setTableSortingList').and.callThrough();
    let statusColumnHeader = await screen.findByText('table.column.status');
    await waitFor(() => {
      fireEvent.click(statusColumnHeader);
    }, { timeout: 3000 });


    expect(setTableFunctionSpy).toHaveBeenCalledWith([ 'status', 'asc' ], 'queued-and-requested');

    expect(notificationsComponent['notificationQueuedAndRequestedSortList']).toEqual([ [ 'status', 'asc' ] ]);
  });


  it('should multisort after column description and status', async () => {
    const { fixture } = await renderNotifications();
    const notificationsComponent = fixture.componentInstance;

    let setTableFunctionSpy = spyOn<any>(notificationsComponent, 'setTableSortingList').and.callThrough();

    fireEvent.click(await waitFor(() => screen.getByText('commonAlert.tabs.received')));

    let descriptionColumnHeader = await screen.findByText('table.column.description');
    await waitFor(() => {
      fireEvent.click(descriptionColumnHeader);
    }, { timeout: 3000 });
    let statusHeader = await screen.findByText('table.column.status');

    await waitFor(() => {
      fireEvent.keyDown(statusHeader, {
        ctrlKey: true,
        charCode: 17,
      });
    });
    expect(notificationsComponent['ctrlKeyState']).toBeTruthy();
    await waitFor(() => {
      fireEvent.click(statusHeader);
    });

    await waitFor(() => {
      fireEvent.keyUp(statusHeader, {
        ctrlKey: true,
        charCode: 17,
      });
    });

    await waitFor(() => {
      fireEvent.click(statusHeader);
    });


    expect(setTableFunctionSpy).toHaveBeenCalledWith([ 'description', 'asc' ], 'received');
    expect(setTableFunctionSpy).toHaveBeenCalledWith([ 'status', 'asc' ], 'received');
    expect(notificationsComponent['notificationReceivedSortList']).toEqual([ [ 'status', 'desc' ] ]);
  });

  it('should reset sorting after third click', async () => {
    const { fixture } = await renderNotifications();
    const notificationsComponent = fixture.componentInstance;

    let descriptionColumnHeader = await screen.findByText('table.column.description');
    await waitFor(() => {
      fireEvent.click(descriptionColumnHeader);
    }, { timeout: 3000 });
    let statusColumnHeader = await screen.findByText('table.column.status');

    await waitFor(() => {
      fireEvent.keyDown(statusColumnHeader, {
        ctrlKey: true,
        charCode: 17,
      });
    });

    await waitFor(() => {
      fireEvent.click(statusColumnHeader);
    });

    await waitFor(() => {
      fireEvent.keyUp(statusColumnHeader, {
        ctrlKey: true,
        charCode: 17,
      });
    });

    await waitFor(() => {
      fireEvent.click(statusColumnHeader);
    });

    await waitFor(() => {
      fireEvent.click(statusColumnHeader);
    });

    expect(notificationsComponent['notificationReceivedSortList']).toEqual([]);
  });

});
