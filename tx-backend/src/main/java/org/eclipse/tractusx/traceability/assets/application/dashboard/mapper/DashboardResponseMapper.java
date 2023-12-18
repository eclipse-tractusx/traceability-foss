/********************************************************************************
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
package org.eclipse.tractusx.traceability.assets.application.dashboard.mapper;

import assets.response.DashboardResponse;
import org.eclipse.tractusx.traceability.assets.domain.dashboard.model.Dashboard;

public class DashboardResponseMapper {

    public static DashboardResponse from(final Dashboard dashboard) {
        return new DashboardResponse(
                dashboard.getAsBuiltCustomerParts(),
                dashboard.getAsPlannedCustomerParts(),
                dashboard.getAsBuiltSupplierParts(),
                dashboard.getAsPlannedSupplierParts(),
                dashboard.getAsBuiltOwnParts(),
                dashboard.getAsPlannedOwnParts(),
                dashboard.getMyPartsWithOpenAlerts(),
                dashboard.getMyPartsWithOpenInvestigations(),
                dashboard.getSupplierPartsWithOpenAlerts(),
                dashboard.getCustomerPartsWithOpenAlerts(),
                dashboard.getSupplierPartsWithOpenInvestigations(),
                dashboard.getCustomerPartsWithOpenInvestigations(),
                dashboard.getReceivedActiveAlerts(),
                dashboard.getReceivedActiveInvestigations(),
                dashboard.getSentActiveAlerts(),
                dashboard.getSentActiveInvestigations()
        );
    }
}
