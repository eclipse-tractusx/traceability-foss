/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { delay, switchMap } from 'rxjs/operators';
import { Pagination } from '@core/model/pagination.model';
import { View } from '@shared/model/view.model';

import { InvestigationsService } from './investigations.service';
import { InvestigationsState } from './investigations.state';
import { Investigation, InvestigationStatusGroup } from '../model/investigations.model';

@Injectable()
export class InvestigationsFacade {
  static DEFAULT_PAGINATION = { page: 0, pageSize: 5 };

  private paginationReceived$ = new BehaviorSubject(InvestigationsFacade.DEFAULT_PAGINATION);
  private paginationQueuedNRequested$ = new BehaviorSubject(InvestigationsFacade.DEFAULT_PAGINATION);

  constructor(
    private readonly investigationsService: InvestigationsService,
    private readonly investigationsState: InvestigationsState,
  ) {}

  get investigationsReceived$(): Observable<View<Pagination<Investigation>>> {
    // IMPORTANT: this delay is needed for view-container directive
    return this.investigationsState.investigationsReceived$.pipe(delay(0));
  }

  get investigationsQueuedNRequested$(): Observable<View<Pagination<Investigation>>> {
    // IMPORTANT: this delay is needed for view-container directive
    return this.investigationsState.investigationsQueuedNRequested$.pipe(delay(0));
  }

  public setInvestigations() {
    this.paginationReceived$
      .pipe(
        switchMap(({ page, pageSize }) =>
          this.investigationsService.getInvestigetionsByType(InvestigationStatusGroup.RECEIVED, page, pageSize),
        ),
      )
      .subscribe({
        next: (partsPage: Pagination<Investigation>) => {
          this.investigationsState.investigationsReceived = { data: partsPage };
        },
        error: (error: Error) => {
          this.investigationsState.investigationsReceived = { error };
        },
      });

    this.paginationQueuedNRequested$
      .pipe(
        switchMap(({ page, pageSize }) =>
          this.investigationsService.getInvestigetionsByType(
            InvestigationStatusGroup.QUEUED_N_REQUESTED,
            page,
            pageSize,
          ),
        ),
      )
      .subscribe({
        next: (partsPage: Pagination<Investigation>) => {
          this.investigationsState.investigationsQueuedNRequested = { data: partsPage };
        },
        error: (error: Error) => {
          this.investigationsState.investigationsQueuedNRequested = { error };
        },
      });
  }

  public setInvestigationsPagination(type: InvestigationStatusGroup, page, pageSize): void {
    const pagination =
      type === InvestigationStatusGroup.RECEIVED ? this.paginationReceived$ : this.paginationQueuedNRequested$;
    pagination.next({ page, pageSize });
  }
}
