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

import { Component, OnInit } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { Pagination } from '@core/model/pagination.model';
import { OtherPartsFacade } from '@page/otherParts/core/otherParts.facade';
import { Part } from '@page/parts/model/parts.model';
import { TableConfig, TableEventConfig } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-parts',
  templateUrl: './otherParts.component.html',
  styleUrls: ['./otherParts.component.scss'],
})
export class OtherPartsComponent implements OnInit {
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

  public tableConfig: TableConfig;
  public selectedItems: Array<Array<Part>> = [];
  public selectedTabIndex = 0;

  public customerParts$: Observable<View<Pagination<Part>>>;
  public supplierParts$: Observable<View<Pagination<Part>>>;

  constructor(
    private readonly otherPartsFacade: OtherPartsFacade,
    private readonly partDetailsFacade: PartDetailsFacade,
  ) {
    this.customerParts$ = this.otherPartsFacade.customerParts$;
    this.supplierParts$ = this.otherPartsFacade.supplierParts$;

    this.tableConfig = {
      displayedColumns: this.displayedColumns,
      header: this.displayedColumns.map(column => `pageParts.column.${column}`),
      sortableColumns: this.sortableColumns,
    };
  }

  ngOnInit(): void {
    this.otherPartsFacade.setCustomerParts();
    this.otherPartsFacade.setSupplierParts();
  }

  public onSelectItem(event: Record<string, unknown>): void {
    this.partDetailsFacade.selectedPart = event as unknown as Part;
  }

  public onTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    if (this.selectedTabIndex === 0) {
      this.otherPartsFacade.setSupplierParts(page, pageSize, sorting);
    } else {
      this.otherPartsFacade.setCustomerParts(page, pageSize, sorting);
    }
  }

  public onMultiSelect(event: unknown[]): void {
    this.selectedItems[this.selectedTabIndex] = event as Part[];
  }

  public onTabChange({ index }: MatTabChangeEvent): void {
    this.selectedTabIndex = index;
    this.partDetailsFacade.selectedPart = null;
  }
}
