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

import io.swagger.annotations.ApiModelProperty;

public record DashboardResponse(
        @ApiModelProperty(example = "5")
        Long asBuiltCustomerParts,
        @ApiModelProperty(example = "10")
        Long asPlannedCustomerParts,
        @ApiModelProperty(example = "2")
        Long asBuiltSupplierParts,
        @ApiModelProperty(example = "3")
        Long asPlannedSupplierParts,
        @ApiModelProperty(example = "1")
        Long asBuiltOwnParts,
        @ApiModelProperty(example = "1")
        Long asPlannedOwnParts,
        @ApiModelProperty(example = "1")
        Long myPartsWithOpenAlerts,
        @ApiModelProperty(example = "1")
        Long myPartsWithOpenInvestigations,
        @ApiModelProperty(example = "1")
        Long supplierPartsWithOpenAlerts,
        @ApiModelProperty(example = "1")
        Long customerPartsWithOpenAlerts,
        @ApiModelProperty(example = "2")
        Long supplierPartsWithOpenInvestigations,
        @ApiModelProperty(example = "2")
        Long customerPartsWithOpenInvestigations,

        @ApiModelProperty(example = "2")
        Long receivedActiveAlerts,

        @ApiModelProperty(example = "2")
        Long receivedActiveInvestigations,

        @ApiModelProperty(example = "2")
        Long sentActiveAlerts,

        @ApiModelProperty(example = "2")
        Long sentActiveInvestigations) {


}
