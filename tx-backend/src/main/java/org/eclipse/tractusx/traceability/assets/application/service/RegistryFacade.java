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

package org.eclipse.tractusx.traceability.assets.application.service;

import org.eclipse.tractusx.traceability.assets.domain.model.ShellDescriptor;
import org.eclipse.tractusx.traceability.assets.domain.service.AssetService;
import org.eclipse.tractusx.traceability.assets.domain.service.ShellDescriptorsService;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.AssetsConverter;
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.registry.RegistryService;
import org.eclipse.tractusx.traceability.assets.infrastructure.config.async.AssetsAsyncConfig;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class RegistryFacade {
	private final ShellDescriptorsService shellDescriptorsService;
	private final RegistryService registryService;
	private final AssetsConverter assetsConverter;
	private final AssetService assetService;

	public RegistryFacade(ShellDescriptorsService shellDescriptorsService, RegistryService registryService, AssetsConverter assetsConverter, AssetService assetService) {
		this.shellDescriptorsService = shellDescriptorsService;
		this.registryService = registryService;
		this.assetsConverter = assetsConverter;
		this.assetService = assetService;
	}

	@Async(value = AssetsAsyncConfig.LOAD_SHELL_DESCRIPTORS_EXECUTOR)
	public void updateShellDescriptorAndSynchronizeAssets() {
		List<ShellDescriptor> ownShellDescriptors = updateOwnShellDescriptors();

		assetService.saveAssets(assetsConverter.convertAssets(ownShellDescriptors));

        synchronizeAssetsByDescriptors(ownShellDescriptors);
    }

    private void synchronizeAssetsByDescriptors(List<ShellDescriptor> descriptors) {
        descriptors.stream()
            .map(ShellDescriptor::globalAssetId)
            .forEach(assetService::synchronizeAssetsAsync);
    }

    private List<ShellDescriptor> updateOwnShellDescriptors() {
        List<ShellDescriptor> ownShellDescriptors = registryService.findOwnShellDescriptors();
        return shellDescriptorsService.update(ownShellDescriptors);
    }
}
