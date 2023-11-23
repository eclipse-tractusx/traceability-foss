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

import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { ALERT_BASE_ROUTE, getRoute, INVESTIGATION_BASE_ROUTE } from '@core/known-route';
import { MetricData } from '@page/dashboard/presentation/dashboard.model';
import {
  Notification,
  Notifications,
  NotificationStatusGroup,
  NotificationType,
} from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { CloseNotificationModalComponent } from '@shared/modules/notification/modal/close/close-notification-modal.component';
import { Observable } from 'rxjs';
import { DashboardFacade } from '../abstraction/dashboard.facade';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit, OnDestroy {
  @ViewChild(CloseNotificationModalComponent) private closeModal: CloseNotificationModalComponent;

  public readonly numberOfTotalMyParts$: Observable<View<number>>;
  public readonly numberOfTotalOtherParts$: Observable<View<number>>;
  public readonly numberOfInvestigations$: Observable<View<number>>;

  public readonly numberOfOwnInvestigationsReceived$: Observable<View<number>>;
  public readonly numberOfOwnInvestigationsCreated$: Observable<View<number>>;
  public readonly numberOfOwnAlertsReceived$: Observable<View<number>>;
  public readonly numberOfOwnAlertsCreated$: Observable<View<number>>;

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
  public investigationsCreatedMetricData: MetricData[];
  public alertsMetricData: MetricData[];
  public alertsCreatedMetricData: MetricData[];


  constructor(private readonly dashboardFacade: DashboardFacade, private readonly router: Router) {
    this.numberOfTotalMyParts$ = this.dashboardFacade.numberOfTotalMyParts$;
    this.numberOfTotalOtherParts$ = this.dashboardFacade.numberOfTotalOtherParts$;

    this.numberOfInvestigations$ = this.dashboardFacade.numberOfMyPartsWithOpenInvestigations$;

    this.numberOfOwnInvestigationsReceived$ = this.dashboardFacade.numberOfOwnOpenInvestigationsReceived$;
    this.numberOfOwnInvestigationsCreated$ = this.dashboardFacade.numberOfOwnOpenInvestigationsCreated$;
    this.numberOfOwnAlertsReceived$ = this.dashboardFacade.numberOfOwnOpenAlertsReceived$;
    this.numberOfOwnAlertsCreated$ = this.dashboardFacade.numberOfOwnOpenAlertsCreated$;


    this.investigationsReceived$ = this.dashboardFacade.recentReceivedInvestigations$;
    this.investigationsCreated$ = this.dashboardFacade.recentCreatedInvestigations$;
    this.alertsReceived$ = this.dashboardFacade.recentReceivedAlerts$
    this.alertsCreated$ = this.dashboardFacade.recentCreatedAlerts$

    const {link: investigationLink, queryParams: investigationQueryParams} = getRoute(INVESTIGATION_BASE_ROUTE, NotificationStatusGroup.RECEIVED);
    const {link: alertLink, queryParams: alertQueryParams} = getRoute(ALERT_BASE_ROUTE, NotificationStatusGroup.RECEIVED);
    this.investigationLink = investigationLink;
    this.investigationParams = investigationQueryParams;
    this.alertLink = alertLink;
    this.alertParams = alertQueryParams;

    this.partsMetricData = [
      {
        metricName: 'totalAmount',
        value: this.numberOfTotalMyParts$,
        metricUnit: 'parts'
      }

    ]

    this.otherPartsMetricData = [
      {
        metricName: 'totalAmount',
        value: this.numberOfTotalOtherParts$,
        metricUnit: 'parts'
      }
    ];

    this.investigationsMetricData = [
      {
        metricName: 'amountReceived',
        value: this.numberOfOwnInvestigationsReceived$,
        metricUnit: 'investigations'
      },
      {
        metricName: 'amountCreated',
        value: this.numberOfOwnInvestigationsCreated$,
        metricUnit: 'investigations'
      }
    ];


    this.alertsMetricData = [
      {
        metricName: 'amountReceived',
        value: this.numberOfOwnAlertsReceived$,
        metricUnit: 'alerts'
      },
      {
        metricName: 'amountCreated',
        value: this.numberOfOwnAlertsCreated$,
        metricUnit: 'alerts'
      }
    ];


  }

  public ngOnInit(): void {
    this.dashboardFacade.setDashboardData();
  }

  public ngOnDestroy(): void {
    this.dashboardFacade.stopDataLoading();
  }

  public onInvestigationSelected(notification: Notification): void {
    const { link } = getRoute(INVESTIGATION_BASE_ROUTE);
    this.router.navigate([`/${link}/${notification.id}`]).then();
  }

  public onAlertSelected(notification: Notification): void {
    const { link } = getRoute(ALERT_BASE_ROUTE);
    this.router.navigate([`/${link}/${notification.id}`]).then();
  }

    protected readonly NotificationType = NotificationType;
}
