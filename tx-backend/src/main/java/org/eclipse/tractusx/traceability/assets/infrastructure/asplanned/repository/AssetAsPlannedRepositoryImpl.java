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

package org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.exception.AssetNotFoundException;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportNote;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.AssetCallbackRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.AssetBaseEntity;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.repository.CriteriaUtility;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.AbstractMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@RequiredArgsConstructor
@Component
@Transactional
public class AssetAsPlannedRepositoryImpl implements AssetAsPlannedRepository, AssetCallbackRepository {

    private final JpaAssetAsPlannedRepository jpaAssetAsPlannedRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public AssetBase getAssetById(String assetId) {
        return jpaAssetAsPlannedRepository.findById(assetId).map(AssetAsPlannedEntity::toDomain)
                .orElseThrow(() -> new AssetNotFoundException("Asset with id %s was not found.".formatted(assetId)));
    }

    @Override
    public boolean existsById(String assetId) {
        return jpaAssetAsPlannedRepository.existsById(assetId);
    }

    @Override
    public List<AssetBase> getAssetsById(List<String> assetIds) {
        return jpaAssetAsPlannedRepository.findByIdIn(assetIds).stream().map(AssetAsPlannedEntity::toDomain)
                .toList();
    }

    @Override
    public AssetBase getAssetByChildId(String childId) {
        return jpaAssetAsPlannedRepository.findById(childId).map(AssetAsPlannedEntity::toDomain)
                .orElseThrow(() -> new AssetNotFoundException("Child Asset Not Found"));
    }

    @Override
    public List<AssetBase> findAll() {
        return jpaAssetAsPlannedRepository.findAll().stream()
                .map(AssetAsPlannedEntity::toDomain).toList();
    }


    @Override
    public PageResult<AssetBase> getAssets(Pageable pageable, SearchCriteria searchCriteria) {
        List<AssetAsPlannedSpecification> assetAsPlannedSpecifications = emptyIfNull(searchCriteria.getSearchCriteriaFilterList()).stream().map(AssetAsPlannedSpecification::new).toList();
        Specification<AssetAsPlannedEntity> specification = AssetAsPlannedSpecification.toSpecification(assetAsPlannedSpecifications);
        return new PageResult<>(jpaAssetAsPlannedRepository.findAll(specification, pageable), AssetAsPlannedEntity::toDomain);
    }

    @Override
    public List<AssetBase> getAssets() {
        return AssetAsPlannedEntity.toDomainList(jpaAssetAsPlannedRepository.findAll());
    }

    @Override
    public AssetBase save(AssetBase asset) {
        return AssetAsPlannedEntity.toDomain(jpaAssetAsPlannedRepository.save(AssetAsPlannedEntity.from(asset)));
    }

    @Override
    public List<AssetBase> saveAll(List<AssetBase> assets) {
        return AssetAsPlannedEntity.toDomainList(jpaAssetAsPlannedRepository.saveAll(AssetAsPlannedEntity.fromList(assets)));
    }

    @Override
    public List<AssetBase> saveAllIfNotInIRSSyncAndUpdateImportStateAndNote(List<AssetBase> assets) {
        if (Objects.isNull(assets)) {
            return List.of();
        }
        List<AssetAsPlannedEntity> toPersist = assets.stream().map(assetToPersist ->
                        new AbstractMap.SimpleEntry<AssetBase, AssetBaseEntity>(
                                assetToPersist,
                                jpaAssetAsPlannedRepository.findById(assetToPersist.getId()).orElse(null)))
                .filter(this::entityIsTransientOrNotExistent)
                .map(entry -> {
                    if (entry.getValue() != null) {
                        entry.getKey().setImportNote(ImportNote.TRANSIENT_UPDATED);
                    }
                    return entry.getKey();
                })
                .map(AssetAsPlannedEntity::from).toList();

        return jpaAssetAsPlannedRepository.saveAll(toPersist).stream().map(AssetAsPlannedEntity::toDomain).toList();

    }

    private boolean entityIsTransientOrNotExistent(AbstractMap.SimpleEntry<AssetBase, AssetBaseEntity> assetBaseAssetBaseEntitySimpleEntry) {
        if (Objects.isNull(assetBaseAssetBaseEntitySimpleEntry.getValue())) {
            return true;
        }
        return assetBaseAssetBaseEntitySimpleEntry.getValue().getImportState() == ImportState.TRANSIENT;
    }

    @Override
    public Optional<AssetBase> findById(String assetId) {
        return jpaAssetAsPlannedRepository.findById(assetId)
                .map(AssetAsPlannedEntity::toDomain);
    }

    @Override
    public long countAssets() {
        return jpaAssetAsPlannedRepository.count();
    }

    @Override
    public long countAssetsByOwner(Owner owner) {
        return jpaAssetAsPlannedRepository.countAssetsByOwner(owner);
    }

    @Override
    public List<String> getFieldValues(String fieldName, String startWith, Integer resultLimit, Owner owner, List<String> inAssetIds) {
        return CriteriaUtility.getDistinctAssetFieldValues(fieldName, startWith, resultLimit, owner, inAssetIds, AssetAsPlannedEntity.class, entityManager);
    }

    @Override
    public List<AssetBase> findByImportStateIn(ImportState... importStates) {
        return jpaAssetAsPlannedRepository.findByImportStateIn(importStates).stream()
                .map(AssetAsPlannedEntity::toDomain).toList();
    }

    @Override
    public void updateImportStateAndNoteForAssets(ImportState importState, String importNote, List<String> assetIds) {
        List<AssetAsPlannedEntity> foundAssets = jpaAssetAsPlannedRepository.findByIdIn(assetIds);
        foundAssets.forEach(assetAsPlanned -> {
            assetAsPlanned.setImportState(importState);
            assetAsPlanned.setImportNote(importNote);
        });
        jpaAssetAsPlannedRepository.saveAll(foundAssets);
    }
}
