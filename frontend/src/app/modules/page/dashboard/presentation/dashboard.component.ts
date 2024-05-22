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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { getRoute, NOTIFICATION_BASE_ROUTE } from '@core/known-route';
import { DashboardStats } from '@page/dashboard/model/dashboard.model';
import { MetricData } from '@page/dashboard/presentation/dashboard.model';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { Notification, Notifications, NotificationStatusGroup } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { DashboardFacade } from '../abstraction/dashboard.facade';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: [ './dashboard.component.scss' ],
})
export class DashboardComponent implements OnInit, OnDestroy {

  public readonly dashboardStats$: Observable<View<DashboardStats>>;
  public readonly investigationsReceived$: Observable<View<Notifications>>;
  public readonly investigationsCreated$: Observable<View<Notifications>>;
  public readonly alertsReceived$: Observable<View<Notifications>>;
  public readonly alertsCreated$: Observable<View<Notifications>>;

  public readonly investigationLink: string;
  public readonly investigationParams: Record<string, string>;

  public readonly alertLink: string;
  public readonly alertParams: Record<string, string>;

  public partsMetricData: MetricData[];
  public otherPartsMetricData: MetricData[];
  public investigationsMetricData: MetricData[];
  public alertsMetricData: MetricData[];

  constructor(private readonly dashboardFacade: DashboardFacade, private readonly router: Router) {
    this.dashboardStats$ = this.dashboardFacade.dashboardStats$;
    this.investigationsReceived$ = this.dashboardFacade.recentReceivedInvestigations$;
    this.investigationsCreated$ = this.dashboardFacade.recentCreatedInvestigations$;
    this.alertsReceived$ = this.dashboardFacade.recentReceivedAlerts$;
    this.alertsCreated$ = this.dashboardFacade.recentCreatedAlerts$;

    const {
      link: alertLink,
      queryParams: alertQueryParams,
    } = getRoute(NOTIFICATION_BASE_ROUTE, NotificationStatusGroup.QUEUED_AND_REQUESTED);

    this.alertLink = alertLink;
    this.alertParams = alertQueryParams;

    this.partsMetricData = [
      {
        metricName: 'totalAmount',
        value: this.dashboardStats$.pipe(map(dashboardStats => dashboardStats.data.totalOwnParts)),
        metricUnit: 'parts',
      },

    ];

    this.otherPartsMetricData = [
      {
        metricName: 'totalAmount',
        value: this.dashboardStats$.pipe(map(dashboardStats => dashboardStats.data.totalOtherParts)),
        metricUnit: 'parts',
      },
    ];

    this.investigationsMetricData = [
      {
        metricName: 'amountReceived',
        value: this.dashboardStats$.pipe(map(dashboardStats => dashboardStats?.data?.receivedActiveInvestigations)),
        metricUnit: 'investigations',
      },
      {
        metricName: 'amountCreated',
        value: this.dashboardStats$.pipe(map(dashboardStats => dashboardStats?.data?.sentActiveInvestigations)),
        metricUnit: 'investigations',
      },
    ];


    this.alertsMetricData = [
      {
        metricName: 'amountReceived',
        value: this.dashboardStats$.pipe(map(dashboardStats => dashboardStats?.data?.receivedActiveAlerts)),
        metricUnit: 'alerts',
      },
      {
        metricName: 'amountCreated',
        value: this.dashboardStats$.pipe(map(dashboardStats => dashboardStats?.data?.sentActiveAlerts)),
        metricUnit: 'alerts',
      },
    ];

  }

  public ngOnInit(): void {
    this.dashboardFacade.setDashboardData();
  }

  public ngOnDestroy(): void {
    this.dashboardFacade.stopDataLoading();
  }

  public onAlertSelected(notification: Notification): void {
    const { link } = getRoute(NOTIFICATION_BASE_ROUTE);
    this.router.navigate([ `/${ link }/${ notification.id }` ]).then();
  }

  protected readonly TableType = TableType;
}
