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
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.PageableFilterRequest;
import org.eclipse.tractusx.traceability.common.request.SearchCriteriaRequestParam;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AlertNotificationsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertNotificationEntity;
import org.eclipse.tractusx.traceability.testdata.AlertTestDataFactory;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.hamcrest.Matchers.equalTo;

class ReadCreatedAlertsWithSearchCriteriaControllerIT extends IntegrationTestSpecification {

    @Autowired
    AlertNotificationsSupport alertNotificationsSupport;

    @Autowired
    BpnSupport bpnSupport;

    @Test
    void givenFilterBySendToProvided_whenGetAlerts_thenReturnCreatedAlertsFilteredBySendTo() throws JoseException {
        // given
        String filterString = "channel,EQUAL,SENDER,AND";
        String filter = "sendTo,EQUAL,BPNL000000000001,AND";
        String testBpn = bpnSupport.testBpn();

        AlertNotificationEntity[] alertNotificationEntities = AlertTestDataFactory.createSenderMajorityAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10,List.of()), new SearchCriteriaRequestParam(List.of(filterString,filter))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(2))
                .body("totalItems", Matchers.is(2))
                .body("content.sendTo", Matchers.hasItems("BPNL000000000001"));
    }

    @Test
    void givenFilterByCreatedDateProvided_whenGetAlerts_thenReturnCreatedAlertsFilteredByCreatedDate() throws JoseException {
        // given
        String filterString = "channel,EQUAL,SENDER,AND";
        Date myDate = Date.from(Instant.now());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter.format(myDate);
        String filter = "createdDate,AT_LOCAL_DATE," + formattedDate + ",AND";
        String testBpn = bpnSupport.testBpn();

        AlertNotificationEntity[] alertNotificationEntities = AlertTestDataFactory.createSenderMajorityAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10,List.of()), new SearchCriteriaRequestParam(List.of(filterString,filter))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(4))
                .body("totalItems", Matchers.is(4));
    }

    @Test
    void givenFilterBySendToNameProvided_whenGetAlerts_thenReturnCreatedAlertsFilteredBySendToName() throws JoseException {
        // given
        String filterString = "channel,EQUAL,SENDER,AND";
        String filter = "sendToName,EQUAL,OEM2,AND";
        String testBpn = bpnSupport.testBpn();

        AlertNotificationEntity[] alertNotificationEntities = AlertTestDataFactory.createSenderMajorityAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10,List.of()), new SearchCriteriaRequestParam(List.of(filterString,filter))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.sendToName", Matchers.hasItems("OEM2"));
    }

    @Test
    void givenFilterByStatusProvided_whenGetAlerts_thenReturnCreatedAlertsFilteredByStatus() throws JoseException {
        // given
        String filterString = "channel,EQUAL,SENDER,AND";
        String filter = "status,EQUAL,ACCEPTED,AND";
        String testBpn = bpnSupport.testBpn();

        AlertNotificationEntity[] alertNotificationEntities = AlertTestDataFactory.createSenderMajorityAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10,List.of()), new SearchCriteriaRequestParam(List.of(filterString,filter))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(2))
                .body("totalItems", Matchers.is(2))
                .body("content.status", Matchers.hasItems("ACCEPTED"));
    }

    @Test
    void givenFilterBySeverityProvided_whenGetAlerts_thenReturnCreatedAlertsFilteredBySeverity() throws JoseException {
        // given
        String filterString = "channel,EQUAL,SENDER,AND";
        String filter = "severity,EQUAL,LIFE-THREATENING,AND";
        String testBpn = bpnSupport.testBpn();

        AlertNotificationEntity[] alertNotificationEntities = AlertTestDataFactory.createSenderMajorityAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10,List.of()), new SearchCriteriaRequestParam(List.of(filterString,filter))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.severity", Matchers.hasItems("LIFE-THREATENING"));
    }

    @Test
    void givenFilterByCreatedByProvided_whenGetAlerts_thenReturnCreatedAlertsFilteredByCreatedBy() throws JoseException {
        // given
        String filterString = "channel,EQUAL,SENDER,AND";
        String filter = "createdBy,EQUAL,BPNL00000000000A,AND";
        String testBpn = bpnSupport.testBpn();

        AlertNotificationEntity[] alertNotificationEntities = AlertTestDataFactory.createSenderMajorityAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10,List.of()), new SearchCriteriaRequestParam(List.of(filterString,filter))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(4))
                .body("totalItems", Matchers.is(4))
                .body("content.createdBy", Matchers.hasItems("BPNL00000000000A"));
    }

    @Test
    void givenFilterByDescriptionProvided_whenGetAlerts_thenReturnCreatedAlertsFilteredByDescription() throws JoseException {
        // given
        String filterString = "channel,EQUAL,SENDER,AND";
        String filter = "description,STARTS_WITH,First,AND";
        String testBpn = bpnSupport.testBpn();

        AlertNotificationEntity[] alertNotificationEntities = AlertTestDataFactory.createSenderMajorityAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10,List.of()), new SearchCriteriaRequestParam(List.of(filterString,filter))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.description", Matchers.hasItems("First Alert on Asset1"));
    }

    @Test
    void givenFilterByDescriptionAndSendToProvided_whenGetAlerts_thenReturnCreatedAlertsFilteredByDescriptionAndSendTo() throws JoseException {
        // given
        String filterString = "channel,EQUAL,SENDER,AND";
        String filterString1 = "description,STARTS_WITH,First,AND";
        String filterString2 = "sendTo,EQUAL,BPNL000000000001,AND";
        String testBpn = bpnSupport.testBpn();

        AlertNotificationEntity[] alertNotificationEntities = AlertTestDataFactory.createSenderMajorityAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10,List.of()), new SearchCriteriaRequestParam(List.of(filterString,filterString1,filterString2))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.sendTo", Matchers.hasItems("BPNL000000000001"))
                .body("content.description", Matchers.hasItems("First Alert on Asset1"));
    }

    @Test
    void givenFilterBySendToNameOrSendToProvided_whenGetAlerts_thenReturnCreatedAlertsFilteredBySendToNameOrSendTo() throws JoseException {
        // given
        String filterString = "channel,EQUAL,SENDER,AND";
        String filterString1 = "sendToName,EQUAL,OEM2,AND";
        String filterString2 = "sendTo,EQUAL,BPNL000000000001,AND";
        String testBpn = bpnSupport.testBpn();

        AlertNotificationEntity[] alertNotificationEntities = AlertTestDataFactory.createSenderMajorityAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10,List.of()), new SearchCriteriaRequestParam(List.of(filterString,filterString1,filterString2))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(3))
                .body("totalItems", Matchers.is(3))
                .body("content.sendTo", Matchers.hasItems("BPNL000000000001"))
                .body("content.sendToName", Matchers.hasItems("OEM2"));
    }

    private static Stream<Arguments> filterArguments() {
        return Stream.of(
                Arguments.of(
                        "description,STARTS_WITH,first,AND"
                ),
                Arguments.of(
                        "description,STARTS_WITH,First,AND"

                ),
                Arguments.of(
                        "description,STARTS_WITH,FIRST,AND"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("filterArguments")
    void testIfFilteringIsCaseInsensitive(String filter) throws JoseException {

        String filterString = "channel,EQUAL,SENDER,AND";
        String testBpn = bpnSupport.testBpn();

        AlertNotificationEntity[] alertNotificationEntities = AlertTestDataFactory.createSenderMajorityAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10,List.of()), new SearchCriteriaRequestParam(List.of(filterString,filter))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(1))
                .body("content.description[0]", equalTo("First Alert on Asset1"));
    }
}
