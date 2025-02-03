/********************************************************************************
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

import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService } from '@core/api/api.service';
import { environment } from '@env';
import { NotificationAssembler } from '@shared/assembler/notification.assembler';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { DateTimeString } from '@shared/components/dateTime/dateTime.component';
import { FilterMethod, TableHeaderSort } from '@shared/components/table/table.model';
import { provideFilterListForNotifications } from '@shared/helper/filter-helper';
import { Severity } from '@shared/model/severity.model';
import type { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { NotificationFilter } from '../../../mocks/services/investigations-mock/investigations.model';
import {
  Notification,
  NotificationCreateResponse,
  NotificationResponse,
  Notifications,
  NotificationsResponse,
  NotificationStatus,
  NotificationType,
} from '../model/notification.model';
import { NotificationChannel } from '@shared/components/multi-select-autocomplete/table-type.model';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private readonly url = environment.apiUrl + '/notifications';

  constructor(private readonly apiService: ApiService) {
  }

  public getNotificationByFilter(page: number, pageSize: number, sorting: TableHeaderSort[], filter?: NotificationFilter, fullFilter?: any, isInvestigation?: boolean, filterMethod = FilterMethod.AND, isReceived?: boolean): Observable<Notifications> {
    const sort = sorting.length ? sorting.map(array => `${array[0]},${array[1]}`) : ['createdDate,desc'];
    const requestUrl = this.url + '/filter';
    const notificationType = isInvestigation ? NotificationType.INVESTIGATION : NotificationType.ALERT;
    const channelType = isReceived? 'RECEIVER' : 'SENDER';
    const notificationTypeString = notificationType.valueOf().toUpperCase();
    const additionalFilters = new Set([...provideFilterListForNotifications(filter, fullFilter, filterMethod), 'channel,EQUAL,' + channelType + ',AND', 'type,EQUAL,' + notificationTypeString + ',AND']);

    const body = {
      pageAble: {
        page: page,
        size: pageSize,
        sort: sort,
      },
      searchCriteria: {
        filter: [...additionalFilters],
      },
    };

    return this.apiService
      .post<NotificationsResponse>(requestUrl, body)
      .pipe(map(data => NotificationAssembler.assembleNotifications(data, notificationType)));
  }


  public getNotificationById(id: string, isInvestigation = true): Observable<Notification> {
    const notificationType = isInvestigation ? NotificationType.INVESTIGATION : NotificationType.ALERT;
    return this.apiService
      .get<NotificationResponse>(`${this.url}/${id}`)
      .pipe(map(notification => NotificationAssembler.assembleNotification(notification, notificationType)));
  }

  public createAlert(affectedPartIds: string[], description: string, severity: Severity, bpn: string, title: string, isAsBuilt: boolean): Observable<string> {
    const body = { affectedPartIds, description, severity, receiverBpn: bpn, isAsBuilt, type: NotificationType.ALERT.toUpperCase() };
    return this.apiService.post<NotificationCreateResponse>(this.url, body).pipe(map(({ id }) => id));
  }

  public createInvestigation(
    affectedPartIds: string[],
    description: string,
    severity: Severity,
    dateString: DateTimeString,
    bpn: string,
    title: string,
  ): Observable<string> {
    // targetDate is an optional field
    const targetDate = null === dateString ? null : new Date(dateString).toISOString();
    const body = { affectedPartIds, description, severity, targetDate, receiverBpn: bpn, title: title === "" ? null: title, type: NotificationType.INVESTIGATION.toUpperCase() };

    return this.apiService
      .post<NotificationCreateResponse>(this.url, body)
      .pipe(map(({ id }) => id));
  }

  public closeNotification(id: string, reason: string): Observable<void> {
    const body = { reason };
    return this.apiService.post<void>(`${this.url}/${id}/close`, body);
  }

  public approveNotification(id: string): Observable<void> {
    return this.apiService.post<void>(`${this.url}/${id}/approve`);
  }

  public cancelNotification(id: string): Observable<void> {
    return this.apiService.post<void>(`${this.url}/${id}/cancel`);
  }

  public updateNotification(
    id: string,
    status: NotificationStatus.ACKNOWLEDGED | NotificationStatus.ACCEPTED | NotificationStatus.DECLINED,
    reason = ''
  ): Observable<void> {
    const body = { reason, status };
    return this.apiService.post<void>(`${this.url}/${id}/update`, body);
  }

  public getDistinctFilterValues(channel: NotificationChannel, fieldNames: string, startsWith: string) {
    const mappedFieldName = PartsAssembler.mapFieldNameToApi(fieldNames);
    const params = new HttpParams()
      .set('fieldName', mappedFieldName)
      .set('startWith', startsWith)
      .set('size', 200)
      .set('channel', channel);

    return this.apiService
      .getBy<any>(`${this.url}/distinctFilterValues`, params);

  }

  public getRequestUrl(): string {
    return this.url;
  }
}
