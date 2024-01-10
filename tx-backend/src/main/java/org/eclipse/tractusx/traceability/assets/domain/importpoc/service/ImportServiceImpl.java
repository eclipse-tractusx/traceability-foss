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
package org.eclipse.tractusx.traceability.assets.domain.importpoc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.component.enums.BomLifecycle;
import org.eclipse.tractusx.traceability.assets.application.importpoc.ImportService;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.ImportException;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.ImportRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.repository.SubmodelPayloadRepository;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImportServiceImpl implements ImportService {
    private final ObjectMapper objectMapper;
    private final AssetAsPlannedRepository assetAsPlannedRepository;
    private final AssetAsBuiltRepository assetAsBuiltRepository;
    private final TraceabilityProperties traceabilityProperties;
    private final MappingStrategyFactory strategyFactory;
    private final SubmodelPayloadRepository submodelPayloadRepository;

    @Override
    public Map<AssetBase, Boolean> importAssets(MultipartFile file) {
        try {
            ImportRequest importRequest = objectMapper.readValue(file.getBytes(), ImportRequest.class);


            Map<BomLifecycle, List<AssetBase>> assetToUploadByBomLifecycle =
                    importRequest.assets()
                            .stream()
                            .map(assetImportItem -> strategyFactory.mapToAssetBase(assetImportItem, traceabilityProperties))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.groupingBy(AssetBase::getBomLifecycle));

            List<AssetBase> persistedAsBuilt = assetAsBuiltRepository.saveAllIfNotInIRSSyncAndUpdateImportStateAndNote(assetToUploadByBomLifecycle.get(BomLifecycle.AS_BUILT));
            List<AssetBase> persistedAsPlanned = assetAsPlannedRepository.saveAllIfNotInIRSSyncAndUpdateImportStateAndNote(assetToUploadByBomLifecycle.get(BomLifecycle.AS_PLANNED));

            List<AssetBase> expectedAssetsToBePersisted = assetToUploadByBomLifecycle.values().stream().flatMap(Collection::stream).toList();
            List<AssetBase> persistedAssets = Stream.concat(persistedAsBuilt.stream(), persistedAsPlanned.stream()).toList();

            saveRawDataForPersistedAssets(persistedAssets, importRequest);

            return compareForUploadResult(expectedAssetsToBePersisted, persistedAssets);
        } catch (Exception e) {
            throw new ImportException(e.getMessage());
        }
    }

    private void saveRawDataForPersistedAssets(List<AssetBase> persistedAssets, ImportRequest importRequest) {
        List<String> persistedAssetsIds = persistedAssets.stream().map(AssetBase::getId).toList();
        importRequest.assets().stream().filter(asset -> persistedAssetsIds.contains(asset.assetMetaInfoRequest().catenaXId()))
                .map(assetImportRequest -> Map.entry(
                        getAssetById(assetImportRequest.assetMetaInfoRequest().catenaXId(), persistedAssets),
                        assetImportRequest.submodels()))
                .forEach(entry -> {
                    if (entry.getKey().getBomLifecycle() == BomLifecycle.AS_BUILT) {
                        submodelPayloadRepository.savePayloadForAssetAsBuilt(entry.getKey().getId(), entry.getValue());
                    } else if (entry.getKey().getBomLifecycle() == BomLifecycle.AS_PLANNED) {
                        submodelPayloadRepository.savePayloadForAssetAsPlanned(entry.getKey().getId(), entry.getValue());
                    }
                });
    }

    private AssetBase getAssetById(String assetId, List<AssetBase> assets) {
        return assets.stream().filter(asset -> asset.getId().equals(assetId)).findFirst().orElseThrow(() -> new ImportException("Failed when trying to persist raw payload to persisted Assets"));
    }

    public static Map<AssetBase, Boolean> compareForUploadResult(List<AssetBase> incoming, List<AssetBase> persisted) {
        return incoming.stream().map(asset -> {
                    Optional<AssetBase> persistedAssetOptional = persisted.stream().filter(persistedAsset -> persistedAsset.getId().equals(asset.getId())).findFirst();
                    return persistedAssetOptional.map(assetBase -> Map.entry(assetBase, true)).orElseGet(() -> Map.entry(asset, false));
                }
        ).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
    }
}
