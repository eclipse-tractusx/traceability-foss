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

import { DashboardStats, DashboardStatsResponse } from '../model/dashboard.model';

export class DashboardAssembler {
  public static assembleDashboard(dashboard: DashboardStatsResponse): DashboardStats {
    return {
      // notification counts (where open means notfication status not closed)
      myPartsWithOpenAlerts: dashboard.myPartsWithOpenAlerts,
      myPartsWithOpenInvestigations: dashboard.myPartsWithOpenInvestigations,

      // part counts
      asBuiltCustomerParts: dashboard.asBuiltCustomerParts,
      asPlannedCustomerParts: dashboard.asPlannedCustomerParts,
      asBuiltSupplierParts: dashboard.asBuiltSupplierParts,
      asPlannedSupplierParts: dashboard.asPlannedSupplierParts,
      asBuiltOwnParts: dashboard.asBuiltOwnParts,
      asPlannedOwnParts: dashboard.asPlannedOwnParts,

      // calculated
      totalOwnParts: dashboard.asBuiltOwnParts + dashboard.asPlannedOwnParts,
      totalOtherParts: dashboard.asBuiltSupplierParts + dashboard.asBuiltCustomerParts + dashboard.asPlannedSupplierParts + dashboard.asPlannedSupplierParts,
      ownOpenInvestigationsReceived: dashboard.myPartsWithOpenInvestigations,
      ownOpenInvestigationsCreated: dashboard.supplierPartsWithOpenInvestigations + dashboard.customerPartsWithOpenInvestigations,
      ownOpenAlertsReceived: dashboard.supplierPartsWithOpenAlerts + dashboard.customerPartsWithOpenAlerts,
      ownOpenAlertsCreated: dashboard.myPartsWithOpenAlerts,
      receivedActiveAlerts: dashboard.receivedActiveAlerts,
      sentActiveAlerts: dashboard.sentActiveAlerts,
      receivedActiveInvestigations: dashboard.receivedActiveInvestigations,
      sentActiveInvestigations: dashboard.sentActiveInvestigations,

    };
  }
}
