package org.eclipse.tractusx.traceability.integration.notification;

import io.restassured.http.ContentType;
import lombok.RequiredArgsConstructor;
import lombok.val;
import notification.request.EditNotificationRequest;
import notification.request.NotificationSeverityRequest;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
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
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
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
    void shouldUpdateInvestigation_RemovingOnePart() throws JsonProcessingException, JoseException, com.fasterxml.jackson.core.JsonProcessingException {
        int id = notificationAPISupport.createInvestigation_withDefaultAssetsStored(oAuth2Support.jwtAuthorization(SUPERVISOR));

        // given
        List<String> partIds = List.of(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb" // BPN: BPNL00000003AYRE
        );
        String description = "at least 15 characters long investigation description";


        val request = EditNotificationRequest.builder()
                .affectedPartIds(partIds)
                .description(description)
                .title("the title")
                .severity(NotificationSeverityRequest.MINOR)
                .build();

        // when
        given()
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .when()
                .put("/api/notifications/" + id + "/edit")
                .then()
                .statusCode(204);

        // then
        partIds.forEach(partId -> {
            AssetBase asset = assetAsBuiltRepository.getAssetById(partId);
            assertThat(asset).isNotNull();
        });

        notificationMessageSupport.assertMessageSize(1);

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
                .log().all();
    }
}
