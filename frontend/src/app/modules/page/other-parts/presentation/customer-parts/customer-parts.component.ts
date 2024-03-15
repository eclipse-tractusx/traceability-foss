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
import { ActivatedRoute, Params, Router } from '@angular/router';
import { Pagination } from '@core/model/pagination.model';
import { OtherPartsFacade } from '@page/other-parts/core/other-parts.facade';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { AssetAsBuiltFilter, AssetAsPlannedFilter, Part } from '@page/parts/model/parts.model';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { PartsTableComponent } from '@shared/components/parts-table/parts-table.component';
import { TableSortingUtil } from '@shared/components/table/table-sorting.util';
import { TableEventConfig, TableHeaderSort } from '@shared/components/table/table.model';
import { toAssetFilter, toGlobalSearchAssetFilter } from '@shared/helper/filter-helper';
import { setMultiSorting } from '@shared/helper/table-helper';
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

  public tableCustomerAsBuiltSortList: TableHeaderSort[] = [];
  public tableCustomerAsPlannedSortList: TableHeaderSort[] = [];

  private ctrlKeyState = false;

  @Input()
  public bomLifecycle: MainAspectType;

  assetAsBuiltFilter: AssetAsBuiltFilter;
  assetsAsPlannedFilter: AssetAsPlannedFilter;

  public currentPartTablePage = {AS_BUILT_CUSTOMER_PAGE: 0, AS_PLANNED_CUSTOMER_PAGE: 0}


  constructor(
    private readonly otherPartsFacade: OtherPartsFacade,
    private readonly partDetailsFacade: PartDetailsFacade,
    private readonly staticIdService: StaticIdService,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {

    window.addEventListener('keydown', (event) => {
      this.ctrlKeyState = setMultiSorting(event);
    });
    window.addEventListener('keyup', (event) => {
      this.ctrlKeyState = setMultiSorting(event);
    });
  }

  public ngOnInit(): void {
    this.route.queryParams.subscribe(params => this.setupPageByUrlParams(params));

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
      this.assetAsBuiltFilter = assetFilter;
      this.otherPartsFacade.setCustomerPartsAsBuilt(this.currentPartTablePage['AS_BUILT_CUSTOMER_PAGE'], 50, [], toAssetFilter(this.assetAsBuiltFilter, true));
    } else {
      this.assetsAsPlannedFilter = assetFilter;
      this.otherPartsFacade.setCustomerPartsAsPlanned(this.currentPartTablePage['AS_PLANNED_CUSTOMER_PAGE'], 50, [], toAssetFilter(this.assetsAsPlannedFilter, false));
    }
  }

  public ngOnDestroy(): void {
    this.otherPartsFacade.unsubscribeParts();
  }

  public onSelectItem(event: Record<string, unknown>): void {
    this.partDetailsFacade.selectedPart = event as unknown as Part;
    let tableData = {};
    for(let component of this.partsTableComponents) {
      tableData[component.tableType+"_PAGE"] = component.pageIndex;
    }
    this.router.navigate([`otherParts/${event?.id}`], {queryParams: tableData})
  }

  public onAsBuiltTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    this.setTableSortingList(sorting, MainAspectType.AS_BUILT);
    this.currentPartTablePage['AS_BUILT_CUSTOMER_PAGE'] = page;

    let pageSizeValue = 50;
    if (pageSize !== 0) {
      pageSizeValue = pageSize;
    }

    if (this.assetAsBuiltFilter && Object.keys(this.assetAsBuiltFilter).filter(key => this.assetAsBuiltFilter[key].length).length) {
      this.otherPartsFacade.setCustomerPartsAsBuilt(0, pageSizeValue, this.tableCustomerAsBuiltSortList, toAssetFilter(this.assetAsBuiltFilter, true));
    } else {
      this.otherPartsFacade.setCustomerPartsAsBuilt(page, pageSizeValue, this.tableCustomerAsBuiltSortList);
    }
  }

  public onAsPlannedTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    this.setTableSortingList(sorting, MainAspectType.AS_PLANNED);
    this.currentPartTablePage['AS_PLANNED_CUSTOMER_PAGE'] = page;

    let pageSizeValue = 50;
    if (pageSize !== 0) {
      pageSizeValue = pageSize;
    }

    if (this.assetsAsPlannedFilter && Object.keys(this.assetsAsPlannedFilter).filter(key => this.assetsAsPlannedFilter[key].length).length) {
      this.otherPartsFacade.setCustomerPartsAsPlanned(0, pageSizeValue, this.tableCustomerAsPlannedSortList, toAssetFilter(this.assetsAsPlannedFilter, true));
    } else {
      this.otherPartsFacade.setCustomerPartsAsPlanned(page, pageSizeValue, this.tableCustomerAsPlannedSortList);
    }
  }

  private setTableSortingList(sorting: TableHeaderSort, partTable: MainAspectType): void {
    const tableSortList = partTable === MainAspectType.AS_BUILT ? this.tableCustomerAsBuiltSortList : this.tableCustomerAsPlannedSortList;
    TableSortingUtil.setTableSortingList(sorting, tableSortList, this.ctrlKeyState);
  }

  private setupPageByUrlParams(params: Params ) {
    if(!params) {
      return;
    }
    this.onAsBuiltTableConfigChange({page: params['AS_BUILT_CUSTOMER_PAGE'], pageSize: 50, sorting: null});
    this.onAsPlannedTableConfigChange({page: params['AS_PLANNED_CUSTOMER_PAGE'], pageSize: 50, sorting: null});
  }

  protected readonly MainAspectType = MainAspectType;
  protected readonly TableType = TableType;
}
