/********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.configuration;

import configuration.request.OrderConfigurationRequest;
import configuration.request.TriggerConfigurationRequest;
import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.configuration.infrastructure.model.OrderConfigurationEntity;
import org.eclipse.tractusx.traceability.configuration.infrastructure.model.OrderEntity;
import org.eclipse.tractusx.traceability.configuration.infrastructure.model.TriggerConfigurationEntity;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.ConfigurationSupport;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Locale;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.SUPERVISOR;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

class ConfigurationControllerIT extends IntegrationTestSpecification {

    @Autowired
    ConfigurationSupport configurationSupport;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        Locale.setDefault(Locale.ENGLISH);
    }

    @Test
    void shouldStoreOrderConfiguration() throws JoseException {
        // given
        OrderConfigurationRequest request = OrderConfigurationRequest.builder()
                .batchSize(10)
                .jobTimeoutMs(100)
                .timeoutMs(1000)
                .build();

        // when
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(request)
                .log()
                .all()
                .when()
                .post("/api/orders/configuration/batches")
                .then()
                .statusCode(201);

        // then
        List<OrderConfigurationEntity> orderConfigurations = configurationSupport.getOrderConfigurations();

        assertThat(orderConfigurations).hasSize(1);
        assertThat(orderConfigurations.get(0).getBatchSize()).isEqualTo(10);
        assertThat(orderConfigurations.get(0).getJobTimeoutMs()).isEqualTo(100);
        assertThat(orderConfigurations.get(0).getTimeoutMs()).isEqualTo(1000);
    }

    @Test
    void shouldRetrieveTheLatestOrderConfigurationForGivenOrder() throws JoseException {
        // given
        Long orderId = 1L;
        OrderEntity orderEntity = OrderEntity.builder().id(orderId).build();

        configurationSupport.storeOrder(orderEntity);
        configurationSupport.storeOrderConfiguration(OrderConfigurationEntity.builder().order(orderEntity).batchSize(80).build());
        configurationSupport.storeOrderConfiguration(OrderConfigurationEntity.builder().order(orderEntity).batchSize(90).build());
        configurationSupport.storeOrderConfiguration(OrderConfigurationEntity.builder().order(orderEntity).batchSize(100).build());

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log()
                .all()
                .when()
                .get("/api/orders/configuration/batches/" + orderId + "/active")
                .then()
                .statusCode(200)
                .body("batchSize", equalTo(100));
    }

    @Test
    void shouldTriggerConfiguration() throws JoseException {
        // given
        TriggerConfigurationRequest request = TriggerConfigurationRequest.builder()
                .aasTTL(10)
                .partTTL(100)
                .cronExpressionRegisterOrderTTLReached("0 0 0 1 1 ?")
                .cronExpressionMapCompletedOrders("0 0 0 2 2 ?")
                .build();

        // when
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(request)
                .log()
                .all()
                .when()
                .post("/api/orders/configuration/triggers")
                .then()
                .statusCode(201);

        // then
        List<TriggerConfigurationEntity> triggerConfigurations = configurationSupport.getTriggersConfigurations();

        assertThat(triggerConfigurations).hasSize(1);
        assertThat(triggerConfigurations.get(0).getAasTTL()).isEqualTo(10);
        assertThat(triggerConfigurations.get(0).getPartTTL()).isEqualTo(100);
        assertThat(triggerConfigurations.get(0).getCronExpressionRegisterOrderTTLReached()).isEqualTo("0 0 0 1 1 ?");
        assertThat(triggerConfigurations.get(0).getCronExpressionMapCompletedOrders()).isEqualTo("0 0 0 2 2 ?");
    }

    @Test
    void shouldRetrieveTheLatestTriggerConfiguration() throws JoseException {
        // given
        configurationSupport.storeTriggerConfiguration(TriggerConfigurationEntity.builder().aasTTL(10).build());
        configurationSupport.storeTriggerConfiguration(TriggerConfigurationEntity.builder().aasTTL(20).build());
        configurationSupport.storeTriggerConfiguration(TriggerConfigurationEntity.builder().aasTTL(30).build());

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log()
                .all()
                .when()
                .get("/api/orders/configuration/triggers/active")
                .then()
                .statusCode(200)
                .body("aasTTL", equalTo(30));
    }

    @Test
    void shouldNotCreateOrderConfigurationAsASupervisor() throws JoseException {
        // given
        OrderConfigurationRequest request = OrderConfigurationRequest.builder()
                .batchSize(10)
                .jobTimeoutMs(100)
                .timeoutMs(1000)
                .build();

        // when
        given()
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .contentType(ContentType.JSON)
                .body(request)
                .log()
                .all()
                .when()
                .post("/api/orders/configuration/batches")
                .then()
                .statusCode(403);
    }

    @Test
    void shouldNotCreateOrderConfigurationDueToBadRequest_batchSizeStep() throws JoseException {
        // given
        OrderConfigurationRequest request = OrderConfigurationRequest.builder()
                .batchSize(11)
                .jobTimeoutMs(100)
                .timeoutMs(1000)
                .build();

        // when
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(request)
                .log()
                .all()
                .when()
                .post("/api/orders/configuration/batches")
                .then()
                .statusCode(400)
                .body("message", equalTo("BatchSize must be one of: 10, 20, ..., 100"));
    }

    @Test
    void shouldNotCreateOrderConfigurationDueToBadRequest_batchSizeStepAndSizeTooSmall() throws JoseException {
        // given
        OrderConfigurationRequest request = OrderConfigurationRequest.builder()
                .batchSize(1)
                .jobTimeoutMs(100)
                .timeoutMs(1000)
                .build();

        // when
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(request)
                .log()
                .all()
                .when()
                .post("/api/orders/configuration/batches")
                .then()
                .statusCode(400)
                .body("message", containsString("must be greater than or equal to 10"))
                .body("message", containsString("BatchSize must be one of: 10, 20, ..., 100"));
    }

    @Test
    void shouldNotCreateOrderConfigurationDueToBadRequest_batchSizeTooBig() throws JoseException {
        // given
        OrderConfigurationRequest request = OrderConfigurationRequest.builder()
                .batchSize(200)
                .jobTimeoutMs(100)
                .timeoutMs(1000)
                .build();

        // when
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(request)
                .log()
                .all()
                .when()
                .post("/api/orders/configuration/batches")
                .then()
                .statusCode(400)
                .body("message", containsString("must be less than or equal to 100"));
    }

    @Test
    void shouldNotCreateTriggerConfigurationDueToBadRequest_invalidCronExpression() throws JoseException {
        // given
        TriggerConfigurationRequest request = TriggerConfigurationRequest.builder()
                .aasTTL(10)
                .partTTL(100)
                .cronExpressionRegisterOrderTTLReached("invalid")
                .cronExpressionMapCompletedOrders("0 0 0 2 2 ?")
                .build();

        // when
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(request)
                .log()
                .all()
                .when()
                .post("/api/orders/configuration/triggers")
                .then()
                .statusCode(400)
                .body("message", equalTo("Cron expression is not valid."));
    }


}
