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

import { DatePipe } from '@angular/common';
import { Component, Input, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { Router } from '@angular/router';
import { OTHER_PARTS_BASE_ROUTE, getRoute } from '@core/known-route';
import { Pagination } from '@core/model/pagination.model';
import { OtherPartsFacade } from '@page/other-parts/core/other-parts.facade';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { AssetAsBuiltFilter, AssetAsPlannedFilter, Part } from '@page/parts/model/parts.model';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { PartsTableComponent } from '@shared/components/parts-table/parts-table.component';
import { TableEventConfig, TableHeaderSort } from '@shared/components/table/table.model';
import { TableSortingUtil } from '@shared/components/table/tableSortingUtil';
import { toAssetFilter, toGlobalSearchAssetFilter } from '@shared/helper/filter-helper';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { StaticIdService } from '@shared/service/staticId.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-customer-parts',
  templateUrl: './customer-parts.component.html',
  styleUrls: ['../other-parts.component.scss'],
})
export class CustomerPartsComponent implements OnInit, OnDestroy {
  @ViewChildren(PartsTableComponent) partsTableComponents: QueryList<PartsTableComponent>;

  public customerPartsAsBuilt$: Observable<View<Pagination<Part>>>;
  public customerPartsAsPlanned$: Observable<View<Pagination<Part>>>;

  public readonly customerTabLabelId = this.staticIdService.generateId('OtherParts.customerTabLabel');

  public tableCustomerAsBuiltSortList: TableHeaderSort[];
  public tableCustomerAsPlannedSortList: TableHeaderSort[];

  public assetAsBuiltFilter: AssetAsBuiltFilter;
  public assetAsPlannedFilter: AssetAsPlannedFilter;
  public readonly searchListAsBuilt: string[];
  public readonly searchListAsPlanned: string[];

  public DEFAULT_PAGE_SIZE = 50;
  public ctrlKeyState = false;
  public globalSearchActive = false;

  @Input()
  public bomLifecycle: MainAspectType;

  constructor(
    public readonly otherPartsFacade: OtherPartsFacade,
    private readonly partDetailsFacade: PartDetailsFacade,
    private readonly staticIdService: StaticIdService,
    private readonly router: Router,
    public datePipe: DatePipe,
  ) {
    this.searchListAsBuilt = [
      'semanticDataModel',
      'nameAtManufacturer',
      'manufacturerName',
      'manufacturerPartId',
      'semanticModelId',
      'manufacturingDate'];
    this.searchListAsPlanned = [
      'semanticDataModel',
      'nameAtManufacturer',
      'manufacturerName',
      'manufacturerPartId',
      'semanticModelId'];
    window.addEventListener('keydown', event => {
      this.ctrlKeyState = event.ctrlKey;
    });
    window.addEventListener('keyup', event => {
      this.ctrlKeyState = event.ctrlKey;
    });
  }

  public ngOnInit(): void {
    if (this.bomLifecycle === MainAspectType.AS_BUILT) {
      this.customerPartsAsBuilt$ = this.otherPartsFacade.customerPartsAsBuilt$;
      this.tableCustomerAsBuiltSortList = [];
      this.assetAsBuiltFilter = {};
      this.otherPartsFacade.setCustomerPartsAsBuilt(0, this.DEFAULT_PAGE_SIZE);
    } else if (this.bomLifecycle === MainAspectType.AS_PLANNED) {
      this.customerPartsAsPlanned$ = this.otherPartsFacade.customerPartsAsPlanned$;
      this.tableCustomerAsPlannedSortList = [];
      this.assetAsPlannedFilter = {};
      this.otherPartsFacade.setCustomerPartsAsPlanned(0, this.DEFAULT_PAGE_SIZE);
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
    this.globalSearchActive = false;
    if (isAsBuilt) {
      this.assetAsBuiltFilter = assetFilter;
      this.otherPartsFacade.setCustomerPartsAsBuilt(
        0,
        this.DEFAULT_PAGE_SIZE,
        this.tableCustomerAsBuiltSortList,
        toAssetFilter(this.assetAsBuiltFilter, true),
        this.globalSearchActive,
      );
    } else {
      this.assetAsPlannedFilter = assetFilter;
      this.otherPartsFacade.setCustomerPartsAsPlanned(
        0,
        this.DEFAULT_PAGE_SIZE,
        this.tableCustomerAsPlannedSortList,
        toAssetFilter(this.assetAsPlannedFilter, false),
        this.globalSearchActive,
      );
    }
  }

  public ngOnDestroy(): void {
    this.otherPartsFacade.unsubscribeParts();
  }

  public onAsBuiltTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    let pageSizeValue = this.DEFAULT_PAGE_SIZE;
    if (pageSize !== 0) {
      pageSizeValue = pageSize;
    }
    this.setTableSortingList(sorting, MainAspectType.AS_BUILT);
    this.otherPartsFacade.setCustomerPartsAsBuilt(page, pageSizeValue, this.tableCustomerAsBuiltSortList, toAssetFilter(this.assetAsBuiltFilter, true), this.globalSearchActive);
  }

  public onAsPlannedTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    let pageSizeValue = this.DEFAULT_PAGE_SIZE;
    if (pageSize !== 0) {
      pageSizeValue = pageSize;
    }
    this.setTableSortingList(sorting, MainAspectType.AS_PLANNED);
    this.otherPartsFacade.setCustomerPartsAsPlanned(page, pageSizeValue, this.tableCustomerAsPlannedSortList, toAssetFilter(this.assetAsPlannedFilter, false), this.globalSearchActive);
  }

  public onDefaultPaginationSizeChange(pageSize: number) {
    this.DEFAULT_PAGE_SIZE = pageSize;
  }

  public openDetailPage(part: Part): void {
    const { link } = getRoute(OTHER_PARTS_BASE_ROUTE);
    this.router.navigate([`/${link}/${part.id}`], { queryParams: { type: part.mainAspectType } });
  }

  public onSelectItem($event: Record<string, unknown>): void {
    const selectedPart = $event as unknown as Part;
    this.partDetailsFacade.mainAspectType = selectedPart.mainAspectType;
    this.partDetailsFacade.selectedPart = selectedPart;
    this.openDetailPage(selectedPart);
  }

  private setTableSortingList(sorting: TableHeaderSort, partTable: MainAspectType): void {
    const tableSortList =
      partTable === MainAspectType.AS_BUILT ? this.tableCustomerAsBuiltSortList : this.tableCustomerAsPlannedSortList;
    TableSortingUtil.setTableSortingList(sorting, tableSortList, this.ctrlKeyState);
  }

  protected readonly MainAspectType = MainAspectType;
  protected readonly TableType = TableType;
}
