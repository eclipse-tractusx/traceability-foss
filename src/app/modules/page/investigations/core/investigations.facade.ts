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
import { Pagination } from '@core/model/pagination.model';
import { Investigation, InvestigationStatusGroup } from '@shared/model/investigations.model';
import { View } from '@shared/model/view.model';
import { InvestigationsService } from '@shared/service/investigations.service';
import { Observable, Subscription } from 'rxjs';
import { delay } from 'rxjs/operators';
import { InvestigationsState } from './investigations.state';

@Injectable()
export class InvestigationsFacade {
  private investigationReceivedSubscription: Subscription;
  private investigationQueuedAndRequestedSubscription: Subscription;

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
    return this.investigationsState.investigationsQueuedAndRequested$.pipe(delay(0));
  }

  public setReceivedInvestigation(page = 0, pageSize = 5): void {
    this.investigationReceivedSubscription?.unsubscribe();
    this.investigationReceivedSubscription = this.investigationsService
      .getInvestigationsByType(InvestigationStatusGroup.RECEIVED, page, pageSize)
      .subscribe({
        next: (data: Pagination<Investigation>) => {
          this.investigationsState.investigationsReceived = { data };
        },
        error: (error: Error) => {
          this.investigationsState.investigationsReceived = { error };
        },
      });
  }

  public setQueuedAndRequestedInvestigations(page = 0, pageSize = 5): void {
    this.investigationQueuedAndRequestedSubscription?.unsubscribe();
    this.investigationQueuedAndRequestedSubscription = this.investigationsService
      .getInvestigationsByType(InvestigationStatusGroup.QUEUED_AND_REQUESTED, page, pageSize)
      .subscribe({
        next: (data: Pagination<Investigation>) => {
          this.investigationsState.investigationsQueuedAndRequested = { data };
        },
        error: (error: Error) => {
          this.investigationsState.investigationsQueuedAndRequested = { error };
        },
      });
  }
}
