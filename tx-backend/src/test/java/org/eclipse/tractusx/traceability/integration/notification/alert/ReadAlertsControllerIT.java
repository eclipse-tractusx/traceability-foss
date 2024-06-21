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

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.PageableFilterRequest;
import org.eclipse.tractusx.traceability.common.request.SearchCriteriaRequestParam;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AlertNotificationsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.AlertsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
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
import java.util.UUID;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.eclipse.tractusx.traceability.integration.common.support.ISO8601DateTimeMatcher.isIso8601DateTime;

class ReadAlertsControllerIT extends IntegrationTestSpecification {

    @Autowired
    AlertsSupport alertsSupport;

    @Autowired
    AlertNotificationsSupport alertNotificationsSupport;

    @Autowired
    BpnSupport bpnSupport;

    @Test
    void shouldNotReturnAlertWithoutAuthentication() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/notifications/123")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldNotReturnAlertsWithoutAuthentication() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(401);
    }


    @Test
    void shouldReturnNoAlerts() throws JoseException {
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
    void givenAlerts_whenGetSenderAlertsSortedAsc_thenReturnProperlySorted() throws JoseException {
        // given
        String filterString = "channel,EQUAL,SENDER,AND";
        String sortString = "createdDate,ASC";
        alertNotificationsSupport.defaultAlertsStored();

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
    void givenSortByDescriptionProvided_whenGetInvestigations_thenReturnInvestigationsProperlySorted() throws JoseException {
        // given
        String filterString = "channel,EQUAL,SENDER,AND";
        String sortString = "description,ASC";
        alertNotificationsSupport.defaultAlertsStored();

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
    void givenSortByStatusProvided_whenGetInvestigations_thenReturnInvestigationsProperlySorted() throws JoseException {
        // given
        String filterString = "channel,EQUAL,SENDER,AND";
        String sortString = "status,ASC";
        alertNotificationsSupport.defaultAlertsStored();

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
    void shouldReturnProperlyPagedReceivedAlerts() throws JoseException {
        // given
        Instant now = Instant.now();
        String testBpn = bpnSupport.testBpn();
        String senderBPN = "BPN0001";
        String senderName = "Sender name";
        String receiverBPN = "BPN0002";
        String receiverName = "Receiver name";
        String filterString = "channel,EQUAL,RECEIVER,AND";

        IntStream.range(101, 201)
                .forEach(number ->
                        {
                            NotificationEntity alertNotificationEntity = NotificationEntity.builder()
                                    .assets(Collections.emptyList())
                                    .bpn(testBpn)
                                    .status(NotificationStatusBaseEntity.CREATED)
                                    .side(NotificationSideBaseEntity.RECEIVER)
                                    .type(NotificationTypeEntity.ALERT)
                                    .createdDate(now)
                                    .initialReceiverBpn(senderBPN)
                                    .build();

                            NotificationEntity alert = alertsSupport.storedAlertFullObject(alertNotificationEntity);

                            NotificationMessageEntity notificationEntity = NotificationMessageEntity
                                    .builder()
                                    .id(UUID.randomUUID().toString())
                                    .notification(alert)
                                    .createdBy(senderBPN)
                                    .status(NotificationStatusBaseEntity.CREATED)
                                    .createdByName(senderName)
                                    .sendTo(receiverBPN)
                                    .sendToName(receiverName)
                                    .messageId("messageId")
                                    .build();

                            NotificationMessageEntity persistedNotification = alertNotificationsSupport.storedAlertNotification(notificationEntity);
                            persistedNotification.setNotification(alert);
                            alertNotificationsSupport.storedAlertNotification(persistedNotification);
                        }
                );

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(2, 10, List.of()), new SearchCriteriaRequestParam(List.of(filterString))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("content.createdBy", Matchers.hasItems(senderBPN))
                .body("content.createdByName", Matchers.hasItems(senderName))
                .body("content.sendTo", Matchers.hasItems("BPN0001"))
                .body("content.sendToName", Matchers.hasItems(receiverName))
                .body("page", Matchers.is(2))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(10))
                .body("totalItems", Matchers.is(100));
    }

    @Test
    void givenNoAlertId_whenGetAlertById_thenReturnNotFound() throws JoseException {
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
        NotificationMessageEntity storedAlertNotification = alertNotificationsSupport.storeAlertNotification();
        NotificationEntity storedAlert = storedAlertNotification.getNotification();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/notifications/{alertId}", storedAlert.getId())
                .then()
                .statusCode(200)
                .body("id", Matchers.is(storedAlert.getId().intValue()))
                .body("status", Matchers.is(storedAlert.getStatus().name()))
                .body("description", Matchers.is(storedAlert.getDescription()))
                .body("assetIds", Matchers.empty())
                .body("createdBy", Matchers.is(storedAlertNotification.getCreatedBy()))
                .body("createdByName", Matchers.is(storedAlertNotification.getCreatedByName()))
                .body("sendTo", Matchers.is("BPNTEST"))
                .body("sendToName", Matchers.is(storedAlertNotification.getSendToName()))
                .body("createdDate", isIso8601DateTime());
    }

    @Test
    void givenNonExistingSortField_whenGetAlerts_thenBadRequest() throws JoseException {
        //GIVEN
        String sortString = "nonExistingField,ASC";

        //WHEN
        //THEN
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, List.of(sortString)), new SearchCriteriaRequestParam(List.of())))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(400);
    }
}
