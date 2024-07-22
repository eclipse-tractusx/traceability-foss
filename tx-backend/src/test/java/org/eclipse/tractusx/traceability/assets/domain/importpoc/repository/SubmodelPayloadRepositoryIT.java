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
package org.eclipse.tractusx.traceability.assets.domain.importpoc.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.ImportRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IrsSubmodel;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class SubmodelPayloadRepositoryIT extends IntegrationTestSpecification {

    @Autowired
    JpaAssetAsBuiltRepository assetAsBuiltRepository;
    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    SubmodelPayloadRepository submodelPayloadRepository;
    @Autowired
    JpaAssetAsBuiltRepository jpaAssetAsBuiltRepository;

    ObjectMapper objectMapper;

    @BeforeEach
    @Transactional
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void givenAssetAsBuilt_when() throws IOException {
        // given
        String filePath = "src/test/resources/testdata/import-request.json";

        String jsonString = Files.readString(Path.of(filePath));
        String assetId = "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb";
        ImportRequest importRequest = objectMapper.readValue(jsonString, ImportRequest.class);
        List<IrsSubmodel> submodels = importRequest.assets().stream()
                .filter(asset -> Objects.equals(asset.assetMetaInfoRequest().catenaXId(), assetId)).findFirst()
                .map(ImportRequest.AssetImportRequest::submodels).get();


        assetsSupport.defaultAssetsStored();
        jpaAssetAsBuiltRepository.findAll();
        submodelPayloadRepository.savePayloadForAssetAsBuilt(assetId, submodels);
        importRequest.assets().stream().map(it -> it.assetMetaInfoRequest().catenaXId()).toList();


        // when
        Map<String, String> result = submodelPayloadRepository.getAspectTypesAndPayloadsByAssetId(assetId);

        // then
        assertThat(result).isNotNull();
    }

}
