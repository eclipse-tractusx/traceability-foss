/********************************************************************************
 * Copyright (c) 2022, 2023, 2025 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023, 2025 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023, 2025 Contributors to the Eclipse Foundation
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

import { AfterViewInit, Component, OnDestroy, OnInit, QueryList, ViewChildren } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { Pagination } from '@core/model/pagination.model';
import { DEFAULT_PAGE_SIZE, FIRST_PAGE } from '@core/pagination/pagination.model';
import { RoleService } from '@core/user/role.service';
import { SharedPartService } from '@page/notifications/detail/edit/shared-part.service';
import { PartsFacade } from '@page/parts/core/parts.facade';
import { resetMultiSelectionAutoCompleteComponent } from '@page/parts/core/parts.helper';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { Owner } from '@page/parts/model/owner.enum';
import { PartReloadOperation } from '@page/parts/model/partReloadOperation.enum';
import { AssetAsBuiltFilter, AssetAsPlannedFilter, Part } from '@page/parts/model/parts.model';
import { BomLifecycleSize } from '@shared/components/bom-lifecycle-activator/bom-lifecycle-activator.model';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { PartsTableComponent } from '@shared/components/parts-table/parts-table.component';
import { QuickFilterComponent } from '@shared/components/quick-filter/quick-filter.component';
import { TableEventConfig, TableHeaderSort } from '@shared/components/table/table.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import { containsAtleastOneFilterEntry, toAssetFilter, toGlobalSearchAssetFilter } from '@shared/helper/filter-helper';
import { setMultiSorting } from '@shared/helper/table-helper';
import { NotificationType } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { BomLifecycleSettingsService } from '@shared/service/bom-lifecycle-settings.service';
import { StaticIdService } from '@shared/service/staticId.service';
import { BehaviorSubject, combineLatest, Observable, Subject } from 'rxjs';
import { map } from 'rxjs/operators';


@Component({
  selector: 'app-parts',
  templateUrl: './parts.component.html',
  styleUrls: [ './parts.component.scss' ],
  host: {
    'class': 'app-parts-style'
  }
})
export class PartsComponent implements OnInit, OnDestroy, AfterViewInit {

  public readonly titleId = this.staticIdService.generateId('PartsComponent.title');
  public readonly partsAsBuilt$: Observable<View<Pagination<Part>>>;
  public readonly partsAsPlanned$: Observable<View<Pagination<Part>>>;

  public readonly isAlertOpen$ = new BehaviorSubject<boolean>(false);

  public readonly deselectPartTrigger$ = new Subject<Part[]>();
  public readonly addPartTrigger$ = new Subject<Part>();
  public readonly currentSelectedItems$ = new BehaviorSubject<Part[]>([]);
  public readonly currentSelectedAsPlannedItems$ = new BehaviorSubject<Part[]>([]);
  public allSelectedItems$ = combineLatest([ this.currentSelectedItems$, this.currentSelectedAsPlannedItems$ ])
    .pipe(
      map(([ array1, array2 ]) => {
        return [ ...array1, ...array2 ];
      }),
    );

  public tableAsBuiltSortList: TableHeaderSort[];
  public tableAsPlannedSortList: TableHeaderSort[];

  public currentQuickFilter = Owner.UNKNOWN;

  public ctrlKeyState = false;
  isPublisherOpen$ = new Subject<boolean>();
  public currentPartTablePage = { AS_BUILT_OWN_PAGE: 0, AS_PLANNED_OWN_PAGE: 0 };

  @ViewChildren(PartsTableComponent) partsTableComponents: QueryList<PartsTableComponent>;
  @ViewChildren(QuickFilterComponent) quickFilterComponents: QueryList<QuickFilterComponent>;

  constructor(
    private readonly partsFacade: PartsFacade,
    private readonly partDetailsFacade: PartDetailsFacade,
    private readonly staticIdService: StaticIdService,
    private readonly userSettingService: BomLifecycleSettingsService,
    private readonly sharedPartService: SharedPartService,
    public toastService: ToastService,
    public roleService: RoleService,
    public router: Router,
    public route: ActivatedRoute,
  ) {
    this.partsAsBuilt$ = this.partsFacade.partsAsBuilt$;
    this.partsAsPlanned$ = this.partsFacade.partsAsPlanned$;
    this.tableAsBuiltSortList = [];
    this.tableAsPlannedSortList = [];

    window.addEventListener('keydown', (event) => {
      this.ctrlKeyState = setMultiSorting(event);
    });
    window.addEventListener('keyup', (event) => {
      this.ctrlKeyState = setMultiSorting(event);
    });
  }

  public bomLifecycleSize: BomLifecycleSize = this.userSettingService.getUserSettings();

  public searchFormGroup = new FormGroup({});
  public searchControl: FormControl;

  assetAsBuiltFilter: AssetAsBuiltFilter;
  assetsAsPlannedFilter: AssetAsPlannedFilter;

  public ngOnInit(): void {
    this.partsFacade.setPartsAsBuilt();
    this.partsFacade.setPartsAsPlanned();
    this.searchFormGroup.addControl('partSearch', new FormControl([]));
    this.searchControl = this.searchFormGroup.get('partSearch') as unknown as FormControl;
    this.route.queryParams.subscribe(params => this.setupPageByUrlParams(params));

  }

  splitAreaChanged(data: any) {
    this.bomLifecycleSize = {
      asBuiltSize: data.sizes[0],
      asPlannedSize: data.sizes[1],
    };
    this.userSettingService.setUserSettings(this.bomLifecycleSize);
  }

  maximizeClicked(tableType: TableType) {
    if (tableType === TableType.AS_BUILT_OWN) {
      if (this.bomLifecycleSize.asBuiltSize === 100) {
        this.bomLifecycleSize = {
          asBuiltSize: 50,
          asPlannedSize: 50,
        };
      } else {
        this.bomLifecycleSize = {
          asBuiltSize: 100,
          asPlannedSize: 0,
        };
      }
      this.userSettingService.setUserSettings(this.bomLifecycleSize);
    }

    if (tableType === TableType.AS_PLANNED_OWN) {
      if (this.bomLifecycleSize.asPlannedSize === 100) {
        this.bomLifecycleSize = {
          asBuiltSize: 50,
          asPlannedSize: 50,
        };
      } else {
        this.bomLifecycleSize = {
          asBuiltSize: 0,
          asPlannedSize: 100,
        };
      }
      this.userSettingService.setUserSettings(this.bomLifecycleSize);
    }
  }


  filterActivated(isAsBuilt: boolean, assetFilter: any): void {
    if (isAsBuilt) {
      this.assetAsBuiltFilter = assetFilter;
      this.partsFacade.setPartsAsBuilt(this.currentPartTablePage['AS_BUILT_OWN_PAGE'] ?? FIRST_PAGE, DEFAULT_PAGE_SIZE, this.tableAsBuiltSortList, toAssetFilter(this.assetAsBuiltFilter, true));
    } else {
      this.assetsAsPlannedFilter = assetFilter;
      this.partsFacade.setPartsAsPlanned(this.currentPartTablePage['AS_PLANNED_OWN_PAGE'] ?? FIRST_PAGE, DEFAULT_PAGE_SIZE, this.tableAsPlannedSortList, toAssetFilter(this.assetsAsPlannedFilter, false));
    }
  }

  triggerPartSearch() {

    this.resetFilterAndShowToast();

    const searchValue = this.searchFormGroup.get('partSearch').value;

    if (searchValue && searchValue !== '') {
      this.partsFacade.setPartsAsPlanned(FIRST_PAGE, DEFAULT_PAGE_SIZE, this.tableAsPlannedSortList, toGlobalSearchAssetFilter(searchValue, false), true);
      this.partsFacade.setPartsAsBuilt(FIRST_PAGE, DEFAULT_PAGE_SIZE, this.tableAsBuiltSortList, toGlobalSearchAssetFilter(searchValue, true), true);
    } else {
      this.partsFacade.setPartsAsBuilt();
      this.partsFacade.setPartsAsPlanned();
    }

  }

  updatePartsByOwner(owner: Owner) {
    this.resetFilterAndShowToast();
    let filter = {};
    if (owner != Owner.UNKNOWN) {
      filter = {
        owner,
      };
    }
    this.partsFacade.setPartsAsPlanned(FIRST_PAGE, DEFAULT_PAGE_SIZE, this.tableAsPlannedSortList, filter, true);
    this.partsFacade.setPartsAsBuilt(FIRST_PAGE, DEFAULT_PAGE_SIZE, this.tableAsBuiltSortList, filter, true);
    this.currentQuickFilter = owner;

  }

  refreshPartsOnPublish(message: string) {
    if (message) {
      this.toastService.error(message);
    } else {
      this.toastService.success('requestPublishAssets.success');
      this.partsFacade.setPartsAsBuilt();
      this.partsFacade.setPartsAsPlanned();
      this.partsTableComponents.map(component => component.clearAllRows());
    }
  }

  triggerPartReload(operation: PartReloadOperation) {
    switch (operation) {
      case PartReloadOperation.RELOAD_REGISTRY:
        this.reloadRegistry();
        break;
      case PartReloadOperation.SYNC_PARTS_AS_BUILT:
        this.syncPartsAsBuilt(this.currentSelectedItems$.value.map(part => part.id));
        break;
      case PartReloadOperation.SYNC_PARTS_AS_PLANNED:
        this.syncPartsAsPlanned(this.currentSelectedAsPlannedItems$.value.map(part => part.id));
        break;
    }
  }

  private reloadRegistry() {
    this.partsFacade.reloadRegistry().subscribe({
      next: data => {
        this.toastService.success('registryReload.success');
      },
      error: error => {
        this.toastService.error(error?.getMessage());
      },
    });
  }

  private syncPartsAsBuilt(assetIds: string[]) {
    this.partsFacade.syncPartsAsBuilt(assetIds).subscribe({
      next: data => {
        this.toastService.success('partSynchronization.success');
      },
      error: error => {
        this.toastService.error(error?.getMessage());
      },
    });
  }

  private syncPartsAsPlanned(assetIds: string[]) {
    this.partsFacade.syncPartsAsPlanned(assetIds).subscribe({
      next: data => {
        this.toastService.success('partSynchronization.success');
      },
      error: error => {
        this.toastService.error(error?.getMessage());
      },
    });
  }

  private resetFilterAndShowToast() {
    let filterIsSet = resetMultiSelectionAutoCompleteComponent(this.partsTableComponents, false);
    if (filterIsSet) {
      this.toastService.info('parts.input.global-search.toastInfo');
    }
  }

  public ngAfterViewInit(): void {
    this.handleTableActivationEvent(this.bomLifecycleSize);
  }

  public ngOnDestroy(): void {
    this.partsFacade.unsubscribeParts();
  }

  public onSelectItem($event: Record<string, unknown>): void {
    this.partDetailsFacade.selectedPart = $event as unknown as Part;
    this.partDetailsFacade.mainAspectType = this.partDetailsFacade.selectedPart.mainAspectType;
    let tableData = {};
    for (let component of this.partsTableComponents) {
      tableData[component.tableType + '_PAGE'] = component.pageIndex;
    }
    tableData['isAsBuilt'] = $event?.partId !== undefined;
    this.router.navigate([ `parts/${ $event?.id }` ], { queryParams: tableData });
  }

  public onAsBuiltTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    this.setTableSortingList(sorting, MainAspectType.AS_BUILT);
    this.currentPartTablePage['AS_BUILT_OWN_PAGE'] = page;
    let pageSizeValue = DEFAULT_PAGE_SIZE;
    if (pageSize !== 0) {
      pageSizeValue = pageSize;
    }
    if (this.quickFilterComponents && this.quickFilterComponents?.get(0)?.owner !== Owner.UNKNOWN){
      this.assetAsBuiltFilter.owner = this.quickFilterComponents.get(0)?.owner;
    }
    if (this.assetAsBuiltFilter && containsAtleastOneFilterEntry(this.assetAsBuiltFilter)) {
      this.partsFacade.setPartsAsBuilt(page, pageSizeValue, this.tableAsBuiltSortList, toAssetFilter(this.assetAsBuiltFilter, true));
    } else {
      this.partsFacade.setPartsAsBuilt(page, pageSizeValue, this.tableAsBuiltSortList);
    }

  }

  public onAsPlannedTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    this.setTableSortingList(sorting, MainAspectType.AS_PLANNED);
    this.currentPartTablePage['AS_PLANNED_OWN_PAGE'] = page;

    let pageSizeValue = DEFAULT_PAGE_SIZE;
    if (pageSize !== 0) {
      pageSizeValue = pageSize;
    }
    if (this.quickFilterComponents && this.quickFilterComponents?.get(0)?.owner !== Owner.UNKNOWN){
      this.assetsAsPlannedFilter.owner = this.quickFilterComponents.get(0)?.owner;
    }
    if (this.assetsAsPlannedFilter && containsAtleastOneFilterEntry(this.assetsAsPlannedFilter)) {
      this.partsFacade.setPartsAsPlanned(page, pageSizeValue, this.tableAsPlannedSortList, toAssetFilter(this.assetsAsPlannedFilter, true));
    } else {
      this.partsFacade.setPartsAsPlanned(page, pageSizeValue, this.tableAsPlannedSortList);
    }

  }


  public handleTableActivationEvent(bomLifecycleSize: BomLifecycleSize) {
    this.bomLifecycleSize = bomLifecycleSize;
  }

  private setTableSortingList(sorting: TableHeaderSort, partTable: MainAspectType): void {
    // if a sorting Columnlist exists but a column gets resetted:
    if (!sorting && (this.tableAsBuiltSortList || this.tableAsPlannedSortList)) {
      this.resetTableSortingList(partTable);
      return;
    }

    // if CTRL is pressed at to sortList
    if (this.ctrlKeyState) {
      const [ columnName ] = sorting;
      const tableSortList = partTable === MainAspectType.AS_BUILT ? this.tableAsBuiltSortList : this.tableAsPlannedSortList;

      // Find the index of the existing entry with the same first item
      const index = tableSortList.findIndex(
        ([ itemColumnName ]) => itemColumnName === columnName,
      );

      if (index !== -1) {
        // Replace the existing entry
        tableSortList[index] = sorting;
      } else {
        // Add the new entry if it doesn't exist
        tableSortList.push(sorting);
      }
      if (partTable === MainAspectType.AS_BUILT) {
        this.tableAsBuiltSortList = tableSortList;
      } else {
        this.tableAsPlannedSortList = tableSortList;
      }
    }
    // If CTRL is not pressed just add a list with one entry
    else if (partTable === MainAspectType.AS_BUILT) {
      this.tableAsBuiltSortList = [ sorting ];
    } else {
      this.tableAsPlannedSortList = [ sorting ];
    }
  }

  private resetTableSortingList(partTable: MainAspectType): void {
    if (partTable === MainAspectType.AS_BUILT) {
      this.tableAsBuiltSortList = [];
    } else {
      this.tableAsPlannedSortList = [];
    }
  }

  private setupPageByUrlParams(params: Params) {
    if (!params) {
      return;
    }
    this.onAsBuiltTableConfigChange({ page: params['AS_BUILT_OWN_PAGE'], pageSize: DEFAULT_PAGE_SIZE, sorting: null });
    this.onAsPlannedTableConfigChange({
      page: params['AS_PLANNED_OWN_PAGE'],
      pageSize: DEFAULT_PAGE_SIZE,
      sorting: null,
    });
  }

  openPublisherSideNav(): void {
    this.isPublisherOpen$.next(true);
  }

  navigateToNotificationCreationView(type?: NotificationType) {
    this.sharedPartService.affectedParts = this.currentSelectedItems$.value;
    if (this.sharedPartService?.affectedParts.length > 0 && type) {
      this.router.navigate([ 'inbox/create' ], { queryParams: { initialType: type } });
    } else {
      this.router.navigate([ 'inbox/create' ]);
    }
  }


  protected readonly TableType = TableType;
  protected readonly MainAspectType = MainAspectType;
  protected readonly NotificationType = NotificationType;


}
