import { CalendarDateModel } from '@core/model/calendar-date.model';

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
// RESPONSE

export interface PolicyResponseMap {
  [key: string]: PolicyEntry[];
}

export interface PolicyEntry {
  validUntil: string;
  payload: PolicyPayload;
}

export interface PolicyPayload {
  '@context': {
    odrl: string;
  };
  '@id': string;
  policy: Policy;
}


export interface Policy {
  // props in response
  policyId: string;
  createdOn: CalendarDateModel | string;
  validUntil: CalendarDateModel | string;
  permissions: PolicyPermission[];

  // additional props
  policyName?: string;
  bpn?: string;
  constraints?: string[]
  accessType?: PolicyAction,

}

export interface PolicyPermission {
  action: PolicyAction;
  constraint: {
    and: PolicyConstraint[];
    or: null | PolicyConstraint[];
    xone?: PolicyConstraint[];
    andsequence?: PolicyConstraint[];
  };
}

export enum PolicyAction {
  ACCESS = 'ACCESS',
  USE = 'USE'
}

export interface PolicyConstraint {
  leftOperand: string;
  operator: { '@id': OperatorType };
  'odrl:rightOperand': string;
}

export enum OperatorType {
  EQ = 'EQ',
  NEQ = 'NEQ',
  LT = 'LT',
  GT = 'GT',
  LTEQ = 'LTEQ',
  GTEQ = 'GTEQ',
  IN = 'IN',
  ISA = 'ISA',
  HASPART = 'HASPART',
  ISPARTOF = 'ISPARTOF',
  ISONEOF = 'ISONEOF',
  ISALLOF = 'ISALLOF',
  ISNONEOF = 'ISNONEOF',
}

export enum ConstraintLogicType {
  AND = 'AND',
  OR = 'OR',
  XONE = 'XONE',
  ANDSEQUENCE = 'ANDSEQUENCE'
}

export function mapToPolicyEntryList(policyResponse: PolicyResponseMap): PolicyEntry[] {
  const list: PolicyEntry[] = [];
  for (const [ key, value ] of Object.entries(policyResponse)) {
    value.forEach((entry) => {
      entry.payload.policy.bpn = key;
      entry.payload.policy.policyName = entry.payload['@id'];
      entry.payload.policy.accessType = entry.payload.policy.permissions[0].action;
      entry.payload.policy.constraints = mapDisplayPropsToPolicyRootLevel(entry);
      list.push(entry);
    });
  }
  return list;
}

function mapDisplayPropsToPolicyRootLevel(entry: PolicyEntry): string[] {
  entry.payload.policy.policyName = entry.payload['@id'];
  entry.payload.policy.accessType = entry.payload.policy.permissions[0].action;
  let constrainsList = [];
  entry.payload.policy.permissions.forEach(permission => {
    permission.constraint.and.forEach(andConstraint => {
      constrainsList.push(andConstraint.leftOperand);
      constrainsList.push(andConstraint.operator['@id']);
      constrainsList.push(andConstraint['odrl:rightOperand']);
    });
    permission.constraint?.or?.forEach(orConstraint => {
      constrainsList.push(orConstraint.leftOperand);
      constrainsList.push(orConstraint.operator['@id']);
      constrainsList.push(orConstraint['odrl:rightOperand']);
    });
  });
  return constrainsList;
}

export function getPolicyFromEntryList(policyEntryList: PolicyEntry[]): Policy[] {
  return policyEntryList.map(entry => entry.payload.policy);
}
