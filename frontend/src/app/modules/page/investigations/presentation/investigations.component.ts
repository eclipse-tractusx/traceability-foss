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

import { AfterContentInit, Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { getRoute, INVESTIGATION_BASE_ROUTE } from '@core/known-route';
import { InvestigationDetailFacade } from '@page/investigations/core/investigation-detail.facade';
import { InvestigationHelperService } from '@page/investigations/core/investigation-helper.service';
import { NotificationCommonModalComponent } from '@shared/components/notification-common-modal/notification-common-modal.component';
import { MenuActionConfig, TableEventConfig } from '@shared/components/table/table.model';
import { NotificationTabInformation } from '@shared/model/notification-tab-information';
import { Notification } from '@shared/model/notification.model';
import { TranslationContext } from '@shared/model/translation-context.model';
import { Subscription } from 'rxjs';
import { InvestigationsFacade } from '../core/investigations.facade';

@Component({
  selector: 'app-investigations',
  templateUrl: './investigations.component.html',
})
export class InvestigationsComponent implements OnInit, OnDestroy, AfterContentInit {
  @ViewChild(NotificationCommonModalComponent) notificationCommonModalComponent: NotificationCommonModalComponent;

  public readonly investigationsReceived$;
  public readonly investigationsQueuedAndRequested$;

  public menuActionsConfig: MenuActionConfig<Notification>[];

  private paramSubscription: Subscription;

  private pagination: TableEventConfig = { page: 0, pageSize: 50, sorting: ['createdDate' , 'desc']  };

  constructor(
    public readonly helperService: InvestigationHelperService,
    public readonly investigationsFacade: InvestigationsFacade,
    private readonly investigationDetailFacade: InvestigationDetailFacade,
    private readonly router: Router,
    private readonly route: ActivatedRoute
  ) {
    this.investigationsReceived$ = this.investigationsFacade.investigationsReceived$;
    this.investigationsQueuedAndRequested$ = this.investigationsFacade.investigationsQueuedAndRequested$;
  }

  public ngOnInit(): void {
    this.paramSubscription = this.route.queryParams.subscribe(params => {
      this.pagination.page = params?.pageNumber;
      this.investigationsFacade.setReceivedInvestigation(this.pagination.page, this.pagination.pageSize, this.pagination.sorting);
      this.investigationsFacade.setQueuedAndRequestedInvestigations(this.pagination.page, this.pagination.pageSize, this.pagination.sorting);
    })
  }

  public ngAfterContentInit(): void {
    this.menuActionsConfig = [
      {
        label: 'actions.close',
        icon: 'close',
        action: data => this.notificationCommonModalComponent.show('close', data),
        condition: data => this.helperService.showCloseButton(data),
      },
      {
        label: 'actions.approve',
        icon: 'share',
        action: data => this.notificationCommonModalComponent.show('approve',data),
        condition: data => this.helperService.showApproveButton(data),
      },
      {
        label: 'actions.cancel',
        icon: 'cancel',
        action: data => this.notificationCommonModalComponent.show('cancel',data),
        condition: data => this.helperService.showCancelButton(data),
      },
      {
        label: 'actions.acknowledge',
        icon: 'work',
        action: data => this.notificationCommonModalComponent.show('acknowledge', data),
        condition: data => this.helperService.showAcknowledgeButton(data),
      },
      {
        label: 'actions.accept',
        icon: 'assignment_turned_in',
        action: data => this.notificationCommonModalComponent.show('accept',data),
        condition: data => this.helperService.showAcceptButton(data),
      },
      {
        label: 'actions.decline',
        icon: 'assignment_late',
        action: data => this.notificationCommonModalComponent.show('decline', data),
        condition: data => this.helperService.showDeclineButton(data),
      },
    ];
  }

  public ngOnDestroy(): void {
    this.investigationsFacade.stopInvestigations();
    this.paramSubscription?.unsubscribe();
  }

  public onReceivedTableConfigChanged(pagination: TableEventConfig) {
    this.pagination = pagination;
    this.investigationsFacade.setReceivedInvestigation(this.pagination.page, this.pagination.pageSize, this.pagination.sorting);
  }

  public onQueuedAndRequestedTableConfigChanged(pagination: TableEventConfig) {
    this.pagination = pagination;
    this.investigationsFacade.setQueuedAndRequestedInvestigations(this.pagination.page, this.pagination.pageSize, this.pagination.sorting);
  }

  public openDetailPage(notification: Notification): void {
    this.investigationDetailFacade.selected = { data: notification };
    const { link } = getRoute(INVESTIGATION_BASE_ROUTE);
    const tabIndex = this.route.snapshot.queryParamMap.get('tabIndex');
    const tabInformation: NotificationTabInformation = {tabIndex: tabIndex, pageNumber: this.pagination.page}
    this.router.navigate([`/${link}/${notification.id}`], { queryParams: tabInformation });
  }

  public handleConfirmActionCompletedEvent() {
    this.ngOnInit();
  }

  protected readonly TranslationContext = TranslationContext;
}
