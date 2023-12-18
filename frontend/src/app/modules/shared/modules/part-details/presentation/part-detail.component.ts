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

import { AfterViewInit, Component, Input, OnDestroy } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { Pagination } from '@core/model/pagination.model';
import { TractionBatteryCode } from '@page/parts/model/aspectModels.model';
import { Part, QualityType } from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { SelectOption } from '@shared/components/select/select.component';
import { State } from '@shared/model/state';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { Observable, Subscription } from 'rxjs';
import { filter, tap } from 'rxjs/operators';

@Component({
  selector: 'app-part-detail',
  templateUrl: './part-detail.component.html',
  styleUrls: [ '../../../components/card-list/card-list.component.scss', './part-detail.component.scss' ],
})
export class PartDetailComponent implements AfterViewInit, OnDestroy {
  @Input() showRelation = true;
  @Input() showStartInvestigation = true;

  public readonly shortenPartDetails$: Observable<View<Part>>;
  public readonly selectedPartDetails$: Observable<View<Part>>;
  public readonly manufacturerDetails$: Observable<View<Part>>;
  public readonly customerOrPartSiteDetails$: Observable<View<Part>>;
  public readonly tractionBatteryDetails$: Observable<View<Part>>;
  public readonly tractionBatterySubcomponents$: Observable<View<TractionBatteryCode>>;
  public readonly paginatedTractionBatterySubcomponents: Pagination<TractionBatteryCode>;

  public readonly displayedColumns: string[];

  public customerOrPartSiteDetailsHeader$: Subscription;
  public customerOrPartSiteHeader: string;

  public showQualityTypeDropdown = false;
  public qualityTypeOptions: SelectOption[];

  public qualityTypeControl = new FormControl<QualityType>(null);
  public readonly isOpen$: Observable<boolean>;

  private readonly isOpenState: State<boolean> = new State<boolean>(false);

  constructor(private readonly partDetailsFacade: PartDetailsFacade, private readonly router: Router) {
    this.isOpen$ = this.isOpenState.observable;

    this.selectedPartDetails$ = this.partDetailsFacade.selectedPart$;
    this.shortenPartDetails$ = this.partDetailsFacade.selectedPart$.pipe(
      PartsAssembler.mapPartForView(),
      tap(({ data }) => this.qualityTypeControl.patchValue(data.qualityType, { emitEvent: false, onlySelf: true })),
    );

    this.manufacturerDetails$ = this.partDetailsFacade.selectedPart$.pipe(PartsAssembler.mapPartForManufacturerView());
    this.customerOrPartSiteDetails$ = this.partDetailsFacade.selectedPart$.pipe(PartsAssembler.mapPartForCustomerOrPartSiteView());

    this.tractionBatteryDetails$ = this.partDetailsFacade.selectedPart$.pipe(PartsAssembler.mapPartForTractionBatteryCodeDetailsView());
    this.tractionBatterySubcomponents$ = this.partDetailsFacade.selectedPart$.pipe(PartsAssembler.mapPartForTractionBatteryCodeSubComponentsView()) as unknown as Observable<View<TractionBatteryCode>>;

    this.customerOrPartSiteDetailsHeader$ = this.customerOrPartSiteDetails$?.subscribe(data => {
      if (data?.data?.functionValidFrom) {
        this.customerOrPartSiteHeader = 'partDetail.partSiteInformationData';
      } else {
        this.customerOrPartSiteHeader = 'partDetail.customerData';
      }
    });


    this.qualityTypeOptions = Object.values(QualityType).map(value => ({
      label: value,
      value: value,
    }));
    this.displayedColumns = [ 'position', 'productType', 'tractionBatteryCode' ];
  }

  public ngOnDestroy(): void {
    this.partDetailsFacade.selectedPart = null;
  }

  public ngAfterViewInit(): void {
    this.partDetailsFacade.selectedPart$.pipe(filter(({ data }) => !!data)).subscribe(_ => this.setIsOpen(true));
  }

  public setIsOpen(openState: boolean) {
    this.isOpenState.update(openState);

    if (!openState) {
      this.partDetailsFacade.selectedPart = null;
    }
  }

  public openRelationPage(part: Part): void {
    this.partDetailsFacade.selectedPart = null;
    this.router.navigate([ `parts/relations/${ part.id }` ]).then(_ => window.location.reload());
  }

}
