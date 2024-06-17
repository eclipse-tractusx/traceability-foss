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


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import lombok.val;
import notification.request.CloseNotificationRequest;
import notification.request.NotificationSeverityRequest;
import notification.request.NotificationTypeRequest;
import notification.request.StartNotificationRequest;
import notification.request.UpdateNotificationStatusRequest;
import notification.request.UpdateNotificationStatusTransitionRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.PageableFilterRequest;
import org.eclipse.tractusx.traceability.common.request.SearchCriteriaRequestParam;
import org.eclipse.tractusx.traceability.common.security.JwtRole;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.DiscoveryFinderSupport;
import org.eclipse.tractusx.traceability.integration.common.support.EdcSupport;
import org.eclipse.tractusx.traceability.integration.common.support.IrsApiSupport;
import org.eclipse.tractusx.traceability.integration.common.support.NotificationApiSupport;
import org.eclipse.tractusx.traceability.integration.common.support.NotificationMessageSupport;
import org.eclipse.tractusx.traceability.integration.common.support.NotificationSupport;
import org.eclipse.tractusx.traceability.integration.common.support.OAuth2ApiSupport;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationAffectedPart;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSeverity;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;
import org.eclipse.tractusx.traceability.notification.domain.notification.service.NotificationReceiverService;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotification;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotificationFactory;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.SUPERVISOR;


class PublisherInvestigationsControllerIT extends IntegrationTestSpecification {

    @Autowired
    NotificationReceiverService notificationReceiverService;
    @Autowired
    AssetsSupport assetsSupport;
    @Autowired
    NotificationMessageSupport notificationMessageSupport;
    @Autowired
    NotificationSupport notificationSupport;
    @Autowired
    AssetAsBuiltRepository assetAsBuiltRepository;
    @Autowired
    NotificationApiSupport notificationApiSupport;
    @Autowired
    EdcSupport edcSupport;

    @Autowired
    DiscoveryFinderSupport discoveryFinderSupport;

    @Autowired
    OAuth2ApiSupport oauth2ApiSupport;

    @Autowired
    IrsApiSupport irsApiSupport;

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

        NotificationType notificationType = NotificationType.INVESTIGATION;
        NotificationMessage notificationBuild = NotificationMessage.builder()
                .id("some-id")
                .notificationStatus(NotificationStatus.SENT)
                .affectedParts(List.of(new NotificationAffectedPart("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb")))
                .createdByName("bpn-a")
                .createdBy("Sender Manufacturer name")
                .sendTo("BPNL00000003AXS3")
                .sendToName("Receiver manufacturer name")
                .severity(NotificationSeverity.MINOR)
                .targetDate(Instant.parse("2018-11-30T18:35:24.00Z"))
                .type(notificationType)
                .severity(NotificationSeverity.MINOR)
                .messageId("messageId")
                .build();
        EDCNotification notification = EDCNotificationFactory.createEdcNotification(
                "it", notificationBuild);

        // when
        notificationReceiverService.handleReceive(notification, notificationType);

