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


import { Component, Input, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { OtherPartsFacade } from '@page/other-parts/core/other-parts.facade';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { Part } from '@page/parts/model/parts.model';
import { PartsTableComponent } from '@shared/components/parts-table/parts-table.component';
import { PartTableType, TableEventConfig, TableHeaderSort } from '@shared/components/table/table.model';
import { TableSortingUtil } from '@shared/components/table/tableSortingUtil';
import { toAssetFilter, toGlobalSearchAssetFilter } from '@shared/helper/filter-helper';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { StaticIdService } from '@shared/service/staticId.service';
import { Observable } from 'rxjs';


@Component({
  selector: 'app-customer-parts',
  templateUrl: './customer-parts.component.html',
})
export class CustomerPartsComponent implements OnInit, OnDestroy {
  @ViewChildren(PartsTableComponent) partsTableComponents: QueryList<PartsTableComponent>;

  public customerPartsAsBuilt$: Observable<View<Pagination<Part>>>;
  public customerPartsAsPlanned$: Observable<View<Pagination<Part>>>;

  public readonly customerTabLabelId = this.staticIdService.generateId('OtherParts.customerTabLabel');

  public tableCustomerAsBuiltSortList: TableHeaderSort[];
  public tableCustomerAsPlannedSortList: TableHeaderSort[];

  private ctrlKeyState = false;

  @Input()
  public bomLifecycle: MainAspectType;

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
    if (this.bomLifecycle === MainAspectType.AS_BUILT) {
      this.customerPartsAsBuilt$ = this.otherPartsFacade.customerPartsAsBuilt$;
      this.tableCustomerAsBuiltSortList = [];
      this.otherPartsFacade.setCustomerPartsAsBuilt();
    } else if (this.bomLifecycle === MainAspectType.AS_PLANNED) {
      this.customerPartsAsPlanned$ = this.otherPartsFacade.customerPartsAsPlanned$;
      this.tableCustomerAsPlannedSortList = [];
      this.otherPartsFacade.setCustomerPartsAsPlanned();
    }
  }

  updateCustomerParts(searchValue?: string): void {
    if (searchValue) {
      this.otherPartsFacade.setCustomerPartsAsBuilt(0, 50, [], toGlobalSearchAssetFilter(searchValue, true), true);
      this.otherPartsFacade.setCustomerPartsAsPlanned(0, 50, [], toGlobalSearchAssetFilter(searchValue, false), true);
    } else {
      this.otherPartsFacade.setCustomerPartsAsBuilt();
      this.otherPartsFacade.setCustomerPartsAsPlanned();
    }
  }

  filterActivated(isAsBuilt: boolean, assetFilter: any): void {
    if (isAsBuilt) {
      this.otherPartsFacade.setCustomerPartsAsBuilt(0, 50, [], toAssetFilter(assetFilter, true));
    } else {
      this.otherPartsFacade.setCustomerPartsAsPlanned(0, 50, [], toAssetFilter(assetFilter, false));
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
    const tableSortList = partTable === MainAspectType.AS_BUILT ? this.tableCustomerAsBuiltSortList : this.tableCustomerAsPlannedSortList;
    TableSortingUtil.setTableSortingList(sorting, tableSortList, this.ctrlKeyState);
  }

  protected readonly MainAspectType = MainAspectType;
  protected readonly PartTableType = PartTableType;
}
