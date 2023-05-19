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

import { Component } from '@angular/core';
import { AlertsFacade } from '@page/alerts/core/alerts.facade';
import { MenuActionConfig, TablePaginationEventConfig } from '@shared/components/table/table.model';
import { Notification } from '@shared/model/notification.model';

@Component({
  selector: 'app-alerts',
  templateUrl: './alerts.component.html',
})
export class AlertsComponent {

  public readonly alertsReceived$;
  public readonly alertsQueuedAndRequested$;

  public menuActionsConfig: MenuActionConfig<Notification>[];

  private pagination: TablePaginationEventConfig = { page: 0, pageSize: 5 };

  constructor(
    private readonly alertsFacade: AlertsFacade
  ) {
    this.alertsReceived$ = this.alertsFacade.alertsReceived$;
    this.alertsQueuedAndRequested$ = this.alertsFacade.alertsQueuedAndRequested$;
  }

  public ngOnInit(): void {
    this.alertsFacade.setReceivedAlerts(this.pagination.page, this.pagination.pageSize);
    this.alertsFacade.setQueuedAndRequestedAlerts(this.pagination.page, this.pagination.pageSize);
  }

  public ngAfterContentInit(): void {
    this.menuActionsConfig = [];
  }

  public ngOnDestroy(): void {
    this.alertsFacade.stopAlerts();
  }

  public onReceivedPagination(pagination: TablePaginationEventConfig) {
    this.pagination = pagination;
    this.alertsFacade.setReceivedAlerts(this.pagination.page, this.pagination.pageSize);
  }

  public onQueuedAndRequestedPagination(pagination: TablePaginationEventConfig) {
    this.pagination = pagination;
    this.alertsFacade.setQueuedAndRequestedAlerts(this.pagination.page, this.pagination.pageSize);
  }
}
