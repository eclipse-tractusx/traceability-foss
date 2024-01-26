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

import {Injectable} from '@angular/core';
import {Pagination} from '@core/model/pagination.model';
import {OtherPartsService} from '@page/other-parts/core/other-parts.service';
import {OtherPartsState} from '@page/other-parts/core/other-parts.state';
import {Owner} from '@page/parts/model/owner.enum';
import {AssetAsBuiltFilter, AssetAsPlannedFilter, Part} from '@page/parts/model/parts.model';
import {TableHeaderSort} from '@shared/components/table/table.model';
import {View} from '@shared/model/view.model';
import {Observable, Subscription} from 'rxjs';

@Injectable()
export class OtherPartsFacade {
    private customerPartsAsBuiltSubscription: Subscription;
    private customerPartsAsPlannedSubscription: Subscription;

    private supplierPartsAsBuiltSubscription: Subscription;
    private supplierPartsAsPlannedSubscription: Subscription;

    constructor(
        private readonly otherPartsService: OtherPartsService,
        private readonly otherPartsState: OtherPartsState,
    ) {
    }

    public get customerPartsAsBuilt$(): Observable<View<Pagination<Part>>> {
        return this.otherPartsState.customerPartsAsBuilt$;
    }

    public get customerPartsAsPlanned$(): Observable<View<Pagination<Part>>> {
        return this.otherPartsState.customerPartsAsPlanned$;
    }

    public get supplierPartsAsBuilt$(): Observable<View<Pagination<Part>>> {
        return this.otherPartsState.supplierPartsAsBuilt$;
    }

    public get supplierPartsAsPlanned$(): Observable<View<Pagination<Part>>> {
        return this.otherPartsState.supplierPartsAsPlanned$;
    }

// TODO: remove OtherPartsService and integrate in PartService
    public setCustomerPartsAsBuilt(page = 0, pageSize = 50, sorting: TableHeaderSort[] = [], filter?: AssetAsBuiltFilter, isOrSearch?: boolean): void {
        this.customerPartsAsBuiltSubscription?.unsubscribe();
        this.customerPartsAsBuiltSubscription = this.otherPartsService.getOtherPartsAsBuilt(page, pageSize, sorting, Owner.CUSTOMER, filter, isOrSearch).subscribe({
            next: data => (this.otherPartsState.customerPartsAsBuilt = {data}),
            error: error => (this.otherPartsState.customerPartsAsBuilt = {error}),
        });
    }

    public setCustomerPartsAsPlanned(page = 0, pageSize = 50, sorting: TableHeaderSort[] = [], filter?: AssetAsPlannedFilter, isOrSearch?: boolean): void {
        this.customerPartsAsPlannedSubscription?.unsubscribe();
        this.customerPartsAsPlannedSubscription = this.otherPartsService.getOtherPartsAsPlanned(page, pageSize, sorting, Owner.CUSTOMER, filter, isOrSearch).subscribe({
            next: data => (this.otherPartsState.customerPartsAsPlanned = {data}),
            error: error => (this.otherPartsState.customerPartsAsPlanned = {error}),
        });
    }

    public setSupplierPartsAsBuilt(page = 0, pageSize = 50, sorting: TableHeaderSort[] = [], filter?: AssetAsBuiltFilter, isOrSearch?: boolean): void {
        this.supplierPartsAsBuiltSubscription?.unsubscribe();
        this.supplierPartsAsBuiltSubscription = this.otherPartsService.getOtherPartsAsBuilt(page, pageSize, sorting, Owner.SUPPLIER, filter, isOrSearch).subscribe({
            next: data => (this.otherPartsState.supplierPartsAsBuilt = {data}),
            error: error => (this.otherPartsState.supplierPartsAsBuilt = {error}),
        });
    }

    public setSupplierPartsAsPlanned(page = 0, pageSize = 50, sorting: TableHeaderSort[] = [], filter?: AssetAsPlannedFilter, isOrSearch?: boolean): void {
        this.supplierPartsAsPlannedSubscription?.unsubscribe();
        this.supplierPartsAsPlannedSubscription = this.otherPartsService.getOtherPartsAsPlanned(page, pageSize, sorting, Owner.SUPPLIER, filter, isOrSearch).subscribe({
            next: data => (this.otherPartsState.supplierPartsAsPlanned = {data}),
            error: error => (this.otherPartsState.supplierPartsAsPlanned = {error}),
        });
    }


    public unsubscribeParts(): void {
        this.customerPartsAsBuiltSubscription?.unsubscribe();
        this.customerPartsAsPlannedSubscription?.unsubscribe();
        this.supplierPartsAsBuiltSubscription?.unsubscribe();
        this.supplierPartsAsPlannedSubscription?.unsubscribe();
    }
}
