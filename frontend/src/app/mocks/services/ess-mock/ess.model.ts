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

import { EssResponse } from '@page/ess/model/ess.model';

export const mockEssList = [
  {
    rowNumber: '1',
    manufacturerPartId: 'ZX-55',
    nameAtManufacturer: 'Vehicle Model A',
    bpns: 'BPNS00ARBITRARY3',
    companyName: 'SensorGmbH',
    status: 'COMPLETED',
    impacted: 'UNKNOWN',
    created: '12/12/23, 09:11 AM',
    updated: '12/12/23, 09:13 AM'
  },
  {
    rowNumber: '2',
    manufacturerPartId: 'ZX-55',
    nameAtManufacturer: 'Vehicle Model A',
    bpns: 'BPNS00000003B6LU',
    companyName: 'CathodeGmbh',
    status: 'COMPLETED',
    impacted: 'AFFECTED',
    created: '12/12/23, 09:14 AM',
    updated: '12/12/23, 09:14 AM'
  },
  {
    rowNumber: '3',
    manufacturerPartId: 'ZX-55',
    nameAtManufacturer: 'Vehicle Model A',
    bpns: 'BPNS000000000001',
    companyName: 'ChipGmbH',
    status: 'COMPLETED',
    impacted: 'NOT AFFECTED',
    created: '12/12/23, 09:15 AM',
    updated: '12/12/23, 09:16 AM'
  }
] as EssResponse[];

export const getRandomAsset = () => {
  const essList = [...mockEssList];
  return essList[Math.floor(Math.random() * essList.length)];
};
