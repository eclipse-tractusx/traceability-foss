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
package org.eclipse.tractusx.traceability.aas.infrastructure.model;

import org.eclipse.tractusx.traceability.aas.domain.model.AAS;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class AASEntityTest {

    @Test
    void testToDomain_withValidEntity_shouldReturnAAS() {
        // Arrange
        AASEntity aasEntity = AASEntity.builder()
                .aasId("test-id")
                .ttl(3600)
                .created(LocalDateTime.of(2023, 10, 10, 10, 10))
                .updated(LocalDateTime.of(2023, 10, 11, 10, 10))
                .expiryDate(LocalDateTime.of(2023, 10, 12, 10, 10))
                .actor("SYSTEM")
                .digitalTwinType("PART_TYPE")
                .bpn("ABC")
                .build();

        // Act
        AAS result = AASEntity.toDomain(aasEntity);

        // Assert
        assertNotNull(result);
        assertEquals(aasEntity.getAasId(), result.getAasId());
        assertEquals(aasEntity.getTtl(), result.getTtl());
        assertEquals(aasEntity.getCreated(), result.getCreated());
        assertEquals(aasEntity.getUpdated(), result.getUpdated());
        assertEquals(aasEntity.getExpiryDate(), result.getExpiryDate());
        assertEquals("SYSTEM", result.getActor().name());
        assertEquals("PART_TYPE", result.getDigitalTwinType().name());
        assertEquals("ABC", result.getBpn().toString());
    }

    @Test
    void testToDomain_withNullFields_shouldHandleGracefully() {
        // Arrange
        AASEntity aasEntity = AASEntity.builder()
                .aasId(null)
                .ttl(null)
                .created(null)
                .updated(null)
                .digitalTwinType(null)
                .actor(null)
                .expiryDate(null)
                .bpn(null)
                .build();

        // Act
        AAS result = AASEntity.toDomain(aasEntity);

        // Assert
        assertNotNull(result);
        assertNull(result.getAasId());
        assertNull(result.getTtl());
        assertNull(result.getCreated());
        assertNull(result.getUpdated());
        assertNull(result.getExpiryDate());
        assertNull(result.getBpn());
    }
}
