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

import { ChangeDetectorRef, Component, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { ALERT_BASE_ROUTE, getRoute } from '@core/known-route';
import { Role } from '@core/user/role.model';
import { AlertDetailFacade } from '@page/alerts/core/alert-detail.facade';
import { AlertHelperService } from '@page/alerts/core/alert-helper.service';
import { AlertsFacade } from '@page/alerts/core/alerts.facade';
import { NotificationActionHelperService } from '@shared/assembler/notification-action-helper.service';
import { NotificationMenuActionsAssembler } from '@shared/assembler/notificationMenuActions.assembler';
import { NotificationChannel, TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { NotificationCommonModalComponent } from '@shared/components/notification-common-modal/notification-common-modal.component';
import { RequestContext } from '@shared/components/request-notification/request-notification.base';
import { RequestStepperComponent } from '@shared/components/request-notification/request-stepper/request-stepper.component';
import {
  FilterMethod,
  MenuActionConfig,
  TableEventConfig,
  TableFilter,
  TableHeaderSort,
} from '@shared/components/table/table.model';
import { TableSortingUtil } from '@shared/components/table/tableSortingUtil';
import { ToastService } from '@shared/components/toasts/toast.service';
import { createDeeplinkNotificationFilter } from '@shared/helper/notification-helper';
import { SearchHelper } from '@shared/helper/search-helper';
import { FilterConfigOptions } from '@shared/model/filter-config';
import { NotificationTabInformation } from '@shared/model/notification-tab-information';
import {
  Notification,
  NotificationFilter,
  NotificationStatusGroup,
  NotificationType,
} from '@shared/model/notification.model';
import { TranslationContext } from '@shared/model/translation-context.model';
import { NotificationComponent } from '@shared/modules/notification/presentation/notification.component';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-alerts',
  templateUrl: './alerts.component.html',
  styleUrls: ['./alerts.component.scss'],
})
export class AlertsComponent {
  @ViewChild(NotificationCommonModalComponent) notificationCommonModalComponent: NotificationCommonModalComponent;
  @ViewChild(NotificationComponent) notificationComponent: NotificationComponent;

  public searchFormGroup = new FormGroup({});
  public searchControl: FormControl;
  public readonly alertsReceived$;
  public readonly alertsQueuedAndRequested$;

  public menuActionsConfig: MenuActionConfig<Notification>[];

  public alertReceivedSortList: TableHeaderSort[] = [];
  public alertQueuedAndRequestedSortList: TableHeaderSort[] = [];
  public filterReceived: TableFilter = { filterMethod: FilterMethod.AND };
  public filterQueuedAndRequested: TableFilter = { filterMethod: FilterMethod.AND };
  public readonly filterConfigOptions = new FilterConfigOptions();

  public alertsReceivedFilterConfiguration: any[];

  public alertsQueuedAndRequestedFilterConfiguration: any[];

  public receivedFilter: NotificationFilter;
  public requestedFilter: NotificationFilter;

  private ctrlKeyState = false;
  public DEFAULT_PAGE_SIZE = 50;

  public readonly searchHelper = new SearchHelper();
  private paramSubscription: Subscription;

  private pagination: TableEventConfig = {
    page: 0,
    pageSize: this.DEFAULT_PAGE_SIZE,
    sorting: ['createdDate', 'desc'],
  };

  protected readonly TableType = TableType;
  protected readonly Role = Role;
  protected readonly NotificationType = NotificationType;

  constructor(
    public readonly helperService: AlertHelperService,
    public readonly alertsFacade: AlertsFacade,
    private readonly actionHelperService: NotificationActionHelperService,
    private readonly alertDetailFacade: AlertDetailFacade,
    public dialog: MatDialog,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly cd: ChangeDetectorRef,
    public toastService: ToastService,
  ) {
    this.alertsReceived$ = this.alertsFacade.alertsReceived$;
    this.alertsQueuedAndRequested$ = this.alertsFacade.alertsQueuedAndRequested$;

    window.addEventListener('keydown', event => {
      this.ctrlKeyState = event.ctrlKey;
    });
    window.addEventListener('keyup', event => {
      this.ctrlKeyState = event.ctrlKey;
    });
  }

  public ngOnInit(): void {
    this.paramSubscription = this.route.queryParams.subscribe(params => {
      const deeplinkNotificationFilter = createDeeplinkNotificationFilter(params);
      this.pagination.page = params?.pageNumber ? params.pageNumber : 0;
      this.alertsFacade.setReceivedAlerts(this.pagination.page, this.pagination.pageSize, this.alertReceivedSortList, deeplinkNotificationFilter?.receivedFilter, this.receivedFilter);
      this.alertsFacade.setQueuedAndRequestedAlerts(this.pagination.page, this.pagination.pageSize, this.alertQueuedAndRequestedSortList, deeplinkNotificationFilter?.sentFilter, this.requestedFilter);
    });

    this.searchFormGroup.addControl('alertSearch', new FormControl([]));
    this.searchControl = this.searchFormGroup.get('alertSearch') as unknown as FormControl;
  }

