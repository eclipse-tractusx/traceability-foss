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


import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { OtherPartsFacade } from '@page/other-parts/core/other-parts.facade';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
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
  public readonly displayedColumnsAsBuilt: string[] = [
    'semanticDataModel',
    'name',
    'manufacturer',
    'partId',
    'semanticModelId',
    'manufacturingDate',
  ];

  public readonly sortableColumnsAsBuilt: Record<string, boolean> = {
    semanticDataModel: true,
    name: true,
    manufacturer: true,
    partId: true,
    semanticModelId: true,
  };

  public readonly displayedColumnsAsPlanned: string[] = [
    'semanticDataModel',
    'name',
    'manufacturer',
    'manufacturerPartId',
    'semanticModelId',
  ];

  public readonly sortableColumnsAsPlanned: Record<string, boolean> = {
    semanticDataModel: true,
    name: true,
    manufacturer: true,
    manufacturerPartId: true,
    semanticModelId: true,
    manufacturingDate: true,
  };

  public tableConfigAsBuilt: TableConfig;
  public tableConfigAsPlanned: TableConfig;


  public customerPartsAsBuilt$: Observable<View<Pagination<Part>>>;
  public customerPartsAsPlanned$: Observable<View<Pagination<Part>>>;

  public readonly customerTabLabelId = this.staticIdService.generateId('OtherParts.customerTabLabel');

  public tableCustomerAsBuiltSortList: TableHeaderSort[];
  public tableCustomerAsPlannedSortList: TableHeaderSort[];

  private ctrlKeyState = false;

  @Input()
  public bomLifecycle: 'asBuilt' | 'asPlanned';
  constructor(
    private readonly otherPartsFacade: OtherPartsFacade,
    private readonly partDetailsFacade: PartDetailsFacade,
    private readonly staticIdService: StaticIdService,
  ) {


    window.addEventListener('keydown', (event) => {
      this.ctrlKeyState = event.ctrlKey;
    });
    window.addEventListener('keyup', (event) => {
      this.ctrlKeyState = event.ctrlKey;
    });
  }

  public ngOnInit(): void {
    if(this.bomLifecycle === 'asBuilt') {
      this.customerPartsAsBuilt$ = this.otherPartsFacade.customerPartsAsBuilt$;
      this.tableCustomerAsBuiltSortList = [];
      this.otherPartsFacade.setCustomerPartsAsBuilt();
    } else {
      this.customerPartsAsPlanned$ = this.otherPartsFacade.customerPartsAsPlanned$;
      this.tableCustomerAsPlannedSortList = [];
      this.otherPartsFacade.setCustomerPartsAsPlanned();
    }
  }

  public ngAfterViewInit(): void {
    if(this.bomLifecycle === "asBuilt") {
      this.tableConfigAsBuilt = {
        displayedColumns: this.displayedColumnsAsBuilt,
        header: CreateHeaderFromColumns(this.displayedColumnsAsBuilt, 'table.column'),
        sortableColumns: this.sortableColumnsAsBuilt,
      }
      } else {
      this.tableConfigAsPlanned = {
        displayedColumns: this.displayedColumnsAsPlanned,
        header: CreateHeaderFromColumns(this.displayedColumnsAsPlanned, 'table.column'),
        sortableColumns: this.sortableColumnsAsPlanned,
      }
    }

  }

  public ngOnDestroy(): void {
    this.otherPartsFacade.unsubscribeParts();
  }

  public onSelectItem(event: Record<string, unknown>): void {
    this.partDetailsFacade.selectedPart = event as unknown as Part;
  }

  public onAsBuiltTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
      this.setTableSortingList(sorting, MainAspectType.AS_BUILT);
      this.otherPartsFacade.setCustomerPartsAsBuilt(page, pageSize, this.tableCustomerAsBuiltSortList);
  }

  public onAsPlannedTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    this.setTableSortingList(sorting, MainAspectType.AS_PLANNED);
    this.otherPartsFacade.setCustomerPartsAsPlanned(page, pageSize, this.tableCustomerAsPlannedSortList);
  }


  private setTableSortingList(sorting: TableHeaderSort, partTable: MainAspectType): void {
    if(!sorting && (this.tableCustomerAsBuiltSortList || this.tableCustomerAsPlannedSortList)) {
      this.resetTableSortingList(partTable);
      return;
    }

    if(this.ctrlKeyState) {
      const [columnName] = sorting;
      const tableSortList = partTable === MainAspectType.AS_BUILT ? this.tableCustomerAsBuiltSortList : this.tableCustomerAsPlannedSortList;

      // Find the index of the existing entry with the same first item
      const index = tableSortList.findIndex(
        ([itemColumnName]) => itemColumnName === columnName
      );

      if (index !== -1) {
        // Replace the existing entry
        tableSortList[index] = sorting;
      } else {
        // Add the new entry if it doesn't exist
        tableSortList.push(sorting);
      }

      if(partTable === MainAspectType.AS_BUILT) {
        this.tableCustomerAsBuiltSortList = tableSortList
      } else {
        this.tableCustomerAsPlannedSortList = tableSortList
      }
    }
    // If CTRL is not pressed just add a list with one entry
    else if(partTable === MainAspectType.AS_BUILT) {
      this.tableCustomerAsBuiltSortList = [sorting];
    } else {
      this.tableCustomerAsPlannedSortList = [sorting]
    }
  }

  private resetTableSortingList(partTable: MainAspectType): void {
    if(partTable === MainAspectType.AS_BUILT) {
      this.tableCustomerAsBuiltSortList = [];
    } else {
      this.tableCustomerAsPlannedSortList= [];
    }
  }
}
