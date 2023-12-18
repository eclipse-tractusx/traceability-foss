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
package org.eclipse.tractusx.traceability.assets.application.asbuilt.mapper;

import assets.response.asbuilt.AssetAsBuiltResponse;
import org.eclipse.tractusx.traceability.assets.application.base.mapper.AssetBaseResponseMapper;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.common.model.PageResult;

import java.util.List;

public class AssetAsBuiltResponseMapper extends AssetBaseResponseMapper {

    public static AssetAsBuiltResponse from(final AssetBase asset) {
        return AssetAsBuiltResponse.builder()
                .id(asset.getId())
                .idShort(asset.getIdShort())
                .classification(asset.getClassification())
                .semanticModelId(asset.getSemanticModelId())
                .businessPartner(asset.getManufacturerId())
                .manufacturerName(asset.getManufacturerName())
                .nameAtManufacturer(asset.getNameAtManufacturer())
                .manufacturerPartId(asset.getManufacturerPartId())
                .owner(from(asset.getOwner()))
                .childRelations(
                        asset.getChildRelations().stream()
                                .map(AssetAsBuiltResponseMapper::from)
                                .toList())
                .parentRelations(
                        asset.getParentRelations().stream()
                                .map(AssetAsBuiltResponseMapper::from)
                                .toList())
                .underInvestigation(asset.isInInvestigation())
                .activeAlert(asset.isActiveAlert())
                .qualityType(
                        from(asset.getQualityType())
                )
                .van(asset.getVan())
                .semanticDataModel(from(asset.getSemanticDataModel()))
                .detailAspectModels(fromList(asset.getDetailAspectModels()))
                .sentQualityAlertIdsInStatusActive(getNotificationIdsInActiveState(asset.getSentQualityAlerts()))
                .receivedQualityAlertIdsInStatusActive(getNotificationIdsInActiveState(asset.getReceivedQualityAlerts()))
                .sentQualityInvestigationIdsInStatusActive(getNotificationIdsInActiveState(asset.getSentQualityInvestigations()))
                .receivedQualityInvestigationIdsInStatusActive(getNotificationIdsInActiveState(asset.getReceivedQualityInvestigations()))
                .build();
    }

    public static PageResult<AssetAsBuiltResponse> from(final PageResult<AssetBase> assetPageResult) {
        return new PageResult<>(
                assetPageResult.content().stream()
                        .map(AssetAsBuiltResponseMapper::from).toList(),
                assetPageResult.page(),
                assetPageResult.pageCount(),
                assetPageResult.pageSize(),
                assetPageResult.totalItems()
        );
    }

    public static List<AssetAsBuiltResponse> from(final List<AssetBase> assets) {
        return assets.stream()
                .map(AssetAsBuiltResponseMapper::from)
                .toList();
    }
}
