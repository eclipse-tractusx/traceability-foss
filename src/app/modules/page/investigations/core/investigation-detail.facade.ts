/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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
import { InvestigationDetailState } from '@page/investigations/core/investigation-detail.state';
import { Part } from '@page/parts/model/parts.model';
import { Notification } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { PartsService } from '@shared/service/parts.service';
import { Observable, Subscription } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { SortDirection } from '../../../../mocks/services/pagination.helper';

@Injectable()
export class InvestigationDetailFacade {
  private notificationPartsInformationDescription: Subscription;
  private supplierPartsSubscription: Subscription;

  constructor(
    private readonly partsService: PartsService,
    private readonly investigationDetailState: InvestigationDetailState,
  ) {}

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

    this.notificationPartsInformationDescription = this.partsService
      .getPartDetailOfIds(notification.assetIds)
      .subscribe({
        next: data => (this.investigationDetailState.investigationPartsInformation = { data }),
        error: error => (this.investigationDetailState.investigationPartsInformation = { error }),
      });
  }

  public setSupplierPartsInformation(notification: Notification): void {
    this.supplierPartsSubscription?.unsubscribe();
    this.investigationDetailState.supplierPartsInformation = { loader: true };

    this.supplierPartsSubscription = this.partsService
      .getPartDetailOfIds(notification.assetIds)
      .pipe(switchMap(parts => this.partsService.getPartDetailOfIds(this.getIdsFromPartList(parts))))
      .subscribe({
        next: data => (this.investigationDetailState.supplierPartsInformation = { data }),
        error: error => (this.investigationDetailState.supplierPartsInformation = { error }),
      });
  }

  public sortNotificationParts(key: string, direction: SortDirection): void {
    const { data } = this.investigationDetailState.investigationPartsInformation;
    if (!data) return;

    const sortedData = this.sortParts(data, key, direction);
    this.investigationDetailState.investigationPartsInformation = { data: [...sortedData] };
  }

  public sortSupplierParts(key: string, direction: SortDirection): void {
    const { data } = this.investigationDetailState.supplierPartsInformation;
    if (!data) return;

    const sortedData = this.sortParts(data, key, direction);
    this.investigationDetailState.supplierPartsInformation = { data: [...sortedData] };
  }

  public unsubscribeSubscriptions(): void {
    this.notificationPartsInformationDescription?.unsubscribe();
    this.supplierPartsSubscription?.unsubscribe();
  }

  private sortParts(data: Part[], key: string, direction: SortDirection): Part[] {
    return data.sort((partA, partB) => {
      const a = direction === 'desc' ? partA[key] : partB[key];
      const b = direction === 'desc' ? partB[key] : partA[key];

      if (a > b) return -1;
      if (a < b) return 1;
      return 0;
    });
  }

  private getIdsFromPartList(parts: Part[]): string[] {
    const childIds = parts.map(part => part.children).reduce((p, c) => [...p, ...c], []);
    return [...new Set(childIds)];
  }
}
