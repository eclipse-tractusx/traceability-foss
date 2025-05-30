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
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.traceability.integration.notification.investigation;

import common.FilterAttribute;
import common.FilterValue;
import io.restassured.http.ContentType;
import notification.request.NotificationFilter;
import notification.request.NotificationRequest;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationNotificationsSupport;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSide;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationMessageEntity;
import org.eclipse.tractusx.traceability.testdata.InvestigationTestDataFactory;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;

class ReadReceivedInvestigationsWithSearchCriteriaControllerIT extends IntegrationTestSpecification {

    @Autowired
    BpnSupport bpnSupport;
    @Autowired
    InvestigationNotificationsSupport investigationNotificationsSupport;

    @Test
    void givenFilterBySendToProvided_whenGetInvestigations_thenReturnReceivedInvestigationsFilteredBySendTo() throws JoseException {
        // Given
        String testBpn = bpnSupport.testBpn();
        NotificationMessageEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createReceiverMajorityInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        NotificationFilter filter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(NotificationSide.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
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
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(2))
                .body("totalItems", Matchers.is(2))
                .body("content.messages.sendTo", Matchers.hasItems(Arrays.asList("BPNL000000000001")));
    }

    @Test
    void givenFilterByCreatedDateProvided_whenGetInvestigations_thenReturnReceivedInvestigationsFilteredByCreatedDate() throws JoseException {
        // Given
        Date myDate = Date.from(Instant.now());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter.format(myDate);
        String testBpn = bpnSupport.testBpn();

        NotificationMessageEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createReceiverMajorityInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        NotificationFilter filter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(NotificationSide.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .createdDate(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(formattedDate).strategy(SearchCriteriaStrategy.AT_LOCAL_DATE.name()).build()))
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
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(4))
                .body("totalItems", Matchers.is(4));
    }

    @Test
    void givenFilterBySendToNameProvided_whenGetInvestigations_thenReturnReceivedInvestigationsFilteredBySendToName() throws JoseException {
        // Given
        String testBpn = bpnSupport.testBpn();
        NotificationMessageEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createReceiverMajorityInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        NotificationFilter filter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(NotificationSide.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
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
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.sendToName", Matchers.hasItems("OEM2"));
    }

    @Test
    void givenFilterByStatusProvided_whenGetInvestigations_thenReturnReceivedInvestigationsFilteredByStatus() throws JoseException {
        // Given
        String testBpn = bpnSupport.testBpn();
        NotificationMessageEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createReceiverMajorityInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        NotificationFilter filter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(NotificationSide.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .status(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("RECEIVED").strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
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
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.status", Matchers.hasItems("RECEIVED"));
    }

    @Test
    void givenFilterBySeverityProvided_whenGetInvestigations_thenReturnReceivedInvestigationsFilteredBySeverity() throws JoseException {
        // Given
        String testBpn = bpnSupport.testBpn();
        NotificationMessageEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createReceiverMajorityInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        NotificationFilter filter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(NotificationSide.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
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
                .body(NotificationRequest.builder().page(0).size(10).notificationFilter(filter).build())
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.severity", Matchers.hasItems("LIFE-THREATENING"));
    }

    @Test
    void givenFilterByCreatedByProvided_whenGetInvestigations_thenReturnReceivedInvestigationsFilteredByCreatedBy() throws JoseException {
        // Given
        String testBpn = bpnSupport.testBpn();
        NotificationMessageEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createReceiverMajorityInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        NotificationFilter filter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(NotificationSide.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
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
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(4))
                .body("totalItems", Matchers.is(4))
                .body("content.createdBy", Matchers.everyItem(Matchers.is("BPNL00000003AXS3")));
    }

    @Test
    void givenFilterByDescriptionProvided_whenGetInvestigations_thenReturnReceivedInvestigationsFilteredByDescription() throws JoseException {
        // Given
        String testBpn = bpnSupport.testBpn();
        NotificationMessageEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createReceiverMajorityInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        NotificationFilter filter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(NotificationSide.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .description(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("Second").strategy(SearchCriteriaStrategy.STARTS_WITH.name()).build()))
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
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.description", Matchers.hasItems("Second Investigation on Asset2"));
    }

    @Test
    void givenFilterByDescriptionAndSendToProvided_whenGetInvestigations_thenReturnReceivedInvestigationsFilteredByDescriptionAndSendTo() throws JoseException {
        // Given
        String testBpn = bpnSupport.testBpn();
        NotificationMessageEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createReceiverMajorityInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        NotificationFilter filter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(NotificationSide.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .description(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("Second").strategy(SearchCriteriaStrategy.STARTS_WITH.name()).build()))
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
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.messages.sendTo", Matchers.hasItems(Arrays.asList("BPNL000000000001")))
                .body("content.description", Matchers.hasItems("Second Investigation on Asset2"));
    }

    @Test
    void givenFilterBySendToNameOrSendNameProvided_whenGetInvestigations_thenReturnReceivedInvestigationsFilteredBySendToNames() throws JoseException {
        // Given
        String testBpn = bpnSupport.testBpn();
        NotificationMessageEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createReceiverMajorityInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        NotificationFilter filter = NotificationFilter.builder()
                .channel(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value(NotificationSide.RECEIVER.name()).strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
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
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(3))
                .body("totalItems", Matchers.is(3))
                .body("content.messages.sendTo", Matchers.hasItems(Arrays.asList("BPNL000000000001")))
                .body("content.sendToName", Matchers.hasItems("OEM2"));
    }
}
