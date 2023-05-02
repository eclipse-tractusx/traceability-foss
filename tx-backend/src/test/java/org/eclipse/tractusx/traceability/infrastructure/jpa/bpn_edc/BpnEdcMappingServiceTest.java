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
import org.eclipse.tractusx.traceability.bpn.mapping.domain.service.BpnEdcMappingService;
import org.eclipse.tractusx.traceability.bpn.mapping.infrastructure.adapters.rest.BpnEdcMappingRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BpnEdcMappingServiceTest {

    @Mock
    private BpnEdcMappingRepository bpnEdcMappingRepositoryMock;

    @InjectMocks
    private BpnEdcMappingService bpnEdcMappingService;


    @Test
    @DisplayName("Test getBpnEdcMappings")
    void testGetBpnEdcMappings() {
        bpnEdcMappingService.findAllBpnEdcMappings();
        verify(bpnEdcMappingRepositoryMock, times(1)).findAll();
    }

    @Test
    @DisplayName("Test createBpnEdcMapping")
    void testCreateBpnEdcMapping() {
        String bpn = "12345";
        String url = "https://example.com/12345";
        List<BpnEdcMappingRequest> bpnEdcMappingRequests = List.of(new BpnEdcMappingRequest(bpn, url));
        bpnEdcMappingService.saveAllBpnEdcMappings(bpnEdcMappingRequests);
        verify(bpnEdcMappingRepositoryMock, times(1)).saveAll(bpnEdcMappingRequests);
    }


    @Test
    @DisplayName("Test updateEdcMapping")
    void testUpdateBpnEdcMapping() {
        String bpn = "12345";
        String url = "https://example.com/12345";
        List<BpnEdcMappingRequest> bpnEdcMappingRequests = List.of(new BpnEdcMappingRequest(bpn, url));
        bpnEdcMappingService.updateAllBpnEdcMappings(bpnEdcMappingRequests);
        verify(bpnEdcMappingRepositoryMock, times(1)).saveAll(bpnEdcMappingRequests);
    }

    @Test
    @DisplayName("Test deleteBpnEdcMapping")
    void testDeleteBpnEdcMapping() {
        String bpn = "12345";
        when(bpnEdcMappingRepositoryMock.exists(bpn)).thenReturn(true);
        bpnEdcMappingService.deleteBpnEdcMapping(bpn);
        verify(bpnEdcMappingRepositoryMock, times(1)).deleteById(bpn);
    }

    @Test
    @DisplayName("Test deleteBpnEdcMapping with missing mapping")
    void testDeleteBpnEdcMappingWithMissingMapping() {
        String bpn = "12345";
        when(bpnEdcMappingRepositoryMock.exists(bpn)).thenReturn(false);
        Assertions.assertThrows(BpnEdcMappingNotFoundException.class, () -> {
            bpnEdcMappingService.deleteBpnEdcMapping(bpn);
        });
        verify(bpnEdcMappingRepositoryMock, never()).deleteById(bpn);
    }
}

