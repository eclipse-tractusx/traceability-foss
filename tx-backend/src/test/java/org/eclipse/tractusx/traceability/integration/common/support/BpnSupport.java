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

import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BpnSupport {

    @Autowired
    BpnRepositoryProvider bpnRepositoryProvider;
    @Autowired
    AssetRepositoryProvider assetRepositoryProvider;

    @Value("${traceability.bpn}")
    String bpn = null;

    public void cachedBpnsForDefaultAssets() {
        List<String> assetIds = assetRepositoryProvider.assetsConverter().readAndConvertAssetsForTests().stream().map(AssetBase::getManufacturerId).toList();
        Map<String, String> bpnMappings = new HashMap<>();

        for (String assetId : assetIds) {
            bpnMappings.put(assetId, "Manufacturer Name $i");
        }

        bpnRepositoryProvider.bpnRepository().updateManufacturers(bpnMappings);
    }

    public String testBpn() {
        return bpn;
    }
}
