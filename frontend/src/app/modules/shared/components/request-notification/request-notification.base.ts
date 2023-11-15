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

import { EventEmitter } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Part } from '@page/parts/model/parts.model';
import { DateTimeString } from '@shared/components/dateTime/dateTime.component';
import { ToastService } from '@shared/components/toasts/toast.service';
import { Severity } from '@shared/model/severity.model';
import { BehaviorSubject } from 'rxjs';
import { ModalComponent } from '@shared/modules/modal/component/modal.component';

export type RequestContext = 'requestInvestigations' | 'requestAlert';

export abstract class RequestNotificationBase {
  public abstract readonly selectedItems: Part[];
  public abstract readonly showHeadline: boolean;

  public abstract readonly deselectPart: EventEmitter<Part>;
  public abstract readonly restorePart: EventEmitter<Part>;
  public abstract readonly clearSelected: EventEmitter<void>;
  public abstract readonly submitted: EventEmitter<void>;

  public abstract readonly context: RequestContext;
  public abstract readonly formGroup:
    | FormGroup<{
      description: FormControl<string>;
      severity: FormControl<Severity>;
      targetDate: FormControl<DateTimeString>;
    }>
    | FormGroup<{
      description: FormControl<string>;
      severity: FormControl<Severity>;
      bpn: FormControl<string>;
    }>;

  public readonly isLoading$ = new BehaviorSubject(false);
  public readonly minDate = new Date();

  public removedItemsHistory: Part[] = [];

  protected constructor(private readonly toastService: ToastService,
    public dialog: MatDialog,
  ) { }

  protected abstract submit(): void;

  protected prepareSubmit(): void {
    this.formGroup.markAllAsTouched();
    this.formGroup.updateValueAndValidity();

    if (this.formGroup.invalid) return;

    this.isLoading$.next(true);
    this.formGroup.disable();
  }

  protected onSuccessfulSubmit(link: string, linkQueryParams: Record<string, string>): void {
    this.isLoading$.next(false);
    const amountOfItems = this.selectedItems.length;
    this.resetForm();

    this.openToast(amountOfItems, link, linkQueryParams);
  }

  protected onUnsuccessfulSubmit(): void {
    this.isLoading$.next(false);
    this.formGroup.enable();
  }

  protected openToast(count: number, link: string, linkQueryParams: Record<string, string>): void {

    this.toastService.success({
      id: `${this.context}.success`,
      values: { count }
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

  public cancelAction(part: Part): void {
    this.dialog.open(ModalComponent, {
      autoFocus: false,
      data: {
        title: 'parts.confirmationDialog.deletePartTitle',
        message: 'parts.confirmationDialog.deletePartMessage',
        buttonLeft: 'confirmationDialog.cancel',
        buttonRight: 'parts.confirmationDialog.deletePartButton',
        primaryButtonColour: 'primary',
        onConfirm: (isConfirmed: boolean) => {
          this.deselectPart.emit(part);
          this.selectedItems.splice(this.selectedItems.indexOf(part), 1);
        }
      }
    });
  }

  public restoreLastItem(): void {
    this.restorePart.emit(this.removedItemsHistory[0]);
    this.removedItemsHistory.shift();
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
