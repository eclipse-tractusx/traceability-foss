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

import { AfterViewInit, Component, Input, ViewChild } from '@angular/core';
import { CalendarDateModel } from '@core/model/calendar-date.model';
import { Notification, NotificationStatus } from '@shared/model/notification.model';
import { Severity } from '@shared/model/severity.model';
import { TranslationContext } from '@shared/model/translation-context.model';
import { AcceptNotificationModalComponent } from '@shared/modules/notification/modal/accept/accept-notification-modal.component';
import { AcknowledgeNotificationModalComponent } from '@shared/modules/notification/modal/acknowledge/acknowledge-notification-modal.component';
import { ApproveNotificationModalComponent } from '@shared/modules/notification/modal/approve/approve-notification-modal.component';
import { CancelNotificationModalComponent } from '@shared/modules/notification/modal/cancel/cancel-notification-modal.component';
import { CloseNotificationModalComponent } from '@shared/modules/notification/modal/close/close-notification-modal.component';
import { DeclineNotificationModalComponent } from '@shared/modules/notification/modal/decline/decline-notification-modal.component';
import { NotificationModule } from '@shared/modules/notification/notification.module';
import { SharedModule } from '@shared/shared.module';
import { TemplateModule } from '@shared/template.module';
import { renderComponent } from '@tests/test-render.utils';
import { of } from 'rxjs';

@Component({
  selector: '',
  template:
    '<app-toast-container></app-toast-container><app-accept-notification-modal [translationContext]="TranslationContext.COMMONINVESTIGATION" [acceptCall]="call"></app-accept-notification-modal>',
})
class AcceptModalComponent implements AfterViewInit {
  @ViewChild(AcceptNotificationModalComponent) modal: AcceptNotificationModalComponent;
  @Input() notification: Notification;
  public call = (id: string) => of(null);

  public ngAfterViewInit() {
    this.modal.show(this.notification);
  }

  protected readonly TranslationContext = TranslationContext;
}

@Component({
  selector: '',
  template:
    '<app-toast-container></app-toast-container><app-acknowledge-notification-modal [translationContext]="TranslationContext.COMMONINVESTIGATION" [acknowledgeCall]="call"></app-acknowledge-notification-modal>',
})
class AcknowledgeModalComponent implements AfterViewInit {
  @ViewChild(AcknowledgeNotificationModalComponent) modal: AcknowledgeNotificationModalComponent;
  @Input() notification: Notification;
  public call = (id: string) => of(null);

  public ngAfterViewInit() {
    this.modal.show(this.notification);
  }

  protected readonly TranslationContext = TranslationContext;
}

@Component({
  selector: '',
  template:
    '<app-toast-container></app-toast-container><app-approve-notification-modal [translationContext]="TranslationContext.COMMONINVESTIGATION" [approveCall]="call"></app-approve-notification-modal>',
})
class ApproveModalComponent implements AfterViewInit {
  @ViewChild(ApproveNotificationModalComponent) modal: ApproveNotificationModalComponent;
  @Input() notification: Notification;
  public call = (id: string) => of(null);

  public ngAfterViewInit() {
    this.modal.show(this.notification);
  }

  protected readonly TranslationContext = TranslationContext;
}

@Component({
  selector: '',
  template:
    '<app-toast-container></app-toast-container><app-cancel-notification-modal [translationContext]="TranslationContext.COMMONINVESTIGATION" [cancelCall]="call"></app-cancel-notification-modal>',
})
class CancelModalComponent implements AfterViewInit {
  @ViewChild(CancelNotificationModalComponent) modal: CancelNotificationModalComponent;
  @Input() notification: Notification;
  public call = (id: string) => of(null);

  public ngAfterViewInit() {
    this.modal.show(this.notification);
  }

  protected readonly TranslationContext = TranslationContext;
}

@Component({
  selector: '',
  template:
    '<app-toast-container></app-toast-container><app-decline-notification-modal [translationContext]="TranslationContext.COMMONINVESTIGATION" [declineCall]="call"></app-decline-notification-modal>',
})
class DeclineModalComponent implements AfterViewInit {
  @ViewChild(DeclineNotificationModalComponent) modal: DeclineNotificationModalComponent;
  @Input() notification: Notification;
  public call = (id: string) => of(null);

  public ngAfterViewInit() {
    this.modal.show(this.notification);
  }

  protected readonly TranslationContext = TranslationContext;
}

@Component({
  selector: '',
  template:
    '<app-toast-container></app-toast-container><app-close-notification-modal [translationContext]="TranslationContext.COMMONINVESTIGATION" [closeCall]="call"></app-close-notification-modal>',
})
class CloseModalComponent implements AfterViewInit {
  @ViewChild(CloseNotificationModalComponent) modal: CloseNotificationModalComponent;
  @Input() notification: Notification;
  public call = (id: string) => of(null);

  public ngAfterViewInit() {
    this.modal.show(this.notification);
  }

  protected readonly TranslationContext = TranslationContext;
}

export const notificationTemplate: Notification = {
  id: 'id-1',
  description: 'Investigation No 1',
  createdBy: 'BPNA',
  createdByName: 'CompanyA',
  sendTo: 'BPNB',
  sendToName: 'CompanyB',
  reason: { close: '', accept: '', decline: '' },
  isFromSender: false,
  assetIds: ['MOCK_part_1'],
  status: null,
  title: 'Title',
  severity: Severity.MINOR,
  createdDate: new CalendarDateModel('2022-05-01T10:34:12.000Z'),
};

export const renderAcceptModal = async (status: NotificationStatus) => {
  const notification = { ...notificationTemplate, status };
  const { fixture } = await renderComponent(AcceptModalComponent, {
    declarations: [AcceptModalComponent],
    imports: [NotificationModule, SharedModule, TemplateModule],
    componentProperties: { notification },
  });

  return { fixture, notification };
};

export const renderAcknowledgeModal = async (status: NotificationStatus) => {
  const notification = { ...notificationTemplate, status };
  const { fixture } = await renderComponent(AcknowledgeModalComponent, {
    declarations: [AcknowledgeModalComponent],
    imports: [NotificationModule, SharedModule, TemplateModule],
    componentProperties: { notification },
  });

  return { fixture, notification };
};

export const renderApproveModal = async (status: NotificationStatus) => {
  const notification = { ...notificationTemplate, status };
  const { fixture } = await renderComponent(ApproveModalComponent, {
    declarations: [ApproveModalComponent],
    imports: [NotificationModule, SharedModule, TemplateModule],
    componentProperties: { notification },
  });

  return { fixture, notification };
};

export const renderCancelModal = async (status: NotificationStatus) => {
  const notification = { ...notificationTemplate, status };
  const { fixture } = await renderComponent(CancelModalComponent, {
    declarations: [CancelModalComponent],
    imports: [NotificationModule, SharedModule, TemplateModule],
    componentProperties: { notification },
  });

  return { fixture, notification };
};

export const renderCloseModal = async (status: NotificationStatus) => {
  const notification = { ...notificationTemplate, status };
  const { fixture } = await renderComponent(CloseModalComponent, {
    declarations: [CloseModalComponent],
    imports: [NotificationModule, SharedModule, TemplateModule],
    componentProperties: { notification },
  });

  return { fixture, notification };
};

export const renderDeclineModal = async (status: NotificationStatus) => {
  const notification = { ...notificationTemplate, status };
  const { fixture } = await renderComponent(DeclineModalComponent, {
    declarations: [DeclineModalComponent],
    imports: [NotificationModule, SharedModule, TemplateModule],
    componentProperties: { notification },
  });

  return { fixture, notification };
};
