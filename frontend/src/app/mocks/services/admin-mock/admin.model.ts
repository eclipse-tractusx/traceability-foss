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

import { BpnConfigResponse} from '@page/admin/core/admin.model';


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
