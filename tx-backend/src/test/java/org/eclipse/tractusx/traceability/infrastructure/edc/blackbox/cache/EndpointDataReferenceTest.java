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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.cache;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EndpointDataReferenceTest {

    private static final String endpoint = "endpoint";
    private static final String id = "id";
    private static final String authkey = "authkey";
    private static final String authcode = "authcode";
    private static final Map<String, String> properties = new HashMap<>();
    private EndpointDataReference endpointDataReference;

    @BeforeEach
    void setUp() {
        properties.put("key0", "value0");
        endpointDataReference = EndpointDataReference.Builder.newInstance()
            .id(id)
            .endpoint(endpoint)
            .authKey(authkey)
            .authCode(authcode)
            .properties(properties)
            .build();
    }

    @Test
    void getId() {
        assertEquals(id, endpointDataReference.getId());
    }

    @Test
    void getEndpoint() {
        assertEquals(endpoint, endpointDataReference.getEndpoint());
    }

    @Test
    void getAuthKey() {
        assertEquals(authkey, endpointDataReference.getAuthKey());
    }

    @Test
    void getAuthCode() {
        assertEquals(authcode, endpointDataReference.getAuthCode());
    }

    @Test
    void getProperties() {
        assertEquals(properties.keySet().size(), endpointDataReference.getProperties().keySet().size());
    }

}
