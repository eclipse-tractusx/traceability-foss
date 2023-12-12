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

import {HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {ApiService} from '@core/api/api.service';
import {environment} from '@env';
import {NotificationAssembler} from '@shared/assembler/notification.assembler';
import {PartsAssembler} from '@shared/assembler/parts.assembler';
import {DateTimeString} from '@shared/components/dateTime/dateTime.component';
import {TableHeaderSort} from '@shared/components/table/table.model';
import {provideFilterForNotifications} from '@shared/helper/filter-helper';
import {Severity} from '@shared/model/severity.model';
import type {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {NotificationFilter} from '../../../mocks/services/investigations-mock/investigations.model';
import {
    Notification,
    NotificationCreateResponse,
    NotificationResponse,
    Notifications,
    NotificationsResponse,
    NotificationStatus,
    NotificationType,
} from '../model/notification.model';
import {NotificationChannel} from "@shared/components/multi-select-autocomplete/table-type.model";

@Injectable({
    providedIn: 'root',
})
export class NotificationService {
    private readonly url = environment.apiUrl;

    constructor(private readonly apiService: ApiService) {
    }

    public getCreated(page: number, pageSize: number, sorting: TableHeaderSort[], filter?: NotificationFilter, fullFilter?: any, isInvestigation = true): Observable<Notifications> {
        let sort = sorting.length ? sorting : ['createdDate,desc'];
        const requestUrl = this.determineRequestUrl(isInvestigation);
        const notificationType = isInvestigation ? NotificationType.INVESTIGATION : NotificationType.ALERT;
        let params = new HttpParams()
            .set('page', page)
            .set('size', pageSize)
            .set('filter', 'channel,EQUAL,SENDER,AND');

        params = provideFilterForNotifications(sort, params, filter, fullFilter);

        return this.apiService
            .getBy<NotificationsResponse>(requestUrl, params)
            .pipe(map(data => NotificationAssembler.assembleNotifications(data, notificationType)));
    }

    public getReceived(page: number, pageSize: number, sorting: TableHeaderSort[], filter?: NotificationFilter, fullFilter?: any, isInvestigation = true): Observable<Notifications> {
        let sort = sorting.length ? sorting : ['createdDate,desc'];
        const requestUrl = this.determineRequestUrl(isInvestigation);
        const notificationType = isInvestigation ? NotificationType.INVESTIGATION : NotificationType.ALERT;
        let params = new HttpParams()
            .set('page', page)
            .set('size', pageSize)
            .set('filter', 'channel,EQUAL,RECEIVER,AND');

        params = provideFilterForNotifications(sort, params, filter, fullFilter);

        return this.apiService
            .getBy<NotificationsResponse>(requestUrl, params)
            .pipe(map(data => NotificationAssembler.assembleNotifications(data, notificationType)));
    }


    public getNotificationById(id: string, isInvestigation = true): Observable<Notification> {
        const requestUrl = this.determineRequestUrl(isInvestigation);
        const notificationType = isInvestigation ? NotificationType.INVESTIGATION : NotificationType.ALERT;
        return this.apiService
            .get<NotificationResponse>(`${requestUrl}/${id}`)
            .pipe(map(notification => NotificationAssembler.assembleNotification(notification, notificationType)));
    }

    public createAlert(partIds: string[], description: string, severity: Severity, bpn: string, isAsBuilt: boolean): Observable<string> {
        const body = {partIds, description, severity, receiverBpn: bpn, isAsBuilt};

        return this.apiService.post<NotificationCreateResponse>(`${this.url}/alerts`, body).pipe(map(({id}) => id));
    }

    public createInvestigation(
        partIds: string[],
        description: string,
        severity: Severity,
        dateString: DateTimeString,
    ): Observable<string> {
        // targetDate is an optional field
        const targetDate = null === dateString ? null : new Date(dateString).toISOString();
        const body = {partIds, description, severity, targetDate};

        return this.apiService
            .post<NotificationCreateResponse>(`${this.url}/investigations`, body)
            .pipe(map(({id}) => id));
    }

    public closeNotification(id: string, reason: string, isInvestigation = true): Observable<void> {
        const requestUrl = this.determineRequestUrl(isInvestigation);
        const body = {reason};
        return this.apiService.post<void>(`${requestUrl}/${id}/close`, body);
    }

    public approveNotification(id: string, isInvestigation = true): Observable<void> {
        const requestUrl = this.determineRequestUrl(isInvestigation);
        return this.apiService.post<void>(`${requestUrl}/${id}/approve`);
    }

    public cancelNotification(id: string, isInvestigation = true): Observable<void> {
        const requestUrl = this.determineRequestUrl(isInvestigation);
        return this.apiService.post<void>(`${requestUrl}/${id}/cancel`);
    }

    public updateNotification(
        id: string,
        status: NotificationStatus.ACKNOWLEDGED | NotificationStatus.ACCEPTED | NotificationStatus.DECLINED,
        reason = '', isInvestigation = true
    ): Observable<void> {
        const requestUrl = this.determineRequestUrl(isInvestigation);
        const body = {reason, status};
        return this.apiService.post<void>(`${requestUrl}/${id}/update`, body);
    }

    public getDistinctFilterValues(channel: NotificationChannel, fieldNames: string, startsWith: string, isInvestigation = true) {
        const mappedFieldName = PartsAssembler.mapFieldNameToApi(fieldNames);
        const requestUrl = this.determineRequestUrl(isInvestigation);
        let params = new HttpParams()
            .set('fieldName', mappedFieldName)
            .set('startWith', startsWith)
            .set('size', 200)
            .set('channel', channel);

        return this.apiService
            .getBy<any>(`${requestUrl}/distinctFilterValues`, params);

    }

    public determineRequestUrl(isInvestigation: boolean): string {
        const path = isInvestigation ? '/investigations' : '/alerts';
        return this.url + path;
    }
}
