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
package org.eclipse.tractusx.traceability.qualitynotification.application;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.eclipse.tractusx.traceability.qualitynotification.application.investigation.response.InvestigationReason;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.InvestigationSide;

import java.util.List;

@Data
@SuperBuilder
public class QualityNotificationDTO {
    @ApiModelProperty(example = "66")
    private Long id;

    @ApiModelProperty(example = "CREATED")
    private String status;

    @ApiModelProperty(example = "DescriptionText")
    private String description;

    @ApiModelProperty(example = "BPNL00000003AYRE")
    private String createdBy;

    @ApiModelProperty(example = "Tier C")
    private String createdByName;

    @ApiModelProperty(example = "2023-02-21T21:27:10.734950Z")
    private String createdDate;

    @ApiModelProperty(example = "[\"urn:uuid:ceb6b964-5779-49c1-b5e9-0ee70528fcbd\"]")
    @ArraySchema(arraySchema = @Schema(description = "assetIds"), maxItems = Integer.MAX_VALUE)
    @Size(max = 1000)
    private List<String> assetIds;

    @ApiModelProperty(example = "SENDER")
    private InvestigationSide channel;

    private InvestigationReason reason;

    @ApiModelProperty(example = "BPNL00000003AYRE")
    private String sendTo;

    @ApiModelProperty(example = "Tier C")
    private String sendToName;

    @ApiModelProperty(example = "MINOR")
    private String severity;

    @ApiModelProperty(example = "2099-02-21T21:27:10.734950Z")
    private String targetDate;
}
