/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

import {AfterViewInit, Component, OnDestroy, OnInit, QueryList, ViewChildren} from '@angular/core';
import {Pagination} from '@core/model/pagination.model';
import { EssFacade } from '@page/ess/core/ess.facade';
import { Ess, EssFilter } from '@page/ess/model/ess.model';
import {PartsFacade} from '@page/parts/core/parts.facade';
import {MainAspectType} from '@page/parts/model/mainAspectType.enum';
import {AssetAsPlannedFilter, Part} from '@page/parts/model/parts.model';
import { RequestContext } from '@shared/components/request-notification/request-notification.base';
import {PartTableType, TableEventConfig, TableHeaderSort,} from '@shared/components/table/table.model';
import {View} from '@shared/model/view.model';
import {PartDetailsFacade} from '@shared/modules/part-details/core/partDetails.facade';
import {StaticIdService} from '@shared/service/staticId.service';
import {BehaviorSubject, Observable, Subject} from 'rxjs';
import {BomLifecycleSize} from "@shared/components/bom-lifecycle-activator/bom-lifecycle-activator.model";
import {BomLifecycleSettingsService, UserSettingView} from "@shared/service/bom-lifecycle-settings.service";
import {toAssetFilter, toGlobalSearchAssetFilter} from "@shared/helper/filter-helper";
import {FormControl, FormGroup} from "@angular/forms";
import {ToastService} from "@shared/components/toasts/toast.service";
import {EssTableComponent} from "@shared/components/ess-table/ess-table.component";
import {resetMultiSelectionAutoCompleteComponent} from "@page/ess/core/ess.helper";


@Component({
    selector: 'app-ess',
    templateUrl: './ess.component.html',
    styleUrls: ['./ess.component.scss'],
})
export class EssComponent implements OnInit, OnDestroy, AfterViewInit {

    public readonly titleId = this.staticIdService.generateId('PartsComponent.title');

    public readonly esss$: Observable<View<Pagination<Ess>>>;
    public readonly partsAsPlanned4Ess$: Observable<View<Pagination<Part>>>;

    public readonly partsAsBuilt$: Observable<View<Pagination<Part>>>;
    public readonly partsAsPlanned$: Observable<View<Pagination<Part>>>;

    public readonly isPartOpen$ = new BehaviorSubject<boolean>(false);
    public readonly isEssOpen$ = new BehaviorSubject<boolean>(false);

    public readonly deselectEssTrigger$ = new Subject<Ess[]>();
    public readonly addEssTrigger$ = new Subject<Ess>();

    public readonly deselectTrigger$ = new Subject<Part[]>();
    public readonly addTrigger$ = new Subject<Part>();
    public readonly currentSelectedPartItems$ = new BehaviorSubject<Part[]>([]);

    public readonly deselectPartTrigger$ = new Subject<Part[]>();
    public readonly addPartTrigger$ = new Subject<Part>();
    public readonly currentSelectedItems$ = new BehaviorSubject<Part[]>([]);

    public tableEssSortList: TableHeaderSort[];
    public tableAsPlannedSortList: TableHeaderSort[];

    public DEFAULT_PAGE_SIZE = 50;
    public ctrlKeyState = false;

    public readonly essContext: RequestContext = 'ess';

    @ViewChildren(EssTableComponent) partsTableComponents: QueryList<EssTableComponent>;

    constructor(
        private readonly esssFacade: EssFacade,
        private readonly partsFacade: PartsFacade,
        private readonly partDetailsFacade: PartDetailsFacade,
        private readonly staticIdService: StaticIdService,
        private readonly userSettingService: BomLifecycleSettingsService,
        public toastService: ToastService
    ) {
        this.esss$ = this.esssFacade.esss$;
        this.partsAsPlanned4Ess$ = this.partsFacade.partsAsPlanned4Ess$;
        this.partsAsBuilt$ = this.partsFacade.partsAsBuilt$;
        this.partsAsPlanned$ = this.partsFacade.partsAsPlanned$;
        this.tableEssSortList = [];
        this.tableAsPlannedSortList = [];
        window.addEventListener('keydown', (event) => {
            this.ctrlKeyState = event.ctrlKey;
        });
        window.addEventListener('keyup', (event) => {
            this.ctrlKeyState = event.ctrlKey;
        });
    }

    public bomLifecycleSize: BomLifecycleSize = this.userSettingService.getSize(UserSettingView.PARTS);

    public searchFormGroup = new FormGroup({});
    public searchControl: FormControl;

    assetFilter: EssFilter | AssetAsPlannedFilter;

