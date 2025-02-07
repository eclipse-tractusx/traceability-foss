/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.integration.edc.blackbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.common.security.JwtRole;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotification;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;

class EdcControllerAuthorizationIT extends IntegrationTestSpecification {

    private static final String ROOT = "/api/internal";
    public static final String API_KEY_HEADER = "x-technical-service-key";
    public static final String API_KEY_VALUE = "test-key";

    ObjectMapper objectMapper = new ObjectMapper();

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#noRoleRequired")
    void shouldAllowQnReceiveEndpointRegardlessOfRole(final JwtRole role) throws JoseException, IOException {
        // Given
        final String notificationJson = readFile("/testdata/edc_notification_okay.json");
        final EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        // Then
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .when()
                .post(ROOT + "/qualitynotifications/receive")
                .then()
                .assertThat()
                .statusCode(400);
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#noRoleRequired")
    void shouldNotAllowQnReceiveEndpointRegardlessOfRoleWithoutApiKey(final JwtRole role) throws JoseException, IOException {
        // Given
        final String notificationJson = readFile("/testdata/edc_notification_okay.json");
        final EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        // Then
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .when()
                .post(ROOT + "/qualitynotifications/receive")
                .then()
                .assertThat()
                .statusCode(401);
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#noRoleRequired")
    void shouldNotAllowQnReceiveEndpointRegardlessOfRoleWithWrongApiKey(final JwtRole role) throws JoseException, IOException {
        // Given
        final String notificationJson = readFile("/testdata/edc_notification_okay.json");
        final EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        // Then
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .header(API_KEY_HEADER, "wrong-api-key")
                .when()
                .post(ROOT + "/qualitynotifications/receive")
                .then()
                .assertThat()
                .statusCode(401);
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#noRoleRequired")
    void shouldAllowQnUpdateEndpointRegardlessOfRole(final JwtRole role) throws JoseException, IOException {
        // Given
        final String notificationJson = readFile("/testdata/edc_notification_okay_update.json");
        final EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        // Then
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .when()
                .post(ROOT + "/qualitynotifications/update")
                .then()
                .assertThat()
                .statusCode(404);
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#noRoleRequired")
    void shouldNotAllowQnUpdateEndpointRegardlessOfRoleWithoutApiKey(final JwtRole role) throws JoseException, IOException {
        // Given
        final String notificationJson = readFile("/testdata/edc_notification_okay_update.json");
        final EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        // Then
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .when()
                .post(ROOT + "/qualitynotifications/update")
                .then()
                .assertThat()
                .statusCode(401);
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#noRoleRequired")
    void shouldNotAllowQnUpdateEndpointRegardlessOfRoleWithWrongApiKey(final JwtRole role) throws JoseException, IOException {
        // Given
        final String notificationJson = readFile("/testdata/edc_notification_okay_update.json");
        final EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        // Then
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .header(API_KEY_HEADER, "wrong-api-key")
                .when()
                .post(ROOT + "/qualitynotifications/update")
                .then()
                .assertThat()
                .statusCode(401);
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#noRoleRequired")
    void shouldAllowAnReceiveEndpointRegardlessOfRole(final JwtRole role) throws JoseException, IOException {
        // Given
        final String notificationJson = readFile("/testdata/edc_alert_okay.json");
        final EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        // Then
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .when()
                .post(ROOT + "/qualityalerts/receive")
                .then()
                .assertThat()
                .statusCode(400);
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#noRoleRequired")
    void shouldNotAllowAnReceiveEndpointRegardlessOfRoleWithoutApiKey(final JwtRole role) throws JoseException, IOException {
        // Given
        final String notificationJson = readFile("/testdata/edc_alert_okay.json");
        final EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        // Then
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .when()
                .post(ROOT + "/qualityalerts/receive")
                .then()
                .assertThat()
                .statusCode(401);
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#noRoleRequired")
    void shouldNotAllowAnReceiveEndpointRegardlessOfRoleWithWrongApiKey(final JwtRole role) throws JoseException, IOException {
        // Given
        final String notificationJson = readFile("/testdata/edc_alert_okay.json");
        final EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        // Then
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .header(API_KEY_HEADER, "wrong-api-key")
                .when()
                .post(ROOT + "/qualityalerts/receive")
                .then()
                .assertThat()
                .statusCode(401);
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#noRoleRequired")
    void shouldAllowAnUpdateEndpointRegardlessOfRole(final JwtRole role) throws JoseException, IOException {
        // Given
        final String notificationJson = readFile("/testdata/edc_alert_okay_update.json");
        final EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        // Then
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .header(API_KEY_HEADER, API_KEY_VALUE)
                .when()
                .post(ROOT + "/qualityalerts/update")
                .then()
                .assertThat()
                .statusCode(400);
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#noRoleRequired")
    void shouldNotAllowAnUpdateEndpointRegardlessOfRoleWithoutApiKey(final JwtRole role) throws JoseException, IOException {
        // Given
        final String notificationJson = readFile("/testdata/edc_alert_okay_update.json");
        final EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        // Then
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .when()
                .post(ROOT + "/qualityalerts/update")
                .then()
                .assertThat()
                .statusCode(401);
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#noRoleRequired")
    void shouldNotAllowAnUpdateEndpointRegardlessOfRoleWithWrongApiKey(final JwtRole role) throws JoseException, IOException {
        // Given
        final String notificationJson = readFile("/testdata/edc_alert_okay_update.json");
        final EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        // Then
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .header(API_KEY_HEADER, "wrong-api-key")
                .when()
                .post(ROOT + "/qualityalerts/update")
                .then()
                .assertThat()
                .statusCode(401);
    }

    private String readFile(final String filePath) throws IOException {
        try (final InputStream file = EdcControllerIT.class.getResourceAsStream(filePath)) {
            if (file == null) {
                return null;
            }
            return new String(file.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
