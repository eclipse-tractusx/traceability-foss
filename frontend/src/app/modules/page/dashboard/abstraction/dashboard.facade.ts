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

import {Injectable} from '@angular/core';
import {Notifications} from '@shared/model/notification.model';
import {View} from '@shared/model/view.model';
import {AlertsService} from '@shared/service/alerts.service';
import {InvestigationsService} from '@shared/service/investigations.service';
import {Observable, Subscription} from 'rxjs';
import {DashboardService} from '../core/dashboard.service';
import {DashboardState} from '../core/dashboard.state';
import {DashboardStats} from '../model/dashboard.model';

@Injectable()
export class DashboardFacade {
  private assetNumbersSubscription: Subscription;
  private investigationsReceivedSubscription: Subscription;
  private investigationsCreatedSubscription: Subscription;
  private alertsReceivedSubscription: Subscription;
  private alertsCreatedSubscription: Subscription;


  constructor(
    private readonly dashboardService: DashboardService,
    private readonly dashboardState: DashboardState,
    // private readonly partsService: PartsService,
    private readonly investigationsService: InvestigationsService,
    private readonly alertsService: AlertsService
  ) {}

  public get numberOfTotalMyParts$(): Observable<View<number>> {
    return this.dashboardState.numberOfTotalMyParts$;
  }

  public get numberOfTotalOtherParts$(): Observable<View<number>> {
    return this.dashboardState.numberOfTotalOtherParts$;
  }


  public get numberOfMyPartsWithOpenInvestigations$(): Observable<View<number>> {
    return this.dashboardState.numberOfMyPartsWithOpenInvestigations$;
  }

  public get recentReceivedInvestigations$(): Observable<View<Notifications>> {
    return this.dashboardState.recentReceivedInvestigations$;
  }

  public get recentCreatedInvestigations$(): Observable<View<Notifications>> {
    return this.dashboardState.recentCreatedInvestigations$;
  }

  public get recentReceivedAlerts$(): Observable<View<Notifications>> {
    return this.dashboardState.recentReceivedAlerts$;
  }

  public get recentCreatedAlerts$(): Observable<View<Notifications>> {
    return this.dashboardState.recentCreatedAlerts$;
  }

  public get numberOfOwnOpenInvestigationsReceived$(): Observable<View<number>> {
    return this.dashboardState.numberOfOwnOpenInvestigationsReceived$;
  }
  public get numberOfOwnOpenInvestigationsCreated$(): Observable<View<number>> {
    return this.dashboardState.numberOfOwnOpenInvestigationsCreated$;
  }
  public get numberOfOwnOpenAlertsReceived$(): Observable<View<number>> {
    return this.dashboardState.numberOfOwnOpenAlertsReceived$;
  }
  public get numberOfOwnOpenAlertsCreated$(): Observable<View<number>> {
    return this.dashboardState.numberOfOwnOpenAlertsCreated$;
  }

  public setDashboardData(): void {
    this.setAssetNumbers();
    this.setReceivedInvestigations();
    this.setCreatedInvestigations();
    this.setReceivedAlerts();
    this.setCreatedAlerts();
  }

