/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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
import { CancelNotificationModalComponent } from '@shared/modules/notification/modal/cancel/cancel-notification-modal.component';
import { NotificationModule } from '@shared/modules/notification/notification.module';
import { SharedModule } from '@shared/shared.module';
import { TemplateModule } from '@shared/template.module';
import { screen, waitFor } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';
import { of } from 'rxjs';

describe('CancelNotificationModalComponent', () => {
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
    const { fixture } = await renderComponent(CancelNotificationModalComponent, {
      declarations: [CancelNotificationModalComponent],
      imports: [NotificationModule, SharedModule, TemplateModule],
    });

    fixture.componentInstance.cancelCall = (id: string) => of(null);
    fixture.componentInstance.show(notification);
    fixture.autoDetectChanges();

    return fixture;
  };

  it('should create cancel modal', async () => {
    await renderModal();
    const title = await waitFor(() => screen.getByText('Cancellation of investigation'));
    const hint = await waitFor(() => screen.getByText('Are you sure you want to cancel this investigation?'));
    const hint2 = await waitFor(() =>
      screen.getByText('Enter the ID of the investigation to confirm your cancellation.'),
    );
    const buttonL = await waitFor(() => screen.getByText('Cancel'));
    const buttonR = await waitFor(() => screen.getByText('Confirm cancellation'));

    expect(title).toBeInTheDocument();
    expect(hint).toBeInTheDocument();
    expect(hint2).toBeInTheDocument();
    expect(buttonL).toBeInTheDocument();
    expect(buttonR).toBeInTheDocument();
  });

  it('should render investigation description', async () => {
    await renderModal();
    const description = await waitFor(() => screen.getByText(notification.description));

    expect(description).toBeInTheDocument();
  });

  it('should check validation of textarea', async () => {
    const fixture = await renderModal();
    const buttonR = await waitFor(() => screen.getByText('Confirm cancellation'));
    buttonR.click();

    const textArea: HTMLTextAreaElement = await waitFor(() => screen.getByTestId('TextAreaComponent-0'));

    const errorMessage_1 = await waitFor(() => screen.getByText('This field is required!'));
    expect(errorMessage_1).toBeInTheDocument();

    textArea.value = 'error';
    textArea.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    const errorMessage_2 = await waitFor(() =>
      screen.getByText(`Please enter data that matches this pattern: ^${notification.id}$.`),
    );
    expect(errorMessage_2).toBeInTheDocument();

    textArea.value = notification.id;
    textArea.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(errorMessage_2).not.toBeInTheDocument();
  });

  xit('should call cancel function', async () => {
    const fixture = await renderModal();
    const randomSpyName = spyOn((fixture.componentInstance as any).toastService, 'success').and.returnValue(of([]));

    const buttonR = await waitFor(() => screen.getByText('Confirm cancellation'));
    buttonR.click();

    await waitFor(() => expect(randomSpyName).toHaveBeenCalled());
    await waitFor(() => expect(randomSpyName).toHaveBeenCalledWith('commonInvestigation.modal.successfullyApproved'));
  });
});
