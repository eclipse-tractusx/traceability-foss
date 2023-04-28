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

package org.eclipse.tractusx.traceability.infrastructure.jpa.bpn_edc;

import org.eclipse.tractusx.traceability.bpn.mapping.domain.model.BpnEdcMappingNotFoundException;
import org.eclipse.tractusx.traceability.bpn.mapping.domain.ports.BpnEdcMappingRepository;
import org.eclipse.tractusx.traceability.bpn.mapping.infrastructure.adapters.jpa.BpnEdcMappingEntity;
import org.eclipse.tractusx.traceability.bpn.mapping.infrastructure.adapters.jpa.JpaBpnEdcRepository;
import org.eclipse.tractusx.traceability.bpn.mapping.infrastructure.adapters.jpa.PersistentBpnEdcMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersistentBpnEdcMappingRepositoryTest {

    @Mock
    private JpaBpnEdcRepository jpaBpnEdcRepository;

    private BpnEdcMappingRepository mappingRepository;

    @BeforeEach
    void setUp() {
        mappingRepository = new PersistentBpnEdcMappingRepository(jpaBpnEdcRepository);
    }

    @Test
    void findById_shouldThrowExceptionIfMappingNotFound() {
        String bpn = "123";
        when(jpaBpnEdcRepository.findById(bpn)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mappingRepository.findById(bpn))
                .isInstanceOf(BpnEdcMappingNotFoundException.class)
                .hasMessage("EDC URL mapping with BPN %s was not found.", bpn);
    }

    @Test
    void exists_shouldReturnTrueIfMappingExists() {
        String bpn = "123";
        when(jpaBpnEdcRepository.findById(bpn)).thenReturn(Optional.of(new BpnEdcMappingEntity(bpn, "http://example.com")));

        boolean result = mappingRepository.exists(bpn);

        assertThat(result).isTrue();
    }

    @Test
    void exists_shouldReturnFalseIfMappingDoesNotExist() {
        String bpn = "123";
        when(jpaBpnEdcRepository.findById(bpn)).thenReturn(Optional.empty());

        boolean result = mappingRepository.exists(bpn);

        assertThat(result).isFalse();
    }

}
