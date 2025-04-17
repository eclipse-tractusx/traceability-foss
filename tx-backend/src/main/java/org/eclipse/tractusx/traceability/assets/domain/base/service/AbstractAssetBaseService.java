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

import assets.request.PartChainIdentificationKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import orders.request.CreateOrderResponse;
import org.eclipse.tractusx.traceability.assets.application.base.service.AssetBaseService;
import org.eclipse.tractusx.traceability.assets.domain.base.AssetRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.OrderRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.domain.base.model.QualityType;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.ManufacturingInfo;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.request.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.Direction;
import org.eclipse.tractusx.traceability.common.domain.EnumFieldUtils;
import org.eclipse.tractusx.traceability.configuration.domain.model.OrderConfiguration;
import org.eclipse.tractusx.traceability.configuration.domain.model.TriggerConfiguration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect.upwardAspectsForAssetsAsBuilt;

@Slf4j
public abstract class AbstractAssetBaseService implements AssetBaseService {

    private static final List<String> SUPPORTED_ENUM_FIELDS = List.of("owner", "qualityType", "semanticDataModel", "importState");

    protected abstract AssetRepository getAssetRepository();

    protected abstract List<String> getDownwardAspects();

    protected abstract List<String> getUpwardAspects();

    protected abstract BomLifecycle getBomLifecycle();

    protected abstract OrderRepository getOrderRepository();

    protected abstract TriggerConfiguration getTriggerConfiguration();

    @Override
    public CreateOrderResponse syncAssetsUsingIRSOrderAPI(List<PartChainIdentificationKey> keys, OrderConfiguration orderConfiguration) {
        List<String> orderIds = new ArrayList<>();
        log.info("Synchronizing assets for aasList: {}", keys);
        try {
            if (!getDownwardAspects().isEmpty()) {
                orderIds.add(getOrderRepository().createOrderToResolveAssets(keys, Direction.DOWNWARD, getDownwardAspects(), getBomLifecycle(), orderConfiguration));
            }

            if (!getUpwardAspects().isEmpty()) {
                orderIds.add(getOrderRepository().createOrderToResolveAssets(keys, Direction.UPWARD, upwardAspectsForAssetsAsBuilt(), BomLifecycle.AS_BUILT, orderConfiguration));
            }
        } catch (Exception e) {
            log.warn("Exception during assets synchronization for aasList: {}. Message: {}.", keys, e.getMessage(), e);
        }
        return new CreateOrderResponse(orderIds);
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
    public void deleteAssetById(String assetId) {
        getAssetRepository().deleteAssetById(assetId);
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
    public List<String> getSearchableValues(String fieldName, List<String> startsWith, Integer size, Owner owner, List<String> inAssetIds) {
        final Integer resultSize = Objects.isNull(size) ? Integer.MAX_VALUE : size;

        if (isSupportedEnumType(fieldName)) {
            return EnumFieldUtils.getValues(fieldName, startsWith);
        }
        return getAssetRepository().getFieldValues(fieldName, startsWith, resultSize, owner, inAssetIds);
    }

    @Override
    public List<AssetBase> findAll() {
        return getAssetRepository().findAll();
    }

    private boolean isSupportedEnumType(String fieldName) {
        return SUPPORTED_ENUM_FIELDS.contains(fieldName);
    }

    @Override
    public void updateAssetsAfterJobCompletion(Set<AssetBase> assets) {
        TriggerConfiguration triggerConfiguration = getTriggerConfiguration();
        assets.forEach(asset -> {
            Optional<AssetBase> assetToUpdate = getAssetRepository().findById(asset.getId());
            assetToUpdate.ifPresentOrElse(assetBase -> {
                if (assetBase.getImportState() != ImportState.ERROR) {
                    asset.setTtl(triggerConfiguration.getAasTTL());
                    asset.setExpirationDate(LocalDateTime.now().plusSeconds(triggerConfiguration.getAasTTL() / 1000));
                    getAssetRepository().save(asset);
                    log.info("Asset with id {} has been updated with expiration date {}", asset.getId(), asset.getExpirationDate());
                } else {
                    log.info("Asset with id {} has an import state of ERROR, skipping updating assets expiration date",
                            asset.getId());
                }
            }, () -> log.info("No asset found with id {}, skipping updating assets expiration date", asset.getId()));
        });
    }
}
