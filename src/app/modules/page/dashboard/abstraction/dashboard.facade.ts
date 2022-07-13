/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Injectable } from '@angular/core';
import { CountryLocationMap, PartsCoordinates } from '@page/dashboard/presentation/map/map.model';
import { PartsService } from '@page/parts/core/parts.service';
import { Observable } from 'rxjs';
import { delay, map } from 'rxjs/operators';
import { View } from '@shared/model/view.model';
import { DashboardService } from '../core/dashboard.service';
import { DashboardState } from '../core/dashboard.state';
import { DashboardStats } from '../model/dashboard.model';

@Injectable()
export class DashboardFacade {
  constructor(
    private readonly dashboardService: DashboardService,
    private readonly dashboardState: DashboardState,
    private readonly partsService: PartsService,
  ) {}

  get numberOfMyParts$(): Observable<View<number>> {
    return this.dashboardState.numberOfMyParts$.pipe(delay(0));
  }

  get numberOfBranchParts$(): Observable<View<number>> {
    return this.dashboardState.numberOfBranchParts$.pipe(delay(0));
  }

  public setNumberOfParts(): void {
    this.dashboardState.setNumberOfMyParts({ loader: true });
    this.dashboardState.setNumberOfBranchParts({ loader: true });
    this.dashboardService.getStats().subscribe({
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

  get assetsPerCountry$(): Observable<View<PartsCoordinates[]>> {
    return this.partsService
      .getPartsPerCountry()
      .pipe(delay(0))
      .pipe(
        map(partsCountriesMap => {
          return Object.keys(partsCountriesMap).map(key => ({
            coordinates: CountryLocationMap[key.toUpperCase()].coordinates,
            numberOfParts: partsCountriesMap[key],
          }));
        }),
        map(data => ({ data })),
      );
  }
}
