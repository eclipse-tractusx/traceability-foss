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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;

class ReadCreatedInvestigationsWithSearchCriteriaControllerIT extends IntegrationTestSpecification {

    @Autowired
    BpnSupport bpnSupport;
    @Autowired
    InvestigationNotificationsSupport investigationNotificationsSupport;

    NotificationFilter notificationFilter = NotificationFilter.builder()
            .channel(FilterAttribute.builder()
                    .value(Collections.singletonList(
                            FilterValue.builder()
                                    .value(NotificationSide.SENDER.name())
                                    .strategy(SearchCriteriaStrategy.EQUAL.name())
                                    .build()
                    ))
                    .operator(SearchCriteriaOperator.OR.name())
                    .build())
            .build();

    @Test
    void givenFilterBySendToProvided_whenGetInvestigations_thenReturnCreatedInvestigationsFilteredBySendTo() throws JoseException {
        // Given
        NotificationFilter notificationFilter1 = NotificationFilter.builder()
                .channel(notificationFilter.getChannel())
                .sendTo(FilterAttribute.builder()
                        .value(Collections.singletonList(
                                FilterValue.builder()
                                        .value("BPNL000000000001")
                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                        .build()
                        ))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        String testBpn = bpnSupport.testBpn();

        NotificationMessageEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createSenderMajorityInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder()
                        .page(0)
                        .size(10)
                        .sort(List.of())
                        .notificationFilter(notificationFilter1)
                        .build())
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
    void givenFilterByCreatedDateProvided_whenGetInvestigations_thenReturnCreatedInvestigationsFilteredByCreatedDate() throws JoseException {
        // Given
        Date myDate = Date.from(Instant.now());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = formatter.format(myDate);

        NotificationFilter notificationFilter1 = NotificationFilter.builder()
                .channel(notificationFilter.getChannel())
                .createdDate(FilterAttribute.builder()
                        .value(Collections.singletonList(
                                FilterValue.builder()
                                        .value(formattedDate)
                                        .strategy(SearchCriteriaStrategy.AT_LOCAL_DATE.name())
                                        .build()
                        ))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        String testBpn = bpnSupport.testBpn();

        NotificationMessageEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createSenderMajorityInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder()
                        .page(0)
                        .size(10)
                        .sort(List.of())
                        .notificationFilter(notificationFilter1)
                        .build())
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
    void givenFilterBySendToNameProvided_whenGetInvestigations_thenReturnCreatedInvestigationsFilteredBySendToName() throws JoseException {
        // Given
        NotificationFilter notificationFilter1 = NotificationFilter.builder()
                .channel(notificationFilter.getChannel())
                .sendToName(FilterAttribute.builder()
                        .value(Collections.singletonList(
                                FilterValue.builder()
                                        .value("OEM2")
                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                        .build()
                        ))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        String testBpn = bpnSupport.testBpn();

        NotificationMessageEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createSenderMajorityInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder()
                        .page(0)
                        .size(10)
                        .sort(List.of())
                        .notificationFilter(notificationFilter1)
                        .build())
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
    void givenFilterByStatusProvided_whenGetInvestigations_thenReturnCreatedInvestigationsFilteredByStatus() throws JoseException {
        // Given
        NotificationFilter notificationFilter1 = NotificationFilter.builder()
                .channel(notificationFilter.getChannel())
                .status(FilterAttribute.builder()
                        .value(Collections.singletonList(
                                FilterValue.builder()
                                        .value("ACCEPTED")
                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                        .build()
                        ))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        String testBpn = bpnSupport.testBpn();

        NotificationMessageEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createSenderMajorityInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder()
                        .page(0)
                        .size(10)
                        .sort(List.of())
                        .notificationFilter(notificationFilter1)
                        .build())
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
                .body("content.status", Matchers.hasItems("ACCEPTED"));
    }

    @Test
    void givenFilterBySeverityProvided_whenGetInvestigations_thenReturnCreatedInvestigationsFilteredBySeverity() throws JoseException {
        // Given
        NotificationFilter notificationFilter1 = NotificationFilter.builder()
                .channel(notificationFilter.getChannel())
                .severity(FilterAttribute.builder()
                        .value(Collections.singletonList(
                                FilterValue.builder()
                                        .value("LIFE_THREATENING")
                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                        .build()
                        ))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        String testBpn = bpnSupport.testBpn();

        NotificationMessageEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createSenderMajorityInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder()
                        .page(0)
                        .size(10)
                        .sort(List.of())
                        .notificationFilter(notificationFilter1)
                        .build())
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
    void givenFilterByCreatedByProvided_whenGetInvestigations_thenReturnCreatedInvestigationsFilteredByCreatedBy() throws JoseException {
        // Given
        NotificationFilter notificationFilter1 = NotificationFilter.builder()
                .channel(notificationFilter.getChannel())
                .createdBy(FilterAttribute.builder()
                        .value(Collections.singletonList(
                                FilterValue.builder()
                                        .value("BPNL00000000000A")
                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                        .build()
                        ))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        String testBpn = bpnSupport.testBpn();

        NotificationMessageEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createSenderMajorityInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder()
                        .page(0)
                        .size(10)
                        .sort(List.of())
                        .notificationFilter(notificationFilter1)
                        .build())
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
    void givenFilterByDescriptionProvided_whenGetInvestigations_thenReturnCreatedInvestigationsFilteredByDescription() throws JoseException {
        // Given
        NotificationFilter notificationFilter1 = NotificationFilter.builder()
                .channel(notificationFilter.getChannel())
                .description(FilterAttribute.builder()
                        .value(Collections.singletonList(
                                FilterValue.builder()
                                        .value("First")
                                        .strategy(SearchCriteriaStrategy.STARTS_WITH.name())
                                        .build()
                        ))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        String testBpn = bpnSupport.testBpn();

        NotificationMessageEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createSenderMajorityInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder()
                        .page(0)
                        .size(10)
                        .sort(List.of())
                        .notificationFilter(notificationFilter1)
                        .build())
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
                .body("content.description", Matchers.hasItems("First Investigation on Asset1"));
    }

    @Test
    void givenFilterByDescriptionAndSendToProvided_whenGetInvestigations_thenReturnCreatedInvestigationsFilteredByDescriptionAndSendTo() throws JoseException {
        // Given
        NotificationFilter notificationFilter1 = NotificationFilter.builder()
                .channel(notificationFilter.getChannel())
                .description(FilterAttribute.builder()
                        .value(Collections.singletonList(
                                FilterValue.builder()
                                        .value("First")
                                        .strategy(SearchCriteriaStrategy.STARTS_WITH.name())
                                        .build()
                        ))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .sendTo(FilterAttribute.builder()
                        .value(Collections.singletonList(
                                FilterValue.builder()
                                        .value("BPNL000000000001")
                                        .strategy(SearchCriteriaStrategy.EQUAL.name())
                                        .build()
                        ))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        String testBpn = bpnSupport.testBpn();

        NotificationMessageEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createSenderMajorityInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        // Then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder()
                        .page(0)
                        .size(10)
                        .sort(List.of())
                        .notificationFilter(notificationFilter1)
                        .build())
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
                .body("content.description", Matchers.hasItems("First Investigation on Asset1"));
    }
}
