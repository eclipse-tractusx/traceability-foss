/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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
import { OtherPartsService } from '@page/other-parts/core/other-parts.service';
import { OtherPartsState } from '@page/other-parts/core/other-parts.state';
import { Part } from '@page/parts/model/parts.model';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { PartsService } from '@shared/service/parts.service';
import { Observable, Subscription } from 'rxjs';
import _deepClone from 'lodash-es/cloneDeep';

@Injectable()
export class OtherPartsFacade {
  private customerPartsSubscription: Subscription;
  private supplierPartsSubscription: Subscription;

  constructor(
    private readonly otherPartsService: OtherPartsService,
    private readonly partsService: PartsService,
    private readonly otherPartsState: OtherPartsState,
  ) {}

  public get customerParts$(): Observable<View<Pagination<Part>>> {
    return this.otherPartsState.customerParts$;
  }

  public get supplierParts$(): Observable<View<Pagination<Part>>> {
    return this.otherPartsState.supplierParts$;
  }

  public setCustomerParts(page = 0, pageSize = 5, sorting: TableHeaderSort = null): void {
    this.customerPartsSubscription?.unsubscribe();
    this.customerPartsSubscription = this.otherPartsService.getCustomerParts(page, pageSize, sorting).subscribe({
      next: data => (this.otherPartsState.customerParts = { data }),
      error: error => (this.otherPartsState.customerParts = { error }),
    });
  }

  public setSupplierParts(page = 0, pageSize = 5, sorting: TableHeaderSort = null): void {
    this.supplierPartsSubscription?.unsubscribe();
    this.supplierPartsSubscription = this.otherPartsService.getSupplierParts(page, pageSize, sorting).subscribe({
      next: data => (this.otherPartsState.supplierParts = { data }),
      error: error => (this.otherPartsState.supplierParts = { error }),
    });
  }

  public setActiveInvestigationForParts(parts: Part[]): void {
    const { data } = _deepClone(this.otherPartsState.supplierParts);

    data.content = data.content.map(part => {
      const shouldHighlight = parts.some(currentPart => currentPart.id === part.id);
      return { ...part, shouldHighlight };
    });

    this.otherPartsState.supplierParts = { data };
  }

  public unsubscribeParts(): void {
    this.customerPartsSubscription?.unsubscribe();
    this.supplierPartsSubscription?.unsubscribe();
  }
}
