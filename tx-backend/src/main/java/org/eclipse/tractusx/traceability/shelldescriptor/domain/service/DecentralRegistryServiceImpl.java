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

package org.eclipse.tractusx.traceability.shelldescriptor.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.service.AssetAsBuiltServiceImpl;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.service.AssetAsPlannedServiceImpl;
import org.eclipse.tractusx.traceability.common.config.AssetsAsyncConfig;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.shelldescriptor.application.DecentralRegistryService;
import org.eclipse.tractusx.traceability.shelldescriptor.application.ShellDescriptorService;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.model.ShellDescriptor;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.repository.DecentralRegistryRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class DecentralRegistryServiceImpl implements DecentralRegistryService {

    private final ShellDescriptorService shellDescriptorsService;
    private final AssetAsBuiltServiceImpl assetAsBuiltService;
    private final AssetAsPlannedServiceImpl assetAsPlannedService;
    private final TraceabilityProperties traceabilityProperties;
    private final DecentralRegistryRepository decentralRegistryRepository;

    @Async(value = AssetsAsyncConfig.LOAD_SHELL_DESCRIPTORS_EXECUTOR)
    public void updateShellDescriptorAndSynchronizeAssets() {
        List<ShellDescriptor> shellDescriptorList = decentralRegistryRepository.retrieveShellDescriptorsByBpn(traceabilityProperties.getBpn().toString());

        List<ShellDescriptor> updatedShellDescriptorList = shellDescriptorsService.determineExistingShellDescriptorsAndUpdate(shellDescriptorList);
        updatedShellDescriptorList.stream()
                .map(ShellDescriptor::getGlobalAssetId)
                .forEach(globalAssetId -> {
                    assetAsPlannedService.synchronizeAssetsAsync(globalAssetId);
                    assetAsBuiltService.synchronizeAssetsAsync(globalAssetId);
                });
    }

}

