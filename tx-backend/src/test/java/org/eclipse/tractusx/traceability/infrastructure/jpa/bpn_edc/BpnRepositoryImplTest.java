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

import org.eclipse.tractusx.traceability.bpn.domain.model.BpnNotFoundException;
import org.eclipse.tractusx.traceability.bpn.domain.service.BpnRepository;
import org.eclipse.tractusx.traceability.bpn.infrastructure.repository.JpaBpnRepository;
import org.eclipse.tractusx.traceability.bpn.infrastructure.repository.BpnRepositoryImpl;
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
class BpnRepositoryImplTest {

    @Mock
    private JpaBpnRepository jpaBpnRepository;

    private BpnRepository bpnRepository;

    @BeforeEach
    void setUp() {
        bpnRepository = new BpnRepositoryImpl(jpaBpnRepository);
    }

    @Test
    void findById_shouldThrowExceptionIfMappingNotFound() {
        String bpn = "123";
        when(jpaBpnRepository.findById(bpn)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bpnRepository.findByIdOrThrowNotFoundException(bpn))
                .isInstanceOf(BpnNotFoundException.class)
                .hasMessage("EDC URL mapping with BPN %s was not found.", bpn);
    }

    @Test
    void exists_shouldReturnFalseIfMappingDoesNotExist() {
        String bpn = "123";
        when(jpaBpnRepository.existsByManufacturerIdAndUrlIsNotNull(bpn)).thenReturn(false);

        boolean result = bpnRepository.existsWhereUrlNotNull(bpn);

        assertThat(result).isFalse();
    }

}
