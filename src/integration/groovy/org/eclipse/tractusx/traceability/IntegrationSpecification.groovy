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

package org.eclipse.tractusx.traceability

import com.xebialabs.restito.server.StubServer
import groovy.json.JsonBuilder
import org.eclipse.tractusx.traceability.common.config.MailboxConfig
import org.eclipse.tractusx.traceability.common.config.PostgreSQLConfig
import org.eclipse.tractusx.traceability.common.config.RestAssuredConfig
import org.eclipse.tractusx.traceability.common.config.RestitoConfig
import org.eclipse.tractusx.traceability.common.support.AssetRepositoryProvider
import org.eclipse.tractusx.traceability.common.support.BpnRepositoryProvider
import org.eclipse.tractusx.traceability.common.support.DatabaseSupport
import org.eclipse.tractusx.traceability.common.support.InvestigationsRepositoryProvider
import org.eclipse.tractusx.traceability.common.support.NotificationsRepositoryProvider
import org.eclipse.tractusx.traceability.common.support.OAuth2ApiSupport
import org.eclipse.tractusx.traceability.common.support.OAuth2Support
import org.eclipse.tractusx.traceability.common.support.ShellDescriptorStoreProvider
import org.eclipse.tractusx.traceability.infrastructure.jpa.investigation.JpaInvestigationRepository
import org.eclipse.tractusx.traceability.infrastructure.jpa.notification.JpaNotificationRepository
import org.eclipse.tractusx.traceability.assets.domain.ports.AssetRepository
import org.eclipse.tractusx.traceability.assets.domain.ports.BpnRepository
import org.eclipse.tractusx.traceability.assets.domain.ports.ShellDescriptorRepository
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.AssetsConverter
import org.eclipse.tractusx.traceability.common.config.ApplicationProfiles
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.spock.Testcontainers
import spock.lang.Specification
import spock.util.concurrent.PollingConditions

@ActiveProfiles(profiles = [ApplicationProfiles.TESTS])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(
	classes = [RestAssuredConfig.class, MailboxConfig.class, RestitoConfig.class, PostgreSQLConfig.class],
	initializers = [RestitoConfig.Initializer.class, PostgreSQLConfig.Initializer.class]
)
@Testcontainers
abstract class IntegrationSpecification extends Specification
	implements OAuth2Support, OAuth2ApiSupport, DatabaseSupport, AssetRepositoryProvider, ShellDescriptorStoreProvider,
		BpnRepositoryProvider, InvestigationsRepositoryProvider, NotificationsRepositoryProvider {

	@Autowired
	private AssetRepository assetRepository

	@Autowired
	private AssetsConverter assetsConverter

	@Autowired
	private ShellDescriptorRepository shellDescriptorRepository

	@Autowired
	private BpnRepository bpnRepository

	@Autowired
	private JpaInvestigationRepository jpaInvestigationRepository

	@Autowired
	private JpaNotificationRepository jpaNotificationRepository

	@Autowired
	private JdbcTemplate jdbcTemplate

	def setup() {
		oauth2ApiReturnsJwkCerts(jwk())
	}

	def cleanup() {
		RestitoConfig.clear()
		clearOAuth2Client()
		clearAllTables()
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
	AssetsConverter assetsConverter() {
		return assetsConverter
	}

	@Override
	ShellDescriptorRepository shellDescriptorRepository() {
		return shellDescriptorRepository
	}

	@Override
	BpnRepository bpnRepository() {
		return bpnRepository
	}

	@Override
	JpaInvestigationRepository jpaInvestigationRepository() {
		return jpaInvestigationRepository
	}

	@Override
	JpaNotificationRepository jpaNotificationRepository() {
		return jpaNotificationRepository
	}

	@Override
	JdbcTemplate jdbcTemplate() {
		return jdbcTemplate
	}

	protected void eventually(Closure<?> conditions) {
		new PollingConditions(timeout: 15, initialDelay: 0.5).eventually(conditions)
	}

	protected String asJson(Map map) {
		return new JsonBuilder(map).toPrettyString()
	}
}
