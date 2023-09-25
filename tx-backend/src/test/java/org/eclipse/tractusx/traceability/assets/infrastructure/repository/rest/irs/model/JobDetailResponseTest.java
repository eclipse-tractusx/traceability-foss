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

package org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.irs.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.eclipse.edc.spi.types.domain.asset.Asset;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobDetailResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobDetailResponseTest {

    ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Test
    void testAssetConverterAddsParentAssets() throws IOException {
        InputStream file = JobDetailResponseTest.class.getResourceAsStream("/data/irs_assets_v3_singleUsageAsBuilt.json");
        JobDetailResponse response = mapper.readValue(file, JobDetailResponse.class);
        // when
        List<AssetBase> assets = response.convertAssets();
        AssetBase ownAsset = assets.get(0);
        AssetBase parentAsset = assets.get(1);

        // then
        final String ownAssetId = "urn:uuid:8f9d8c7f-6d7a-48f1-9959-9fa3a1a7a891";
        final String parentAssetId = "urn:uuid:3e300930-0e1c-459c-8914-1ac631176716";

        assertThat(ownAsset.getId()).isEqualTo(ownAssetId);
        assertThat(ownAsset.getOwner()).isEqualTo(Owner.OWN);
        assertThat(ownAsset.getParentRelations().get(0).id()).isEqualTo(parentAssetId);
        assertTrue(ownAsset.getChildRelations().isEmpty());

        assertThat(parentAsset.getId()).isEqualTo(parentAssetId);
        assertThat(parentAsset.getOwner()).isEqualTo(Owner.CUSTOMER);
        assertThat(parentAsset.getParentRelations()).isEmpty();
        assertTrue(parentAsset.getChildRelations().isEmpty());
    }

    @Test
    void testAssetConverterWithNullManufacturerNames() throws IOException {
        InputStream file = JobDetailResponseTest.class.getResourceAsStream("/data/irs_job_response_with_null_manufacturer_names.json");

        // when
        JobDetailResponse response = mapper.readValue(file, JobDetailResponse.class);

        // then
        assertThat(response).isNotNull();
        assertThat(response.bpns())
                .containsEntry("BPNL00000003AZQP", "UNKNOWN_MANUFACTURER")
                .containsEntry("BPNL50096894aNXY", "UNKNOWN_MANUFACTURER")
                .containsEntry("BPNL00000003CML1", "TEST_BPN_DFT_1");
    }

}
