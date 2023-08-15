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

import org.eclipse.tractusx.irs.registryclient.decentral.DecentralDigitalTwinRegistryService;
import org.eclipse.tractusx.irs.registryclient.exceptions.RegistryServiceException;
import org.eclipse.tractusx.traceability.assets.domain.service.AssetServiceImpl;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.model.ShellDescriptor;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.service.ShellDescriptorsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistryFacadeTest {

    @Mock
    private ShellDescriptorsServiceImpl shellDescriptorsService;

    @Mock
    private DecentralDigitalTwinRegistryService decentralDigitalTwinRegistryService;

    @Mock
    private TraceabilityProperties traceabilityProperties;
    @Mock
    private AssetServiceImpl assetService;

    @InjectMocks
    private RegistryFacade registryFacade;


    @Test
    void testUpdateShellDescriptorAndSynchronizeAssets() throws RegistryServiceException {
        // Given
        List<ShellDescriptor> shellDescriptors = new ArrayList<>();
        ShellDescriptor shellDescritor = ShellDescriptor.builder().globalAssetId("1").build();
        ShellDescriptor shellDescritor2 = ShellDescriptor.builder().globalAssetId("2").build();
        shellDescriptors.add(shellDescritor);
        shellDescriptors.add(shellDescritor2);
        when(traceabilityProperties.getBpn()).thenReturn(BPN.of("test"));
        List<String> globalAssetIds = List.of("1", "2");
        when(decentralDigitalTwinRegistryService.lookupGlobalAssetIds(BPN.of("test").toString())).thenReturn(globalAssetIds);

        // When
        registryFacade.updateShellDescriptorAndSynchronizeAssets();

        // Then
        verify(shellDescriptorsService, times(1)).update(shellDescriptors);
    }
}
