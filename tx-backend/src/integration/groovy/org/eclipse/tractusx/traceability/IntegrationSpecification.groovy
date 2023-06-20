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
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.AssetAsBuiltRepository
import org.eclipse.tractusx.traceability.assets.domain.asplanned.AssetAsPlannedRepository
import org.eclipse.tractusx.traceability.assets.domain.base.BpnRepository
import org.eclipse.tractusx.traceability.bpn.mapping.domain.ports.BpnEdcMappingRepository
import org.eclipse.tractusx.traceability.common.config.ApplicationProfiles
import org.eclipse.tractusx.traceability.common.config.PostgreSQLConfig
import org.eclipse.tractusx.traceability.common.config.RestAssuredConfig
import org.eclipse.tractusx.traceability.common.config.RestitoConfig
import org.eclipse.tractusx.traceability.common.support.*
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.repository.JpaAlertNotificationRepository
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.repository.JpaAlertRepository
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.repository.JpaInvestigationNotificationRepository
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.repository.JpaInvestigationRepository
import org.eclipse.tractusx.traceability.shelldescriptor.domain.repository.ShellDescriptorRepository
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
        classes = [RestAssuredConfig.class, RestitoConfig.class, PostgreSQLConfig.class],
        initializers = [RestitoConfig.Initializer.class, PostgreSQLConfig.Initializer.class]
)
@Testcontainers
abstract class IntegrationSpecification extends Specification
        implements OAuth2Support, OAuth2ApiSupport, DatabaseSupport, AssetRepositoryProvider, ShellDescriptorStoreProvider,
                BpnRepositoryProvider, InvestigationsRepositoryProvider, AlertsRepositoryProvider, InvestigationNotificationRepositoryProvider, AlertNotificationsRepositoryProvider, BpnEdcRepositoryProvider {

    @Autowired
    private AssetAsPlannedRepository assetAsPlannedRepository;

    @Autowired
    private AssetAsBuiltRepository assetRepository

    private AssetTestData assetTestDataConverter = new AssetTestData()

    @Autowired
    private ShellDescriptorRepository shellDescriptorRepository

    @Autowired
    private BpnRepository bpnRepository

    @Autowired
    private BpnEdcMappingRepository bpnEdcRepository

    @Autowired
    private JpaInvestigationRepository jpaInvestigationRepository

    @Autowired
    private JpaAlertRepository jpaAlertRepository

    @Autowired
    private JpaAlertNotificationRepository jpaAlertNotificationRepository

    @Autowired
    private JpaInvestigationNotificationRepository jpaInvestigationNotificationRepository

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
    AssetAsBuiltRepository assetRepository() {
        return assetRepository
    }

    @Override
    AssetTestData assetsConverter() {
        return assetTestDataConverter
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
    BpnEdcMappingRepository bpnEdcRepository() {
        return bpnEdcRepository
    }

    @Override
    JpaInvestigationRepository jpaInvestigationRepository() {
        return jpaInvestigationRepository
    }

    @Override
    JpaAlertRepository jpaAlertRepository() {
        return jpaAlertRepository
    }

    @Override
    JpaAlertNotificationRepository jpaAlertNotificationRepository() {
        return jpaAlertNotificationRepository
    }

    @Override
    JpaInvestigationNotificationRepository jpaNotificationRepository() {
        return jpaInvestigationNotificationRepository
    }

    @Override
    JdbcTemplate jdbcTemplate() {
        return jdbcTemplate
    }

    protected void eventually(Closure<?> conditions) {
        new PollingConditions(timeout: 15, initialDelay: 0.5).eventually(conditions)
    }

    protected String asJson(Object object) {
        return new JsonBuilder(object).toPrettyString()
    }

    protected String asJson(Map map) {
        return new JsonBuilder(map).toPrettyString()
    }
}
