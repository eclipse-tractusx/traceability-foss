/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

package ess.response;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@ArraySchema(arraySchema =
    @Schema(description = "Ess", additionalProperties =
        Schema.AdditionalPropertiesValue.FALSE), maxItems = Integer.MAX_VALUE)
@Data
@RequiredArgsConstructor
@SuperBuilder
public class VEssResponse {

    @ApiModelProperty(example = "ON")
    @Size(max = 255)
    private EssStatusType essStatus;

    @ApiModelProperty(example = "Status: created")
    @Size(max = 255)
    private String message;

    @ApiModelProperty(example = "1")
    @Size(max = 255)
    private String rowNumber;

    @ApiModelProperty(example = "urn:uuid:ceb6b964-5779-49c1-b5e9-0ee70528fcbd")
    @Size(max = 255)
    private String id;

    @ApiModelProperty(example = "urn:uuid:ceb6b964-5779-49c1-b5e9-0ee70528fcbd")
    @Size(max = 255)
    private String jobId;

    @ApiModelProperty(example = "urn:uuid:ceb6b964-5779-49c1-b5e9-0ee70528fcbd")
    @Size(max = 255)
    private String manufacturerPartId;

    @ApiModelProperty(example = "Iron Looser Co.")
    @Size(max = 255)
    private String nameAtManufacturer;

    @ApiModelProperty(example = "BMW")
    @Size(max = 255)
    private String companyName;

    @ApiModelProperty(example = "urn:uuid:ceb6b964-5779-49c1-b5e9-0ee70528fcbd")
    @Size(max = 255)
    private String catenaxSiteId;

    @ApiModelProperty(example = "BPNS00000003B6LU")
    @Size(max = 255)
    private String bpns;

    @ApiModelProperty(example = "STATUS")
    @Size(max = 255)
    private String status;

    @ApiModelProperty(example = "IMPACTED")
    @Size(max = 255)
    private String impacted;

    @ApiModelProperty(example = "RESPONSE")
    @Size(max = 255)
    private String response;

    @ApiModelProperty(example = "11.12.2023")
    @Size(max = 255)
    private String created;

    @ApiModelProperty(example = "31.12.2023")
    @Size(max = 255)
    private String updated;

}
