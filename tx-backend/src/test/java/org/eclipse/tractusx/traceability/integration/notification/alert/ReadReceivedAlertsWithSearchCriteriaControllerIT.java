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

package org.eclipse.tractusx.traceability.integration.notification.alert;

import common.FilterAttribute;
import common.FilterValue;
import io.restassured.http.ContentType;
import notification.request.NotificationFilter;
import notification.request.NotificationRequest;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AlertNotificationsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationMessageEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.testdata.AlertTestDataFactory;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;

class ReadReceivedAlertsWithSearchCriteriaControllerIT extends IntegrationTestSpecification {

    @Autowired
    AlertNotificationsSupport alertNotificationsSupport;

    @Autowired
    BpnSupport bpnSupport;

    @Test
    void givenFilterBySendToProvided_whenGetAlerts_thenReturnReceivedAlertsFilteredBySendTo() throws JoseException {
        // Given
        String testBpn = bpnSupport.testBpn();
        NotificationMessageEntity[] alertNotificationEntities = AlertTestDataFactory.createReceiverMajorityAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        NotificationFilter filter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(NotificationSideBaseEntity.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .sendTo(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("BPNL000000000001").strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).notificationFilter(filter).build())
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(2))
                .body("totalItems", Matchers.is(2))
                .body("content.messages.sendTo", Matchers.hasItems(List.of("BPNL000000000001")));
    }

    @Test
    void givenFilterByCreatedDateProvided_whenGetAlerts_thenReturnReceivedAlertsFilteredByCreatedDate() throws JoseException {
        // Given
        Date myDate = Date.from(Instant.now());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter.format(myDate);
        String testBpn = bpnSupport.testBpn();

        NotificationMessageEntity[] alertNotificationEntities = AlertTestDataFactory.createReceiverMajorityAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        NotificationFilter filter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(NotificationSideBaseEntity.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .createdDate(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(formattedDate).strategy(SearchCriteriaStrategy.AT_LOCAL_DATE.name()).build()))
                        .operator(SearchCriteriaOperator.AND.name())
                        .build())
                .build();

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).notificationFilter(filter).build())
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(4))
                .body("totalItems", Matchers.is(4));
    }

    @Test
    void givenFilterBySendToNameProvided_whenGetAlerts_thenReturnReceivedAlertsFilteredBySendToName() throws JoseException {
        // Given
        String testBpn = bpnSupport.testBpn();
        NotificationMessageEntity[] alertNotificationEntities = AlertTestDataFactory.createReceiverMajorityAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        NotificationFilter filter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(NotificationSideBaseEntity.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .sendToName(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("OEM2").strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).notificationFilter(filter).build())
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
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
    void givenFilterByStatusProvided_whenGetAlerts_thenReturnReceivedAlertsFilteredByStatus() throws JoseException {
        // Given
        String testBpn = bpnSupport.testBpn();
        NotificationMessageEntity[] alertNotificationEntities = AlertTestDataFactory.createReceiverMajorityAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        NotificationFilter filter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(NotificationSideBaseEntity.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .status(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("ACCEPTED").strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).notificationFilter(filter).build())
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
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
    void givenFilterBySeverityProvided_whenGetAlerts_thenReturnReceivedAlertsFilteredBySeverity() throws JoseException {
        // Given
        String sort = "createdDate,desc";
        String testBpn = bpnSupport.testBpn();

        NotificationMessageEntity[] alertNotificationEntities = AlertTestDataFactory.createReceiverMajorityAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        NotificationFilter filter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(NotificationSideBaseEntity.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .severity(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("LIFE_THREATENING").strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).sort(List.of(sort)).notificationFilter(filter).build())
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
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
    void givenFilterByCreatedByProvided_whenGetAlerts_thenReturnReceivedAlertsFilteredByCreatedBy() throws JoseException {
        // Given
        String testBpn = bpnSupport.testBpn();
        NotificationMessageEntity[] alertNotificationEntities = AlertTestDataFactory.createReceiverMajorityAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        NotificationFilter filter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(NotificationSideBaseEntity.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .createdBy(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("BPNL00000000000A").strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).notificationFilter(filter).build())
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(4))
                .body("totalItems", Matchers.is(4))
                .body("content.createdBy", Matchers.hasItems("BPNL00000003AXS3"));
    }

    @Test
    void givenFilterByDescriptionProvided_whenGetAlerts_thenReturnReceivedAlertsFilteredByDescription() throws JoseException {
        // Given
        String testBpn = bpnSupport.testBpn();
        NotificationMessageEntity[] alertNotificationEntities = AlertTestDataFactory.createReceiverMajorityAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        NotificationFilter filter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(NotificationSideBaseEntity.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .description(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("First").strategy(SearchCriteriaStrategy.STARTS_WITH.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).notificationFilter(filter).build())
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
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
    void givenFilterByDescriptionAndSendToProvided_whenGetAlerts_thenReturnReceivedAlertsFilteredByDescriptionAndSendTo() throws JoseException {
        // Given
        String testBpn = bpnSupport.testBpn();
        NotificationMessageEntity[] alertNotificationEntities = AlertTestDataFactory.createReceiverMajorityAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        NotificationFilter filter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(NotificationSideBaseEntity.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .description(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("First").strategy(SearchCriteriaStrategy.STARTS_WITH.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .sendTo(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("BPNL000000000001").strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).notificationFilter(filter).build())
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.messages.sendTo", Matchers.hasItems(List.of("BPNL000000000001")))
                .body("content.description", Matchers.hasItems("First Alert on Asset1"));
    }

    @Test
    void givenFilterBySendToNameOrSendToName_whenGetAlerts_thenReturnReceivedAlertsFilteredBySendToNames() throws JoseException {
        // Given
        String testBpn = bpnSupport.testBpn();
        NotificationMessageEntity[] alertNotificationEntities = AlertTestDataFactory.createReceiverMajorityAlertNotificationEntitiesTestData(testBpn);
        alertNotificationsSupport.storedAlertNotifications(alertNotificationEntities);

        NotificationFilter filter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(NotificationSideBaseEntity.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .sendToName(FilterAttribute.builder()
                        .value(List.of(
                                FilterValue.builder().value("OEM2").strategy(SearchCriteriaStrategy.EQUAL.name()).build(),
                                FilterValue.builder().value("OEM1").strategy(SearchCriteriaStrategy.EQUAL.name()).build()
                        ))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).notificationFilter(filter).build())
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(3))
                .body("totalItems", Matchers.is(3))
                .body("content.messages.sendTo", Matchers.hasItems(List.of("BPNL000000000001")))
                .body("content.sendToName", Matchers.hasItems("OEM2"));
    }
}
