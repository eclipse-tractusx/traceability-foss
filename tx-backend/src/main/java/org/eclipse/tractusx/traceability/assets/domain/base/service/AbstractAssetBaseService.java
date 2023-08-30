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
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect;
import org.eclipse.tractusx.traceability.common.config.AssetsAsyncConfig;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractAssetBaseService implements AssetBaseService {

    protected abstract AssetRepository getAssetRepository();

    protected abstract IrsRepository getIrsRepository();

    protected abstract List<String> getDownwardAspects();

    protected abstract List<String> getUpwardAspects();

    protected abstract BomLifecycle getBomLifecycle();

    @Async(value = AssetsAsyncConfig.SYNCHRONIZE_ASSETS_EXECUTOR)
    public void synchronizeAssetsAsync(String globalAssetId) {
        log.info("Synchronizing assets for globalAssetId: {}", globalAssetId);
        try {
            if (!getDownwardAspects().isEmpty()) {
                List<AssetBase> downwardAssets = getIrsRepository().findAssets(globalAssetId, Direction.DOWNWARD, getDownwardAspects(), getBomLifecycle());
                getAssetRepository().saveAll(downwardAssets);
            }

            if (!getUpwardAspects().isEmpty()) {

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


    public void setAssetsInvestigationStatus(QualityNotification investigation) {
        getAssetRepository().getAssetsById(investigation.getAssetIds()).forEach(asset -> {
            // Assets in status closed will be false, others true
            asset.setUnderInvestigation(!investigation.getNotificationStatus().equals(QualityNotificationStatus.CLOSED));
            getAssetRepository().save(asset);
        });
    }

    public void setAssetsAlertStatus(QualityNotification alert) {
        getAssetRepository().getAssetsById(alert.getAssetIds()).forEach(asset -> {
            // Assets in status closed will be false, others true
            asset.setActiveAlert(!alert.getNotificationStatus().equals(QualityNotificationStatus.CLOSED));
            getAssetRepository().save(asset);
        });
    }

    public Map<String, Long> getAssetsCountryMap() {
        return getAssetRepository().getAssets().stream()
                .collect(Collectors.groupingBy(asset -> asset.getSemanticModel().getManufacturingCountry(), Collectors.counting()));
    }

    public AssetBase updateQualityType(String assetId, QualityType qualityType) {
        AssetBase foundAsset = getAssetRepository().getAssetById(assetId);
        foundAsset.setQualityType(qualityType);
        return getAssetRepository().save(foundAsset);
    }

    public PageResult<AssetBase> getAssets(Pageable pageable, Owner owner) {
        return getAssetRepository().getAssets(pageable, owner);
    }

    public AssetBase getAssetById(String assetId) {
        return getAssetRepository().getAssetById(assetId);
    }

    public List<AssetBase> getAssetsById(List<String> assetIds) {
        return getAssetRepository().getAssetsById(assetIds);
    }

    public AssetBase getAssetByChildId(String assetId, String childId) {
        return getAssetRepository().getAssetByChildId(assetId, childId);
    }
}
