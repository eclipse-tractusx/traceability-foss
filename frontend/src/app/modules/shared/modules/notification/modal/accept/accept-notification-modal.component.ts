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
import { Notification } from '@shared/model/notification.model';
import { TranslationContext } from '@shared/model/translation-context.model';
import { ModalData } from '@shared/modules/modal/core/modal.model';
import { ModalService } from '@shared/modules/modal/core/modal.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-accept-notification-modal',
  templateUrl: './accept-notification-modal.component.html',
})
export class AcceptNotificationModalComponent {
  @ViewChild('Modal') modal: TemplateRef<unknown>;
  @Input() acceptCall: (id: string, reason: string) => Observable<void>;
  @Input() translationContext: TranslationContext;
  @Output() confirmActionCompleted = new EventEmitter<void>();

  public notification: Notification;
  public readonly formGroup;
  private readonly textAreaControl = new UntypedFormControl();

  constructor(private readonly toastService: ToastService, private readonly confirmModalService: ModalService) {
    this.formGroup = new UntypedFormGroup({ reason: this.textAreaControl });
  }

  public show(notification: Notification): void {
    this.notification = notification;


    this.textAreaControl.setValidators([ Validators.required, Validators.maxLength(1000), Validators.minLength(15) ]);

    const onConfirm = (isConfirmed: boolean) => {
      const reason = this.formGroup.get('reason').value;
      this.formGroup.reset();

      if (!isConfirmed) return;

      this.acceptCall(notification.id, reason).subscribe({
        next: () => {
          this.toastService.success(this.translationContext + '.modal.successfullyAccepted');
          this.confirmActionCompleted.emit();
        },
        error: () => {
          this.toastService.error(this.translationContext + '.modal.failedAccept');
        },
      });
    };

    const options: ModalData = {
      title: this.translationContext + '.modal.acceptTitle',
      buttonRight: 'actions.accept',
      template: this.modal,
      formGroup: this.formGroup,
      onConfirm,
    };

    this.confirmModalService.open(options);
  }
}
