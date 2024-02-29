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
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.GenericSubmodel;
import org.eclipse.tractusx.traceability.submodel.infrastructure.model.SubmodelPayloadEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SubmodelPayloadRepositoryImpl implements SubmodelPayloadRepository {
    private final JpaSubmodelPayloadRepository jpaSubmodelPayloadRepository;
    private final JpaAssetAsBuiltRepository jpaAssetAsBuiltRepository;
    private final JpaAssetAsPlannedRepository jpaAssetAsPlannedRepository;

    private static final String ASSET_NOT_FOUND_EXCEPTION_TEMPLATE = "Asset with id: '%s' not found while saving submodels";

    @Override
    public void savePayloadForAssetAsBuilt(String assetId, List<GenericSubmodel> submodels) {
        AssetAsBuiltEntity asset = jpaAssetAsBuiltRepository.findById(assetId).orElseThrow(() -> new AssetNotFoundException(ASSET_NOT_FOUND_EXCEPTION_TEMPLATE.formatted(assetId)));
        jpaSubmodelPayloadRepository.saveAll(SubmodelPayloadEntity.from(asset, submodels));
    }

    @Override
    public void savePayloadForAssetAsPlanned(String assetId, List<GenericSubmodel> submodels) {
        AssetAsPlannedEntity asset = jpaAssetAsPlannedRepository.findById(assetId).orElseThrow(() -> new AssetNotFoundException(ASSET_NOT_FOUND_EXCEPTION_TEMPLATE.formatted(assetId)));
        jpaSubmodelPayloadRepository.saveAll(SubmodelPayloadEntity.from(asset, submodels));
    }

    @Override
    public Map<String, String> getTypesAndPayloadsByAssetId(String assetId) {
        return null; // TODO implement
    }
}
