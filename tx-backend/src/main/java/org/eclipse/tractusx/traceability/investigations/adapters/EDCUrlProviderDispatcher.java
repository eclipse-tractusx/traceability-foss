/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.investigations.adapters;

import org.eclipse.tractusx.traceability.bpn.mapping.domain.ports.BpnEdcMappingRepository;
import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.eclipse.tractusx.traceability.investigations.adapters.bpn.mapping.BpnMappingProvider;
import org.eclipse.tractusx.traceability.investigations.adapters.feign.portal.DataspaceDiscoveryService;
import org.eclipse.tractusx.traceability.investigations.adapters.feign.portal.PortalAdministrationApiClient;
import org.eclipse.tractusx.traceability.investigations.domain.ports.EDCUrlProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class EDCUrlProviderDispatcher implements EDCUrlProvider {

    private static final Logger logger = LoggerFactory.getLogger(EDCUrlProviderDispatcher.class);

    private final DataspaceDiscoveryService dataspaceDiscoveryService;

    private final BpnEdcMappingRepository bpnEdcMappingRepository;

    private final BpnMappingProvider bpnMappingProvider;

    private final EdcProperties edcProperties;

    public EDCUrlProviderDispatcher(PortalAdministrationApiClient portalAdministrationApiClient,
                                    EdcProperties edcProperties, BpnEdcMappingRepository bpnEdcMappingRepository) {
        this.bpnEdcMappingRepository = bpnEdcMappingRepository;
        this.bpnMappingProvider = new BpnMappingProvider(bpnEdcMappingRepository, edcProperties);
        this.dataspaceDiscoveryService = new DataspaceDiscoveryService(portalAdministrationApiClient, edcProperties);
        this.edcProperties = edcProperties;
    }

    @Override
    public List<String> getEdcUrls(String bpn) {
        final List<String> edcUrls = dataspaceDiscoveryService.getEdcUrls(bpn);
        final List<String> fallbackEdcUrls = getEdcUrlsFallback(bpn);
        return combineDiscoveredUrlsAndFallbackUrls(edcUrls, fallbackEdcUrls);
    }

    private List<String> combineDiscoveredUrlsAndFallbackUrls(List<String> discoveredUrls, List<String> fallbackUrls) {
        Set<String> combinedUrlSet = new HashSet<>();
        combinedUrlSet.addAll(discoveredUrls);
        combinedUrlSet.addAll(fallbackUrls);
        return new ArrayList<>(combinedUrlSet);
    }

    private List<String> getEdcUrlsFallback(String bpn) {
        if (bpnMappingProvider != null) {
            return bpnMappingProvider.getEdcUrls(bpn);
        } else {
            logger.warn("No fallback method available for getting edc urls");
            return Collections.emptyList();
        }
    }

    @Override
    public String getSenderUrl() {
        return edcProperties.getProviderEdcUrl();
    }
}
