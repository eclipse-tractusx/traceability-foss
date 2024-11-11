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

package org.eclipse.tractusx.traceability.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.application.importpoc.ImportJsonService;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.common.model.CustomMultiPartFile;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@Component
@RequiredArgsConstructor
public class ImportDataConfig {
    private final TraceabilityProperties traceabilityProperties;
    private final ImportJsonService importJsonService;

    private static final String BMW_BPN = "BPNL000000000ISY";
    private static final String TX_A_BPN = "BPNLCOFINITYEZFA";
    private static final String FILE_PATH_A = "testdata/preprod-tx-a.json";
    private static final String FILE_PATH_BMW = "testdata/preprod-tx-bmw.json";

    @EventListener(ApplicationReadyEvent.class)
    public void initializeTestData() {
        if (traceabilityProperties.isApplicationBpn(BMW_BPN) || traceabilityProperties.isApplicationBpn(TX_A_BPN)) {
            ExecutorService executor = Executors.newSingleThreadExecutor();

            executor.execute(() -> {
                try {
                    this.importTestData();
                } catch (Exception exception) {
                    log.error("Failed to create Irs Policies: ", exception);
                }
            });

            executor.shutdown();
        }

    }

    private void importTestData() {
        log.info("Importing test data...");
        try {
            importAssets(FILE_PATH_A);
            importAssets(FILE_PATH_BMW);
            log.info("Test data imported successfully.");
        } catch (IOException e) {
            log.error("Error while importing test data from JSON files: {}", e.getMessage(), e);
        }
    }

    private void importAssets(String filePath) throws IOException {
        MultipartFile multipartFile = createMultipartFile(filePath);
        Map<AssetBase, Boolean> assetImportResults = importJsonService.importJsonAssets(multipartFile);
        log.info("Imported assets from {}: {}", filePath, assetImportResults);
    }

    private MultipartFile createMultipartFile(String filePath) throws IOException {
        Path path = Path.of(filePath);
        byte[] content = Files.readAllBytes(path);
        String fileName = path.getFileName().toString();
        return new CustomMultiPartFile(content, fileName, "application/json");
    }

}
