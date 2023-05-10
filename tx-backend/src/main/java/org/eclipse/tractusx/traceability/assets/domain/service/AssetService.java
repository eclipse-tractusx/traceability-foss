/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.assets.domain.service;

import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.QualityType;
import org.eclipse.tractusx.traceability.assets.domain.ports.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.ports.IrsRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.Aspect;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.config.async.AssetsAsyncConfig;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Component
public class AssetService {
    private static final Logger logger = LoggerFactory.getLogger(AssetService.class);

    private final AssetRepository assetRepository;
    private final IrsRepository irsRepository;

    public AssetService(AssetRepository assetRepository, IrsRepository irsRepository) {
        this.assetRepository = assetRepository;
        this.irsRepository = irsRepository;
    }

    public void synchronizeAssets(List<String> globalAssetIds) {
        for (String globalAssetId : globalAssetIds) {
            try {
                synchronizeAssets(globalAssetId);
            } catch (Exception e) {
                logger.warn("Cannot fetch assets for id: {}. Error: {}", globalAssetId, e.getMessage());
            }
        }
    }

    @Async(value = AssetsAsyncConfig.SYNCHRONIZE_ASSETS_EXECUTOR)
    public void synchronizeAssets(String globalAssetId) {
        logger.info("Synchronizing assets for globalAssetId: {}", globalAssetId);
        try {
            List<Asset> downwardAssets = irsRepository.findAssets(globalAssetId, Direction.DOWNWARD, Aspect.downwardAspects());
            List<Asset> upwardAssets = irsRepository.findAssets(globalAssetId, Direction.UPWARD, Aspect.upwardAspects());
            List<Asset> combinedAssetList = combineAssetsAndMergeParentDescriptionIntoDownwardAssets(downwardAssets, upwardAssets);
            assetRepository.saveAll(combinedAssetList);
            logger.info("Assets {} for globalAssetId {} successfully saved.", combinedAssetList, globalAssetId);
        } catch (Exception e) {
            logger.warn("Exception during assets synchronization for globalAssetId: {}. Message: {}.", globalAssetId, e.getMessage(), e);
        }
    }

    /**
     * Combines the list of downward assets with the list of upward assets by merging the parent descriptions of
     * matching child assets into the corresponding downward assets. If an upward asset has no matching downward asset,
     * it is added to the result list as is.
     *
     * @param downwardAssets the list of downward assets to be combined with the upward assets
     * @param upwardAssets   the list of upward assets to be combined with the downward assets
     * @return a new list of {@link Asset} objects that contains the combined assets with merged parent descriptions
     */
    public List<Asset> combineAssetsAndMergeParentDescriptionIntoDownwardAssets(List<Asset> downwardAssets, List<Asset> upwardAssets) {

        Map<String, Asset> downwardAssetsMap = emptyIfNull(downwardAssets).stream()
                .collect(Collectors.toMap(Asset::getId, Function.identity()));

        return emptyIfNull(upwardAssets).stream()
                .map(parentAsset -> {
                    Asset matchingChildAsset = downwardAssetsMap.get(parentAsset.getId());
                    if (matchingChildAsset == null) {
                        return parentAsset;
                    } else {
                        matchingChildAsset.setParentDescriptions(parentAsset.getParentDescriptions());
                        return matchingChildAsset;
                    }
                })
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void setAssetsInvestigationStatus(QualityNotification investigation) {
        assetRepository.getAssetsById(investigation.getAssetIds()).forEach(asset -> {
            // Assets in status closed will be false, others true
            asset.setUnderInvestigation(!investigation.getInvestigationStatus().equals(QualityNotificationStatus.CLOSED));
            assetRepository.save(asset);
        });

    }

    public Asset updateQualityType(String assetId, QualityType qualityType) {
        Asset foundAsset = assetRepository.getAssetById(assetId);
        foundAsset.updateQualityType(qualityType);
        return assetRepository.save(foundAsset);
    }

    public Map<String, Long> getAssetsCountryMap() {
        return assetRepository.getAssets().stream()
                .collect(Collectors.groupingBy(Asset::getManufacturingCountry, Collectors.counting()));
    }

    public void saveAssets(List<Asset> assets) {
        assetRepository.saveAll(assets);
    }
}
