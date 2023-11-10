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

import { Component, EventEmitter, Inject, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { ALERT_BASE_ROUTE, getRoute } from '@core/known-route';
import { bpnRegex } from '@page/admin/presentation/bpn-configuration/bpn-configuration.component';
import { Part, SemanticDataModel } from '@page/parts/model/parts.model';
import { BaseInputHelper } from '@shared/abstraction/baseInput/baseInput.helper';
import {
  RequestContext,
  RequestNotificationBase,
} from '@shared/components/request-notification/request-notification.base';
import { ToastService } from '@shared/components/toasts/toast.service';
import { NotificationStatusGroup } from '@shared/model/notification.model';
import { Severity } from '@shared/model/severity.model';
import { AlertsService } from '@shared/service/alerts.service';
import { RequestComponentData } from './request.componenet.model';

@Component({
  selector: 'app-request-alert',
  styleUrls: ['./request-notification.base.scss'],
  templateUrl: './request-notification.base.html',
})
export class RequestAlertComponent extends RequestNotificationBase {

  @Output() deselectPart = new EventEmitter<Part>();
  @Output() restorePart = new EventEmitter<Part>();
  @Output() clearSelected = new EventEmitter<void>();
  @Output() submitted = new EventEmitter<void>();

  public selectedItems: Part[] = [];
  public showHeadline = true;

  public readonly context: RequestContext = 'requestAlert';

  constructor(toastService: ToastService, private readonly alertsService: AlertsService, public dialog: MatDialog, @Inject(MAT_DIALOG_DATA) data: RequestComponentData) {
    super(toastService, dialog);
    this.selectedItems = data.selectedItems;
    this.showHeadline = data.showHeadline;
  }

  public readonly formGroup = new FormGroup({
    description: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.minLength(15)]),
    severity: new FormControl(null, [Validators.required]),
    bpn: new FormControl(null, [Validators.required, BaseInputHelper.getCustomPatternValidator(bpnRegex, 'bpn')]),
  });

  public submit(): void {
    this.prepareSubmit();
    if (this.formGroup.invalid) {
      return;
    }

    const partIds = this.selectedItems.map(part => part.id);
    // set asBuilt parameter if one of the selectedItems are a asPlanned Part
    const isAsBuilt = this.selectedItems.map(part => part.semanticDataModel === SemanticDataModel.PARTASPLANNED).includes(true);

    const { description, bpn, severity } = this.formGroup.value;
    const { link, queryParams } = getRoute(ALERT_BASE_ROUTE, NotificationStatusGroup.QUEUED_AND_REQUESTED);

    this.alertsService.postAlert(partIds, description, severity, bpn, isAsBuilt).subscribe({
      next: () => this.onSuccessfulSubmit(link, queryParams),
      error: () => this.onUnsuccessfulSubmit(),
    });
  }
}
