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
package org.eclipse.tractusx.traceability.integration.common.support;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.IRSResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.factory.IrsResponseAssetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
public class AssetTestData {

    @Autowired
    private IrsResponseAssetMapper assetMapperFactory;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(new SimpleModule().addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    List<AssetBase> readAndConvertAssetsForTests() {
        return getAssetBases("/testdata/irs_assets_as_built_1_v4.json");
    }

    List<AssetBase> readAndConvertMultipleAssetsAsBuiltForTests() {
        // Asset 1
        final List<AssetBase> assetBaseList = readAndConvertAssetsForTests();
        // Asset 2
        assetBaseList.addAll(getAssetBases("/testdata/irs_assets_as_built_2_v4.json"));
        return assetBaseList;
    }

    List<AssetBase> readAndConvertTractionBatteryCodeAssetsForTests() {
        return getAssetBases("/data/irs_assets_tractionbatterycode.json");
    }

    List<AssetBase> readAndConvertAssetsAsPlannedForTests() {
        // Test data contains different spellings for 'catenaXSiteId', as long as no clear spelling is defined. https://github.com/eclipse-tractusx/sldt-semantic-models/issues/470
        return getAssetBases("/testdata/irs_assets_as_planned_v4.json");
    }

    List<AssetBase> readAndConvertAssetsAsPlannedForTests(final String resourceName) {
        return getAssetBases(resourceName);
    }

    @NotNull
    private List<AssetBase> getAssetBases(final String resourceName) {
        try {
            final InputStream file = AssetTestData.class.getResourceAsStream(resourceName);
            final IRSResponse response = mapper.readValue(file, IRSResponse.class);
            return assetMapperFactory.toAssetBaseList(response);
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}
