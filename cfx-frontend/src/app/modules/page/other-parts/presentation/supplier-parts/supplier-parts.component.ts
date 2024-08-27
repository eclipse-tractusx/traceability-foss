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
import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, QueryList, ViewChildren } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { OTHER_PARTS_BASE_ROUTE, getRoute } from '@core/known-route';
import { Pagination } from '@core/model/pagination.model';
import { OtherPartsFacade } from '@page/other-parts/core/other-parts.facade';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { AssetAsBuiltFilter, AssetAsPlannedFilter, Part, SemanticDataModel } from '@page/parts/model/parts.model';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { PartsTableComponent } from '@shared/components/parts-table/parts-table.component';
import { RequestContext } from '@shared/components/request-notification/request-notification.base';
import { RequestStepperComponent } from '@shared/components/request-notification/request-stepper/request-stepper.component';
import { TableEventConfig, TableHeaderSort } from '@shared/components/table/table.model';
import { TableSortingUtil } from '@shared/components/table/tableSortingUtil';
import { toAssetFilter, toGlobalSearchAssetFilter } from '@shared/helper/filter-helper';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { StaticIdService } from '@shared/service/staticId.service';
import { BehaviorSubject, Observable, Subject } from 'rxjs';

@Component({
  selector: 'app-supplier-parts',
  templateUrl: './supplier-parts.component.html',
  styleUrls: ['../other-parts.component.scss'],
})
export class SupplierPartsComponent implements OnInit, OnDestroy {

  @Output() onPartsSelected = new EventEmitter<Part[]>();
  @Output() onTotalItemsChanged = new EventEmitter<number>();
  @Output() onPartDeselected = new EventEmitter<Part>();

  public supplierPartsAsBuilt$: Observable<View<Pagination<Part>>>;
  public supplierPartsAsPlanned$: Observable<View<Pagination<Part>>>;

  public readonly deselectPartTrigger$ = new Subject<Part[]>();
  public readonly addPartTrigger$ = new Subject<Part>();

  @Input() set deselectTrigger(deselectItems: unknown[]) {
    if (!deselectItems) {
      return;
    }

    this.deselectPartTrigger$.next(deselectItems as Part[]);
  }

  public readonly isInvestigationOpen$ = new BehaviorSubject<boolean>(false);
  public selectedItems: Array<Part> = [];

  public readonly supplierTabLabelId = this.staticIdService.generateId('OtherParts.supplierTabLabel');

  public tableSupplierAsBuiltSortList: TableHeaderSort[];
  public tableSupplierAsPlannedSortList: TableHeaderSort[];

  public assetAsBuiltFilter: AssetAsBuiltFilter;
  public assetAsPlannedFilter: AssetAsPlannedFilter;

  public readonly searchListAsBuilt: string[];
  public readonly searchListAsPlanned: string[];

  public DEFAULT_PAGE_SIZE = 50;
  public ctrlKeyState = false;
  public globalSearchActive = false;

  @Input() public bomLifecycle: MainAspectType;
  @Input() public showActionButton = true;
  @Input() public showHeader = true;
  @Input() public compactSize = false;

  @ViewChildren(PartsTableComponent) partsTableComponents: QueryList<PartsTableComponent>;

  constructor(
    public readonly otherPartsFacade: OtherPartsFacade,
    private readonly partDetailsFacade: PartDetailsFacade,
    private readonly staticIdService: StaticIdService,
    private readonly router: Router,
    public dialog: MatDialog,
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
    window.addEventListener('keydown', (event) => {
      this.ctrlKeyState = event.ctrlKey;
    });
    window.addEventListener('keyup', (event) => {
      this.ctrlKeyState = event.ctrlKey;
    });
  }

  public get currentSelectedItems(): Part[] {

    this.selectedItems = this.selectedItems.map(part => {
      return {
        ...part,
        semanticDataModel: SemanticDataModel[part.semanticDataModel.toUpperCase() as keyof typeof SemanticDataModel],
      };
    });

    return this.selectedItems || [];
  }

  public set currentSelectedItems(parts: Part[]) {
    this.selectedItems = parts;

    this.onPartsSelected.emit(this.selectedItems);
  }

