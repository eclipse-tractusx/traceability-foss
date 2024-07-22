/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
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

import { PaginationResponse } from '@core/model/pagination.model';
import { BpnConfigResponse, ContractResponse, ContractState, ContractType } from '@page/admin/core/admin.model';


export const getBpnConfig = (): BpnConfigResponse[] => [
  {
    organization: 'OEM A',
    edcType: 'Provider',
    providedBy: 'BMW EDC',
    bpn: 'BPNL000000TESTRE',
    url: 'https://test.de/BPNL000000TESTRE',
  },
  {
    organization: 'OEM B',
    edcType: 'Provider',
    providedBy: 'Mercedes Benz EDC',
    bpn: 'BPNL000000TESTTH',
    url: 'http://test.de/BPNL000000TESTTH',
  },
  {
    organization: 'OEM C',
    edcType: 'Provider',
    providedBy: 'SAP (VW EDC)',
    bpn: 'BPNL000000TESTQP',
    url: 'www.test.de/BPNL000000TESTQP',
  },
  {
    organization: 'Tier A',
    edcType: 'Provider',
    providedBy: 'ZF',
    bpn: 'BPNL000000TESTOM',
    url: 'https://www.test.de/BPNL000000TESTOM',
  },
  {
    organization: 'Sub Tier A',
    edcType: 'Provider',
    providedBy: 'ZF',
    bpn: 'BPNL000000TESTNX',
    url: 'http://www.test.de/BPNL000000TESTNX',
  },
  {
    organization: 'Tier B',
    edcType: 'Provider',
    providedBy: 'Bosch',
    bpn: 'BPNL000000TESTMJ',
    url: 'test.de:9012/BPNL000000TESTMJ',
  },
  {
    organization: 'N-Tier A',
    edcType: 'Provider',
    providedBy: 'BASF',
    bpn: 'BPNL000000TESTQ0',
    url: 'test.de/BPNL000000TESTQ0',
  },
  {
    organization: 'Sub Tier B',
    edcType: 'Provider',
    providedBy: 'Henkel',
    bpn: 'BPNL000000TESTS3',
    url: 'test.de/BPNL000000TESTS3',
  },
  {
    organization: 'IRS-Test',
    edcType: 'Consumer',
    providedBy: 'IRS-Test',
    bpn: 'BPNL000000TESTSS',
    url: 'test.de/BPNL000000TESTSS',
  },
  {
    organization: 'Tier C',
    edcType: 'Provider',
    providedBy: 'GEC',
    bpn: 'BPNL000000TESTGV',
    url: 'test.de/BPNL000000TESTGV',
  },
  {
    organization: 'Trace-X EDC',
    edcType: 'Consumer',
    providedBy: 'Trace-X',
    bpn: 'BPNL000000TESTL1',
    url: 'test.de/BPNL000000TESTL1',
  },
  {
    organization: 'Trace-X EDC 2',
    edcType: 'Provider',
    providedBy: 'Trace-X',
    bpn: 'BPNL000000TESTKC',
    url: 'test.de/BPNL000000TESTKC',
  },
];

export const getImportReport = (): any => {
  return {
    'importStateMessage': [
      {
        'catenaXId': 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:aad27ddb-43aa-4e42-98c2-01e529ef127c',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:e5c96ab5-896a-482c-8761-efd74777ca98',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:4f7b1cf2-a598-4027-bc78-63f6d8e55699',
        'persistedOrUpdated': true,
      },
      {
        'catenaXId': 'urn:uuid:bee5614f-9e46-4c98-9209-61a6f2b2a7fc',
        'persistedOrUpdated': true,
      },
      {
        'catenaXId': 'urn:uuid:4518c080-14fb-4252-b8de-4362d615868d',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:3cdd2826-5df0-4c7b-b540-9eeccecb2301',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:68904173-ad59-4a77-8412-3e73fcafbd8b',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:6b2296cc-26c0-4f38-8a22-092338c36e22',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:d8030bbf-a874-49fb-b2e1-7610f0ccad12',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:4e390dab-707f-446e-bfbe-653f6f5b1f37',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:7c7d5aec-b15d-491c-8fbd-c61c6c02c69a',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:4a5e9ff6-2d5c-4510-a90e-d55af3ba502f',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:6ec3f1db-2798-454b-a73f-0d21a8966c74',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa01',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa03',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:0733946c-59c6-41ae-9570-cb43a6e43842',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:44347dec-21d1-47aa-b2a7-f959bf9d424b',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:1233b405-5ac8-4867-93f8-6fdf37733737',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:bcfae197-40fa-4be0-821d-5c1873a1b7c2',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:254604ab-2153-45fb-8cad-54ef09f4070e',
        'persistedOrUpdated': false,
      },
      {
        'catenaXId': 'urn:uuid:e3b2f5e2-5be5-4ea6-98f0-6876de0fca4f',
        'persistedOrUpdated': false,
      },
    ],
    'validationResult': {
      'validationErrors': [],
    },
  };
};

