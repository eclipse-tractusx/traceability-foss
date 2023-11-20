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

import { Injectable } from '@angular/core';
import { Notifications } from '@shared/model/notification.model';
import { View } from '@shared/model/view.model';
import { AlertsService } from '@shared/service/alerts.service';
import { InvestigationsService } from '@shared/service/investigations.service';
import { Observable, Subscription } from 'rxjs';
import { DashboardService } from '../core/dashboard.service';
import { DashboardState } from '../core/dashboard.state';
import { DashboardStats } from '../model/dashboard.model';

@Injectable()
export class DashboardFacade {
  private assetNumbersSubscription: Subscription;
  private investigationSubscription: Subscription;
  private alertsSubscription: Subscription;

  constructor(
    private readonly dashboardService: DashboardService,
    private readonly dashboardState: DashboardState,
    // private readonly partsService: PartsService,
    private readonly investigationsService: InvestigationsService,
    private readonly alertsService: AlertsService
  ) {}

  public get numberOfMyParts$(): Observable<View<number>> {
    return this.dashboardState.numberOfTotalMyParts$;
  }
/*
  public get numberOfOtherParts$(): Observable<View<number>> {
    return this.dashboardState.numberOfAsBuiltSupplierParts$.subscribe(next => next.data as number)
  }

 */
  public get numberOfMyPartsWithOpenInvestigations$(): Observable<View<number>> {
    return this.dashboardState.numberOfMyPartsWithOpenInvestigations$;
  }

  public get recentInvestigations$(): Observable<View<Notifications>> {
    return this.dashboardState.recentInvestigations$;
  }

  public get recentAlerts$(): Observable<View<Notifications>> {
    return this.dashboardState.recentAlerts$;
  }

  public setDashboardData(): void {
    this.setAssetNumbers();
    this.setInvestigations();
    this.setAlerts();
  }

  private setAssetNumbers(): void {
    this.dashboardState.setNumberOfTotalMyParts({ loader: true });

    this.dashboardState.setNumberOfAsBuiltOwnParts({loader: true});
    this.dashboardState.setNumberOfAsPlannedOwnParts({loader: true});
    this.dashboardState.setNumberOfAsBuiltSupplierParts({loader: true});
    this.dashboardState.setNumberOfAsPlannedSupplierParts({loader: true});
    this.dashboardState.setNumberOfAsBuiltCustomerParts({loader: true});
    this.dashboardState.setNumberOfAsPlannedCustomerParts({loader: true});

    this.dashboardState.setNumberOfMyPartsWithOpenInvestigations({loader: true});
    this.dashboardState.setNumberOfMyPartsWithOpenAlerts({loader:true});
    this.dashboardState.setNumberOfOtherPartsWithOpenInvestigations({loader:true});
    this.dashboardState.setNumberOfOtherPartsWithOpenAlerts({loader:true});

    this.assetNumbersSubscription?.unsubscribe();
    this.assetNumbersSubscription = this.dashboardService.getStats().subscribe({
      next: (dashboardStats: DashboardStats) => {
        this.dashboardState.setNumberOfTotalMyParts({ data: dashboardStats.totalOwnParts });

        this.dashboardState.setNumberOfAsBuiltOwnParts({data: dashboardStats.asBuiltOwnParts});
        this.dashboardState.setNumberOfAsPlannedOwnParts({data: dashboardStats.asPlannedOwnParts});
        this.dashboardState.setNumberOfAsBuiltSupplierParts({data: dashboardStats.asBuiltSupplierParts});
        this.dashboardState.setNumberOfAsPlannedSupplierParts({data: dashboardStats.asPlannedSupplierParts});
        this.dashboardState.setNumberOfAsBuiltCustomerParts({data: dashboardStats.asBuiltCustomerParts});
        this.dashboardState.setNumberOfAsPlannedCustomerParts({data: dashboardStats.asPlannedCustomerParts});

        this.dashboardState.setNumberOfMyPartsWithOpenInvestigations({data: dashboardStats.myPartsWithOpenInvestigations});
        this.dashboardState.setNumberOfMyPartsWithOpenAlerts({data: dashboardStats.myPartsWithOpenAlerts});
        this.dashboardState.setNumberOfOtherPartsWithOpenInvestigations({data: dashboardStats.otherPartsWithOpenInvestigations});
        this.dashboardState.setNumberOfOtherPartsWithOpenAlerts({data: dashboardStats.otherPartsWithOpenAlerts});
      },
      error: error => {
        this.dashboardState.setNumberOfTotalMyParts({ error });

        this.dashboardState.setNumberOfAsBuiltOwnParts({error});
        this.dashboardState.setNumberOfAsPlannedOwnParts({error});
        this.dashboardState.setNumberOfAsBuiltSupplierParts({error});
        this.dashboardState.setNumberOfAsPlannedSupplierParts({error});
        this.dashboardState.setNumberOfAsBuiltCustomerParts({error});
        this.dashboardState.setNumberOfAsPlannedCustomerParts({error});

        this.dashboardState.setNumberOfMyPartsWithOpenInvestigations({error});
        this.dashboardState.setNumberOfMyPartsWithOpenAlerts({error});
        this.dashboardState.setNumberOfOtherPartsWithOpenInvestigations({error});
        this.dashboardState.setNumberOfOtherPartsWithOpenAlerts({error});
      },
    });
  }

  public stopDataLoading(): void {
    this.assetNumbersSubscription?.unsubscribe();
    this.investigationSubscription?.unsubscribe();
  }

  private setInvestigations(): void {
    this.investigationSubscription?.unsubscribe();
    this.investigationSubscription = this.investigationsService.getReceivedInvestigations(0, 5, [['createdDate','desc']]).subscribe({
      next: data => this.dashboardState.setInvestigationsReceived({ data }),
      error: (error: Error) => this.dashboardState.setInvestigationsReceived({ error }),
    });
  }

  private setAlerts(): void {
    this.alertsSubscription?.unsubscribe();
    this.alertsSubscription = this.alertsService.getReceivedAlerts(0, 5, [['createdDate','desc']]).subscribe({
      next: data => this.dashboardState.setRecentAlerts({data}),
      error: (error: Error) => this.dashboardState.setRecentAlerts({ error }),
    });
  }
}
