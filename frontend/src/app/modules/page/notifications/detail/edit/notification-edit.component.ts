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
import { DEFAULT_PAGE_SIZE, FIRST_PAGE } from '@core/pagination/pagination.model';
import { NotificationDetailFacade } from '@page/notifications/core/notification-detail.facade';
import { NotificationsFacade } from '@page/notifications/core/notifications.facade';
import { PartsFacade } from '@page/parts/core/parts.facade';
import { MainAspectType } from '@page/parts/model/mainAspectType.enum';
import { Part } from '@page/parts/model/parts.model';
import { NotificationActionHelperService } from '@shared/assembler/notification-action-helper.service';
import { TableType } from '@shared/components/multi-select-autocomplete/table-type.model';
import { NotificationCommonModalComponent } from '@shared/components/notification-common-modal/notification-common-modal.component';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { ToastService } from '@shared/components/toasts/toast.service';
import { toAssetFilter } from '@shared/helper/filter-helper';
import { Notification } from '@shared/model/notification.model';
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
  public readonly partsAsBuilt$: Observable<View<Pagination<Part>>>;
  public affectedParts: Part[];
  private filteredDataCache: any[] = [];

  public readonly titleId = this.staticIdService.generateId('NotificationDetail');
  public readonly deselectPartTrigger$ = new Subject<Part[]>();
  public readonly editMode = true;

  public readonly selected$: Observable<View<Notification>>;

  public affectedPartIds: string[] = [];

  public temporaryAffectedParts: string[] = [];

  public readonly addPartTrigger$ = new Subject<Part>();
  public readonly currentSelectedAvailableParts$ = new BehaviorSubject<Part[]>([]);
  public readonly currentSelectedAffectedParts$ = new BehaviorSubject<Part[]>([]);

  private originPageNumber: number;
  private originTabIndex: number;

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
    this.selected$ = this.notificationDetailFacade.selected$;


    this.currentSelectedAvailableParts$.subscribe((parts: Part[]) => {
      console.log('Received available:', parts);
      this.temporaryAffectedParts = parts.map(part => part.id);
      console.log(this.temporaryAffectedParts, 'temp affected');
    });

    this.currentSelectedAffectedParts$.subscribe((parts: Part[]) => {
      // Do something with the emitted array of parts
      console.log('Received affected:', parts);
      this.temporaryAffectedParts = parts.map(part => part.id);
      console.log(this.temporaryAffectedParts, 'temp affected');
      // Example: Update UI or perform other operations
    });
    this.paramSubscription = this.route.queryParams.subscribe(params => {

      this.originPageNumber = params.pageNumber;
      this.originTabIndex = params?.tabIndex;
    });


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
  }

  filterAvailableAssets(assetFilter: any): void {
    this.partsFacade.setPartsAsBuilt(FIRST_PAGE, DEFAULT_PAGE_SIZE, this.tableAsBuiltSortList, toAssetFilter(assetFilter, true));
  }


  // TODO implement save / detection change
  public clickedSave(): void {

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

  filterDataMemoized = (() => {
    const memo = new Map<string, any[]>();

    return (data: any[]): any[] => {
      const key = JSON.stringify(data); // Use data as cache key

      // Check if the result is already memoized
      if (memo.has(key)) {
        return memo.get(key)!;
      } else {
        // Perform filtering operation
        const filteredData = data.filter(part => this.affectedPartIds.includes(part.id));

        // Cache the result
        memo.set(key, filteredData);
        return filteredData;
      }
    };
  })();

  filterOnlyAffected(parts: any): Pagination<Part> {
    console.log('Incoming parts', parts.content);
    console.log('affectedPartIds', this.affectedPartIds);

    // TODO performance
    const partsFiltered = parts.content.filter(part => this.affectedPartIds.includes(part.id));

    // TODO pagination
    return {
      page: 0,
      pageCount: 0,
      pageSize: 0,
      totalItems: 0,
      content: partsFiltered,
    };
  }

  removeSelectedPartsToAffectedParts() {

  }

  addSelectedPartsToAffectedParts() {
    this.temporaryAffectedParts.forEach(value => this.affectedPartIds.push(value));
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
          this.affectedPartIds = notification.assetIds;
        }),
      )
      .subscribe();
  }

  protected readonly TableType = TableType;
  protected readonly MainAspectType = MainAspectType;
}
