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

import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService } from '@core/api/api.service';
import { environment } from '@env';
import type { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { InvestigationsAssembler } from '../assembler/investigations.assembler';
import {
  Notification,
  NotificationCreateResponse,
  NotificationResponse,
  Notifications,
  NotificationsResponse,
} from '../model/notification.model';

@Injectable({
  providedIn: 'root',
})
export class InvestigationsService {
  private readonly url = environment.apiUrl;

  constructor(private readonly apiService: ApiService) {}

  public getCreatedInvestigations(page: number, pageSize: number): Observable<Notifications> {
    const params = new HttpParams().set('page', page).set('size', pageSize);

    return this.apiService
      .getBy<NotificationsResponse>(`${this.url}/investigations/created`, params)
      .pipe(map(investigations => InvestigationsAssembler.assembleInvestigations(investigations)));
  }

  public getReceivedInvestigations(page: number, pageSize: number): Observable<Notifications> {
    const params = new HttpParams().set('page', page).set('size', pageSize);

    return this.apiService
      .getBy<NotificationsResponse>(`${this.url}/investigations/received`, params)
      .pipe(map(investigations => InvestigationsAssembler.assembleInvestigations(investigations)));
  }

  public getInvestigation(id: string): Observable<Notification> {
    return this.apiService
      .get<NotificationResponse>(`${this.url}/investigations/${id}`)
      .pipe(map(notification => InvestigationsAssembler.assembleInvestigation(notification)));
  }

  public postInvestigation(partIds: string[], description: string): Observable<string> {
    const body = { partIds, description };

    return this.apiService
      .post<NotificationCreateResponse>(`${this.url}/investigations`, body)
      .pipe(map(({ id }) => id));
  }
}
