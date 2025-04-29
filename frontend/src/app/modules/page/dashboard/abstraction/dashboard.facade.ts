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

import { Injectable } from '@angular/core';

import { NotificationChannel } from '@shared/components/multi-select-autocomplete/table-type.model';
import { FilterOperator } from '@shared/model/filter.model';
import { Notifications } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { NotificationService } from '@shared/service/notification.service';
import { Observable, Subscription } from 'rxjs';
import { DashboardService } from '../core/dashboard.service';
import { DashboardState } from '../core/dashboard.state';
import { DashboardStats } from '../model/dashboard.model';

@Injectable()
export class DashboardFacade {
  private dashboardStatsSubscription: Subscription;
  private investigationsReceivedSubscription: Subscription;
  private investigationsCreatedSubscription: Subscription;
  private alertsReceivedSubscription: Subscription;
  private alertsCreatedSubscription: Subscription;


  constructor(
    private readonly dashboardService: DashboardService,
    private readonly notificationService: NotificationService,
    private readonly dashboardState: DashboardState,
  ) {
  }

  public get dashboardStats$(): Observable<View<DashboardStats>> {
    return this.dashboardState.dashboardStats$;
  }

  public get recentReceivedInvestigations$(): Observable<View<Notifications>> {
    return this.dashboardState.recentReceivedInvestigations$;
  }

  public get recentCreatedInvestigations$(): Observable<View<Notifications>> {
    return this.dashboardState.recentCreatedInvestigations$;
  }

  public get recentReceivedAlerts$(): Observable<View<Notifications>> {
    return this.dashboardState.recentReceivedAlerts$;
  }

  public get recentCreatedAlerts$(): Observable<View<Notifications>> {
    return this.dashboardState.recentCreatedAlerts$;
  }

  public setDashboardData(): void {
    this.setDashboardMetricData();
    this.setReceivedInvestigations();
    this.setCreatedInvestigations();
    this.setReceivedAlerts();
    this.setCreatedAlerts();
  }

  public stopDataLoading(): void {
    this.dashboardStatsSubscription?.unsubscribe();
    this.investigationsReceivedSubscription?.unsubscribe();
    this.investigationsCreatedSubscription?.unsubscribe();
    this.alertsReceivedSubscription?.unsubscribe();
    this.alertsCreatedSubscription?.unsubscribe();
  }

  private setDashboardMetricData(): void {
    this.dashboardState.setDashboardStats({ loader: true });

    this.dashboardStatsSubscription?.unsubscribe();
    this.dashboardStatsSubscription = this.dashboardService.getStats().subscribe({
      next: (dashboardStats: DashboardStats) => {
        this.dashboardState.setDashboardStats({ data: dashboardStats });

      },
      error: error => {
        this.dashboardState.setDashboardStats({ error });
      },
    });
  }

  private setReceivedInvestigations(): void {
    this.investigationsReceivedSubscription?.unsubscribe();
    this.investigationsReceivedSubscription = this.notificationService.getNotifications(0, 5, [ [ 'createdDate', 'desc' ] ], {
      channel: {
        value: [ {
          value: NotificationChannel.RECEIVER,
          strategy: FilterOperator.EQUAL,
        } ],
        operator: 'OR',
      },
    }).subscribe({
      next: data => this.dashboardState.setRecentReceivedInvestigations({ data }),
      error: (error: Error) => this.dashboardState.setRecentReceivedInvestigations({ error }),
    });
  }

  private setCreatedInvestigations(): void {
    this.investigationsCreatedSubscription?.unsubscribe();
    this.investigationsCreatedSubscription = this.notificationService.getNotifications(0, 5, [ [ 'createdDate', 'desc' ] ], {
      channel: {
        value: [ {
          value: NotificationChannel.SENDER,
          strategy: FilterOperator.EQUAL,
        } ],
        operator: 'OR',
      },
    }).subscribe({
      next: data => this.dashboardState.setRecentCreatedInvestigations({ data }),
      error: (error: Error) => this.dashboardState.setRecentCreatedInvestigations({ error }),
    });
  }

  private setReceivedAlerts(): void {
    this.alertsReceivedSubscription?.unsubscribe();
    this.alertsReceivedSubscription = this.notificationService.getNotifications(0, 5, [ [ 'createdDate', 'desc' ] ],{
      channel: {
        value: [ {
          value: NotificationChannel.RECEIVER,
          strategy: FilterOperator.EQUAL,
        } ],
        operator: 'OR',
      },
    }).subscribe({
      next: data => this.dashboardState.setRecentReceivedAlerts({ data }),
      error: (error: Error) => this.dashboardState.setRecentReceivedAlerts({ error }),
    });
  }


  private setCreatedAlerts(): void {
    this.alertsCreatedSubscription?.unsubscribe();
    this.alertsCreatedSubscription = this.notificationService.getNotifications(0, 5, [ [ 'createdDate', 'desc' ] ], {
      channel: {
        value: [ {
          value: NotificationChannel.SENDER,
          strategy: FilterOperator.EQUAL,
        } ],
        operator: 'OR',
      },
    }).subscribe({
      next: data => this.dashboardState.setRecentCreatedAlerts({ data }),
      error: (error: Error) => this.dashboardState.setRecentCreatedAlerts({ error }),
    });
  }
}
