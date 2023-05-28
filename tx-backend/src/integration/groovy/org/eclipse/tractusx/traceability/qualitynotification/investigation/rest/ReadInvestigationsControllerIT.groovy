/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.qualitynotification.investigation.rest

import io.restassured.http.ContentType
import org.eclipse.tractusx.traceability.IntegrationSpecification
import org.eclipse.tractusx.traceability.common.support.BpnSupport
import org.eclipse.tractusx.traceability.common.support.InvestigationsSupport
import org.eclipse.tractusx.traceability.common.support.NotificationsSupport
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationEntity
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationNotificationEntity
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationSideBaseEntity
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationStatusBaseEntity
import org.hamcrest.Matchers
import spock.lang.Unroll

import java.time.Instant

import static io.restassured.RestAssured.given
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN
import static org.eclipse.tractusx.traceability.common.support.ISO8601DateTimeMatcher.isIso8601DateTime

class ReadInvestigationsControllerIT extends IntegrationSpecification implements InvestigationsSupport, NotificationsSupport, BpnSupport {

    @Unroll
    def "should not return #type investigations without authentication"() {
        expect:
        given()
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/$type")
                .then()
                .statusCode(401)
        where:
        type << ["created", "received"]
    }

    def "should not return investigation without authentication"() {
        expect:
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/123")
                .then()
                .statusCode(401)
    }

    def "should return no investigations"() {
        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/$type")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(0))

        where:
        type << ["created", "received"]
    }

