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
package org.eclipse.tractusx.traceability.assets.application.rest.mapper;

import assets.response.*;
import org.eclipse.tractusx.traceability.assets.domain.model.*;
import org.eclipse.tractusx.traceability.common.model.PageResult;

import java.util.List;

public class AssetResponseMapper {


    public static AssetResponse from(final Asset asset) {
        return AssetResponse.builder()
                .id(asset.getId())
                .idShort(asset.getIdShort())
                .classification(asset.getClassification())
                .semanticModelId(asset.getSemanticModelId())
                .manufacturerId(asset.getManufacturerId())
                .manufacturerName(asset.getManufacturerName())
                .semanticModel(from(asset.getSemanticModel()))
                .owner(from(asset.getOwner()))
                .childRelations(
                        asset.getChildRelations().stream()
                                .map(AssetResponseMapper::from)
                                .toList())
                .parentRelations(
                        asset.getParentRelations().stream()
                                .map(AssetResponseMapper::from)
                                .toList())
                .underInvestigation(asset.isUnderInvestigation())
                .activeAlert(asset.isActiveAlert())
                .qualityType(
                        from(asset.getQualityType())
                )
                .van(asset.getVan())
                .semanticDataModel(from(asset.getSemanticDataModel()))
                .build();
    }

    public static PageResult<AssetResponse> from(final PageResult<Asset> assetPageResult) {
        return new PageResult<>(
                assetPageResult.content().stream()
                        .map(AssetResponseMapper::from).toList(),
                assetPageResult.page(),
                assetPageResult.pageCount(),
                assetPageResult.pageSize(),
                assetPageResult.totalItems()
        );
    }

    public static List<AssetResponse> from(final List<Asset> assets) {
        return assets.stream()
                .map(AssetResponseMapper::from)
                .toList();
    }

    public static SemanticModelResponse from(final SemanticModel semanticModel) {
        return SemanticModelResponse.builder()
                .customerPartId(semanticModel.getCustomerPartId())
                .manufacturerPartId(semanticModel.getManufacturerPartId())
                .manufacturingCountry(semanticModel.getManufacturingCountry())
                .manufacturingDate(semanticModel.getManufacturingDate())
                .nameAtCustomer(semanticModel.getNameAtCustomer())
                .nameAtManufacturer(semanticModel.getNameAtManufacturer())
                .build();
    }


    public static OwnerResponse from(final Owner owner) {
        return OwnerResponse.valueOf(owner.name());
    }

    public static DescriptionsResponse from(final Descriptions descriptions) {
        return new DescriptionsResponse(
                descriptions.id(),
                descriptions.idShort()
        );
    }

    public static QualityTypeResponse from(final QualityType qualityType) {
        return QualityTypeResponse.valueOf(qualityType.name());
    }


    public static SemanticDataModelResponse from(final SemanticDataModel semanticDataModel) {
        if (semanticDataModel == null) {
            return SemanticDataModelResponse.UNKNOWN;
        }
        return SemanticDataModelResponse.valueOf(semanticDataModel.name());
    }
}
