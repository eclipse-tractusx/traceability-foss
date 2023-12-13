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

import {Injectable} from '@angular/core';
import {Pagination} from '@core/model/pagination.model';
import { EssState } from '@page/ess/core/ess.state';
import { Ess, EssFilter } from '@page/ess/model/ess.model';
import {AssetAsPlannedFilter} from '@page/parts/model/parts.model';
import {TableHeaderSort} from '@shared/components/table/table.model';
import {View} from '@shared/model/view.model';
import { EssService } from '@shared/service/ess.service';
import {Observable, Subject, Subscription} from 'rxjs';

@Injectable()
export class EssFacade {
    private essSubscription: Subscription;
    private partsAsPlanned4EssSubscription: Subscription;
    private readonly unsubscribeTrigger = new Subject<void>();

    constructor(private readonly essService: EssService, private readonly essState: EssState) {
    }

    public get esss$(): Observable<View<Pagination<Ess>>> {
      return this.essState.esss$;
    }

    public setEss(page = 0, pageSize = 50, sorting: TableHeaderSort[] = [], essFilter?: EssFilter, isOrSearch?: boolean): void {
      this.essSubscription?.unsubscribe();
      this.essSubscription = this.essService.getEsss(page, pageSize, sorting, essFilter, isOrSearch).subscribe({
        next: data => (this.essState.esss = {data: data}),
        error: error => (this.essState.esss = {error}),
      });
    }

    public get partsAsPlanned4Ess$(): Observable<View<Pagination<Ess>>> {
      return this.essState.partsAsPlanned4Ess$;
    }

    public setPartsAsPlanned4Ess(page = 0, pageSize = 50, sorting: TableHeaderSort[] = [], assetAsPlannedFilter?: AssetAsPlannedFilter, isOrSearch?: boolean): void {
      this.partsAsPlanned4EssSubscription?.unsubscribe();
      this.partsAsPlanned4EssSubscription = this.essService.getEsss(page, pageSize, sorting, assetAsPlannedFilter, isOrSearch).subscribe({
        next: data => (this.essState.partsAsPlanned4Ess = {data}),
        error: error => (this.essState.partsAsPlanned4Ess = {error}),
      });
    }

    public unsubscribeParts(): void {
        this.essSubscription?.unsubscribe();
        this.partsAsPlanned4EssSubscription?.unsubscribe();
        this.unsubscribeTrigger.next();
    }
}
