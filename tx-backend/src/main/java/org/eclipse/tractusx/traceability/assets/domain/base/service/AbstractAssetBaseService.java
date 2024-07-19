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
import org.eclipse.tractusx.traceability.assets.domain.base.JobRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.base.model.QualityType;
import org.eclipse.tractusx.traceability.assets.domain.base.model.SemanticDataModel;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.ManufacturingInfo;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect;
import org.eclipse.tractusx.traceability.common.config.AssetsAsyncConfig;
import org.springframework.scheduling.annotation.Async;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractAssetBaseService implements AssetBaseService {

    private static final List<String> SUPPORTED_ENUM_FIELDS = List.of("owner", "qualityType", "semanticDataModel", "importState");

    protected abstract AssetRepository getAssetRepository();

    protected abstract JobRepository getJobRepository();

    protected abstract List<String> getDownwardAspects();

    protected abstract List<String> getUpwardAspects();

    protected abstract BomLifecycle getBomLifecycle();

    @Override
    @Async(value = AssetsAsyncConfig.SYNCHRONIZE_ASSETS_EXECUTOR)
    public void synchronizeAssetsAsync(String globalAssetId) {
        log.info("Synchronizing assets for globalAssetId: {}", globalAssetId);
        try {
            if (!getDownwardAspects().isEmpty()) {
                getJobRepository().createJobToResolveAssets(globalAssetId, Direction.DOWNWARD, getDownwardAspects(), getBomLifecycle());
            }

            if (!getUpwardAspects().isEmpty()) {

                // TODO: change BomLifecycle.AS_BUILT to getBomLifecycle()
                getJobRepository().createJobToResolveAssets(globalAssetId, Direction.UPWARD, Aspect.upwardAspectsForAssetsAsBuilt(), BomLifecycle.AS_BUILT);
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
    public AssetBase updateQualityType(String assetId, QualityType qualityType) {
        AssetBase foundAsset = getAssetRepository().getAssetById(assetId);
        foundAsset.setQualityType(qualityType);
        return getAssetRepository().save(foundAsset);
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
    public AssetBase getAssetByChildId(String childId) {
        return getAssetRepository().getAssetByChildId(childId);
    }

    @Override
    public Map<String, Long> getAssetsCountryMap() {
        return getAssetRepository().getAssets().stream()
                .collect(Collectors.groupingBy(
                        asset -> ManufacturingInfo.from(asset.getDetailAspectModels()).getManufacturingCountry(), Collectors.counting()));
    }

    @Override
    public List<String> getSearchableValues(String fieldName, String startWith, Integer size, Owner owner, List<String> inAssetIds) {
        final Integer resultSize = Objects.isNull(size) ? Integer.MAX_VALUE : size;

        if (isSupportedEnumType(fieldName)) {
            return getAssetEnumFieldValues(fieldName);
        }
        return getAssetRepository().getFieldValues(fieldName, startWith, resultSize, owner, inAssetIds);
    }

    @Override
    public List<String> getAssetIdsInImportState(ImportState... importStates) {
        return getAssetRepository().findByImportStateIn(importStates).stream().map(AssetBase::getId).toList();
    }

    @Override
    public List<AssetBase> findAll() {
        return getAssetRepository().findAll();
    }

    private boolean isSupportedEnumType(String fieldName) {
        return SUPPORTED_ENUM_FIELDS.contains(fieldName);
    }

    private List<String> getAssetEnumFieldValues(String fieldName) {
        return switch (fieldName) {
            case "owner" -> Arrays.stream(Owner.values()).map(Enum::name).toList();
            case "qualityType" -> Arrays.stream(QualityType.values()).map(Enum::name).toList();
            case "semanticDataModel" -> Arrays.stream(SemanticDataModel.values()).map(Enum::name).toList();
            case "importState" -> Arrays.stream(ImportState.values()).map(Enum::name).toList();
            default -> null;
        };
    }
}
