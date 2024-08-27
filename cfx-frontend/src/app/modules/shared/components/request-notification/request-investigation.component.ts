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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { DateTimeString } from '@shared/components/dateTime/dateTime.component';
import { DateValidators } from '@shared/components/dateTime/dateValidators.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import { Severity } from '@shared/model/severity.model';
import {
  RequestContext,
  RequestNotificationBase,
} from '@shared/components/request-notification/request-notification.base';
import { getRoute, INVESTIGATION_BASE_ROUTE } from '@core/known-route';
import { bpnRegex } from '@page/admin/presentation/bpn-configuration/bpn-configuration.component';
import { BaseInputHelper } from '@shared/abstraction/baseInput/baseInput.helper';
import { NotificationStatusGroup } from '@shared/model/notification.model';
import { Part } from '@page/parts/model/parts.model';
import { MatDialog } from '@angular/material/dialog';
import { NotificationService } from '@shared/service/notification.service';

@Component({
  selector: 'app-request-investigation',
  templateUrl: './request-notification.base.html',
  styleUrls: ['./request-notification.base.scss'],
})
export class RequestInvestigationComponent extends RequestNotificationBase {
  @Output() deselectPart = new EventEmitter<Part>();
  @Output() restorePart = new EventEmitter<Part>();
  @Output() clearSelected = new EventEmitter<void>();
  @Output() submitted = new EventEmitter<void>();
  @Output() onBackClicked = new EventEmitter<void>();

  @Input() selectedItems: Part[] = [];

  public readonly context: RequestContext = RequestContext.REQUEST_INVESTIGATION;
  @Input() public formGroup: FormGroup<{ description: FormControl<string>; targetDate: FormControl<DateTimeString>; severity: FormControl<Severity>; bpn: FormControl<any>; title: FormControl<string>; }>;

  constructor(toastService: ToastService, private readonly investigationsService: NotificationService, public dialog: MatDialog) {
    super(toastService, dialog);
  }

  public ngOnInit(): void {
    this.formGroup = new FormGroup({
      description: new FormControl(this.forwardedNotification ? 'FW: ' + this.forwardedNotification.description : '', [ Validators.required, Validators.maxLength(1000), Validators.minLength(15) ]),
      targetDate: new FormControl(null, [DateValidators.atLeastNow(), DateValidators.maxDeadline(this.forwardedNotification?.targetDate?.valueOf()), Validators.required]),
      severity: new FormControl(this.forwardedNotification ? this.forwardedNotification.severity : Severity.MINOR),
      bpn: new FormControl(null, [ Validators.required, BaseInputHelper.getCustomPatternValidator(bpnRegex, 'bpn') ]),
      title: new FormControl('', [ Validators.maxLength(30), Validators.minLength(0) ]),
    });

  }

  public submit(): void {
    if (this.selectedItems.length === 0) {
      return;
    }
    this.prepareSubmit();
    if (this.formGroup.invalid) {
      return;
    }
    const affectedPartIds = this.selectedItems.map(part => part.id);
    const { description, targetDate, severity, bpn, title } = this.formGroup.value;
    const { link, queryParams } = getRoute(INVESTIGATION_BASE_ROUTE, NotificationStatusGroup.QUEUED_AND_REQUESTED);

    this.investigationsService.createInvestigation(affectedPartIds, description, severity, targetDate, bpn, title).subscribe({
      next: () => this.onSuccessfulSubmit(link, queryParams),
      error: () => this.onUnsuccessfulSubmit(),
    });

    this.dialog.closeAll();
  }
}
