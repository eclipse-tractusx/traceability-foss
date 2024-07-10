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

import { Injectable } from '@angular/core';
import { ApiService } from '@core/api/api.service';
import { environment } from '@env';
import { NotificationAssembler } from '@shared/assembler/notification.assembler';
import { PartsAssembler } from '@shared/assembler/parts.assembler';
import { DateTimeString } from '@shared/components/dateTime/dateTime.component';
import { NotificationChannel } from '@shared/components/multi-select-autocomplete/table-type.model';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { provideFilterListForNotifications } from '@shared/helper/filter-helper';
import { Severity } from '@shared/model/severity.model';
import type { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  Notification,
  NotificationCreateResponse,
  NotificationDeeplinkFilter,
  NotificationResponse,
  Notifications,
  NotificationsResponse,
  NotificationStatus,
} from '../model/notification.model';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private readonly url = environment.apiUrl;

  constructor(private readonly apiService: ApiService) {
  }

  public getNotifications(page: number, pageSize: number, sorting: TableHeaderSort[], channel?: NotificationChannel, filter?: NotificationDeeplinkFilter, fullFilter?: any): Observable<Notifications> {
    const sort = sorting.length ? sorting.map(array => `${ array[0] },${ array[1] }`) : [ 'createdDate,desc' ];
    const requestUrl = this.notificationUrl() + '/filter';
    const channelFilter = channel === NotificationChannel.RECEIVER ? 'channel,EQUAL,RECEIVER,AND' : 'channel,EQUAL,SENDER,AND';
    const additionalFilters = new Set([ ...provideFilterListForNotifications(filter, fullFilter), channelFilter ]);

    const body = {
      pageAble: {
        page: page,
        size: pageSize,
        sort: sort,
      },
      searchCriteria: {
        filter: [ ...additionalFilters ],
      },
    };

    return this.apiService
      .post<NotificationsResponse>(requestUrl, body)
      .pipe(map(data => NotificationAssembler.assembleNotifications(data)));
  }


  public getNotificationById(id: string): Observable<Notification> {
    const requestUrl = this.notificationUrl();
    return this.apiService
      .get<NotificationResponse>(`${ requestUrl }/${ id }`)
      .pipe(map(notification => NotificationAssembler.assembleNotification(notification)));
  }

  public createNotification(affectedPartIds: string[], description: string, severity: Severity, bpn: string, type: string, title: string, dateString: DateTimeString,
  ): Observable<string> {
    const targetDate = dateString?.length > 0 ? new Date(dateString).toISOString() : null;
    const upperCaseType = type ? type.toUpperCase() : null;
    const body = {
      affectedPartIds,
      description,
      severity,
      receiverBpn: bpn,
      type: upperCaseType,
      title: title === '' ? null : title,
      targetDate,
    };

    return this.apiService.post<NotificationCreateResponse>(`${ this.url }/notifications`, body).pipe(map(({ id }) => id));
  }


  public closeNotification(id: string, reason: string): Observable<void> {
    const requestUrl = this.notificationUrl();
    const body = { reason };
    const request = () => this.apiService.post<void>(`${ requestUrl }/${ id }/close`, body);
    this.apiService.lastRequest = {
      context: {
        notificationStatus: NotificationStatus.CLOSED,
        notificationId: id,
      },
      execute: request,
    };
    return request();
  }

  public approveNotification(id: string): Observable<void> {
    const requestUrl = this.notificationUrl();
    const request = () => this.apiService.post<void>(`${ requestUrl }/${ id }/approve`);
    this.apiService.lastRequest = {
      context: {
        notificationStatus: NotificationStatus.APPROVED,
        notificationId: id,
      },
      execute: request,
    };
    return request();
  }

  public cancelNotification(id: string): Observable<void> {
    const requestUrl = this.notificationUrl();
    const request = () => this.apiService.post<void>(`${ requestUrl }/${ id }/cancel`);
    this.apiService.lastRequest = {
      context: {
        notificationStatus: NotificationStatus.CANCELED,
        notificationId: id,
      },
      execute: request,
    };
    return request();
  }

  public updateNotification(
    id: string,
    status: NotificationStatus.ACKNOWLEDGED | NotificationStatus.ACCEPTED | NotificationStatus.DECLINED,
    reason = '',
  ): Observable<void> {
    const requestUrl = this.notificationUrl();
    const body = { reason, status };
    const request = () => this.apiService.post<void>(`${ requestUrl }/${ id }/update`, body);
    this.apiService.lastRequest = {
      context: {
        notificationStatus: NotificationStatus.CLOSED,
        notificationId: id,
      },
      execute: request,
    };
    return request();
  }

  public editNotification(notificationId: string, title: string, receiverBpn: string, severity: string, targetDate: string, description: string, affectedPartIds: string[]): Observable<void> {
    const requestUrl = this.notificationUrl();
    if (targetDate?.length > 0) {
      targetDate = new Date(targetDate).toISOString();
    }
    const body = {
      title: title === '' ? null : title,
      receiverBpn: receiverBpn,
      severity,
      targetDate,
      description,
      affectedPartIds: affectedPartIds,
    };
    return this.apiService.put<void>(`${ requestUrl }/${ notificationId }/edit`, body);
  }


  public getSearchableValues(channel: NotificationChannel, fieldNames: string, startsWith: string) {
    const mappedFieldName = PartsAssembler.mapFieldNameToApi(fieldNames);
    const requestUrl = this.notificationUrl();

    const body = {
      'fieldName': mappedFieldName,
      'startWith': startsWith,
      'size': 200,
      'channel': channel,
    };

    return this.apiService.post(`${ requestUrl }/searchable-values`, body);
  }

  public notificationUrl(): string {
    return this.url + '/notifications';
  }
}
