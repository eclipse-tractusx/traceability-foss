import { HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApiService } from '@core/api/api.service';
import { environment } from '@env';
import { Policy, PolicyEntry } from '@page/policies/model/policy.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PolicyService {
  private readonly url = environment.apiUrl;
  constructor(private readonly apiService: ApiService) {}

  getPolicies(): Observable<Policy[]> {
    return this.apiService.get<Policy[]>(`${ this.url }/policies`);
  }

  getPolicyById(policyId: string): Observable<Policy> {
    return this.apiService.getBy<Policy>(`${ this.url }/policies/${ policyId }`);
  }

  publishAssets(assetIds: string[],policyId: string) {
    return this.apiService.post(`${this.url}/assets/publish`, {assetIds, policyId});
  }

  deletePolicies(policyIds: string[]) {
    return this.apiService.delete(`${ this.url }/policies`, new HttpParams().set('policyIds', policyIds.toString()));
  }

  createPolicy(policyEntry: PolicyEntry): Observable<any> {
    return this.apiService.post(`${ this.url }/policies`, policyEntry);
  }

  updatePolicy(policyEntry: PolicyEntry) {
    return this.apiService.put(`${ this.url }/policies/` + policyEntry.payload.policy.policyId, policyEntry);
  }
}
