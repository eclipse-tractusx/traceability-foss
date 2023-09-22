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

import {AfterViewInit, Component, OnDestroy, OnInit} from '@angular/core';
import {Pagination} from '@core/model/pagination.model';
import {PartsFacade} from '@page/parts/core/parts.facade';
import {MainAspectType} from '@page/parts/model/mainAspectType.enum';
import {Part} from '@page/parts/model/parts.model';
import {
    CreateHeaderFromColumns,
    TableConfig,
    TableEventConfig,
    TableHeaderSort,
} from '@shared/components/table/table.model';
import {View} from '@shared/model/view.model';
import {PartDetailsFacade} from '@shared/modules/part-details/core/partDetails.facade';
import {StaticIdService} from '@shared/service/staticId.service';
import {BehaviorSubject, Observable, Subject} from 'rxjs';
import {
    BomLifecycleSize,
    initialBomLifecycleSize
} from "@shared/components/bom-lifecycle-activator/bom-lifecycle-activator.model";


@Component({
    selector: 'app-parts',
    templateUrl: './parts.component.html',
    styleUrls: ['./parts.component.scss'],
})
export class PartsComponent implements OnInit, OnDestroy, AfterViewInit {

    public readonly displayedColumnsAsBuilt: string[] = [
        'select',
        'id',
        'idShort',
        'name', // nameAtManufacturer
        'manufacturer',
        'partId', // Part number / Batch Number / JIS Number
        'manufacturerPartId',
        'customerPartId', // --> semanticModel.customerPartId
        'classification',
        //'nameAtManufacturer', --> already in name
        'nameAtCustomer', // --> semanticModel.nameAtCustomer
        'semanticModelId',
        'semanticDataModel',
        'manufacturingDate',
        'manufacturingCountry',
    ];


    public readonly displayedColumnsAsPlanned: string[] = [
        'select',
        'id',
        'idShort',
        'name',
        'manufacturer',
        'manufacturerPartId',
        'classification',
        'semanticDataModel',
        'semanticModelId',
        'validityPeriodFrom',
        'validityPeriodTo',
        'psFunction',
        'catenaXSiteId',
        'functionValidFrom',
        'functionValidUntil',
    ];

    public readonly sortableColumnsAsBuilt: Record<string, boolean> = {
        id: true,
        idShort: true,
        name: true,
        manufacturer: true,
        partId: true,
        manufacturerPartId: true,
        customerPartId: true,
        classification: true,
        nameAtCustomer: true,
        semanticModelId: true,
        semanticDataModel: true,
        manufacturingDate: true,
        manufacturingCountry: true,

    };

    public readonly sortableColumnsAsPlanned: Record<string, boolean> = {
        id: true,
        idShort: true,
        name: true,
        manufacturer: true,
        manufacturerPartId: true,
        classification: true,
        semanticDataModel: true,
        semanticModelId: true,
        validityPeriodFrom: true,
        validityPeriodTo: true,
        psFunction: true,
        catenaXSiteId: true,
        functionValidFrom: true,
        functionValidUntil: true,
    };

    public readonly titleId = this.staticIdService.generateId('PartsComponent.title');
    public readonly partsAsBuilt$: Observable<View<Pagination<Part>>>;
    public readonly partsAsPlanned$: Observable<View<Pagination<Part>>>;

    public readonly isAlertOpen$ = new BehaviorSubject<boolean>(false);

    public readonly deselectPartTrigger$ = new Subject<Part[]>();
    public readonly addPartTrigger$ = new Subject<Part>();
    public readonly currentSelectedItems$ = new BehaviorSubject<Part[]>([]);

    public tableAsBuiltSortList: TableHeaderSort[];
    public tableAsPlannedSortList: TableHeaderSort[];

    public tableConfigAsBuilt: TableConfig;
    public tableConfigAsPlanned: TableConfig;

    private ctrlKeyState = false;

    public bomLifecycleSize: BomLifecycleSize = initialBomLifecycleSize();

    constructor(
        private readonly partsFacade: PartsFacade,
        private readonly partDetailsFacade: PartDetailsFacade,
        private readonly staticIdService: StaticIdService,
    ) {
        this.partsAsBuilt$ = this.partsFacade.partsAsBuilt$;
        this.partsAsPlanned$ = this.partsFacade.partsAsPlanned$;
        this.tableAsBuiltSortList = [];
        this.tableAsPlannedSortList = [];

        window.addEventListener('keydown', (event) => {
            this.ctrlKeyState = event.ctrlKey;
        });
        window.addEventListener('keyup', (event) => {
            this.ctrlKeyState = event.ctrlKey;
        });
    }

    public ngOnInit(): void {
        this.partsFacade.setPartsAsBuilt();
        this.partsFacade.setPartsAsPlanned();
    }

    public ngAfterViewInit(): void {
        this.tableConfigAsBuilt = {
            displayedColumns: this.displayedColumnsAsBuilt,
            header: CreateHeaderFromColumns(this.displayedColumnsAsBuilt, 'table.column'),
            sortableColumns: this.sortableColumnsAsBuilt,
        };
        this.tableConfigAsPlanned = {
            displayedColumns: this.displayedColumnsAsPlanned,
            header: CreateHeaderFromColumns(this.displayedColumnsAsPlanned, 'table.column'),
            sortableColumns: this.sortableColumnsAsPlanned,
        }
    }

    public ngOnDestroy(): void {
        this.partsFacade.unsubscribeParts();
    }

    public onSelectItem($event: Record<string, unknown>): void {
        this.partDetailsFacade.selectedPart = $event as unknown as Part;
    }

    public onAsBuiltTableConfigChange({page, pageSize, sorting}: TableEventConfig): void {
        this.setTableSortingList(sorting, MainAspectType.AS_BUILT);
        this.partsFacade.setPartsAsBuilt(page, pageSize, this.tableAsBuiltSortList);
    }

    public onAsPlannedTableConfigChange({page, pageSize, sorting}: TableEventConfig): void {
        this.setTableSortingList(sorting, MainAspectType.AS_PLANNED);
        this.partsFacade.setPartsAsPlanned(page, pageSize, this.tableAsPlannedSortList);
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
            const [columnName] = sorting;
            const tableSortList = partTable === MainAspectType.AS_BUILT ? this.tableAsBuiltSortList : this.tableAsPlannedSortList

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
            if (partTable === MainAspectType.AS_BUILT) {
                this.tableAsBuiltSortList = tableSortList
            } else {
                this.tableAsPlannedSortList = tableSortList
            }
        }
        // If CTRL is not pressed just add a list with one entry
        else if (partTable === MainAspectType.AS_BUILT) {
            this.tableAsBuiltSortList = [sorting];
        } else {
            this.tableAsPlannedSortList = [sorting]
        }
    }

    private resetTableSortingList(partTable: MainAspectType): void {
        if (partTable === MainAspectType.AS_BUILT) {
            this.tableAsBuiltSortList = [];
        } else {
            this.tableAsPlannedSortList = [];
        }
    }

}
