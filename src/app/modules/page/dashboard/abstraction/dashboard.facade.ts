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
import { CountryLocationMap, PartsCoordinates } from '@page/dashboard/presentation/map/map.model';
import { View } from '@shared/model/view.model';
import { PartsService } from '@shared/service/parts.service';
import { Observable, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { DashboardService } from '../core/dashboard.service';
import { DashboardState } from '../core/dashboard.state';
import { DashboardStats } from '../model/dashboard.model';

@Injectable()
export class DashboardFacade {
  private assetNumbersSubscription: Subscription;
  private assetsPerCountrySubscription: Subscription;

  constructor(
    private readonly dashboardService: DashboardService,
    private readonly dashboardState: DashboardState,
    private readonly partsService: PartsService,
  ) {}

  public get numberOfMyParts$(): Observable<View<number>> {
    return this.dashboardState.numberOfMyParts$;
  }

  public get numberOfBranchParts$(): Observable<View<number>> {
    return this.dashboardState.numberOfBranchParts$;
  }

  public get assetsPerCountry$(): Observable<View<PartsCoordinates[]>> {
    return this.dashboardState.assetsPerCountry$;
  }

  public setDashboardData(): void {
    this.setAssetNumbers();
    this.setAssetsPerCountry();
  }

  private setAssetNumbers(): void {
    this.dashboardState.setNumberOfMyParts({ loader: true });
    this.dashboardState.setNumberOfBranchParts({ loader: true });

    this.assetNumbersSubscription?.unsubscribe();
    this.assetNumbersSubscription = this.dashboardService.getStats().subscribe({
      next: (dashboardStats: DashboardStats) => {
        this.dashboardState.setNumberOfMyParts({ data: dashboardStats.myItems });
        this.dashboardState.setNumberOfBranchParts({ data: dashboardStats.branchItems });
      },
      error: error => {
        this.dashboardState.setNumberOfMyParts({ error });
        this.dashboardState.setNumberOfBranchParts({ error });
      },
    });
  }

  private setAssetsPerCountry(): void {
    this.dashboardState.setNumberOfBranchParts({ loader: true });
    this.assetsPerCountrySubscription?.unsubscribe();

    this.assetsPerCountrySubscription = this.partsService
      .getPartsPerCountry()
      .pipe(
        map(partsCountriesMap => {
          return Object.keys(partsCountriesMap).map(key => ({
            coordinates: CountryLocationMap[key.toUpperCase()].coordinates,
            numberOfParts: partsCountriesMap[key],
          }));
        }),
      )
      .subscribe({
        next: data => this.dashboardState.setAssetsPerCountry({ data }),
        error: error => this.dashboardState.setAssetsPerCountry({ error }),
      });
  }

  public stopDataLoading(): void {
    this.assetNumbersSubscription?.unsubscribe();
    this.assetsPerCountrySubscription?.unsubscribe();
  }
}
