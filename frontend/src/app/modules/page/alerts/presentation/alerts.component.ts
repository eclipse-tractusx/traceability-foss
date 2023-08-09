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

import { Component, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ALERT_BASE_ROUTE, getRoute } from '@core/known-route';
import { AlertDetailFacade } from '@page/alerts/core/alert-detail.facade';
import { AlertHelperService } from '@page/alerts/core/alert-helper.service';
import { AlertsFacade } from '@page/alerts/core/alerts.facade';
import { NotificationCommonModalComponent } from '@shared/components/notification-common-modal/notification-common-modal.component';
import { MenuActionConfig, TableEventConfig } from '@shared/components/table/table.model';
import { NotificationTabInformation } from '@shared/model/notification-tab-information';
import { Notification } from '@shared/model/notification.model';
import { TranslationContext } from '@shared/model/translation-context.model';
import { Subscription } from 'rxjs';
import {NotificationMenuActionsAssembler} from "@shared/assembler/notificationMenuActions.assembler";

@Component({
  selector: 'app-alerts',
  templateUrl: './alerts.component.html',
})
export class AlertsComponent {
  @ViewChild(NotificationCommonModalComponent) notificationCommonModalComponent: NotificationCommonModalComponent;


  public readonly alertsReceived$;
  public readonly alertsQueuedAndRequested$;

  public menuActionsConfig: MenuActionConfig<Notification>[];

  private paramSubscription: Subscription;

  private pagination: TableEventConfig = { page: 0, pageSize: 50, sorting: ['createdDate' , 'desc'] };

  constructor(
    public readonly helperService: AlertHelperService,
    private readonly alertsFacade: AlertsFacade,
    private readonly alertDetailFacade: AlertDetailFacade,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {
    this.alertsReceived$ = this.alertsFacade.alertsReceived$;
    this.alertsQueuedAndRequested$ = this.alertsFacade.alertsQueuedAndRequested$;
  }

  public ngOnInit(): void {
    this.paramSubscription = this.route.queryParams.subscribe(params => {
      this.pagination.page = params?.pageNumber;
      this.alertsFacade.setReceivedAlerts(this.pagination.page, this.pagination.pageSize, this.pagination.sorting);
      this.alertsFacade.setQueuedAndRequestedAlerts(this.pagination.page, this.pagination.pageSize, this.pagination.sorting);
    })
  }

  public ngAfterViewChecked(): void {
    this.menuActionsConfig = NotificationMenuActionsAssembler.getMenuActions(this.helperService, this.notificationCommonModalComponent)
  }

  public ngOnDestroy(): void {
    this.alertsFacade.stopAlerts();
    this.paramSubscription?.unsubscribe();
  }

  public onReceivedTableConfigChange(pagination: TableEventConfig) {
    this.pagination = pagination;
    this.alertsFacade.setReceivedAlerts(this.pagination.page, this.pagination.pageSize, this.pagination.sorting);
  }

  public onQueuedAndRequestedTableConfigChange(pagination: TableEventConfig) {
    this.pagination = pagination;
    this.alertsFacade.setQueuedAndRequestedAlerts(this.pagination.page, this.pagination.pageSize, this.pagination.sorting);
  }

  public openDetailPage(notification: Notification): void {
    this.alertDetailFacade.selected = { data: notification };
    const { link } = getRoute(ALERT_BASE_ROUTE);
    const tabIndex = this.route.snapshot.queryParamMap.get('tabIndex');
    const tabInformation: NotificationTabInformation = {tabIndex: tabIndex, pageNumber: this.pagination.page}
    this.router.navigate([`/${link}/${notification.id}`], { queryParams: tabInformation });
  }

  public handleConfirmActionCompletedEvent() {
    this.ngOnInit();
  }

  protected readonly TranslationContext = TranslationContext;
}
