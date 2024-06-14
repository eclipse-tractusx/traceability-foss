import { Injectable } from '@angular/core';
import { ApiService } from '@core/api/api.service';
import { environment } from '@env';
import { Policy, PolicyEntry, PolicyResponseMap } from '@page/policies/model/policy.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PolicyService {
  private readonly url = environment.apiUrl;
  constructor(private readonly apiService: ApiService) {}

  getPolicies(): Observable<PolicyResponseMap> {
    return this.apiService.get<PolicyResponseMap>(`${ this.url }/policies`);
  }

  getPolicyById(policyId: string): Observable<Policy> {
    return this.apiService.getBy<Policy>(`${ this.url }/policies/${ policyId }`);
  }

  publishAssets(assetIds: string[],policyId: string) {
    return this.apiService.post(`${this.url}/assets/publish`, {assetIds, policyId});
  }

  deletePolicy(policyId: string) {
    return this.apiService.delete(`${ this.url }/policies` + policyId);
  }

  createPolicy(policyEntry: PolicyEntry): Observable<any> {
    return this.apiService.post(`${ this.url }/policies`, policyEntry);
  }

  updatePolicy(policyEntry: PolicyEntry) {
    policyEntry.policyIds = [ policyEntry.payload.policy.policyId ];

    const body = {
      policyIds: [ policyEntry.payload.policy.policyId ],
      validUntil: policyEntry.validUntil,
      businessPartnerNumbers: policyEntry.businessPartnerNumber,
    };

    return this.apiService.put(`${ this.url }/policies`, body);
  }
}
