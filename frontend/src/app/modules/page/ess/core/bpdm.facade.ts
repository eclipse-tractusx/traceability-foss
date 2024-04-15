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
import { FormControl, ɵValue } from '@angular/forms';
import { PaginationBpdm } from '@core/model/pagination.bpdm.model';
import {Pagination} from '@core/model/pagination.model';
import { BpdmState } from '@page/ess/core/bpdm.state';
import { LegalEntity } from '@page/ess/model/bpdm.model';
import {EssFilter, Ess} from '@page/ess/model/ess.model';
import { SelectOption } from '@shared/components/select/select.component';
import {TableHeaderSort} from '@shared/components/table/table.model';
import {View} from '@shared/model/view.model';
import { BpdmService } from '@shared/service/bpdm.service';
import {Observable, Subject, Subscription} from 'rxjs';

@Injectable()
export class BpdmFacade {

  private legalEntitiesSubscription: Subscription;

  private readonly unsubscribeTrigger = new Subject<void>();

  constructor(private readonly bpdmService: BpdmService, private readonly bpdmState: BpdmState) {
  }

  public get legalEntities$(): Observable<View<PaginationBpdm<LegalEntity>>> {
    return this.bpdmState.legalEntities$;
  }

  public setLegalEntities(company_name: string): void {
    this.legalEntitiesSubscription?.unsubscribe();
    this.legalEntitiesSubscription = this.bpdmService.getLegalEntities(company_name).subscribe({
      next: data => {(this.bpdmState.legalEntities = {data})},
      error: error => (this.bpdmState.legalEntities = {error}),
    });
  }

  public unsubscribeParts(): void {
    this.legalEntitiesSubscription?.unsubscribe();
    this.unsubscribeTrigger.next();
  }
}
