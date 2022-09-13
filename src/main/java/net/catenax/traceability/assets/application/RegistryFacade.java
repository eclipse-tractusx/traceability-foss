/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

package net.catenax.traceability.assets.application;

import net.catenax.traceability.assets.domain.model.ShellDescriptor;
import net.catenax.traceability.assets.domain.service.AssetService;
import net.catenax.traceability.assets.domain.service.ShellDescriptorsService;
import net.catenax.traceability.assets.infrastructure.adapters.openapi.registry.RegistryService;
import net.catenax.traceability.assets.infrastructure.config.async.AssetsAsyncConfig;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RegistryFacade {

	private final ShellDescriptorsService shellDescriptiorsService;
	private final RegistryService registryService;
	private final AssetService assetService;

	public RegistryFacade(ShellDescriptorsService shellDescriptiorsService, RegistryService registryService, AssetService assetService) {
		this.shellDescriptiorsService = shellDescriptiorsService;
		this.registryService = registryService;
		this.assetService = assetService;
	}

	public List<ShellDescriptor> updateShellDescriptors() {
		List<ShellDescriptor> descriptors = registryService.findAssets();
		return shellDescriptiorsService.update(descriptors);
	}

	@Async(value = AssetsAsyncConfig.LOAD_SHELL_DESCRIPTORS_EXECUTOR)
	public void loadShellDescriptors() {
		List<ShellDescriptor> descriptors = updateShellDescriptors();
		List<String> globalAssetIds = descriptors.stream()
			.map(ShellDescriptor::globalAssetId)
			.toList();

		// add list
		assetService.synchronizeAssets(globalAssetIds);
	}
}
