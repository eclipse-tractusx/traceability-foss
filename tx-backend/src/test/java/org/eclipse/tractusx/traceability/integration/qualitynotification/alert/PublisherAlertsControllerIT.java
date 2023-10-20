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
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.common.security.JwtRole;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AlertNotificationsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.AlertsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.qualitynotification.domain.alert.service.AlertsReceiverService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationType;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotification;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotificationFactory;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import qualitynotification.base.request.CloseQualityNotificationRequest;
import qualitynotification.base.request.QualityNotificationSeverityRequest;
import qualitynotification.base.request.StartQualityNotificationRequest;
import qualitynotification.base.request.UpdateQualityNotificationRequest;
import qualitynotification.base.request.UpdateQualityNotificationStatusRequest;

import java.time.Instant;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;

class PublisherAlertsControllerIT extends IntegrationTestSpecification {

    ObjectMapper objectMapper;
    @Autowired
    AlertsReceiverService alertsReceiverService;
    @Autowired
    AlertsSupport alertsSupport;
    @Autowired
    AlertNotificationsSupport alertNotificationsSupport;
    @Autowired
    AssetsSupport assetsSupport;
    @Autowired
    AssetAsBuiltRepository assetAsBuiltRepository;
    @Autowired
    AssetAsPlannedRepository assetAsPlannedRepository;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Transactional
    @Test
    void shouldReceiveAlert() {
        // given
        assetsSupport.defaultAssetsStored();

        QualityNotificationMessage notificationBuild = QualityNotificationMessage.builder()
                .id("some-id")
                .notificationStatus(QualityNotificationStatus.SENT)
                .affectedParts(List.of(new QualityNotificationAffectedPart("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb")))
                .createdByName("bpn-a")
                .createdBy("Sender Manufacturer name")
                .sendTo("BPNL00000003AXS3")
                .sendToName("Receiver manufacturer name")
                .severity(QualityNotificationSeverity.MINOR)
                .targetDate(Instant.parse("2018-11-30T18:35:24.00Z"))
                .isInitial(false)
                .type(QualityNotificationType.ALERT)
                .messageId("messageId")
                .build();
        EDCNotification notification = EDCNotificationFactory.createEdcNotification(
                "it", notificationBuild);

        // when
        alertsReceiverService.handleNotificationReceive(notification);

        // then
        alertsSupport.assertAlertsSize(1);
        alertNotificationsSupport.assertAlertNotificationsSize(1);
    }

    @Test
    void shouldStartAlert() throws JsonProcessingException, JoseException {
        // given
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        );
        String description = "at least 15 characters long investigation description";
        QualityNotificationSeverityRequest severity = QualityNotificationSeverityRequest.MINOR;
        String receiverBpn = "BPN";

        assetsSupport.defaultAssetsStored();

        val request = StartQualityNotificationRequest.builder()
                .partIds(partIds)
                .description(description)
                .severity(severity)
                .receiverBpn(receiverBpn)
                .isAsBuilt(true)
                .build();

        // when
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts")
                .then()
                .statusCode(201)
                .body("id", Matchers.isA(Number.class));

        partIds.forEach(
                partId -> {
                    AssetBase asset = assetAsBuiltRepository.getAssetById(partId);
                    assertThat(asset).isNotNull();
                    assertThat(asset.isActiveAlert()).isTrue();
                }
        );

