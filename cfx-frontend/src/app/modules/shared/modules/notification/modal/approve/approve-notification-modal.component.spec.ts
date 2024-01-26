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
import { renderApproveModal } from '@shared/modules/notification/modal/modalTestHelper.spec';
import { fireEvent, screen, waitFor } from '@testing-library/angular';
import { ApproveNotificationModalComponent } from './approve-notification-modal.component';

describe('ApproveNotificationModalComponent', () => {
  it('should create approve modal', async () => {
    await renderApproveModal(NotificationStatus.CREATED);
    const title = await waitFor(() => screen.getByText('commonInvestigation.modal.approvalTitle'));
    const buttonL = await waitFor(() => screen.getByText('actions.cancel'));
    const buttonR = await waitFor(() => screen.getByText('actions.confirm'));

    expect(title).toBeInTheDocument();
    expect(buttonL).toBeInTheDocument();
    expect(buttonR).toBeInTheDocument();
  });

  it('should render investigation description', async () => {
    const { notification } = await renderApproveModal(NotificationStatus.CREATED);
    const description = await waitFor(() => screen.getAllByText(notification.description));

    expect(description[0]).toBeInTheDocument();
  });

  it('should call approve function', async () => {
    await renderApproveModal(NotificationStatus.CREATED);
    fireEvent.click(await waitFor(() => screen.getByText('actions.confirm')));

    await waitFor(() => expect(screen.getByText('commonInvestigation.modal.successfullyApproved')).toBeInTheDocument());
  });
});
