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

import { FormatPartlistSemanticDataModelToCamelCasePipe} from '@shared/pipes/format-partlist-semantic-data-model-to-camelcase.pipe';
import { Injectable } from '@angular/core';
import { InvestigationDetailState } from '@page/investigations/core/investigation-detail.state';
import { Part } from '@page/parts/model/parts.model';
import { Notification } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { PartsService } from '@shared/service/parts.service';
import { Observable, of, Subscription } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { SortDirection } from '../../../../mocks/services/pagination.helper';

@Injectable()
export class InvestigationDetailFacade {
  private notificationPartsInformationDescription: Subscription;
  private supplierPartsSubscription: Subscription;

  constructor(
    private readonly partsService: PartsService,
    private readonly investigationDetailState: InvestigationDetailState,
    private readonly formatPartlistSemanticDataModelToCamelCasePipe: FormatPartlistSemanticDataModelToCamelCasePipe

  ) {
  }

  public get notificationPartsInformation$(): Observable<View<Part[]>> {
    return this.investigationDetailState.investigationPartsInformation$;
  }

  public get supplierPartsInformation$(): Observable<View<Part[]>> {
    return this.investigationDetailState.supplierPartsInformation$;
  }

  public get selected$(): Observable<View<Notification>> {
    return this.investigationDetailState.selected$;
  }

  public set selected(selectedInvestigation: View<Notification>) {
    this.investigationDetailState.selected = selectedInvestigation;
  }

  public get selected(): View<Notification> {
    return this.investigationDetailState.selected;
  }

  public setInvestigationPartsInformation(notification: Notification): void {
    this.notificationPartsInformationDescription?.unsubscribe();
    this.investigationDetailState.investigationPartsInformation = { loader: true };

    if (!notification.assetIds.length) {
      this.investigationDetailState.investigationPartsInformation = { data: [] };
      return;
    }

    this.notificationPartsInformationDescription = this.partsService
      .getPartDetailOfIds(notification.assetIds)
      .subscribe({
        next: data => {
          this.formatPartlistSemanticDataModelToCamelCasePipe.transform(data);
          this.investigationDetailState.investigationPartsInformation = { data };
        },
        error: error => (this.investigationDetailState.investigationPartsInformation = { error }),
      });
  }

  public setAndSupplierPartsInformation(): void {
    this.supplierPartsSubscription?.unsubscribe();
    this.investigationDetailState.supplierPartsInformation = { loader: true };

    this.supplierPartsSubscription = this.investigationDetailState.investigationPartsInformation$
      .pipe(
        filter(view => !!view.data),
        map(({ data }) => this.getIdsFromPartList(data)),
        switchMap(affectedPartIds => (!!affectedPartIds && !!affectedPartIds.length ? this.partsService.getPartDetailOfIds(affectedPartIds) : of([]))),
      )
      .subscribe({
        next: data => {
          this.formatPartlistSemanticDataModelToCamelCasePipe.transform(data);
          this.investigationDetailState.supplierPartsInformation = { data };
        },
        error: error => (this.investigationDetailState.supplierPartsInformation = { error }),
      });
  }

  public sortNotificationParts(key: string, direction: SortDirection): void {
    const { data } = this.investigationDetailState.investigationPartsInformation;
    if (!data) return;

    const sortedData = this.partsService.sortParts(data, key, direction);
    this.investigationDetailState.investigationPartsInformation = { data: [ ...sortedData ] };
  }

  public sortSupplierParts(key: string, direction: SortDirection): void {
    const { data } = this.investigationDetailState.supplierPartsInformation;
    if (!data) return;

    const sortedData = this.partsService.sortParts(data, key, direction);
    this.investigationDetailState.supplierPartsInformation = { data: [ ...sortedData ] };
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