  private setAssetNumbers(): void {
    this.dashboardState.setNumberOfTotalMyParts({ loader: true });
    this.dashboardState.setNumberOfTotalOtherParts({loader: true});

    this.dashboardState.setNumberOfAsBuiltOwnParts({loader: true});
    this.dashboardState.setNumberOfAsPlannedOwnParts({loader: true});
    this.dashboardState.setNumberOfAsBuiltSupplierParts({loader: true});
    this.dashboardState.setNumberOfAsPlannedSupplierParts({loader: true});
    this.dashboardState.setNumberOfAsBuiltCustomerParts({loader: true});
    this.dashboardState.setNumberOfAsPlannedCustomerParts({loader: true});

    this.dashboardState.setNumberOfOwnOpenInvestigationsReceived({loader:true})
    this.dashboardState.setNumberOfOwnOpenAlertsReceived({loader:true})
    this.dashboardState.setNumberOfOwnOpenInvestigationsCreated({loader:true})
    this.dashboardState.setNumberOfOwnOpenAlertsCreated({loader:true})


    this.assetNumbersSubscription?.unsubscribe();
    this.assetNumbersSubscription = this.dashboardService.getStats().subscribe({
      next: (dashboardStats: DashboardStats) => {
        this.dashboardState.setNumberOfTotalMyParts({ data: dashboardStats.totalOwnParts });
        this.dashboardState.setNumberOfTotalOtherParts({data: dashboardStats.totalOtherParts});

        this.dashboardState.setNumberOfAsBuiltOwnParts({data: dashboardStats.asBuiltOwnParts});
        this.dashboardState.setNumberOfAsPlannedOwnParts({data: dashboardStats.asPlannedOwnParts});
        this.dashboardState.setNumberOfAsBuiltSupplierParts({data: dashboardStats.asBuiltSupplierParts});
        this.dashboardState.setNumberOfAsPlannedSupplierParts({data: dashboardStats.asPlannedSupplierParts});
        this.dashboardState.setNumberOfAsBuiltCustomerParts({data: dashboardStats.asBuiltCustomerParts});
        this.dashboardState.setNumberOfAsPlannedCustomerParts({data: dashboardStats.asPlannedCustomerParts});

        this.dashboardState.setNumberOfOwnOpenInvestigationsReceived({data: dashboardStats.ownOpenInvestigationsReceived})
        this.dashboardState.setNumberOfOwnOpenAlertsReceived({data: dashboardStats.ownOpenAlertsReceived})
        this.dashboardState.setNumberOfOwnOpenInvestigationsCreated({data: dashboardStats.ownOpenInvestigationsCreated})
        this.dashboardState.setNumberOfOwnOpenAlertsCreated({data: dashboardStats.ownOpenAlertsCreated})


      },
      error: error => {
        this.dashboardState.setNumberOfTotalMyParts({ error });
        this.dashboardState.setNumberOfTotalOtherParts({error});

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

        this.dashboardState.setNumberOfOwnOpenInvestigationsReceived({error})
        this.dashboardState.setNumberOfOwnOpenAlertsReceived({error})
        this.dashboardState.setNumberOfOwnOpenInvestigationsCreated({error})
        this.dashboardState.setNumberOfOwnOpenAlertsCreated({error})
      },
    });
  }

  public stopDataLoading(): void {
    this.assetNumbersSubscription?.unsubscribe();
    this.investigationsReceivedSubscription?.unsubscribe();
    this.investigationsCreatedSubscription?.unsubscribe();
    this.alertsReceivedSubscription?.unsubscribe();
    this.alertsCreatedSubscription?.unsubscribe();
  }

  private setReceivedInvestigations(): void {
    this.investigationsReceivedSubscription?.unsubscribe();
    this.investigationsReceivedSubscription = this.investigationsService.getReceivedInvestigations(0, 5, [['createdDate','desc']]).subscribe({
      next: data => this.dashboardState.setRecentReceivedInvestigations({ data }),
      error: (error: Error) => this.dashboardState.setRecentReceivedInvestigations({ error }),
    });
  }

  private setCreatedInvestigations(): void {
    this.investigationsCreatedSubscription?.unsubscribe();
    this.investigationsCreatedSubscription = this.investigationsService.getCreatedInvestigations(0, 5, [['createdDate','desc']]).subscribe({
      next: data => this.dashboardState.setRecentCreatedInvestigations({ data }),
      error: (error: Error) => this.dashboardState.setRecentCreatedInvestigations({ error }),
    });
  }

  private setReceivedAlerts(): void {
    this.alertsReceivedSubscription?.unsubscribe();
    this.alertsReceivedSubscription = this.alertsService.getReceivedAlerts(0, 5, [['createdDate','desc']]).subscribe({
      next: data => this.dashboardState.setRecentReceivedAlerts({data}),
      error: (error: Error) => this.dashboardState.setRecentReceivedAlerts({ error }),
    });
  }


  private setCreatedAlerts(): void {
    this.alertsCreatedSubscription?.unsubscribe();
    this.alertsCreatedSubscription = this.alertsService.getCreatedAlerts(0, 5, [['createdDate','desc']]).subscribe({
      next: data => this.dashboardState.setRecentCreatedAlerts({data}),
      error: (error: Error) => this.dashboardState.setRecentCreatedAlerts({ error }),
    });
  }
}
