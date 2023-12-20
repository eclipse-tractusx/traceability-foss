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
import org.eclipse.tractusx.traceability.assets.application.base.service.AssetBaseService;
import org.eclipse.tractusx.traceability.assets.application.importpoc.ImportService;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.ImportRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.exception.ImportException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImportServiceImpl implements ImportService {
    private final ObjectMapper objectMapper;
    private final AssetAsPlannedRepository assetAsPlannedRepository;
    private final AssetAsBuiltRepository assetAsBuiltRepository;

    @Override
    public void importAssets(MultipartFile file) {
        try {
            // TODO - build a json schema construct which consists of the assetmetainfo and the submodels.
            //  The submodels needs to be pulled by (example for serialpart) https://github.com/eclipse-tractusx/sldt-semantic-models/blob/main/io.catenax.serial_part/2.0.0/gen/SerialPart-schema.json
            //  It is okay to download the schemas and put them in a folder as we need to have control over the schemas
            // For the validation see: https://github.com/eclipse-tractusx/item-relationship-service/blob/main/irs-api/src/main/java/org/eclipse/tractusx/irs/services/validation/JsonValidatorService.java#L43

            String fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);
            log.info("Imported file: " + fileContent);
            ImportRequest importRequest = objectMapper.readValue(fileContent, ImportRequest.class);
            List<AssetBase> persistedAsBuilts = this.assetAsBuiltRepository.saveAll(importRequest.convertAssetsAsBuilt());
            try {
                log.info("persistedAsBuilts as JSON {}", objectMapper.writeValueAsString(persistedAsBuilts));
            } catch (Exception e) {
                log.error("exception", e);
            }
            List<AssetBase> persistedAsPlanned = this.assetAsPlannedRepository.saveAll(importRequest.convertAssetsAsPlanned());
            try {
                log.info("persistedAsPlanned as JSON {}", objectMapper.writeValueAsString(persistedAsPlanned));
            } catch (Exception e) {
                log.error("exception", e);
            }
        } catch (Exception e) {
            throw new ImportException(e.getMessage());
        }
    }
}
