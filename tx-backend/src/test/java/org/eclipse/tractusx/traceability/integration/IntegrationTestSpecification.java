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
package org.eclipse.tractusx.traceability.integration;

import groovy.json.JsonBuilder;
import io.restassured.RestAssured;
import org.awaitility.Awaitility;
import org.eclipse.tractusx.traceability.integration.common.config.PostgreSQLConfig;
import org.eclipse.tractusx.traceability.integration.common.config.RestAssuredConfig;
import org.eclipse.tractusx.traceability.integration.common.config.RestitoConfig;
import org.eclipse.tractusx.traceability.integration.common.support.DatabaseSupport;
import org.eclipse.tractusx.traceability.integration.common.support.OAuth2ApiSupport;
import org.eclipse.tractusx.traceability.integration.common.support.OAuth2Support;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
@ActiveProfiles("integration-spring-boot")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@ContextConfiguration(initializers = {PostgreSQLConfig.Initializer.class, RestitoConfig.Initializer.class}, classes = {RestAssuredConfig.class})
public class IntegrationTestSpecification {

    @Autowired
    public OAuth2Support oAuth2Support;

    @Autowired
    public OAuth2ApiSupport oAuth2ApiSupport;

    @Autowired
    DatabaseSupport databaseSupport;

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void beforeEach() throws JoseException {
        RestAssured.port = port;
        oAuth2ApiSupport.oauth2ApiReturnsTechnicalUserToken();
        oAuth2ApiSupport.oauth2ApiReturnsJwkCerts(oAuth2Support.jwk());
    }

    @AfterEach
    void afterEach() {
        RestitoConfig.clear();
        oAuth2Support.clearOAuth2Client();
        databaseSupport.clearAllTables();
    }

    protected String asJson(Map map) {
        return new JsonBuilder(map).toPrettyString();
    }

    protected void eventually(Callable<Boolean> conditions) throws InterruptedException {
        Awaitility.setDefaultPollInterval(500, TimeUnit.MILLISECONDS);
        Awaitility.setDefaultTimeout(30, TimeUnit.SECONDS);
        Awaitility.pollInSameThread();
        await().pollDelay(2, TimeUnit.SECONDS).until(conditions, Matchers.equalTo(true));
    }

}
