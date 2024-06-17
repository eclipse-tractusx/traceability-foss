import { PoliciesAssembler } from '@page/admin/presentation/policy-management/policies/policy.assembler';
import { OperatorType, Policy, PolicyAction, PolicyEntry, PolicyResponseMap } from '@page/policies/model/policy.model';

// Mock data
const mockPolicy: Policy = {
  policyId: 'policy123',
  createdOn: '2024-01-01T00:00:00Z',
  validUntil: '2024-12-31T23:59:59Z',
  permissions: [
    {
      action: 'use' as PolicyAction,
      constraint: {
        and: [
          {
            leftOperand: 'left1',
            operator: { '@id': OperatorType.EQ },
            operatorTypeResponse: OperatorType.EQ,
            'odrl:rightOperand': 'right1',
          },
        ],
        or: [
          {
            leftOperand: 'left2',
            operator: { '@id': OperatorType.NEQ },
            operatorTypeResponse: OperatorType.NEQ,
            'odrl:rightOperand': 'right2',
          },
        ],
      },
    },
  ],

};

const mockPolicy2: Policy = {
  policyId: 'policy123',
  createdOn: '2024-01-01T00:00:00Z',
  validUntil: '2024-12-31T23:59:59Z',
  permissions: [
    {
      action: 'use' as PolicyAction,
      constraints: {
        and: [
          {
            leftOperand: 'left1',
            operator: { '@id': OperatorType.EQ },
            operatorTypeResponse: OperatorType.EQ,
            rightOperand: 'right1',
          },
        ],
        or: [
          {
            leftOperand: 'left2',
            operator: { '@id': OperatorType.NEQ },
            operatorTypeResponse: OperatorType.NEQ,
            rightOperand: 'right2',
          },
        ],
      },
    },
  ],
};

const mockPolicyResponse: PolicyResponseMap = {
  'bpn123': [
    {
      payload: {
        '@context': {
          odrl: 'test',
        },
        '@id': 'entry123',
        policy: mockPolicy,
      },
      validUntil: '2024-01-01T00:00:00Z',
    },
  ],
};

describe('PoliciesAssembler', () => {
  it('should assemble policy', () => {
    const assembledPolicy = PoliciesAssembler.assemblePolicy(mockPolicy2);
    console.log(assembledPolicy.constraints);
    expect(assembledPolicy.policyName).toBe(mockPolicy2.policyId);
    expect(assembledPolicy.createdOn).toBe('2024-01-01T00:00');
    expect(assembledPolicy.validUntil).toBe('2024-12-31T23:59');
    expect(assembledPolicy.accessType).toBe('USE');
    expect(assembledPolicy.constraints).toEqual([ 'left1', '=', 'right1', 'left2', '!=', 'right2' ]);

  });

  it('should map policy response to policy entry list', () => {
    const policyEntryList = PoliciesAssembler.mapToPolicyEntryList(mockPolicyResponse);
    expect(policyEntryList.length).toBe(1);
    expect(policyEntryList[0].payload.policy.bpn).toBe('bpn123');
    expect(policyEntryList[0].payload.policy.policyName).toBe('entry123');
  });

  it('should map display props to policy root level from policy entry', () => {
    const policyEntry: PolicyEntry = {
      validUntil: '2024-01-01T00:00:00Z',
      payload: {
        '@context': {
          odrl: 'test',
        },
        '@id': 'entry123',
        policy: mockPolicy,
      },
    };
    const constraints = PoliciesAssembler.mapDisplayPropsToPolicyRootLevelFromPolicyEntry(policyEntry);
    expect(constraints).toEqual([
      'left1', '=', 'right1', 'left2', '!=', 'right2',
    ]);
  });

  it('should map display props to policy root level from policy', () => {
    const constraints = PoliciesAssembler.mapDisplayPropsToPolicyRootLevelFromPolicy(mockPolicy2);
    expect(constraints).toEqual([
      'left1', '=', 'right1', 'left2', '!=', 'right2',
    ]);
  });
});
