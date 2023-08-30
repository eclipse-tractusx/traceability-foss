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

package org.eclipse.tractusx.traceability.qualitynotification.alert.rest

import io.restassured.http.ContentType
import org.apache.commons.lang3.RandomStringUtils
import org.eclipse.tractusx.traceability.IntegrationSpecification
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase
import org.eclipse.tractusx.traceability.common.security.JwtRole
import org.eclipse.tractusx.traceability.common.support.*
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotificationFactory
import org.eclipse.tractusx.traceability.qualitynotification.domain.alert.service.AlertsReceiverService
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.*
import org.hamcrest.Matchers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import spock.lang.Unroll

import java.time.Instant

import static io.restassured.RestAssured.given
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN

class PublisherAlertsControllerIT extends IntegrationSpecification implements IrsApiSupport, AssetsSupport, InvestigationsSupport, AlertsSupport, InvestigationNotificationsSupport, AlertNotificationsSupport, BpnSupport {
    @Autowired
    AlertsReceiverService alertsReceiverService

    @Transactional
    def "should receive alert"() {
        given:
        defaultAssetsStored()

        and:
        QualityNotificationMessage notificationBuild = QualityNotificationMessage.builder()
                .id("some-id")
                .notificationStatus(QualityNotificationStatus.SENT)
                .affectedParts(List.of(new QualityNotificationAffectedPart("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb")))
                .senderManufacturerName("bpn-a")
                .senderBpnNumber("Sender Manufacturer name")
                .receiverBpnNumber("BPNL00000003AXS3")
                .receiverManufacturerName("Receiver manufacturer name")
                .severity(QualityNotificationSeverity.MINOR)
                .isInitial(false)
                .targetDate(Instant.parse("2018-11-30T18:35:24.00Z"))
                .isInitial(false)
                .type(QualityNotificationType.ALERT)
                .messageId("messageId")
                .build();
        EDCNotification notification = EDCNotificationFactory.createEdcNotification(
                "it", notificationBuild)

        when:
        alertsReceiverService.handleNotificationReceive(notification)

        then:
        assertAlertsSize(1)
        assertAlertNotificationsSize(1)
    }

