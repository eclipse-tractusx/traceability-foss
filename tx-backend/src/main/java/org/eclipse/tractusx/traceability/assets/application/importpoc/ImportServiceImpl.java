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
            // todo - how to deal with wrong aspecttype formats? They are currently just ignored and do not bring the mapper to fail
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
