/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { InvestigationDetailFacade } from '@page/investigations/core/investigation-detail.facade';
import { getInvestigationInboxRoute } from '@page/investigations/investigations-external-route';
import { MenuActionConfig, TablePaginationEventConfig } from '@shared/components/table/table.model';
import { Notification } from '@shared/model/notification.model';
import { CloseNotificationModalComponent } from '@shared/modules/notification/modal/close/close-notification-modal.component';
import { InvestigationsFacade } from '../core/investigations.facade';

@Component({
  selector: 'app-investigations',
  templateUrl: './investigations.component.html',
})
export class InvestigationsComponent implements OnInit, OnDestroy, AfterViewInit {
  @ViewChild(CloseNotificationModalComponent)
  private modalClose!: CloseNotificationModalComponent;

  public readonly investigationsReceived$ = this.investigationsFacade.investigationsReceived$;
  public readonly investigationsQueuedAndRequested$ = this.investigationsFacade.investigationsQueuedAndRequested$;

  public menuActionsConfig: MenuActionConfig[];

  constructor(
    private readonly investigationsFacade: InvestigationsFacade,
    private readonly investigationDetailFacade: InvestigationDetailFacade,
    private readonly router: Router,
  ) {
    this.menuActionsConfig = [
      { label: 'actions.approve', icon: 'share', action: null },
      { label: 'actions.delete', icon: 'delete', action: null },
      { label: 'actions.close', icon: 'close', action: this.closeNotification.bind(this) },
    ];
  }

  public ngOnInit(): void {
    this.investigationsFacade.setReceivedInvestigation();
    this.investigationsFacade.setQueuedAndRequestedInvestigations();
  }

  public ngAfterViewInit(): void {}

  public ngOnDestroy(): void {
    this.investigationsFacade.stopInvestigations();
  }

  public onReceivedPagination(pagination: TablePaginationEventConfig) {
    this.investigationsFacade.setReceivedInvestigation(pagination.page, pagination.pageSize);
  }

  public onQueuedAndRequestedPagination(pagination: TablePaginationEventConfig) {
    this.investigationsFacade.setQueuedAndRequestedInvestigations(pagination.page, pagination.pageSize);
  }

  public openDetailPage(notification: Notification): void {
    this.investigationDetailFacade.selected = { data: notification };
    const { link } = getInvestigationInboxRoute();
    this.router.navigate([`/${link}/${notification.id}`]).then();
  }

  private closeNotification(notification: Notification): void {
    this.modalClose.closeAction(notification);
  }
}
