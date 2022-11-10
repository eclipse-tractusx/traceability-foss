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
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { InvestigationsFacade } from '@page/investigations/core/investigations.facade';
import { CtaSnackbarService } from '@shared/components/call-to-action-snackbar/cta-snackbar.service';
import { ConfirmModalData } from '@shared/modules/modal/core/modal.model';
import { ModalService } from '@shared/modules/modal/core/modal.service';
import { Notification } from '@shared/model/notification.model';

@Component({
  selector: 'app-close-notification-modal',
  templateUrl: './close-notification-modal.component.html',
  styleUrls: ['./close-notification-modal.component.scss'],
})
export class CloseNotificationModalComponent implements OnInit {
  @ViewChild('ModalClose') modalClose: TemplateRef<unknown>;
  @Input() notification: Notification;

  private readonly textAreaControl = new FormControl();
  public readonly closeFormGroup = new FormGroup({ reason: this.textAreaControl });

  constructor(
    private readonly investigationsFacade: InvestigationsFacade,
    private readonly ctaSnackbarService: CtaSnackbarService,
    private readonly confirmModalService: ModalService,
  ) {}

  ngOnInit(): void {}

  public closeAction(notification: Notification): void {
    this.notification = notification;
    this.textAreaControl.setValidators([Validators.required]);

    const onConfirm = (isConfirmed: boolean) => {
      const reason = this.closeFormGroup.get('reason').value;

      if (isConfirmed) {
        this.investigationsFacade.closeInvestigation(notification.id, reason).subscribe({
          next: () => {
            this.ctaSnackbarService.show({ id: 'commonInvestigation.modal.closeSuccess' });
          },
          error: () => {
            // TODO: error handling
          },
        });
      }
    };

    const options: ConfirmModalData = {
      title: `commonInvestigation.modal.closeTitle`,
      confirmText: `commonInvestigation.modal.close`,
      cancelText: `commonInvestigation.modal.cancel`,

      template: this.modalClose,
      formGroup: this.closeFormGroup,
      onConfirm,
    };

    this.confirmModalService.open(options);
  }
}
