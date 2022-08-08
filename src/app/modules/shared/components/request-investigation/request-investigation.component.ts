/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { getInvestigationInboxRoute } from '@page/investigations/investigations-external-route';
import { Part } from '@page/parts/model/parts.model';
import { CtaNotificationService } from '@shared/components/call-to-action-notifications/cta-notification.service';
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
  }

  @Input() selectedItems: Part[];
  @Output() deselectPart = new EventEmitter<Part>();
  @Output() restorePart = new EventEmitter<Part>();
  @Output() clearSelected = new EventEmitter<void>();
  @Output() sidenavIsClosing = new EventEmitter<void>();

  constructor(
    private readonly investigationsService: InvestigationsService,
    private readonly ctaNotificationService: CtaNotificationService,
  ) {}

  public isOpen$ = new BehaviorSubject<boolean>(false);

  private textAreaControl = new FormControl(undefined, [
    Validators.required,
    Validators.maxLength(1000),
    Validators.minLength(15),
  ]);
  public investigationFormGroup = new FormGroup({ description: this.textAreaControl });

  public submitInvestigation(): void {
    this.investigationFormGroup.markAllAsTouched();
    this.investigationFormGroup.updateValueAndValidity();

    if (this.investigationFormGroup.invalid) {
      return;
    }
    this.isLoading$.next(true);
    this.textAreaControl.disable();

    const count = this.selectedItems.length;
    this.investigationsService.postInvestigation(this.selectedItems, this.textAreaControl.value).subscribe({
      next: () => {
        this.isLoading$.next(false);
        this.textAreaControl.enable();

        this.isOpen = false;
        this.removedItemsHistory = [];
        this.clearSelected.emit();

        this.textAreaControl.setValue(undefined);
        this.textAreaControl.markAsUntouched();

        const investigationsRoute = getInvestigationInboxRoute(InvestigationStatusGroup.QUEUED_AND_REQUESTED);
        this.ctaNotificationService.show(
          {
            id: 'qualityInvestigation.success',
            values: { count },
          },
          [
            {
              text: 'qualityInvestigation.goToQueue',
              link: investigationsRoute.link,
              linkQueryParams: investigationsRoute.queryParams,
            },
          ],
        );
      },
    });
  }

  public cancelAction(part: Part) {
    this.removedItemsHistory.unshift(part);
    this.deselectPart.emit(part);
  }

  public restoreLastItem(): void {
    this.restorePart.emit(this.removedItemsHistory[0]);
    this.removedItemsHistory.shift();
  }
}
