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
import { ALERT_BASE_ROUTE, getRoute, INVESTIGATION_BASE_ROUTE } from '@core/known-route';
import { bpnRegex } from '@page/admin/presentation/bpn-configuration/bpn-configuration.component';
import { Part, SemanticDataModel } from '@page/parts/model/parts.model';
import { BaseInputHelper } from '@shared/abstraction/baseInput/baseInput.helper';
import { DateValidators } from '@shared/components/dateTime/dateValidators.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import { NotificationStatusGroup, NotificationType } from '@shared/model/notification.model';
import { Severity } from '@shared/model/severity.model';
import { NotificationService } from '@shared/service/notification.service';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-notification-request',
  templateUrl: './request-notification.component.html',
})
export class RequestNotificationComponent {
  @Input() selectedItems: Part[];
  @Input() showHeadline = true;

  context: string;
  isInvestigation: boolean;
  formGroup = new FormGroup<any>({});

  @Input() set notificationType(notificationType: NotificationType) {
    this.context = notificationType === NotificationType.INVESTIGATION ? 'requestInvestigations' : 'requestAlert';
    this.isInvestigation = this.notificationType === NotificationType.INVESTIGATION;
    this.formGroup.addControl('description', new FormControl('', [ Validators.required, Validators.maxLength(1000), Validators.minLength(15) ]));
    this.formGroup.addControl('severity', new FormControl(Severity.MINOR, [ Validators.required ]));
    if (this.isInvestigation) {
      this.formGroup.addControl('targetDate', new FormControl(null, [ DateValidators.atLeastNow() ]));
    } else {
      this.formGroup.addControl('bpn', new FormControl(null, [ Validators.required, BaseInputHelper.getCustomPatternValidator(bpnRegex, 'bpn') ]));
    }
  }


  @Output() deselectPart = new EventEmitter<Part>();
  @Output() restorePart = new EventEmitter<Part>();
  @Output() clearSelected = new EventEmitter<void>();
  @Output() submitted = new EventEmitter<void>();

  public readonly isLoading$ = new BehaviorSubject(false);
  public readonly minDate = new Date();

  public removedItemsHistory: Part[] = [];

  constructor(private readonly toastService: ToastService, private readonly notificationService: NotificationService) {
  }

  protected prepareSubmit(): void {
    this.formGroup.markAllAsTouched();
    this.formGroup.updateValueAndValidity();

    if (this.formGroup.invalid) return;

    this.isLoading$.next(true);
    this.formGroup.disable();
  }

  protected onUnsuccessfulSubmit(): void {
    this.isLoading$.next(false);
    this.formGroup.enable();
  }

  public submit(): void {
    this.prepareSubmit();
    if (this.formGroup.invalid) return;
    const partIds = this.selectedItems.map(part => part.id);
    if (this.isInvestigation) {
      const { description, severity, targetDate } = this.formGroup.value;
      const { link, queryParams } = getRoute(INVESTIGATION_BASE_ROUTE, NotificationStatusGroup.QUEUED_AND_REQUESTED);
      this.notificationService.createInvestigation(partIds, description, severity, targetDate).subscribe({
        next: () => this.onSuccessfulSubmit(link, queryParams),
        error: () => this.onUnsuccessfulSubmit(),
      });
    } else {

      // TODO this is not correct behaviour BUG!
      // set asBuilt parameter if one of the selectedItems are a asPlanned Part
      const isAsBuilt = this.selectedItems.map(part => part.semanticDataModel === SemanticDataModel.PARTASPLANNED).includes(true);

      const { description, bpn, severity } = this.formGroup.value;
      const { link, queryParams } = getRoute(ALERT_BASE_ROUTE, NotificationStatusGroup.QUEUED_AND_REQUESTED);

      this.notificationService.createAlert(partIds, description, severity, bpn, isAsBuilt).subscribe({
        next: () => this.onSuccessfulSubmit(link, queryParams),
        error: () => this.onUnsuccessfulSubmit(),
      });
    }
  }

  protected onSuccessfulSubmit(link: string, linkQueryParams: Record<string, string>): void {
    this.isLoading$.next(false);
    const amountOfItems = this.selectedItems.length;
    this.resetForm();

    this.openToast(amountOfItems, link, linkQueryParams);
  }


  protected openToast(count: number, link: string, linkQueryParams: Record<string, string>): void {

    this.toastService.success({
        id: `${ this.context }.success`,
        values: { count },
      },
      5000,
      [
        {
          text: 'actions.goToQueue',
          linkQueryParams,
          link,
        },
      ],
    );
  }

  public resetForm(): void {
    this.formGroup.enable();
    this.removedItemsHistory = [];

    this.submitted.emit();
    this.clearSelected.emit();

    this.formGroup.markAsUntouched();
    this.formGroup.reset();

    this.formGroup.markAsUntouched();
    this.formGroup.reset();
  }
}
