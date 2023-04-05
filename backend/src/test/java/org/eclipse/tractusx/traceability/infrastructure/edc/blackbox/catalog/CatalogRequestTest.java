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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CatalogRequestTest {

    private static final String protocol = "protocol";
    private static final String connectorId = "connectorId";
    private static final String connectorAddress = "connectorAddress";

    private CatalogRequest catalogRequest;

    @BeforeEach
    void setUp() {
        catalogRequest = CatalogRequest.Builder.newInstance()
                .protocol(protocol)
                .connectorId(connectorId)
                .connectorAddress(connectorAddress)
                .build();
    }

    @Test
    void getProtocol() {
        assertEquals(protocol, catalogRequest.getProtocol());
    }

    @Test
    void getConnectorId() {
        assertEquals(connectorId, catalogRequest.getConnectorId());
    }

    @Test
    void getConnectorAddress() {
        assertEquals(connectorAddress, catalogRequest.getConnectorAddress());
    }
}
