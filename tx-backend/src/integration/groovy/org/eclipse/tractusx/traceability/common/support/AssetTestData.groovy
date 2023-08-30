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
package org.eclipse.tractusx.traceability.common.support

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase
import org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response.JobDetailResponse

class AssetTestData {

    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    List<AssetBase> readAndConvertAssetsForTests() {
        try {
            InputStream file = AssetTestData.class.getResourceAsStream("/data/irs_assets_v4.json")
            JobDetailResponse response = mapper.readValue(file, JobDetailResponse.class)
            def assets = response.convertAssets()
            return assets
        } catch (IOException e) {
            return Collections.emptyList()
        }
    }

}
