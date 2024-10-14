/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.submodel.infrastructure.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class SubmodelUtilTest {

    private ObjectMapper objectMapper;
    private final String jsonString = """
            {
                "aspectType": "urn:samm:io.catenax.serial_part:3.0.0#SerialPart",
                "payload": {
                    "localIdentifiers": [
                        {
                            "value": "BPNL00000003CML1",
                            "key": "manufacturerId"
                        }
                    ]
                }
            }""";

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper(); // You can mock it if you prefer
    }

    @Test
    public void testEnrichWithSubmodelUuid() throws Exception {
        // Call the method to enrich with submodel UUID
        String enrichedJson = SubmodelUtil.enrichWithSubmodelUuid(objectMapper, jsonString);

        // Check if the resulting JSON contains the submodelId
        JsonNode rootNode = objectMapper.readTree(enrichedJson);
        assertTrue(rootNode.has("submodelId"), "The enriched JSON should contain the submodelId");

        // Validate the submodelId format (UUID)
        String submodelId = rootNode.get("submodelId").asText();
        assertDoesNotThrow(() -> UUID.fromString(submodelId), "The submodelId should be a valid UUID");
    }

    @Test
    public void testGetSubmodelId() throws Exception {
        // Create a JSON string with a submodelId
        String enrichedJson = """
                {
                    "aspectType": "urn:samm:io.catenax.serial_part:3.0.0#SerialPart",
                    "submodelId": "6b2296cc-26c0-4f38-8a22-092338c36b92",
                    "payload": {
                        "localIdentifiers": [
                            {
                                "value": "BPNL00000003CML1",
                                "key": "manufacturerId"
                            }
                        ]
                    }
                }""";

        // Call the method to extract the submodelId
        String submodelId = SubmodelUtil.getSubmodelId(objectMapper, enrichedJson);

        // Verify that the submodelId matches the expected value
        assertEquals("6b2296cc-26c0-4f38-8a22-092338c36b92", submodelId, "The submodelId should match the expected value");
    }

    @Test
    public void testGetSubmodelIdWhenNotPresent() throws Exception {
        // Call the method on JSON without submodelId
        String submodelId = SubmodelUtil.getSubmodelId(objectMapper, jsonString);

        // Verify that submodelId is null when not present
        assertNull(submodelId, "The submodelId should be null when not present in the JSON");
    }
}

