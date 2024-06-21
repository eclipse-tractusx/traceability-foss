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

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.PageableFilterRequest;
import org.eclipse.tractusx.traceability.common.request.SearchCriteriaRequestParam;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationNotificationsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationsSupport;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationTypeEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationMessageEntity;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.eclipse.tractusx.traceability.integration.common.support.ISO8601DateTimeMatcher.isIso8601DateTime;

class ReadInvestigationsControllerIT extends IntegrationTestSpecification {

    @Autowired
    BpnSupport bpnSupport;
    @Autowired
    InvestigationNotificationsSupport investigationNotificationsSupport;
    @Autowired
    InvestigationsSupport investigationsSupport;

    @Test
    void shouldNotReturnInvestigationWithoutAuthentication() {
        given()
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldNotReturnInvestigationWithIdWithoutAuthentication() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/notifications/123")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldReturnNoInvestigations() throws JoseException {
        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, List.of()), new SearchCriteriaRequestParam(List.of())))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(0));
    }

    @Test
    void givenInvestigations_whenGetSenderInvestigationsSortedAsc_thenReturnProperlySorted() throws JoseException {
        // given
        String filterString = "channel,EQUAL,SENDER,AND";
        String sortString = "createdDate,ASC";
        investigationNotificationsSupport.defaultInvestigationsStored();

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, List.of(sortString)), new SearchCriteriaRequestParam(List.of(filterString))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(8))
                .body("content", Matchers.hasSize(8))
                .body("content.description", Matchers.containsInRelativeOrder("1", "2", "3", "4", "5", "6", "7", "8"));
    }

    @Test
    void givenInvestigations_whenGetSenderInvestigationsSortedDesc_thenReturnProperlySorted() throws JoseException {
        // given
        String filterString = "channel,EQUAL,SENDER,AND";
        String sortString = "createdDate,DESC";
        investigationNotificationsSupport.defaultInvestigationsStored();

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, List.of(sortString)), new SearchCriteriaRequestParam(List.of(filterString))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(8))
                .body("content", Matchers.hasSize(8))
                .body("content.description", Matchers.containsInRelativeOrder("8", "7", "6", "5", "4", "3", "2", "1"));
    }


    @Test
    void givenSortByDescriptionProvided_whenGetInvestigations_thenReturnInvestigationsProperlySorted() throws JoseException {
        // given
        String filterString = "channel,EQUAL,SENDER,AND";
        String sortString = "description,ASC";
        investigationNotificationsSupport.defaultInvestigationsStored();

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, List.of(sortString)), new SearchCriteriaRequestParam(List.of(filterString))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(8))
                .body("content", Matchers.hasSize(8))
                .log().all()
                .body("content.description", Matchers.containsInRelativeOrder("1", "2", "3", "4", "5", "6", "7", "8"));
    }

    @Test
    void givenSortByStatusProvided_whenGetInvestigations_thenReturnInvestigationsProperlySorted() throws JoseException {
        // given
        String filterString = "channel,EQUAL,SENDER,AND";
        String sortString = "status,ASC";
        investigationNotificationsSupport.defaultInvestigationsStored();

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, List.of(sortString)), new SearchCriteriaRequestParam(List.of(filterString))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(8))
                .body("content", Matchers.hasSize(8))
                .body("content.status", Matchers.containsInRelativeOrder("ACCEPTED", "ACKNOWLEDGED", "CANCELED", "CLOSED", "CREATED", "DECLINED", "RECEIVED", "SENT"));
    }

    @Test
    void givenInvalidSort_whenGet_thenBadRequest() throws JoseException {
        // given
        String sortString = "createdDate,failure";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, List.of(sortString)), new SearchCriteriaRequestParam(List.of())))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(400)
                .body("message", Matchers.is(
                        "Invalid sort param provided sort=createdDate,failure expected format is following sort=parameter,order"
                ));
    }

    @Test
    void shouldReturnPagedInvestigations() throws JoseException {
        // given
        Instant now = Instant.now();
        String testBpn = bpnSupport.testBpn();

        IntStream.range(1, 101)
                .forEach(
                        number -> {
                            investigationsSupport.storedInvestigation(
                                    NotificationEntity.builder()
                                            .assets(Collections.emptyList())
                                            .bpn(testBpn)
                                            .type(NotificationTypeEntity.INVESTIGATION)
                                            .status(NotificationStatusBaseEntity.CREATED)
                                            .side(NotificationSideBaseEntity.SENDER)
                                            .createdDate(now)
                                            .initialReceiverBpn(testBpn)
                                            .build()
                            );
                        }
                );

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(2, 10, List.of()), new SearchCriteriaRequestParam(List.of())))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(2))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(10))
                .body("totalItems", Matchers.is(100));
    }

    @Test
    void givenNonExistingInvestigation_whenGetInvestigationById_thenReturnNotFound() throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/notifications/1234")
                .then()
                .statusCode(404)
                .body("message", Matchers.is("Notification with id: 1234 not found"));
    }

    @Test
    void shouldReturnInvestigationById() throws JoseException {
        // given
        NotificationMessageEntity storedInvestigationNotification = investigationNotificationsSupport.storeInvestigationNotification();
        NotificationEntity storedInvestigation = storedInvestigationNotification.getNotification();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/notifications/{investigationId}", storedInvestigation.getId())
                .then()
                .statusCode(200)
                .body("id", Matchers.is(storedInvestigation.getId().intValue()))
                .body("status", Matchers.is(storedInvestigation.getStatus().name()))
                .body("description", Matchers.is(storedInvestigation.getDescription()))
                .body("assetIds", Matchers.empty())
                .body("createdBy", Matchers.is(storedInvestigationNotification.getCreatedBy()))
                .body("createdByName", Matchers.is(storedInvestigationNotification.getCreatedByName()))
                .body("sendTo", Matchers.is("TESTBPN"))
                .body("sendToName", Matchers.is(storedInvestigationNotification.getSendToName()))
                .body("createdDate", isIso8601DateTime());
    }

    @Test
    void givenNonExistingSortField_whenGetInvestigations_thenBadRequest() throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, List.of("nonExistingField,ASC")), new SearchCriteriaRequestParam(List.of())))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(400);
    }
}
