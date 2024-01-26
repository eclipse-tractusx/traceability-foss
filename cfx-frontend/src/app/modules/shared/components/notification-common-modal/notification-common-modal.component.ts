/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
import {
  Component,
  EventEmitter,
  Input, Optional,
  Output,
  ViewChild,
} from '@angular/core';
import { AlertHelperService } from '@page/alerts/core/alert-helper.service';
import { AlertsFacade } from '@page/alerts/core/alerts.facade';
import { InvestigationHelperService } from '@page/investigations/core/investigation-helper.service';
import { InvestigationsFacade } from '@page/investigations/core/investigations.facade';
import { Notification } from '@shared/model/notification.model';
import { TranslationContext } from '@shared/model/translation-context.model';
import { AcceptNotificationModalComponent } from '@shared/modules/notification/modal/accept/accept-notification-modal.component';
import { AcknowledgeNotificationModalComponent } from '@shared/modules/notification/modal/acknowledge/acknowledge-notification-modal.component';
import { ApproveNotificationModalComponent } from '@shared/modules/notification/modal/approve/approve-notification-modal.component';
import { CancelNotificationModalComponent } from '@shared/modules/notification/modal/cancel/cancel-notification-modal.component';
import { CloseNotificationModalComponent } from '@shared/modules/notification/modal/close/close-notification-modal.component';
import { DeclineNotificationModalComponent } from '@shared/modules/notification/modal/decline/decline-notification-modal.component';

@Component({
  selector: 'app-notification-common-modal',
  templateUrl: './notification-common-modal.component.html',
})

export class NotificationCommonModalComponent {
  @Input() selectedNotification: Notification;
  @Input() translationContext: TranslationContext;
  @Input() helperService: InvestigationHelperService | AlertHelperService;
  @Output() confirmActionCompleted = new EventEmitter<void>();


  @ViewChild(ApproveNotificationModalComponent) approveModal: ApproveNotificationModalComponent;
  @ViewChild(CloseNotificationModalComponent) closeModal: CloseNotificationModalComponent;
  @ViewChild(CancelNotificationModalComponent) cancelModal: CancelNotificationModalComponent;

  @ViewChild(AcceptNotificationModalComponent) acceptModal: AcceptNotificationModalComponent;
  @ViewChild(AcknowledgeNotificationModalComponent) acknowledgeModal: AcknowledgeNotificationModalComponent;
  @ViewChild(DeclineNotificationModalComponent) declineModal: DeclineNotificationModalComponent;


  public constructor(
    @Optional() private readonly investigationsFacade: InvestigationsFacade,
    @Optional() private readonly alertsFacade: AlertsFacade
  ) {}


  public handleModalConfirmActionCompletedEvent(): void {
    this.confirmActionCompleted.emit();
  }

  public show(modalContext: string, notification?: Notification) {
    const notificationToShow = notification || this.selectedNotification;
    switch (modalContext) {
      case 'approve': {
        this.approveModal.show(notificationToShow);
        break;
      }
      case 'close': {
        this.closeModal.show(notificationToShow);
        break;
      }
      case 'cancel': {
        this.cancelModal.show(notificationToShow);
        break;
      }
      case 'accept': {
        this.acceptModal.show(notificationToShow);
        break;
      }
      case 'acknowledge': {
        this.acknowledgeModal.show(notificationToShow);
        break;
      }
      case 'decline': {
        this.declineModal.show(notificationToShow);
        break;
      }
    }
  }


  protected readonly TranslationContext = TranslationContext;
}
