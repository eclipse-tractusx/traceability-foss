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
import { ActivatedRoute, Router } from '@angular/router';
import { ALERT_BASE_ROUTE, getRoute } from '@core/known-route';
import { AlertDetailFacade } from '@page/alerts/core/alert-detail.facade';
import { AlertHelperService } from '@page/alerts/core/alert-helper.service';
import { AlertsFacade } from '@page/alerts/core/alerts.facade';
import { NotificationMenuActionsAssembler } from '@shared/assembler/notificationMenuActions.assembler';
import { NotificationCommonModalComponent } from '@shared/components/notification-common-modal/notification-common-modal.component';
import {
  PartTableType,
  MenuActionConfig,
  TableEventConfig,
  TableHeaderSort,
  TableFilter,
  FilterMethod,
} from '@shared/components/table/table.model';
import { TableSortingUtil } from '@shared/components/table/tableSortingUtil';
import { NotificationTabInformation } from '@shared/model/notification-tab-information';
import { Notification, NotificationStatusGroup } from '@shared/model/notification.model';
import { TranslationContext } from '@shared/model/translation-context.model';
import { Subscription } from 'rxjs';
import { Severity } from '@shared/model/severity.model';
import { NotificationStatus } from '@shared/model/notification.model';

@Component({
  selector: 'app-alerts',
  templateUrl: './alerts.component.html',
  styleUrls: ['./alerts.component.scss'],
})
export class AlertsComponent {
  @ViewChild(NotificationCommonModalComponent) notificationCommonModalComponent: NotificationCommonModalComponent;

  public searchFormGroup = new FormGroup({});
  public searchControl: FormControl;
  public readonly alertsReceived$;
  public readonly alertsQueuedAndRequested$;

  public menuActionsConfig: MenuActionConfig<Notification>[];

  public alertReceivedSortList: TableHeaderSort[] = [];
  public alertQueuedAndRequestedSortList: TableHeaderSort[] = [];
  public filterReceived: TableFilter = { filterMethod: FilterMethod.AND };
  public filterQueuedAndRequested: TableFilter = { filterMethod: FilterMethod.AND };

  optionTextSearch = [];
  severityOptions = [
    {
      display: 'severity.' + Severity.MINOR,
      value: 0,
      checked: false,
    },
    {
      display: 'severity.' + Severity.MAJOR,
      value: 1,
      checked: false,
    },
    {
      display: 'severity.' + Severity.CRITICAL,
      value: 2,
      checked: false,
    },
    {
      display: 'severity.' + Severity.LIFE_THREATENING,
      value: 3,
      checked: false,
    },
  ];
  //Approved and Requested only exist in the frontend
  statusOptions = [
    {
      display: 'commonAlert.status.' + NotificationStatus.ACCEPTED,
      value: NotificationStatus.ACCEPTED,
      checked: false,
    },
    {
      display: 'commonAlert.status.' + NotificationStatus.ACKNOWLEDGED,
      value: NotificationStatus.ACKNOWLEDGED,
      checked: false,
    },
    {
      display: 'commonAlert.status.' + NotificationStatus.CANCELED,
      value: NotificationStatus.CANCELED,
      checked: false,
    },
    {
      display: 'commonAlert.status.' + NotificationStatus.CLOSED,
      value: NotificationStatus.CLOSED,
      checked: false,
    },
    {
      display: 'commonAlert.status.' + NotificationStatus.CREATED,
      value: NotificationStatus.CREATED,
      checked: false,
    },
    {
      display: 'commonAlert.status.' + NotificationStatus.DECLINED,
      value: NotificationStatus.DECLINED,
      checked: false,
    },
    {
      display: 'commonAlert.status.' + NotificationStatus.RECEIVED,
      value: NotificationStatus.RECEIVED,
      checked: false,
    },
    {
      display: 'commonAlert.status.' + NotificationStatus.SENT,
      value: NotificationStatus.SENT,
      checked: false,
    },
  ];

