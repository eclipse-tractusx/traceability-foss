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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.discovery.domain.model.Discovery;
import org.eclipse.tractusx.traceability.discovery.domain.repository.DiscoveryRepository;
import org.eclipse.tractusx.traceability.discovery.infrastructure.model.ConnectorDiscoveryMappingResponse;
import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static org.eclipse.tractusx.traceability.discovery.domain.model.Discovery.toDiscovery;

@Slf4j
@RequiredArgsConstructor
@Component
public class FeignDiscoveryRepositoryImpl implements DiscoveryRepository {
    private final FeignDiscoveryRepository feignDiscoveryRepository;
    private final EdcProperties edcProperties;

    @Override
    public Optional<Discovery> getDiscoveryByBpnFromConnectorEndpoint(String bpn) {
        try {
            List<ConnectorDiscoveryMappingResponse> response = feignDiscoveryRepository.getConnectorEndpointMappings(List.of(bpn));
            return Optional.of(toDiscovery(response, bpn, edcProperties.getProviderEdcUrl()));
        } catch (Exception e) {
            log.warn("Exception during retrieving EDC Urls from DiscoveryService for {} bpn. Http Message: {} " +
                    "This is okay if the discovery service is not reachable from the specific environment", bpn, e.getMessage());
            return Optional.empty();
        }
    }
}
