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

package org.eclipse.tractusx.traceability.shelldescriptor.domain;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.irs.registryclient.exceptions.RegistryServiceException;
import org.eclipse.tractusx.traceability.assets.domain.service.AssetServiceImpl;
import org.eclipse.tractusx.traceability.common.config.AssetsAsyncConfig;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.model.ShellDescriptor;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.service.ShellDescriptorsService;
import org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.repository.rest.registry.RegistryService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class RegistryFacade {
    private final ShellDescriptorsService shellDescriptorsService;
    private final RegistryService registryService;
    private final AssetServiceImpl assetService;

    @Async(value = AssetsAsyncConfig.LOAD_SHELL_DESCRIPTORS_EXECUTOR)
    public void updateShellDescriptorAndSynchronizeAssets() throws RegistryServiceException {
        List<ShellDescriptor> ownShellDescriptors = updateOwnShellDescriptors();
        synchronizeAssetsByDescriptors(ownShellDescriptors);
    }

    private void synchronizeAssetsByDescriptors(List<ShellDescriptor> descriptors) {
        descriptors
                .forEach(descriptor ->
                        assetService.synchronizeAssetsAsync(
                                descriptor.getGlobalAssetId(),
                                descriptor.getManufacturerId()));
    }

    private List<ShellDescriptor> updateOwnShellDescriptors() throws RegistryServiceException {
        List<ShellDescriptor> ownShellDescriptors = registryService.findOwnShellDescriptors();
        return shellDescriptorsService.update(ownShellDescriptors);
    }

}
