/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

export const mockLegalEntities = [
    {
    "score": 1,
    "legalName": "CathodeGmbH",
    "bpnl": "BPNL0123456789",
    "legalShortName": "BMW",
    "currentness": "2023-11-15T11:06:02.736586Z",
    "createdAt": "2023-11-15T11:06:02.736586Z",
    "updatedAt": "2023-11-15T11:06:02.736586Z",
    "legalAddress": {
      "bpna": "BPNA0123456789A1",
      "name": null,
      "alternativePostalAddress": null,
      "bpnLegalEntity": null,
      "isLegalAddress": false,
      "bpnSite": "BPNS00000003B6LU",
      "isMainAddress": false,
      "createdAt": "2023-11-15T11:05:59.321523Z",
      "updatedAt": "2023-11-15T11:05:59.321523Z"
    }
  },
    {
      "score": 1,
      "legalName": "SensorGmbH",
      "bpnl": "BPNL00ARBITRARY3",
      "legalShortName": "Sensor",
      "currentness": "2023-11-15T11:06:02.736586Z",
      "createdAt": "2023-11-15T11:06:02.736586Z",
      "updatedAt": "2023-11-15T11:06:02.736586Z",
      "legalAddress": {
        "bpna": "BPNA00ARBITRARY3",
        "name": null,
        "bpnLegalEntity": null,
        "isLegalAddress": false,
        "bpnSite": "BPNS00ARBITRARY3",
        "isMainAddress": false,
        "createdAt": "2023-11-15T11:05:59.321523Z",
        "updatedAt": "2023-11-15T11:05:59.321523Z"
      }
    },
    {
      "score": 1,
      "legalName": "ChipGmbH",
      "bpnl": "BPNL0123456789",
      "legalShortName": "BM",
      "currentness": "2023-11-15T11:06:02.736586Z",
      "createdAt": "2023-11-15T11:06:02.736586Z",
      "updatedAt": "2023-11-15T11:06:02.736586Z",
      "legalAddress": {
        "bpna": "BPNA0123456789A1",
        "name": null,
        "bpnLegalEntity": null,
        "isLegalAddress": false,
        "bpnSite": "BPNS000000000001",
        "isMainAddress": false,
        "createdAt": "2023-11-15T11:05:59.321523Z",
        "updatedAt": "2023-11-15T11:05:59.321523Z"
      }
    }
];

export const getLegalEntitiesByName = (legalName: string) => {
  const res = mockLegalEntities.filter(
    le => le.legalName.toUpperCase().indexOf(legalName.toUpperCase()) > -1);
  return res;
};
