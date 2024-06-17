import { TestBed } from '@angular/core/testing';
import { Policy } from '@page/policies/model/policy.model';
import { View } from '@shared/model/view.model';
import { PoliciesState } from './policies.state';

describe('PoliciesState', () => {
  let policiesState: PoliciesState;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ PoliciesState ],
    });
    policiesState = TestBed.inject(PoliciesState);
  });

  it('should be created', () => {
    expect(policiesState).toBeTruthy();
  });

  it('should set and get policies correctly', () => {
    const mockPolicies: Policy[] = [
      { policyId: '1', policyName: 'Policy 1', validUntil: '123', permissions: [], createdOn: '' },
      { policyId: '2', policyName: 'Policy 2', validUntil: '123', permissions: [], createdOn: '' },
    ];
    const mockView: View<Policy[]> = {
      data: mockPolicies,
      loader: false,
      error: undefined,
    };

    // Set policies
    policiesState.policies = mockView;

    // Verify get policies$
    policiesState.policies$.subscribe((policies) => {
      expect(policies).toEqual(mockView);
    });
  });

  it('should set and get selected policy correctly', () => {
    const mockSelectedPolicy: Policy = {
      policyId: '1',
      policyName: 'Selected Policy',
      validUntil: '123',
      permissions: [],
      createdOn: '',
    };
    const mockView: View<Policy> = {
      data: mockSelectedPolicy,
      loader: false,
      error: undefined,
    };

    // Set selected policy
    policiesState.selectedPolicy = mockView;

    // Verify get selectedPolicy$
    policiesState.selectedPolicy$.subscribe((selectedPolicy) => {
      expect(selectedPolicy).toEqual(mockView);
    });

    // Verify get selectedPolicy
    expect(policiesState.selectedPolicy).toEqual(mockView);
  });
});