    def "should return created investigations sorted by creation time"() {
        given:
        Instant now = Instant.now()
        String testBpn = testBpn()

        and:
        InvestigationEntity firstInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.CREATED)
                .side(QualityNotificationSideBaseEntity.SENDER)
                .description("1")
                .created(now.minusSeconds(10L))
                .build();
        InvestigationEntity secondInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.CREATED)
                .description("2")
                .side(QualityNotificationSideBaseEntity.SENDER)
                .created(now.plusSeconds(21L))
                .build();
        InvestigationEntity thirdInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.CREATED)
                .description("3")
                .side(QualityNotificationSideBaseEntity.SENDER)
                .created(now)
                .build();
        InvestigationEntity fourthInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.CREATED)
                .description("4")
                .side(QualityNotificationSideBaseEntity.SENDER)
                .created(now.plusSeconds(20L))
                .build();
        InvestigationEntity fifthInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.CREATED)
                .description("5")
                .side(QualityNotificationSideBaseEntity.RECEIVER)
                .created(now.plusSeconds(40L))
                .build();

        and:

        storedNotifications(
                InvestigationNotificationEntity
                        .builder()
                        .id("1")
                        .investigation(firstInvestigation)
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .id("2")
                        .investigation(secondInvestigation)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .id("3")
                        .investigation(thirdInvestigation)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .id("4")
                        .investigation(fourthInvestigation)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .id("5")
                        .investigation(fifthInvestigation)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build()
        )

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(4))
                .body("totalItems", Matchers.is(4))
    }

    def "should return properly paged created investigations"() {
        given:
        Instant now = Instant.now()
        String testBpn = testBpn()

        and:
        (1..100).each { it ->

            InvestigationEntity investigationEntity = InvestigationEntity.builder()
                    .assets(Collections.emptyList())
                    .bpn(testBpn)
                    .status(QualityNotificationStatusBaseEntity.CREATED)
                    .side(QualityNotificationSideBaseEntity.SENDER)
                    .created(now)
                    .build();
            storedInvestigation(investigationEntity)
        }

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .param("page", "2")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(2))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(10))
                .body("totalItems", Matchers.is(100))
    }

    def "should return properly paged received investigations"() {
        given:
        Instant now = Instant.now()
        String testBpn = testBpn()
        String senderBPN = "BPN0001"
        String senderName = "Sender name"
        String receiverBPN = "BPN0002"
        String receiverName = "Receiver name"
        and:
        (101..200).each { it ->
            InvestigationEntity investigationEntity = InvestigationEntity.builder()
                    .assets(Collections.emptyList())
                    .bpn(testBpn)
                    .status(QualityNotificationStatusBaseEntity.CREATED)
                    .side(QualityNotificationSideBaseEntity.RECEIVER)
                    .created(now)
                    .build();

            InvestigationEntity investigation = storedInvestigationFullObject(investigationEntity)

            InvestigationNotificationEntity notificationEntity = InvestigationNotificationEntity
                    .builder()
                    .id(UUID.randomUUID().toString())
                    .investigation(investigation)
                    .senderBpnNumber(senderBPN)
                    .status(QualityNotificationStatusBaseEntity.CREATED)
                    .senderManufacturerName(senderName)
                    .receiverBpnNumber(receiverBPN)
                    .receiverManufacturerName(receiverName)
                    .messageId("messageId")
                    .build()

            InvestigationNotificationEntity persistedNotification = storedNotification(notificationEntity)
            persistedNotification.setInvestigation(investigation);
            storedNotification(persistedNotification)
        }

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .param("page", "2")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/received")
                .then()
                .statusCode(200)
                .body("content.createdBy", Matchers.hasItems(senderBPN))
                .body("content.createdByName", Matchers.hasItems(senderName))
                .body("content.sendTo", Matchers.hasItems(receiverBPN))
                .body("content.sendToName", Matchers.hasItems(receiverName))
                .body("page", Matchers.is(2))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(10))
                .body("totalItems", Matchers.is(100))
    }

    def "should return received investigations sorted by creation time"() {
        given:
        Instant now = Instant.now()
        String testBpn = testBpn()

        and:
        InvestigationEntity firstInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.RECEIVED)
                .side(QualityNotificationSideBaseEntity.RECEIVER)
                .description("1")
                .created(now.minusSeconds(5L))
                .build();
        InvestigationEntity secondInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.RECEIVED)
                .description("2")
                .side(QualityNotificationSideBaseEntity.RECEIVER)
                .created(now.plusSeconds(2L))
                .build();
        InvestigationEntity thirdInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.RECEIVED)
                .description("3")
                .side(QualityNotificationSideBaseEntity.RECEIVER)
                .created(now)
                .build();
        InvestigationEntity fourthInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.RECEIVED)
                .description("4")
                .side(QualityNotificationSideBaseEntity.RECEIVER)
                .created(now.plusSeconds(20L))
                .build();
        InvestigationEntity fifthInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(testBpn)
                .status(QualityNotificationStatusBaseEntity.CREATED)
                .description("5")
                .side(QualityNotificationSideBaseEntity.SENDER)
                .created(now.plusSeconds(40L))
                .build();

        and:
        storedNotifications(
                InvestigationNotificationEntity
                        .builder()
                        .id("1")
                        .investigation(firstInvestigation)
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("2")
                        .investigation(secondInvestigation)
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("3")
                        .investigation(thirdInvestigation)
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("4")
                        .investigation(fourthInvestigation)
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("5")
                        .investigation(fifthInvestigation)
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .build()
        )

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/received")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(4))
                .body("totalItems", Matchers.is(4))
                .body("content.description", Matchers.containsInRelativeOrder("4", "2", "3", "1"))
                .body("content.createdDate", Matchers.hasItems(isIso8601DateTime()))
    }

    def "should not find non existing investigation"() {
        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/1234")
                .then()
                .statusCode(404)
                .body("message", Matchers.is("Investigation not found for 1234 id"))
    }

    def "should return investigation by id"() {
        given:
        String testBpn = testBpn()
        String senderBPN = "BPN0001"
        String senderName = "Sender name"
        String receiverBPN = "BPN0002"
        String receiverName = "Receiver name"

        and:
        InvestigationEntity investigationEntity =
                InvestigationEntity
                        .builder()
                        .id(1L)
                        .assets([])
                        .bpn(testBpn)
                        .description("1")
                        .status(QualityNotificationStatusBaseEntity.RECEIVED)
                        .side(QualityNotificationSideBaseEntity.SENDER)
                        .created(Instant.now())
                        .build();

        InvestigationEntity persistedInvestigation = storedInvestigationFullObject(investigationEntity)
        and:
        InvestigationNotificationEntity notificationEntity = storedNotification(
                InvestigationNotificationEntity
                        .builder()
                        .id("1")
                        .investigation(persistedInvestigation)
                        .senderBpnNumber(senderBPN)
                        .senderManufacturerName(senderName)
                        .receiverBpnNumber(receiverBPN)
                        .status(QualityNotificationStatusBaseEntity.CREATED)
                        .receiverManufacturerName(receiverName)
                        .build())
        notificationEntity.setInvestigation(persistedInvestigation)
        storedNotification(notificationEntity)
        and:
        Long investigationId = persistedInvestigation.getId()

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/$investigationId")
                .then()
                .statusCode(200)
                .body("id", Matchers.is(investigationId.toInteger()))
                .body("status", Matchers.is("RECEIVED"))
                .body("description", Matchers.is("1"))
                .body("assetIds", Matchers.empty())
                .body("createdBy", Matchers.is(senderBPN))
                .body("createdByName", Matchers.is(senderName))
                .body("sendTo", Matchers.is(receiverBPN))
                .body("sendToName", Matchers.is(receiverName))
                .body("createdDate", isIso8601DateTime())
    }
}
