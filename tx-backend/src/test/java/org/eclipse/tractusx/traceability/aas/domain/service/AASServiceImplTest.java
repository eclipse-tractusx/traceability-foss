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
package org.eclipse.tractusx.traceability.aas.domain.service;

import org.eclipse.tractusx.traceability.aas.domain.model.AAS;
import org.eclipse.tractusx.traceability.aas.domain.model.Actor;
import org.eclipse.tractusx.traceability.aas.domain.model.DTR;
import org.eclipse.tractusx.traceability.aas.domain.repository.AASRepository;
import org.eclipse.tractusx.traceability.aas.infrastructure.model.DigitalTwinType;
import org.eclipse.tractusx.traceability.common.properties.AASProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AASServiceImplTest {

    @Mock
    private AASRepository aasRepository;
    @Mock
    private AASProperties aasProperties;
    @InjectMocks
    private AASServiceImpl aasService;

    @Test
    void upsertAASList_withOnlyExistingEntries_shouldOnlyUpdate() {
        // Arrange
        when(aasProperties.getTtl()).thenReturn(3600);

        DTR dtr = DTR.builder()
                .aasIds(List.of("existingAAS"))
                .digitalTwinType(DigitalTwinType.PART_TYPE)
                .bpn("BPN123")
                .limit(5)
                .nextCursor(null)
                .build();
        AAS existingAAS = AAS.builder().aasId("existingAAS").updated(LocalDateTime.now()).build();

        when(aasRepository.findExistingAasList(anyList())).thenReturn(List.of(existingAAS));

        // Act
        aasService.upsertAASList(dtr);

        // Assert
        verify(aasRepository).save(anyList());
        verify(aasRepository, never()).save(Collections.emptyList());
    }

    @Test
    void upsertAASList_withOnlyNewEntries_shouldOnlyInsert() {
        // Arrange
        when(aasProperties.getTtl()).thenReturn(3600);

        DTR dtr = DTR.builder()
                .aasIds(List.of("newAAS"))
                .digitalTwinType(DigitalTwinType.PART_TYPE)
                .bpn("BPN123")
                .limit(5)
                .nextCursor(null)
                .build();
        when(aasRepository.findExistingAasList(anyList())).thenReturn(Collections.emptyList());

        // Act
        aasService.upsertAASList(dtr);

        // Assert
        verify(aasRepository, never()).save(Collections.emptyList());
        verify(aasRepository).save(anyList());
    }

    @Test
    void upsertAASList_withNoEntries_shouldDoNothing() {
        // Arrange
        DTR dtr = DTR.builder()
                .aasIds(Collections.emptyList())
                .digitalTwinType(DigitalTwinType.PART_TYPE)
                .bpn("BPN123")
                .limit(5)
                .nextCursor(null)
                .build();
        // Act
        aasService.upsertAASList(dtr);

        // Assert
        verify(aasRepository, never()).save(anyList());
    }

    @Test
    void cleanExpiredAASEntries_shouldInvokeRepositoryMethod() {
        // Act
        aasService.cleanExpiredAASEntries();

        // Assert
        verify(aasRepository).cleanExpiredEntries();
    }
}
