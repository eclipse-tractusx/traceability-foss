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
package org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.rest;

import io.github.resilience4j.core.functions.Either;
import org.eclipse.tractusx.irs.component.Shell;
import org.eclipse.tractusx.irs.component.assetadministrationshell.AssetAdministrationShellDescriptor;
import org.eclipse.tractusx.irs.component.assetadministrationshell.Reference;
import org.eclipse.tractusx.irs.component.assetadministrationshell.SemanticId;
import org.eclipse.tractusx.irs.component.assetadministrationshell.SubmodelDescriptor;
import org.eclipse.tractusx.irs.registryclient.decentral.DecentralDigitalTwinRegistryService;
import org.eclipse.tractusx.irs.registryclient.exceptions.RegistryServiceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DecentralRegistryRepositoryImplTest {
    @Mock
    private DecentralDigitalTwinRegistryService decentralDigitalTwinRegistryService;

    @InjectMocks
    private DecentralRegistryRepositoryImpl decentralRegistryRepository;

    @Test
    void testRetrieveShellDescriptorsByBpnSuccess() throws RegistryServiceException {
        // Given
        String bpn = "12345";
        AssetAdministrationShellDescriptor asBuilt = AssetAdministrationShellDescriptor.builder()
                .id("shellId1")
                .globalAssetId("id1")
                .submodelDescriptors(List.of(
                        SubmodelDescriptor.builder()
                                .semanticId(
                                        Reference.builder()
                                                .keys(List.of(
                                                                SemanticId.builder()
                                                                        .type("GlobalReference")
                                                                        .value("urn:samm:io.catenax.single_level_usage_as_built:3.0.0#SerialPart").build()
                                                        )
                                                )
                                                .build()
                                )
                                .build()
                ))
                .build();
        AssetAdministrationShellDescriptor asPlanned = AssetAdministrationShellDescriptor.builder()
                .id("shellId2")
                .globalAssetId("id2")
                .submodelDescriptors(List.of(
                        SubmodelDescriptor.builder()
                                .semanticId(
                                        Reference.builder()
                                                .keys(List.of(
                                                                SemanticId.builder()
                                                                        .type("GlobalReference")
                                                                        .value("urn:samm:io.catenax.single_level_usage_as_built:2.0.0#PartAsPlanned").build()
                                                        )
                                                )
                                                .build()
                                )
                                .build()
                ))
                .build();

        Shell asBuiltShell = Shell.builder()
                .payload(asBuilt)
                .build();

        Shell asPlannedShell = Shell.builder()
                .payload(asPlanned)
                .build();

        List<Either<Exception, Shell>> shells = List.of(Either.right(asBuiltShell), Either.right(asPlannedShell));

        when(decentralDigitalTwinRegistryService.lookupShellsByBPN(bpn)).thenReturn(shells);

        decentralRegistryRepository = new DecentralRegistryRepositoryImpl(decentralDigitalTwinRegistryService);

        // When
        List<Shell> actualDescriptors = decentralRegistryRepository.retrieveShellDescriptorsByBpn(bpn);

        // Then
        assertThat(actualDescriptors).containsExactlyInAnyOrderElementsOf(shells.stream().map(Either::get).toList());
    }

    @Test
    void testRetrieveShellDescriptorsByBpnRegistryServiceException() throws RegistryServiceException {
        // Given
        String bpn = "12345";

        when(decentralDigitalTwinRegistryService.lookupShellsByBPN(bpn)).thenThrow(new RegistryServiceException("Error"));

        decentralRegistryRepository = new DecentralRegistryRepositoryImpl(decentralDigitalTwinRegistryService);

        // When
        List<Shell> actualDescriptors = decentralRegistryRepository.retrieveShellDescriptorsByBpn(bpn);

        // Then
        assertThat(actualDescriptors).isEmpty();
    }
}
