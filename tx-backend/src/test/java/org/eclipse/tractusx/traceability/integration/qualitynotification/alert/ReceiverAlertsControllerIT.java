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

package org.eclipse.tractusx.traceability.integration.qualitynotification.alert;

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AlertsSupport;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import qualitynotification.base.request.UpdateQualityNotificationStatusRequest;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.SUPERVISOR;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ReceiverAlertsControllerIT extends IntegrationTestSpecification {

    @Autowired
    AlertsSupport alertsSupport;

    @Test
    void ShouldAcknowledgeReceivedAlert() throws JoseException {
        // given
        var alertId = alertsSupport.defaultReceivedAlertStored();
        String filterString = "channel,EQUAL,RECEIVER,AND";

        // when
        given()
                .contentType(ContentType.JSON)
                .body(
                        """
                                 {
                                 "status" : "ACKNOWLEDGED"
                                 }
                                """
                )
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .when()
                .post("/api/alerts/$alertId/update".replace("$alertId", alertId.toString()))
                .then()
                .statusCode(204);

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .param("filter", filterString)
                .get("/api/alerts")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1));
    }

    @Test
    void shouldNotUpdateToAcknowledgedNonExistingAlert() throws JoseException {
        // given
        final long notExistingAlertId = 1234L;
        String filterString = "channel,EQUAL,RECEIVER,AND";

        // when
        given()
                .contentType(ContentType.JSON)
                .body("""
                         {
                         "status" : "$status"
                         }
                        """.replace("$status", UpdateQualityNotificationStatusRequest.ACKNOWLEDGED.name()))
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .when()
                .post("/api/alerts/$notExistingAlertId/update".replace("$notExistingAlertId", Long.toString(notExistingAlertId)))
                .then()
                .statusCode(404);

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "15")
                .contentType(ContentType.JSON)
                .when()
                .param("filter", filterString)
                .get("/api/alerts")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(15))
                .body("content", Matchers.hasSize(0));
    }

    @Test
    void shouldNotUpdateToAcceptedNonExistingAlert() throws JoseException {
        // given
        final long notExistingAlertId = 1234L;
        String filterString = "channel,EQUAL,RECEIVER,AND";

        // when
        given()
                .contentType(ContentType.JSON)
                .body("""
                         {
                         "status" : "$status",
                         "reason" : "some reason, why not"
                         }
                        """.replace("$status", UpdateQualityNotificationStatusRequest.ACCEPTED.name()))
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .when()
                .post("/api/alerts/$notExistingAlertId/update".replace("$notExistingAlertId", Long.toString(notExistingAlertId)))
                .then()
                .statusCode(404);

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "15")
                .contentType(ContentType.JSON)
                .when()
                .param("filter", filterString)
                .get("/api/alerts")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(15))
                .body("content", Matchers.hasSize(0));
    }

    @Test
    void shouldNotUpdateToDeclinedNonExistingAlert() throws JoseException {
        // given
        final long notExistingAlertId = 1234L;
        String filterString = "channel,EQUAL,RECEIVER,AND";

        // when
        given()
                .contentType(ContentType.JSON)
                .body("""
                         {
                         "status" : "$status",
                         "reason" : "some reason, why not"
                         }
                        """.replace("$status", UpdateQualityNotificationStatusRequest.DECLINED.name()))
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .when()
                .post("/api/alerts/$notExistingAlertId/update".replace("$notExistingAlertId", Long.toString(notExistingAlertId)))
                .then()
                .statusCode(404);

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "15")
                .contentType(ContentType.JSON)
                .when()
                .param("filter", filterString)
                .get("/api/alerts")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(15))
                .body("content", Matchers.hasSize(0));
    }

    @ParameterizedTest
    @MethodSource("invalidRequest")
    void shouldNotUpdateWithInvalidRequest(final String request) throws JoseException {
        // given
        final long notExistingAlertId = 1234L;
        String filterString = "channel,EQUAL,SENDER,AND";

        // when
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .when()
                .post("/api/alerts/{notExistingAlertId}/update", Long.toString(notExistingAlertId))
                .then()
                .statusCode(400);

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "15")
                .contentType(ContentType.JSON)
                .when()
                .param("filter", filterString)
                .get("/api/alerts")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(15))
                .body("content", Matchers.hasSize(0));
    }

    private static Stream<Arguments> invalidRequest() {
        return Stream.of(
                arguments("""
                         {
                         "status" : "$status",
                         "reason" : "No reason should be for acknowledging"
                         }
                        """.replace("$status", UpdateQualityNotificationStatusRequest.ACKNOWLEDGED.name())),
                arguments("""
                         {
                         "status" : "$status",
                         "reason" : null
                         }
                        """.replace("$status", UpdateQualityNotificationStatusRequest.ACCEPTED.name())),
                arguments("""
                         {
                         "status" : "$status",
                         "reason" : " "
                         }
                        """.replace("$status", UpdateQualityNotificationStatusRequest.ACCEPTED.name())),
                arguments("""
                         {
                         "status" : "$status",
                         "reason" : null
                         }
                        """.replace("$status", UpdateQualityNotificationStatusRequest.DECLINED.name())),
                arguments("""
                         {
                         "status" : "$status",
                         "reason" : " "
                         }
                        """.replace("$status", UpdateQualityNotificationStatusRequest.DECLINED.name()))
        );
    }

}
