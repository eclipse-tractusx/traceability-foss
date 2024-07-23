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
package org.eclipse.tractusx.traceability.discovery.infrastructure.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.registryclient.discovery.DiscoveryFinderClient;
import org.eclipse.tractusx.irs.registryclient.discovery.DiscoveryFinderRequest;
import org.eclipse.tractusx.irs.registryclient.discovery.DiscoveryResponse;
import org.eclipse.tractusx.irs.registryclient.discovery.EdcDiscoveryResult;
import org.eclipse.tractusx.traceability.common.properties.EdcProperties;
import org.eclipse.tractusx.traceability.discovery.domain.model.Discovery;
import org.eclipse.tractusx.traceability.discovery.domain.repository.DiscoveryRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.eclipse.tractusx.traceability.discovery.domain.model.Discovery.toDiscovery;

@Slf4j
@RequiredArgsConstructor
@Component
public class FeignDiscoveryRepositoryImpl implements DiscoveryRepository {
    private final EdcProperties edcProperties;
    private final DiscoveryFinderClient discoveryFinderClient;
    private final ObjectMapper objectMapper;

    @Override
    public Optional<Discovery> retrieveDiscoveryByFinderAndEdcDiscoveryService(String bpn) {
        DiscoveryFinderRequest request = new DiscoveryFinderRequest(List.of(bpn));
        DiscoveryResponse discoveryEndpoints = discoveryFinderClient.findDiscoveryEndpoints(request);
        try {
            objectMapper.writeValueAsString(discoveryEndpoints);
        } catch (JsonProcessingException e) {
            log.warn("Could not write value as string");
        }
        List<EdcDiscoveryResult> discoveryResults = new ArrayList<>();
        discoveryEndpoints.endpoints().forEach(discoveryEndpoint -> {
            String endPointAddress = discoveryEndpoint.endpointAddress();
            log.info("endPointAddress {}", endPointAddress);
            List<EdcDiscoveryResult> connectorEndpoints = discoveryFinderClient.findConnectorEndpoints(endPointAddress, List.of(bpn));
            try {
                objectMapper.writeValueAsString(connectorEndpoints);
            } catch (JsonProcessingException e) {
                log.warn("Could not write value as string");
            }
            discoveryResults.addAll(connectorEndpoints);
        });
        List<EdcDiscoveryResult> discoveryResultByBPN
                = discoveryResults.stream().filter(edcDiscoveryResult -> edcDiscoveryResult.bpn().equals(bpn)).toList();

        log.info("Retrieved discoveryResult size for bpn {} is {}",
                bpn,
                discoveryResultByBPN.stream()
                        .findFirst().orElse(new EdcDiscoveryResult(null, Collections.emptyList()))
                        .connectorEndpoint().size()
        );

        if (discoveryResultByBPN.size() > 1) {
            log.warn("Multiple discoveryResults with same bpn {} found, but only the edcDiscoveryResultOptional will be used!", bpn);
        }

        Optional<EdcDiscoveryResult> edcDiscoveryResultOptional = discoveryResultByBPN.stream().findFirst();

        if (edcDiscoveryResultOptional.isPresent()) {
            return Optional.of(toDiscovery(edcDiscoveryResultOptional.get(), edcProperties.getProviderEdcUrl()));
        } else {
            log.warn("No discovery result found. Please check if connector for bpn {} is registered in discovery finder.", bpn);
            return Optional.empty();
        }
    }
}
