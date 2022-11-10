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

package net.catenax.traceability.assets

import net.catenax.traceability.assets.application.AssetFacade
import net.catenax.traceability.assets.application.RegistryFacade
import net.catenax.traceability.assets.domain.ports.AssetRepository
import net.catenax.traceability.assets.domain.ports.IrsRepository
import net.catenax.traceability.assets.domain.ports.ShellDescriptorRepository
import net.catenax.traceability.assets.domain.service.AssetService
import net.catenax.traceability.assets.domain.service.ShellDescriptorsService
import net.catenax.traceability.assets.infrastructure.adapters.openapi.registry.RegistryService
import spock.lang.Specification

abstract class AssetsSpec extends Specification {

	AssetFacade assetFacade
	RegistryFacade registryFacade
	RegistryService registryService
	AssetRepository assetRepository
	IrsRepository irsRepository
	ShellDescriptorRepository shellDescriptorsRepository

	def setup() {
		registryService = Mock(RegistryService)
		assetRepository = Mock(AssetRepository)
		irsRepository = Mock(IrsRepository)
		def assetService = new AssetService(assetRepository, irsRepository)
		assetFacade = new AssetFacade(assetService)

		shellDescriptorsRepository = Mock(ShellDescriptorRepository)
		registryFacade = new RegistryFacade(new ShellDescriptorsService(shellDescriptorsRepository), registryService, assetService)
	}
}
