import { TestBed } from '@angular/core/testing';
import { PoliciesState } from '@page/admin/presentation/policy-management/policies/policies.state'; // Adjust the path as necessary
import { PoliciesAssembler } from '@page/admin/presentation/policy-management/policies/policy.assembler'; // Adjust the path as necessary
import { Policy, PolicyEntry } from '@page/policies/model/policy.model'; // Adjust the path as necessary
import { PolicyService } from '@shared/service/policy.service'; // Adjust the path as necessary
import { of, throwError } from 'rxjs';
import { PoliciesFacade } from './policies.facade'; // Adjust the path as necessary

describe('PoliciesFacade', () => {
  let facade: PoliciesFacade;
  let policyServiceSpy: jasmine.SpyObj<PolicyService>;
  let policiesStateSpy: jasmine.SpyObj<PoliciesState>;

  beforeEach(() => {
    const policyServiceMock = jasmine.createSpyObj('PolicyService', [ 'getPolicies', 'getPolicyById', 'deletePolicy', 'createPolicy', 'updatePolicy' ]);
    const policiesStateMock = jasmine.createSpyObj('PoliciesState', [ 'policies$', 'selectedPolicy$', 'policies', 'selectedPolicy' ]);

    TestBed.configureTestingModule({
      providers: [
        PoliciesFacade,
        { provide: PolicyService, useValue: policyServiceMock },
        { provide: PoliciesState, useValue: policiesStateMock },
      ],
    });

    facade = TestBed.inject(PoliciesFacade);
    policyServiceSpy = TestBed.inject(PolicyService) as jasmine.SpyObj<PolicyService>;
    policiesStateSpy = TestBed.inject(PoliciesState) as jasmine.SpyObj<PoliciesState>;
  });

  it('should be created', () => {
    expect(facade).toBeTruthy();
  });

  describe('setPolicies', () => {

    it('should handle error when fetching policies', () => {
      const mockError = new Error('Test error');
      policyServiceSpy.getPolicies.and.returnValue(throwError(mockError));

      facade.setPolicies();

      expect(policyServiceSpy.getPolicies).toHaveBeenCalled();
      expect(policiesStateSpy.policies).toEqual({ error: mockError });
    });
  });

  describe('selectedPolicy', () => {
    it('should get selected policy', () => {
      const mockPolicy = { policyName: 'Test Policy' } as Policy;
      policiesStateSpy.selectedPolicy = { data: mockPolicy };

      expect(facade.selectedPolicy).toBe(mockPolicy);
    });

    it('should set selected policy', () => {
      const mockPolicy = { policyName: 'Test Policy' } as Policy;

      facade.selectedPolicy = mockPolicy;

      expect(policiesStateSpy.selectedPolicy).toEqual({ data: mockPolicy });
    });
  });

  describe('setSelectedPolicyById', () => {
    it('should fetch and set selected policy by ID', () => {
      const mockPolicy = { policyName: 'Test Policy' } as Policy;
      policyServiceSpy.getPolicyById.and.returnValue(of(mockPolicy));
      spyOn(PoliciesAssembler, 'assemblePolicy').and.returnValue(mockPolicy);

      facade.setSelectedPolicyById('test-id');

      expect(policyServiceSpy.getPolicyById).toHaveBeenCalledWith('test-id');
      expect(policiesStateSpy.selectedPolicy).toEqual({ data: mockPolicy });
    });

    it('should handle error when fetching policy by ID', () => {
      const mockError = new Error('Test error');
      policyServiceSpy.getPolicyById.and.returnValue(throwError(mockError));

      facade.setSelectedPolicyById('test-id');

      expect(policyServiceSpy.getPolicyById).toHaveBeenCalledWith('test-id');
      expect(policiesStateSpy.selectedPolicy).toEqual({ error: mockError });
    });
  });

  describe('unsubscribePolicies', () => {
    it('should unsubscribe from policies subscriptions', () => {
      const mockPoliciesSubscription = jasmine.createSpyObj('Subscription', [ 'unsubscribe' ]);
      const mockSelectedPoliciesSubscription = jasmine.createSpyObj('Subscription', [ 'unsubscribe' ]);
      facade['policiesSubscription'] = mockPoliciesSubscription;
      facade['selectedPoliciesSubscription'] = mockSelectedPoliciesSubscription;

      facade.unsubscribePolicies();

      expect(mockPoliciesSubscription.unsubscribe).toHaveBeenCalled();
      expect(mockSelectedPoliciesSubscription.unsubscribe).toHaveBeenCalled();
    });
  });

  describe('deletePolicies', () => {
    it('should call policyService.deletePolicies with policy IDs', () => {
      const mockPolicies = [ { policyId: '1' }, { policyId: '2' } ] as Policy[];
      const mockDeleteResponse1 = of(null);
      const mockDeleteResponse2 = of(null);

      policyServiceSpy.deletePolicy.withArgs('1').and.returnValue(mockDeleteResponse1);
      policyServiceSpy.deletePolicy.withArgs('2').and.returnValue(mockDeleteResponse2);

      const result = facade.deletePolicies(mockPolicies);

      // Expect the policyService.deletePolicy to be called with each policyId
      expect(policyServiceSpy.deletePolicy).toHaveBeenCalledWith('1');
      expect(policyServiceSpy.deletePolicy).toHaveBeenCalledWith('2');

      // Expect the result to be an observable that emits an array of results
      result.subscribe(responses => {
        expect(responses).toEqual([ null, null ]);
      });
    });
  });

  describe('createPolicy', () => {
    it('should call policyService.createPolicy with policy entry', () => {
      const mockPolicyEntry = {
        policyName: 'New Policy',
        validUntil: null,
        payload: null,
        businessPartnerNumber: '',
      } as PolicyEntry;
      const mockCreateResponse = of(null);
      policyServiceSpy.createPolicy.and.returnValue(mockCreateResponse);

      const result = facade.createPolicy(mockPolicyEntry);

      expect(policyServiceSpy.createPolicy).toHaveBeenCalledWith(mockPolicyEntry);
      expect(result).toBe(mockCreateResponse);
    });
  });

  describe('updatePolicy', () => {
    it('should call policyService.updatePolicy with policy entry', () => {
      const mockPolicyEntry = {
        policyName: 'New Policy',
        validUntil: null,
        payload: null,
        businessPartnerNumber: '',
      } as PolicyEntry;
      const mockUpdateResponse = of(null);
      policyServiceSpy.updatePolicy.and.returnValue(mockUpdateResponse);

      const result = facade.updatePolicy(mockPolicyEntry);

      expect(policyServiceSpy.updatePolicy).toHaveBeenCalledWith(mockPolicyEntry);
      expect(result).toBe(mockUpdateResponse);
    });
  });
});
