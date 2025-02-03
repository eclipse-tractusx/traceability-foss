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

import { AfterViewInit, Component, Input, OnDestroy, inject } from '@angular/core';
import { FormControl } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { ActivatedRoute, Router } from '@angular/router';
import { Role } from '@core/user/role.model';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { Part, QualityType } from '@page/parts/model/parts.model';
import { RelationComponent } from '@page/parts/presentation/relation/relation.component';
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
  styleUrls: ['../../../components/card-list/card-list.component.scss', './part-detail.component.scss'],
})
export class PartDetailComponent implements AfterViewInit, OnDestroy {
  @Input() showRelation = true;
  @Input() showStartInvestigation = true;
  @Input() mainAspectType: MainAspectType;

  public selectedTab = 0;
  public shortenPartDetails$: Observable<View<Part>>;
  public selectedPartDetails$: Observable<View<Part>>;
  public manufacturerDetails$: Observable<View<Part>>;
  public manufacturerDetailsHeader$: Subscription;

  public customerOrPartSiteDetails$: Observable<View<Part>>;
  public customerOrPartSiteDetailsHeader$: Subscription;

  public customerOrPartSiteHeader: string;
  public manufacturerNameHeader: string;

  public showQualityTypeDropdown = false;
  public qualityTypeOptions: SelectOption[];

  public qualityTypeControl = new FormControl<QualityType>(null);
  public context: string;

  private readonly isOpenState: State<boolean> = new State<boolean>(false);
  private activatedRoute = inject(ActivatedRoute);
  private readyPromise = Promise.resolve();

  protected readonly MainAspectType = MainAspectType;
  protected readonly Role = Role;

  constructor(private readonly partDetailsFacade: PartDetailsFacade, private readonly router: Router,
    public dialog: MatDialog,
  ) {

    this.context = this.activatedRoute?.parent?.toString().split('\'')[1];

    if (!this.partDetailsFacade.selectedPart) {
      const partId = this.activatedRoute.snapshot.params['partId'];
      const mainAspectType = this.activatedRoute.snapshot.queryParams?.type as MainAspectType;
      this.readyPromise = new Promise((resolve) => {
        this.partDetailsFacade.mainAspectType = mainAspectType;
        this.partDetailsFacade.setPartFromTree(partId).subscribe(() => {
          resolve();
        });
      });
    }

    this.readyPromise.then(() => {

      this.selectedPartDetails$ = this.partDetailsFacade.selectedPart$;

      this.shortenPartDetails$ = this.partDetailsFacade.selectedPart$.pipe(
        PartsAssembler.mapPartForView(),
        tap(({ data }) => {
          if (data) {
            this.qualityTypeControl.patchValue(data.qualityType, { emitEvent: false, onlySelf: true });
          }
        }),
      );

      this.manufacturerDetails$ = this.partDetailsFacade.selectedPart$.pipe(PartsAssembler.mapPartForManufacturerView());
      this.manufacturerDetailsHeader$ = this.manufacturerDetails$?.subscribe(data => {
        this.manufacturerNameHeader = data?.data?.nameAtManufacturer;
      });

      this.customerOrPartSiteDetails$ = this.partDetailsFacade.selectedPart$.pipe(PartsAssembler.mapPartForCustomerOrPartSiteView());

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
    });
  }

  public ngOnDestroy(): void {
    this.partDetailsFacade.selectedPart = null;
  }

  public ngAfterViewInit(): void {
    this.partDetailsFacade.selectedPart$.pipe(filter(({ data }) => !!data));
  }

  public openRelationPage(part: Part): void {
    this.dialog.open(RelationComponent, {
      autoFocus: false,
      width: '100%',
      maxWidth: '85vw',
      height: '80vh',
      data: {
        partId: part.id,
        context: this.activatedRoute?.parent?.toString().split('\'')[1],
        partName: part.nameAtManufacturer,
      }
    });
  }

  public onTabChange({ index }: MatTabChangeEvent): void {
    this.selectedTab = index;
  }

  public navigateBackToParts(): void {
    this.partDetailsFacade.selectedPart = null;
    this.router.navigate([`/${this.context}`]);
  }
}
