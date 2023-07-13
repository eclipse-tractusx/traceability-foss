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

import org.eclipse.tractusx.traceability.discovery.domain.model.Discovery;
import org.eclipse.tractusx.traceability.discovery.infrastructure.model.ConnectorDiscoveryMappingResponse;
import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeignDiscoveryRepositoryImplTest {

    @InjectMocks
    private FeignDiscoveryRepositoryImpl feignDiscoveryRepositoryImpl;

    @Mock
    private EdcProperties edcProperties;

    @Mock
    private FeignDiscoveryRepository feignDiscoveryRepository;

    @Test
    void testGetDiscoveryByBpnFromConnectorEndpointSuccessful() {
        ConnectorDiscoveryMappingResponse connectorDiscoveryMappingResponse = new ConnectorDiscoveryMappingResponse("bpn", List.of("test.de"));
        when(feignDiscoveryRepository.getConnectorEndpointMappings(List.of("bpn"))).thenReturn(List.of(connectorDiscoveryMappingResponse));
        when(edcProperties.getProviderEdcUrl()).thenReturn("sender.de");
        Optional<Discovery> discovery = feignDiscoveryRepositoryImpl.retrieveDiscoveryByFinderAndEdcDiscoveryService("bpn");

        Assertions.assertTrue(discovery.isPresent());
        assertThat(discovery.get().getReceiverUrls()).isEqualTo(List.of("test.de"));
        assertThat(discovery.get().getSenderUrl()).isEqualTo("sender.de");
    }

    @Test
    void testGetDiscoveryByBpnFromConnectorEndpointException() {
        when(feignDiscoveryRepository.getConnectorEndpointMappings(List.of("bpn"))).thenThrow(new RuntimeException());
        Optional<Discovery> discoveryByBpnFromConnectorEndpoint = feignDiscoveryRepositoryImpl.retrieveDiscoveryByFinderAndEdcDiscoveryService("bpn");
        Assertions.assertTrue(discoveryByBpnFromConnectorEndpoint.isEmpty());
    }
}
