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
import { Notification } from '@shared/model/notification.model';
import { ConfirmModalData } from '@shared/modules/modal/core/modal.model';
import { InvestigationsFacade } from '@page/investigations/core/investigations.facade';
import { CtaSnackbarService } from '@shared/components/call-to-action-snackbar/cta-snackbar.service';
import { ModalService } from '@shared/modules/modal/core/modal.service';

@Component({
  selector: 'app-approve-notification-modal',
  templateUrl: './approve-notification-modal.component.html',
  styleUrls: ['./approve-notification-modal.component.scss'],
})
export class ApproveNotificationModalComponent implements OnInit {
  @ViewChild('Modal') modal: TemplateRef<unknown>;
  @Input() notification: Notification;

  constructor(
    private readonly investigationsFacade: InvestigationsFacade,
    private readonly ctaSnackbarService: CtaSnackbarService,
    private readonly confirmModalService: ModalService,
  ) {}

  ngOnInit(): void {}

  public show(notification: Notification): void {
    this.notification = notification;
    // TODO: implement call to endpoint
    const onConfirm = (isConfirmed: boolean) => console.log(isConfirmed);

    const options: ConfirmModalData = {
      title: 'commonInvestigation.modal.approvalTitle',
      confirmText: 'commonInvestigation.modal.confirm',
      cancelText: 'commonInvestigation.modal.cancel',

      template: this.modal,
      onConfirm,
    };

    this.confirmModalService.open(options);
  }
}
