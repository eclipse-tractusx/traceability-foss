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

import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.AssetMetaInfoRequest;
import org.eclipse.tractusx.traceability.assets.domain.importpoc.model.ImportRequest;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MainAspectAsPlannedStrategyTest {

    MainAspectAsPlannedStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new MainAspectAsPlannedStrategy();
    }


    @Test
    void testMappingWithNoSubmodels() {
        // given
        ImportRequest.AssetImportRequest assetImportRequestV2 = new ImportRequest.AssetImportRequest(
                new AssetMetaInfoRequest("catenaXId"),
                List.of()
        );

        // when
        AssetBase assetBase = strategy.mapToAssetBase(assetImportRequestV2, new TraceabilityProperties());

        // then
        assertThat(assetBase.getDetailAspectModels()).isNull();
    }
}
