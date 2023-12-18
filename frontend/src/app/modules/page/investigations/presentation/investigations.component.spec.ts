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

import { InvestigationsModule } from '@page/investigations/investigations.module';
import { InvestigationsComponent } from '@page/investigations/presentation/investigations.component';
import { NotificationTabInformation } from '@shared/model/notification-tab-information';
import { NotificationService } from '@shared/service/notification.service';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

describe('InvestigationsComponent', () => {
  const renderInvestigations = async () => {
    return await renderComponent(InvestigationsComponent, {
      imports: [ InvestigationsModule ],
      providers: [ NotificationService ],
      translations: [ 'page.investigation' ],
    });
  };

  it('should call detail page with correct ID', async () => {
    const { fixture } = await renderInvestigations();
    fireEvent.click((await waitFor(() => screen.getAllByTestId('table-menu-button')))[0]);

    const spy = spyOn((fixture.componentInstance as any).router, 'navigate');
    spy.and.returnValue(new Promise(null));

    fireEvent.click(await waitFor(() => screen.getByTestId('table-menu-button--actions.viewDetails')));
    const tabInformation: NotificationTabInformation = { tabIndex: null, pageNumber: 0 };
    expect(spy).toHaveBeenCalledWith([ '/investigations/id-84' ], { queryParams: tabInformation });
  });

  it('should call change pagination of received investigations', async () => {
    await renderInvestigations();
    fireEvent.click(await waitFor(() => screen.getByLabelText('pagination.nextPageLabel', { selector: 'button' })));

    expect(await waitFor(() => screen.getByText('Investigation No 84'))).toBeInTheDocument();
    expect(await waitFor(() => screen.getByText('Investigation No 11'))).toBeInTheDocument();
  });

  it('should call change pagination of queued & requested investigations', async () => {
    await renderInvestigations();

    fireEvent.click(await waitFor(() => screen.getByText('commonInvestigation.tabs.queuedAndRequested')));

    fireEvent.click(await waitFor(() => screen.getByLabelText('pagination.nextPageLabel', { selector: 'button' })));

    expect(await waitFor(() => screen.getByText('Investigation No 84'))).toBeInTheDocument();
    expect(await waitFor(() => screen.getByText('Investigation No 11'))).toBeInTheDocument();
  });

  it('should sort received investigations after column status', async () => {
    const { fixture } = await renderInvestigations();
    const investigationComponent = fixture.componentInstance;

    let setTableFunctionSpy = spyOn<any>(investigationComponent, 'setTableSortingList').and.callThrough();
    let statusColumnHeader = await screen.findByText('table.column.status');
    await waitFor(() => {
      fireEvent.click(statusColumnHeader);
    }, { timeout: 3000 });


    expect(setTableFunctionSpy).toHaveBeenCalledWith([ 'status', 'asc' ], 'received');

    expect(investigationComponent['investigationReceivedSortList']).toEqual([ [ 'status', 'asc' ] ]);
  });

  it('should sort queued and requested investigations after column status', async () => {
    const { fixture } = await renderInvestigations();
    const investigationComponent = fixture.componentInstance;

    fireEvent.click(await waitFor(() => screen.getByText('commonInvestigation.tabs.queuedAndRequested')));

    let setTableFunctionSpy = spyOn<any>(investigationComponent, 'setTableSortingList').and.callThrough();
    let statusColumnHeader = await screen.findByText('table.column.status');
    await waitFor(() => {
      fireEvent.click(statusColumnHeader);
    }, { timeout: 3000 });


    expect(setTableFunctionSpy).toHaveBeenCalledWith([ 'status', 'asc' ], 'queued-and-requested');

    expect(investigationComponent['investigationQueuedAndRequestedSortList']).toEqual([ [ 'status', 'asc' ] ]);
  });


  it('should multisort after column description and status', async () => {
    const { fixture } = await renderInvestigations();
    const investigationsComponent = fixture.componentInstance;

    let setTableFunctionSpy = spyOn<any>(investigationsComponent, 'setTableSortingList').and.callThrough();
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
    expect(investigationsComponent['ctrlKeyState']).toBeTruthy();
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
    expect(investigationsComponent['investigationReceivedSortList']).toEqual([ [ 'description', 'asc' ], [ 'status', 'desc' ] ]);
  });

  it('should reset sorting after third click', async () => {
    const { fixture } = await renderInvestigations();
    const investigationsComponent = fixture.componentInstance;

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

    expect(investigationsComponent['investigationReceivedSortList']).toEqual([]);
  });

});
