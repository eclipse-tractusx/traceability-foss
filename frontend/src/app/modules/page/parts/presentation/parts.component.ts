/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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
import { Pagination } from '@core/model/pagination.model';
import { PartsFacade } from '@page/parts/core/parts.facade';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import {
  AssetAsBuiltFilter,
  AssetAsDesignedFilter,
  AssetAsPlannedFilter,
  AssetAsRecycledFilter,
  AssetAsSupportedFilter,
  AssetAsOrderedFilter,
  Part,
} from '@page/parts/model/parts.model';
import { PartTableType, TableEventConfig, TableHeaderSort } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { StaticIdService } from '@shared/service/staticId.service';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { BomLifecycleSize } from '@shared/components/bom-lifecycle-activator/bom-lifecycle-activator.model';
import { BomLifecycleSettingsService, UserSettingView } from '@shared/service/bom-lifecycle-settings.service';
import { toAssetFilter, toGlobalSearchAssetFilter } from '@shared/helper/filter-helper';
import { FormControl, FormGroup } from '@angular/forms';
import { ToastService } from '@shared/components/toasts/toast.service';
import { PartsTableComponent } from '@shared/components/parts-table/parts-table.component';
import { resetMultiSelectionAutoCompleteComponent } from '@page/parts/core/parts.helper';
import { MatDialog } from '@angular/material/dialog';
import { RequestAlertComponent } from '@shared/components/request-notification/request-alert.component';

@Component({
  selector: 'app-parts',
  templateUrl: './parts.component.html',
  styleUrls: ['./parts.component.scss'],
})
export class PartsComponent implements OnInit, OnDestroy, AfterViewInit {
  public readonly titleId = this.staticIdService.generateId('PartsComponent.title');

  public readonly partsAsBuilt$: Observable<View<Pagination<Part>>>;
  public readonly partsAsPlanned$: Observable<View<Pagination<Part>>>;
  public readonly partsAsDesigned$: Observable<View<Pagination<Part>>>;
  public readonly partsAsOrdered$: Observable<View<Pagination<Part>>>;
  public readonly partsAsSupported$: Observable<View<Pagination<Part>>>;
  public readonly partsAsRecycled$: Observable<View<Pagination<Part>>>;

  public readonly deselectPartTrigger$ = new Subject<Part[]>();
  public readonly addPartTrigger$ = new Subject<Part>();
  public readonly currentSelectedItems$ = new BehaviorSubject<Part[]>([]);

  public tableAsBuiltSortList: TableHeaderSort[];
  public tableAsPlannedSortList: TableHeaderSort[];
  public tableAsDesignedSortList: TableHeaderSort[];
  public tableAsSupportedSortList: TableHeaderSort[];
  public tableAsOrderedSortList: TableHeaderSort[];
  public tableAsRecycledSortList: TableHeaderSort[];

  public assetAsBuiltFilter: AssetAsBuiltFilter;
  public assetAsPlannedFilter: AssetAsPlannedFilter;
  public assetAsDesignedFilter: AssetAsDesignedFilter;
  public assetAsRecycledFilter: AssetAsRecycledFilter;
  public assetAsSupportedFilter: AssetAsSupportedFilter;
  public assetAsOrderedFilter: AssetAsOrderedFilter;

  public DEFAULT_PAGE_SIZE = 50;
  public ctrlKeyState = false;

  protected readonly UserSettingView = UserSettingView;
  protected readonly PartTableType = PartTableType;
  protected readonly MainAspectType = MainAspectType;

  @ViewChildren(PartsTableComponent) partsTableComponents: QueryList<PartsTableComponent>;

