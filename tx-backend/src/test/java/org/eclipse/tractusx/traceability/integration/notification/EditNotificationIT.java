package org.eclipse.tractusx.traceability.integration.notification;

import io.restassured.http.ContentType;
import io.restassured.http.Header;
import lombok.RequiredArgsConstructor;
import lombok.val;
import notification.request.EditNotificationRequest;
import notification.request.NotificationSeverityRequest;
import notification.request.NotificationTypeRequest;
import notification.request.StartNotificationRequest;
import notification.response.NotificationResponse;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.PageableFilterRequest;
import org.eclipse.tractusx.traceability.common.request.SearchCriteriaRequestParam;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.NotificationApiSupport;
import org.eclipse.tractusx.traceability.integration.common.support.NotificationMessageSupport;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.SUPERVISOR;

@RequiredArgsConstructor
class EditNotificationIT extends IntegrationTestSpecification {

    @Autowired
    NotificationMessageSupport notificationMessageSupport;
    @Autowired
    AssetAsBuiltRepository assetAsBuiltRepository;

    ObjectMapper objectMapper;
    @Autowired
    NotificationApiSupport notificationAPISupport;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldUpdateInvestigation_RemovingOnePartOnly() throws JoseException, com.fasterxml.jackson.core.JsonProcessingException {
        Header authHeader = oAuth2Support.jwtAuthorization(SUPERVISOR);
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
                .receiverBpn("BPNL00000003CNKC")
                .severity(NotificationSeverityRequest.MINOR)
                .build();
        int id = notificationAPISupport.createNotificationRequest_withDefaultAssetsStored(authHeader, startNotificationRequest, 201);

        // given
        List<String> editedPartIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb" // BPN: BPNL00000003AYRE
        );

        val request = EditNotificationRequest.builder()
                .affectedPartIds(editedPartIds)
                .severity(startNotificationRequest.getSeverity())
                .description(startNotificationRequest.getDescription())
                .title(startNotificationRequest.getTitle())
                .receiverBpn("BPNL00000003CNKC")
                .build();

        // when
        notificationAPISupport.editNotificationRequest(authHeader, request, id, 204);

        // then
        notificationMessageSupport.assertMessageSize(0);

