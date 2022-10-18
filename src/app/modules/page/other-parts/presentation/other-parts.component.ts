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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { Pagination } from '@core/model/pagination.model';
import { OtherPartsFacade } from '@page/other-parts/core/other-parts.facade';
import { Part } from '@page/parts/model/parts.model';
import { CreateHeaderFromColumns, TableConfig, TableEventConfig } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { StaticIdService } from '@shared/service/staticId.service';
import { BehaviorSubject, Observable, Subject } from 'rxjs';

@Component({
  selector: 'app-other-parts',
  templateUrl: './other-parts.component.html',
  styleUrls: ['./other-parts.component.scss'],
})
export class OtherPartsComponent implements OnInit, OnDestroy {
  public readonly displayedColumns: string[] = [
    'select',
    'name',
    'manufacturer',
    'partNumber',
    'serialNumber',
    'productionDate',
  ];

  public readonly sortableColumns: Record<string, boolean> = {
    name: true,
    manufacturer: true,
    partNumber: true,
    serialNumber: true,
    productionDate: true,
  };

  public readonly tableConfig: TableConfig = {
    displayedColumns: this.displayedColumns,
    header: CreateHeaderFromColumns(this.displayedColumns, 'table.partsColumn'),
    sortableColumns: this.sortableColumns,
  };

  public readonly customerParts$: Observable<View<Pagination<Part>>>;
  public readonly supplierParts$: Observable<View<Pagination<Part>>>;

  public readonly deselectPartTrigger$ = new Subject<Part[]>();
  public readonly addPartTrigger$ = new Subject<Part>();

  public readonly isInvestigationOpen$ = new BehaviorSubject<boolean>(false);
  public readonly selectedItems: Array<Array<Part>> = [];

  public selectedTab = 0;

  public readonly supplierTabLabelId = this.staticIdService.generateId('OtherParts.supplierTabLabel');
  public readonly customerTabLabelId = this.staticIdService.generateId('OtherParts.customerTabLabel');

  constructor(
    private readonly otherPartsFacade: OtherPartsFacade,
    private readonly partDetailsFacade: PartDetailsFacade,
    private readonly staticIdService: StaticIdService,
  ) {
    this.customerParts$ = this.otherPartsFacade.customerParts$;
    this.supplierParts$ = this.otherPartsFacade.supplierParts$;
  }

  public get currentSelectedItems(): Part[] {
    return this.selectedItems[this.selectedTab] || [];
  }

  public set currentSelectedItems(parts: Part[]) {
    this.selectedItems[this.selectedTab] = parts;
  }

  public ngOnInit(): void {
    this.otherPartsFacade.setCustomerParts();
    this.otherPartsFacade.setSupplierParts();
  }

  public ngOnDestroy(): void {
    this.otherPartsFacade.unsubscribeParts();
  }

  public onSelectItem(event: Record<string, unknown>): void {
    this.partDetailsFacade.selectedPart = event as unknown as Part;
  }

  public onTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    if (this.selectedTab === 0) {
      this.otherPartsFacade.setSupplierParts(page, pageSize, sorting);
    } else {
      this.otherPartsFacade.setCustomerParts(page, pageSize, sorting);
    }
  }

  public onMultiSelect(event: unknown[]): void {
    this.currentSelectedItems = event as Part[];
  }

  public removeItemFromSelection(part: Part): void {
    this.deselectPartTrigger$.next([part]);
    this.currentSelectedItems = this.currentSelectedItems.filter(({ id }) => id !== part.id);
  }

  public onTabChange({ index }: MatTabChangeEvent): void {
    this.selectedTab = index;
    this.partDetailsFacade.selectedPart = null;
  }

  public clearSelected(): void {
    this.deselectPartTrigger$.next(this.currentSelectedItems);
    this.currentSelectedItems = [];
  }

  public addItemToSelection(part: Part): void {
    this.addPartTrigger$.next(part);
    this.currentSelectedItems = [...this.currentSelectedItems, part];
  }
}
