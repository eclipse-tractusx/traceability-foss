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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.ImportJob;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.ImportJobStatus;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.repository.ImportJobRepository;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.repository.SubmodelPayloadRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.factory.ImportAssetMapper;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.eclipse.tractusx.traceability.testdata.AssetTestDataFactory.createAssetAsBuiltTestdata;
import static org.eclipse.tractusx.traceability.testdata.AssetTestDataFactory.createAssetAsPlannedTestdata;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImportServiceImplTest {

    @InjectMocks
    private ImportServiceImpl importService;

    @Mock
    private AssetAsPlannedRepository assetAsPlannedRepository;
    @Mock
    private AssetAsBuiltRepository assetAsBuiltRepository;
    @Mock
    private SubmodelPayloadRepository submodelPayloadRepository;

    @Mock
    private TraceabilityProperties traceabilityProperties;
    @Mock
    private ImportJobRepository importJobRepository;

    @Mock
    private ImportAssetMapper assetMapper;

    @BeforeEach
    public void testSetup() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        importService = new ImportServiceImpl(objectMapper, assetAsPlannedRepository, assetAsBuiltRepository, traceabilityProperties, submodelPayloadRepository, importJobRepository, assetMapper);

    }

    @Test
    void testImportRequestSuccessful() throws IOException {

        InputStream file = ImportServiceImplTest.class.getResourceAsStream("/testdata/import-request.json");
        // Convert the file to a MockMultipartFile
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",               // Parameter name in the multipart request
                "import-request",             // Original file name
                "application/json",   // Content type
                file
        );

        when(assetMapper.toAssetBaseList(any())).thenReturn(List.of(createAssetAsBuiltTestdata(), createAssetAsPlannedTestdata()));
        when(traceabilityProperties.getBpn()).thenReturn(BPN.of("BPNL000000000UKM"));
        importService.importAssets(multipartFile, new ImportJob(UUID.randomUUID(), Instant.now(), null, ImportJobStatus.RUNNING, List.of(), List.of()));
        verify(assetAsBuiltRepository, times(1)).saveAllIfNotInIRSSyncAndUpdateImportStateAndNote(anyList());
        verify(assetAsPlannedRepository, times(1)).saveAllIfNotInIRSSyncAndUpdateImportStateAndNote(anyList());
    }
}
