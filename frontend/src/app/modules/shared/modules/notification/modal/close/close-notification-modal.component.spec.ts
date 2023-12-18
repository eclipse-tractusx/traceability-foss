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
import { CloseNotificationModalComponent } from '@shared/modules/notification/modal/close/close-notification-modal.component';
import { renderCloseModal } from '@shared/modules/notification/modal/modalTestHelper.spec';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { getRandomText } from '../../../../../../mocks/services/text-generator.helper';

describe('CloseNotificationModalComponent', () => {
  it('should create close modal', async () => {
    await renderCloseModal(NotificationStatus.SENT);
    const title = await waitFor(() => screen.getByText('commonInvestigation.modal.closeTitle'));
    const hint2 = await waitFor(() => screen.getByText('commonInvestigation.modal.closeReasonHint'));
    const buttonR = await waitFor(() => screen.getByText('actions.close'));

    expect(title).toBeInTheDocument();
    expect(hint2).toBeInTheDocument();
    expect(buttonR).toBeInTheDocument();
  });

  it('should render investigation description', async () => {
    const { notification } = await renderCloseModal(NotificationStatus.SENT);
    const description = await waitFor(() => screen.getByText(notification.description));

    expect(description).toBeInTheDocument();
  });

  it('should check validation of textarea', async () => {
    await renderCloseModal(NotificationStatus.SENT);
    fireEvent.click(await waitFor(() => screen.getByText('actions.close')));
    const textArea: HTMLTextAreaElement = await waitFor(() => screen.getByTestId('BaseInputElement-0'));

    const errorMessage_1 = await waitFor(() => screen.getByText('errorMessage.required'));
    expect(errorMessage_1).toBeInTheDocument();

    fireEvent.input(textArea, { target: { value: 'Some Text' } });
    const errorMessage_2 = await waitFor(() => screen.getByText('errorMessage.minLength'));
    expect(errorMessage_2).toBeInTheDocument();

    fireEvent.input(textArea, { target: { value: getRandomText(1500) } });
    const errorMessage_3 = await waitFor(() => screen.getByText('errorMessage.maxLength'));
    expect(errorMessage_3).toBeInTheDocument();

    fireEvent.input(textArea, { target: { value: 'Some longer text with at least 15 chars' } });
    expect(errorMessage_1).not.toBeInTheDocument();
    expect(errorMessage_2).not.toBeInTheDocument();
    expect(errorMessage_3).not.toBeInTheDocument();
  });

  it('should call close function', async () => {
    await renderCloseModal(NotificationStatus.SENT);

    const textArea = await waitFor(() => screen.getByTestId('BaseInputElement-0'));
    fireEvent.input(textArea, { target: { value: 'Some Text Some Text Some Text' } });

    fireEvent.click(await waitFor(() => screen.getByText('actions.close')));
    await waitFor(() => expect(screen.getByText('commonInvestigation.modal.successfullyClosed')).toBeInTheDocument());
  });
});
