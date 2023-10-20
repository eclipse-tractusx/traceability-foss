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

import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService } from '@core/api/api.service';
import { environment } from '@env';
import { NotificationAssembler } from '@shared/assembler/notification.assembler';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { Severity } from '@shared/model/severity.model';
import type { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  Notification,
  NotificationCreateResponse,
  NotificationResponse,
  Notifications,
  NotificationsResponse,
  NotificationStatus,
  NotificationType,
} from '../model/notification.model';

@Injectable({
  providedIn: 'root',
})
export class AlertsService {
  private readonly url = environment.apiUrl;

  constructor(private readonly apiService: ApiService) { }

  public getCreatedAlerts(page: number, pageSize: number, sorting: TableHeaderSort[]): Observable<Notifications> {
    let sort = sorting.length ? sorting : ['createdDate,desc'];
    let params = new HttpParams()
      .set('page', page)
      .set('size', pageSize)

    sort.forEach(sortingItem => {
      params = params.append('sort', sortingItem);
    })

    return this.apiService
      .getBy<NotificationsResponse>(`${this.url}/alerts/created`, params)
      .pipe(map(alerts => NotificationAssembler.assembleNotifications(alerts, NotificationType.ALERT)));
  }

  public getReceivedAlerts(page: number, pageSize: number, sorting: TableHeaderSort[]): Observable<Notifications> {
    let sort = sorting ? sorting : ['createdDate,desc'];
    let params = new HttpParams()
      .set('page', page)
      .set('size', pageSize)

    sort.forEach(sortingItem => {
      params = params.append('sort', sortingItem);
    })

    return this.apiService
      .getBy<NotificationsResponse>(`${this.url}/alerts/received`, params)
      .pipe(map(alerts => NotificationAssembler.assembleNotifications(alerts, NotificationType.ALERT)));
  }

  public getAlert(id: string): Observable<Notification> {
    return this.apiService
      .get<NotificationResponse>(`${this.url}/alerts/${id}`)
      .pipe(map(notification => NotificationAssembler.assembleNotification(notification, NotificationType.ALERT)));
  }

  public postAlert(partIds: string[], description: string, severity: Severity, bpn: string, isAsBuilt: boolean): Observable<string> {
    const body = { partIds, description, severity, receiverBpn: bpn, isAsBuilt };

    return this.apiService.post<NotificationCreateResponse>(`${this.url}/alerts`, body).pipe(map(({ id }) => id));
  }

  public closeAlert(id: string, reason: string): Observable<void> {
    const body = { reason };

    return this.apiService.post<void>(`${this.url}/alerts/${id}/close`, body);
  }

  public approveAlert(id: string): Observable<void> {
    return this.apiService.post<void>(`${this.url}/alerts/${id}/approve`);
  }

  public cancelAlert(id: string): Observable<void> {
    return this.apiService.post<void>(`${this.url}/alerts/${id}/cancel`);
  }

  public updateAlert(
    id: string,
    status: NotificationStatus.ACKNOWLEDGED | NotificationStatus.ACCEPTED | NotificationStatus.DECLINED,
    reason = '',
  ): Observable<void> {
    const body = { reason, status };
    return this.apiService.post<void>(`${this.url}/alerts/${id}/update`, body);
  }
}
