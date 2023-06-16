/********************************************************************************
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


import { Component, OnDestroy, OnInit } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { OtherPartsFacade } from '@page/other-parts/core/other-parts.facade';
import { Part, SemanticDataModel } from '@page/parts/model/parts.model';
import { CreateHeaderFromColumns, TableConfig, TableEventConfig } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { StaticIdService } from '@shared/service/staticId.service';
import { BehaviorSubject, Observable, Subject } from 'rxjs';

@Component({
  selector: 'app-supplier-parts',
  templateUrl: './supplier-parts.component.html'
})
export class SupplierPartsComponent implements OnInit, OnDestroy {
  public readonly displayedColumns: string[] = [
    'select',
    'semanticDataModel',
    'name',
    'manufacturer',
    'partNumber',
    'serialNumber',
    'batchNumber',
    'productionDate',
  ];

  public readonly sortableColumns: Record<string, boolean> = {
    name: true,
    manufacturer: true,
    partNumber: true,
    serialNumber: true,
    batchNumber: true,
    productionDate: true,
    semanticDataModel: true,
  };

  public readonly tableConfig: TableConfig = {
    displayedColumns: this.displayedColumns,
    header: CreateHeaderFromColumns(this.displayedColumns, 'table.column'),
    sortableColumns: this.sortableColumns,
  };

  public readonly supplierParts$: Observable<View<Pagination<Part>>>;

  public readonly deselectPartTrigger$ = new Subject<Part[]>();
  public readonly addPartTrigger$ = new Subject<Part>();

  public readonly isInvestigationOpen$ = new BehaviorSubject<boolean>(false);
  public selectedItems: Array<Part> = [];

  public readonly supplierTabLabelId = this.staticIdService.generateId('OtherParts.supplierTabLabel');

  constructor(
    private readonly otherPartsFacade: OtherPartsFacade,
    private readonly partDetailsFacade: PartDetailsFacade,
    private readonly staticIdService: StaticIdService,
  ) {
    this.supplierParts$ = this.otherPartsFacade.supplierParts$;
  }

  public get currentSelectedItems(): Part[] {

    this.selectedItems = this.selectedItems.map(part => {
      return {...part, semanticDataModel: SemanticDataModel[part.semanticDataModel.toUpperCase() as keyof typeof SemanticDataModel]}
    })
    return this.selectedItems || [];
  }

  public set currentSelectedItems(parts: Part[]) {
    this.selectedItems = parts;
  }

  public ngOnInit(): void {
    this.otherPartsFacade.setSupplierParts();
  }

  public ngOnDestroy(): void {
    this.otherPartsFacade.unsubscribeParts();
  }

  public onSelectItem(event: Record<string, unknown>): void {
    this.partDetailsFacade.selectedPart = event as unknown as Part;
  }

  public onTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
      this.otherPartsFacade.setSupplierParts(page, pageSize, sorting);
  }

  public onMultiSelect(event: unknown[]): void {
    this.currentSelectedItems = event as Part[];
  }

  public removeItemFromSelection(part: Part): void {
    this.deselectPartTrigger$.next([part]);
    this.currentSelectedItems = this.currentSelectedItems.filter(({ id }) => id !== part.id);
  }

  public clearSelected(): void {
    this.deselectPartTrigger$.next(this.currentSelectedItems);
    this.currentSelectedItems = [];
  }

  public addItemToSelection(part: Part): void {
    this.addPartTrigger$.next(part);
    this.currentSelectedItems = [...this.currentSelectedItems, part];
  }

  public submit(): void {
    this.otherPartsFacade.setActiveInvestigationForParts(this.currentSelectedItems);
    this.isInvestigationOpen$.next(false);
  }
}