    def "should start alert"() {
        given:
        List<String> partIds = [
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        ]
        String description = "at least 15 characters long investigation description"
        String severity = "MINOR"
        String bpn = "BPN"
        and:
        defaultAssetsStored()

        when:
        given()
                .contentType(ContentType.JSON)
                .body(
                        asJson(
                                [
                                        partIds    : partIds,
                                        description: description,
                                        severity   : severity,
                                        bpn        : bpn
                                ]
                        )
                )
                .header(jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts")
                .then()
                .statusCode(201)
                .body("id", Matchers.isA(Number.class))

        then:
        partIds.each { partId ->
            AssetBase asset = assetAsBuiltRepository().getAssetById(partId)
            assert asset
            assert asset.isActiveAlert()
        }

        and:
        assertAlertNotificationsSize(1)

        and:
        given()
                .header(jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
    }

    def "should throw bad request on start alert missing required parameter severity"() {
        given:
        List<String> partIds = [
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        ]
        String description = "at least 15 characters long investigation description"

        expect:
        given()
                .contentType(ContentType.JSON)
                .body(
                        asJson(
                                [
                                        partIds    : partIds,
                                        description: description
                                ]
                        )
                )
                .header(jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts")
                .then()
                .statusCode(400)
    }

    def "should throw bad request on start alert description exceeds maximum length"() {
        given:
        List<String> partIds = [
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        ]

        String description = RandomStringUtils.random(1001);

        expect:
        given()
                .contentType(ContentType.JSON)
                .body(
                        asJson(
                                [
                                        partIds    : partIds,
                                        description: description,
                                        severity   : "MINOR",
                                        bpn        : "BPN"
                                ]
                        )
                )
                .header(jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts")
                .then()
                .statusCode(400)
                .body(Matchers.containsString("Description should have at least 15 characters and at most 1000 characters"))
    }

    def "should throw bad request on update alert reason too long"() {
        given:
        String description = RandomStringUtils.random(1001);

        expect:
        given()
                .contentType(ContentType.JSON)
                .body(
                        asJson(
                                [
                                        status: "ACCEPTED",
                                        reason: description
                                ]
                        )
                )
                .header(jwtAuthorization(JwtRole.SUPERVISOR))
                .when()
                .post("/api/alerts/1/update")
                .then()
                .statusCode(400)
                .body(Matchers.containsString("Reason should have at least 15 characters and at most 1000 characters"))
    }

    def "should throw bad request on update alert wrong status"() {
        given:
        String description = RandomStringUtils.random(15);

        expect:
        given()
                .contentType(ContentType.JSON)
                .body(
                        asJson(
                                [
                                        status: "anything",
                                        reason: description
                                ]
                        )
                )
                .header(jwtAuthorization(JwtRole.SUPERVISOR))
                .when()
                .post("/api/alerts/1/update")
                .then()
                .statusCode(400)
                .body(Matchers.containsString("message\":\"NoSuchElementException: Unsupported UpdateInvestigationStatus: anything. Must be one of: ACKNOWLEDGED, ACCEPTED, DECLINED"))
    }

    def "should cancel alert"() {
        given:
        defaultAssetsStored()

        and:
        def alertId = given()
                .contentType(ContentType.JSON)
                .body(
                        asJson(
                                [
                                        partIds    : ["urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978"],
                                        description: "at least 15 characters long investigation description",
                                        severity   : "MAJOR",
                                        bpn        : "BPN"
                                ]
                        )
                )
                .header(jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts")
                .then()
                .statusCode(201)
                .extract().path("id")

        and:
        given()
                .header(jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/$alertId/cancel")
                .then()
                .statusCode(204)

        and:
        given()
                .header(jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
    }

    def "should approve alert status"() {
        given:
        List<String> partIds = [
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        ]
        String description = "at least 15 characters long investigation description"

        String severity = "MINOR"
        and:
        defaultAssetsStored()

        when:
        def alertId = given()
                .contentType(ContentType.JSON)
                .body(asJson([
                        partIds    : partIds,
                        description: description,
                        severity   : severity,
                        bpn        : "BPN"
                ]))
                .header(jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts")
                .then()
                .statusCode(201)
                .extract().path("id")

        then:
        assertAlertsSize(1)

        when:
        given()
                .contentType(ContentType.JSON)
                .header(jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts/$alertId/approve")
                .then()
                .statusCode(204)

        then:
        given()
                .header(jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("content[0].sendTo", Matchers.is(Matchers.not(Matchers.blankOrNullString())))
    }

    def "should close alert status"() {
        given:
        List<String> partIds = [
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978" // BPN: BPNL00000003AYRE
        ]
        String description = "at least 15 characters long investigation description"
        String severity = "MINOR"
        and:
        defaultAssetsStored()

        when:
        def alertId = given()
                .contentType(ContentType.JSON)
                .body(asJson([
                        partIds    : partIds,
                        description: description,
                        severity   : severity,
                        bpn        : "BPN"
                ]))
                .header(jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts")
                .then()
                .statusCode(201)
                .extract().path("id")

        then:
        assertAlertsSize(1)

        when:
        given()
                .contentType(ContentType.JSON)
                .header(jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts/$alertId/approve")
                .then()
                .statusCode(204)

        then:
        given()
                .header(jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("content[0].sendTo", Matchers.is(Matchers.not(Matchers.blankOrNullString())))
        when:
        given()
                .contentType(ContentType.JSON)
                .body(asJson([
                        reason: "this is the close reason for that investigation"
                ]))
                .header(jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts/$alertId/close")
                .then()
                .statusCode(204)

        then:
        given()
                .header(jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))

        then:
        assertAlertsSize(1)
        assertAlertStatus(QualityNotificationStatus.CLOSED)
    }

    def "should not cancel not existing alert"() {
        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/1/cancel")
                .then()
                .statusCode(404)
                .body("message", Matchers.is("Alert not found for 1 id"))
    }

    @Unroll
    def "should not #action alerts without authentication"() {
        expect:
        given()
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/1/cancel")
                .then()
                .statusCode(401)

        where:
        action << ["approve", "cancel", "close"]
    }

    def "should be created by sender"() {
        given:
        List<String> partIds = [
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        ]
        String description = "at least 15 characters long investigation description"
        String severity = "MINOR"
        and:
        defaultAssetsStored()
        when:
        given()
                .contentType(ContentType.JSON)
                .body(
                        asJson(
                                [
                                        partIds    : partIds,
                                        description: description,
                                        severity   : severity,
                                        bpn        : "BPN"
                                ]
                        )
                )
                .header(jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts")
                .then()
                .statusCode(201)
                .body("id", Matchers.isA(Number.class))
        then:
        partIds.each { partId ->
            AssetBase asset = assetAsBuiltRepository().getAssetById(partId)
            assert asset
            assert asset.isActiveAlert()
        }
        and:
        assertAlertNotificationsSize(1)
        and:
        given()
                .header(jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
    }

}

