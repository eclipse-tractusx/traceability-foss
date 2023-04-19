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

package org.eclipse.tractusx.traceability.investigations.adapters.feign.portal;

import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.eclipse.tractusx.traceability.investigations.domain.ports.EDCUrlProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataspaceDiscoveryService implements EDCUrlProvider {

    private static final Logger logger = LoggerFactory.getLogger(DataspaceDiscoveryService.class);

    private final PortalAdministrationApiClient portalAdministrationApiClient;

    private final EdcProperties edcProperties;

    public DataspaceDiscoveryService(PortalAdministrationApiClient portalAdministrationApiClient, EdcProperties edcProperties) {
        this.portalAdministrationApiClient = portalAdministrationApiClient;
        this.edcProperties = edcProperties;
    }

    @Override
    public List<String> getEdcUrls(String bpn) {
        final List<ConnectorDiscoveryMappingResponse> response;
        try {
            response = portalAdministrationApiClient.getConnectorEndpointMappings(List.of(bpn));
            if (response == null) {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            logger.warn("Exception during fetching connector endpoints for {} bpn. Http Message: {} " +
                    "This is okay if the discovery service is not reachable from the specific environment", bpn, e.getMessage());
            return Collections.emptyList();
        }


        Map<String, List<String>> bpnToEndpointMappings = response.stream()
                .collect(Collectors.toMap(ConnectorDiscoveryMappingResponse::bpn, ConnectorDiscoveryMappingResponse::connectorEndpoint));

        List<String> endpoints = bpnToEndpointMappings.get(bpn);

        if (endpoints == null) {
            logger.warn("No connector endpoint registered for {} bpn", bpn);

            throw new IllegalStateException("No connector endpoint registered for %s bpn".formatted(bpn));
        }

        return endpoints;
    }

    @Override
    public String getSenderUrl() {
        return edcProperties.getProviderEdcUrl();
    }
}
