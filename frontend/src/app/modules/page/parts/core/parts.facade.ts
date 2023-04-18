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
import { Part } from '@page/parts/model/parts.model';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { PartsService } from '@shared/service/parts.service';
import { Observable, Subject, Subscription } from 'rxjs';

@Injectable()
export class PartsFacade {
  private myPartsSubscription: Subscription;
  private readonly unsubscribeTrigger = new Subject<void>();

  constructor(private readonly partsService: PartsService, private readonly partsState: PartsState) {}

  public get parts$(): Observable<View<Pagination<Part>>> {
    return this.partsState.myParts$;
  }

  public setMyParts(page = 0, pageSize = 5, sorting: TableHeaderSort = null): void {
    this.myPartsSubscription?.unsubscribe();
    this.myPartsSubscription = this.partsService.getMyParts(page, pageSize, sorting).subscribe({
      next: data => (this.partsState.myParts = { data }),
      error: error => (this.partsState.myParts = { error }),
    });
  }

  public unsubscribeParts(): void {
    this.myPartsSubscription?.unsubscribe();
    this.unsubscribeTrigger.next();
  }
}