  constructor(
    private readonly partsFacade: PartsFacade,
    private readonly partDetailsFacade: PartDetailsFacade,
    private readonly staticIdService: StaticIdService,
    private readonly userSettingService: BomLifecycleSettingsService,
    public dialog: MatDialog,
    public toastService: ToastService,
  ) {
    this.partsAsBuilt$ = this.partsFacade.partsAsBuilt$;
    this.partsAsPlanned$ = this.partsFacade.partsAsPlanned$;
    this.partsAsDesigned$ = this.partsFacade.partsAsDesigned$;
    this.partsAsOrdered$ = this.partsFacade.partsAsOrdered$;
    this.partsAsSupported$ = this.partsFacade.partsAsSupported$;
    this.partsAsRecycled$ = this.partsFacade.partsAsRecycled$;

    this.tableAsBuiltSortList = [];
    this.tableAsPlannedSortList = [];
    this.tableAsDesignedSortList = [];
    this.tableAsOrderedSortList = [];
    this.tableAsSupportedSortList = [];
    this.tableAsRecycledSortList = [];

    this.assetAsBuiltFilter = {};
    this.assetAsDesignedFilter = {};
    this.assetAsOrderedFilter = {};
    this.assetAsPlannedFilter = {};
    this.assetAsRecycledFilter = {};
    this.assetAsSupportedFilter = {};

    window.addEventListener('keydown', event => {
      this.ctrlKeyState = event.ctrlKey;
    });
    window.addEventListener('keyup', event => {
      this.ctrlKeyState = event.ctrlKey;
    });
  }

  public bomLifecycleSize: BomLifecycleSize = this.userSettingService.getSize(UserSettingView.PARTS);

  public searchFormGroup = new FormGroup({});
  public searchControl: FormControl;

  public ngOnInit(): void {
    this.partsFacade.setPartsAsBuilt();
    this.partsFacade.setPartsAsPlanned();
    this.searchFormGroup.addControl('partSearch', new FormControl([]));
    this.searchControl = this.searchFormGroup.get('partSearch') as unknown as FormControl;
  }

  openDialog(): void {
    this.dialog.open(RequestAlertComponent, {
      data: { selectedItems: this.currentSelectedItems$.value, showHeadline: true },
    });
  }

  filterActivated(type: MainAspectType, assetFilter: any): void {
    switch (type) {
      case MainAspectType.AS_BUILT: {
        this.assetAsBuiltFilter = assetFilter;
        this.partsFacade.setPartsAsBuilt(
          0,
          this.DEFAULT_PAGE_SIZE,
          this.tableAsBuiltSortList,
          toAssetFilter(this.assetAsBuiltFilter, true),
        );
        break;
      }
      case MainAspectType.AS_PLANNED: {
        this.assetAsPlannedFilter = assetFilter;
        this.partsFacade.setPartsAsPlanned(
          0,
          this.DEFAULT_PAGE_SIZE,
          this.tableAsPlannedSortList,
          toAssetFilter(this.assetAsPlannedFilter, false),
        );
        break;
      }
      case MainAspectType.AS_DESIGNED: {
        this.assetAsDesignedFilter = assetFilter;
        this.partsFacade.setPartsAsDesigned(
          0,
          this.DEFAULT_PAGE_SIZE,
          this.tableAsDesignedSortList,
          toAssetFilter(this.assetAsDesignedFilter, true),
        );
        break;
      }
      case MainAspectType.AS_ORDERED: {
        this.assetAsOrderedFilter = assetFilter;
        this.partsFacade.setPartsAsOrdered(
          0,
          this.DEFAULT_PAGE_SIZE,
          this.tableAsOrderedSortList,
          toAssetFilter(this.assetAsOrderedFilter, true),
        );
        break;
      }
      case MainAspectType.AS_SUPPORTED: {
        this.assetAsSupportedFilter = assetFilter;
        this.partsFacade.setPartsAsSupported(
          0,
          this.DEFAULT_PAGE_SIZE,
          this.tableAsSupportedSortList,
          toAssetFilter(this.assetAsSupportedFilter, true),
        );
        break;
      }
      case MainAspectType.AS_RECYCLED: {
        this.assetAsRecycledFilter = assetFilter;
        this.partsFacade.setPartsAsRecycled(
          0,
          this.DEFAULT_PAGE_SIZE,
          this.tableAsRecycledSortList,
          toAssetFilter(this.assetAsRecycledFilter, true),
        );
        break;
      }
    }
  }

