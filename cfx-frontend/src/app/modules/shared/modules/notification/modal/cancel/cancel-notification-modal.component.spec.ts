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
import { fireEvent, screen, waitFor } from '@testing-library/angular';

describe('CancelNotificationModalComponent', () => {
  it('should create cancel modal', async () => {
    await renderCancelModal(NotificationStatus.CREATED);
    const title = await waitFor(() => screen.getByText('commonInvestigation.modal.cancellationTitle'));
    const hint2 = await waitFor(() => screen.getByText('commonInvestigation.modal.cancellationHint'));
    const buttonL = await waitFor(() => screen.getByText('actions.cancel'));
    const buttonR = await waitFor(() => screen.getByText('actions.cancellationConfirm'));

    expect(title).toBeInTheDocument();
    expect(hint2).toBeInTheDocument();
    expect(buttonL).toBeInTheDocument();
    expect(buttonR).toBeInTheDocument();
  });

  it('should render investigation description', async () => {
    const { notification } = await renderCancelModal(NotificationStatus.CREATED);
    const description = await waitFor(() => screen.getAllByText(notification.description));

    expect(description[0]).toBeInTheDocument();
  });

  it('should check validation of textarea', async () => {
    const { notification } = await renderCancelModal(NotificationStatus.CREATED);

    fireEvent.click(await waitFor(() => screen.getByText('actions.cancellationConfirm')));
    const errorMessage_1 = await waitFor(() => screen.getByText('errorMessage.required'));
    expect(errorMessage_1).toBeInTheDocument();

    const textArea: HTMLTextAreaElement = await waitFor(() => screen.getByTestId('BaseInputElement-0'));
    fireEvent.input(textArea, { target: { value: 'error' } });
    const errorMessage_2 = await waitFor(() => screen.getByText('errorMessage.pattern'));
    expect(errorMessage_2).toBeInTheDocument();

    fireEvent.input(textArea, { target: { value: notification.id } });
    expect(errorMessage_2).not.toBeInTheDocument();
  });

  it('should call cancel function', async () => {
    const { notification } = await renderCancelModal(NotificationStatus.CREATED);

    const textArea: HTMLTextAreaElement = await waitFor(() => screen.getByTestId('BaseInputElement-0'));
    fireEvent.input(textArea, { target: { value: notification.id } });

    fireEvent.click(await waitFor(() => screen.getByText('actions.cancellationConfirm')));
    await waitFor(() => expect(screen.getByText('commonInvestigation.modal.successfullyCanceled')).toBeInTheDocument());
  });
});
