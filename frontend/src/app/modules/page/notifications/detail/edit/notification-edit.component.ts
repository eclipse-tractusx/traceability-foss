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
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { getRoute, NOTIFICATION_BASE_ROUTE } from '@core/known-route';
import { Pagination } from '@core/model/pagination.model';
import { DEFAULT_PAGE_SIZE, FIRST_PAGE } from '@core/pagination/pagination.model';
import { NotificationDetailFacade } from '@page/notifications/core/notification-detail.facade';
import { NotificationsFacade } from '@page/notifications/core/notifications.facade';
import { OtherPartsFacade } from '@page/other-parts/core/other-parts.facade';
import { PartsFacade } from '@page/parts/core/parts.facade';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { Part } from '@page/parts/model/parts.model';
import { NotificationActionHelperService } from '@shared/assembler/notification-action-helper.service';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { NotificationCommonModalComponent } from '@shared/components/notification-common-modal/notification-common-modal.component';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import { toAssetFilter } from '@shared/helper/filter-helper';
import { Notification, NotificationType } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { StaticIdService } from '@shared/service/staticId.service';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { first, tap } from 'rxjs/operators';

@Component({
  selector: 'app-notification-edit',
  templateUrl: './notification-edit.component.html',
  styleUrls: [ './notification-edit.component.scss' ],
})
export class NotificationEditComponent implements AfterViewInit, OnDestroy {
  @ViewChild(NotificationCommonModalComponent) notificationCommonModalComponent: NotificationCommonModalComponent;

  @ViewChild('semanticModelIdTmp') semanticModelIdTmp: TemplateRef<unknown>;
  public availablePartsAsBuilt$: Observable<View<Pagination<Part>>>;
  public affectedPartsAsBuilt$: Observable<View<Pagination<Part>>>;
  public affectedParts: Part[];
  private filteredDataCache: any[] = [];

  public readonly titleId = this.staticIdService.generateId('NotificationDetail');
  public readonly deselectPartTrigger$ = new Subject<Part[]>();
  public readonly editMode = true;
  public notificationFormGroup: FormGroup;

  public affectedPartIds: string[] = [];
  public temporaryAffectedParts: Part[] = [];
  public temporaryAffectedPartsForRemoval: Part[] = [];
  public readonly addPartTrigger$ = new Subject<Part>();
  public readonly currentSelectedAvailableParts$ = new BehaviorSubject<Part[]>([]);
  public readonly currentSelectedAffectedParts$ = new BehaviorSubject<Part[]>([]);

  private originPageNumber: number;
  private originTabIndex: number;

  public selectedNotification: Notification;
  public tableType: TableType;
  public tableAsBuiltSortList: TableHeaderSort[];
  private paramSubscription: Subscription;

  constructor(
    private readonly partsFacade: OtherPartsFacade,
    private readonly ownPartsFacade: PartsFacade,
    public readonly actionHelperService: NotificationActionHelperService,
    public readonly notificationDetailFacade: NotificationDetailFacade,
    private readonly staticIdService: StaticIdService,
    private readonly notificationsFacade: NotificationsFacade,
    private router: Router,
    private readonly route: ActivatedRoute,
    private readonly toastService: ToastService,
  ) {
    this.currentSelectedAvailableParts$.subscribe((parts: Part[]) => {
      this.temporaryAffectedParts = parts;
    });

    this.currentSelectedAffectedParts$.subscribe((parts: Part[]) => {
      this.temporaryAffectedPartsForRemoval = parts;
    });

    this.paramSubscription = this.route.queryParams.subscribe(params => {
      this.originPageNumber = params.pageNumber;
      this.originTabIndex = params?.tabIndex;
    });
  }

  public notificationFormGroupChange(notificationFormGroup: FormGroup) {
    this.notificationFormGroup = notificationFormGroup;
  }

  // TODO parts table
  public onSelectedItemAvailableParts($event: Record<string, unknown>): void {
    /*  this.partDetailsFacade.selectedPart = $event as unknown as Part;
      let tableData = {};
      for (let component of this.partsTableComponents) {
        tableData[component.tableType + "_PAGE"] = component.pageIndex;
      }
      this.router.navigate([`parts/${$event?.id}`], {queryParams: tableData})*/
  }

  public onSelectedItemAffectedParts($event: Record<string, unknown>): void {
    console.log('affected', $event);

  }


  filterAffectedAssets(assetFilter: any): void {
    this.setPartsBasedOnNotificationType(this.selectedNotification, false, assetFilter);
  }


  filterAvailableAssets(assetFilter: any): void {
    this.setPartsBasedOnNotificationType(this.selectedNotification, true, assetFilter);
  }

  public clickedSave(): void {
    const { title,  description, severity, targetDate, bpn } = this.notificationFormGroup.value;
    this.notificationsFacade.updateEditedNotification(this.selectedNotification.id, title, bpn, severity, targetDate, description, this.affectedPartIds);
  }

