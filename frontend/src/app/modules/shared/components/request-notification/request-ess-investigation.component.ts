/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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
import { ESS_BASE_ROUTE, getRoute } from '@core/known-route';
import { PaginationBpdm } from '@core/model/pagination.bpdm.model';
import { environment } from '@env';
import { bpnRegex } from '@page/admin/presentation/bpn-configuration/bpn-configuration.component';
import { BpdmFacade } from '@page/ess/core/bpdm.facade';
import { CountryCodes, LegalEntity } from '@page/ess/model/bpdm.model';
import { Ess } from '@page/ess/model/ess.model';
import { Part } from '@page/parts/model/parts.model';
import { BaseInputHelper } from '@shared/abstraction/baseInput/baseInput.helper';
import { RequestContext, RequestNotificationBase } from '@shared/components/request-notification/request-notification.base';
import { SelectOption } from '@shared/components/select/select.component';
import { ToastService } from '@shared/components/toasts/toast.service';
import { View } from '@shared/model/view.model';
import { EssService } from '@shared/service/ess.service';
import { BehaviorSubject, Observable } from 'rxjs';
import { debounceTime } from 'rxjs/operators';

@Component({
  selector: 'app-request-ess-investigation',
  templateUrl: './request-ess-investigation.html',
})
export class RequestEssInvestigationComponent extends RequestNotificationBase {

  @Input() selectedItems: Part[];
  @Input() selectedEssItems: Ess[];
  @Input() showHeadline = true;

  @Output() deselectPart = new EventEmitter<Part>();
  @Output() deselectEss = new EventEmitter<Ess>();
  @Output() restorePart = new EventEmitter<Part>();
  @Output() restoreEss = new EventEmitter<Ess>();
  @Output() clearSelected = new EventEmitter<void>();
  @Output() submitted = new EventEmitter<void>();
  @Output() leavePage = new EventEmitter<boolean>();

  public readonly isLoading$ = new BehaviorSubject(false);
  public readonly context: RequestContext = 'ess';
  public readonly legalEntities$: Observable<View<PaginationBpdm<LegalEntity>>>;

  public readonly isSearching$ = new BehaviorSubject(false);
  public countryCodes: SelectOption[] = Object.values(CountryCodes).map(c=> ({ label: c.name, value: c.name }));
  public BPNSs: SelectOption[] = [];
  public readonly bpdmConnectionAvailable = environment.bpdmConnectionAvailable;

  constructor(toastService: ToastService, public ts:ToastService,
              private readonly essService: EssService,
              private readonly bpdmFacade: BpdmFacade) {
    super(toastService);
  }

  public readonly goBack = () => {
    this.formGroup.enable()
    this.removedItemsHistory = [];

    this.submitted.emit();
    this.leavePage.emit(true);
    this.clearSelected.emit();

    this.formGroup.markAsUntouched();
    this.formGroup.reset();
  };

  public readonly formGroup = new FormGroup({
    company_name: new FormControl(''),
    company_street: new FormControl(''),
    company_no: new FormControl(0),
    company_postal_code: new FormControl(''),
    company_location: new FormControl(''),
    company_country: new FormControl(''),
    bpns: new FormControl('', [ Validators.required, BaseInputHelper.getCustomPatternValidator(bpnRegex, 'bpns') ]),
    bpnss: new FormControl(''),
  });

  public ngOnInit(): void {
    if(!this.bpdmConnectionAvailable) {
      this.formGroup.controls.company_name.disable();
      this.formGroup.controls.company_street.disable();
      this.formGroup.controls.company_no.disable();
      this.formGroup.controls.company_postal_code.disable();
      this.formGroup.controls.company_location.disable();
      this.formGroup.controls.company_country.disable();
    }
    this.formGroup.valueChanges.pipe(debounceTime(2000)).subscribe(value => {
      //TODO: This if should check all input fields for address, not just the company name
      if (value.company_name) {
        this.bpdmFacade.setLegalEntities(value.company_name);
        this.bpdmFacade.legalEntities$.subscribe({
          next: (v) => {
            let legalEnitities = v.data.content;
            this.BPNSs = legalEnitities.map(
              legalEnitity => ({
                label: legalEnitity.legalAddress.bpnSite,
                value: legalEnitity.legalAddress.bpnSite
              }));
            if (legalEnitities.length == 1) {
              this.formGroup.controls.bpns.enable();
              this.formGroup.controls.bpns.setValue(legalEnitities[0].legalAddress.bpnSite,
                {onlySelf: true, emitEvent: false, emitModelToViewChange: true, emitViewToModelChange: true});
              this.formGroup.controls.bpns.disable({onlySelf: true, emitEvent: false});
              this.formGroup.controls.company_name.setValue(legalEnitities[0].legalName, {onlySelf: true, emitEvent: false});
            }
            if (legalEnitities.length > 1) {
              // this.formGroup.controls.bpns.disable();
            }
            this.isSearching$.next(false);
          }
        });
      } else {
        //the address fields are empty, the bpns field needs to be editable
        this.formGroup.controls.bpns.enable({onlySelf: true, emitEvent: false});
      }
      if (value.bpns && !this.formGroup.controls.bpns.disabled) {
        this.formGroup.controls.company_name.disable({onlySelf: true, emitEvent: false});
        this.formGroup.controls.company_street.disable({onlySelf: true, emitEvent: false});
        this.formGroup.controls.company_no.disable({onlySelf: true, emitEvent: false});
        this.formGroup.controls.company_location.disable({onlySelf: true, emitEvent: false});
        this.formGroup.controls.company_postal_code.disable({onlySelf: true, emitEvent: false});
        this.formGroup.controls.company_country.disable({onlySelf: true, emitEvent: false});
      } else {
        this.formGroup.controls.bpns.setErrors(null);
        this.formGroup.controls.company_name.enable({onlySelf: true, emitEvent: false});
        this.formGroup.controls.company_street.enable({onlySelf: true, emitEvent: false});
        this.formGroup.controls.company_no.enable({onlySelf: true, emitEvent: false});
        this.formGroup.controls.company_location.enable({onlySelf: true, emitEvent: false});
        this.formGroup.controls.company_postal_code.enable({onlySelf: true, emitEvent: false});
        this.formGroup.controls.company_country.enable({onlySelf: true, emitEvent: false});
      }
    });
  }

  public submit(): void {
    this.prepareSubmit();
    if (this.formGroup.invalid) return;
    const partIds = this.selectedItems.map(part => 'urn:uuid:' + part.id);
    const { bpns, bpnss } = this.formGroup.value;
    let BPNS = (typeof bpns === "undefined" ? bpnss : bpns);
    const { link, queryParams } = getRoute(ESS_BASE_ROUTE);
    this.essService.postEss(partIds, BPNS).subscribe({
      next: () => this.onSuccessfulSubmit(link, queryParams),
      error: () => this.onUnsuccessfulSubmit(),
    });
  }
}
