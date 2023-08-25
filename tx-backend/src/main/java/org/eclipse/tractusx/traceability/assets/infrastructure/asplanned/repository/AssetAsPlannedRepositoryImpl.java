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

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.exception.AssetNotFoundException;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Component
public class AssetAsPlannedRepositoryImpl implements AssetAsPlannedRepository {

    private final JpaAssetAsPlannedRepository jpaAssetAsPlannedRepository;

    @Override
    @Transactional
    public Asset getAssetById(String assetId) {
        return jpaAssetAsPlannedRepository.findById(assetId).map(AssetAsPlannedEntity::toDomain)
                .orElseThrow(() -> new AssetNotFoundException("Asset with id %s was not found.".formatted(assetId)));
    }

    @Override
    public boolean existsById(String globalAssetId) {
        return jpaAssetAsPlannedRepository.existsById(globalAssetId);
    }

    @Override
    public List<Asset> getAssetsById(List<String> assetIds) {
        return jpaAssetAsPlannedRepository.findByIdIn(assetIds).stream().map(AssetAsPlannedEntity::toDomain)
                .toList();
    }

    @Override
    public Asset getAssetByChildId(String assetId, String childId) {
        return jpaAssetAsPlannedRepository.findById(childId).map(AssetAsPlannedEntity::toDomain)
                .orElseThrow(() -> new AssetNotFoundException("Child Asset Not Found"));
    }

    @Override
    public PageResult<Asset> getAssets(Pageable pageable, Owner owner) {
        if (owner != null) {
            return new PageResult<>(jpaAssetAsPlannedRepository.findByOwner(pageable, owner), AssetAsPlannedEntity::toDomain);
        }
        return new PageResult<>(jpaAssetAsPlannedRepository.findAll(pageable), AssetAsPlannedEntity::toDomain);
    }

    @Override
    @Transactional
    public List<Asset> getAssets() {
        return AssetAsPlannedEntity.toDomainList(jpaAssetAsPlannedRepository.findAll());
    }

    @Override
    public Asset save(Asset asset) {
        return AssetAsPlannedEntity.toDomain(jpaAssetAsPlannedRepository.save(AssetAsPlannedEntity.from(asset)));
    }

    @Override
    @Transactional
    public List<Asset> saveAll(List<Asset> assets) {
        return AssetAsPlannedEntity.toDomainList(jpaAssetAsPlannedRepository.saveAll(AssetAsPlannedEntity.fromList(assets)));
    }

    @Transactional
    @Override
    public void updateParentDescriptionsAndOwner(final Asset asset) {
        Asset assetById = this.getAssetById(asset.getId());
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
}
