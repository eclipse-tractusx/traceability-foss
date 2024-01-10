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

import { ChangeDetectorRef, Component, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { getRoute, INVESTIGATION_BASE_ROUTE } from '@core/known-route';
import { InvestigationDetailFacade } from '@page/investigations/core/investigation-detail.facade';
import { InvestigationHelperService } from '@page/investigations/core/investigation-helper.service';
import { NotificationActionHelperService } from '@shared/assembler/notification-action-helper.service';
import { NotificationMenuActionsAssembler } from '@shared/assembler/notificationMenuActions.assembler';
import { NotificationChannel } from '@shared/components/multi-select-autocomplete/table-type.model';
import { NotificationCommonModalComponent } from '@shared/components/notification-common-modal/notification-common-modal.component';
import { TableSortingUtil } from '@shared/components/table/table-sorting.util';
import { MenuActionConfig, TableEventConfig, TableHeaderSort } from '@shared/components/table/table.model';
import { createDeeplinkNotificationFilter } from '@shared/helper/notification-helper';
import { NotificationTabInformation } from '@shared/model/notification-tab-information';
import {
  Notification,
  NotificationFilter,
  NotificationStatusGroup,
  NotificationType,
} from '@shared/model/notification.model';
import { TranslationContext } from '@shared/model/translation-context.model';
import { Subscription } from 'rxjs';
import { InvestigationsFacade } from '../core/investigations.facade';

@Component({
  selector: 'app-investigations',
  templateUrl: './investigations.component.html',
})

export class InvestigationsComponent {
  @ViewChild(NotificationCommonModalComponent) notificationCommonModalComponent: NotificationCommonModalComponent;

  public readonly investigationsReceived$;
  public readonly investigationsQueuedAndRequested$;

  public menuActionsConfig: MenuActionConfig<Notification>[];

  public isInvestigation = true;
  public investigationReceivedSortList: TableHeaderSort[] = [];
  public investigationQueuedAndRequestedSortList: TableHeaderSort[] = [];
  private ctrlKeyState: boolean = false;

  private paramSubscription: Subscription;

  receivedFilter: NotificationFilter;
  requestedFilter: NotificationFilter;

  private pagination: TableEventConfig = { page: 0, pageSize: 50, sorting: [ 'createdDate', 'desc' ] };

  constructor(
    public readonly helperService: InvestigationHelperService,
    private readonly actionHelperService: NotificationActionHelperService,
    public readonly investigationsFacade: InvestigationsFacade,
    private readonly investigationDetailFacade: InvestigationDetailFacade,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly cd: ChangeDetectorRef,
  ) {
    this.investigationsReceived$ = this.investigationsFacade.investigationsReceived$;
    this.investigationsQueuedAndRequested$ = this.investigationsFacade.investigationsQueuedAndRequested$;

    window.addEventListener('keydown', (event) => {
      this.ctrlKeyState = event.ctrlKey;
    });
    window.addEventListener('keyup', (event) => {
      this.ctrlKeyState = event.ctrlKey;
    });

  }

  public ngOnInit(): void {
    this.paramSubscription = this.route.queryParams.subscribe(params => {
      this.pagination.page = params?.pageNumber ? params.pageNumber : 0;
      let deeplinkNotificationFilter = createDeeplinkNotificationFilter(params);
      this.investigationsFacade.setReceivedInvestigation(this.pagination.page, this.pagination.pageSize, this.investigationReceivedSortList, deeplinkNotificationFilter?.receivedFilter, this.receivedFilter /*Filter */);
      this.investigationsFacade.setQueuedAndRequestedInvestigations(this.pagination.page, this.pagination.pageSize, this.investigationQueuedAndRequestedSortList, deeplinkNotificationFilter?.sentFilter, this.requestedFilter);
    });
  }

  public ngAfterViewInit(): void {
    this.menuActionsConfig = NotificationMenuActionsAssembler.getMenuActions(this.actionHelperService, this.notificationCommonModalComponent);
    this.cd.detectChanges();
  }

  public ngOnDestroy(): void {
    this.investigationsFacade.stopInvestigations();
    this.paramSubscription?.unsubscribe();
  }

  public onReceivedTableConfigChanged(pagination: TableEventConfig) {
    this.pagination = pagination;
    this.setTableSortingList(pagination.sorting, NotificationStatusGroup.RECEIVED);
    this.investigationsFacade.setReceivedInvestigation(this.pagination.page, this.pagination.pageSize, this.investigationReceivedSortList, null, this.receivedFilter );
  }

  public onQueuedAndRequestedTableConfigChanged(pagination: TableEventConfig) {
    this.pagination = pagination;
    this.setTableSortingList(pagination.sorting, NotificationStatusGroup.QUEUED_AND_REQUESTED);
    this.investigationsFacade.setQueuedAndRequestedInvestigations(this.pagination.page, this.pagination.pageSize, this.investigationQueuedAndRequestedSortList, null, this.requestedFilter);
  }

  public openDetailPage(notification: Notification): void {
    this.investigationDetailFacade.selected = { data: notification };
    const { link } = getRoute(INVESTIGATION_BASE_ROUTE);
    const tabIndex = this.route.snapshot.queryParamMap.get('tabIndex');
    const tabInformation: NotificationTabInformation = { tabIndex: tabIndex, pageNumber: this.pagination.page };
    this.router.navigate([ `/${ link }/${ notification.id }` ], { queryParams: tabInformation });
  }

  public handleConfirmActionCompletedEvent() {
    this.ngOnInit();
  }

  private setTableSortingList(sorting: TableHeaderSort, notificationTable: NotificationStatusGroup): void {
    const tableSortList = notificationTable === NotificationStatusGroup.RECEIVED ?
      this.investigationReceivedSortList : this.investigationQueuedAndRequestedSortList;
    TableSortingUtil.setTableSortingList(sorting, tableSortList, this.ctrlKeyState);
  }

  protected readonly TranslationContext = TranslationContext;
  protected readonly NotificationType = NotificationType;

  filterNotifications(filterContext: any) {
    if(filterContext.channel === NotificationChannel.RECEIVER) {
      this.receivedFilter = filterContext.filter;
    } else {
      this.requestedFilter = filterContext.filter;
    }

    if(filterContext.channel === NotificationChannel.RECEIVER) {
      this.investigationsFacade.setReceivedInvestigation(this.pagination.page, this.pagination.pageSize, this.investigationReceivedSortList, null, this.receivedFilter /*Filter */);

    } else {
      this.investigationsFacade.setQueuedAndRequestedInvestigations(this.pagination.page, this.pagination.pageSize, this.investigationQueuedAndRequestedSortList, null, this.requestedFilter);

    }
  }
}
