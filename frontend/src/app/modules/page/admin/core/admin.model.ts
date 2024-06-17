/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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


import { FormArray, FormControl, FormGroup } from '@angular/forms';
import { CalendarDateModel } from '@core/model/calendar-date.model';
import { Pagination, PaginationResponse } from '@core/model/pagination.model';

export enum KnownAdminRoutes {
  BPN = 'configure-bpn',
  IMPORT = 'configure-import',
  CONTRACT = 'contracts',
  CONTRACT_DETAIL_VIEW = 'contracts/:contractId',
  POLICY_MANAGEMENT = 'policies',
  POLICY_MANAGEMENT_EDIT = 'policies/edit/:policyId',
  POLICY_MANAGEMENT_CREATE = 'policies/create',
  POLICY_MANAGEMENT_DETAIL_VIEW = 'policies/:policyId',
}



export interface BpnConfigResponse {
  organization?: string;
  edcType?: string;
  providedBy?: string;
  bpn: string;
  url: string;
}

export interface BpnConfig {
  bpn: string;
  url: string;
}

export type BpnConfigFormGroup = FormGroup<{ bpnConfig: FormArray<FormControl<BpnConfig>> }>;

export interface Contract {
  contractId: string,
  counterpartyAddress: string,
  creationDate: CalendarDateModel,
  endDate: CalendarDateModel,
  state: ContractState,
  policy: string
}

export interface ContractResponse {
  contractId: string,
  counterpartyAddress: string,
  creationDate: string,
  endDate: string,
  state: ContractState,
  policy: string
}

export type ContractsResponse = PaginationResponse<ContractResponse>;
export type Contracts = Pagination<Contract>;
export function assembleContract(contractResponse: ContractResponse): Contract {

  return {
    contractId: contractResponse.contractId,
    counterpartyAddress: contractResponse.counterpartyAddress,
    creationDate: new CalendarDateModel(contractResponse.creationDate),
    endDate: new CalendarDateModel(contractResponse.endDate),
    state: contractResponse.state,
    policy: contractResponse.policy
  };
}

export function assembleContracts(contractResponseList: ContractResponse[]) {
  return contractResponseList.map(contractResponse => assembleContract(contractResponse));
}

export enum ContractState {
  FINALIZED = 'Finalized',
  TERMINATED = 'Terminated'
}
