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
package org.eclipse.tractusx.traceability.assets.application.importpoc;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class ImportServiceImpl implements ImportService {
    private final ObjectMapper objectMapper;

    @Override
    public void importAssets(MultipartFile file) {
        try {
            // TODO - build a json schema construct which consists of the assetmetainfo and the submodels.
            //  The submodels needs to be pulled by (example for serialpart) https://github.com/eclipse-tractusx/sldt-semantic-models/blob/main/io.catenax.serial_part/2.0.0/gen/SerialPart-schema.json
            //  It is okay to download the schemas and put them in a folder as we need to have control over the schemas
            // For the validation see: https://github.com/eclipse-tractusx/item-relationship-service/blob/main/irs-api/src/main/java/org/eclipse/tractusx/irs/services/validation/JsonValidatorService.java#L43

            String fileContent = new String(file.getBytes());
            log.info("Imported file: " + fileContent);
            ImportRequest importRequest = objectMapper.readValue(fileContent, ImportRequest.class);
          //Submodels per assetId
           // importRequest.assetRawRequestList().get(0).assetMetaInfoRequest().catenaXId();
          //  importRequest.assetRawRequestList().get(0).submodels();
        } catch (Exception e) {
            throw new ImportException(e.getMessage());
        }
    }
}
