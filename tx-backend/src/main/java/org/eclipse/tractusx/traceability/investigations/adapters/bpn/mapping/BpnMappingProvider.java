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
package org.eclipse.tractusx.traceability.investigations.adapters.bpn.mapping;

import org.eclipse.tractusx.traceability.bpn.mapping.domain.ports.BpnEdcMappingRepository;
import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.eclipse.tractusx.traceability.investigations.domain.ports.EDCUrlProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;


public class BpnMappingProvider implements EDCUrlProvider {

    private final BpnEdcMappingRepository bpnEdcMappingRepository;
    private final EdcProperties edcProperties;

    public BpnMappingProvider(@Autowired BpnEdcMappingRepository bpnEdcMappingRepository, EdcProperties edcProperties) {
        this.bpnEdcMappingRepository = bpnEdcMappingRepository;
        this.edcProperties = edcProperties;
    }

    @Override
    public List<String> getEdcUrls(String bpn) {
        if (bpnEdcMappingRepository.exists(bpn)) {
            return List.of(bpnEdcMappingRepository.findById(bpn).url());
        }
        return Collections.emptyList();
    }

    @Override
    public String getSenderUrl() {
        return edcProperties.getProviderEdcUrl();
    }

}
