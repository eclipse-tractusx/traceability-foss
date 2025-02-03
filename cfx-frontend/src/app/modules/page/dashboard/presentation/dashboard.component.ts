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
import { Notification, Notifications, NotificationStatusGroup } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { CloseNotificationModalComponent } from '@shared/modules/notification/modal/close/close-notification-modal.component';
import { Observable } from 'rxjs';
import { DashboardFacade } from '../abstraction/dashboard.facade';
import { Role } from '@core/user/role.model';
import { MatDialog } from '@angular/material/dialog';
import { RequestStepperComponent } from '@shared/components/request-notification/request-stepper/request-stepper.component';
import { RequestContext } from '@shared/components/request-notification/request-notification.base';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit, OnDestroy {
  @ViewChild(CloseNotificationModalComponent) private closeModal: CloseNotificationModalComponent;

  public readonly numberOfMyParts$: Observable<View<number>>;
  public readonly numberOfOtherParts$: Observable<View<number>>;
  public readonly numberOfInvestigations$: Observable<View<number>>;
  public readonly numberOfAlerts$: Observable<View<number>>;

  public readonly investigations$: Observable<View<Notifications>>;
  public readonly investigationLink: string;
  public readonly investigationParams: Record<string, string>;

  public readonly alerts$: Observable<View<Notifications>>;
  public readonly alertsLink: string;
  public readonly alertsParams: Record<string, string>;

  protected readonly TableType = TableType;
  protected readonly Role = Role;

  constructor(private readonly dashboardFacade: DashboardFacade,
    private readonly router: Router,
    public dialog: MatDialog,
  ) {
    this.numberOfMyParts$ = this.dashboardFacade.numberOfMyParts$;
    this.numberOfOtherParts$ = this.dashboardFacade.numberOfOtherParts$;
    this.numberOfInvestigations$ = this.dashboardFacade.numberOfInvestigations$;
    this.numberOfAlerts$ = this.dashboardFacade.numberOfAlerts$;

    this.investigations$ = this.dashboardFacade.investigations$;
    this.alerts$ = this.dashboardFacade.alerts$;

    const investigationRoute = getRoute(INVESTIGATION_BASE_ROUTE, NotificationStatusGroup.RECEIVED);
    this.investigationLink = investigationRoute.link;
    this.investigationParams = investigationRoute.queryParams;

    const alertsRoute = getRoute(ALERT_BASE_ROUTE, NotificationStatusGroup.RECEIVED);
    this.alertsLink = alertsRoute.link;
    this.alertsParams = alertsRoute.queryParams;
  }

  public ngOnInit(): void {
    this.dashboardFacade.setDashboardData();
  }

  public ngOnDestroy(): void {
    this.dashboardFacade.stopDataLoading();
  }

  public onNotificationSelected(notification: Notification, isInvestigation: boolean): void {
    const { link } = getRoute(isInvestigation ? INVESTIGATION_BASE_ROUTE : ALERT_BASE_ROUTE);
    this.router.navigate([`/${link}/${notification.id}`]).then();
  }

  public openRequestDialog(isInvestigation: boolean): void {
    this.dialog.open(RequestStepperComponent, {
      autoFocus: false,
      disableClose: true,
      data: {
        context: isInvestigation ? RequestContext.REQUEST_INVESTIGATION : RequestContext.REQUEST_ALERT,
      }
    });
  }
}
