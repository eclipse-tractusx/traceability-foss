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
        Discovery discovery = Discovery.mergeDiscoveries(Collections.emptyList());
        assertNotNull(discovery);
    }

    @Test
    void testMergeDiscoverySuccessful() {
        Discovery discovery1 = Discovery.toDiscovery("test.de", "sender.de");
        Discovery discovery2 = Discovery.toDiscovery("test2.de", "sender2.de");
        Discovery discovery = Discovery.mergeDiscoveries(List.of(discovery2, discovery1));
        assertThat(discovery.getReceiverUrls()).contains("test.de");
        assertThat(discovery.getReceiverUrls()).contains("test2.de");
    }

    @Test
    void testMergeDiscoverySuccessfulFromConnectorResponse() {
        Discovery discovery1 = Discovery.toDiscovery("test.de", "sender.de");
        ConnectorDiscoveryMappingResponse connectorDiscoveryMappingResponse = new ConnectorDiscoveryMappingResponse("bpn", List.of("abc.de", "abc2.de"));
        ConnectorDiscoveryMappingResponse connectorDiscoveryMappingResponse2 = new ConnectorDiscoveryMappingResponse("bpn2", List.of("abc3.de", "abc4.de"));
        Discovery discovery2 = Discovery.toDiscovery(List.of(connectorDiscoveryMappingResponse, connectorDiscoveryMappingResponse2), "bpn", "sender.de");
        Discovery discovery = Discovery.mergeDiscoveries(List.of(discovery2, discovery1));
        assertThat(discovery.getReceiverUrls()).contains("test.de");
        assertThat(discovery.getReceiverUrls()).contains("abc.de");
        assertThat(discovery.getReceiverUrls()).contains("abc2.de");
        assertThat(discovery.getReceiverUrls()).doesNotContain("abc3.de");
        assertThat(discovery.getReceiverUrls()).doesNotContain("abc4.de");
        assertThat(discovery.getSenderUrl()).isEqualTo("sender.de");
    }
}