  public ngAfterViewInit(): void {
    if (!this.notificationDetailFacade.selected?.data) {
      this.selectedNotificationBasedOnUrl();
    } else {
      this.selectNotificationAndLoadPartsBasedOnNotification(this.notificationDetailFacade.selected?.data);
    }
  }

  private setPartsBasedOnNotificationType(notification: Notification, isAvailablePartSubscription: boolean, assetFilter?: any) {

    if (isAvailablePartSubscription) {
      if (notification.type === NotificationType.INVESTIGATION) {
        assetFilter ? this.partsFacade.setSupplierPartsAsBuilt(FIRST_PAGE, DEFAULT_PAGE_SIZE, this.tableAsBuiltSortList, toAssetFilter(assetFilter, true)) : this.setSupplierPartsAsBuilt();
      } else {
        assetFilter ? this.ownPartsFacade.setPartsAsBuilt(FIRST_PAGE, DEFAULT_PAGE_SIZE, this.tableAsBuiltSortList, toAssetFilter(assetFilter, true)) : this.setOwnPartsAsBuilt();
      }
    } else {
      if (notification.type === NotificationType.INVESTIGATION) {
        assetFilter ? this.partsFacade.setSupplierPartsAsBuiltSecond(FIRST_PAGE, DEFAULT_PAGE_SIZE, this.tableAsBuiltSortList, toAssetFilter(assetFilter, true)) : this.setSupplierPartsAsBuilt();
      } else {
        assetFilter ? this.ownPartsFacade.setPartsAsBuiltSecond(FIRST_PAGE, DEFAULT_PAGE_SIZE, this.tableAsBuiltSortList, toAssetFilter(assetFilter, true)) : this.setOwnPartsAsBuilt();
      }
    }


  }


  private setSupplierPartsAsBuilt() {
    this.tableType = TableType.AS_BUILT_SUPPLIER;
    this.availablePartsAsBuilt$ = this.partsFacade.supplierPartsAsBuilt$;
    this.affectedPartsAsBuilt$ = this.partsFacade.supplierPartsAsBuiltSecond$;
    this.partsFacade.setSupplierPartsAsBuilt();
    this.partsFacade.setSupplierPartsAsBuiltSecond();
  }

  private setOwnPartsAsBuilt() {
    this.tableType = TableType.AS_BUILT_OWN;
    this.availablePartsAsBuilt$ = this.ownPartsFacade.partsAsBuilt$;
    this.affectedPartsAsBuilt$ = this.ownPartsFacade.partsAsBuiltSecond$;
    this.ownPartsFacade.setPartsAsBuilt();
    this.ownPartsFacade.setPartsAsBuiltSecond();
  }

  public ngOnDestroy(): void {
    this.notificationDetailFacade.unsubscribeSubscriptions();
    this.paramSubscription?.unsubscribe();
  }

  filterOnlyAffected(parts: any): Pagination<Part> {

    // TODO performance
    const partsFiltered = parts.content.filter(part => this.affectedPartIds.includes(part.id));

    // TODO fix pagination
    return {
      page: parts.page,
      pageCount: parts.pageCount,
      pageSize: parts.pageSize,
      totalItems: partsFiltered.length,
      content: partsFiltered,
    };
  }

  removeAffectedParts() {
    this.affectedPartIds = this.affectedPartIds.filter(value => {
      return !this.temporaryAffectedPartsForRemoval.some(part => part.id === value);
    });
    this.temporaryAffectedPartsForRemoval = [];
    this.currentSelectedAffectedParts$.next([]);
  }

  addAffectedParts() {
    this.temporaryAffectedParts.forEach(value => {
      if (!this.affectedPartIds.includes(value.id)) {
        this.affectedPartIds.push(value.id);
      }
    });
    this.deselectPartTrigger$.next(this.temporaryAffectedParts);
    this.currentSelectedAvailableParts$.next([]);
    console.log(this.temporaryAffectedParts)
    this.temporaryAffectedParts = [];
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

  private selectedNotificationBasedOnUrl(): void {
    const notificationId = this.route.snapshot.paramMap.get('notificationId');

    this.notificationsFacade
      .getNotification(notificationId)
      .pipe(
        first(),
        tap(notification => {
          this.notificationDetailFacade.selected = { data: notification };
          this.selectNotificationAndLoadPartsBasedOnNotification(notification);

        }),
      )
      .subscribe();
  }

  private selectNotificationAndLoadPartsBasedOnNotification(notification: Notification) {
    this.selectedNotification = notification;
    this.affectedPartIds = notification.assetIds;
    this.setPartsBasedOnNotificationType(this.selectedNotification, true);
    this.setPartsBasedOnNotificationType(this.selectedNotification, false);
  }

  protected readonly TableType = TableType;
  protected readonly MainAspectType = MainAspectType;
}
