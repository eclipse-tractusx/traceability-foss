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
// TODO: Decide if long term a Policy state, facade, ResponseType and Assembler is needed
// TODO: Align with BE to a first valid Policy Model, changes seem to be happen frequently
export interface Policy {
  policyId: string;
  createdOn: string;
  validUntil: string;
  permissions?: PolicyPermission[];
}

export interface PolicyPermission {
  action: PolicyType;
  constraints?: Constraint[];
}

export enum PolicyType {
  ACCESS="ACCESS",
  USE="USE"
}

export interface Constraint {
  leftOperand: string;
  operator: OperatorType;
  rightOperand: string[];
}

export enum OperatorType {
    EQ = 'eq',
    NEQ = 'neq',
    LT = 'lt',
    GT = 'gt',
    IN = 'in',
    LTEQ = 'lteq',
    GTEQ = 'gteq',
    ISA = 'isA',
    HASPART = 'hasPart',
    ISPARTOF = 'isPartOf',
    ISONEOF = 'isOneOf',
    ISALLOF = 'isAllOf',
    ISNONEOF = 'isNoneOf',
}
