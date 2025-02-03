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
import { notificationTemplate } from '@shared/modules/notification/modal/modalTestHelper.spec';
import { SharedModule } from '@shared/shared.module';
import { screen } from '@testing-library/angular';
import { renderComponent } from '@tests/test-render.utils';

describe('NotificationReasonComponent', () => {
  const defaultNotification = Object.assign({ ...notificationTemplate });
  const renderReason = (notification: Notification = defaultNotification) => {
    return renderComponent(`<app-notification-reason [notification]='notification'></app-notification-reason>`, {
      imports: [ SharedModule ],
      componentProperties: { notification },
    });
  };

  it('should render description', async () => {
    await renderReason();
    expect(screen.getByText(defaultNotification.description)).toBeInTheDocument();
  });

  it('should render accept reason with sent status', async () => {
    const reason = { accept: 'Accept reason', close: '', decline: '' };
    const status = NotificationStatus.SENT;

    await renderReason({ ...defaultNotification, reason, status });
    expect(screen.getByText(reason.accept)).toBeInTheDocument();
    expect(screen.getByText('commonInvestigation.status.SENT')).toBeInTheDocument();

    expect(screen.getByText(defaultNotification.createdByName)).toBeInTheDocument();
    expect(screen.getByText(defaultNotification.sendToName)).toBeInTheDocument();
  });

  it('should render username from sender', async () => {
    await renderReason();
    expect(screen.getByText(defaultNotification.createdByName)).toBeInTheDocument();
  });
});
