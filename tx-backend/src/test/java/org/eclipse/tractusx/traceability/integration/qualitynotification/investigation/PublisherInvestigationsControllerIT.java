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

package org.eclipse.tractusx.traceability.integration.qualitynotification.investigation;

import io.restassured.http.ContentType;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.common.security.JwtRole;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationNotificationsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationsSupport;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationType;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.service.InvestigationsReceiverService;
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

class PublisherInvestigationsControllerIT extends IntegrationTestSpecification {

    @Autowired
    InvestigationsReceiverService investigationsReceiverService;
    @Autowired
    InvestigationsSupport investigationsSupport;
    @Autowired
    AssetsSupport assetsSupport;
    @Autowired
    InvestigationNotificationsSupport investigationNotificationsSupport;
    @Autowired
    AssetAsBuiltRepository assetAsBuiltRepository;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Transactional
    @Test
    void shouldReceiveNotification() {
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
                .isInitial(false)
                .targetDate(Instant.parse("2018-11-30T18:35:24.00Z"))
                .isInitial(false)
                .type(QualityNotificationType.INVESTIGATION)
                .messageId("messageId")
                .build();
        EDCNotification notification = EDCNotificationFactory.createEdcNotification(
                "it", notificationBuild);

        // when
        investigationsReceiverService.handleNotificationReceive(notification);

        // then
        investigationsSupport.assertInvestigationsSize(1);
        investigationNotificationsSupport.assertNotificationsSize(1);
    }

    @Test
    void shouldStartInvestigation() throws JsonProcessingException, JoseException {
        // given
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        );
        String description = "at least 15 characters long investigation description";

        assetsSupport.defaultAssetsStored();

        val request = StartQualityNotificationRequest.builder()
                .partIds(partIds)
                .description(description)
                .severity(QualityNotificationSeverityRequest.MINOR)
                .isAsBuilt(true)
                .build();

        // when
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/investigations")
                .then()
                .statusCode(201)
                .body("id", Matchers.isA(Number.class));

        // then
        partIds.forEach(partId -> {
            AssetBase asset = assetAsBuiltRepository.getAssetById(partId);
            assertThat(asset).isNotNull();
            assertThat(asset.isUnderInvestigation()).isTrue();
        });

