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

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.EdcSupport;
import org.eclipse.tractusx.traceability.integration.common.support.IrsApiSupport;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeEach;
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

class AssetAsPlannedControllerByIdIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    EdcSupport edcSupport;

    @Autowired
    IrsApiSupport irsApiSupport;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        edcSupport.performSupportActionsForBpdmAccess();
        irsApiSupport.irsApiReturnsPoliciesBpdm();
    }

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
        assetsSupport.defaultAssetsAsPlannedStored();

        //THEN
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-planned/urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01")
                .then()
                .statusCode(200);
    }


    @Test
    void shouldNotReturnAssetsWhenUserIsNotAuthenticated() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/as-planned/1234")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldGetChildrenAsset() throws JoseException {
        //GIVEN
        assetsSupport.defaultAssetsAsPlannedStored();

        //THEN
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/as-planned/urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4eb01/children/urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01")
                .then()
                .statusCode(200)
                .body("id", Matchers.is("urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01"));
    }

    @Test
    void shouldReturn404WhenChildrenAssetIsNotFound() throws JoseException {
        //GIVEN
        assetsSupport.defaultAssetsAsPlannedStored();

        //THEN
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/as-planned/urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb/children/unknown")
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
                .patch("/api/assets/as-planned/1234")
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
                .patch("/api/assets/as-planned/1234")
                .then()
                .statusCode(400)
                .body("message", equalTo(errorMessage));
    }

    @Test
    void shouldUpdateQualityTypeForExistingAsset() throws JoseException {
        //GIVEN
        assetsSupport.defaultAssetsAsPlannedStored();
        String existingAssetId = "urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01";

        //THEN
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/as-planned/" + existingAssetId)
                .then()
                .statusCode(200)
                .body("qualityType", equalTo("Ok"));

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(Map.of("qualityType", "Critical"))
                .when()
                .patch("/api/assets/as-planned/" + existingAssetId)
                .then()
                .statusCode(200)
                .body("qualityType", equalTo("Critical"));

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/as-planned/" + existingAssetId)
                .then()
                .statusCode(200)
                .body("qualityType", equalTo("Critical"));
    }

}
