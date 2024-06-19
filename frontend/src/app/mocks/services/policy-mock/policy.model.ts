import { OperatorType, Policy, PolicyAction, PolicyResponseMap } from '@page/policies/model/policy.model';

/********************************************************************************
 * Copyright (c) 2022, 2023, 2024 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/
// For now Mocks are built by current response (any). This is because Policy Model changes frequently
export const getPolicies = (): Policy[] => {
  return mockedPolicyList;
};

export const getPolicyById = (policyId: string | ReadonlyArray<string>): Policy => {
  return mockedPolicyList.filter(policy => policy.policyId === policyId)[0];
};


export const mockedPolicyList: Policy[] = [
  {
    'policyId': 'default-policy',
    'bpn': 'BPNL00000003CML1,BPNL00000003CNKC',
    'createdOn': '2024-05-29T06:18:40Z',
    'validUntil': '2029-05-29T06:18:40Z',
    'permissions': [
      {
        'action': PolicyAction.USE,
        'constraints': {
          'and': null,
          'or': [
            {
              'leftOperand': 'cx-policy:FrameworkAgreement',
              'operatorTypeResponse': OperatorType.EQ,
              'rightOperand': 'traceability:1.0',
            },
            {
              'leftOperand': 'cx-policy:UsagePurpose',
              'operatorTypeResponse': OperatorType.EQ,
              'rightOperand': 'cx.core.industrycore:1',
            },
          ],
        },
      },
    ],
  },
  {
    'policyId': 'default-policy-2',
    'bpn': 'BPNL00000003CML1',
    'createdOn': '2024-05-29T06:18:40Z',
    'validUntil': '2029-05-29T06:18:40Z',
    'permissions': [
      {
        'action': PolicyAction.USE,
        'constraints': {
          'and': [
            {
              'leftOperand': 'cx-policy:FrameworkAgreement',
              'operatorTypeResponse': OperatorType.EQ,
              'rightOperand': 'traceability:1.0',
            },
            {
              'leftOperand': 'cx-policy:UsagePurpose',
              'operatorTypeResponse': OperatorType.EQ,
              'rightOperand': 'cx.core.industrycore:1',
            },
          ],
          'or': null,
        },
      },
    ],
  },
];

const mockedPolicies = {
  page: 0,
  pageCount: 0,
  pageSize: 10,
  totalItems: 2,

  content: [
    {
      bpnSelection: [ 'BPN10000000OEM0A', 'BPN10000000OEM0B' ],
      policyName: 'Mocked_Policy_Name_1',
      policyId: 'Mocked_Policy_1',
      accessType: PolicyAction.ACCESS,
      createdOn: '2024-05-29T06:18:40Z',
      validUntil: '2024-05-29T06:18:40Z',
      constraints: [ 'Membership = active', 'AND FrameworkAgreement.traceability in [active]', 'AND PURPOSE = ID 3.1 Trace' ],
      permissions: [
        {
          action: PolicyAction.USE,
          constraint: {
            and: [
              {
                leftOperand: 'PURPOSE',
                operator: {
                  id: OperatorType.EQ,
                },
                rightOperand: 'ID 3.0 Trace',
              },
            ],
            or: [
              {
                leftOperand: 'PURPOSE',
                operator: {
                  id: OperatorType.EQ,
                },
                rightOperand: 'ID 3.0 Trace',
              },
            ],
          },
        },
      ],
    },
    {
      bpnSelection: [ 'BPN10000000OEM0A', 'BPN10000000OEM0B' ],
      policyName: 'Mocked_Policy_Name_2',
      policyId: 'Mocked_Policy_2',
      accessType: PolicyAction.USE,
      createdOn: '2024-05-29T06:18:40Z',
      validUntil: '2024-05-29T06:18:40Z',
      constraints: [ 'PURPOSE = ID 3.1 Trace', 'OR PURPOSE = ID 3.0 Trace' ],
      permissions: [
        {
          action: PolicyAction.USE,
          constraint: {
            and: [
              {
                leftOperand: 'PURPOSE',
                operator: {
                  id: OperatorType.EQ,
                },
                rightOperand: 'BMW',
              },
            ],
            or: [
              {
                leftOperand: 'PURPOSE',
                operator: {
                  id: OperatorType.EQ,
                },
                rightOperand: 'ID 3.0 Trace',
              },
            ],
          },
        },
      ],
    },

  ],

};

export const MockPolicyResponseMap: PolicyResponseMap = {
  'default': [
    {
      'validUntil': '2024-06-30T11:07:00Z',
      'payload': {
        '@context': {
          'odrl': 'http://www.w3.org/ns/odrl/2/',
        },
        '@id': 'asdadasdas',
        'policy': {
          'policyId': 'asdadasdas',
          'createdOn': '2024-06-13T09:07:32.229901783Z',
          'validUntil': '2024-06-30T11:07:00Z',
          'permissions': [
            {
              'action': PolicyAction.USE,
              'constraint': {
                'and': null,
                'or': [
                  {
                    'leftOperand': 'asd',
                    'operator': {
                      '@id': OperatorType.EQ,
                    },
                    'odrl:rightOperand': 'dsa',
                  },
                ],
              },
            },
          ],
        },
      },
    },
  ],
};