        // then
        notificationSupport.assertInvestigationsSize(1);
        notificationMessageSupport.assertMessageSize(1);
    }

    @Test
    void shouldStartInvestigation() throws JoseException, JsonProcessingException {


        // given
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        );
        String description = "at least 15 characters long investigation description";
        String title = "the title";

        val startNotificationRequest = StartNotificationRequest.builder()
                .affectedPartIds(partIds)
                .description(description)
                .title(title)
                .type(NotificationTypeRequest.INVESTIGATION)
                .severity(NotificationSeverityRequest.MINOR)
                .receiverBpn("BPNL00000003CNKC")
                .build();

        // when
        notificationApiSupport.createNotificationRequest_withDefaultAssetsStored(oAuth2Support.jwtAuthorization(SUPERVISOR), startNotificationRequest, 201);

        // then
        notificationMessageSupport.assertMessageSize(2);

        given()
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of("channel,EQUAL,SENDER,AND"))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1));

    }

    @Test
    void givenMissingPartIds_whenStartInvestigation_thenBadRequest() throws JoseException, JsonProcessingException {


        // given

        String description = "at least 15 characters long investigation description";
        String title = "the title";

        val startNotificationRequest = StartNotificationRequest.builder()
                .description(description)
                .title(title)
                .type(NotificationTypeRequest.INVESTIGATION)
                .severity(NotificationSeverityRequest.MINOR)
                .build();

        // when
        // then
        notificationApiSupport.createNotificationRequest_withDefaultAssetsStored(oAuth2Support.jwtAuthorization(SUPERVISOR), startNotificationRequest, 400);
    }

    @Test
    void givenMissingBPN_whenStartInvestigation_thenBadRequest() throws JoseException, JsonProcessingException {


        // given
        // given
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        );
        String description = "at least 15 characters long investigation description";
        String title = "the title";

        val startNotificationRequest = StartNotificationRequest.builder()
                .affectedPartIds(partIds)
                .description(description)
                .title(title)
                .type(NotificationTypeRequest.INVESTIGATION)
                .severity(NotificationSeverityRequest.MINOR)
                .build();

        // when
        // then
        notificationApiSupport.createNotificationRequest_withDefaultAssetsStored(oAuth2Support.jwtAuthorization(SUPERVISOR), startNotificationRequest, 400);
    }

    @Test
    void givenMissingSeverity_whenStartInvestigation_thenBadRequest() throws JoseException, JsonProcessingException {
        // given
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        );
        String description = "at least 15 characters long investigation description";

        val request = StartNotificationRequest.builder()
                .affectedPartIds(partIds)
                .description(description)
                .build();
        // when/then
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .when()
                .post("/api/notifications")
                .then()
                .statusCode(400);
    }

    @Test
    void givenDescriptionExceedsMaxLength_whenStartInvestigation_thenBadRequest() throws JoseException, JsonProcessingException {
        // given
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        );

        String description = RandomStringUtils.random(1001);

        val request = StartNotificationRequest.builder()
                .affectedPartIds(partIds)
                .description(description)
                .severity(NotificationSeverityRequest.MINOR)
                .build();

        // when/then
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .when()
                .post("/api/notifications")
                .then()
                .statusCode(400)
                .body(Matchers.containsString("Description should have at least 15 characters and at most 1000 characters"));
    }


    @Test
    void givenInvestigationReasonTooLong_whenUpdate_thenBadRequest() throws JsonProcessingException, JoseException {
        // given
        String description = RandomStringUtils.random(1001);

        UpdateNotificationStatusTransitionRequest request =
                UpdateNotificationStatusTransitionRequest
                        .builder()
                        .reason(description)
                        .status(UpdateNotificationStatusRequest.ACCEPTED)
                        .build();
        // when/then
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .header(oAuth2Support.jwtAuthorization(JwtRole.SUPERVISOR))
                .when()
                .post("/api/notifications/1/update")
                .then()
                .statusCode(400)
                .body(Matchers.containsString("Reason should have at least 15 characters and at most 1000 characters"));
    }

    @Test
    void givenWrongStatus_whenUpdateInvestigation_thenBadRequest() throws JsonProcessingException, JoseException {
        // given
        String description = RandomStringUtils.random(15);

        UpdateNotificationStatusTransitionRequest request =
                UpdateNotificationStatusTransitionRequest
                        .builder()
                        .reason(description)
                        .status(UpdateNotificationStatusRequest.ACCEPTED)
                        .build();

        // when/then
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request)
                        .replace("ACCEPTED", "wrongStatus"))
                .header(oAuth2Support.jwtAuthorization(JwtRole.SUPERVISOR))
                .when()
                .post("/api/notifications/1/update")
                .then()
                .statusCode(400)
                .body(Matchers.containsString("message\":\"NoSuchElementException: Unsupported UpdateInvestigationStatus: wrongStatus. Must be one of: ACKNOWLEDGED, ACCEPTED, DECLINED"));
    }

    @Test
    void shouldCancelInvestigation() throws JoseException, JsonProcessingException {
        // given
        assetsSupport.defaultAssetsStored();
        val startInvestigationRequest = StartNotificationRequest.builder()
                .affectedPartIds(List.of("urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978"))
                .description("at least 15 characters long investigation description")
                .type(NotificationTypeRequest.INVESTIGATION)
                .severity(NotificationSeverityRequest.MAJOR)
                .receiverBpn("BPNL00000003CNKC")
                .build();

        val investigationId = notificationApiSupport.createNotificationRequest_withDefaultAssetsStored(oAuth2Support.jwtAuthorization(SUPERVISOR), startInvestigationRequest, 201);

        given()
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of("channel,EQUAL,SENDER,AND"))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1));
        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/$investigationId/cancel".replace("$investigationId", String.valueOf(investigationId)))
                .then()
                .statusCode(204);

        given()
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of("channel,EQUAL,SENDER,AND"))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1));
    }

    @Test
    void shouldApproveInvestigationStatus() throws JoseException, JsonProcessingException {
        // given
        irsApiSupport.irsApiReturnsPolicies();
        discoveryFinderSupport.discoveryFinderWillReturnEndpointAddress();
        discoveryFinderSupport.discoveryFinderWillReturnConnectorEndpoints();
        oauth2ApiSupport.oauth2ApiReturnsDtrToken();
        edcSupport.performSupportActionsForAsyncNotificationMessageExecutor();
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        );
        String description = "at least 15 characters long investigation description";

        assetsSupport.defaultAssetsStored();
        val startInvestigationRequest = StartNotificationRequest.builder()
                .affectedPartIds(partIds)
                .description(description)
                .severity(NotificationSeverityRequest.MINOR)
                .type(NotificationTypeRequest.INVESTIGATION)
                .receiverBpn("BPNL00000003CNKC")
                .build();

        // when
        val investigationId = notificationApiSupport.createNotificationRequest_withDefaultAssetsStored(oAuth2Support.jwtAuthorization(SUPERVISOR), startInvestigationRequest, 201);


        notificationSupport.assertInvestigationsSize(1);

        given()
                .contentType(ContentType.JSON)
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .when()
                .post("/api/notifications/{investigationId}/approve", investigationId)
                .then()
                .statusCode(204);

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of("channel,EQUAL,SENDER,AND"))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("content[0].sendTo", Matchers.is(Matchers.not(Matchers.blankOrNullString())));

        notificationMessageSupport.assertMessageSize(4);
    }

    @Test
    void shouldCloseInvestigationStatus() throws JoseException, JsonProcessingException {
        // given
        irsApiSupport.irsApiReturnsPolicies();
        discoveryFinderSupport.discoveryFinderWillReturnEndpointAddress();
        discoveryFinderSupport.discoveryFinderWillReturnConnectorEndpoints();
        oauth2ApiSupport.oauth2ApiReturnsDtrToken();
        edcSupport.performSupportActionsForAsyncNotificationMessageExecutor();

        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978" // BPN: BPNL00000003AYRE
        );
        String description = "at least 15 characters long investigation description";
        oAuth2ApiSupport.oauth2ApiReturnsTechnicalUserToken();

        assetsSupport.defaultAssetsStored();
        val startInvestigationRequest = StartNotificationRequest.builder()
                .affectedPartIds(partIds)
                .description(description)
                .type(NotificationTypeRequest.INVESTIGATION)
                .receiverBpn("BPNL00000003CNKC")
                .severity(NotificationSeverityRequest.MINOR)
                .build();

        val investigationId = notificationApiSupport.createNotificationRequest_withDefaultAssetsStored(oAuth2Support.jwtAuthorization(SUPERVISOR), startInvestigationRequest, 201);

        // then
        notificationSupport.assertInvestigationsSize(1);

        // when
        given()
                .contentType(ContentType.JSON)
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .when()
                .post("/api/notifications/{investigationId}/approve", investigationId)
                .then()
                .statusCode(204);
        // then
        given()
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of("channel,EQUAL,SENDER,AND"))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("content[0].sendTo", Matchers.is(Matchers.not(Matchers.blankOrNullString())));

        // when
        CloseNotificationRequest closeInvestigationRequest =
                CloseNotificationRequest
                        .builder()
                        .reason("this is the close reason for that investigation")
                        .build();
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(closeInvestigationRequest))
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .when()
                .post("/api/notifications/{investigationId}/close", investigationId)
                .then()
                .statusCode(204);

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of("channel,EQUAL,SENDER,AND"))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1));

        notificationMessageSupport.assertMessageSize(3);
        notificationSupport.assertInvestigationsSize(1);
        notificationSupport.assertInvestigationStatus(NotificationStatus.CLOSED);
    }

    @Test
    void givenNonExistingInvestigation_whenCancel_thenReturnNotFound() throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/1/cancel")
                .then()
                .statusCode(404)
                .body("message", Matchers.is("Notification with id: 1 not found"));
    }

    @Test
    void givenNoAuthorization_whenCancel_thenReturn401() {
        given()
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/1/cancel")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldBeCreatedBySender() throws JoseException, JsonProcessingException {
        // given
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        );
        String description = "at least 15 characters long investigation description";
        assetsSupport.defaultAssetsStored();
        val startInvestigationRequest = StartNotificationRequest.builder()
                .affectedPartIds(partIds)
                .description(description)
                .severity(NotificationSeverityRequest.MINOR)
                .receiverBpn("BPNL00000003CNKC")
                .type(NotificationTypeRequest.INVESTIGATION)
                .build();

        // when
        notificationApiSupport.createNotificationRequest_withDefaultAssetsStored(oAuth2Support.jwtAuthorization(SUPERVISOR), startInvestigationRequest, 201);

        // then
        partIds.forEach(partId -> {
            AssetBase asset = assetAsBuiltRepository.getAssetById(partId);
            assertThat(asset).isNotNull();
        });

        notificationMessageSupport.assertMessageSize(2);
        given()
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of("channel,EQUAL,SENDER,AND"))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1));
    }
}
