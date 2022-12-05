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
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ToastService } from '@shared/components/toasts/toast.service';
import { Notification } from '@shared/model/notification.model';
import { ModalData } from '@shared/modules/modal/core/modal.model';
import { ModalService } from '@shared/modules/modal/core/modal.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-close-notification-modal',
  templateUrl: './close-notification-modal.component.html',
})
export class CloseNotificationModalComponent {
  @ViewChild('Modal') modal: TemplateRef<unknown>;
  @Input() closeCall: (id: string, reason: string) => Observable<void>;
  @Output() confirmActionCompleted = new EventEmitter<void>();

  public notification: Notification;
  public readonly formGroup;
  private readonly textAreaControl = new FormControl();

  constructor(private readonly toastService: ToastService, private readonly confirmModalService: ModalService) {
    this.formGroup = new FormGroup({ reason: this.textAreaControl });
  }

  public show(notification: Notification): void {
    this.notification = notification;
    this.textAreaControl.setValidators([Validators.required]);

    const onConfirm = (isConfirmed: boolean) => {
      const reason = this.formGroup.get('reason').value;
      this.formGroup.reset();

      if (!isConfirmed) return;

      this.closeCall(notification.id, reason).subscribe({
        next: () => {
          this.toastService.success('commonInvestigation.modal.successfullyClosed');
          this.confirmActionCompleted.emit();
        },
        error: () => {
          this.toastService.error('commonInvestigation.modal.failedClose');
        },
      });
    };

    const options: ModalData = {
      title: 'commonInvestigation.modal.closeTitle',
      buttonRight: 'commonInvestigation.modal.close',
      buttonLeft: 'commonInvestigation.modal.cancel',

      template: this.modal,
      formGroup: this.formGroup,
      onConfirm,
    };

    this.confirmModalService.open(options);
  }
}
