// /********************************************************************************
//  * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
//  * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
//  * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
//  *
//  * See the NOTICE file(s) distributed with this work for additional
//  * information regarding copyright ownership.
//  *
//  * This program and the accompanying materials are made available under the
//  * terms of the Apache License, Version 2.0 which is available at
//  * https://www.apache.org/licenses/LICENSE-2.0.
//  *
//  * Unless required by applicable law or agreed to in writing, software
//  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
//  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
//  * License for the specific language governing permissions and limitations
//  * under the License.
//  *
//  * SPDX-License-Identifier: Apache-2.0
//  ********************************************************************************/

// import { InvestigationsModule } from '@page/investigations/investigations.module';
// import { InvestigationsComponent } from '@page/investigations/presentation/investigations.component';
// import { NotificationTabInformation } from '@shared/model/notification-tab-information';
// import { InvestigationsService } from '@shared/service/investigations.service';
// import { fireEvent, screen, waitFor } from '@testing-library/angular';
// import { renderComponent } from '@tests/test-render.utils';

// describe('InvestigationsComponent', () => {
//   const renderInvestigations = async () => {
//     return await renderComponent(InvestigationsComponent, {
//       imports: [InvestigationsModule],
//       providers: [InvestigationsService],
//       translations: ['page.investigation'],
//     });
//   };

//   // it('should call detail page with correct ID', async () => {
//   //   const { fixture } = await renderInvestigations();
//   //   fireEvent.click((await waitFor(() => screen.getAllByTestId('table-menu-button')))[0]);

//   //   const spy = spyOn((fixture.componentInstance as any).router, 'navigate');
//   //   spy.and.returnValue(new Promise(null));

//   //   fireEvent.click(await waitFor(() => screen.getByTestId('table-menu-button--actions.viewDetails')));
//   //   const tabInformation: NotificationTabInformation = { tabIndex: null, pageNumber: undefined}
//   //   expect(spy).toHaveBeenCalledWith(['/investigations/id-84'], { queryParams: tabInformation } );
//   // });

//   // it('should call change pagination of received investigations', async () => {
//   //   await renderInvestigations();
//   //   fireEvent.click(await waitFor(() => screen.getByLabelText('pagination.nextPageLabel', { selector: 'button' })));

//   //   expect(await waitFor(() => screen.getByText('Investigation No 84'))).toBeInTheDocument();
//   //   expect(await waitFor(() => screen.getByText('Investigation No 11'))).toBeInTheDocument();
//   // });

//   // it('should call change pagination of queued & requested investigations', async () => {
//   //   await renderInvestigations();

//   //   fireEvent.click(await waitFor(() => screen.getByText('commonInvestigation.tabs.queuedAndRequested')));

//   //   fireEvent.click(await waitFor(() => screen.getByLabelText('pagination.nextPageLabel', { selector: 'button' })));

//   //   expect(await waitFor(() => screen.getByText('Investigation No 84'))).toBeInTheDocument();
//   //   expect(await waitFor(() => screen.getByText('Investigation No 11'))).toBeInTheDocument();
//   // });
// });
