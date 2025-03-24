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
package org.eclipse.tractusx.traceability.aas.infrastructure.repository;

import org.eclipse.tractusx.traceability.aas.domain.model.AAS;
import org.eclipse.tractusx.traceability.aas.domain.model.Actor;
import org.eclipse.tractusx.traceability.aas.infrastructure.model.AASEntity;
import org.eclipse.tractusx.traceability.aas.infrastructure.model.DigitalTwinType;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AASRepositoryImplTest {
    @Mock
    private JpaAASRepository jpaAASRepository;

    private AASRepositoryImpl aasRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        aasRepository = new AASRepositoryImpl(jpaAASRepository);
    }

    @Test
    void testFindExistingAasList() {
        // Arrange
        List<String> aasIds = List.of("aas1", "aas2");
        List<AASEntity> mockEntities = List.of(
                AASEntity.builder()
                        .aasId("aas1")
                        .ttl(30)
                        .created(LocalDateTime.now().minusDays(1))
                        .updated(LocalDateTime.now())
                        .expiryDate(LocalDateTime.now().plusDays(30))
                        .actor("SYSTEM")
                        .digitalTwinType("PART_TYPE")
                        .bpn("BPN_001")
                        .build(),
                AASEntity.builder()
                        .aasId("aas2")
                        .ttl(60)
                        .created(LocalDateTime.now().minusDays(2))
                        .updated(LocalDateTime.now())
                        .expiryDate(LocalDateTime.now().plusDays(60))
                        .actor("SYSTEM")
                        .digitalTwinType("PART_TYPE")
                        .bpn("BPN_002")
                        .build()
        );
        when(jpaAASRepository.findExistingAasList(aasIds)).thenReturn(mockEntities);

        // Act
        List<AAS> result = aasRepository.findExistingAasList(aasIds);

        // Assert
        assertEquals(2, result.size());
        assertEquals("aas1", result.get(0).getAasId());
        assertEquals("aas2", result.get(1).getAasId());
        verify(jpaAASRepository).findExistingAasList(aasIds);
    }

    @Test
    void testSave() {
        // Arrange
        List<AAS> aasList = List.of(
                AAS.builder()
                        .aasId("aas1")
                        .ttl(30)
                        .created(LocalDateTime.now().minusDays(1))
                        .updated(LocalDateTime.now())
                        .expiryDate(LocalDateTime.now().plusDays(30))
                        .actor(Actor.SYSTEM)
                        .digitalTwinType(DigitalTwinType.PART_TYPE)
                        .bpn(BPN.of("ABC"))
                        .build());
        List<AASEntity> aasEntities = AASEntity.fromList(aasList);

        // Act
        aasRepository.save(aasList);

        // Assert
        verify(jpaAASRepository).saveAll(aasEntities);
    }

    @Test
    void testCleanExpiredEntries_WithExpiredEntries() {
        // Arrange
        List<AASEntity> expiredEntries = List.of(
                AASEntity.builder()
                        .aasId("expired1")
                        .ttl(3600)
                        .created(LocalDateTime.now().minusDays(10))
                        .updated(LocalDateTime.now().minusDays(5))
                        .expiryDate(LocalDateTime.now().minusDays(1))
                        .actor("sampleActor")
                        .digitalTwinType("sampleType")
                        .bpn("BPN123456789")
                        .build()
        );
        when(jpaAASRepository.findExpiredEntries()).thenReturn(expiredEntries);

        // Act
        aasRepository.cleanExpiredEntries();

        // Assert
        verify(jpaAASRepository).deleteAll(expiredEntries);
    }

    @Test
    void testCleanExpiredEntries_NoExpiredEntries() {
        // Arrange
        when(jpaAASRepository.findExpiredEntries()).thenReturn(List.of());

        // Act
        aasRepository.cleanExpiredEntries();

        // Assert
        verify(jpaAASRepository, never()).deleteAll(any());
    }
}
