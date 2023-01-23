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

import { NotificationStatus } from '@shared/model/notification.model';
import { CancelNotificationModalComponent } from '@shared/modules/notification/modal/cancel/cancel-notification-modal.component';
import { renderCancelModal } from '@shared/modules/notification/modal/modalTestHelper.spec';
import { screen, waitFor } from '@testing-library/angular';

describe('CancelNotificationModalComponent', () => {
  it('should create cancel modal', async () => {
    await renderCancelModal(NotificationStatus.CREATED);
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
    const { notification } = await renderCancelModal(NotificationStatus.CREATED);
    const description = await waitFor(() => screen.getByText(notification.description));

    expect(description).toBeInTheDocument();
  });

  it('should check validation of textarea', async () => {
    const { fixture, notification } = await renderCancelModal(NotificationStatus.CREATED);
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

  it('should call cancel function', async () => {
    const { fixture, notification } = await renderCancelModal(NotificationStatus.CREATED);

    const textArea: HTMLTextAreaElement = await waitFor(() => screen.getByTestId('TextAreaComponent-0'));
    textArea.value = notification.id;
    textArea.dispatchEvent(new Event('input'));
    fixture.detectChanges();

    const buttonR = await waitFor(() => screen.getByText('Confirm cancellation'));
    buttonR.click();

    await waitFor(() => expect(screen.getByText('Investigation was canceled successfully.')).toBeInTheDocument());
  });
});
