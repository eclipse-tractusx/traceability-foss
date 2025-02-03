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
import { ToastService } from '@shared/components/toasts/toast.service';
import { Notification } from '@shared/model/notification.model';
import { TranslationContext } from '@shared/model/translation-context.model';
import { ModalData } from '@shared/modules/modal/core/modal.model';
import { ModalService } from '@shared/modules/modal/core/modal.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-acknowledge-notification-modal',
  templateUrl: './acknowledge-notification-modal.component.html',
})
export class AcknowledgeNotificationModalComponent {
  @ViewChild('Modal') modal: TemplateRef<unknown>;
  @Input() acknowledgeCall: (id: string) => Observable<void>;
  @Input() translationContext: TranslationContext;
  @Output() confirmActionCompleted = new EventEmitter<void>();

  public notification: Notification;

  constructor(private readonly toastService: ToastService, private readonly confirmModalService: ModalService) { }

  public show(notification: Notification): void {
    this.notification = notification;
    const onConfirm = (isConfirmed: boolean) => {
      if (!isConfirmed) return;

      this.acknowledgeCall(notification.id).subscribe({
        next: () => {
          this.toastService.success(this.translationContext + '.modal.successfullyAcknowledgedTitle', this.translationContext + '.modal.successfullyAcknowledged');
          this.confirmActionCompleted.emit();
        },
        error: () => {
          this.toastService.error(this.translationContext + '.modal.failedAcknowledgeTitle', this.translationContext + '.modal.failedAcknowledge');
        },
      });
    };

    const options: ModalData = {
      title: this.translationContext + '.modal.acknowledgeTitle',
      buttonRight: 'actions.acknowledge',
      buttonLeft: 'actions.cancel',

      template: this.modal,
      onConfirm,
    };

    this.confirmModalService.open(options);
  }
}
