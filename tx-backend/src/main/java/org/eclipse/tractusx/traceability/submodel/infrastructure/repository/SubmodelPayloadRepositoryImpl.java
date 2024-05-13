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

package org.eclipse.tractusx.traceability.submodel.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.exception.AssetNotFoundException;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.repository.SubmodelPayloadRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.model.AssetAsPlannedEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.repository.JpaAssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsSubmodel;
import org.eclipse.tractusx.traceability.submodel.infrastructure.model.SubmodelPayloadEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SubmodelPayloadRepositoryImpl implements SubmodelPayloadRepository {
    private final JpaSubmodelPayloadRepository jpaSubmodelPayloadRepository;
    private final JpaAssetAsBuiltRepository jpaAssetAsBuiltRepository;
    private final JpaAssetAsPlannedRepository jpaAssetAsPlannedRepository;

    private static final String ASSET_NOT_FOUND_EXCEPTION_TEMPLATE = "Asset with id: '%s' not found while saving submodels";

    @Override
    public void savePayloadForAssetAsBuilt(String assetId, List<IrsSubmodel> submodels) {
        AssetAsBuiltEntity asset = jpaAssetAsBuiltRepository.findById(assetId).orElseThrow(() -> new AssetNotFoundException(ASSET_NOT_FOUND_EXCEPTION_TEMPLATE.formatted(assetId)));
        jpaSubmodelPayloadRepository.saveAll(SubmodelPayloadEntity.from(asset, submodels));
    }

    @Override
    public void savePayloadForAssetAsPlanned(String assetId, List<IrsSubmodel> submodels) {
        AssetAsPlannedEntity asset = jpaAssetAsPlannedRepository.findById(assetId).orElseThrow(() -> new AssetNotFoundException(ASSET_NOT_FOUND_EXCEPTION_TEMPLATE.formatted(assetId)));
        jpaSubmodelPayloadRepository.saveAll(SubmodelPayloadEntity.from(asset, submodels));
    }

    @Override
    public Map<String, String> getAspectTypesAndPayloadsByAssetId(String assetId) {
        Optional<AssetAsBuiltEntity> assetAsBuilt = jpaAssetAsBuiltRepository.findById(assetId);
        Optional<AssetAsPlannedEntity> assetAsPlanned = jpaAssetAsPlannedRepository.findById(assetId);

        if (assetAsBuilt.isPresent()) {
            return toTypesAndPayloadsMap(jpaSubmodelPayloadRepository.findByAssetAsBuilt(assetAsBuilt.get()));
        } else if (assetAsPlanned.isPresent()) {
            return toTypesAndPayloadsMap(jpaSubmodelPayloadRepository.findByAssetAsPlanned(assetAsPlanned.get()));
        }
        throw new AssetNotFoundException(ASSET_NOT_FOUND_EXCEPTION_TEMPLATE.formatted(assetId));
    }

    private Map<String, String> toTypesAndPayloadsMap(List<SubmodelPayloadEntity> entities) {
        return entities.stream().map(entity -> Map.entry(entity.getAspectType(), entity.getJson()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
