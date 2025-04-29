/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.aas.domain.model;

import org.eclipse.tractusx.traceability.aas.infrastructure.model.DigitalTwinType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import org.eclipse.tractusx.traceability.common.model.BPN;

class AASTest {

    /**
     * Tests for the static method {@code actorFromString} in the {@code AAS} class.
     * This method converts a {@code String} representation of an {@code Actor} into the corresponding enum constant.
     * If the input is null, the method returns null.
     */

    @Test
    void testDigitalTwinTypeFromStringWithValidPartType() {
        // Given
        String input = "PART_TYPE";

        // When
        DigitalTwinType result = AAS.digitalTwinTypeFromString(input);

        // Then
        assertNotNull(result);
        assertEquals(DigitalTwinType.PART_TYPE, result);
    }

    @Test
    void testDigitalTwinTypeFromStringWithValidPartInstance() {
        // Given
        String input = "PART_INSTANCE";

        // When
        DigitalTwinType result = AAS.digitalTwinTypeFromString(input);

        // Then
        assertNotNull(result);
        assertEquals(DigitalTwinType.PART_INSTANCE, result);
    }

    @Test

    void testDigitalTwinTypeFromStringWithInvalidType() {
        // Given
        String input = "INVALID";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            AAS.digitalTwinTypeFromString(input);
        });
    }

    @Test

    void testDigitalTwinTypeFromStringWithNull() {
        // Given
        String input = null;

        // When
        DigitalTwinType result = AAS.digitalTwinTypeFromString(input);

        // Then
        assertNull(result);
    }
    @Test
    void testBpnFromStringWithValidBpn() {
        // Given
        String input = "BPN123456";

        // When
        BPN result = AAS.bpnFromString(input);

        // Then
        assertNotNull(result);
        assertEquals("BPN123456", result.value());
    }

    @Test
    void testBpnFromStringWithNull() {
        // Given
        String input = null;

        // When
        BPN result = AAS.bpnFromString(input);

        // Then
        assertNull(result);
    }

    @Test
    void testBpnFromStringWithInvalidBpn() {
        // Given
        String input = "";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> AAS.bpnFromString(input));
    }
    @Test
    void testActorFromStringWithValidSystem() {
        // Given
        String input = "SYSTEM";

        // When
        Actor result = AAS.actorFromString(input);

        // Then
        assertNotNull(result);
        assertEquals(Actor.SYSTEM, result);
    }

    @Test
    void testActorFromStringWithValidAdmin() {
        // Given
        String input = "ADMIN";

        // When
        Actor result = AAS.actorFromString(input);

        // Then
        assertNotNull(result);
        assertEquals(Actor.ADMIN, result);
    }

    @Test
    void testActorFromStringWithInvalidActor() {
        // Given
        String input = "INVALID";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> AAS.actorFromString(input));
    }

    @Test
    void testActorFromStringWithNull() {
        // Given
        String input = null;

        // When
        Actor result = AAS.actorFromString(input);

        // Then
        assertNull(result);
    }
}
