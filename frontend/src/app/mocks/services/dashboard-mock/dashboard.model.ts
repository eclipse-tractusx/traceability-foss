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

import { DashboardStatsResponse } from '@page/dashboard/model/dashboard.model';

export const mockDashboardStats: DashboardStatsResponse = {
  // notification counts (where open means notficaiton status not closed)
  myPartsWithOpenAlerts: 82,
  myPartsWithOpenInvestigations: 33543,
  supplierPartsWithOpenAlerts: 3,
  customerPartsWithOpenAlerts: 563,
  supplierPartsWithOpenInvestigations: 4643,
  customerPartsWithOpenInvestigations: 12,

  // part counts
  asBuiltCustomerParts: 100,
  asPlannedCustomerParts: 50,
  asBuiltSupplierParts: 163000,
  asPlannedSupplierParts: 2563,
  asBuiltOwnParts: 5300000,
  asPlannedOwnParts: 11203,
  receivedActiveAlerts: 5000,
  receivedActiveInvestigations: 2000,
  sentActiveAlerts: 7000,
  sentActiveInvestigations: 5,
};
