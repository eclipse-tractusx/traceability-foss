import { Injectable } from '@angular/core';
import { Pagination } from '@core/model/pagination.model';
import { PoliciesState } from '@page/admin/presentation/policy-management/policies/policies.state';
import { PoliciesAssembler } from '@page/admin/presentation/policy-management/policies/policy.assembler';
import { provideDataObject } from '@page/parts/core/parts.helper';
import { Policy, PolicyEntry } from '@page/policies/model/policy.model';
import { TableHeaderSort } from '@shared/components/table/table.model';
import { View } from '@shared/model/view.model';
import { PolicyService } from '@shared/service/policy.service';
import { Observable, Subject, Subscription } from 'rxjs';
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

  get policies$(): Observable<View<Pagination<Policy>>> {
    return this.policiesState.policies$;
  }

  public setPolicies(page: number, pageSize = 50, sorting: TableHeaderSort[] = [], filter?: any): void {
    this.policiesSubscription?.unsubscribe();
    this.policiesSubscription = this.policyService.getPaginatedPolicies(page, pageSize, sorting, filter).pipe(map(response => {
      const assembled = response.content.map(policy => {
        return PoliciesAssembler.assemblePolicy(policy);
      });
      return { ...response, content: assembled } as Pagination<Policy>;
    })).subscribe({
      next: data => (this.policiesState.policies = { data: provideDataObject(data) }),
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

  deletePolicies(selectedPolicies: Policy[]) {
    return this.policyService.deletePolicies(selectedPolicies.map(policy => policy.policyId));
  }

  createPolicy(policyEntry: PolicyEntry) {
    return this.policyService.createPolicy(policyEntry);
  }
}