        alertNotificationsSupport.assertAlertNotificationsSize(1);

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1));
    }

    @Test
    void shouldStartAlertForAsPlanned() throws JsonProcessingException, JoseException {
        // given
        List<String> partIds = List.of(
                "urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01"  // BPN: BPNL00000003CML1
        );
        String description = "at least 15 characters long investigation description";
        QualityNotificationSeverityRequest severity = QualityNotificationSeverityRequest.MINOR;
        String receiverBpn = "BPN";

        assetsSupport.defaultAssetsAsPlannedStored();

        val request = StartQualityNotificationRequest.builder()
                .partIds(partIds)
                .description(description)
                .severity(severity)
                .receiverBpn(receiverBpn)
                .isAsBuilt(false)
                .build();

        // when
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts")
                .then()
                .statusCode(201)
                .body("id", Matchers.isA(Number.class));

        partIds.forEach(
                partId -> {
                    AssetBase asset = assetAsPlannedRepository.getAssetById(partId);
                    assertThat(asset).isNotNull();
                    assertThat(asset.isActiveAlert()).isTrue();
                }
        );

        alertNotificationsSupport.assertAlertNotificationsSize(1);

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1));
    }

    @Test
    void givenMissingSeverity_whenStartAlert_thenBadRequest() throws JsonProcessingException, JoseException {
        // given
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        );
        String description = "at least 15 characters long investigation description";
        val request = StartQualityNotificationRequest.builder()
                .partIds(partIds)
                .description(description)
                .build();

        // when/then
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts")
                .then()
                .statusCode(400);
    }

    @Test
    void givenDescriptionOverMaxLength_whenStartAlert_thenBadRequest() throws JsonProcessingException, JoseException {
        // given
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        );

        String description = RandomStringUtils.random(1001);

        val request = StartQualityNotificationRequest.builder()
                .partIds(partIds)
                .description(description)
                .severity(QualityNotificationSeverityRequest.MINOR)
                .receiverBpn("BPN")
                .build();

        // when/then
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts")
                .then()
                .statusCode(400)
                .body(Matchers.containsString("Description should have at least 15 characters and at most 1000 characters"));
    }

    @Test
    void givenTooLongAlertReason_whenUpdateAlert_thenBadRequest() throws JsonProcessingException, JoseException {
        // given
        String description = RandomStringUtils.random(1001);
        val request = new UpdateQualityNotificationRequest();
        request.setStatus(UpdateQualityNotificationStatusRequest.ACCEPTED);
        request.setReason(description);

        // when/then
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .header(oAuth2Support.jwtAuthorization(JwtRole.SUPERVISOR))
                .when()
                .post("/api/alerts/1/update")
                .then()
                .statusCode(400)
                .body(Matchers.containsString("Reason should have at least 15 characters and at most 1000 characters"));
    }

    @Test
    void givenWrongStatus_whenUpdateAlert_thenBadRequest() throws JsonProcessingException, JoseException {
        // given
        String description = RandomStringUtils.random(15);
        val request = new UpdateQualityNotificationRequest();
        request.setStatus(UpdateQualityNotificationStatusRequest.ACCEPTED);
        request.setReason(description);

        // when/then
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request)
                        .replace("ACCEPTED", "wrongStatus")
                )
                .header(oAuth2Support.jwtAuthorization(JwtRole.SUPERVISOR))
                .when()
                .post("/api/alerts/1/update")
                .then()
                .statusCode(400)
                .body(Matchers.containsString("message\":\"NoSuchElementException: Unsupported UpdateInvestigationStatus: wrongStatus. Must be one of: ACKNOWLEDGED, ACCEPTED, DECLINED"));
    }

    @Test
    void shouldCancelAlert() throws JsonProcessingException, JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        val startAlertRequest = StartQualityNotificationRequest.builder()
                .partIds(List.of("urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978"))
                .description("at least 15 characters long investigation description")
                .severity(QualityNotificationSeverityRequest.MAJOR)
                .receiverBpn("BPN")
                .isAsBuilt(true)
                .build();

        val alertId = given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(startAlertRequest))
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1));

        // when
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/$alertId/cancel".replace("$alertId", alertId.toString()))
                .then()
                .statusCode(204);

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1));
    }

    @Test
    void shouldApproveAlertStatus() throws JsonProcessingException, JoseException {
        // given
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        );
        String description = "at least 15 characters long investigation description";

        assetsSupport.defaultAssetsStored();

        val startAlertRequest = StartQualityNotificationRequest.builder()
                .partIds(partIds)
                .description(description)
                .severity(QualityNotificationSeverityRequest.MINOR)
                .receiverBpn("BPN")
                .isAsBuilt(true)
                .build();

        // when
        var alertId = given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(startAlertRequest))
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts")
                .then()
                .statusCode(201)
                .extract().path("id");

        alertsSupport.assertAlertsSize(1);

        given()
                .contentType(ContentType.JSON)
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts/$alertId/approve".replace("$alertId", alertId.toString()))
                .then()
                .statusCode(204);

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
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
                .body("content[0].sendTo", Matchers.is(Matchers.not(Matchers.blankOrNullString())));
    }

    @Test
    void shouldCloseAlertStatus() throws JsonProcessingException, JoseException {
        // given
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978" // BPN: BPNL00000003AYRE
        );
        String description = "at least 15 characters long investigation description";

        assetsSupport.defaultAssetsStored();

        val startAlertRequest = StartQualityNotificationRequest.builder()
                .partIds(partIds)
                .description(description)
                .severity(QualityNotificationSeverityRequest.MINOR)
                .receiverBpn("BPN")
                .isAsBuilt(true)
                .build();

        // when
        val alertId = given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(startAlertRequest))
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts")
                .then()
                .statusCode(201)
                .extract().path("id");

        // then
        alertsSupport.assertAlertsSize(1);

        // when
        given()
                .contentType(ContentType.JSON)
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts/$alertId/approve".replace("$alertId", alertId.toString()))
                .then()
                .statusCode(204);

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
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
                .body("content[0].sendTo", Matchers.is(Matchers.not(Matchers.blankOrNullString())));
        // when
        var closeAlertRequest = new CloseQualityNotificationRequest();
        closeAlertRequest.setReason("this is the close reason for that investigation");
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(closeAlertRequest))
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts/$alertId/close".replace("$alertId", alertId.toString()))
                .then()
                .statusCode(204);

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1));

        alertsSupport.assertAlertsSize(1);
        alertsSupport.assertAlertStatus(QualityNotificationStatus.CLOSED);
    }

    @Test
    void givenNonExistingAlert_whenCancel_thenReturnNotFound() throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/1/cancel")
                .then()
                .statusCode(404)
                .body("message", Matchers.is("Alert not found for 1 id"));
    }

    @Test
    void givenNoAuthorization_whenCancel_thenReturn401() {
        given()
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .post("/api/alerts/1/cancel")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldBeCreatedBySender() throws JsonProcessingException, JoseException {
        // given
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        );
        String description = "at least 15 characters long investigation description";
        assetsSupport.defaultAssetsStored();
        val startAlertRequest = StartQualityNotificationRequest.builder()
                .partIds(partIds)
                .description(description)
                .severity(QualityNotificationSeverityRequest.MINOR)
                .receiverBpn("BPN")
                .isAsBuilt(true)
                .build();

        // when
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(startAlertRequest))
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/alerts")
                .then()
                .statusCode(201)
                .body("id", Matchers.isA(Number.class));

        // then
        partIds.forEach(partId -> {
            AssetBase asset = assetAsBuiltRepository.getAssetById(partId);
            assertThat(asset).isNotNull();
            assertThat(asset.isActiveAlert()).isTrue();
        });

        alertNotificationsSupport.assertAlertNotificationsSize(1);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1));
    }
}
