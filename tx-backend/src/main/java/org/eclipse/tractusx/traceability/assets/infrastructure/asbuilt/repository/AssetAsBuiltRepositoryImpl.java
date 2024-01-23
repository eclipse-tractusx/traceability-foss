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
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.eclipse.tractusx.traceability.common.repository.EntityNameMapper.toDatabaseName;

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
    public AssetBase getAssetByChildId(String assetId, String childId) {
        return jpaAssetAsBuiltRepository.findById(childId)
                .map(AssetAsBuiltEntity::toDomain)
                .orElseThrow(() -> new AssetNotFoundException("Child Asset Not Found"));
    }

    @Override
    public PageResult<AssetBase> getAssets(Pageable pageable, SearchCriteria searchCriteria) {
        List<AssetAsBuildSpecification> assetAsBuildSpecifications = emptyIfNull(searchCriteria.getSearchCriteriaFilterList()).stream().map(AssetAsBuildSpecification::new).toList();
        Specification<AssetAsBuiltEntity> specification = AssetAsBuildSpecification.toSpecification(assetAsBuildSpecifications, searchCriteria.getSearchCriteriaOperator());
        return new PageResult<>(jpaAssetAsBuiltRepository.findAll(specification, pageable), AssetAsBuiltEntity::toDomain);
    }

    @Override
    public List<String> getFieldValues(String fieldName, Long resultLimit) {
        String databaseFieldName = toDatabaseName(fieldName);
        String getFieldValuesQuery = "SELECT DISTINCT " + databaseFieldName + " FROM assets_as_built ORDER BY " + databaseFieldName + " ASC LIMIT :resultLimit";
        return entityManager.createNativeQuery(getFieldValuesQuery, String.class)
                .setParameter("resultLimit", resultLimit)
                .getResultList();
    }

    @Override
    @Transactional
    public List<AssetBase> getAssets() {
        return AssetAsBuiltEntity.toDomainList(jpaAssetAsBuiltRepository.findAll());
    }

    @Override
    public AssetBase save(AssetBase asset) {
        return AssetAsBuiltEntity.toDomain(jpaAssetAsBuiltRepository.save(AssetAsBuiltEntity.from(asset)));
    }

    @Override
    @Transactional
    public List<AssetBase> saveAll(List<AssetBase> assets) {
        return AssetAsBuiltEntity.toDomainList(jpaAssetAsBuiltRepository.saveAll(AssetAsBuiltEntity.fromList(assets)));
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
        return jpaAssetAsBuiltRepository.count();
    }

    @Override
    public long countAssetsByOwner(Owner owner) {
        return jpaAssetAsBuiltRepository.countAssetsByOwner(owner);
    }
}
