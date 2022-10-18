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
import { Notification, Notifications } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { InvestigationsService } from '@shared/service/investigations.service';
import { Observable, Subscription } from 'rxjs';
import { InvestigationsState } from './investigations.state';

@Injectable()
export class InvestigationsFacade {
  private investigationReceivedSubscription: Subscription;
  private investigationQueuedAndRequestedSubscription: Subscription;

  constructor(
    private readonly investigationsService: InvestigationsService,
    private readonly investigationsState: InvestigationsState,
  ) {}

  public get investigationsReceived$(): Observable<View<Notifications>> {
    return this.investigationsState.investigationsReceived$;
  }

  public get investigationsQueuedAndRequested$(): Observable<View<Notifications>> {
    return this.investigationsState.investigationsQueuedAndRequested$;
  }

  public getInvestigation(id: string): Observable<Notification> {
    return this.investigationsService.getInvestigation(id);
  }

  public setReceivedInvestigation(page = 0, pageSize = 5): void {
    this.investigationReceivedSubscription?.unsubscribe();
    this.investigationReceivedSubscription = this.investigationsService
      .getReceivedInvestigations(page, pageSize)
      .subscribe({
        next: data => (this.investigationsState.investigationsReceived = { data }),
        error: (error: Error) => (this.investigationsState.investigationsReceived = { error }),
      });
  }

  public setQueuedAndRequestedInvestigations(page = 0, pageSize = 5): void {
    this.investigationQueuedAndRequestedSubscription?.unsubscribe();
    this.investigationQueuedAndRequestedSubscription = this.investigationsService
      .getCreatedInvestigations(page, pageSize)
      .subscribe({
        next: data => (this.investigationsState.investigationsQueuedAndRequested = { data }),
        error: (error: Error) => (this.investigationsState.investigationsQueuedAndRequested = { error }),
      });
  }

  public stopInvestigations(): void {
    this.investigationReceivedSubscription?.unsubscribe();
    this.investigationQueuedAndRequestedSubscription?.unsubscribe();
  }
}
