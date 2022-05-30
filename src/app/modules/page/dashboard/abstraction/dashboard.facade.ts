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
import { Observable } from 'rxjs';
import { delay } from 'rxjs/operators';
import { realm } from '@core/api/api.service.properties';
import { View } from '@shared/model/view.model';
import { DashboardService } from '../core/dashboard.service';
import { DashboardState } from '../core/dashboard.state';
import { Dashboard } from '../model/dashboard.model';

@Injectable()
export class DashboardFacade {
  constructor(private dashboardService: DashboardService, private dashboardState: DashboardState) {}

  get numberOfParts$(): Observable<View<number>> {
    return this.dashboardState.numberOfParts$.pipe(delay(0));
  }

  public setNumberOfParts(): void {
    this.dashboardState.setNumberOfParts({ loader: true });
    this.dashboardService.getStats().subscribe({
      next: (kpiStats: Dashboard) => {
        const mspid = realm?.toLocaleUpperCase();
        const assetsCount =
          kpiStats.qualityAlertCount[mspid] && kpiStats.qualityAlertCount[mspid].length
            ? +kpiStats.qualityAlertCount[mspid][0].totalAssetsCount
            : 0;

        this.dashboardState.setNumberOfParts({ data: assetsCount });
      },
      error: error => this.dashboardState.setNumberOfParts({ error }),
    });
  }
}