        investigationNotificationsSupport.assertNotificationsSize(2);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1));
    }

    @Test
    void givenMissingSeverity_whenStartInvestigation_thenBadRequest() throws JsonProcessingException, JoseException {
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
                .post("/api/investigations")
                .then()
                .statusCode(400);
    }

    @Test
    void givenDescriptionExceedsMaxLength_whenStartInvestigation_thenBadRequest() throws JsonProcessingException, JoseException {
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
                .build();

        // when/then
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/investigations")
                .then()
                .statusCode(400)
                .body(Matchers.containsString("Description should have at least 15 characters and at most 1000 characters"));
    }

    @Test
    void givenInvestigationReasonTooLong_whenUpdate_thenBadRequest() throws JsonProcessingException, JoseException {
        // given
        String description = RandomStringUtils.random(1001);
        val request = new UpdateQualityNotificationRequest();
        request.setReason(description);
        request.setStatus(UpdateQualityNotificationStatusRequest.ACCEPTED);

        // when/then
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .header(oAuth2Support.jwtAuthorization(JwtRole.SUPERVISOR))
                .when()
                .post("/api/investigations/1/update")
                .then()
                .statusCode(400)
                .body(Matchers.containsString("Reason should have at least 15 characters and at most 1000 characters"));
    }

    @Test
    void givenWrongStatus_whenUpdateInvestigation_thenBadRequest() throws JsonProcessingException, JoseException {
        // given
        String description = RandomStringUtils.random(15);
        val request = new UpdateQualityNotificationRequest();
        request.setStatus(UpdateQualityNotificationStatusRequest.ACCEPTED);
        request.setReason(description);

        // when/then
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request)
                        .replace("ACCEPTED", "wrongStatus"))
                .header(oAuth2Support.jwtAuthorization(JwtRole.SUPERVISOR))
                .when()
                .post("/api/investigations/1/update")
                .then()
                .statusCode(400)
                .body(Matchers.containsString("message\":\"NoSuchElementException: Unsupported UpdateInvestigationStatus: wrongStatus. Must be one of: ACKNOWLEDGED, ACCEPTED, DECLINED"));
    }

    @Test
    void shouldCancelInvestigation() throws JsonProcessingException, JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        val startInvestigationRequest = StartQualityNotificationRequest.builder()
                .partIds(List.of("urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978"))
                .description("at least 15 characters long investigation description")
                .severity(QualityNotificationSeverityRequest.MAJOR)
                .isAsBuilt(true)
                .build();

        val investigationId = given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(startInvestigationRequest))
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/investigations")
                .then()
                .statusCode(201)
                .extract().path("id");

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1));
        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/investigations/$investigationId/cancel".replace("$investigationId", investigationId.toString()))
                .then()
                .statusCode(204);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1));
    }

    @Test
    void shouldApproveInvestigationStatus() throws JsonProcessingException, JoseException {
        // given
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        );
        String description = "at least 15 characters long investigation description";

        assetsSupport.defaultAssetsStored();
        val startInvestigationRequest = StartQualityNotificationRequest.builder()
                .partIds(partIds)
                .description(description)
                .severity(QualityNotificationSeverityRequest.MINOR)
                .isAsBuilt(true)
                .build();

        // when
        val investigationId = given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(startInvestigationRequest))
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/investigations")
                .then()
                .statusCode(201)
                .extract().path("id");

        investigationsSupport.assertInvestigationsSize(1);

        given()
                .contentType(ContentType.JSON)
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/investigations/{investigationId}/approve", investigationId)
                .then()
                .statusCode(204);

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("content[0].sendTo", Matchers.is(Matchers.not(Matchers.blankOrNullString())));
    }

    @Test
    void shouldCloseInvestigationStatus() throws JsonProcessingException, JoseException {
        // given
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978" // BPN: BPNL00000003AYRE
        );
        String description = "at least 15 characters long investigation description";

        assetsSupport.defaultAssetsStored();
        val startInvestigationRequest = StartQualityNotificationRequest.builder()
                .partIds(partIds)
                .description(description)
                .severity(QualityNotificationSeverityRequest.MINOR)
                .isAsBuilt(true)
                .build();

        // when
        val investigationId = given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(startInvestigationRequest))
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/investigations")
                .then()
                .statusCode(201)
                .extract().path("id");

        // then
        investigationsSupport.assertInvestigationsSize(1);

        // when
        given()
                .contentType(ContentType.JSON)
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/investigations/{investigationId}/approve", investigationId)
                .then()
                .statusCode(204);
        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("content[0].sendTo", Matchers.is(Matchers.not(Matchers.blankOrNullString())));

        // when
        val closeInvestigationRequest = new CloseQualityNotificationRequest();
        closeInvestigationRequest.setReason("this is the close reason for that investigation");
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(closeInvestigationRequest))
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/investigations/{investigationId}/close", investigationId)
                .then()
                .statusCode(204);

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1));

        investigationsSupport.assertInvestigationsSize(1);
        investigationsSupport.assertInvestigationStatus(QualityNotificationStatus.CLOSED);
    }

    @Test
    void givenNonExistingAlert_whenCancel_thenReturnNotFound() throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/investigations/1/cancel")
                .then()
                .statusCode(404)
                .body("message", Matchers.is("Investigation not found for 1 id"));
    }

    @Test
    void givenNoAuthorization_whenCancel_thenReturn401() {
        given()
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .post("/api/investigations/1/cancel")
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
        val startInvestigationRequest = StartQualityNotificationRequest.builder()
                .partIds(partIds)
                .description(description)
                .severity(QualityNotificationSeverityRequest.MINOR)
                .isAsBuilt(true)
                .build();

        // when
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(startInvestigationRequest))
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/investigations")
                .then()
                .statusCode(201)
                .body("id", Matchers.isA(Number.class));

        // then
        partIds.forEach(partId -> {
            AssetBase asset = assetAsBuiltRepository.getAssetById(partId);
            assertThat(asset).isNotNull();
            assertThat(asset.isUnderInvestigation()).isTrue();
        });

        investigationNotificationsSupport.assertNotificationsSize(2);
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1));
    }

}
