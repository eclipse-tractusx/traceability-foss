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
import { startsWith } from 'lodash-es';
import { combineLatest, merge, Observable, of } from 'rxjs';
import { delay, map, startWith, switchMap, tap } from 'rxjs/operators';
import { realm } from '@core/api/api.service.properties';
import { View } from '@shared/model/view.model';
import { DashboardService } from '../core/dashboard.service';
import { DashboardState } from '../core/dashboard.state';
import { Dashboard } from '../model/dashboard.model';

@Injectable()
export class DashboardFacade {
  constructor(
    private readonly dashboardService: DashboardService,
    private readonly dashboardState: DashboardState,
    private readonly partsService: PartsService,
  ) {}

  get numberOfParts$(): Observable<View<number>> {
    return merge(of({ loader: true }), of({ data: 2822 }).pipe(delay(1500))).pipe(delay(0));
    // return this.dashboardState.numberOfParts$.pipe(delay(0));
  }

  public setNumberOfParts(): void {
    // Disabled until dashboard endpoint is available
    return;
    this.dashboardState.setNumberOfParts({ loader: true });
    this.dashboardService.getStats().subscribe({
      next: (kpiStats: Dashboard) => {
        const realmAsAKey = realm?.toLocaleUpperCase();
        const assetsCount =
          kpiStats.qualityAlertCount[realmAsAKey] && kpiStats.qualityAlertCount[realmAsAKey].length
            ? +kpiStats.qualityAlertCount[realmAsAKey][0].totalAssetsCount
            : 0;

        this.dashboardState.setNumberOfParts({ data: assetsCount });
      },
      error: error => this.dashboardState.setNumberOfParts({ error }),
    });
  }

  get assetsPerCountry$(): Observable<View<PartsCoordinates[]>> {
    return this.partsService
      .getParts(0, 1000000000, ['productionCountry', 'asc'])
      .pipe(delay(0))
      .pipe(
        map(({ content }) => {
          const countedCountries = content.reduce((p, { productionCountry }) => {
            const countryCount = p[productionCountry];
            p[productionCountry] = countryCount ? countryCount + 1 : 1;
            return p;
          }, {});

          return Object.keys(countedCountries).map(key => ({
            coordinates: CountryLocationMap[key],
            numberOfParts: countedCountries[key],
          }));
        }),
        map(data => ({ data })),
      );
  }
}