  public ngAfterViewInit(): void {
    this.menuActionsConfig = NotificationMenuActionsAssembler.getMenuActions(
      this.actionHelperService,
      this.notificationCommonModalComponent,
    );
    this.cd.detectChanges();
  }

  public ngOnDestroy(): void {
    this.alertsFacade.stopAlerts();
    this.paramSubscription?.unsubscribe();
  }

  public onReceivedTableConfigChange(pagination: TableEventConfig) {
    this.pagination = pagination;
    if (this.pagination.pageSize === 0) {
      this.pagination.pageSize = this.DEFAULT_PAGE_SIZE;
    }
    this.setTableSortingList(pagination.sorting, NotificationStatusGroup.RECEIVED);
    if (pagination.filtering && Object.keys(pagination.filtering).length > 1) {
      this.filterReceived = pagination.filtering;
    }
    this.alertsFacade.setReceivedAlerts(
      this.pagination.page,
      this.pagination.pageSize,
      this.alertReceivedSortList,
      null,
      this.receivedFilter,
    );
  }

  public onQueuedAndRequestedTableConfigChange(pagination: TableEventConfig) {
    this.pagination = pagination;
    if (this.pagination.pageSize === 0) {
      this.pagination.pageSize = this.DEFAULT_PAGE_SIZE;
    }
    this.setTableSortingList(pagination.sorting, NotificationStatusGroup.QUEUED_AND_REQUESTED);
    if (pagination.filtering && Object.keys(pagination.filtering).length > 1) {
      this.filterQueuedAndRequested = pagination.filtering;
    }
    this.alertsFacade.setQueuedAndRequestedAlerts(
      this.pagination.page,
      this.pagination.pageSize,
      this.alertQueuedAndRequestedSortList,
      null,
      this.requestedFilter,
    );
  }

  public onDefaultPaginationSizeChange(pageSize: number) {
    this.DEFAULT_PAGE_SIZE = pageSize;
  }

  public openDetailPage(notification: Notification): void {
    this.alertDetailFacade.selected = { data: notification };
    const { link } = getRoute(ALERT_BASE_ROUTE);
    const tabIndex = this.route.snapshot.queryParamMap.get('tabIndex');
    const tabInformation: NotificationTabInformation = { tabIndex: tabIndex, pageNumber: this.pagination.page };
    this.router.navigate([`/${link}/${notification.id}`], { queryParams: tabInformation });
  }

  public openRequestDialog(): void {
    this.dialog.open(RequestStepperComponent, {
      autoFocus: false,
      disableClose: true,
      data: {
        context: RequestContext.REQUEST_ALERT,
      },
    });
  }

  public handleConfirmActionCompletedEvent() {
    this.ngOnInit();
  }

  public triggerSearch(): void {
    this.searchHelper.resetFilterAndShowToast(false, this.notificationComponent, this.toastService);
    const searchValue = this.searchControl.value;

    const receivedFilter = { description: searchValue, createdBy: searchValue };
    const queuedAndRequestedFilter = { description: searchValue, sendTo: searchValue };

    this.alertsFacade.setReceivedAlerts(this.pagination.page, this.pagination.pageSize, this.alertReceivedSortList, null, receivedFilter, FilterMethod.OR);
    this.alertsFacade.setQueuedAndRequestedAlerts(this.pagination.page, this.pagination.pageSize, this.alertQueuedAndRequestedSortList, null, queuedAndRequestedFilter, FilterMethod.OR);
  }

  public filterNotifications(filterContext: any) {
    if (filterContext.channel === NotificationChannel.RECEIVER) {
      this.receivedFilter = filterContext.filter;
    } else {
      this.requestedFilter = filterContext.filter;
    }

    if (filterContext.channel === NotificationChannel.RECEIVER) {
      this.alertsFacade.setReceivedAlerts(this.pagination.page, this.pagination.pageSize, this.alertReceivedSortList, null, this.receivedFilter);
    } else {
      this.alertsFacade.setQueuedAndRequestedAlerts(this.pagination.page, this.pagination.pageSize, this.alertQueuedAndRequestedSortList, null, this.requestedFilter);
    }
  }

  private setTableSortingList(sorting: TableHeaderSort, notificationTable: NotificationStatusGroup): void {
    const tableSortList =
      notificationTable === NotificationStatusGroup.RECEIVED
        ? this.alertReceivedSortList
        : this.alertQueuedAndRequestedSortList;
    TableSortingUtil.setTableSortingList(sorting, tableSortList, this.ctrlKeyState);
  }

  protected readonly TranslationContext = TranslationContext;
}
