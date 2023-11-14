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
import org.eclipse.tractusx.traceability.integration.common.support.AlertNotificationsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertNotificationEntity;
import org.eclipse.tractusx.traceability.testdata.AlertTestDataFactory;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;

class ReadReceivedAlertsWithSearchCriteriaControllerIT extends IntegrationTestSpecification {

    @Autowired
    AlertNotificationsSupport alertNotificationsSupport;

    @Autowired
    BpnSupport bpnSupport;

    @Test
    void givenFilterBySendToProvided_whenGetAlerts_thenReturnReceivedAlertsFilteredBySendTo() throws JoseException {
        // given
        String filterString = "sendTo,EQUAL,BPNL000000000004";
        String testBpn = bpnSupport.testBpn();

        AlertNotificationEntity[] alertNotificationEntities = AlertTestDataFactory.createAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .param("filter", filterString)
                .param("filterOperator", "AND")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/received")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.sendTo", Matchers.hasItems("BPNL000000000004"));
    }

    @Test
    void givenFilterByCreatedDateProvided_whenGetAlerts_thenReturnReceivedAlertsFilteredByCreatedDate() throws JoseException {
        // given
        String filterString = "createdDate,AT_LOCAL_DATE,2023-12-09";
        String testBpn = bpnSupport.testBpn();

        AlertNotificationEntity[] alertNotificationEntities = AlertTestDataFactory.createAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .param("filter", filterString)
                .param("filterOperator", "AND")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/received")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1));
    }

    @Test
    void givenFilterBySendToNameProvided_whenGetAlerts_thenReturnReceivedAlertsFilteredBySendToName() throws JoseException {
        // given
        String filterString = "sendToName,EQUAL,OEM4";
        String testBpn = bpnSupport.testBpn();

        AlertNotificationEntity[] alertNotificationEntities = AlertTestDataFactory.createAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .param("filter", filterString)
                .param("filterOperator", "AND")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/received")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.sendToName", Matchers.hasItems("OEM4"));
    }

    @Test
    void givenFilterByStatusProvided_whenGetAlerts_thenReturnReceivedAlertsFilteredByStatus() throws JoseException {
        // given
        String filterString = "status,EQUAL,CANCELED";
        String testBpn = bpnSupport.testBpn();

        AlertNotificationEntity[] alertNotificationEntities = AlertTestDataFactory.createAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .param("filter", filterString)
                .param("filterOperator", "AND")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/received")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.status", Matchers.hasItems("CANCELED"));
    }

    @Test
    void givenFilterBySeverityProvided_whenGetAlerts_thenReturnReceivedAlertsFilteredBySeverity() throws JoseException {
        // given
        String filterString = "severity,EQUAL,0";
        String testBpn = bpnSupport.testBpn();

        AlertNotificationEntity[] alertNotificationEntities = AlertTestDataFactory.createAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .param("filter", filterString)
                .param("filterOperator", "AND")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/received")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.severity", Matchers.hasItems("MINOR"));
    }

    @Test
    void givenFilterByCreatedByProvided_whenGetAlerts_thenReturnReceivedAlertsFilteredByCreatedBy() throws JoseException {
        // given
        String filterString = "createdBy,EQUAL,BPNL00000000000A";
        String testBpn = bpnSupport.testBpn();

        AlertNotificationEntity[] alertNotificationEntities = AlertTestDataFactory.createAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .param("filter", filterString)
                .param("filterOperator", "AND")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/received")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.createdBy", Matchers.hasItems("BPNL00000000000A"));
    }

    @Test
    void givenFilterByDescriptionProvided_whenGetAlerts_thenReturnReceivedAlertsFilteredByDescription() throws JoseException {
        // given
        String filterString = "description,STARTS_WITH,Fifth";
        String testBpn = bpnSupport.testBpn();

        AlertNotificationEntity[] alertNotificationEntities = AlertTestDataFactory.createAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .param("filter", filterString)
                .param("filterOperator", "AND")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/received")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.description", Matchers.hasItems("Fifth Alert on Asset5"));
    }

    @Test
    void givenFilterByDescriptionAndSendToProvided_whenGetAlerts_thenReturnReceivedAlertsFilteredByDescriptionAndSendTo() throws JoseException {
        // given
        String filterString1 = "description,STARTS_WITH,Fifth";
        String filterString2 = "sendTo,EQUAL,BPNL000000000004";
        String testBpn = bpnSupport.testBpn();

        AlertNotificationEntity[] alertNotificationEntities = AlertTestDataFactory.createAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .param("filter", filterString1)
                .param("filter", filterString2)
                .param("filterOperator", "AND")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/received")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.sendTo", Matchers.hasItems("BPNL000000000004"))
                .body("content.description", Matchers.hasItems("Fifth Alert on Asset5"));
    }

    @Test
    void givenFilterBySendToNameOrSendToProvided_whenGetAlerts_thenReturnReceivedAlertsFilteredBySendToNameOrSendTo() throws JoseException {
        // given
        String filterString1 = "sendToName,EQUAL,OEM4";
        // non-existent BPN but based on OR condition, OEM4 record would still be fetched
        String filterString2 = "sendTo,EQUAL,BPNL000000000005";
        String testBpn = bpnSupport.testBpn();

        AlertNotificationEntity[] alertNotificationEntities = AlertTestDataFactory.createAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .param("filter", filterString1)
                .param("filter", filterString2)
                .param("filterOperator", "OR")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/received")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.sendToName", Matchers.hasItems("OEM4"));
    }
}
