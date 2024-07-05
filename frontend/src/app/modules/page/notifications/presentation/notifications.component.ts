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
import { ActivatedRoute, Router } from '@angular/router';
import { getRoute, NOTIFICATION_BASE_ROUTE } from '@core/known-route';
import { NotificationHelperService } from '@page/notifications/core/notification-helper.service';
import { NotificationsFacade } from '@page/notifications/core/notifications.facade';
import { NotificationActionHelperService } from '@shared/assembler/notification-action-helper.service';
import { NotificationMenuActionsAssembler } from '@shared/assembler/notificationMenuActions.assembler';
import { NotificationChannel } from '@shared/components/multi-select-autocomplete/table-type.model';
import { NotificationCommonModalComponent } from '@shared/components/notification-common-modal/notification-common-modal.component';
import { TableSortingUtil } from '@shared/components/table/table-sorting.util';
import { MenuActionConfig, TableEventConfig, TableHeaderSort } from '@shared/components/table/table.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import { createDeeplinkNotificationFilter } from '@shared/helper/notification-helper';
import { setMultiSorting } from '@shared/helper/table-helper';
import { NotificationTabInformation } from '@shared/model/notification-tab-information';
import {
  Notification,
  NotificationFilter,
  NotificationStatusGroup,
  NotificationType,
} from '@shared/model/notification.model';
import { TranslationContext } from '@shared/model/translation-context.model';
import { NotificationProcessingService } from '@shared/service/notification-processing.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-notification-component',
  templateUrl: './notifications.component.html',
})
export class NotificationsComponent {
  @ViewChild(NotificationCommonModalComponent) notificationCommonModalComponent: NotificationCommonModalComponent;


  public readonly notificationsReceived$;
  public readonly notificationsQueuedAndRequested$;

  public menuActionsConfig: MenuActionConfig<Notification>[];

  public notificationReceivedSortList: TableHeaderSort[] = [];
  public notificationQueuedAndRequestedSortList: TableHeaderSort[] = [];
  private ctrlKeyState: boolean = false;

  private paramSubscription: Subscription;
  private toastActionSubscription: Subscription;

  receivedFilter: NotificationFilter;
  requestedFilter: NotificationFilter;

  private pagination: TableEventConfig = { page: 0, pageSize: 50, sorting: [ 'createdDate', 'desc' ] };

  constructor(
    public readonly helperService: NotificationHelperService,
    private readonly actionHelperService: NotificationActionHelperService,
    private readonly notificationsFacade: NotificationsFacade,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly cd: ChangeDetectorRef,
    private readonly toastService: ToastService,
    private readonly notificationProcessingService: NotificationProcessingService,
  ) {
    this.notificationsReceived$ = this.notificationsFacade.notificationsReceived$;
    this.notificationsQueuedAndRequested$ = this.notificationsFacade.notificationsQueuedAndRequested$;

    window.addEventListener('keydown', (event) => {
      this.ctrlKeyState = setMultiSorting(event);
    });
    window.addEventListener('keyup', (event) => {
      this.ctrlKeyState = setMultiSorting(event);
    });

    this.toastActionSubscription = this.toastService.retryAction.subscribe({
      next: result => {
        const formatted = result?.context?.charAt(0)?.toUpperCase() + result?.context?.slice(1)?.toLowerCase();
        if (result?.success) {
          this.toastService.success(`requestNotification.successfully${ formatted }`);
        } else if (result?.error) {
          this.toastService.error(`requestNotification.failed${ formatted }`, 15000, true);
        }
        this.handleConfirmActionCompletedEvent();
      },
    });
  }

  public ngOnInit(): void {

    this.paramSubscription = this.route.queryParams.subscribe(params => {

      let deeplinkNotificationFilter = createDeeplinkNotificationFilter(params);
      this.pagination.page = params?.pageNumber ? params.pageNumber : 0;
      this.pagination.page = params?.pageNumber;
      this.notificationsFacade.setReceivedNotifications(this.pagination.page, this.pagination.pageSize, this.notificationReceivedSortList, deeplinkNotificationFilter?.receivedFilter, this.receivedFilter);
      this.notificationsFacade.setQueuedAndRequestedNotifications(this.pagination.page, this.pagination.pageSize, this.notificationQueuedAndRequestedSortList, deeplinkNotificationFilter?.sentFilter, this.requestedFilter);
    });
  }

  public ngAfterViewInit(): void {
    this.menuActionsConfig = NotificationMenuActionsAssembler.getMenuActions(
      this.actionHelperService,
      this.notificationCommonModalComponent,
      this.notificationProcessingService,
    );
    this.cd.detectChanges();
  }

  public ngOnDestroy(): void {
    this.notificationsFacade.stopNotifications();
    this.paramSubscription?.unsubscribe();
    this.toastActionSubscription?.unsubscribe();
  }

  public onReceivedTableConfigChange(pagination: TableEventConfig) {
    this.pagination = pagination;
    this.setTableSortingList(pagination.sorting, NotificationStatusGroup.RECEIVED);
    this.notificationsFacade.setReceivedNotifications(this.pagination.page, this.pagination.pageSize, this.notificationReceivedSortList, null, this.receivedFilter);
  }

  public onQueuedAndRequestedTableConfigChange(pagination: TableEventConfig) {
    this.pagination = pagination;
    this.setTableSortingList(pagination.sorting, NotificationStatusGroup.QUEUED_AND_REQUESTED);
    this.notificationsFacade.setQueuedAndRequestedNotifications(this.pagination.page, this.pagination.pageSize, this.notificationQueuedAndRequestedSortList, null, this.requestedFilter);
  }

  public openDetailPage(notification: Notification): void {
    const { link, tabInformation } = this.getTabInformation();
    this.router.navigate([ `/${ link }/${ notification.id }` ], { queryParams: tabInformation });
  }

  public openEditPage(notification: Notification): void {
    const { link, tabInformation } = this.getTabInformation();
    this.router.navigate([ `/${ link }/${ notification.id }/edit` ], { queryParams: tabInformation });
  }

  private getTabInformation(): { link: string, tabInformation: any } {
    const { link } = getRoute(NOTIFICATION_BASE_ROUTE);
    const tabIndex = this.route.snapshot.queryParamMap.get('tabIndex');
    const tabInformation: NotificationTabInformation = { tabIndex: tabIndex, pageNumber: this.pagination.page };
    return { link, tabInformation };
  }

  public handleConfirmActionCompletedEvent() {
    this.ngOnInit();
  }

  private setTableSortingList(sorting: TableHeaderSort, notificationTable: NotificationStatusGroup): void {
    const tableSortList = notificationTable === NotificationStatusGroup.RECEIVED ?
      this.notificationReceivedSortList : this.notificationQueuedAndRequestedSortList;
    TableSortingUtil.setTableSortingList(sorting, tableSortList, this.ctrlKeyState);
  }

  protected readonly TranslationContext = TranslationContext;
  protected readonly NotificationType = NotificationType;

  filterNotifications(filterContext: any) {
    if (filterContext.channel === NotificationChannel.RECEIVER) {
      this.receivedFilter = filterContext.filter;
    } else {
      this.requestedFilter = filterContext.filter;
    }
    if (filterContext.channel === NotificationChannel.RECEIVER) {
      this.notificationsFacade.setReceivedNotifications(this.pagination.page, this.pagination.pageSize, this.notificationReceivedSortList, null, this.receivedFilter);
    } else {
      this.notificationsFacade.setQueuedAndRequestedNotifications(this.pagination.page, this.pagination.pageSize, this.notificationQueuedAndRequestedSortList, null, this.requestedFilter);
    }
  }
}
