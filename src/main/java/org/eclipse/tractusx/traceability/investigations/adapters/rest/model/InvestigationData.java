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

package org.eclipse.tractusx.traceability.investigations.adapters.rest.model;

import io.swagger.annotations.ApiModelProperty;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationSide;

import java.util.List;

public record InvestigationData(@ApiModelProperty(example = "66") Long id,
								@ApiModelProperty(example = "CREATED") String status,
								@ApiModelProperty(example = "DescriptionText") String description,
								@ApiModelProperty(example = "BPNL00000003AYRE") String createdBy,
								@ApiModelProperty(example = "2023-02-21T21:27:10.734950Z") String createdDate,
								@ApiModelProperty(example = "[\"urn:uuid:ceb6b964-5779-49c1-b5e9-0ee70528fcbd\"]") List<String> assetIds,
								@ApiModelProperty(example = "SENDER") InvestigationSide channel,
								InvestigationReason reason,
								@ApiModelProperty(example = "BPNL00000003AYRE") String sendTo
) {
}
