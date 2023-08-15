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

package org.eclipse.tractusx.traceability.assets.infrastructure.repository.rest.registry;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.tractusx.irs.component.assetadministrationshell.AssetAdministrationShellDescriptor;
import org.eclipse.tractusx.irs.component.assetadministrationshell.IdentifierKeyValuePair;
import org.eclipse.tractusx.irs.registryclient.DigitalTwinRegistryKey;
import org.eclipse.tractusx.irs.registryclient.decentral.DecentralDigitalTwinRegistryService;
import org.eclipse.tractusx.irs.registryclient.exceptions.RegistryServiceException;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.model.ShellDescriptor;
import org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.repository.rest.registry.RegistryService;
import org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.repository.rest.registry.shelldescriptor.RegistryShellDescriptor;
import org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.repository.rest.registry.shelldescriptor.RegistryShellDescriptorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistryServiceTest {

    @InjectMocks
    private RegistryService registryService;

    @Mock
    private RegistryShellDescriptorResponse registryShellDescriptorResponse;

    @Mock
    private RegistryShellDescriptor registryShellDescriptor;

    @Mock
    private DecentralDigitalTwinRegistryService decentralDigitalTwinRegistryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String bpn = "test-bpn";
        String manufacturerIdKey = "test-manufacturer-id-key";

        registryService = new RegistryService(bpn, manufacturerIdKey, Clock.systemUTC(), decentralDigitalTwinRegistryService);

    }

    @Test
    void testFindAssets() throws RegistryServiceException {
        // Given
        List<DigitalTwinRegistryKey> registryKeys = List.of(
                new DigitalTwinRegistryKey("assetId", "test-bpn")
        );
        when(decentralDigitalTwinRegistryService.lookupShellIdentifiers("test-bpn")).thenReturn(registryKeys);
        final String globalAssetId = "GLOBAL_ASSET_ID";
        final String idShort = "ID_SHORT";
        final String identification = "IDENTIFICATION";
        final String keyIdentifier = "KEY_IDENTIFIER";
        final String valueIdentifier = "VALUE_IDENTIFIER";
        IdentifierKeyValuePair identifierKeyValuePair = IdentifierKeyValuePair.builder().name(keyIdentifier).value(valueIdentifier).build();
        final AssetAdministrationShellDescriptor assetAdministrationShellDescriptor = AssetAdministrationShellDescriptor.builder()
                .globalAssetId(globalAssetId)
                .idShort(idShort)
                .id(identification)
                .specificAssetIds(List.of(identifierKeyValuePair))
                .build();
        List<AssetAdministrationShellDescriptor> administrationShellDescriptors = List.of(assetAdministrationShellDescriptor);
        when(decentralDigitalTwinRegistryService.fetchShells(registryKeys)).thenReturn(administrationShellDescriptors);

        // When
        List<ShellDescriptor> shellDescriptors = registryService.findOwnShellDescriptors();

        // Then
        assertThat(shellDescriptors).hasSize(1);
    }
}
