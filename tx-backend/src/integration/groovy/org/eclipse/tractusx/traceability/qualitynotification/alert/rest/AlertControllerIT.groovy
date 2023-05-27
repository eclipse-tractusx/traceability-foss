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
import org.eclipse.tractusx.traceability.IntegrationSpecification
import org.eclipse.tractusx.traceability.common.model.PageResult
import org.eclipse.tractusx.traceability.common.support.*
import org.eclipse.tractusx.traceability.qualitynotification.application.request.CloseQualityNotificationRequest
import org.eclipse.tractusx.traceability.qualitynotification.application.request.UpdateQualityNotificationRequest
import org.eclipse.tractusx.traceability.qualitynotification.application.request.UpdateQualityNotificationStatusRequest
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationId
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationSide
import org.eclipse.tractusx.traceability.testdata.InvestigationTestDataFactory
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus

import static io.restassured.RestAssured.given
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN
import static org.eclipse.tractusx.traceability.common.security.JwtRole.SUPERVISOR

class AlertControllerIT extends IntegrationSpecification implements IrsApiSupport, AssetsSupport, InvestigationsSupport, NotificationsSupport, BpnSupport {

    def "alert asset should return 400 when non existent severity is provided"() {
        given:
        String requestWithNotValidSeverity = asJson(
                [
                        partIds    : [
                                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb"
                        ],
                        description: "very bad things happened",
                        targetDate : "2099-03-11T22:44:06.333826952Z",
                        severity   : "nonExistentSeverity",
                ]
        )

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(requestWithNotValidSeverity)
                .when()
                .post("/api/alerts")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }

    def "alert asset should return 201 whenValid severity is provided"() {
        given:
        String requestWithNotValidSeverity = asJson(
                [
                        partIds    : [
                                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb"
                        ],
                        description: "very bad things happened",
                        targetDate : "2099-03-11T22:44:06.333826952Z",
                        severity   : "$severity",
                ]
        )
        1 * alertService.start(_, _, _, _) >> new QualityNotificationId(1L)

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(requestWithNotValidSeverity)
                .when()
                .post("/api/alerts")
                .then()
                .statusCode(HttpStatus.CREATED.value())

        where:
        severity << ["MINOR", "MAJOR", "CRITICAL", "LIFE-THREATENING"]
    }

    def "getCreatedAlerts should return 200 whenValid pageable is provided"() {
        given:
        String request = asJson(
                Pageable.ofSize(1)
        )
        1 * alertService.getCreated(_) >> new PageResult<QualityNotification>(List.of())

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get("/api/alerts/created")
                .then()
                .statusCode(HttpStatus.OK.value())
    }

    def "getReceivedAlerts should return 200 whenValid pageable is provided"() {
        given:
        String request = asJson(
                Pageable.ofSize(1)
        )
        1 * alertService.getReceived(_) >> new PageResult<QualityNotification>(List.of())

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .get("/api/alerts/received")
                .then()
                .statusCode(HttpStatus.OK.value())
    }

    def "getAlert should return 200 whenAlertId provided"() {
        given:
        Long alertId = 1L
        1 * alertService.find(_) >> InvestigationTestDataFactory
                .createInvestigationTestData(QualityNotificationSide.RECEIVER)

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/{alertId}"
                        .replace("{alertId}", alertId.toString()))
                .then()
                .statusCode(HttpStatus.OK.value())
    }

    def "approveAlert should return 204 whenAlertId provided"() {
        given:
        Long alertId = 1L

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/{alertId}/approve"
                        .replace("{alertId}", alertId.toString()))
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
    }

    def "cancelAlert should return 204 whenAlertId provided"() {
        given:
        Long alertId = 1L

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/{alertId}/cancel"
                        .replace("{alertId}", alertId.toString()))
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
    }

    def "close should return 400 whenAlertId provided but reason is under min size"() {
        given:
        Long alertId = 1L
        String request = asJson(
                [
                        reason: "invalid reason"
                ]
        )
        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
        .body(request)
                .when()
                .post("/api/alerts/{alertId}/close"
                        .replace("{alertId}", alertId.toString()))
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
    }
    def "close should return 204 whenAlertId provided "() {
        given:
        Long alertId = 1L
        String request = asJson(
                [
                        reason: "very valid reason provided"
                ]
        )
        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/alerts/{alertId}/close"
                        .replace("{alertId}", alertId.toString()))
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
    }

    def "updateAlert should return 204 whenAlertId provided"() {
        given:
        Long alertId = 1L
        String request = asJson(
                [
                        status: "ACCEPTED",
                        reason: "some reason description"
                ]
        )
        UpdateQualityNotificationRequest t = new UpdateQualityNotificationRequest()
        t.setStatus(UpdateQualityNotificationStatusRequest.ACCEPTED)
        t.setReason("because I can")
        String request2 = asJson(t)

        expect:
        given()
                .header(jwtAuthorization(SUPERVISOR))
                .contentType(ContentType.JSON)
                .when()
        .body(request)
                .post("/api/alerts/{alertId}/update"
                        .replace("{alertId}", alertId.toString()))
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value())
    }

    def "updateAlert should return 403 Admin user"() {
        given:
        Long alertId = 1L
        String request = asJson(
                [
                        status: "ACKNOWLEDGED",
                        reason: "some reason"
                ]
        )

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .body(request)
                .post("/api/alerts/{alertId}/update"
                        .replace("{alertId}", alertId.toString()))
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value())
    }

}
