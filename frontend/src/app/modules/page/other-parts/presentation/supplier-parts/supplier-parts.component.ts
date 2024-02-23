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
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { PartsTableComponent } from '@shared/components/parts-table/parts-table.component';
import { TableSortingUtil } from '@shared/components/table/table-sorting.util';
import { TableEventConfig, TableHeaderSort } from '@shared/components/table/table.model';
import { toAssetFilter, toGlobalSearchAssetFilter } from '@shared/helper/filter-helper';
import { NotificationType } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { StaticIdService } from '@shared/service/staticId.service';
import { BehaviorSubject, Observable, Subject } from 'rxjs';

@Component({
  selector: 'app-supplier-parts',
  templateUrl: './supplier-parts.component.html',
  styleUrls: [ '../other-parts.component.scss' ],
})
export class SupplierPartsComponent implements OnInit, OnDestroy {

  public supplierPartsAsBuilt$: Observable<View<Pagination<Part>>>;
  public supplierPartsAsPlanned$: Observable<View<Pagination<Part>>>;

  public readonly deselectPartTrigger$ = new Subject<Part[]>();
  public readonly addPartTrigger$ = new Subject<Part>();
  public readonly currentSelectedItems$ = new BehaviorSubject<Part[]>([]);

  public readonly isInvestigationOpen$ = new BehaviorSubject<boolean>(false);

  public readonly supplierTabLabelId = this.staticIdService.generateId('OtherParts.supplierTabLabel');

  public tableSupplierAsBuiltSortList: TableHeaderSort[];
  public tableSupplierAsPlannedSortList: TableHeaderSort[];

  private ctrlKeyState = false;

  @Input()
  public bomLifecycle: MainAspectType;

  @ViewChildren(PartsTableComponent) partsTableComponents: QueryList<PartsTableComponent>;

  constructor(
    private readonly otherPartsFacade: OtherPartsFacade,
    private readonly partDetailsFacade: PartDetailsFacade,
    private readonly staticIdService: StaticIdService,
  ) {

    window.addEventListener('keydown', (event) => {
      if(event.metaKey || event.ctrlKey) {
        this.ctrlKeyState = true;
      }
    });
    window.addEventListener('keyup', (event) => {
      if(this.ctrlKeyState && (!event.metaKey || !event.ctrlKey)) {
        this.ctrlKeyState = false;
      }

    });
  }

  public ngOnInit(): void {
    if (this.bomLifecycle === MainAspectType.AS_BUILT) {
      this.supplierPartsAsBuilt$ = this.otherPartsFacade.supplierPartsAsBuilt$;
      this.tableSupplierAsBuiltSortList = [];
      this.otherPartsFacade.setSupplierPartsAsBuilt();
    } else if (this.bomLifecycle === MainAspectType.AS_PLANNED) {
      this.supplierPartsAsPlanned$ = this.otherPartsFacade.supplierPartsAsPlanned$;
      this.tableSupplierAsPlannedSortList = [];
      this.otherPartsFacade.setSupplierPartsAsPlanned();
    }
  }

  filterActivated(isAsBuilt: boolean, assetFilter: any): void {
    if (isAsBuilt) {
      this.otherPartsFacade.setSupplierPartsAsBuilt(0, 50, [], toAssetFilter(assetFilter, true));
    } else {
      this.otherPartsFacade.setSupplierPartsAsPlanned(0, 50, [], toAssetFilter(assetFilter, false));
    }
  }

  updateSupplierParts(searchValue?: string): void {
    if (searchValue || searchValue === '') {
      this.otherPartsFacade.setSupplierPartsAsBuilt(0, 50, [], toGlobalSearchAssetFilter(searchValue, true), true);
      this.otherPartsFacade.setSupplierPartsAsPlanned(0, 50, [], toGlobalSearchAssetFilter(searchValue, false), true);
    } else {
      this.otherPartsFacade.setSupplierPartsAsBuilt();
      this.otherPartsFacade.setSupplierPartsAsPlanned();
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
    this.otherPartsFacade.setSupplierPartsAsBuilt(page, pageSize, this.tableSupplierAsBuiltSortList);
  }

  public onAsPlannedTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    this.setTableSortingList(sorting, MainAspectType.AS_PLANNED);
    this.otherPartsFacade.setSupplierPartsAsPlanned(page, pageSize, this.tableSupplierAsPlannedSortList);
  }


  private setTableSortingList(sorting: TableHeaderSort, partTable: MainAspectType): void {
    const tableSortList = partTable === MainAspectType.AS_BUILT ? this.tableSupplierAsBuiltSortList : this.tableSupplierAsPlannedSortList;
    TableSortingUtil.setTableSortingList(sorting, tableSortList, this.ctrlKeyState);
  }

  protected readonly MainAspectType = MainAspectType;
  protected readonly TableType = TableType;
    protected readonly NotificationType = NotificationType;
}
