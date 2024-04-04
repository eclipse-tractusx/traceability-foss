/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

import { AfterViewInit, Component, OnDestroy, TemplateRef, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { getRoute, NOTIFICATION_BASE_ROUTE } from '@core/known-route';
import { Pagination } from '@core/model/pagination.model';
import { NotificationDetailFacade } from '@page/notifications/core/notification-detail.facade';
import { NotificationHelperService } from '@page/notifications/core/notification-helper.service';
import { NotificationsFacade } from '@page/notifications/core/notifications.facade';
import { PartsFacade } from '@page/parts/core/parts.facade';
import { Part } from '@page/parts/model/parts.model';
import { NotificationActionHelperService } from '@shared/assembler/notification-action-helper.service';
import { NotificationCommonModalComponent } from '@shared/components/notification-common-modal/notification-common-modal.component';
import {
  CreateHeaderFromColumns,
  TableConfig,
  TableEventConfig,
  TableHeaderSort,
} from '@shared/components/table/table.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import { containsAtleastOneFilterEntry, toAssetFilter } from '@shared/helper/filter-helper';
import { Notification, NotificationStatus, NotificationType } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { NotificationAction } from '@shared/modules/notification/notification-action.enum';
import { PartDetailsFacade } from '@shared/modules/part-details/core/partDetails.facade';
import { StaticIdService } from '@shared/service/staticId.service';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { filter, first, tap } from 'rxjs/operators';
import {TableType} from "@shared/components/multi-select-autocomplete/table-type.model";
import {MainAspectType} from "@page/parts/model/mainAspectType.enum";

@Component({
  selector: 'app-notification-edit',
  templateUrl: './notification-edit.component.html',
  styleUrls: [ './notification-edit.component.scss' ],
})
export class NotificationEditComponent implements AfterViewInit, OnDestroy {
  @ViewChild(NotificationCommonModalComponent) notificationCommonModalComponent: NotificationCommonModalComponent;

  @ViewChild('semanticModelIdTmp') semanticModelIdTmp: TemplateRef<unknown>;
  public readonly partsAsBuilt$: Observable<View<Pagination<Part>>>;
  public readonly titleId = this.staticIdService.generateId('NotificationDetail');
  public readonly deselectPartTrigger$ = new Subject<Part[]>();

  public readonly notificationPartsInformation$: Observable<View<Part[]>>;
  public readonly supplierPartsDetailInformation$: Observable<View<Part[]>>;
  public readonly selected$: Observable<View<Notification>>;

  public affectedParts = [];
  public readonly selectedItems$ = new BehaviorSubject<Part[]>([]);
  public readonly addPartTrigger$ = new Subject<Part>();
  public readonly currentSelectedItems$ = new BehaviorSubject<Part[]>([]);
  public readonly isAlertOpen$ = new BehaviorSubject<boolean>(false);

  public readonly notificationPartsTableId = this.staticIdService.generateId('NotificationDetail');

  public notificationPartsTableConfig: TableConfig;
  public supplierPartsTableConfig: TableConfig;
  public isReceived: boolean;
  private originPageNumber: number;
  private originTabIndex: number;


  private selectedNotificationTmpStore: Notification;
  public selectedNotification: Notification;
  public tableAsBuiltSortList: TableHeaderSort[];
  private paramSubscription: Subscription;

  constructor(
    private readonly partsFacade: PartsFacade,
    public readonly actionHelperService: NotificationActionHelperService,
    public readonly notificationDetailFacade: NotificationDetailFacade,
    private readonly staticIdService: StaticIdService,
    private readonly notificationsFacade: NotificationsFacade,
    private router: Router,
    private readonly route: ActivatedRoute,
    private readonly toastService: ToastService,
  ) {
    this.partsAsBuilt$ = this.partsFacade.partsAsBuilt$;
    this.partsFacade.setPartsAsBuilt();
    this.notificationPartsInformation$ = this.notificationDetailFacade.notificationPartsInformation$;
    this.supplierPartsDetailInformation$ = this.notificationDetailFacade.supplierPartsInformation$;
    this.selected$ = this.notificationDetailFacade.selected$;
console.log(this.selectedNotification, "selected");
    this.paramSubscription = this.route.queryParams.subscribe(params => {

      this.originPageNumber = params.pageNumber;
      this.originTabIndex = params?.tabIndex;
    });



  }

  // TODO parts table
  public onSelectItem($event: Record<string, unknown>): void {
  /*  this.partDetailsFacade.selectedPart = $event as unknown as Part;
    let tableData = {};
    for (let component of this.partsTableComponents) {
      tableData[component.tableType + "_PAGE"] = component.pageIndex;
    }
    this.router.navigate([`parts/${$event?.id}`], {queryParams: tableData})*/
  }

  // TODO parts table
  public onAsBuiltTableConfigChange({page, pageSize, sorting}: TableEventConfig): void {
/*
    this.setTableSortingList(sorting, MainAspectType.AS_BUILT);
    this.currentPartTablePage['AS_BUILT_OWN_PAGE'] = page;
    let pageSizeValue = this.DEFAULT_PAGE_SIZE;
    if (pageSize !== 0) {
      pageSizeValue = pageSize;
    }
    if (this.assetAsBuiltFilter && containsAtleastOneFilterEntry(this.assetAsBuiltFilter)) {
      this.partsFacade.setPartsAsBuilt(0, pageSizeValue, this.tableAsBuiltSortList, toAssetFilter(this.assetAsBuiltFilter, true));
    } else {
      this.partsFacade.setPartsAsBuilt(page, pageSizeValue, this.tableAsBuiltSortList);
    }
*/

  }
  // TODO parts table
  filterActivated(isAsBuilt: boolean, assetFilter: any): void {
/*    if (isAsBuilt) {
      this.assetAsBuiltFilter = assetFilter;
      this.partsFacade.setPartsAsBuilt(this.currentPartTablePage['AS_BUILT_OWN_PAGE'] ?? 0, this.DEFAULT_PAGE_SIZE, this.tableAsBuiltSortList, toAssetFilter(this.assetAsBuiltFilter, true));
    } else {
      this.assetsAsPlannedFilter = assetFilter;
      this.partsFacade.setPartsAsPlanned(this.currentPartTablePage['AS_PLANNED_OWN_PAGE'] ?? 0, this.DEFAULT_PAGE_SIZE, this.tableAsPlannedSortList, toAssetFilter(this.assetsAsPlannedFilter, false));
    }*/
  }



  // TODO implement save / detection change
  public clickedSave(): void {

  }

  // TODO Implement cancel / popup do you really want to leave without saving?
  public clickedCancellation(): void {

  }

  public submittedByNotificationRequest(): void{
    console.log("submitted");
  }

  public ngAfterViewInit(): void {
    if (!this.notificationDetailFacade.selected?.data) {
      this.selectedNotificationBasedOnUrl();
    }

  }

  public ngOnDestroy(): void {
    this.notificationDetailFacade.unsubscribeSubscriptions();
    this.paramSubscription?.unsubscribe();
  }

  public onNotificationPartsSort({ sorting }: TableEventConfig): void {
    const [ name, direction ] = sorting || [ '', '' ];
    this.notificationDetailFacade.sortNotificationParts(name, direction);
  }

  public onSupplierPartsSort({ sorting }: TableEventConfig): void {
    const [ name, direction ] = sorting || [ '', '' ];
    this.notificationDetailFacade.sortSupplierParts(name, direction);
  }

  public onMultiSelect(event: unknown[]): void {
    this.selectedNotificationTmpStore = Object.assign(this.notificationDetailFacade.selected);
    this.selectedItems$.next(event as Part[]);
  }

  public removeItemFromSelection(part: Part): void {
    this.deselectPartTrigger$.next([ part ]);
    this.selectedItems$.next(this.selectedItems$.getValue().filter(({ id }) => id !== part.id));
  }

  public clearSelected(): void {
    this.deselectPartTrigger$.next(this.selectedItems$.getValue());
    this.selectedItems$.next([]);
  }

  public addItemToSelection(part: Part): void {
    this.addPartTrigger$.next(part);
    this.selectedItems$.next([ ...this.selectedItems$.getValue(), part ]);
  }

  public copyToClipboard(semanticModelId: string): void {
    const text = { id: 'clipboard', values: { value: semanticModelId } };
    navigator.clipboard.writeText(semanticModelId).then(_ => this.toastService.info(text));
  }

  public navigateBackToNotifications(): void {
    const { link } = getRoute(NOTIFICATION_BASE_ROUTE);
    this.router.navigate([ `/${ link }` ], {
      queryParams: {
        tabIndex: this.originTabIndex,
        pageNumber: this.originPageNumber,
      },
    });
  }

  public handleConfirmActionCompletedEvent(): void {
    this.notificationDetailFacade.selected = { loader: true };
    this.ngAfterViewInit();
  }

  private setTableConfigs(data: Notification): void {
    this.isReceived = !data.isFromSender;

    const displayedColumns = [ 'id', 'semanticDataModel', 'name', 'semanticModelId' ];
    const sortableColumns = { id: true, semanticDataModel: true, name: true, semanticModelId: true };

    const tableConfig = {
      displayedColumns,
      header: CreateHeaderFromColumns(displayedColumns, 'table.column'),
      sortableColumns: sortableColumns,
      hasPagination: false,
      cellRenderers: {
        semanticModelId: this.semanticModelIdTmp,
      },
    };

    this.notificationDetailFacade.setNotificationPartsInformation(data);
    this.notificationPartsTableConfig = { ...tableConfig };

    if (!this.isReceived) {
      return;
    }
    this.notificationDetailFacade.setAndSupplierPartsInformation();
    this.supplierPartsTableConfig = {
      ...tableConfig,
      displayedColumns: [ 'select', ...displayedColumns ],
      header: CreateHeaderFromColumns([ 'select', ...displayedColumns ], 'table.column'),
    };
  }

  private selectedNotificationBasedOnUrl(): void {
    const notificationId = this.route.snapshot.paramMap.get('notificationId');
    this.notificationsFacade
      .getNotification(notificationId)
      .pipe(
        first(),
        tap(notification => (this.notificationDetailFacade.selected = { data: notification })),
      )
      .subscribe();
  }

  protected readonly NotificationType = NotificationType;
  protected readonly NotificationAction = NotificationAction;

  protected readonly NotificationStatus = NotificationStatus;
    protected readonly TableType = TableType;
    protected readonly MainAspectType = MainAspectType;
}
