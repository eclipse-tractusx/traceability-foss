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


import { CalendarDateModel } from '@core/model/calendar-date.model';
import { Notification, NotificationStatus } from '@shared/model/notification.model';
import { NotificationModule } from '@shared/modules/notification/notification.module';
import { SharedModule } from '@shared/shared.module';
import { TemplateModule } from '@shared/template.module';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { of } from 'rxjs';
import { ApproveNotificationModalComponent } from './approve-notification-modal.component';

describe('ApproveNotificationModalComponent', () => {
  let notification: Notification = {
    id: 'id-1',
    description: 'Investigation No 1',
    createdBy: 'OEM A',
    assetIds: [
      'MOCK_part_1',
      'urn:uuid:8cdc4414-91a5-47af-8c96-da39ccefbab8',
      'urn:uuid:81b4dd13-2ead-4a89-9253-3494ba8fc8e5',
      'urn:uuid:35ba10d6-c41c-456e-8ed0-92747530a132',
    ],
    status: NotificationStatus.RECEIVED,
    createdDate: new CalendarDateModel('2022-05-01T10:34:12.000Z'),
  };

  const renderModal = async () => {
    const { fixture } = await renderComponent(ApproveNotificationModalComponent, {
      declarations: [ApproveNotificationModalComponent],
      imports: [NotificationModule, SharedModule, TemplateModule],
    });

    fixture.componentInstance.approveCall = (id: string) => of(null);
    fixture.componentInstance.show(notification);
    fixture.autoDetectChanges();

    return fixture;
  };

  it('should create approve modal', async () => {
    await renderModal();
    const title = await waitFor(() => screen.getByText('Approval of investigation'));
    const hint = await waitFor(() => screen.getByText('Are you sure you want to approve this investigation?'));
    const buttonL = await waitFor(() => screen.getByText('Cancel'));
    const buttonR = await waitFor(() => screen.getByText('Approve'));

    expect(title).toBeInTheDocument();
    expect(hint).toBeInTheDocument();
    expect(buttonL).toBeInTheDocument();
    expect(buttonR).toBeInTheDocument();
  });

  it('should render investigation description', async () => {
    await renderModal();
    const description = await waitFor(() => screen.getByText(notification.description));

    expect(description).toBeInTheDocument();
  });

  xit('should call approve function', async () => {
    const fixture = await renderModal();
    const randomSpyName = spyOn((fixture.componentInstance as any).toastService, 'success').and.returnValue(of([]));

    const buttonR = await waitFor(() => screen.getByText('Approve'));
    buttonR.click();

    await waitFor(() => expect(randomSpyName).toHaveBeenCalled());
    await waitFor(() => expect(randomSpyName).toHaveBeenCalledWith('commonInvestigation.modal.successfullyApproved'));
  });
});