        given()
                .header(authHeader)
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of("channel,EQUAL,SENDER,AND"))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .log().all();
    }

    @Test
    void shouldThrowBadRequestWhenUpdateInvestigation_SenderAndReceiverBpnIsSame() throws JoseException, com.fasterxml.jackson.core.JsonProcessingException {
        Header authHeader = oAuth2Support.jwtAuthorization(SUPERVISOR);
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
                .receiverBpn("BPNL00000003CNKC")
                .severity(NotificationSeverityRequest.MINOR)
                .build();
        int id = notificationAPISupport.createNotificationRequest_withDefaultAssetsStored(authHeader, startNotificationRequest, 201);

        List<String> editedPartIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb" // BPN: BPNL00000003AYRE
        );

        val request = EditNotificationRequest.builder()
                .affectedPartIds(editedPartIds)
                .severity(startNotificationRequest.getSeverity())
                .description(startNotificationRequest.getDescription())
                .title(startNotificationRequest.getTitle())
                .receiverBpn("BPNL00000003AXS3")
                .build();

        // when
        notificationAPISupport.editNotificationRequest(authHeader, request, id, 400);
    }

    @Test
    void shouldUpdateInvestigationFields() throws JoseException, com.fasterxml.jackson.core.JsonProcessingException {
        Header authHeader = oAuth2Support.jwtAuthorization(SUPERVISOR);
        // given
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        );
        String description = "at least 15 characters long investigation description";
        String title = "the initial title";
        val startNotificationRequest = StartNotificationRequest.builder()
                .affectedPartIds(partIds)
                .description(description)
                .title(title)
                .type(NotificationTypeRequest.INVESTIGATION)
                .severity(NotificationSeverityRequest.MINOR)
                .receiverBpn("BPNL00000003AYRE")
                .build();


        int id = notificationAPISupport.createNotificationRequest_withDefaultAssetsStored(authHeader, startNotificationRequest, 201);

        // given
        String editedDescription = "at least 15 characters long investigation description which was edited";

        String editedTitle = "changed title";
        val editNotificationRequest = EditNotificationRequest.builder()
                .affectedPartIds(partIds)
                .description(editedDescription)
                .title(editedTitle)
                .affectedPartIds(startNotificationRequest.getAffectedPartIds())
                .severity(NotificationSeverityRequest.CRITICAL)
                .receiverBpn("BPNL00000003AYRE")
                .build();

        // when
        notificationAPISupport.editNotificationRequest(authHeader, editNotificationRequest, id, 204);

        // then
        notificationMessageSupport.assertMessageSize(0);

        PageableFilterRequest pageableFilterRequest =
                new PageableFilterRequest(
                        new OwnPageable(0, 10, Collections.emptyList()),
                        new SearchCriteriaRequestParam(List.of("channel,EQUAL,SENDER,AND")));

        PageResult<NotificationResponse> notificationResponsePageResult
                = notificationAPISupport.getNotificationsRequest(authHeader, pageableFilterRequest);


        NotificationResponse notificationResponse = notificationResponsePageResult.content().get(0);
        assertThat(notificationResponse.getId()).isEqualTo(id);
        assertThat(notificationResponse.getDescription()).isEqualTo(editNotificationRequest.getDescription());
        assertThat(notificationResponse.getTitle()).isEqualTo(editNotificationRequest.getTitle());
        assertThat(notificationResponse.getAssetIds()).hasSize(editNotificationRequest.getAffectedPartIds().size());
        assertThat(notificationResponse.getSeverity().getRealName()).isEqualTo(editNotificationRequest.getSeverity().getRealName());
        assertThat(notificationResponsePageResult.content()).hasSize(1);

    }

    @Test
    void shouldNotUpdateInvestigationFields_whenBpnWrongFormatted() throws JoseException, com.fasterxml.jackson.core.JsonProcessingException {
        Header authHeader = oAuth2Support.jwtAuthorization(SUPERVISOR);
        // given
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        );
        String description = "at least 15 characters long investigation description";
        String title = "the initial title";
        val startNotificationRequest = StartNotificationRequest.builder()
                .affectedPartIds(partIds)
                .description(description)
                .title(title)
                .type(NotificationTypeRequest.INVESTIGATION)
                .severity(NotificationSeverityRequest.MINOR)
                .receiverBpn("BPNL00000003CNKC")
                .build();


        int id = notificationAPISupport.createNotificationRequest_withDefaultAssetsStored(authHeader, startNotificationRequest, 201);

        // given
        String editedDescription = "at least 15 characters long investigation description which was edited";

        String editedTitle = "changed title";
        val editNotificationRequest = EditNotificationRequest.builder()
                .affectedPartIds(partIds)
                .description(editedDescription)
                .title(editedTitle)
                .receiverBpn("WRONG_FORMAT")
                .affectedPartIds(startNotificationRequest.getAffectedPartIds())
                .severity(NotificationSeverityRequest.CRITICAL)
                .build();

        // when
        notificationAPISupport.editNotificationRequest(authHeader, editNotificationRequest, id, 400);

        // then
        PageableFilterRequest pageableFilterRequest =
                new PageableFilterRequest(
                        new OwnPageable(0, 10, Collections.emptyList()),
                        new SearchCriteriaRequestParam(List.of("channel,EQUAL,SENDER,AND")));
        PageResult<NotificationResponse> notificationResponsePageResult
                = notificationAPISupport.getNotificationsRequest(authHeader, pageableFilterRequest);

        NotificationResponse notificationResponse = notificationResponsePageResult.content().get(0);
        assertThat(notificationResponse.getSendTo()).isEqualTo("BPNL00000003CNKC");

    }

    @Test
    void shouldUpdateReceiverBpn() throws JoseException, com.fasterxml.jackson.core.JsonProcessingException {
        Header authHeader = oAuth2Support.jwtAuthorization(SUPERVISOR);

        // given
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", // BPN: BPNL00000003AYRE
                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
        );
        String description = "at least 15 characters long investigation description";
        String title = "the initial title";
        val startNotificationRequest = StartNotificationRequest.builder()
                .affectedPartIds(partIds)
                .description(description)
                .title(title)
                .type(NotificationTypeRequest.ALERT)
                .severity(NotificationSeverityRequest.MINOR)
                .receiverBpn("BPNL00000003AYRE")
                .build();

        int id = notificationAPISupport.createNotificationRequest_withDefaultAssetsStored(authHeader, startNotificationRequest, 201);

        String editedDescription = "at least 15 characters long investigation description which was edited";

        String editedTitle = "changed title";
        val editNotificationRequest = EditNotificationRequest.builder()
                .receiverBpn("BPNL00000003AABC")
                .affectedPartIds(partIds)
                .description(editedDescription)
                .title(editedTitle)
                .affectedPartIds(startNotificationRequest.getAffectedPartIds())
                .severity(NotificationSeverityRequest.CRITICAL)
                .build();

        // when
        notificationAPISupport.editNotificationRequest(authHeader, editNotificationRequest, id, 204);

        // then
        notificationMessageSupport.assertMessageSize(0);

        PageableFilterRequest pageableFilterRequest =
                new PageableFilterRequest(
                        new OwnPageable(0, 10, Collections.emptyList()),
                        new SearchCriteriaRequestParam(List.of("channel,EQUAL,SENDER,AND")));

        PageResult<NotificationResponse> notificationResponsePageResult
                = notificationAPISupport.getNotificationsRequest(authHeader, pageableFilterRequest);

        NotificationResponse notificationResponse = notificationResponsePageResult.content().get(0);
        assertThat(notificationResponse.getId()).isEqualTo(id);
        assertThat(notificationResponse.getSendTo()).isEqualTo(editNotificationRequest.getReceiverBpn());
        assertThat(notificationResponse.getCreatedBy()).isEqualTo("BPNL00000003AXS3");

    }
}
