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

package org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.exception.AssetNotFoundException;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltViewRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltViewEntity;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.repository.BaseSpecification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@RequiredArgsConstructor
@Component
@Transactional(readOnly = true)
public class AssetAsBuiltViewRepositoryImpl implements AssetAsBuiltViewRepository {

    private final JpaAssetAsBuiltViewRepository jpaAssetAsBuiltViewRepository;

    @Override
    public PageResult<AssetBase> getAssets(Pageable pageable, SearchCriteria searchCriteria) {
        List<AssetAsBuiltViewSpecification> assetAsBuildSpecifications = emptyIfNull(searchCriteria.getSearchCriteriaFilterList()).stream().map(AssetAsBuiltViewSpecification::new).toList();
        Specification<AssetAsBuiltViewEntity> specification = BaseSpecification.toSpecification(assetAsBuildSpecifications);
        return new PageResult<>(jpaAssetAsBuiltViewRepository.findAll(specification, pageable), AssetAsBuiltViewEntity::toDomain);
    }

    @Override
    public List<AssetBase> getAssets() {
        return jpaAssetAsBuiltViewRepository.findAll().stream()
                .map(AssetAsBuiltViewEntity::toDomain).toList();

    }

    @Override
    public AssetBase getAssetByChildId(String childId) {
        return jpaAssetAsBuiltViewRepository.findById(childId)
                .map(AssetAsBuiltViewEntity::toDomain)
                .orElseThrow(() -> new AssetNotFoundException("Child Asset Not Found"));
    }

    @Override
    public List<AssetBase> getAssetsById(List<String> assetIds) {
        return jpaAssetAsBuiltViewRepository.findByIdIn(assetIds).stream()
                .map(AssetAsBuiltViewEntity::toDomain)
                .toList();
    }

    @Override
    public AssetBase getAssetById(String assetId) {
        return jpaAssetAsBuiltViewRepository.findById(assetId)
                .map(AssetAsBuiltViewEntity::toDomain)
                .orElseThrow(() -> new AssetNotFoundException("Asset with id %s was not found.".formatted(assetId)));
    }
}
