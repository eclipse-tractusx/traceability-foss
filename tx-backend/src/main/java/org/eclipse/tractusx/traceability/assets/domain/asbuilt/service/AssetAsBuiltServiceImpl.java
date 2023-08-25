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

package org.eclipse.tractusx.traceability.assets.domain.asbuilt.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.application.asbuilt.service.AssetAsBuiltService;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.exception.AssetNotFoundException;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AssetAsBuiltServiceImpl implements AssetAsBuiltService {

    private final AssetAsBuiltRepository assetAsBuiltRepository;
    private final AssetAsPlannedRepository assetAsPlannedRepository;
    private final IrsRepository irsRepository;

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

    @Async(value = AssetsAsyncConfig.SYNCHRONIZE_ASSETS_EXECUTOR)
    public void synchronizeAssetsAsync(String globalAssetId) {
        log.info("Synchronizing assets for globalAssetId: {}", globalAssetId);
        try {
            syncAssetsAsBuilt(globalAssetId);
            syncAssetsAsPlanned(globalAssetId);

        } catch (Exception e) {
            log.warn("Exception during assets synchronization for globalAssetId: {}. Message: {}.", globalAssetId, e.getMessage(), e);
        }
    }

    private void syncAssetsAsPlanned(String globalAssetId) {
        List<AssetBase> downwardAssets = irsRepository.findAssets(globalAssetId, Direction.DOWNWARD, Aspect.downwardAspectsForAssetsAsPlanned(), BomLifecycle.AS_PLANNED);
        assetAsPlannedRepository.saveAll(downwardAssets);
    }

    private void syncAssetsAsBuilt(String globalAssetId) {
        List<AssetBase> downwardAssets = irsRepository.findAssets(globalAssetId, Direction.DOWNWARD, Aspect.downwardAspectsForAssetsAsBuilt(), BomLifecycle.AS_BUILT);
        assetAsBuiltRepository.saveAll(downwardAssets);

        List<AssetBase> upwardAssets = irsRepository.findAssets(globalAssetId, Direction.UPWARD, Aspect.upwardAspectsForAssetsAsBuilt(), BomLifecycle.AS_BUILT);

        upwardAssets.forEach(asset -> {
            if (assetAsBuiltRepository.existsById(asset.getId())) {
                log.info(asset.getId() + "isUpwardAsset 1 - asBuilt");
                assetAsBuiltRepository.updateParentDescriptionsAndOwner(asset);
            } else {
                log.info(asset.getId() + "isUpwardAsset 2 - asBuilt");
                assetAsBuiltRepository.save(asset);
            }
        });
    }

    public void setAssetsInvestigationStatus(QualityNotification investigation) {
        assetAsBuiltRepository.getAssetsById(investigation.getAssetIds()).forEach(asset -> {
            // Assets in status closed will be false, others true
            asset.setUnderInvestigation(!investigation.getNotificationStatus().equals(QualityNotificationStatus.CLOSED));
            assetAsBuiltRepository.save(asset);
        });
        assetAsPlannedRepository.getAssetsById(investigation.getAssetIds()).forEach(asset -> {
            // Assets in status closed will be false, others true
            asset.setUnderInvestigation(!investigation.getNotificationStatus().equals(QualityNotificationStatus.CLOSED));
            assetAsBuiltRepository.save(asset);
        });
    }

    public void setAssetsAlertStatus(QualityNotification alert) {
        assetAsBuiltRepository.getAssetsById(alert.getAssetIds()).forEach(asset -> {
            // Assets in status closed will be false, others true
            asset.setActiveAlert(!alert.getNotificationStatus().equals(QualityNotificationStatus.CLOSED));
            assetAsBuiltRepository.save(asset);
        });
        assetAsPlannedRepository.getAssetsById(alert.getAssetIds()).forEach(asset -> {
            // Assets in status closed will be false, others true
            asset.setActiveAlert(!alert.getNotificationStatus().equals(QualityNotificationStatus.CLOSED));
            assetAsBuiltRepository.save(asset);
        });
    }

    public AssetBase updateQualityType(String assetId, QualityType qualityType) {
        AssetBase foundAsset = assetAsBuiltRepository.getAssetById(assetId);
        if (foundAsset == null) {
            AssetBase foundAssetAsPlanned = assetAsPlannedRepository.getAssetById(assetId);
            foundAssetAsPlanned.setQualityType(qualityType);
            return assetAsPlannedRepository.save(foundAssetAsPlanned);
        } else {
            foundAsset.setQualityType(qualityType);
            return assetAsBuiltRepository.save(foundAsset);
        }


    }

    public Map<String, Long> getAssetsCountryMap() {
        Map<String, Long> assetsCountryMap = assetAsBuiltRepository.getAssets().stream()
                .collect(Collectors.groupingBy(asset -> asset.getSemanticModel().getManufacturingCountry(), Collectors.counting()));

        Map<String, Long> assetsAsPlannedCountryMap = assetAsPlannedRepository.getAssets().stream()
                .collect(Collectors.groupingBy(asset -> asset.getSemanticModel().getManufacturingCountry(), Collectors.counting()));

        Map<String, Long> mergedMap = new HashMap<>(assetsCountryMap);
        assetsAsPlannedCountryMap.forEach((country, count) -> mergedMap.merge(country, count, Long::sum));

        return mergedMap;
    }

    public PageResult<AssetBase> getAssets(Pageable pageable, Owner owner) {
        Pageable halfPage = halfPageable(pageable);
        PageResult<AssetBase> assetsAsPlanned = assetAsPlannedRepository.getAssets(halfPage, owner);
        PageResult<AssetBase> assetsAsBuilt = assetAsBuiltRepository.getAssets(halfPage, owner);

        List<AssetBase> mergedContent = new ArrayList<>(assetsAsPlanned.content());
        mergedContent.addAll(assetsAsBuilt.content());
        return new PageResult<>(mergedContent,
                assetsAsBuilt.page(),
                assetsAsBuilt.pageCount() + assetsAsPlanned.pageCount(),
                assetsAsBuilt.pageSize() + assetsAsBuilt.pageSize(),
                (long) mergedContent.size());
    }

    // TODO once asPlanned has own domain object this special pageable logic should be removed
    private Pageable halfPageable(Pageable pageable) {
        if (pageable != null) {
            int pageSize = pageable.getPageSize();
            int pageNumber = pageable.getPageNumber();
            return PageRequest.of(pageNumber / 2, pageSize / 2);
        }
        return Pageable.unpaged();
    }

    public AssetBase getAssetById(String assetId) {
        try {
            return assetAsBuiltRepository.getAssetById(assetId);
        } catch (AssetNotFoundException assetNotFoundException) {
            return assetAsPlannedRepository.getAssetById(assetId);
        }
    }

    public List<AssetBase> getAssetsById(List<String> assetIds) {
        List<AssetBase> assetAsBuiltIds = assetAsBuiltRepository.getAssetsById(assetIds);
        List<AssetBase> assetAsPlannedIds = assetAsPlannedRepository.getAssetsById(assetIds);
        List<AssetBase> mergedList = new ArrayList<>(assetAsBuiltIds);
        mergedList.addAll(assetAsPlannedIds);
        return mergedList;
    }

    public AssetBase getAssetByChildId(String assetId, String childId) {
        try {
            return assetAsBuiltRepository.getAssetByChildId(assetId, childId);
        } catch (AssetNotFoundException assetNotFoundException) {
            return assetAsPlannedRepository.getAssetByChildId(assetId, childId);
        }

    }
}
