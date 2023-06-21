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
import org.eclipse.tractusx.traceability.assets.domain.model.Asset;
import org.eclipse.tractusx.traceability.assets.domain.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobDetailResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobDetailResponseTest {

    @Test
    void testAssetConverterAddsParentAssets() throws IOException {
        // Given
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        InputStream file = JobDetailResponseTest.class.getResourceAsStream("/data/irs_assets_v2_singleUsageAsBuilt.json");
        JobDetailResponse response = mapper.readValue(file, JobDetailResponse.class);
        // when
        List<Asset> assets = response.convertAssets();
        Asset ownAsset = assets.get(0);
        Asset parentAsset = assets.get(1);

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

}
