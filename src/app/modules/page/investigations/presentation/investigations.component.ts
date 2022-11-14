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

import { AfterContentInit, AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { InvestigationDetailFacade } from '@page/investigations/core/investigation-detail.facade';
import { getInvestigationInboxRoute } from '@page/investigations/investigations-external-route';
import { MenuActionConfig, TablePaginationEventConfig } from '@shared/components/table/table.model';
import { Notification } from '@shared/model/notification.model';
import { CloseNotificationModalComponent } from '@shared/modules/notification/modal/close/close-notification-modal.component';
import { Observable } from 'rxjs';
import { InvestigationsFacade } from '../core/investigations.facade';
import { ApproveNotificationModalComponent } from '@shared/modules/notification/modal/approve/approve-notification-modal.component';
import { DeleteNotificationModalComponent } from '@shared/modules/notification/modal/delete/delete-notification-modal.component';

@Component({
  selector: 'app-investigations',
  templateUrl: './investigations.component.html',
})
export class InvestigationsComponent implements OnInit, OnDestroy, AfterContentInit {
  @ViewChild(CloseNotificationModalComponent) private closeModal: CloseNotificationModalComponent;
  @ViewChild(ApproveNotificationModalComponent) private approveModal: ApproveNotificationModalComponent;
  @ViewChild(DeleteNotificationModalComponent) private deleteModal: DeleteNotificationModalComponent;

  public readonly investigationsReceived$;
  public readonly investigationsQueuedAndRequested$;

  public menuActionsConfig: [MenuActionConfig[], MenuActionConfig[]];

  constructor(
    private readonly investigationsFacade: InvestigationsFacade,
    private readonly investigationDetailFacade: InvestigationDetailFacade,
    private readonly router: Router,
  ) {
    this.investigationsReceived$ = this.investigationsFacade.investigationsReceived$;
    this.investigationsQueuedAndRequested$ = this.investigationsFacade.investigationsQueuedAndRequested$;
  }

  public ngOnInit(): void {
    this.investigationsFacade.setReceivedInvestigation();
    this.investigationsFacade.setQueuedAndRequestedInvestigations();
  }

  public ngAfterContentInit(): void {
    this.menuActionsConfig = [
      [{ label: 'actions.close', icon: 'close', action: (data: Notification) => this.closeModal.show(data) }],
      [
        { label: 'actions.approve', icon: 'share', action: (data: Notification) => this.approveModal.show(data) },
        { label: 'actions.delete', icon: 'delete', action: (data: Notification) => this.deleteModal.show(data) },
      ],
    ];
  }

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

  public approveInvestigation(id: string): Observable<void> {
    return this.investigationsFacade.approveInvestigation(id);
  }

  public cancelInvestigation(id: string): Observable<void> {
    return this.investigationsFacade.cancelInvestigation(id);
  }

  public closeInvestigation(id: string, reason: string): Observable<void> {
    return this.investigationsFacade.closeInvestigation(id, reason);
  }
}
