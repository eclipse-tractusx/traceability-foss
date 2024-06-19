import { Injectable } from '@angular/core';
import { PoliciesState } from '@page/admin/presentation/policy-management/policies/policies.state';
import { PoliciesAssembler } from '@page/admin/presentation/policy-management/policies/policy.assembler';
import { Policy, PolicyEntry } from '@page/policies/model/policy.model';
import { View } from '@shared/model/view.model';
import { PolicyService } from '@shared/service/policy.service';
import { forkJoin, Observable, Subject, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class PoliciesFacade {
  private policiesSubscription: Subscription;
  private selectedPoliciesSubscription: Subscription;
  private readonly unsubscribeTrigger = new Subject<void>();


  constructor(private readonly policyService: PolicyService,
              private readonly policiesState: PoliciesState,
  ) {
  }

  get policies$(): Observable<View<Policy[]>> {
    return this.policiesState.policies$;
  }

  public setPolicies(): void {
    this.policiesSubscription?.unsubscribe();
    this.policiesSubscription = this.policyService.getPolicies().pipe(map(response => {
      return PoliciesAssembler.mapToPolicyEntryList(response).map(entry => entry.payload.policy).map(policy => PoliciesAssembler.assemblePolicy(policy));
    })).subscribe({
      next: data => (this.policiesState.policies = { data: data }),
      error: error => (this.policiesState.policies = { error }),
    });
  }

  get selectedPolicy$(): Observable<View<Policy>> {
    return this.policiesState.selectedPolicy$;
  }

  get selectedPolicy(): Policy {
    return this.policiesState.selectedPolicy?.data;
  }


  public set selectedPolicy(policy: Policy) {
    this.policiesState.selectedPolicy = { data: policy };
  }

  public setSelectedPolicyById(policyId: string): void {
    this.policyService.getPolicyById(policyId).subscribe({
      next: data => (this.policiesState.selectedPolicy = { data: PoliciesAssembler.assemblePolicy(data) }),
      error: error => (this.policiesState.selectedPolicy = { error }),
    });
  }


  public unsubscribePolicies(): void {
    this.policiesSubscription?.unsubscribe();
    this.selectedPoliciesSubscription?.unsubscribe();
    this.unsubscribeTrigger.next();
  }

  deletePolicies(selectedPolicies: Policy[]): Observable<any> {
    const deleteRequests = selectedPolicies.map(policy =>
      this.policyService.deletePolicy(policy.policyId),
    );

    return forkJoin(deleteRequests);
  }

  createPolicy(policyEntry: PolicyEntry) {
    return this.policyService.createPolicy(policyEntry);
  }

  updatePolicy(policyEntry: PolicyEntry) {
    return this.policyService.updatePolicy(policyEntry);
  }
}
