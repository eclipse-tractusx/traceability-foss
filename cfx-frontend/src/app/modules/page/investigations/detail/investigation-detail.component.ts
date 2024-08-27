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

import { AfterViewInit, Component, OnDestroy, TemplateRef, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { getRoute, INVESTIGATION_BASE_ROUTE } from '@core/known-route';
import { InvestigationDetailFacade } from '@page/investigations/core/investigation-detail.facade';
import { InvestigationHelperService } from '@page/investigations/core/investigation-helper.service';
import { InvestigationsFacade } from '@page/investigations/core/investigations.facade';
import { Part } from '@page/parts/model/parts.model';
import { NotificationActionHelperService } from '@shared/assembler/notification-action-helper.service';
import { NotificationCommonModalComponent } from '@shared/components/notification-common-modal/notification-common-modal.component';
import { ForwardNotificationComponent } from '@shared/components/request-notification/forward-notification/forward-notification.component';
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
  selector: 'app-investigation-detail',
  templateUrl: './investigation-detail.component.html',
  styleUrls: ['./investigation-detail.component.scss'],
})
export class InvestigationDetailComponent implements AfterViewInit, OnDestroy {
  @ViewChild(NotificationCommonModalComponent) notificationCommonModalComponent: NotificationCommonModalComponent;

  @ViewChild('semanticModelIdTmp') semanticModelIdTmp: TemplateRef<unknown>;

  public readonly investigationPartsInformation$: Observable<View<Part[]>>;
  public readonly supplierPartsDetailInformation$: Observable<View<Part[]>>;
  public readonly selected$: Observable<View<Notification>>;

  public readonly isInvestigationOpen$ = new BehaviorSubject<boolean>(false);
  public readonly selectedItems$ = new BehaviorSubject<Part[]>([]);
  public readonly deselectPartTrigger$ = new Subject<Part[]>();
  public readonly addPartTrigger$ = new Subject<Part>();

  public readonly notificationPartsTableId = this.staticIdService.generateId('InvestigationDetail');
  public readonly supplierPartsTableId = this.staticIdService.generateId('InvestigationDetail');

  public notificationPartsTableConfig: TableConfig;
  public supplierPartsTableConfig: TableConfig;
  public isReceived: boolean;
  private originPageNumber: number;
  private originTabIndex: number;

  private subscription: Subscription;
  private selectedInvestigationTmpStore: Notification;
  private paramSubscription: Subscription;

  public selectedInvestigation: Notification;

  constructor(
    public readonly helperService: InvestigationHelperService,
    public readonly actionHelperService: NotificationActionHelperService,
    public readonly investigationDetailFacade: InvestigationDetailFacade,
    private readonly staticIdService: StaticIdService,
    public readonly investigationsFacade: InvestigationsFacade,
    private router: Router,
    private readonly route: ActivatedRoute,
    private readonly toastService: ToastService,
    public dialog: MatDialog,
  ) {
    this.investigationPartsInformation$ = this.investigationDetailFacade.notificationPartsInformation$;
    this.supplierPartsDetailInformation$ = this.investigationDetailFacade.supplierPartsInformation$;

    this.selected$ = this.investigationDetailFacade.selected$;

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
          this.selectedInvestigation = data;
        }),
      )
      .subscribe();
  }

  public ngOnDestroy(): void {
    this.subscription?.unsubscribe();
    this.investigationDetailFacade.unsubscribeSubscriptions();
    this.paramSubscription?.unsubscribe();
  }

  public onNotificationPartsSort({ sorting }: TableEventConfig): void {
    const [name, direction] = sorting || ['', ''];
    this.investigationDetailFacade.sortNotificationParts(name, direction);
  }

  public onSupplierPartsSort({ sorting }: TableEventConfig): void {
    const [name, direction] = sorting || ['', ''];
    this.investigationDetailFacade.sortSupplierParts(name, direction);
  }

  public onMultiSelect(event: unknown[]): void {
    this.selectedInvestigationTmpStore = Object.assign(this.investigationDetailFacade.selected);
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

  public navigateBackToInvestigations(): void {
    const { link } = getRoute(INVESTIGATION_BASE_ROUTE);
    this.router.navigate([`/${link}`], { queryParams: { tabIndex: this.originTabIndex, pageNumber: this.originPageNumber } });
  }

  public handleConfirmActionCompletedEvent(): void {
    this.investigationDetailFacade.selected = { loader: true };
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

    this.investigationDetailFacade.setInvestigationPartsInformation(data);
    this.notificationPartsTableConfig = { ...tableConfig };

    if (!this.isReceived) {
      return;
    }

    this.investigationDetailFacade.setAndSupplierPartsInformation();
    this.supplierPartsTableConfig = {
      ...tableConfig,
      displayedColumns: [...displayedColumns],
      header: CreateHeaderFromColumns([...displayedColumns], 'table.column'),
    };
  }

  private selectedNotificationBasedOnUrl(): void {
    const investigationId = this.route.snapshot.paramMap.get('investigationId');
    this.investigationsFacade
      .getInvestigation(investigationId)
      .pipe(
        first(),
        tap(notification => (this.investigationDetailFacade.selected = { data: notification })),
      )
      .subscribe();
  }

  openForwardDialog(investigation: Notification) {
    this.dialog.open(ForwardNotificationComponent, {
      autoFocus: false,
      disableClose: true,
      data: {
        context: RequestContext.REQUEST_INVESTIGATION,
        forwardedNotification: investigation
      },
    });
  }

  protected readonly TranslationContext = TranslationContext;
}