  public readonly alertsReceivedFilterConfiguration: any[] = [
    { filterKey: 'createdDate', isTextSearch: false, isDate: true, option: this.optionTextSearch },
    { filterKey: 'description', isTextSearch: true, option: this.optionTextSearch },
    { filterKey: 'status', isTextSearch: false, option: this.statusOptions },
    { filterKey: 'severity', isTextSearch: false, option: this.severityOptions },
    { filterKey: 'createdBy', isTextSearch: true, option: this.optionTextSearch },
  ];

  public readonly alertsQueuedAndRequestedFilterConfiguration: any[] = [
    { filterKey: 'createdDate', isTextSearch: false, isDate: true, option: this.optionTextSearch },
    { filterKey: 'description', isTextSearch: true, option: this.optionTextSearch },
    { filterKey: 'status', isTextSearch: false, option: this.statusOptions },
    { filterKey: 'severity', isTextSearch: false, option: this.severityOptions },
    { filterKey: 'sendTo', isTextSearch: true, option: this.optionTextSearch },
  ];

  private ctrlKeyState: boolean = false;

  private paramSubscription: Subscription;

  private pagination: TableEventConfig = { page: 0, pageSize: 50, sorting: ['createdDate', 'desc'] };

  protected readonly PartTableType = PartTableType;

  constructor(
    public readonly helperService: AlertHelperService,
    private readonly alertsFacade: AlertsFacade,
    private readonly alertDetailFacade: AlertDetailFacade,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly cd: ChangeDetectorRef,
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
      this.pagination.page = params?.pageNumber;
      this.alertsFacade.setReceivedAlerts(
        this.pagination.page,
        this.pagination.pageSize,
        this.alertReceivedSortList,
        this.filterReceived,
      );
      this.alertsFacade.setQueuedAndRequestedAlerts(
        this.pagination.page,
        this.pagination.pageSize,
        this.alertQueuedAndRequestedSortList,
        this.filterQueuedAndRequested,
      );
    });

    this.searchFormGroup.addControl('alertSearch', new FormControl([]));
    this.searchControl = this.searchFormGroup.get('alertSearch') as unknown as FormControl;
  }

  public ngAfterViewInit(): void {
    this.menuActionsConfig = NotificationMenuActionsAssembler.getMenuActions(
      this.helperService,
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
    this.setTableSortingList(pagination.sorting, NotificationStatusGroup.RECEIVED);
    if (pagination.filtering) {
      this.filterReceived = pagination.filtering;
    }
    this.alertsFacade.setReceivedAlerts(
      this.pagination.page,
      this.pagination.pageSize,
      this.alertReceivedSortList,
      this.filterReceived,
    );
  }

  public onQueuedAndRequestedTableConfigChange(pagination: TableEventConfig) {
    this.pagination = pagination;
    this.setTableSortingList(pagination.sorting, NotificationStatusGroup.QUEUED_AND_REQUESTED);
    if (pagination.filtering) {
      this.filterQueuedAndRequested = pagination.filtering;
    }
    this.alertsFacade.setQueuedAndRequestedAlerts(
      this.pagination.page,
      this.pagination.pageSize,
      this.alertQueuedAndRequestedSortList,
      this.filterQueuedAndRequested,
    );
  }

  public openDetailPage(notification: Notification): void {
    this.alertDetailFacade.selected = { data: notification };
    const { link } = getRoute(ALERT_BASE_ROUTE);
    const tabIndex = this.route.snapshot.queryParamMap.get('tabIndex');
    const tabInformation: NotificationTabInformation = { tabIndex: tabIndex, pageNumber: this.pagination.page };
    this.router.navigate([`/${link}/${notification.id}`], { queryParams: tabInformation });
  }

  public handleConfirmActionCompletedEvent() {
    this.ngOnInit();
  }

  public triggerSearch(): void {
    // TODO: implement search
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
