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
package assets.response.base;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@RequiredArgsConstructor
@Data
@SuperBuilder
public class AssetBaseResponse {
    @ApiModelProperty(example = "urn:uuid:ceb6b964-5779-49c1-b5e9-0ee70528fcbd")
    @Size(max = 255)
    private String id;
    @ApiModelProperty(example = "assembly-part-relationship")
    @Size(max = 255)
    private String idShort;
    @ApiModelProperty(example = "NO-246880451848384868750731")
    @Size(max = 255)
    private String semanticModelId;
    @ApiModelProperty(example = "BPNL00000003CSGV")
    @Size(max = 255)
    private String businessPartner;
    @ApiModelProperty(example = "Tier C")
    @Size(max = 255)
    private String manufacturerName;
    @ApiModelProperty(example = "Tier C")
    @Size(max = 255)
    private String nameAtManufacturer;
    @ApiModelProperty(example = "Tier C")
    @Size(max = 255)
    private String manufacturerPartId;
    @ApiModelProperty(example = "CUSTOMER")
    private OwnerResponse owner;
    @ArraySchema(arraySchema = @Schema(description = "Child relationships", additionalProperties = Schema.AdditionalPropertiesValue.FALSE), maxItems = Integer.MAX_VALUE)
    private List<DescriptionsResponse> childRelations;
    @ArraySchema(arraySchema = @Schema(description = "Parent relationships", additionalProperties = Schema.AdditionalPropertiesValue.FALSE), maxItems = Integer.MAX_VALUE)
    private List<DescriptionsResponse> parentRelations;

    @ApiModelProperty(example = "false")
    private boolean activeAlert;
    @ApiModelProperty(example = "false")
    private boolean underInvestigation;
    @ApiModelProperty(example = "Ok")
    private QualityTypeResponse qualityType;
    @ApiModelProperty(example = "OMAYSKEITUGNVHKKX")
    @Size(max = 255)
    private String van;
    @ApiModelProperty(example = "BATCH")
    private SemanticDataModelResponse semanticDataModel;
    @ApiModelProperty(example = "component")
    @Size(max = 255)
    private String classification;
    private List<DetailAspectModelResponse> detailAspectModels;
}
