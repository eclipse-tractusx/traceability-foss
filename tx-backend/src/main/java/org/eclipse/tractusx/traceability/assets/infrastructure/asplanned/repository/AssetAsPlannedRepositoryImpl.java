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
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.AssetAsBuildSpecification;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.eclipse.tractusx.traceability.common.repository.EntityNameMapper.toDatabaseName;

@RequiredArgsConstructor
@Component
public class AssetAsPlannedRepositoryImpl implements AssetAsPlannedRepository {

    private final JpaAssetAsPlannedRepository jpaAssetAsPlannedRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public AssetBase getAssetById(String assetId) {
        return jpaAssetAsPlannedRepository.findById(assetId).map(AssetAsPlannedEntity::toDomain)
                .orElseThrow(() -> new AssetNotFoundException("Asset with id %s was not found.".formatted(assetId)));
    }

    @Override
    public boolean existsById(String globalAssetId) {
        return jpaAssetAsPlannedRepository.existsById(globalAssetId);
    }

    @Override
    public List<AssetBase> getAssetsById(List<String> assetIds) {
        return jpaAssetAsPlannedRepository.findByIdIn(assetIds).stream().map(AssetAsPlannedEntity::toDomain)
                .toList();
    }

    @Override
    public AssetBase getAssetByChildId(String assetId, String childId) {
        return jpaAssetAsPlannedRepository.findById(childId).map(AssetAsPlannedEntity::toDomain)
                .orElseThrow(() -> new AssetNotFoundException("Child Asset Not Found"));
    }


    @Override
    public PageResult<AssetBase> getAssets(Pageable pageable, SearchCriteria searchCriteria) {
        List<AssetAsPlannedSpecification> assetAsPlannedSpecifications = emptyIfNull(searchCriteria.getSearchCriteriaFilterList()).stream().map(AssetAsPlannedSpecification::new).toList();
        Specification<AssetAsPlannedEntity> specification = AssetAsPlannedSpecification.toSpecification(assetAsPlannedSpecifications, searchCriteria.getSearchCriteriaOperator());
        return new PageResult<>(jpaAssetAsPlannedRepository.findAll(specification, pageable), AssetAsPlannedEntity::toDomain);
    }

    @Override
    @Transactional
    public List<AssetBase> getAssets() {
        return AssetAsPlannedEntity.toDomainList(jpaAssetAsPlannedRepository.findAll());
    }

    @Override
    public AssetBase save(AssetBase asset) {
        return AssetAsPlannedEntity.toDomain(jpaAssetAsPlannedRepository.save(AssetAsPlannedEntity.from(asset)));
    }

    @Override
    @Transactional
    public List<AssetBase> saveAll(List<AssetBase> assets) {
        return AssetAsPlannedEntity.toDomainList(jpaAssetAsPlannedRepository.saveAll(AssetAsPlannedEntity.fromList(assets)));
    }

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
        return jpaAssetAsPlannedRepository.count();
    }

    @Override
    public long countAssetsByOwner(Owner owner) {
        return jpaAssetAsPlannedRepository.countAssetsByOwner(owner);
    }

    @Override
    public List<String> getFieldValues(String fieldName, Long resultLimit) {
        String databaseFieldName = toDatabaseName(fieldName);
        String getFieldValuesQuery = "SELECT DISTINCT " + databaseFieldName + " FROM assets_as_planned ORDER BY " + databaseFieldName + " ASC LIMIT :resultLimit";
        return entityManager.createNativeQuery(getFieldValuesQuery, String.class)
                .setParameter("resultLimit", resultLimit)
                .getResultList();
    }
}
