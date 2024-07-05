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

import { Component, EventEmitter, Input, Output, TemplateRef, ViewChild } from '@angular/core';
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { ToastService } from '@shared/components/toasts/toast.service';
import { getTranslationContext } from '@shared/helper/notification-helper';
import { Notification, NotificationStatus } from '@shared/model/notification.model';
import { ModalData } from '@shared/modules/modal/core/modal.model';
import { ModalService } from '@shared/modules/modal/core/modal.service';
import { NotificationProcessingService } from '@shared/service/notification-processing.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-notification-action-modal',
  templateUrl: './notification-action-modal.component.html',
})

export class NotificationActionModalComponent {
  @ViewChild('Modal') modal: TemplateRef<unknown>;
  @Input() callback: (status: NotificationStatus, id: string, reason?: string) => Observable<void>;
  @Output() confirmActionCompleted = new EventEmitter<void>();

  public showTextArea = false;
  public notification: Notification;
  public readonly formGroup;
  private readonly textAreaControl = new UntypedFormControl();
  public reasonHintLabel = null;

  constructor(private readonly toastService: ToastService, private readonly confirmModalService: ModalService, private readonly notificationProcessingService: NotificationProcessingService) {
    this.formGroup = new UntypedFormGroup({ reason: this.textAreaControl });
  }

  getModalDataBasedOnNotificationStatus(desiredStatus: NotificationStatus): NotificationModalData {
    const context = getTranslationContext(this.notification);
    switch (desiredStatus) {
      case NotificationStatus.ACCEPTED: {
        this.reasonHintLabel = context + '.modal.acceptReasonHint';
        return {
          title: context + '.modal.acceptTitle',
          buttonRight: 'actions.accept',
          successMessage: context + '.modal.successfullyAccepted',
          errorMessage: context + '.modal.failedAccept',
          reasonHint: context + '.modal.acceptReasonHint',
        };
      }
      case NotificationStatus.DECLINED: {
        this.reasonHintLabel = context + '.modal.declineReasonHint';

        return {
          title: context + '.modal.declineTitle',
          buttonRight: 'actions.decline',
          successMessage: context + '.modal.successfullyDeclined',
          errorMessage: context + '.modal.failedDecline',
          reasonHint: context + '.modal.declineReasonHint',
        };
      }
      case NotificationStatus.ACKNOWLEDGED: {
        return {
          title: context + '.modal.acknowledgeTitle',
          buttonRight: 'actions.acknowledge',
          successMessage: context + '.modal.successfullyAcknowledged',
          errorMessage: context + '.modal.failedAcknowledge',
        };
      }
      case NotificationStatus.APPROVED: {
        return {
          title: context + '.modal.approvalTitle',
          buttonRight: 'actions.confirm',
          successMessage: context + '.modal.successfullyApproved',
          errorMessage: context + '.modal.failedApprove',
        };
      }
      case NotificationStatus.CANCELED: {
        return {
          title: context + '.modal.cancellationTitle',
          buttonRight: 'actions.cancellationConfirm',
          successMessage: context + '.modal.successfullyCanceled',
          errorMessage: context + '.modal.failedCancel',
        };
      }
      case NotificationStatus.CLOSED: {
        this.reasonHintLabel = context + '.modal.closeReasonHint';
        return {
          title: context + '.modal.closeTitle',
          buttonRight: 'actions.close',
          successMessage: context + '.modal.successfullyClosed',
          errorMessage: context + '.modal.failedClose',
          reasonHint: context + '.modal.closeReasonHint',
        };
      }
    }

  }

  public show(notification: Notification, desiredStatus: NotificationStatus): void {
    this.notification = notification;
    const modalData = this.getModalDataBasedOnNotificationStatus(desiredStatus);

    if (this.hasTextArea(desiredStatus)) {
      this.showTextArea = true;
      this.textAreaControl.setValidators([ Validators.required, Validators.maxLength(1000), Validators.minLength(15) ]);
      this.formGroup.reset();
    } else {
      this.showTextArea = false;
    }
    const onConfirm = (isConfirmed: boolean) => {
      if (!isConfirmed) return;
      this.notificationProcessingService.notificationIdsInLoadingState.add(notification.id);
      console.log('START ', this.notificationProcessingService.notificationIdsInLoadingState);
      const reason = this.formGroup.get('reason').value;
      this.callback(desiredStatus, notification.id, reason).subscribe({
        next: () => {
          this.notificationProcessingService.notificationIdsInLoadingState.delete(notification.id);
          console.log('STOP WITH SUCCESS: ', this.notificationProcessingService.notificationIdsInLoadingState);
          this.toastService.success(modalData.successMessage);
          this.confirmActionCompleted.emit();
        },
        error: () => {
          this.notificationProcessingService.notificationIdsInLoadingState.delete(notification.id);
          console.log('STOP WITH ERROR: ', this.notificationProcessingService.notificationIdsInLoadingState);
          this.toastService.error(modalData.errorMessage, 15000, true);
          this.confirmActionCompleted.emit();
        },
      });
    };

    const options: ModalData = {
      title: modalData.title,
      buttonRight: modalData.buttonRight,
      template: this.modal,
      onConfirm,
    };

    if (desiredStatus === NotificationStatus.CANCELED) {
      options.primaryButtonColour = 'warn';
      options.notificationId = this.notification.id;
      options.type = getTranslationContext(this.notification) + '.modal.cancellationConfirmationLabel';
    }
    if (this.hasTextArea(desiredStatus)) {
      options.formGroup = this.formGroup;
    }

    this.confirmModalService.open(options);
  }


  private hasTextArea(desiredStatus: NotificationStatus) {
    return desiredStatus === NotificationStatus.CLOSED || desiredStatus === NotificationStatus.ACCEPTED || desiredStatus === NotificationStatus.DECLINED;
  }
}

export interface NotificationModalData {
  title: string,
  buttonRight: string,
  successMessage: string,
  errorMessage: string,
  reason?: string
  reasonHint?: string
}
