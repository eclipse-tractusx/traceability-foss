import { Injectable } from '@angular/core';
import { Policy } from '@shared/components/asset-publisher/policy.model';

@Injectable({
  providedIn: 'root'
})
export class AssetPublisherService {

  POLICIES: Policy[] = [
    {
      id: 'policy_id1',
      name: 'EDC Policy'
    },
    {
      id: 'policy_id2',
      name: 'Assets Policy',
    },
    {
      id: 'policy_id3',
      name: 'Investigations Policy'
    },
    {
      id: 'policy_id4',
      name: 'Alerts Policy',
    },
  ]

  constructor() { }

  getPolicies(): Policy[] {
    return this.POLICIES;
  }

  publishAssets(policyId: string) {
    return "successfully published assets under policy with id " + policyId;
  }
}
