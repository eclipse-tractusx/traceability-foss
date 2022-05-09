import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService } from '../../../core/api/api.service';
import { Dashboard } from '../model/dashboard.model';

@Injectable()
export class DashboardService {
  constructor(private apiService: ApiService) {}

  public getStats(): Observable<Dashboard> {
    return this.apiService.post(`/kpi-stats`).pipe(map((payload: { data: Dashboard; status: number }) => payload.data));
  }
}
