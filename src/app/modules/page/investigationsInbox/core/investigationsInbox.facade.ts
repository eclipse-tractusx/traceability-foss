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
import { Observable, BehaviorSubject, Subject } from 'rxjs';
import { delay, withLatestFrom, switchMap } from 'rxjs/operators';
import { Pagination } from '@core/model/pagination.model';
import { View } from '@shared/model/view.model';

import { InvestigationsService } from './investigations.service';
import { InvestigationsInboxState } from './investigationsInbox.state';
import { Investigation, InvestigationType } from '../model/investigationsInbox.model';

@Injectable()
export class InvestigationsInboxFacade {
  private pagination$ = new Subject<{ page: number; pageSize: number }>();
  private type$ = new Subject<InvestigationType>();

  constructor(
    private readonly investigationsService: InvestigationsService,
    private readonly investigationsInboxState: InvestigationsInboxState,
  ) {}

  get investigations$(): Observable<View<Pagination<Investigation>>> {
    // IMPORTANT: this delay is needed for view-container directive
    return this.investigationsInboxState.investigations$.pipe(delay(0));
  }

  public setInvestigations() {
    this.pagination$
      .pipe(
        withLatestFrom(this.type$),
        switchMap(([{ page, pageSize }, type]) =>
          this.investigationsService.getInvestigetionsByType(type, page, pageSize),
        ),
      )
      .subscribe({
        next: (partsPage: Pagination<Investigation>) => {
          this.investigationsInboxState.investigations = { data: partsPage };
        },
        error: (error: Error) => {
          this.investigationsInboxState.investigations = { error };
        },
      });
  }

  public setInvestigationsType(type: InvestigationType): void {
    this.type$.next(type);
    this.pagination$.next({ page: 0, pageSize: 5 });
  }

  public setInvestigationsPagination(page, pageSize): void {
    this.pagination$.next({ page, pageSize });
  }
}