  public ngOnInit(): void {
    if (this.bomLifecycle === MainAspectType.AS_BUILT) {
      this.supplierPartsAsBuilt$ = this.otherPartsFacade.supplierPartsAsBuilt$;
      this.tableSupplierAsBuiltSortList = [];
      this.assetAsBuiltFilter = {};
      this.otherPartsFacade.setSupplierPartsAsBuilt();
    } else if (this.bomLifecycle === MainAspectType.AS_PLANNED) {
      this.supplierPartsAsPlanned$ = this.otherPartsFacade.supplierPartsAsPlanned$;
      this.tableSupplierAsPlannedSortList = [];
      this.assetAsPlannedFilter = {};
      this.otherPartsFacade.setSupplierPartsAsPlanned();
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

  openDialog(): void {
    const dialogRef = this.dialog.open(RequestStepperComponent, {
      autoFocus: false,
      data: {
        selectedItems: this.currentSelectedItems,
        context: RequestContext.REQUEST_INVESTIGATION,
        tabIndex: 1,
        fromExternal: true,
      },
    });

    const callback = (part: Part) => {
      this.deselectPartTrigger$.next([part]);
      this.currentSelectedItems = this.currentSelectedItems.filter(({ id }) => id !== part.id);
    };

    dialogRef?.componentInstance.deselectPart.subscribe(callback);
    if (dialogRef?.afterClosed) {
      dialogRef.afterClosed().subscribe((_part: Part) => {
        dialogRef.componentInstance.deselectPart.unsubscribe();
      });
    }
  }

  filterActivated(isAsBuilt: boolean, assetFilter: any): void {
    this.globalSearchActive = false;
    if (isAsBuilt) {
      this.assetAsBuiltFilter = assetFilter;
      this.otherPartsFacade.setSupplierPartsAsBuilt(0, this.DEFAULT_PAGE_SIZE, this.tableSupplierAsBuiltSortList, toAssetFilter(this.assetAsBuiltFilter, true), this.globalSearchActive);
    } else {
      this.assetAsPlannedFilter = assetFilter;
      this.otherPartsFacade.setSupplierPartsAsPlanned(0, this.DEFAULT_PAGE_SIZE, this.tableSupplierAsPlannedSortList, toAssetFilter(this.assetAsPlannedFilter, false), this.globalSearchActive);
    }
  }

  public ngOnDestroy(): void {
    this.otherPartsFacade.unsubscribeParts();
  }

  public onSelectItem($event: Record<string, unknown>): void {
    const selectedPart = $event as unknown as Part;
    this.partDetailsFacade.mainAspectType = selectedPart.mainAspectType;
    this.partDetailsFacade.selectedPart = selectedPart;
    this.openDetailPage(selectedPart);
  }

  public openDetailPage(part: Part): void {
    const { link } = getRoute(OTHER_PARTS_BASE_ROUTE);
    this.router.navigate([`/${link}/${part.id}`], { queryParams: { type: part.mainAspectType } });
  }

  public onAsBuiltTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    let pageSizeValue = this.DEFAULT_PAGE_SIZE;
    if (pageSize !== 0) {
      pageSizeValue = pageSize;
    }
    this.setTableSortingList(sorting, MainAspectType.AS_BUILT);
    this.otherPartsFacade.setSupplierPartsAsBuilt(page, pageSizeValue, this.tableSupplierAsBuiltSortList, toAssetFilter(this.assetAsBuiltFilter, true), this.globalSearchActive);
  }

  public onAsPlannedTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    let pageSizeValue = this.DEFAULT_PAGE_SIZE;
    if (pageSize !== 0) {
      pageSizeValue = pageSize;
    }
    this.setTableSortingList(sorting, MainAspectType.AS_PLANNED);
    this.otherPartsFacade.setSupplierPartsAsPlanned(page, pageSizeValue, this.tableSupplierAsPlannedSortList, toAssetFilter(this.assetAsPlannedFilter, false), this.globalSearchActive);
  }

  public onMultiSelect(event: unknown[]): void {
    this.currentSelectedItems = event as Part[];
  }

  public onDefaultPaginationSizeChange(pageSize: number) {
    this.DEFAULT_PAGE_SIZE = pageSize;
  }

  public removeItemFromSelection(part: Part): void {
    this.deselectPartTrigger$.next([part]);
    this.onPartDeselected.emit(part);
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
    this.isInvestigationOpen$.next(false);
  }


  private setTableSortingList(sorting: TableHeaderSort, partTable: MainAspectType): void {
    const tableSortList = partTable === MainAspectType.AS_BUILT ? this.tableSupplierAsBuiltSortList : this.tableSupplierAsPlannedSortList;
    TableSortingUtil.setTableSortingList(sorting, tableSortList, this.ctrlKeyState);
  }

  protected readonly MainAspectType = MainAspectType;
  protected readonly TableType = TableType;
}
