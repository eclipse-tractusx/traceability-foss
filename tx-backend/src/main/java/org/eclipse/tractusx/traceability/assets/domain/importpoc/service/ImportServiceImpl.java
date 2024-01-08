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
import org.eclipse.tractusx.traceability.assets.domain.importpoc.ImportRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.ImportRequestV2;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.ImportException;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.v2.StrategyFactory;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.relationship.Aspect.isAsBuiltMainAspect;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImportServiceImpl implements ImportService {
    private final ObjectMapper objectMapper;
    private final AssetAsPlannedRepository assetAsPlannedRepository;
    private final AssetAsBuiltRepository assetAsBuiltRepository;
    private final TraceabilityProperties traceabilityProperties;
    private final StrategyFactory strategyFactory;

    @Override
    public void importAssets(MultipartFile file) {
        try {
            String fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);
            log.info("Imported file: " + fileContent);

            ImportRequest importRequest = objectMapper.readValue(fileContent, ImportRequest.class);

            List<AssetBase> persistedAsBuilts = this.assetAsBuiltRepository.saveAll(importRequest.convertAssetsAsBuilt());
            try {
                log.info("persistedAsBuilts as JSON {}", objectMapper.writeValueAsString(persistedAsBuilts));
            } catch (Exception e) {
                log.error("exception", e);
            }
            final String bpn = traceabilityProperties.getBpn().toString();
            List<AssetBase> persistedAsPlanned = this.assetAsPlannedRepository.saveAll(importRequest.convertAssetsAsPlanned(bpn));
            try {
                log.info("persistedAsPlanned as JSON {}", objectMapper.writeValueAsString(persistedAsPlanned));
            } catch (Exception e) {
                log.error("exception", e);
            }
        } catch (Exception e) {
            throw new ImportException(e.getMessage());
        }
    }

    @Override
    public void importAssetV2(MultipartFile file) {
        try {
            ImportRequestV2 importRequest = objectMapper.readValue(file.getBytes(), ImportRequestV2.class);
            log.info(importRequest.toString());
            Map<BomLifecycle, List<AssetBase>> map =
                    importRequest.assets().stream().map(strategyFactory::map).filter(Objects::nonNull).collect(Collectors.groupingBy(assetBase -> {
                        if (isAsBuiltMainAspect(assetBase.getSemanticDataModel().name())) {
                            return BomLifecycle.AS_BUILT;
                        } else {
                            return BomLifecycle.AS_PLANNED;
                        }
                    }));


            this.assetAsBuiltRepository.saveAll(map.get(BomLifecycle.AS_BUILT));
            this.assetAsPlannedRepository.saveAll(map.get(BomLifecycle.AS_PLANNED));

        } catch (Exception e) {
            log.error("did not work");
        }
    }

    public List<AssetBase> toAssetBase(ImportRequestV2 importRequestV2) {
        List<AssetBase> list = new ArrayList<>();
        return Collections.emptyList();
    }
}
