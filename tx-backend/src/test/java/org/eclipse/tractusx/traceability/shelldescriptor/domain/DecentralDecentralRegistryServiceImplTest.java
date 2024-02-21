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
package org.eclipse.tractusx.traceability.shelldescriptor.domain;

import org.eclipse.tractusx.irs.component.Shell;
import org.eclipse.tractusx.irs.component.assetadministrationshell.AssetAdministrationShellDescriptor;
import org.eclipse.tractusx.irs.component.assetadministrationshell.Reference;
import org.eclipse.tractusx.irs.component.assetadministrationshell.SemanticId;
import org.eclipse.tractusx.irs.component.assetadministrationshell.SubmodelDescriptor;
import org.eclipse.tractusx.irs.registryclient.exceptions.RegistryServiceException;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.service.AssetAsBuiltServiceImpl;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.service.AssetAsPlannedServiceImpl;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.repository.DecentralRegistryRepository;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.service.DecentralRegistryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DecentralRegistryServiceImplTest {


    @Mock
    private DecentralRegistryRepository decentralRegistryRepository;

    @Mock
    private TraceabilityProperties traceabilityProperties;
    @Mock
    private AssetAsBuiltServiceImpl assetAsBuiltService;

    @Mock
    private AssetAsPlannedServiceImpl assetAsPlannedService;
    @InjectMocks
    private DecentralRegistryServiceImpl registryFacade;


    @Test
    void testUpdateShellDescriptorAndSynchronizeAssets() throws RegistryServiceException {
        // Given
        AssetAdministrationShellDescriptor asBuilt = AssetAdministrationShellDescriptor.builder()
                .globalAssetId("id1")
                .submodelDescriptors(List.of(
                        SubmodelDescriptor.builder()
                                .semanticId(
                                        Reference.builder()
                                                .keys(List.of(
                                                                SemanticId.builder()
                                                                        .type("GlobalReference")
                                                                        .value("urn:bamm:io.catenax.single_level_usage_as_built:2.0.0#SerialPart").build()
                                                        )
                                                )
                                                .build()
                                )
                                .build()
                ))
                .build();
        AssetAdministrationShellDescriptor asPlanned = AssetAdministrationShellDescriptor.builder()
                .globalAssetId("id2")
                .submodelDescriptors(List.of(
                        SubmodelDescriptor.builder()
                                .semanticId(
                                        Reference.builder()
                                                .keys(List.of(
                                                                SemanticId.builder()
                                                                        .type("GlobalReference")
                                                                        .value("urn:bamm:io.catenax.single_level_usage_as_built:2.0.0#PartAsPlanned").build()
                                                        )
                                                )
                                                .build()
                                )
                                .build()
                ))
                .build();

        Shell asBuiltShell = Shell.builder()
                .payload(asBuilt).build();

        Shell asPlannedShell = Shell.builder()
                .payload(asPlanned).build();
        when(traceabilityProperties.getBpn()).thenReturn(BPN.of("test"));
        when(decentralRegistryRepository.retrieveShellDescriptorsByBpn(BPN.of("test").toString())).thenReturn(List.of(asBuiltShell, asPlannedShell));

        // When
        registryFacade.synchronizeAssets();

        // Then
        verify(assetAsBuiltService, times(1)).synchronizeAssetsAsync(any(String.class));
        verify(assetAsPlannedService, times(1)).synchronizeAssetsAsync(any(String.class));
    }
}
