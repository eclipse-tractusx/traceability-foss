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

package org.eclipse.tractusx.traceability.assets.application.rest.response;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.common.model.PageResult;

import java.util.List;

@AllArgsConstructor
@Builder
@Data
@ArraySchema(arraySchema = @Schema(description = "Assets", additionalProperties = Schema.AdditionalPropertiesValue.FALSE), maxItems = Integer.MAX_VALUE)
public class AssetResponse {
    @ApiModelProperty(example = "urn:uuid:ceb6b964-5779-49c1-b5e9-0ee70528fcbd")
    private String id;
    @ApiModelProperty(example = "--")
    @Size(max = 255)
    private String idShort;
    @ApiModelProperty(example = "--")
    private String semanticModelId;
    @ApiModelProperty(example = "BPNL00000003CSGV")
    private String manufacturerId;
    @ApiModelProperty(example = "Tier C")
    private String manufacturerName;
    private SemanticModelResponse semanticModel;
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
    @ApiModelProperty(example = "--")
    private String van;
    @ApiModelProperty(example = "BATCH")
    private SemanticDataModelResponse semanticDataModel;
    @ApiModelProperty(example = "component")
    private String classification;

    public static AssetResponse from(final Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .idShort(asset.getIdShort())
                .classification(asset.getClassification())
                .semanticModelId(asset.getSemanticModelId())
                .manufacturerId(asset.getManufacturerId())
                .manufacturerName(asset.getManufacturerName())
                .semanticModel(SemanticModelResponse.from(asset.getSemanticModel()))
                .owner(OwnerResponse.from(asset.getOwner()))
                .childRelations(
                        asset.getChildRelations().stream()
                                .map(DescriptionsResponse::from)
                                .toList())
                .parentRelations(
                        asset.getParentRelations().stream()
                                .map(DescriptionsResponse::from)
                                .toList())
                .underInvestigation(asset.isUnderInvestigation())
                .activeAlert(asset.isActiveAlert())
                .qualityType(
                        QualityTypeResponse.from(asset.getQualityType())
                )
                .van(asset.getVan())
                .semanticDataModel(SemanticDataModelResponse.from(asset.getSemanticDataModel()))
                .build();
    }

    public static PageResult<AssetResponse> from(final PageResult<Asset> assetPageResult) {
        return new PageResult<>(
                assetPageResult.content().stream()
                        .map(AssetResponse::from).toList(),
                assetPageResult.page(),
                assetPageResult.pageCount(),
                assetPageResult.pageSize(),
                assetPageResult.totalItems()
        );
    }

    public static List<AssetResponse> from(final List<Asset> assets) {
        return assets.stream()
                .map(AssetResponse::from)
                .toList();
    }
}
