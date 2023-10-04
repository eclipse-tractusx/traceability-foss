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
package org.eclipse.tractusx.traceability.assets.domain.base.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.application.base.service.AssetBaseService;
import org.eclipse.tractusx.traceability.assets.domain.base.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.IrsRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.base.model.QualityType;
import org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.ManufacturingInfo;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect;
import org.eclipse.tractusx.traceability.common.config.AssetsAsyncConfig;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractAssetBaseService implements AssetBaseService {

    private static List<String> SUPPORTED_ENUM_FIELDS = List.of("owner", "qualityType", "semanticDataModel");

    protected abstract AssetRepository getAssetRepository();

    protected abstract IrsRepository getIrsRepository();

    protected abstract List<String> getDownwardAspects();

    protected abstract List<String> getUpwardAspects();

    protected abstract BomLifecycle getBomLifecycle();

    @Override
    @Async(value = AssetsAsyncConfig.SYNCHRONIZE_ASSETS_EXECUTOR)
    public void synchronizeAssetsAsync(String globalAssetId) {
        log.info("Synchronizing assets for globalAssetId: {}", globalAssetId);
        try {
            if (!getDownwardAspects().isEmpty()) {
                List<AssetBase> downwardAssets = getIrsRepository().findAssets(globalAssetId, Direction.DOWNWARD, getDownwardAspects(), getBomLifecycle());
                getAssetRepository().saveAll(downwardAssets);
            }

            if (!getUpwardAspects().isEmpty()) {

                // TODO: change BomLifecycle.AS_BUILT to getBomLifecycle()
                List<AssetBase> upwardAssets = getIrsRepository().findAssets(globalAssetId, Direction.UPWARD, Aspect.upwardAspectsForAssetsAsBuilt(), BomLifecycle.AS_BUILT);

                upwardAssets.forEach(asset -> {
                    if (getAssetRepository().existsById(asset.getId())) {
                        getAssetRepository().updateParentDescriptionsAndOwner(asset);
                    } else {
                        getAssetRepository().save(asset);
                    }
                });

            }

        } catch (Exception e) {
            log.warn("Exception during assets synchronization for globalAssetId: {}. Message: {}.", globalAssetId, e.getMessage(), e);
        }
    }

    @Override
    @Async(value = AssetsAsyncConfig.SYNCHRONIZE_ASSETS_EXECUTOR)
    public void synchronizeAssetsAsync(List<String> globalAssetIds) {
        for (String globalAssetId : globalAssetIds) {
            try {
                synchronizeAssetsAsync(globalAssetId);
            } catch (Exception e) {
                log.warn("Cannot fetch assets for id: {}. Error: {}", globalAssetId, e.getMessage());
            }
        }
    }

    @Override
    public void setAssetsInvestigationStatus(QualityNotification investigation) {
        getAssetRepository().getAssetsById(investigation.getAssetIds()).forEach(asset -> {
            // Assets in status closed will be false, others true
            asset.setUnderInvestigation(!investigation.getNotificationStatus().equals(QualityNotificationStatus.CLOSED));
            getAssetRepository().save(asset);
        });
    }

    @Override
    public void setAssetsAlertStatus(QualityNotification alert) {
        getAssetRepository().getAssetsById(alert.getAssetIds()).forEach(asset -> {
            // Assets in status closed will be false, others true
            asset.setActiveAlert(!alert.getNotificationStatus().equals(QualityNotificationStatus.CLOSED));
            getAssetRepository().save(asset);
        });
    }

    @Override
    public AssetBase updateQualityType(String assetId, QualityType qualityType) {
        AssetBase foundAsset = getAssetRepository().getAssetById(assetId);
        foundAsset.setQualityType(qualityType);
        return getAssetRepository().save(foundAsset);
    }

    @Override
    public PageResult<AssetBase> getAssets(Pageable pageable, SearchCriteria searchCriteria) {
        return getAssetRepository().getAssets(pageable, searchCriteria);
    }

    @Override
    public AssetBase getAssetById(String assetId) {
        return getAssetRepository().getAssetById(assetId);
    }

    @Override
    public List<AssetBase> getAssetsById(List<String> assetIds) {
        return getAssetRepository().getAssetsById(assetIds);
    }

    @Override
    public AssetBase getAssetByChildId(String assetId, String childId) {
        return getAssetRepository().getAssetByChildId(assetId, childId);
    }

    @Override
    public Map<String, Long> getAssetsCountryMap() {
        return getAssetRepository().getAssets().stream()
                .collect(Collectors.groupingBy(
                        asset -> ManufacturingInfo.from(asset.getDetailAspectModels()).getManufacturingCountry(), Collectors.counting()));
    }

    @Override
    public List<String> getDistinctFilterValues(String fieldName, Long size) {
        if (isSupportedEnumType(fieldName)) {
            return getAssetEnumFieldValues(fieldName);
        }
        return getAssetRepository().getFieldValues(fieldName, size);
    }

    private boolean isSupportedEnumType(String fieldName) {
        return SUPPORTED_ENUM_FIELDS.contains(fieldName);
    }

    private List<String> getAssetEnumFieldValues(String fieldName) {
        return switch (fieldName) {
            case "owner" -> Arrays.stream(Owner.values()).map(Enum::name).toList();
            case "qualityType" -> Arrays.stream(QualityType.values()).map(Enum::name).toList();
            case "semanticDataModel" -> Arrays.stream(SemanticDataModel.values()).map(Enum::name).toList();
            default -> null;
        };
    }
}
