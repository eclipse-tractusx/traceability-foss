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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.transfer;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DataFlowRequestTest {

    private static final String processId = "processId";
    private static final boolean trackable = false;
    private static final String id = "id";
    private static final String type = "type";

    private Map<String, String> properties;
    private Map<String, String> traceContext;
    private DataAddress sourceDataAddress;
    private DataAddress destinationDataAddress;
    private DataFlowRequest request;

    @BeforeEach
    void setUp() {
        sourceDataAddress = DataAddress.Builder.newInstance()
            .type(type)
            .build();
        destinationDataAddress = DataAddress.Builder.newInstance()
            .type(type)
            .build();
        properties = new HashMap<>();
        properties.put(id, processId);
        traceContext = new HashMap<>();
        traceContext.put(id, processId);
        request = DataFlowRequest.Builder.newInstance()
            .processId(processId)
            .sourceDataAddress(sourceDataAddress)
            .destinationDataAddress(destinationDataAddress)
            .trackable(trackable)
            .id(id)
            .properties(properties)
            .traceContext(traceContext)
            .build();
    }

    @Test
    void getId() {
        assertEquals(id, request.getId());
    }

    @Test
    void getProcessId() {
        assertEquals(processId, request.getProcessId());
    }

    @Test
    void getSourceDataAddress() {
        assertEquals(sourceDataAddress, request.getSourceDataAddress());
    }

    @Test
    void getDestinationDataAddress() {
        assertEquals(destinationDataAddress, request.getDestinationDataAddress());
    }

    @Test
    void isTrackable() {
        assertFalse(request.isTrackable());
    }

    @Test
    void getProperties() {
        assertEquals(properties.keySet().size(), request.getProperties().keySet().size());
        assertTrue(processId.equalsIgnoreCase(request.getProperties().get(id)));
    }

    @Test
    void getTraceContext() {
        assertTrue(traceContext.keySet().size() == request.getTraceContext().keySet().size()
            && processId.equalsIgnoreCase(request.getTraceContext().get(id)));
    }

}
