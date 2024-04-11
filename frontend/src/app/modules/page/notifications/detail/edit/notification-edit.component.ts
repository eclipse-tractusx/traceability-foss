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
import { SharedPartIdsService } from '@page/notifications/detail/edit/shared-part-ids.service';
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

@Component({
  selector: 'app-notification-edit',
  templateUrl: './notification-edit.component.html',
  styleUrls: [ './notification-edit.component.scss' ],
})
export class NotificationEditComponent implements AfterViewInit, OnDestroy {
  @ViewChild(NotificationCommonModalComponent) notificationCommonModalComponent: NotificationCommonModalComponent;

  @ViewChild('semanticModelIdTmp') semanticModelIdTmp: TemplateRef<unknown>;


  public readonly affectedPartsTableLabelId = this.staticIdService.generateId('AffectedPartsTable');
  public readonly availablePartsTableLabelId = this.staticIdService.generateId('AvailablePartsTable');

  public readonly addPartTrigger$ = new Subject<Part>();
  public readonly deselectPartTrigger$ = new Subject<Part[]>();

  public readonly editMode: boolean = true;
  public notificationFormGroup: FormGroup;

  public affectedPartIds: string[] = [];
  public temporaryAffectedParts: Part[] = [];
  public temporaryAffectedPartsForRemoval: Part[] = [];
  public readonly currentSelectedAvailableParts$ = new BehaviorSubject<Part[]>([]);
  public readonly currentSelectedAffectedParts$ = new BehaviorSubject<Part[]>([]);
  public availablePartsAsBuilt$: Observable<View<Pagination<Part>>>;
  public affectedPartsAsBuilt$: Observable<View<Pagination<Part>>>;

  private originPageNumber: number;
  private originTabIndex: number;

  public selectedNotification: Notification;
  public tableType: TableType;
  public tableAsBuiltSortList: TableHeaderSort[];
  private paramSubscription: Subscription;
  isSaveButtonDisabled: boolean;

  constructor(
    private readonly partsFacade: OtherPartsFacade,
    private readonly ownPartsFacade: PartsFacade,
    public readonly actionHelperService: NotificationActionHelperService,
    public readonly notificationDetailFacade: NotificationDetailFacade,
    private readonly staticIdService: StaticIdService,
    private readonly notificationsFacade: NotificationsFacade,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly toastService: ToastService,
    private readonly sharedPartIdsService: SharedPartIdsService,
  ) {

    this.editMode = this.route.snapshot.url[this.route.snapshot.url.length - 1].path === 'edit';

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
    this.isSaveButtonDisabled = notificationFormGroup.invalid;
    this.notificationFormGroup = notificationFormGroup;

  }

  filterAffectedParts(partsFilter: any): void {
    this.setAffectedPartsBasedOnNotificationType(this.selectedNotification, partsFilter);
  }


  filterAvailableParts(partsFilter: any): void {
    this.setAvailablePartsBasedOnNotificationType(this.selectedNotification, partsFilter);
  }

  public clickedSave(): void {
    const { title, type, description, severity, targetDate, bpn } = this.notificationFormGroup.value;
    if (this.editMode) {
      this.notificationsFacade.editNotification(this.selectedNotification.id, title, bpn, severity, targetDate, description, this.affectedPartIds).subscribe({
        next: () => {
          this.navigateBackToNotifications();
          this.toastService.success('requestNotification.saveSuccess');
        },
        error: (error) => this.toastService.error('requestNotification.saveError'),
      });
    } else {
      this.notificationsFacade.createNotification(this.affectedPartIds, type, title, bpn, severity, targetDate, description).subscribe({
        next: () => {
          this.toastService.success('requestNotification.saveSuccess');
          this.navigateBackToNotifications();
        },
        error: (error) => this.toastService.error('requestNotification.saveError'),
      });
    }
  }

  public ngAfterViewInit(): void {
    console.log(this.sharedPartIdsService.sharedPartIds);
    if (this.editMode) {
      if (!this.notificationDetailFacade.selected?.data) {
        this.selectedNotificationBasedOnUrl();
      } else {
        this.selectNotificationAndLoadPartsBasedOnNotification(this.notificationDetailFacade.selected.data);
      }
    } else {
      // TODO: input asset Ids from router and set notification type based on my parts (alert) / other parts (investigations) origin
      const newNotification: Notification = {
        assetIds: this.sharedPartIdsService.sharedPartIds,
        createdBy: '',
        type: this.route.snapshot.queryParams['initialType'],
        createdByName: '',
        createdDate: undefined,
        description: '',
        isFromSender: true,
        reason: undefined,
        sendTo: '',
        sendToName: '',
        severity: undefined,
        status: undefined,
        title: '',
        id: 'new',
      };
      this.selectNotificationAndLoadPartsBasedOnNotification(newNotification);
    }
  }


  private setAvailablePartsBasedOnNotificationType(notification: Notification, assetFilter?: any) {
    if (notification.type === NotificationType.INVESTIGATION) {
      assetFilter ? this.partsFacade.setSupplierPartsAsBuilt(FIRST_PAGE, DEFAULT_PAGE_SIZE, this.tableAsBuiltSortList, toAssetFilter(assetFilter, true)) : this.setSupplierPartsAsBuilt();
    } else {
      assetFilter ? this.ownPartsFacade.setPartsAsBuilt(FIRST_PAGE, DEFAULT_PAGE_SIZE, this.tableAsBuiltSortList, toAssetFilter(assetFilter, true)) : this.setOwnPartsAsBuilt();
    }
  }

  private setAffectedPartsBasedOnNotificationType(notification: Notification, assetFilter?: any) {
    if (notification.type === NotificationType.INVESTIGATION) {
      assetFilter ? this.partsFacade.setSupplierPartsAsBuiltSecond(FIRST_PAGE, DEFAULT_PAGE_SIZE, this.tableAsBuiltSortList, toAssetFilter(assetFilter, true)) : this.setSupplierPartsAsBuilt();
    } else {
      assetFilter ? this.ownPartsFacade.setPartsAsBuiltSecond(FIRST_PAGE, DEFAULT_PAGE_SIZE, this.tableAsBuiltSortList, toAssetFilter(assetFilter, true)) : this.setOwnPartsAsBuilt();
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
    this.deselectPartTrigger$.next(this.temporaryAffectedPartsForRemoval);
    this.currentSelectedAffectedParts$.next([]);
    this.temporaryAffectedPartsForRemoval = [];
  }

  addAffectedParts() {
    this.temporaryAffectedParts.forEach(value => {
      if (!this.affectedPartIds.includes(value.id)) {
        this.affectedPartIds.push(value.id);
      }
    });
    this.deselectPartTrigger$.next(this.temporaryAffectedParts);
    this.currentSelectedAvailableParts$.next([]);
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
      .getNotificationById(notificationId)
      .subscribe({
        next: data => {
          this.selectNotificationAndLoadPartsBasedOnNotification(data);
        },
        error: (error: Error) => {
        },
      });

  }

  private selectNotificationAndLoadPartsBasedOnNotification(notification: Notification) {
    this.selectedNotification = notification;
    this.affectedPartIds = notification.assetIds;
    this.setAvailablePartsBasedOnNotificationType(this.selectedNotification);
    this.setAffectedPartsBasedOnNotificationType(this.selectedNotification, false);
  }

  protected readonly TableType = TableType;
  protected readonly MainAspectType = MainAspectType;
}
