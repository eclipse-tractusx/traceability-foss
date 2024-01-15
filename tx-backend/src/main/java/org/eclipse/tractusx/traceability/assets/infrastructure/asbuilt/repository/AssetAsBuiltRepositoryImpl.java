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

package org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.exception.AssetNotFoundException;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportNote;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.AssetAsBuiltCallbackRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.AssetBaseEntity;
import org.eclipse.tractusx.traceability.common.repository.CriteriaUtility;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AssetAsBuiltRepositoryImpl implements AssetAsBuiltRepository, AssetAsBuiltCallbackRepository {

    private final JpaAssetAsBuiltRepository jpaAssetAsBuiltRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public AssetBase getAssetById(String assetId) {
        return jpaAssetAsBuiltRepository.findById(assetId)
                .map(AssetAsBuiltEntity::toDomain)
                .orElseThrow(() -> new AssetNotFoundException("Asset with id %s was not found.".formatted(assetId)));
    }

    @Override
    public List<AssetBase> getAssetsById(List<String> assetIds) {
        return jpaAssetAsBuiltRepository.findByIdIn(assetIds).stream()
                .map(AssetAsBuiltEntity::toDomain)
                .toList();
    }

    @Override
    public AssetBase getAssetByChildId(String childId) {
        return jpaAssetAsBuiltRepository.findById(childId)
                .map(AssetAsBuiltEntity::toDomain)
                .orElseThrow(() -> new AssetNotFoundException("Child Asset Not Found"));
    }

    @Override
    public List<String> getFieldValues(String fieldName, String startWith, Integer resultLimit, Owner owner) {
        return CriteriaUtility.getDistinctAssetFieldValues(fieldName, startWith, resultLimit, owner, AssetAsBuiltEntity.class, entityManager);
    }

    @Override
    @Transactional
    public List<AssetBase> getAssets() {
        return jpaAssetAsBuiltRepository.findAll().stream()
                .map(AssetAsBuiltEntity::toDomain)
                .toList();
    }

    @Override
    public AssetBase save(AssetBase asset) {
        return jpaAssetAsBuiltRepository.save(AssetAsBuiltEntity.from(asset)).toDomain();
    }

    @Override
    @Transactional
    public List<AssetBase> saveAll(List<AssetBase> assets) {
        return jpaAssetAsBuiltRepository.saveAll(AssetAsBuiltEntity.fromList(assets)).stream()
                .map(AssetAsBuiltEntity::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public List<AssetBase> saveAllIfNotInIRSSyncAndUpdateImportStateAndNote(List<AssetBase> assets) {

        List<AssetAsBuiltEntity> toPersist = assets.stream().map(assetToPersist -> new AbstractMap.SimpleEntry<AssetBase, AssetBaseEntity>(assetToPersist, jpaAssetAsBuiltRepository.findById(assetToPersist.getId()).orElse(null)))
                .filter(this::entityIsTransientOrNotExistent)
                .map(entry -> {
                    if (entry.getValue() != null) {
                        entry.getKey().setImportNote(ImportNote.TRANSIENT_UPDATED);
                    }
                    return entry.getKey();
                })
                .map(AssetAsBuiltEntity::from).toList();

        return jpaAssetAsBuiltRepository.saveAll(toPersist).stream().map(AssetAsBuiltEntity::toDomain).toList();
    }

    private boolean entityIsTransientOrNotExistent(AbstractMap.SimpleEntry<AssetBase, AssetBaseEntity> assetBaseAssetBaseEntitySimpleEntry) {
        if (Objects.isNull(assetBaseAssetBaseEntitySimpleEntry.getValue())) {
            return true;
        }
        return assetBaseAssetBaseEntitySimpleEntry.getValue().getImportState() == ImportState.TRANSIENT;
    }

    // TODO check if it exists


//    @Transactional
//    @Override
//    // TODO remove ?
//    public void updateParentDescriptionsAndOwner(final AssetBase asset) {
//        AssetBase assetById = this.getAssetById(asset.getId());
//        if (assetById.getOwner().equals(Owner.UNKNOWN)) {
//            assetById.setOwner(asset.getOwner());
//        }
//        assetById.setParentRelations(asset.getParentRelations());
//        save(assetById);
//    }

    @Transactional
    @Override
    // Why if it exists we only update parent relations ? not other data etc ? check this logic
    public void updateParentDescriptionsAndOwnerOrSaveNew(final AssetBase asset) {
        Optional<AssetBase> existingAssetOptional = findAssetById(asset.getId());
        if(existingAssetOptional.isPresent()) {
            AssetBase existingAsset = existingAssetOptional.get();
            if (existingAsset.getOwner().equals(Owner.UNKNOWN)) {
                existingAsset.setOwner(asset.getOwner());
            }
            if (!asset.getParentRelations().isEmpty()) { // No other way to know callback direction
                existingAsset.setParentRelations(asset.getParentRelations());
            }
            save(existingAsset);
        } else {
            save(asset);
        }
    }

    private Optional<AssetBase> findAssetById(String assetId) {
        return jpaAssetAsBuiltRepository.findById(assetId)
                .map(AssetAsBuiltEntity::toDomain);
    }

    @Transactional
    @Override
    public long countAssets() {
        return jpaAssetAsBuiltRepository.count();
    }

    @Override
    public long countAssetsByOwner(Owner owner) {
        return jpaAssetAsBuiltRepository.countAssetsByOwner(owner);
    }
}