    public ngOnInit(): void {
        this.esssFacade.setEss();
        this.partsFacade.setPartsAsPlanned4Ess();
        this.partsFacade.setPartsAsBuilt();
        this.partsFacade.setPartsAsPlanned();
        this.searchFormGroup.addControl("partSearch", new FormControl([]));
        this.searchControl = this.searchFormGroup.get('partSearch') as unknown as FormControl;
        this.isPartOpen$.subscribe((value) => {
          console.log(value);
        });
    }

    filterActivated(isEss: boolean, assetFilter: any): void {
        this.assetFilter = assetFilter;
        if (isEss) {
            this.esssFacade.setEss(0, this.DEFAULT_PAGE_SIZE, this.tableEssSortList, toAssetFilter(this.assetFilter, true))
        } else {
            this.partsFacade.setPartsAsPlanned(0, this.DEFAULT_PAGE_SIZE, this.tableAsPlannedSortList, toAssetFilter(this.assetFilter, false));
        }
    }

    triggerPartSearch() {

        this.resetFilterAndShowToast();
        const searchValue = this.searchFormGroup.get("partSearch").value;

        if (searchValue && searchValue !== "") {
            this.esssFacade.setEss(0, this.DEFAULT_PAGE_SIZE, this.tableEssSortList, toGlobalSearchAssetFilter(searchValue, false), true);
            this.partsFacade.setPartsAsBuilt(0, this.DEFAULT_PAGE_SIZE, this.tableAsPlannedSortList, toGlobalSearchAssetFilter(searchValue, true), true);
        } else {
            this.esssFacade.setEss();
            this.partsFacade.setPartsAsPlanned();
        }

    }

    private resetFilterAndShowToast() {
        let filterIsSet = resetMultiSelectionAutoCompleteComponent(this.partsTableComponents, false);
        if (filterIsSet) {
            this.toastService.info("parts.input.global-search.toastInfo");
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

    public onSelectPartItem($event: Record<string, unknown>): void {
      // this.partDetailsFacade.selectedPart = $event as unknown as Ess;
    }

    public onEssTableConfigChange({page, pageSize, sorting}: TableEventConfig): void {
      this.setTableSortingList(sorting, MainAspectType.ESS);
      let pageSizeValue = this.DEFAULT_PAGE_SIZE;
      if (pageSize !== 0) {
        pageSizeValue = pageSize;
      }
      if (this.assetFilter) {
        this.esssFacade.setEss(0, pageSizeValue, this.tableEssSortList, toAssetFilter(this.assetFilter, true));
      } else {
        this.esssFacade.setEss(page, pageSizeValue, this.tableEssSortList);
      }
    }

    public openCloseEssCreateDialogForm = (status: boolean) =>{
      this.isEssOpen$.next(status);
      // this.isPartOpen$.next(!status);
    }

    public openClosePartListDialogForm = (status: boolean) =>{
      this.isPartOpen$.next(status);
      // this.isPartOpen$.next(!status);
    }

    public onAsPlannedTableConfigChange({page, pageSize, sorting}: TableEventConfig): void {

        let pageSizeValue = this.DEFAULT_PAGE_SIZE;
        if (pageSize !== 0) {
            pageSizeValue = pageSize;
        }

        if (this.assetFilter) {
            this.partsFacade.setPartsAsPlanned(0, pageSizeValue, this.tableAsPlannedSortList, toAssetFilter(this.assetFilter, true));
        } else {
            this.partsFacade.setPartsAsPlanned(page, pageSizeValue, this.tableAsPlannedSortList);
        }

    }

    public handleTableActivationEvent(bomLifecycleSize: BomLifecycleSize) {
        this.bomLifecycleSize = bomLifecycleSize;
    }

    private setTableSortingList(sorting: TableHeaderSort, partTable: MainAspectType): void {
        // if a sorting Columnlist exists but a column gets resetted:
        if (!sorting && (this.tableEssSortList || this.tableAsPlannedSortList)) {
            this.resetTableSortingList(partTable);
            return;
        }

        // if CTRL is pressed at to sortList
        if (this.ctrlKeyState) {
            const [columnName] = sorting;
            const tableSortList = partTable === MainAspectType.ESS ? this.tableEssSortList : this.tableAsPlannedSortList

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
            if (partTable === MainAspectType.ESS) {
                this.tableEssSortList = tableSortList
            } else {
                this.tableAsPlannedSortList = tableSortList
            }
        }
        // If CTRL is not pressed just add a list with one entry
        else if (partTable === MainAspectType.ESS) {
            this.tableEssSortList = [sorting];
        } else {
            this.tableAsPlannedSortList = [sorting]
        }
    }

    private resetTableSortingList(partTable: MainAspectType): void {
        if (partTable === MainAspectType.ESS) {
          this.tableEssSortList = [];
        } else {
            this.tableAsPlannedSortList = [];
        }
    }

    protected readonly UserSettingView = UserSettingView;
    protected readonly PartTableType = PartTableType;
    protected readonly MainAspectType = MainAspectType;
}
