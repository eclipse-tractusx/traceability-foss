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
import { Part } from '@page/parts/model/parts.model';
import {
  CreateHeaderFromColumns,
  TableConfig,
  TableEventConfig,
  TableHeaderSort,
} from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { StaticIdService } from '@shared/service/staticId.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-customer-parts',
  templateUrl: './customer-parts.component.html'
})
export class CustomerPartsComponent implements OnInit, OnDestroy {
  public readonly displayedColumns: string[] = [
    'semanticDataModel',
    'name',
    'manufacturer',
    'partId',
    'semanticModelId',
    'manufacturingDate',
  ];

  public readonly sortableColumns: Record<string, boolean> = {
    semanticDataModel: true,
    name: true,
    manufacturer: true,
    partId: true,
    semanticModelId: true,
    manufacturingDate: true,
  };

  public readonly tableConfig: TableConfig = {
    displayedColumns: this.displayedColumns,
    header: CreateHeaderFromColumns(this.displayedColumns, 'table.column'),
    sortableColumns: this.sortableColumns,
  };

  public readonly customerParts$: Observable<View<Pagination<Part>>>;

  public readonly customerTabLabelId = this.staticIdService.generateId('OtherParts.customerTabLabel');

  public tableCustomerSortList: TableHeaderSort[];

  private ctrlKeyState = false;
  constructor(
    private readonly otherPartsFacade: OtherPartsFacade,
    private readonly partDetailsFacade: PartDetailsFacade,
    private readonly staticIdService: StaticIdService,
  ) {
    this.customerParts$ = this.otherPartsFacade.customerParts$;
    this.tableCustomerSortList = [];

    window.addEventListener('keydown', (event) => {
      this.ctrlKeyState = event.ctrlKey;
    });
    window.addEventListener('keyup', (event) => {
      this.ctrlKeyState = event.ctrlKey;
    });
  }

  public ngOnInit(): void {
    this.otherPartsFacade.setCustomerParts();
  }

  public ngOnDestroy(): void {
    this.otherPartsFacade.unsubscribeParts();
  }

  public onSelectItem(event: Record<string, unknown>): void {
    this.partDetailsFacade.selectedPart = event as unknown as Part;
  }

  public onTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
      this.setTableSortingList(sorting);
      this.otherPartsFacade.setCustomerParts(page, pageSize, this.tableCustomerSortList);
  }


  private setTableSortingList(sorting: TableHeaderSort): void {
    if(!sorting && this.tableCustomerSortList) {
      this.tableCustomerSortList = [];
      return;
    }
    if(this.ctrlKeyState) {
      const [columnName, direction] = sorting;
      const tableSortList = this.tableCustomerSortList;

      // Find the index of the existing entry with the same first item
      const index = tableSortList.findIndex(
          ([itemColumnName, direction]) => itemColumnName === columnName
      );

      if (index !== -1) {
        // Replace the existing entry
        tableSortList[index] = sorting;
      } else {
        // Add the new entry if it doesn't exist
        tableSortList.push(sorting);
      }
      this.tableCustomerSortList = tableSortList;
    } else {
      this.tableCustomerSortList = [sorting];
    }
  }
}