export const getContracts = (): PaginationResponse<ContractResponse> => {
  return {
    page: 0,
    pageSize: 10,
    totalItems: 12,
    pageCount: 2,
    content: [
      {
        "contractId": "abc1",
        'contractType': ContractType.ASSET_AS_BUILT,
        "counterpartyAddress": "https://trace-x-edc-e2e-a.dev.demo.catena-x.net/api/v1/dsp",
        "creationDate": "2024-02-26T13:38:07+01:00",
        "endDate": null,
        "state": ContractState.FINALIZED,
        'policy': 'jsontextaspolicy',
        'globalAssetId': 'uuid-1',
      },
      {
        "contractId": "abc2",
        'contractType': ContractType.ASSET_AS_BUILT,
        "counterpartyAddress": "https://trace-x-edc-e2e-a.dev.demo.catena-x.net/api/v1/dsp",
        "creationDate": "2022-05-01T12:34:12",
        "endDate": null,
        "state": ContractState.FINALIZED,
        'policy': 'jsontextaspolicy',
        'globalAssetId': 'uuid-2',
      },
      {
        "contractId": "abc3",
        'contractType': ContractType.ASSET_AS_BUILT,
        "counterpartyAddress": "https://trace-x-edc-e2e-a.dev.demo.catena-x.net/api/v1/dsp",
        "creationDate": "2022-05-01T12:34:12",
        "endDate": null,
        "state": ContractState.FINALIZED,
        'policy': 'jsontextaspolicy',
        'globalAssetId': 'uuid-3',
      },
      {
        "contractId": "abc4",
        'contractType': ContractType.ASSET_AS_BUILT,
        "counterpartyAddress": "https://trace-x-edc-e2e-a.dev.demo.catena-x.net/api/v1/dsp",
        "creationDate": "2022-05-01T12:34:12",
        "endDate": null,
        "state": ContractState.FINALIZED,
        'policy': 'jsontextaspolicy',
        'globalAssetId': 'uuid-4',
      },
      {
        "contractId": "abc5",
        'contractType': ContractType.ASSET_AS_PLANNED,
        "counterpartyAddress": "https://trace-x-edc-e2e-a.dev.demo.catena-x.net/api/v1/dsp",
        "creationDate": "2022-05-01T12:34:12",
        "endDate": null,
        "state": ContractState.FINALIZED,
        'policy': 'jsontextaspolicy',
        'globalAssetId': 'uuid-5',
      },
      {
        "contractId": "abc6",
        'contractType': ContractType.ASSET_AS_PLANNED,
        "counterpartyAddress": "https://trace-x-edc-e2e-a.dev.demo.catena-x.net/api/v1/dsp",
        "creationDate": "2022-05-01T12:34:12",
        "endDate": null,
        "state": ContractState.FINALIZED,
        'policy': 'jsontextaspolicy',
        'globalAssetId': 'uuid-6',
      },
      {
        "contractId": "abc7",
        'contractType': ContractType.ASSET_AS_PLANNED,
        "counterpartyAddress": "https://trace-x-edc-e2e-a.dev.demo.catena-x.net/api/v1/dsp",
        "creationDate": "2022-05-01T12:34:12",
        "endDate": null,
        "state": ContractState.FINALIZED,
        'policy': 'jsontextaspolicy',
        'globalAssetId': 'uuid-7',
      },
      {
        "contractId": "abc8",
        'contractType': ContractType.ASSET_AS_PLANNED,
        "counterpartyAddress": "https://trace-x-edc-e2e-a.dev.demo.catena-x.net/api/v1/dsp",
        "creationDate": "2022-05-01T12:34:12",
        "endDate": null,
        "state": ContractState.FINALIZED,
        'policy': 'jsontextaspolicy',
        'globalAssetId': 'uuid-8',
      },
      {
        "contractId": "abc9",
        'contractType': ContractType.NOTIFICATION,
        "counterpartyAddress": "https://trace-x-edc-e2e-a.dev.demo.catena-x.net/api/v1/dsp",
        "creationDate": "2022-05-01T12:34:12",
        "endDate": null,
        "state": ContractState.FINALIZED,
        "policy": "jsontextaspolicy"
      },
      {
        "contractId": "abc10",
        'contractType': ContractType.NOTIFICATION,
        "counterpartyAddress": "https://trace-x-edc-e2e-a.dev.demo.catena-x.net/api/v1/dsp",
        "creationDate": "2022-05-01T12:34:12",
        "endDate": null,
        "state": ContractState.FINALIZED,
        "policy": "jsontextaspolicy"
      },
      {
        "contractId": "abc11",
        'contractType': ContractType.NOTIFICATION,
        "counterpartyAddress": "https://trace-x-edc-e2e-a.dev.demo.catena-x.net/api/v1/dsp",
        "creationDate": "2022-05-01T12:34:12",
        "endDate": null,
        "state": ContractState.FINALIZED,
        "policy": "jsontextaspolicy"
      },
      {
        "contractId": "abc12",
        'contractType': ContractType.NOTIFICATION,
        "counterpartyAddress": "https://trace-x-edc-e2e-a.dev.demo.catena-x.net/api/v1/dsp",
        "creationDate": "2022-05-01T12:34:12",
        "endDate": null,
        "state": ContractState.FINALIZED,
        "policy": "jsontextaspolicy"
      },
      {
        "contractId": "abc13",
        'contractType': ContractType.NOTIFICATION,
        "counterpartyAddress": "https://trace-x-edc-e2e-a.dev.demo.catena-x.net/api/v1/dsp",
        "creationDate": "2022-05-01T12:34:12",
        "endDate": null,
        "state": ContractState.FINALIZED,
        "policy": "jsontextaspolicy"
      }
    ],
  };
};
