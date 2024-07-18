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
package org.eclipse.tractusx.traceability.assets.application.asplanned.mapper;

import assets.response.asplanned.AssetAsPlannedResponse;
import org.eclipse.tractusx.traceability.assets.application.base.mapper.AssetBaseResponseMapper;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.common.model.PageResult;

import java.util.List;

public class AssetAsPlannedResponseMapper extends AssetBaseResponseMapper {

    public static AssetAsPlannedResponse from(final AssetBase asset) {
        return AssetAsPlannedResponse.builder()
                .id(asset.getId())
                .idShort(asset.getIdShort())
                .classification(asset.getClassification())
                .semanticModelId(asset.getSemanticModelId())
                .businessPartner(asset.getManufacturerId())
                .manufacturerName(asset.getManufacturerName())
                .nameAtManufacturer(asset.getNameAtManufacturer())
                .manufacturerPartId(asset.getManufacturerPartId())
                .owner(from(asset.getOwner()))
                .businessPartner(asset.getManufacturerId())
                .childRelations(
                        asset.getChildRelations().stream()
                                .map(AssetAsPlannedResponseMapper::from)
                                .toList())
                .parentRelations(
                        asset.getParentRelations().stream()
                                .map(AssetAsPlannedResponseMapper::from)
                                .toList())
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
                .importState(toImportStateResponse(asset.getImportState()))
                .importNote(asset.getImportNote())
                .tombstone(asset.getTombstone())
                .build();
    }


    public static PageResult<AssetAsPlannedResponse> from(final PageResult<AssetBase> assetPageResult) {
        return new PageResult<>(
                assetPageResult.content().stream()
                        .map(AssetAsPlannedResponseMapper::from).toList(),
                assetPageResult.page(),
                assetPageResult.pageCount(),
                assetPageResult.pageSize(),
                assetPageResult.totalItems()
        );
    }

    public static List<AssetAsPlannedResponse> from(final List<AssetBase> assets) {
        return assets.stream()
                .map(AssetAsPlannedResponseMapper::from)
                .toList();
    }

}
