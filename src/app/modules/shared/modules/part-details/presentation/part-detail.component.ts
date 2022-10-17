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

import { AfterViewInit, Component, Input, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { realm } from '@core/api/api.service.properties';
import { Part, QualityType } from '@page/parts/model/parts.model';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { SelectOption } from '@shared/components/select/select.component';
import { State } from '@shared/model/state';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { Observable } from 'rxjs';
import { filter, tap } from 'rxjs/operators';

@Component({
  selector: 'app-part-detail',
  templateUrl: './part-detail.component.html',
  styleUrls: ['./part-detail.component.scss'],
})
export class PartDetailComponent implements AfterViewInit, OnDestroy {
  @Input() showRelation = true;

  public readonly partDetails$: Observable<View<Part>>;
  public readonly relationPartDetails$: Observable<View<Part>>;
  public readonly manufacturerDetails$: Observable<View<Part>>;
  public readonly customerDetails$: Observable<View<Part>>;

  public showQualityTypeDropdown = false;
  public qualityTypeOptions: SelectOption[];

  public readonly isOpen$: Observable<boolean>;
  public selectedValue: QualityType;

  private readonly isOpenState: State<boolean> = new State<boolean>(false);

  constructor(private readonly partDetailsFacade: PartDetailsFacade, private readonly router: Router) {
    this.isOpen$ = this.isOpenState.observable;
    this.relationPartDetails$ = this.partDetailsFacade.selectedPart$;
    this.partDetails$ = this.partDetailsFacade.selectedPart$.pipe(
      PartsAssembler.mapPartForView(),
      tap(_ => (this.selectedValue = null)),
    );

    this.manufacturerDetails$ = this.partDetailsFacade.selectedPart$.pipe(PartsAssembler.mapPartForManufacturerView());
    this.customerDetails$ = this.partDetailsFacade.selectedPart$.pipe(PartsAssembler.mapPartForCustomerView());

    this.qualityTypeOptions = Object.values(QualityType).map(value => ({
      lable: value,
      value: value,
    }));
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
    this.router.navigate([`${realm}/parts/relations/${part.id}`]).then(_ => window.location.reload());
  }

  public updateQualityType(newQualityType: string): void {
    this.selectedValue = newQualityType as QualityType;
    this.partDetailsFacade.updateQualityType(newQualityType as QualityType).subscribe();
  }
}
