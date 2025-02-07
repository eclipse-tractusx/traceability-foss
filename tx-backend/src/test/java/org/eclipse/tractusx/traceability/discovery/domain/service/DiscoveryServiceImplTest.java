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
package org.eclipse.tractusx.traceability.discovery.domain.service;

import org.eclipse.tractusx.traceability.bpn.domain.model.BpnEdcMapping;
import org.eclipse.tractusx.traceability.bpn.infrastructure.repository.BpnRepository;
import org.eclipse.tractusx.traceability.common.properties.EdcProperties;
import org.eclipse.tractusx.traceability.discovery.domain.model.Discovery;
import org.eclipse.tractusx.traceability.discovery.domain.repository.DiscoveryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiscoveryServiceImplTest {

    private static final String DSP_PATH = "/api/v1/dsp";
    @InjectMocks
    private DiscoveryServiceImpl discoveryService;

    @Mock
    private DiscoveryRepository discoveryRepository;

    @Mock
    private BpnRepository bpnRepository;

    @Mock
    private EdcProperties edcProperties;

    @Test
    void testGetDiscoveryByBPNSuccessfulBoth() {

        // given
        Discovery discoveryByService = Discovery.builder().receiverUrls(List.of("https://receiver2.de")).senderUrl("https://sender2.de").build();
        BpnEdcMapping bpnEdcMapping = new BpnEdcMapping("bpn", "https://receiver.de", "name");
        when(bpnRepository.existsWhereUrlNotNull(any())).thenReturn(true);
        when(bpnRepository.findByIdOrThrowNotFoundException(any())).thenReturn(bpnEdcMapping);
        when(discoveryRepository.retrieveDiscoveryByFinderAndEdcDiscoveryService(any())).thenReturn(Optional.of(discoveryByService));
        when(edcProperties.getProviderEdcUrl()).thenReturn("https://sender2.de");
        when(edcProperties.getIdsPath()).thenReturn(DSP_PATH);
        // when
        Discovery discoveryByBPN = discoveryService.getDiscoveryByBPN("bpn");
        // then
        assertThat(discoveryByBPN.getReceiverUrls()).isEqualTo(List.of("https://receiver2.de" + DSP_PATH, "https://receiver.de" + DSP_PATH));
        assertThat(discoveryByBPN.getSenderUrl()).isEqualTo("https://sender2.de");
    }

    @Test
    void givenTrailingUrl_whenTestGetDiscoveryByBPN_thenSuccessfulBoth() {
        // given
        Discovery discoveryByService = Discovery.builder().receiverUrls(List.of("https://receiver2.de/")).senderUrl("https://sender2.de").build();
        BpnEdcMapping bpnEdcMapping = new BpnEdcMapping("bpn", "https://receiver.de", "name");
        when(bpnRepository.existsWhereUrlNotNull(any())).thenReturn(true);
        when(bpnRepository.findByIdOrThrowNotFoundException(any())).thenReturn(bpnEdcMapping);
        when(discoveryRepository.retrieveDiscoveryByFinderAndEdcDiscoveryService(any())).thenReturn(Optional.of(discoveryByService));
        when(edcProperties.getProviderEdcUrl()).thenReturn("https://sender2.de");
        when(edcProperties.getIdsPath()).thenReturn(DSP_PATH);
        // when
        Discovery discoveryByBPN = discoveryService.getDiscoveryByBPN("bpn");

        // then
        assertThat(discoveryByBPN.getReceiverUrls()).isEqualTo(List.of("https://receiver2.de" + DSP_PATH, "https://receiver.de" + DSP_PATH));
        assertThat(discoveryByBPN.getSenderUrl()).isEqualTo("https://sender2.de");
    }

    @Test
    void testGetDiscoveryByBPNSuccessfulNoFallback() {

        // given
        Discovery discoveryByService = Discovery.builder().receiverUrls(List.of("https://receiver2.de")).senderUrl("https://sender2.de").build();

        when(bpnRepository.existsWhereUrlNotNull(any())).thenReturn(false);
        when(discoveryRepository.retrieveDiscoveryByFinderAndEdcDiscoveryService(any())).thenReturn(Optional.of(discoveryByService));
        when(edcProperties.getIdsPath()).thenReturn(DSP_PATH);
        // when
        Discovery discoveryByBPN = discoveryService.getDiscoveryByBPN("bpn");

        // then
        assertThat(discoveryByBPN.getReceiverUrls()).isEqualTo(List.of("https://receiver2.de" + DSP_PATH));
        assertThat(discoveryByBPN.getSenderUrl()).isEqualTo("https://sender2.de");
    }
}
