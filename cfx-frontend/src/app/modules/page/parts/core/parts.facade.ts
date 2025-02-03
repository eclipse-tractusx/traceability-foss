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

import { Injectable } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { PartsState } from '@page/parts/core/parts.state';
import { AssetAsBuiltFilter, AssetAsDesignedFilter, AssetAsOrderedFilter, AssetAsPlannedFilter, AssetAsRecycledFilter, AssetAsSupportedFilter, Part } from '@page/parts/model/parts.model';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { PartsService } from '@shared/service/parts.service';
import { Observable, Subject, Subscription } from 'rxjs';

@Injectable()
export class PartsFacade {
    private partsAsBuiltSubscription: Subscription;
    private partsAsPlannedSubscription: Subscription;
    private partsAsDesignedSubscription: Subscription;
    private partsAsOrderedSubscription: Subscription;
    private partsAsSupportedSubscription: Subscription;
    private partsAsRecycledSubscription: Subscription;

    private readonly unsubscribeTrigger = new Subject<void>();

    constructor(private readonly partsService: PartsService, private readonly partsState: PartsState) {
    }

    public get partsAsBuilt$(): Observable<View<Pagination<Part>>> {
        return this.partsState.partsAsBuilt$;
    }

    public get partsAsPlanned$(): Observable<View<Pagination<Part>>> {
        return this.partsState.partsAsPlanned$;
    }

    public get partsAsDesigned$(): Observable<View<Pagination<Part>>> {
        return this.partsState.partsAsDesigned$;
    }

    public get partsAsOrdered$(): Observable<View<Pagination<Part>>> {
        return this.partsState.partsAsOrdered$;
    }

    public get partsAsSupported$(): Observable<View<Pagination<Part>>> {
        return this.partsState.partsAsSupported$;
    }

    public get partsAsRecycled$(): Observable<View<Pagination<Part>>> {
        return this.partsState.partsAsRecycled$;
    }

    public setPartsAsBuilt(page = 0, pageSize = 50, sorting: TableHeaderSort[] = [], assetAsBuiltFilter?: AssetAsBuiltFilter, isOrSearch?: boolean): void {
        this.partsAsBuiltSubscription?.unsubscribe();
        this.partsAsBuiltSubscription = this.partsService.getPartsAsBuilt(page, pageSize, sorting, assetAsBuiltFilter, isOrSearch).subscribe({
            next: data => (this.partsState.partsAsBuilt = { data }),
            error: error => (this.partsState.partsAsBuilt = { error }),
        });
    }

    public setPartsAsPlanned(page = 0, pageSize = 50, sorting: TableHeaderSort[] = [], assetAsPlannedFilter?: AssetAsPlannedFilter, isOrSearch?: boolean): void {
        this.partsAsPlannedSubscription?.unsubscribe();
        this.partsAsPlannedSubscription = this.partsService.getPartsAsPlanned(page, pageSize, sorting, assetAsPlannedFilter, isOrSearch).subscribe({
            next: data => (this.partsState.partsAsPlanned = { data }),
            error: error => (this.partsState.partsAsPlanned = { error }),
        });
    }

    public setPartsAsOrdered(page = 0, pageSize = 50, sorting: TableHeaderSort[] = [], assetAsOrderedFilter?: AssetAsOrderedFilter, isOrSearch?: boolean): void {
        this.partsAsOrderedSubscription?.unsubscribe();
        this.partsAsOrderedSubscription = this.partsService.getPartsAsOrdered(page, pageSize, sorting, assetAsOrderedFilter, isOrSearch).subscribe({
            next: data => (this.partsState.partsAsOrdered = { data }),
            error: error => (this.partsState.partsAsOrdered = { error }),
        });
    }

    public setPartsAsDesigned(page = 0, pageSize = 50, sorting: TableHeaderSort[] = [], assetAsDesignedFilter?: AssetAsDesignedFilter, isOrSearch?: boolean): void {
        this.partsAsDesignedSubscription?.unsubscribe();
        this.partsAsDesignedSubscription = this.partsService.getPartsAsDesigned(page, pageSize, sorting, assetAsDesignedFilter, isOrSearch).subscribe({
            next: data => (this.partsState.partsAsDesigned = { data }),
            error: error => (this.partsState.partsAsDesigned = { error }),
        });
    }

    public setPartsAsSupported(page = 0, pageSize = 50, sorting: TableHeaderSort[] = [], assetAsSupportedFilter?: AssetAsSupportedFilter, isOrSearch?: boolean): void {
        this.partsAsSupportedSubscription?.unsubscribe();
        this.partsAsSupportedSubscription = this.partsService.getPartsAsSupported(page, pageSize, sorting, assetAsSupportedFilter, isOrSearch).subscribe({
            next: data => (this.partsState.partsAsSupported = { data }),
            error: error => (this.partsState.partsAsSupported = { error }),
        });
    }

    public setPartsAsRecycled(page = 0, pageSize = 50, sorting: TableHeaderSort[] = [], assetAsRecycledFilter?: AssetAsRecycledFilter, isOrSearch?: boolean): void {
        this.partsAsRecycledSubscription?.unsubscribe();
        this.partsAsRecycledSubscription = this.partsService.getPartsAsRecycled(page, pageSize, sorting, assetAsRecycledFilter, isOrSearch).subscribe({
            next: data => (this.partsState.partsAsRecycled = { data }),
            error: error => (this.partsState.partsAsRecycled = { error }),
        });
    }

    public unsubscribeParts(): void {
        this.partsAsBuiltSubscription?.unsubscribe();
        this.partsAsPlannedSubscription?.unsubscribe();
        this.partsAsOrderedSubscription?.unsubscribe();
        this.partsAsDesignedSubscription?.unsubscribe();
        this.partsAsSupportedSubscription?.unsubscribe();
        this.partsAsRecycledSubscription?.unsubscribe();
        this.unsubscribeTrigger.next();
    }
}
