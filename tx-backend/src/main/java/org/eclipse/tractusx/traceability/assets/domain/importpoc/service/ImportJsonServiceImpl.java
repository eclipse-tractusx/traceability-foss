/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.assets.domain.importpoc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.component.enums.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.application.importpoc.ImportJsonService;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.ImportException;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.ImportRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.repository.ImportJobRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.factory.DataConfigMapper;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.factory.ImportAssetMapper;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImportJsonServiceImpl implements ImportJsonService {
    private final ObjectMapper objectMapper;
    private final AssetAsBuiltRepository assetAsBuiltRepository;
    private final DataConfigMapper assetMapper;


    @Override
    public Map<AssetBase, Boolean> importJsonAssets(MultipartFile file) {
        try {
            ImportRequest importRequest = objectMapper.readValue(file.getBytes(), ImportRequest.class);
            Map<BomLifecycle, List<AssetBase>> assetToUploadByBomLifecycle = assetMapper.toAssetBaseList(importRequest.assets()).stream().collect(Collectors.groupingBy(AssetBase::getBomLifecycle));

            List<AssetBase> assetAsBuilt = assetToUploadByBomLifecycle.get(BomLifecycle.AS_BUILT);
            List<AssetBase> persistedAssets = assetAsBuiltRepository.saveAllIfNotInIRSSyncAndUpdateImportStateAndNote(assetAsBuilt);
            List<AssetBase> expectedAssetsToBePersisted = assetToUploadByBomLifecycle.values().stream().flatMap(Collection::stream).toList();

            return compareForUploadResult(expectedAssetsToBePersisted, persistedAssets);
        } catch (Exception e) {
            throw new ImportException(e.getMessage(), e);
        }
    }

    public static Map<AssetBase, Boolean> compareForUploadResult(List<AssetBase> incoming, List<AssetBase> persisted) {
        return getAssetBaseBooleanMap(incoming, persisted);
    }

    @NotNull
    static Map<AssetBase, Boolean> getAssetBaseBooleanMap(List<AssetBase> incoming, List<AssetBase> persisted) {
        return incoming.stream().map(asset -> {
                    Optional<AssetBase> persistedAssetOptional = persisted.stream().filter(persistedAsset -> persistedAsset.getId().equals(asset.getId())).findFirst();
                    return persistedAssetOptional.map(assetBase -> Map.entry(assetBase, true)).orElseGet(() -> Map.entry(asset, false));
                }
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
    }
}