  // TODO implement search for other tables when they are implemented
  triggerPartSearch() {
    this.resetFilterAndShowToast();
    const searchValue = this.searchFormGroup.get('partSearch').value;

    if (searchValue && searchValue !== '') {
      this.partsFacade.setPartsAsPlanned(
        0,
        this.DEFAULT_PAGE_SIZE,
        this.tableAsPlannedSortList,
        toGlobalSearchAssetFilter(searchValue, false),
        true,
      );
      this.partsFacade.setPartsAsBuilt(
        0,
        this.DEFAULT_PAGE_SIZE,
        this.tableAsBuiltSortList,
        toGlobalSearchAssetFilter(searchValue, true),
        true,
      );
    } else {
      this.partsFacade.setPartsAsBuilt();
      this.partsFacade.setPartsAsPlanned();
    }
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
  }

  public onAsBuiltTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    this.setTableSortingList(sorting, MainAspectType.AS_BUILT);

    let pageSizeValue = this.DEFAULT_PAGE_SIZE;
    if (pageSize !== 0) {
      pageSizeValue = pageSize;
    }
    this.partsFacade.setPartsAsBuilt(
      page,
      pageSizeValue,
      this.tableAsBuiltSortList,
      toAssetFilter(this.assetAsBuiltFilter, true),
    );

  }

  public onAsPlannedTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    this.setTableSortingList(sorting, MainAspectType.AS_PLANNED);
    let pageSizeValue = this.DEFAULT_PAGE_SIZE;
    if (pageSize !== 0) {
      pageSizeValue = pageSize;
    }

    this.partsFacade.setPartsAsPlanned(
      page,
      pageSizeValue,
      this.tableAsPlannedSortList,
      toAssetFilter(this.assetAsPlannedFilter, true),
    );

  }


  public onDefaultPaginationSizeChange(pageSize: number) {
    this.DEFAULT_PAGE_SIZE = pageSize;
  }

  private setTableSortingList(sorting: TableHeaderSort, partTable: MainAspectType): void {
    // if a sorting Columnlist exists but a column gets resetted:
    if (
      !sorting &&
      (this.tableAsBuiltSortList ||
        this.tableAsPlannedSortList ||
        this.tableAsDesignedSortList ||
        this.tableAsOrderedSortList ||
        this.tableAsSupportedSortList ||
        this.tableAsRecycledSortList)
    ) {
      this.resetTableSortingList(partTable);
      return;
    }

    // if CTRL is pressed at to sortList
    if (this.ctrlKeyState) {
      const [columnName] = sorting;
      let tableSortList: TableHeaderSort[] = [];
      switch (partTable) {
        case MainAspectType.AS_BUILT: {
          tableSortList = this.tableAsBuiltSortList;
          break;
        }
        case MainAspectType.AS_PLANNED: {
          tableSortList = this.tableAsPlannedSortList;
          break;
        }
        case MainAspectType.AS_DESIGNED: {
          tableSortList = this.tableAsDesignedSortList;
          break;
        }
        case MainAspectType.AS_ORDERED: {
          tableSortList = this.tableAsOrderedSortList;
          break;
        }
        case MainAspectType.AS_SUPPORTED: {
          tableSortList = this.tableAsSupportedSortList;
          break;
        }
        case MainAspectType.AS_RECYCLED: {
          tableSortList = this.tableAsRecycledSortList;
          break;
        }
      }

      // Find the index of the existing entry with the same first item
      const index = tableSortList.findIndex(([itemColumnName]) => itemColumnName === columnName);

      if (index !== -1) {
        // Replace the existing entry
        tableSortList[index] = sorting;
      } else {
        // Add the new entry if it doesn't exist
        tableSortList.push(sorting);
      }

      switch (partTable) {
        case MainAspectType.AS_BUILT: {
          this.tableAsBuiltSortList = tableSortList;
          break;
        }
        case MainAspectType.AS_PLANNED: {
          this.tableAsPlannedSortList = tableSortList;
          break;
        }
        case MainAspectType.AS_DESIGNED: {
          this.tableAsDesignedSortList = tableSortList;
          break;
        }
        case MainAspectType.AS_ORDERED: {
          this.tableAsOrderedSortList = tableSortList;
          break;
        }
        case MainAspectType.AS_SUPPORTED: {
          this.tableAsSupportedSortList = tableSortList;
          break;
        }
        case MainAspectType.AS_RECYCLED: {
          this.tableAsRecycledSortList = tableSortList;
          break;
        }
      }
    }
    // If CTRL is not pressed just add a list with one entry
    else {
      switch (partTable) {
        case MainAspectType.AS_BUILT: {
          this.tableAsBuiltSortList = [sorting];
          break;
        }
        case MainAspectType.AS_PLANNED: {
          this.tableAsPlannedSortList = [sorting];
          break;
        }
        case MainAspectType.AS_DESIGNED: {
          this.tableAsDesignedSortList = [sorting];
          break;
        }
        case MainAspectType.AS_ORDERED: {
          this.tableAsOrderedSortList = [sorting];
          break;
        }
        case MainAspectType.AS_SUPPORTED: {
          this.tableAsSupportedSortList = [sorting];
          break;
        }
        case MainAspectType.AS_RECYCLED: {
          this.tableAsRecycledSortList = [sorting];
          break;
        }
      }
    }
  }

  private resetTableSortingList(partTable: MainAspectType): void {
    switch (partTable) {
      case MainAspectType.AS_BUILT: {
        this.tableAsBuiltSortList = [];
        break;
      }
      case MainAspectType.AS_PLANNED: {
        this.tableAsPlannedSortList = [];
        break;
      }
      case MainAspectType.AS_DESIGNED: {
        this.tableAsDesignedSortList = [];
        break;
      }
      case MainAspectType.AS_ORDERED: {
        this.tableAsOrderedSortList = [];
        break;
      }
      case MainAspectType.AS_SUPPORTED: {
        this.tableAsSupportedSortList = [];
        break;
      }
      case MainAspectType.AS_RECYCLED: {
        this.tableAsRecycledSortList = [];
        break;
      }
    }
  }

  public onAsDesignedTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    this.setTableSortingList(sorting, MainAspectType.AS_DESIGNED);

    let pageSizeValue = this.DEFAULT_PAGE_SIZE;
    if (pageSize !== 0) {
      pageSizeValue = pageSize;
    }

    this.partsFacade.setPartsAsDesigned(
      page,
      pageSizeValue,
      this.tableAsDesignedSortList,
      toAssetFilter(this.assetAsDesignedFilter, true),
    );

  }

  public onAsOrderedTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    this.setTableSortingList(sorting, MainAspectType.AS_ORDERED);

    let pageSizeValue = this.DEFAULT_PAGE_SIZE;
    if (pageSize !== 0) {
      pageSizeValue = pageSize;
    }

    this.partsFacade.setPartsAsOrdered(
      page,
      pageSizeValue,
      this.tableAsOrderedSortList,
      toAssetFilter(this.assetAsOrderedFilter, true),
    );
  }

  public onAsSupportedTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    this.setTableSortingList(sorting, MainAspectType.AS_SUPPORTED);

    let pageSizeValue = this.DEFAULT_PAGE_SIZE;
    if (pageSize !== 0) {
      pageSizeValue = pageSize;
    }
    this.partsFacade.setPartsAsSupported(
      page,
      pageSizeValue,
      this.tableAsSupportedSortList,
      toAssetFilter(this.assetAsSupportedFilter, true),
    );
  }

  public onAsRecycledTableConfigChange({ page, pageSize, sorting }: TableEventConfig): void {
    this.setTableSortingList(sorting, MainAspectType.AS_RECYCLED);

    let pageSizeValue = this.DEFAULT_PAGE_SIZE;
    if (pageSize !== 0) {
      pageSizeValue = pageSize;
    }

    this.partsFacade.setPartsAsRecycled(
      page,
      pageSizeValue,
      this.tableAsRecycledSortList,
      toAssetFilter(this.assetAsRecycledFilter, true),
    );
  }

  public handleTableActivationEvent(bomLifecycleSize: BomLifecycleSize) {
    this.bomLifecycleSize = bomLifecycleSize;
  }
}
