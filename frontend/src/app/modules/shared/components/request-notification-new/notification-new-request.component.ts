/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
import { bpnRegex } from '@page/admin/presentation/bpn-configuration/bpn-configuration.component';
import { Part } from '@page/parts/model/parts.model';
import { BaseInputHelper } from '@shared/abstraction/baseInput/baseInput.helper';
import { DateValidators } from '@shared/components/dateTime/dateValidators.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import { Severity } from '@shared/model/severity.model';
import { NotificationService } from '@shared/service/notification.service';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-notification-new-request',
  templateUrl: './notification-new-request.component.html',
})
export class RequestNotificationNewComponent {
  @Input() selectedItems: Part[];
  @Input() showHeadline = true;

  context: string;

  formGroup = new FormGroup<any>({});


  @Output() submitted = new EventEmitter<void>();

  public readonly isLoading$ = new BehaviorSubject(false);
  public readonly minDate = new Date();

  constructor() {
    this.context = 'requestAlert';
    this.formGroup.addControl('title', new FormControl('', [ Validators.maxLength(30), Validators.minLength(0) ]));
    this.formGroup.addControl('description', new FormControl('', [ Validators.required, Validators.maxLength(1000), Validators.minLength(15) ]));
    this.formGroup.addControl('severity', new FormControl(Severity.MINOR, [ Validators.required ]));
    this.formGroup.addControl('type', new FormControl('', [ Validators.required ]));
    this.formGroup.addControl('targetDate', new FormControl(null, [ DateValidators.atLeastNow() ]));
    this.formGroup.addControl('bpn', new FormControl(null, [ Validators.required, BaseInputHelper.getCustomPatternValidator(bpnRegex, 'bpn') ]));

  }

  protected prepareSubmit(): void {
    this.formGroup.markAllAsTouched();
    this.formGroup.updateValueAndValidity();

    if (this.formGroup.invalid) return;

    this.isLoading$.next(true);
    this.formGroup.disable();
  }

}
