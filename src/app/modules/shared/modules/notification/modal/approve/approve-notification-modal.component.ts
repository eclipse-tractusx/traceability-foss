/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

import { Component, Input, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { ToastService } from '@shared/components/toasts/toast.service';
import { Notification } from '@shared/model/notification.model';
import { ModalData } from '@shared/modules/modal/core/modal.model';
import { ModalService } from '@shared/modules/modal/core/modal.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-approve-notification-modal',
  templateUrl: './approve-notification-modal.component.html',
})
export class ApproveNotificationModalComponent implements OnInit {
  @ViewChild('Modal') modal: TemplateRef<unknown>;
  @Input() approveCall: (id: string) => Observable<void>;

  public notification: Notification;

  constructor(private readonly toastService: ToastService, private readonly confirmModalService: ModalService) {}

  ngOnInit(): void {}

  public show(notification: Notification): void {
    this.notification = notification;

    const onConfirm = (isConfirmed: boolean) => {
      if (!isConfirmed) return;

      this.approveCall(notification.id).subscribe({
        next: () => {
          this.toastService.success('commonInvestigation.modal.successfullyApproved');
        },
        error: () => {
          this.toastService.error('commonInvestigation.modal.failedApprove');
        },
      });
    };

    const options: ModalData = {
      title: 'commonInvestigation.modal.approvalTitle',
      buttonRight: 'commonInvestigation.modal.confirm',
      buttonLeft: 'commonInvestigation.modal.cancel',

      template: this.modal,
      onConfirm,
    };

    this.confirmModalService.open(options);
  }
}
