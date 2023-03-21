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
import { UntypedFormControl, UntypedFormGroup, Validators } from '@angular/forms';
import { getRoute, INVESTIGATION_BASE_ROUTE } from '@core/known-route';
import { OtherPartsFacade } from '@page/other-parts/core/other-parts.facade';
import { Part } from '@page/parts/model/parts.model';
import { CtaSnackbarService } from '@shared/components/call-to-action-snackbar/cta-snackbar.service';
import { SelectOption } from '@shared/components/select/select.component';
import { DateValidators } from '@shared/components/dateTime/dateValidators.model';
import { NotificationStatusGroup } from '@shared/model/notification.model';
import { InvestigationsService } from '@shared/service/investigations.service';
import { BehaviorSubject } from 'rxjs';
import { Severity } from '@shared/model/severity.model';

@Component({
  selector: 'app-request-investigation',
  templateUrl: './request-investigation.component.html',
})
export class RequestInvestigationComponent {
  public isLoading$ = new BehaviorSubject(false);
  public removedItemsHistory: Part[] = [];
  public minDate = new Date();

  @Input() selectedItems: Part[];
  @Input() showHeadline = true;

  @Output() deselectPart = new EventEmitter<Part>();
  @Output() restorePart = new EventEmitter<Part>();
  @Output() clearSelected = new EventEmitter<void>();
  @Output() submitted = new EventEmitter<void>();

  public selectedSeverity: Severity = Severity.MINOR;

  constructor(
    private readonly investigationsService: InvestigationsService,
    private readonly otherPartsFacade: OtherPartsFacade,
    private readonly ctaSnackbarService: CtaSnackbarService,
  ) {}

  private readonly textAreaControl = new UntypedFormControl(undefined, [
    Validators.required,
    Validators.maxLength(1000),
    Validators.minLength(15),
  ]);

  private readonly targetDateControl = new UntypedFormControl(undefined, [DateValidators.atLeastNow()]);

  public readonly investigationFormGroup = new UntypedFormGroup({
    description: this.textAreaControl,
    targetDate: this.targetDateControl,
  });

  public submitInvestigation(): void {
    this.investigationFormGroup.markAllAsTouched();
    this.investigationFormGroup.updateValueAndValidity();
    this.targetDateControl.updateValueAndValidity();

    if (this.investigationFormGroup.invalid) {
      return;
    }

    this.isLoading$.next(true);
    this.textAreaControl.disable();

    const partIds = this.selectedItems.map(part => part.id);
    const amountOfItems = this.selectedItems.length;

    const description = this.textAreaControl.value;
    const targetDate = this.targetDateControl.value;
    this.investigationsService.postInvestigation(partIds, description, this.selectedSeverity, targetDate).subscribe({
      next: () => {
        this.isLoading$.next(false);
        this.resetForm();

        this.openCtaSnackbar(amountOfItems);
        // Improve this call. Only call when needed.
        this.otherPartsFacade.setActiveInvestigationForParts(this.selectedItems);
      },
      error: () => {
        this.isLoading$.next(false);
        this.textAreaControl.enable();
      },
    });
  }

  private openCtaSnackbar(count: number): void {
    const { link, queryParams } = getRoute(INVESTIGATION_BASE_ROUTE, NotificationStatusGroup.QUEUED_AND_REQUESTED);

    this.ctaSnackbarService.show(
      {
        id: 'qualityInvestigation.success',
        values: { count },
      },
      [
        {
          text: 'qualityInvestigation.goToQueue',
          linkQueryParams: queryParams,
          link,
        },
      ],
    );
  }

  public cancelAction(part: Part): void {
    this.removedItemsHistory.unshift(part);
    this.deselectPart.emit(part);
  }

  public restoreLastItem(): void {
    this.restorePart.emit(this.removedItemsHistory[0]);
    this.removedItemsHistory.shift();
  }

  public resetForm(): void {
    this.textAreaControl.enable();
    this.removedItemsHistory = [];

    this.clearSelected.emit();
    this.submitted.emit();

    this.investigationFormGroup.markAsUntouched();
    this.investigationFormGroup.reset();

    this.textAreaControl.markAsUntouched();
    this.textAreaControl.reset();
  }

  public onSeveritySelected(selectedSeverity: Severity) {
    this.selectedSeverity = selectedSeverity;
  }
}
