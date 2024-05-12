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

import { Notification, NotificationStatus, NotificationType } from '@shared/model/notification.model';
import { SharedModule } from '@shared/shared.module';
import { renderComponent } from '@tests/test-render.utils';
import { NotificationActionModalComponent, NotificationModalData } from './notification-action-modal.component';

describe('NotificationActionModalComponent', () => {

  const renderNotificationActionModalComponent = () => {
    return renderComponent(NotificationActionModalComponent, {
      imports: [ SharedModule ],
      providers: [],
      componentProperties: {},
    });
  };
  it('should create the component', async () => {
    const { fixture } = await renderNotificationActionModalComponent();
    const { componentInstance } = fixture;
    expect(componentInstance).toBeTruthy();
  });

  it('should return ACCEPTED state', async () => {
    const { fixture } = await renderNotificationActionModalComponent();
    const { componentInstance } = fixture;
    const actual = componentInstance.getModalDataBasedOnNotificationStatus(NotificationStatus.ACCEPTED);
    const expected = {
      title: 'commonInvestigation.modal.acceptTitle',
      buttonRight: 'actions.accept',
      successMessage: 'commonInvestigation.modal.successfullyAccepted',
      errorMessage: 'commonInvestigation.modal.failedAccept',
      reasonHint: 'commonInvestigation.modal.acceptReasonHint',
    };
    expect(actual).toEqual(expected);
  });

  it('should return DECLINED state', async () => {
    const { fixture } = await renderNotificationActionModalComponent();
    const { componentInstance } = fixture;
    const actual = componentInstance.getModalDataBasedOnNotificationStatus(NotificationStatus.DECLINED);
    const expected = {
      title: 'commonInvestigation.modal.declineTitle',
      buttonRight: 'actions.decline',
      successMessage: 'commonInvestigation.modal.successfullyDeclined',
      errorMessage: 'commonInvestigation.modal.failedDecline',
      reasonHint: 'commonInvestigation.modal.declineReasonHint',
    };
    expect(actual).toEqual(expected);
  });

  it('should return ACKNOWLEDGED state', async () => {
    const { fixture } = await renderNotificationActionModalComponent();
    const { componentInstance } = fixture;
    const actual = componentInstance.getModalDataBasedOnNotificationStatus(NotificationStatus.ACKNOWLEDGED);
    const expected: NotificationModalData = {
      title: 'commonInvestigation.modal.acknowledgeTitle',
      buttonRight: 'actions.acknowledge',
      successMessage: 'commonInvestigation.modal.successfullyAcknowledged',
      errorMessage: 'commonInvestigation.modal.failedAcknowledge',
    };
    expect(actual).toEqual(expected);
  });

  it('should return APPROVED state', async () => {
    const { fixture } = await renderNotificationActionModalComponent();
    const { componentInstance } = fixture;
    const actual = componentInstance.getModalDataBasedOnNotificationStatus(NotificationStatus.APPROVED);
    const expected: NotificationModalData = {
      title: 'commonInvestigation.modal.approvalTitle',
      buttonRight: 'actions.confirm',
      successMessage: 'commonInvestigation.modal.successfullyApproved',
      errorMessage: 'commonInvestigation.modal.failedApprove',
    };
    expect(actual).toEqual(expected);
  });

  it('should return CANCELED state', async () => {
    const { fixture } = await renderNotificationActionModalComponent();
    const { componentInstance } = fixture;
    const actual = componentInstance.getModalDataBasedOnNotificationStatus(NotificationStatus.CANCELED);
    const expected: NotificationModalData = {
      title: 'commonInvestigation.modal.cancellationTitle',
      buttonRight: 'actions.cancellationConfirm',
      successMessage: 'commonInvestigation.modal.successfullyCanceled',
      errorMessage: 'commonInvestigation.modal.failedCancel',
    };
    expect(actual).toEqual(expected);
  });

  it('should return CLOSED state', async () => {
    const { fixture } = await renderNotificationActionModalComponent();
    const { componentInstance } = fixture;
    const actual = componentInstance.getModalDataBasedOnNotificationStatus(NotificationStatus.CLOSED);
    const expected: NotificationModalData = {
      title: 'commonInvestigation.modal.closeTitle',
      buttonRight: 'actions.close',
      successMessage: 'commonInvestigation.modal.successfullyClosed',
      errorMessage: 'commonInvestigation.modal.failedClose',
      reasonHint: 'commonInvestigation.modal.closeReasonHint',
    };
    expect(actual).toEqual(expected);
  });

  it('should execute show successfully and show text area', async () => {
    const { fixture } = await renderNotificationActionModalComponent();
    const { componentInstance } = fixture;
    const notification: Notification = {
      assetIds: [],
      createdBy: '',
      type: NotificationType.ALERT,
      createdByName: '',
      createdDate: undefined,
      description: '',
      isFromSender: false,
      reason: undefined,
      sendTo: '',
      sendToName: '',
      severity: undefined,
      status: undefined,
      title: '',
      id: 'abc',
    };

    componentInstance.show(notification, NotificationStatus.CLOSED);
    expect(componentInstance.showTextArea).toBeTrue();
  });

});
