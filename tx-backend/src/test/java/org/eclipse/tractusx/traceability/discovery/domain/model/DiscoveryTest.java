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

import org.eclipse.tractusx.traceability.discovery.infrastructure.model.ConnectorDiscoveryMappingResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DiscoveryTest {

    @Test
    void testMergeDiscoveryNotNull() {
        Discovery discovery = Discovery.mergeDiscoveriesAndRemoveDuplicates(Collections.emptyList());
        assertNotNull(discovery);
    }

    @Test
    void testMergeDiscoverySuccessful() {
        Discovery discovery1 = Discovery.toDiscovery("https://test2.de", "https://sender.de");
        Discovery discovery2 = Discovery.toDiscovery("https://test3.de", "https://sender.de");
        Discovery discovery = Discovery.mergeDiscoveriesAndRemoveDuplicates(List.of(discovery2, discovery1));
        assertThat(discovery.getReceiverUrls()).contains("https://test2.de");
        assertThat(discovery.getReceiverUrls()).contains("https://test3.de");
        assertThat(discovery.getReceiverUrls().size()).isEqualTo(2);
    }

    @Test
    void testMergeDiscoverySuccessfulRemoveDuplicates() {
        Discovery discovery1 = Discovery.toDiscovery("https://test2.de", "https://sender.de");
        Discovery discovery2 = Discovery.toDiscovery("https://test2.de", "https://sender.de");
        Discovery discovery = Discovery.mergeDiscoveriesAndRemoveDuplicates(List.of(discovery2, discovery1));
        assertThat(discovery.getReceiverUrls()).contains("https://test2.de");
        assertThat(discovery.getReceiverUrls().size()).isEqualTo(1);
    }

    @Test
    void testMergeDiscoverySuccessfulFromConnectorResponse() {
        Discovery discovery1 = Discovery.toDiscovery("https://test.de", "https://sender.de");
        ConnectorDiscoveryMappingResponse connectorDiscoveryMappingResponse = new ConnectorDiscoveryMappingResponse("bpn", List.of("https://abc.de", "https://abc2.de"));
        ConnectorDiscoveryMappingResponse connectorDiscoveryMappingResponse2 = new ConnectorDiscoveryMappingResponse("bpn2", List.of("https://abc3.de", "https://abc4.de"));
        Discovery discovery2 = Discovery.toDiscovery(List.of(connectorDiscoveryMappingResponse, connectorDiscoveryMappingResponse2), "bpn", "https://sender.de");
        Discovery discovery = Discovery.mergeDiscoveriesAndRemoveDuplicates(List.of(discovery2, discovery1));
        assertThat(discovery.getReceiverUrls()).contains("https://test.de");
        assertThat(discovery.getReceiverUrls()).contains("https://abc.de");
        assertThat(discovery.getReceiverUrls()).contains("https://abc2.de");
        assertThat(discovery.getReceiverUrls()).doesNotContain("https://abc3.de");
        assertThat(discovery.getReceiverUrls()).doesNotContain("https://abc4.de");
        assertThat(discovery.getSenderUrl()).isEqualTo("https://sender.de");
    }
}
