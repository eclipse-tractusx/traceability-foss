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
package org.eclipse.tractusx.traceability.discovery.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.registryclient.discovery.EdcDiscoveryResult;
import org.eclipse.tractusx.traceability.discovery.infrastructure.model.ConnectorDiscoveryMappingResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Setter
@Getter
@Slf4j
@Builder
public class Discovery {
    private String senderUrl;
    private List<String> receiverUrls;


    public static Discovery toDiscovery(String receiverUrl, String senderUrl) {
        return Discovery.builder().receiverUrls(List.of(receiverUrl)).senderUrl(senderUrl).build();
    }

    public static Discovery toDiscovery(List<ConnectorDiscoveryMappingResponse> connectorDiscoveryMappingResponse, String bpn, String senderUrl) {
        Map<String, List<String>> bpnToEndpointMappings = emptyIfNull(connectorDiscoveryMappingResponse).stream()
                .collect(Collectors.toMap(ConnectorDiscoveryMappingResponse::bpn, ConnectorDiscoveryMappingResponse::connectorEndpoint));
        List<String> receiverUrls = bpnToEndpointMappings.get(bpn);
        return Discovery.builder().receiverUrls(receiverUrls).senderUrl(senderUrl).build();
    }

    public static Discovery toDiscovery(EdcDiscoveryResult edcDiscoveryResult, String senderUrl) {
        return Discovery.builder().receiverUrls(edcDiscoveryResult.connectorEndpoint()).senderUrl(senderUrl).build();
    }


    public static Discovery mergeDiscoveriesAndRemoveDuplicates(List<Discovery> discoveries) {
        Discovery mergedDiscovery = Discovery.builder().receiverUrls(new ArrayList<>()).build();
        final Set<String> seenHosts = new HashSet<>();

        discoveries.forEach(discovery -> {
            mergedDiscovery.setSenderUrl(discovery.getSenderUrl());
            mergedDiscovery.getReceiverUrls().addAll(discovery.getReceiverUrls());
        });

        List<String> receiverUrlsWithoutDuplicates = mergedDiscovery.getReceiverUrls().stream().filter(receiverUrl -> {
            try {
                String domain = extractDomain(receiverUrl);
                return seenHosts.add(domain);
            } catch (URISyntaxException e) {
                log.warn("Discovery connector response is not a valid URL: {}", receiverUrl);
                return false;
            }
        }).toList();
        mergedDiscovery.setReceiverUrls(receiverUrlsWithoutDuplicates);
        return mergedDiscovery;
    }

    private static String extractDomain(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        // Ensure consistency by stripping "www." if present
        if (domain != null && domain.startsWith("www.")) {
            domain = domain.substring(4);
        }
        log.info(domain, "domain");
        return domain;
    }


}
