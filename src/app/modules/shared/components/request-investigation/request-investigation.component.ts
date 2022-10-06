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

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { getInvestigationInboxRoute } from '@page/investigations/investigations-external-route';
import { Part } from '@page/parts/model/parts.model';
import { CtaSnackbarService } from '@shared/components/call-to-action-snackbar/cta-snackbar.service';
import { InvestigationStatusGroup } from '@shared/model/investigations.model';
import { InvestigationsService } from '@shared/service/investigations.service';
import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'app-request-investigation',
  templateUrl: './request-investigation.component.html',
})
export class RequestInvestigationComponent {
  public isLoading$ = new BehaviorSubject(false);
  public removedItemsHistory: Part[] = [];

  @Input() set isOpen(isOpen: boolean) {
    this.isOpen$.next(isOpen);
    if (isOpen) return;

    this.sidenavIsClosing.emit();
    this.removedItemsHistory = [];
    this.clearForm();
  }

  @Input() selectedItems: Part[];
  @Output() deselectPart = new EventEmitter<Part>();
  @Output() restorePart = new EventEmitter<Part>();
  @Output() clearSelected = new EventEmitter<void>();
  @Output() sidenavIsClosing = new EventEmitter<void>();

  constructor(
    private readonly investigationsService: InvestigationsService,
    private readonly ctaSnackbarService: CtaSnackbarService,
  ) {}

  public readonly isOpen$ = new BehaviorSubject<boolean>(false);

  private readonly textAreaControl = new FormControl(undefined, [
    Validators.required,
    Validators.maxLength(1000),
    Validators.minLength(15),
  ]);
  public readonly investigationFormGroup = new FormGroup({ description: this.textAreaControl });

  public submitInvestigation(): void {
    this.investigationFormGroup.markAllAsTouched();
    this.investigationFormGroup.updateValueAndValidity();

    if (this.investigationFormGroup.invalid) {
      return;
    }

    this.isLoading$.next(true);
    this.textAreaControl.disable();

    const partIds = this.selectedItems.map(part => part.id);
    const amountOfItems = this.selectedItems.length;
    this.investigationsService.postInvestigation(partIds, this.textAreaControl.value).subscribe({
      next: () => {
        this.isLoading$.next(false);
        this.textAreaControl.enable();

        this.isOpen = false;
        this.clearSelected.emit();

        this.openCtaSnackbar(amountOfItems);
      },
      error: () => {
        this.isLoading$.next(false);
        this.textAreaControl.enable();
      },
    });
  }

  private openCtaSnackbar(count: number): void {
    const { link, queryParams } = getInvestigationInboxRoute(InvestigationStatusGroup.QUEUED_AND_REQUESTED);

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

  private clearForm() {
    this.investigationFormGroup.markAsUntouched();
    this.investigationFormGroup.reset();
  }
}
