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

import { EssResponse, EsssResponse } from '@page/ess/model/ess.model';

export const mockEsss = [
  {
    rowNumber: '1',
    manufacturerPartId: 'ZX-55',
    nameAtManufacturer: 'Vehicle Model A',
    bpns: 'BPNS00ARBITRARY3',
    companyName: 'SensorGmbH',
    status: 'initial',
    impacted: '--',
    created: '2023-11-23 12:29:23.65436',
    updated: '--'
  }
] as EssResponse[];

const MockEmptyEss: EssResponse = {
  id: 'urn:uuid:a000a0aa-00a0-0000-000a-0a0000a0a000',
  essStatus: '',
  message: '',
  rowNumber: '',
  manufacturerPartId: '',
  nameAtManufacturer: '',
  catenaxSiteId: '',
  bpns: '',
  companyName: '',
  jobId: '',
  status: '',
  impacted: '',
  response: '',
  created: '',
  updated: ''
};

export const getEssById = (id: string) => {
  return [...mockEsss].find(ess => ess.id === id) || { ...MockEmptyEss, id };
};

export const getRandomAsset = () => {
  const esss = [...mockEsss];
  return esss[Math.floor(Math.random() * esss.length)];
};

export const MOCK_ess_1 = {
  id: 'MOCK_ess_1',
  essStatus: '',
  message: '',
  rowNumber: '',
  manufacturerPartId: '',
  nameAtManufacturer: '',
  catenaxSiteId: '',
  bpns: '',
  companyName: '',
  jobId: '',
  status: '',
  impacted: '',
  response: '',
  created: '',
  updated: ''
}

export const mockEsssResponse: EsssResponse = {
  content: [MOCK_ess_1],
  page: 0,
  pageCount: 1,
  pageSize: 10,
  totalItems: 5,
};
