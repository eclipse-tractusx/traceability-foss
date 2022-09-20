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

package net.catenax.traceability

import com.xebialabs.restito.server.StubServer
import groovy.json.JsonBuilder
import net.catenax.traceability.assets.domain.ports.AssetRepository
import net.catenax.traceability.assets.domain.ports.ShellDescriptorRepository
import net.catenax.traceability.common.config.MailboxConfig
import net.catenax.traceability.common.config.OAuth2Config
import net.catenax.traceability.common.config.PostgreSQLConfig
import net.catenax.traceability.common.config.RestitoConfig
import net.catenax.traceability.common.config.SecurityTestConfig
import net.catenax.traceability.common.support.AssetRepositoryProvider
import net.catenax.traceability.common.support.KeycloakApiSupport
import net.catenax.traceability.common.support.KeycloakSupport
import net.catenax.traceability.common.support.ShellDescriptorStoreProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.spock.Testcontainers
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

@AutoConfigureMockMvc
@ActiveProfiles(profiles = ["integration"])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
	classes = [SecurityTestConfig.class, MailboxConfig.class, RestitoConfig.class, OAuth2Config.class, PostgreSQLConfig.class],
	initializers = [RestitoConfig.Initializer.class, PostgreSQLConfig.Initializer.class]
)
@Testcontainers
abstract class IntegrationSpec extends Specification implements KeycloakSupport, KeycloakApiSupport, AssetRepositoryProvider, ShellDescriptorStoreProvider {

	@Autowired
	protected MockMvc mvc

	@Autowired
	private AssetRepository assetRepository

	@Autowired
	private ShellDescriptorRepository shellDescriptorRepository

	def cleanup() {
		RestitoConfig.clear()
		clearAuthentication()
		assetRepository.clean()
		shellDescriptorRepository.clean()
	}

	@Override
	StubServer stubServer() {
		return RestitoConfig.getStubServer()
	}

	@Override
	AssetRepository assetRepository() {
		return assetRepository
	}

	@Override
	ShellDescriptorRepository shellDescriptorRepository() {
		return shellDescriptorRepository
	}

	protected void eventually(Closure<?> conditions) {
		new PollingConditions(timeout: 15, initialDelay: 0.5).eventually(conditions)
	}

	protected String asJson(Map map) {
		return new JsonBuilder(map).toPrettyString()
	}
}
