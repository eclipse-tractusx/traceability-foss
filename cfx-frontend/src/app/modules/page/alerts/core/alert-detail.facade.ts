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
import { AlertDetailState } from '@page/alerts/core/alert-detail.state';
import { Part } from '@page/parts/model/parts.model';
import { Notification } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { FormatPartlistSemanticDataModelToCamelCasePipe } from '@shared/pipes/format-partlist-semantic-data-model-to-camelcase.pipe';
import { PartsService } from '@shared/service/parts.service';
import { Observable, of, Subscription } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { SortDirection } from '../../../../mocks/services/pagination.helper';

@Injectable()
export class AlertDetailFacade {
  private notificationPartsInformationDescription: Subscription;
  private supplierPartsSubscription: Subscription;

  constructor(
    private readonly partsService: PartsService,
    private readonly alertDetailState: AlertDetailState,
    private readonly formatPartlistSemanticDataModelToCamelCasePipe: FormatPartlistSemanticDataModelToCamelCasePipe,
  ) {
  }

  public get notificationPartsInformation$(): Observable<View<Part[]>> {
    return this.alertDetailState.alertPartsInformation$;
  }

  public get supplierPartsInformation$(): Observable<View<Part[]>> {
    return this.alertDetailState.supplierPartsInformation$;
  }

  public get selected$(): Observable<View<Notification>> {
    return this.alertDetailState.selected$;
  }

  public set selected(selectedAlert: View<Notification>) {
    this.alertDetailState.selected = selectedAlert;
  }

  public get selected(): View<Notification> {
    return this.alertDetailState.selected;
  }

  public setAlertPartsInformation(notification: Notification): void {
    this.notificationPartsInformationDescription?.unsubscribe();
    this.alertDetailState.alertPartsInformation = { loader: true };

    if (!notification.assetIds.length) {
      this.alertDetailState.alertPartsInformation = { data: [] };
      return;
    }

    this.notificationPartsInformationDescription = this.partsService
      .getPartDetailOfIds(notification.assetIds)
      .subscribe({
        next: data => {
          this.formatPartlistSemanticDataModelToCamelCasePipe.transform(data);
          this.alertDetailState.alertPartsInformation = { data };
        },
        error: error => (this.alertDetailState.alertPartsInformation = { error }),
      });

  }

  public setAndSupplierPartsInformation(): void {
    this.supplierPartsSubscription?.unsubscribe();
    this.alertDetailState.supplierPartsInformation = { loader: true };

    this.supplierPartsSubscription = this.alertDetailState.alertPartsInformation$
      .pipe(
        filter(view => !!view.data),
        map(({ data }) => this.getIdsFromPartList(data)),
        switchMap(affectedPartIds => (!!affectedPartIds && !!affectedPartIds.length ? this.partsService.getPartDetailOfIds(affectedPartIds) : of([]))),
      )
      .subscribe({
        next: data => {
          this.formatPartlistSemanticDataModelToCamelCasePipe.transform(data);
          this.alertDetailState.supplierPartsInformation = { data };
        },
        error: error => (this.alertDetailState.supplierPartsInformation = { error }),
      });
  }

  public sortNotificationParts(key: string, direction: SortDirection): void {
    const { data } = this.alertDetailState.alertPartsInformation;
    if (!data) return;

    const sortedData = this.partsService.sortParts(data, key, direction);
    this.alertDetailState.alertPartsInformation = { data: [ ...sortedData ] };
  }

  public sortSupplierParts(key: string, direction: SortDirection): void {
    const { data } = this.alertDetailState.supplierPartsInformation;
    if (!data) return;

    const sortedData = this.partsService.sortParts(data, key, direction);
    this.alertDetailState.supplierPartsInformation = { data: [ ...sortedData ] };
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
