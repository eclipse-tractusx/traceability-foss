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
import org.eclipse.tractusx.traceability.common.repository.CriteriaUtility;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AssetAsBuiltRepositoryImpl implements AssetAsBuiltRepository {

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
    public boolean existsById(String globalAssetId) {
        return jpaAssetAsBuiltRepository.existsById(globalAssetId);
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

    // TODO make sure this will update based on the import strategy and updated import note and state based on it
    @Override
    @Transactional
    public List<AssetBase> saveAllIfNotInIRSSyncAndUpdateImportStateAndNote(List<AssetBase> assets) {
        List<AssetAsBuiltEntity> savedEntities = new ArrayList<>();

        for (AssetBase asset : assets) {
            String assetId = asset.getId(); // Assuming getId() returns the ID of the entity

            // Check if the entity with the given ID already exists
            Optional<AssetAsBuiltEntity> existingEntityOptional = jpaAssetAsBuiltRepository.findById(assetId);

            if (existingEntityOptional.isPresent()) {
                // If it exists, update the single attribute
                AssetAsBuiltEntity existingEntity = existingEntityOptional.get();
                ImportNote importNote = existingEntity.getImportState().equals(ImportState.PERSISTENT)
                savedEntities.add(existingEntity);
            } else {
                // If it doesn't exist, save the new entity
                AssetAsBuiltEntity newEntity = AssetAsBuiltEntity.fromDomain(asset);
                savedEntities.add(newEntity);
            }
        }

        return jpaAssetAsBuiltRepository.saveAll(savedEntities).stream()
                .map(AssetAsBuiltEntity::toDomain)
                .toList();
    }

    // TODO check if it exists


    @Transactional
    @Override
    public void updateParentDescriptionsAndOwner(final AssetBase asset) {
        AssetBase assetById = this.getAssetById(asset.getId());
        if (assetById.getOwner().equals(Owner.UNKNOWN)) {
            assetById.setOwner(asset.getOwner());
        }
        assetById.setParentRelations(asset.getParentRelations());
        save(assetById);
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
