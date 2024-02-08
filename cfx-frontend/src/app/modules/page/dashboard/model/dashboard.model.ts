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


export interface DashboardStats {
  totalOtherParts: number,
  totalOwnParts: number,
  investigationsReceived: number,
  alertsReceived: number,
}

export interface DashboardStatsResponse {
  asBuiltCustomerParts: number;
  asPlannedCustomerParts: number;
  asBuiltSupplierParts: number;
  asPlannedSupplierParts: number;
  asBuiltOwnParts: number;
  asPlannedOwnParts: number;
  myPartsWithOpenAlerts: number;
  myPartsWithOpenInvestigations: number;
  supplierPartsWithOpenAlerts: number;
  customerPartsWithOpenAlerts: number;
  supplierPartsWithOpenInvestigations: number;
  customerPartsWithOpenInvestigations: number;
  receivedActiveAlerts: number;
  receivedActiveInvestigations: number;
  sentActiveAlerts: number;
  sentActiveInvestigations: number;
}
