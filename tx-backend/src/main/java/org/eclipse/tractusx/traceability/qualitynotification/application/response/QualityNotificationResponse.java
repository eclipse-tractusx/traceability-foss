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
package org.eclipse.tractusx.traceability.qualitynotification.application.response;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
public class QualityNotificationResponse {
    @ApiModelProperty(example = "66")
    @Size(max = 255)
    private Long id;

    @ApiModelProperty(example = "CREATED")
    @Size(max = 255)
    private QualityNotificationStatusResponse status;

    @ApiModelProperty(example = "DescriptionText")
    @Size(max = 1000)
    private String description;

    @ApiModelProperty(example = "BPNL00000003AYRE")
    @Size(max = 255)
    private String createdBy;

    @ApiModelProperty(example = "Tier C")
    @Size(max = 255)
    private String createdByName;

    @ApiModelProperty(example = "2023-02-21T21:27:10.734950Z")
    @Size(max = 50)
    private String createdDate;

    @ApiModelProperty(example = "[\"urn:uuid:ceb6b964-5779-49c1-b5e9-0ee70528fcbd\"]")
    @ArraySchema(arraySchema = @Schema(description = "assetIds", additionalProperties = Schema.AdditionalPropertiesValue.FALSE), maxItems = Integer.MAX_VALUE)
    @Size(max = 1000)
    private List<String> assetIds;

    @ApiModelProperty(example = "SENDER")
    @Size(max = 255)
    private QualityNotificationSideResponse channel;

    private QualityNotificationReasonResponse reason;

    @ApiModelProperty(example = "BPNL00000003AYRE")
    @Size(max = 255)
    private String sendTo;

    @ApiModelProperty(example = "Tier C")
    @Size(max = 255)
    private String sendToName;

    @ApiModelProperty(example = "MINOR")
    @Size(max = 255)
    private QualityNotificationSeverityResponse severity;

    @ApiModelProperty(example = "2099-02-21T21:27:10.734950Z")
    @Size(max = 50)
    private String targetDate;
}
