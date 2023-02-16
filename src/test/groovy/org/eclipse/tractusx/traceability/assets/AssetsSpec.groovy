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

package org.eclipse.tractusx.traceability.assets


import org.eclipse.tractusx.traceability.assets.application.AssetFacade
import org.eclipse.tractusx.traceability.assets.application.RegistryFacade
import org.eclipse.tractusx.traceability.assets.domain.ports.AssetRepository
import org.eclipse.tractusx.traceability.assets.domain.ports.BpnRepository
import org.eclipse.tractusx.traceability.assets.domain.ports.IrsRepository
import org.eclipse.tractusx.traceability.assets.domain.ports.ShellDescriptorRepository
import org.eclipse.tractusx.traceability.assets.domain.service.AssetService
import org.eclipse.tractusx.traceability.assets.domain.service.ShellDescriptorsService
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.AssetsConverter
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.registry.RegistryService
import spock.lang.Specification

abstract class AssetsSpec extends Specification {

	AssetFacade assetFacade
	RegistryFacade registryFacade
	RegistryService registryService
	AssetRepository assetRepository
	IrsRepository irsRepository
	ShellDescriptorRepository shellDescriptorsRepository
	AssetsConverter assetsConverter

	def setup() {
		registryService = Mock(RegistryService)
		assetRepository = Mock(AssetRepository)
		irsRepository = Mock(IrsRepository)
		def assetService = new AssetService(assetRepository, irsRepository)
		assetFacade = new AssetFacade(assetService)
		assetsConverter = new AssetsConverter(Mock(BpnRepository))

		shellDescriptorsRepository = Mock(ShellDescriptorRepository)
		registryFacade = new RegistryFacade(new ShellDescriptorsService(shellDescriptorsRepository), registryService, assetsConverter, assetService)
	}
}
