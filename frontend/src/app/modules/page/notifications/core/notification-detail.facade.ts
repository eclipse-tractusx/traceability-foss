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

import { Injectable } from '@angular/core';
import { NotificationDetailState } from '@page/notifications/core/notification-detail.state';
import { Part } from '@page/parts/model/parts.model';
import { Notification } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { FormatPartlistSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-partlist-semantic-data-model-to-camelcase.pipe';
import { PartsService } from '@shared/service/parts.service';
import { Observable, of, Subscription } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { SortDirection } from '../../../../mocks/services/pagination.helper';

@Injectable()
export class NotificationDetailFacade {
  private notificationPartsInformationDescription: Subscription;
  private supplierPartsSubscription: Subscription;

  constructor(
    private readonly partsService: PartsService,
    private readonly notificationDetailState: NotificationDetailState,
    private readonly formatPartlistSemanticDataModelToCamelCasePipe: FormatPartlistSemanticDataModelToCamelCasePipe,
  ) {
  }

  public get notificationPartsInformation$(): Observable<View<Part[]>> {
    return this.notificationDetailState.notificationPartsInformation$;
  }

  public get supplierPartsInformation$(): Observable<View<Part[]>> {
    return this.notificationDetailState.supplierPartsInformation$;
  }

  public get selected$(): Observable<View<Notification>> {
    return this.notificationDetailState.selected$;
  }

  public set selected(selectedNotification: View<Notification>) {
    this.notificationDetailState.selected = selectedNotification;
  }

  public get selected(): View<Notification> {
    return this.notificationDetailState.selected;
  }

  public setNotificationPartsInformation(notification: Notification): void {
    this.notificationPartsInformationDescription?.unsubscribe();
    this.notificationDetailState.notificationPartsInformation = { loader: true };

    if (!notification.assetIds.length) {
      this.notificationDetailState.notificationPartsInformation = { data: [] };
      return;
    }

    this.notificationPartsInformationDescription = this.partsService
      .getPartDetailOfIds(notification.assetIds)
      .subscribe({
        next: data => {
          this.formatPartlistSemanticDataModelToCamelCasePipe.transform(data);
          this.notificationDetailState.notificationPartsInformation = { data };
        },
        error: error => (this.notificationDetailState.notificationPartsInformation = { error }),
      });

  }

  public setAndSupplierPartsInformation(): void {
    this.supplierPartsSubscription?.unsubscribe();
    this.notificationDetailState.supplierPartsInformation = { loader: true };

    this.supplierPartsSubscription = this.notificationDetailState.notificationPartsInformation$
      .pipe(
        filter(view => !!view.data),
        map(({ data }) => this.getIdsFromPartList(data)),
        switchMap(partIds => (!!partIds && !!partIds.length ? this.partsService.getPartDetailOfIds(partIds) : of([]))),
      )
      .subscribe({
        next: data => {
          this.formatPartlistSemanticDataModelToCamelCasePipe.transform(data);
          this.notificationDetailState.supplierPartsInformation = { data };
        },
        error: error => (this.notificationDetailState.supplierPartsInformation = { error }),
      });
  }

  public sortNotificationParts(key: string, direction: SortDirection): void {
    const { data } = this.notificationDetailState.notificationPartsInformation;
    if (!data) return;

    const sortedData = this.partsService.sortParts(data, key, direction);
    this.notificationDetailState.notificationPartsInformation = { data: [ ...sortedData ] };
  }

  public sortSupplierParts(key: string, direction: SortDirection): void {
    const { data } = this.notificationDetailState.supplierPartsInformation;
    if (!data) return;

    const sortedData = this.partsService.sortParts(data, key, direction);
    this.notificationDetailState.supplierPartsInformation = { data: [ ...sortedData ] };
  }

  public unsubscribeSubscriptions(): void {
    this.notificationPartsInformationDescription?.unsubscribe();
    this.supplierPartsSubscription?.unsubscribe();
  }

  private getIdsFromPartList(parts: Part[]): string[] {
    const childIds = parts.map(part => part.children).reduce((p, c) => [ ...p, ...c ], []);
    return [ ...new Set(childIds) ];
  }
}
