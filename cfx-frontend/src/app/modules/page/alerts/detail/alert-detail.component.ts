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
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { ALERT_BASE_ROUTE, getRoute } from '@core/known-route';
import { AlertDetailFacade } from '@page/alerts/core/alert-detail.facade';
import { AlertHelperService } from '@page/alerts/core/alert-helper.service';
import { AlertsFacade } from '@page/alerts/core/alerts.facade';
import { Part } from '@page/parts/model/parts.model';
import { NotificationActionHelperService } from '@shared/assembler/notification-action-helper.service';
import { ForwardNotificationComponent } from '@shared/components/request-notification/forward-notification/forward-notification.component';
import { NotificationCommonModalComponent } from '@shared/components/notification-common-modal/notification-common-modal.component';
import { RequestContext } from '@shared/components/request-notification/request-notification.base';
import { CreateHeaderFromColumns, TableConfig, TableEventConfig } from '@shared/components/table/table.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import { Notification } from '@shared/model/notification.model';
import { TranslationContext } from '@shared/model/translation-context.model';
import { View } from '@shared/model/view.model';
import { StaticIdService } from '@shared/service/staticId.service';
import { BehaviorSubject, Observable, Subject, Subscription } from 'rxjs';
import { filter, first, tap } from 'rxjs/operators';

@Component({
  selector: 'app-alert-detail',
  templateUrl: './alert-detail.component.html',
  styleUrls: ['./alert-detail.component.scss'],
})
export class AlertDetailComponent implements AfterViewInit, OnDestroy {
  @ViewChild(NotificationCommonModalComponent) notificationCommonModalComponent: NotificationCommonModalComponent;

  @ViewChild('semanticModelIdTmp') semanticModelIdTmp: TemplateRef<unknown>;

  public readonly alertPartsInformation$: Observable<View<Part[]>>;
  public readonly supplierPartsDetailInformation$: Observable<View<Part[]>>;
  public readonly selected$: Observable<View<Notification>>;

  public readonly isAlertOpen$ = new BehaviorSubject<boolean>(false);
  public readonly selectedItems$ = new BehaviorSubject<Part[]>([]);
  public readonly deselectPartTrigger$ = new Subject<Part[]>();
  public readonly addPartTrigger$ = new Subject<Part>();

  public readonly notificationPartsTableId = this.staticIdService.generateId('AlertDetail');
  public readonly supplierPartsTableId = this.staticIdService.generateId('AlertDetail');

  public notificationPartsTableConfig: TableConfig;
  public supplierPartsTableConfig: TableConfig;
  public isReceived: boolean;
  private originPageNumber: number;
  private originTabIndex: number;

  private subscription: Subscription;
  private selectedAlertTmpStore: Notification;
  public selectedAlert: Notification;

  private paramSubscription: Subscription;

  constructor(
    public readonly helperService: AlertHelperService,
    public readonly actionHelperService: NotificationActionHelperService,
    public readonly alertDetailFacade: AlertDetailFacade,
    private readonly staticIdService: StaticIdService,
    private readonly alertsFacade: AlertsFacade,
    private router: Router,
    private readonly route: ActivatedRoute,
    private readonly toastService: ToastService,
    public dialog: MatDialog,
  ) {
    this.alertPartsInformation$ = this.alertDetailFacade.notificationPartsInformation$;
    this.supplierPartsDetailInformation$ = this.alertDetailFacade.supplierPartsInformation$;

    this.selected$ = this.alertDetailFacade.selected$;

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
          this.selectedAlert = data;
        }),
      )
      .subscribe();
  }

  public ngOnDestroy(): void {
    this.subscription?.unsubscribe();
    this.alertDetailFacade.unsubscribeSubscriptions();
    this.paramSubscription?.unsubscribe();
  }

  public onNotificationPartsSort({ sorting }: TableEventConfig): void {
    const [name, direction] = sorting || ['', ''];
    this.alertDetailFacade.sortNotificationParts(name, direction);
  }

  public onSupplierPartsSort({ sorting }: TableEventConfig): void {
    const [name, direction] = sorting || ['', ''];
    this.alertDetailFacade.sortSupplierParts(name, direction);
  }

  public onMultiSelect(event: unknown[]): void {
    this.selectedAlertTmpStore = Object.assign(this.alertDetailFacade.selected);
    this.selectedItems$.next(event as Part[]);
  }

  public removeItemFromSelection(part: Part): void {
    this.deselectPartTrigger$.next([part]);
    this.selectedItems$.next(this.selectedItems$.getValue().filter(({ id }) => id !== part.id));
  }

  public clearSelected(): void {
    this.deselectPartTrigger$.next(this.selectedItems$.getValue());
    this.selectedItems$.next([]);
  }

  public addItemToSelection(part: Part): void {
    this.addPartTrigger$.next(part);
    this.selectedItems$.next([...this.selectedItems$.getValue(), part]);
  }

  public copyToClipboard(semanticModelId: string): void {
    const text = { id: 'clipboard', values: { value: semanticModelId } };
    navigator.clipboard.writeText(semanticModelId).then(_ => this.toastService.info('clipboardTitle', text));
  }

  public navigateBackToAlerts(): void {
    const { link } = getRoute(ALERT_BASE_ROUTE);
    this.router.navigate([`/${link}`], { queryParams: { tabIndex: this.originTabIndex, pageNumber: this.originPageNumber } });
  }

  public handleConfirmActionCompletedEvent(): void {
    this.alertDetailFacade.selected = { loader: true };
    this.subscription?.unsubscribe();
    this.ngAfterViewInit();
  }

  private setTableConfigs(data: Notification): void {
    this.isReceived = !data.isFromSender;

    const displayedColumns = ['id', 'semanticDataModel', 'nameAtManufacturer', 'semanticModelId'];
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

    this.alertDetailFacade.setAlertPartsInformation(data);
    this.notificationPartsTableConfig = { ...tableConfig };

    if (!this.isReceived) {
      return;
    }
    this.alertDetailFacade.setAndSupplierPartsInformation();
    this.supplierPartsTableConfig = {
      ...tableConfig,
      displayedColumns: [...displayedColumns],
      header: CreateHeaderFromColumns([...displayedColumns], 'table.column'),
    };
  }

  private selectedNotificationBasedOnUrl(): void {
    const alertId = this.route.snapshot.paramMap.get('alertId');
    this.alertsFacade
      .getAlert(alertId)
      .pipe(
        first(),
        tap(notification => (this.alertDetailFacade.selected = { data: notification })),
      )
      .subscribe();
  }

  protected readonly TranslationContext = TranslationContext;

  openForwardDialog(alert: Notification) {
    this.dialog.open(ForwardNotificationComponent, {
      autoFocus: false,
      disableClose: true,
      data: {
        context: RequestContext.REQUEST_ALERT,
        forwardedNotification: alert
      },
    });
  }
}
