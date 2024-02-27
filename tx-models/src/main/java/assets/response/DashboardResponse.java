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

package assets.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record DashboardResponse(
        @Schema(example = "5")
        Long asBuiltCustomerParts,
        @Schema(example = "10")
        Long asPlannedCustomerParts,
        @Schema(example = "2")
        Long asBuiltSupplierParts,
        @Schema(example = "3")
        Long asPlannedSupplierParts,
        @Schema(example = "1")
        Long asBuiltOwnParts,
        @Schema(example = "1")
        Long asPlannedOwnParts,
        @Schema(example = "1")
        Long myPartsWithOpenAlerts,
        @Schema(example = "1")
        Long myPartsWithOpenInvestigations,
        @Schema(example = "1")
        Long supplierPartsWithOpenAlerts,
        @Schema(example = "1")
        Long customerPartsWithOpenAlerts,
        @Schema(example = "2")
        Long supplierPartsWithOpenInvestigations,
        @Schema(example = "2")
        Long customerPartsWithOpenInvestigations,

        @Schema(example = "2")
        Long receivedActiveAlerts,

        @Schema(example = "2")
        Long receivedActiveInvestigations,

        @Schema(example = "2")
        Long sentActiveAlerts,

        @Schema(example = "2")
        Long sentActiveInvestigations) {


}
