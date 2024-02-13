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

import { DashboardAssembler } from './dashboard.assembler';

describe('DashboardAssembler', () => {
  describe('assembleDashboard', () => {
    it('should map response', () => {
      expect(
        DashboardAssembler.assembleDashboard({
          asBuiltOwnParts: 200,
          asBuiltCustomerParts: 0,
          asBuiltSupplierParts: 10,
          asPlannedCustomerParts: 0,
          asPlannedOwnParts: 0,
          asPlannedSupplierParts: 0,
          customerPartsWithOpenAlerts: 0,
          customerPartsWithOpenInvestigations: 0,
          myPartsWithOpenAlerts: 0,
          myPartsWithOpenInvestigations: 0,
          supplierPartsWithOpenAlerts: 0,
          supplierPartsWithOpenInvestigations: 0,
          receivedActiveAlerts: 0,
          receivedActiveInvestigations: 0,
          sentActiveInvestigations: 0,
          sentActiveAlerts: 0,

        }),
      ).toEqual({
        asBuiltCustomerParts: 0,
        asBuiltSupplierParts: 10,
        asPlannedCustomerParts: 0,
        asPlannedOwnParts: 0,
        asPlannedSupplierParts: 0,
        asBuiltOwnParts: 200,

        totalOwnParts: 200,
        totalOtherParts: 10,

        ownOpenInvestigationsReceived: 0,
        ownOpenInvestigationsCreated: 0,
        ownOpenAlertsReceived: 0,
        ownOpenAlertsCreated: 0,

        myPartsWithOpenAlerts: 0,
        myPartsWithOpenInvestigations: 0,
        receivedActiveAlerts: 0,
        receivedActiveInvestigations: 0,
        sentActiveAlerts: 0,
        sentActiveInvestigations: 0,
      });
    });
  });
});
