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
import { NotificationStatusGroup } from '@shared/model/notification.model';
import { InvestigationsService } from '@shared/service/investigations.service';
import { Part } from '@page/parts/model/parts.model';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-request-investigation',
  templateUrl: './request-notification.base.html',
})
export class RequestInvestigationComponent extends RequestNotificationBase {
  @Input() selectedItems: Part[];
  @Input() showHeadline = true;

  @Output() deselectPart = new EventEmitter<Part>();
  @Output() restorePart = new EventEmitter<Part>();
  @Output() clearSelected = new EventEmitter<void>();
  @Output() submitted = new EventEmitter<void>();

  public readonly context: RequestContext = 'requestInvestigations';

  constructor(toastService: ToastService, private readonly investigationsService: InvestigationsService, public dialog: MatDialog) {
    super(toastService, dialog);
  }

  public readonly formGroup = new FormGroup<{
    description: FormControl<string>;
    targetDate: FormControl<DateTimeString>;
    severity: FormControl<Severity>;
  }>({
    description: new FormControl('', [Validators.required, Validators.maxLength(1000), Validators.minLength(15)]),
    targetDate: new FormControl(null, [DateValidators.atLeastNow()]),
    severity: new FormControl(Severity.MINOR, [Validators.required]),
  });

  public submit(): void {
    this.prepareSubmit();
    if (this.formGroup.invalid) return;

    const partIds = this.selectedItems.map(part => part.id);
    const { description, targetDate, severity } = this.formGroup.value;
    const { link, queryParams } = getRoute(INVESTIGATION_BASE_ROUTE, NotificationStatusGroup.QUEUED_AND_REQUESTED);

    this.investigationsService.postInvestigation(partIds, description, severity, targetDate).subscribe({
      next: () => this.onSuccessfulSubmit(link, queryParams),
      error: () => this.onUnsuccessfulSubmit(),
    });
  }
}
