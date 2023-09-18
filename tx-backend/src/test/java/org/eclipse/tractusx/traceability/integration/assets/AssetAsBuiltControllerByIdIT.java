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
package org.eclipse.tractusx.traceability.integration.assets;

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class AssetAsBuiltControllerByIdIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;

    private static Stream<Arguments> requests() {
        return Stream.of(
                arguments(Map.of("qualityType", "NOT_EXISTING_QUALITY_TYPE"), "Failed to deserialize request body."),
                arguments(Map.of("qualityType", "'CRITICAL'"), "Failed to deserialize request body."),
                arguments(Map.of("qualityType", ""), "Failed to deserialize request body."),
                arguments(Map.of("qualityType", " "), "Failed to deserialize request body.")
        );
    }

    @Test
    void shouldReturnAssetsForAuthenticatedUserWithRole() throws JoseException {
        //GIVEN
        assetsSupport.defaultAssetsStored();

        //THEN
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/as-built/urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb")
                .then()
                .statusCode(200);
    }

    @Test
    void shouldReturnAssetWithoutUnderInvestigationMark() throws JoseException {
        //GIVEN
        assetsSupport.defaultAssetsStoredWithOnGoingInvestigation(NotificationStatusBaseEntity.CLOSED, false);

        //THEN
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/as-built/urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb")
                .then()
                .statusCode(200)
                .body("underInvestigation", equalTo(false));
    }

    @Test
    void shouldReturnAssetWithUnderInvestigationMark() throws JoseException {
        //GIVEN
        assetsSupport.defaultAssetsStoredWithOnGoingInvestigation(NotificationStatusBaseEntity.SENT, true);

        //THEN
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/as-built/urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb")
                .then()
                .statusCode(200)
                .body("underInvestigation", equalTo(true));
    }

    @Test
    void shouldNotReturnAssetsWhenUserIsNotAuthenticated() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/as-built/1234")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldGetChildrenAsset() throws JoseException {
        //GIVEN
        assetsSupport.defaultAssetsStored();

        //THEN
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/as-built/urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb/children/urn:uuid:587cfb38-7149-4f06-b1e0-0e9b6e98be2a")
                .then()
                .statusCode(200)
                .body("id", Matchers.is("urn:uuid:587cfb38-7149-4f06-b1e0-0e9b6e98be2a"));
    }

    @Test
    void shouldReturn404WhenChildrenAssetIsNotFound() throws JoseException {
        //GIVEN
        assetsSupport.defaultAssetsStored();

        //THEN
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/as-built/urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb/children/unknown")
                .then()
                .statusCode(404);
    }

    @Test
    void shouldNotUpdateQualityTypeForNotExistingAsset() throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(asJson(Map.of("qualityType", "Critical")))
                .when()
                .patch("/api/assets/as-built/1234")
                .then()
                .statusCode(404)
                .body("message", equalTo("Asset with id 1234 was not found."));
    }

    @ParameterizedTest
    @MethodSource("requests")
    void shouldNotUpdateQualityTypeWithInvalidRequestBody(Map<String, String> requestBody, String errorMessage) throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(asJson(requestBody))
                .when()
                .patch("/api/assets/as-built/1234")
                .then()
                .statusCode(400)
                .body("message", equalTo(errorMessage));
    }

    @Test
    void shouldUpdateQualityTypeForExistingAsset() throws JoseException {
        //GIVEN
        assetsSupport.defaultAssetsStored();
        String existingAssetId = "urn:uuid:1ae94880-e6b0-4bf3-ab74-8148b63c0640";

        //THEN
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/as-built/" + existingAssetId)
                .then()
                .statusCode(200)
                .body("qualityType", equalTo("Ok"));

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(Map.of("qualityType", "Critical"))
                .when()
                .patch("/api/assets/as-built/" + existingAssetId)
                .then()
                .statusCode(200)
                .body("qualityType", equalTo("Critical"));

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/as-built/" + existingAssetId)
                .then()
                .statusCode(200)
                .body("qualityType", equalTo("Critical"));
    }

}
