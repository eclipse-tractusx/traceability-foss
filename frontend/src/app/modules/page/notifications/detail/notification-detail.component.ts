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

import { AfterViewInit, Component, OnDestroy, TemplateRef, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { getRoute, NOTIFICATION_BASE_ROUTE } from '@core/known-route';
import { NotificationDetailFacade } from '@page/notifications/core/notification-detail.facade';
import { NotificationHelperService } from '@page/notifications/core/notification-helper.service';
import { NotificationsFacade } from '@page/notifications/core/notifications.facade';
import { Part } from '@page/parts/model/parts.model';
import { NotificationActionHelperService } from '@shared/assembler/notification-action-helper.service';
import { NotificationCommonModalComponent } from '@shared/components/notification-common-modal/notification-common-modal.component';
import { CreateHeaderFromColumns, TableConfig, TableEventConfig } from '@shared/components/table/table.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import { Notification, NotificationStatus, NotificationType } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { NotificationAction } from '@shared/modules/notification/notification-action.enum';
import { NotificationProcessingService } from '@shared/service/notification-processing.service';
import { StaticIdService } from '@shared/service/staticId.service';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { filter, first, tap } from 'rxjs/operators';

@Component({
  selector: 'app-alert-detail',
  templateUrl: './notification-detail.component.html',
  styleUrls: [ './notification-detail.component.scss' ],
})
export class NotificationDetailComponent implements AfterViewInit, OnDestroy {
  @ViewChild(NotificationCommonModalComponent) notificationCommonModalComponent: NotificationCommonModalComponent;

  @ViewChild('semanticModelIdTmp') semanticModelIdTmp: TemplateRef<unknown>;

  public notificationPartsInformation$: Observable<View<Part[]>>;
  public supplierPartsDetailInformation$: Observable<View<Part[]>>;
  public readonly selected$: Observable<View<Notification>>;

  public readonly selectedItems$ = new BehaviorSubject<Part[]>([]);
  public readonly deselectPartTrigger$ = new Subject<Part[]>();
  public readonly addPartTrigger$ = new Subject<Part>();

  public readonly notificationPartsTableId = this.staticIdService.generateId('NotificationDetail');
  public readonly supplierPartsTableId = this.staticIdService.generateId('NotificationDetail');

  public notificationPartsTableConfig: TableConfig;
  public supplierPartsTableConfig: TableConfig;
  public isReceived: boolean;
  private originPageNumber: number;
  private originTabIndex: number;

  private subscription: Subscription;
  private selectedNotificationTmpStore: Notification;
  public selectedNotification: Notification;

  private paramSubscription: Subscription;
  private toastActionSubscription: Subscription;

  constructor(
    public readonly helperService: NotificationHelperService,
    public readonly actionHelperService: NotificationActionHelperService,
    public readonly notificationDetailFacade: NotificationDetailFacade,
    private readonly staticIdService: StaticIdService,
    private readonly notificationsFacade: NotificationsFacade,
    private router: Router,
    private readonly route: ActivatedRoute,
    private readonly toastService: ToastService,
    private readonly notificationProcessingService: NotificationProcessingService,
  ) {
    this.notificationPartsInformation$ = this.notificationDetailFacade.notificationPartsInformation$;
    this.supplierPartsDetailInformation$ = this.notificationDetailFacade.supplierPartsInformation$;

    this.toastActionSubscription = this.toastService.retryAction.subscribe({
      next: result => {
        const formattedStatus = result?.context?.notificationStatus?.charAt(0)?.toUpperCase() + result?.context?.notificationStatus?.slice(1)?.toLowerCase();
        if (result?.success) {
          this.toastService.success(`requestNotification.successfully${ formattedStatus }`);
        } else if (result?.error) {
          this.toastService.error(`requestNotification.failed${ formattedStatus }`, 15000, true);
        }
        this.notificationProcessingService.deleteNotificationId(result?.context?.notificationId);
        this.ngAfterViewInit();
      },
    });

    this.notificationProcessingService.doneEmit.subscribe(() => {
      this.ngAfterViewInit();
    });

    this.selected$ = this.notificationDetailFacade.selected$;

    this.paramSubscription = this.route.queryParams.subscribe(params => {
      this.originPageNumber = params.pageNumber;
      this.originTabIndex = params?.tabIndex;
    });

  }

  public ngAfterViewInit(): void {
    this.selectedNotificationBasedOnUrl();

    this.subscription = this.selected$
      .pipe(
        filter(({ data }) => !!data),
        tap(({ data }) => {
          this.setTableConfigs(data);
          this.selectedNotification = data;
        }),
      )
      .subscribe();
  }

  public ngOnDestroy(): void {
    this.notificationDetailFacade.selected = { data: null };
    this.subscription?.unsubscribe();
    this.notificationDetailFacade.unsubscribeSubscriptions();
    this.paramSubscription?.unsubscribe();
    this.toastActionSubscription?.unsubscribe();
  }

  public navigateToEditView() {

    this.router.navigate([ `/inbox/${ this.selectedNotification.id }/edit` ], {
      queryParams: {
        tabIndex: this.originTabIndex,
        pageNumber: this.originPageNumber,
      },
    });
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
    this.subscription?.unsubscribe();
    this.ngAfterViewInit();
  }

  private setTableConfigs(data: Notification): void {
    this.isReceived = !data.isFromSender;

    const displayedColumns = [ 'id', 'semanticDataModel', 'nameAtManufacturer', 'semanticModelId' ];
    const sortableColumns = { id: true, semanticDataModel: true, nameAtManufacturer: true, semanticModelId: true };

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
      .getNotificationById(notificationId)
      .pipe(
        first(),
        tap(notification => (this.notificationDetailFacade.selected = { data: notification })),
      )
      .subscribe();
  }

  isProcessing() {
    return this.notificationProcessingService.isInLoadingProcess(this.selectedNotification);
  }

  protected readonly NotificationType = NotificationType;
  protected readonly NotificationAction = NotificationAction;

  protected readonly NotificationStatus = NotificationStatus;
}
