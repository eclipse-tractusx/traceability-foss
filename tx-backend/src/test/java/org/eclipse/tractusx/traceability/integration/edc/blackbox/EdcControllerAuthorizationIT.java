package org.eclipse.tractusx.traceability.integration.edc.blackbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.common.security.JwtRole;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.ForbiddenMatcher;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotification;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;

class EdcControllerAuthorizationIT extends IntegrationTestSpecification {

    private static final String ROOT = "/api";

    ObjectMapper objectMapper = new ObjectMapper();

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#noRoleRequired")
    void shouldAllowQnReceiveEndpointRegardlessOfRole(JwtRole role, boolean isAllowed) throws JoseException, IOException {
        // Given
        String notificationJson = readFile("/testdata/edc_notification_okay.json");
        EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        // Then
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .when()
                .post(ROOT + "/qualitynotifications/receive")
                .then()
                .assertThat()
                .statusCode(new ForbiddenMatcher(isAllowed));
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#noRoleRequired")
    void shouldAllowQnUpdateEndpointRegardlessOfRole(JwtRole role, boolean isAllowed) throws JoseException, IOException {
        // Given
        String notificationJson = readFile("/testdata/edc_notification_okay_update.json");
        EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        // Then
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .when()
                .post(ROOT + "/qualitynotifications/update")
                .then()
                .assertThat()
                .statusCode(new ForbiddenMatcher(isAllowed));
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#noRoleRequired")
    void shouldAllowAnReceiveEndpointRegardlessOfRole(JwtRole role, boolean isAllowed) throws JoseException, IOException {
        // Given
        String notificationJson = readFile("/testdata/edc_alert_okay.json");
        EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        // Then
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .when()
                .post(ROOT + "/qualityalerts/receive")
                .then()
                .assertThat()
                .statusCode(new ForbiddenMatcher(isAllowed));
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#noRoleRequired")
    void shouldAllowAnUpdateEndpointRegardlessOfRole(JwtRole role, boolean isAllowed) throws JoseException, IOException {
        // Given
        String notificationJson = readFile("/testdata/edc_alert_okay_update.json");
        EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        // Then
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .when()
                .post(ROOT + "/qualityalerts/update")
                .then()
                .assertThat()
                .statusCode(new ForbiddenMatcher(isAllowed));
    }

    private String readFile(final String filePath) throws IOException {
        try (InputStream file = EdcControllerIT.class.getResourceAsStream(filePath)) {
            if (file == null) {
                return null;
            }
            return new String(file.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
