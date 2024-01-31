import { Injectable } from '@angular/core';
import { ApiService } from '@core/api/api.service';
import { environment } from '@env';
import { Policy } from '@page/policies/model/policy.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PolicyService {
  private readonly url = environment.apiUrl;
  constructor(private readonly apiService: ApiService) {}

  getPolicies(): Observable<Policy[]> {
    return this.apiService
        .getBy<Policy[]>(`${this.url}/policies`);
  }

  publishAssets(assetIds: string[],policyId: string) {
    return this.apiService.post(`${this.url}/assets/publish`, {assetIds, policyId});
  }
}
