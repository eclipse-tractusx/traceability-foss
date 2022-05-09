import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { delay } from 'rxjs/operators';
import { realm } from 'src/app/modules/core/api/api.service.properties';
import { View } from 'src/app/modules/shared/model/view.model';
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
        const mspid = realm.toLocaleUpperCase();
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
