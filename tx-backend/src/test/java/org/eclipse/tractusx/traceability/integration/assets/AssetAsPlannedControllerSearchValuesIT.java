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
import org.eclipse.tractusx.traceability.assets.infrastructure.asplanned.repository.JpaAssetAsPlannedRepository;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.hamcrest.Matchers.is;

class AssetAsPlannedControllerSearchValuesIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    JpaAssetAsPlannedRepository repo;

    @ParameterizedTest
    @MethodSource("fieldNameTestProvider")
    void givenNotEnumTypeFieldNameAndSize_whenCallSearchableValues_thenProperResponse(
            String fieldName,
            Long resultLimit,
            Integer expectedSize
    ) throws JoseException {
        // given
        assetsSupport.defaultAssetsAsPlannedStored();

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(asJson(Map.of("fieldName", fieldName, "size", resultLimit)))
                .log().all()
                .when()
                .post("/api/assets/as-planned/searchable-values")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("size()", is(expectedSize));
    }

    @Test
    void givenNotEnumTypeFieldNameAndSizeAndOwnerOwn_whenCallSearchableValues_thenProperResponse() throws JoseException {
        // given
        assetsSupport.defaultAssetsAsPlannedStored();
        String fieldName = "id";
        String resultLimit = "100";
        String owner = "OWN";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(asJson(Map.of("fieldName", fieldName, "size", resultLimit, "owner", owner)))
                .log().all()
                .when()
                .post("/api/assets/as-planned/searchable-values")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("size()", is(1));
    }

    @Test
    void givenNotEnumTypeFieldNameAndOwnerOwn_whenCallSearchableValues_thenProperResponse() throws JoseException {
        // given
        assetsSupport.defaultAssetsAsPlannedStored();
        String fieldName = "id";
        String owner = "OWN";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(asJson(Map.of("fieldName", fieldName, "owner", owner)))
                .log().all()
                .when()
                .post("/api/assets/as-planned/searchable-values")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("size()", is(1));
    }

    @Test
    void givenIdShortLowerCase_whenCallSearchableValues_thenProperResponse() throws JoseException {
        // given
        assetsSupport.defaultAssetsAsPlannedStored();
        String fieldName = "idShort";
        String startsWith = "vehicle";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(asJson(Map.of("fieldName", fieldName, "startsWith", List.of(startsWith))))
                .log().all()
                .when()
                .post("/api/assets/as-planned/searchable-values")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(".", Matchers.containsInRelativeOrder(
                        "VehicleModelA",
                        "VehicleModelB"));
    }

    @Test
    void givenIdShortMixedCase_whenCallSearchableValues_thenProperResponse() throws JoseException {
        // given
        assetsSupport.defaultAssetsAsPlannedStored();
        String fieldName = "idShort";
        String startsWith = "vehicleMODEL";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(asJson(Map.of("fieldName", fieldName, "startsWith", List.of(startsWith))))
                .log().all()
                .when()
                .post("/api/assets/as-planned/searchable-values")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(".", Matchers.containsInRelativeOrder(
                        "VehicleModelA",
                        "VehicleModelB"));
    }

    @Test
    void givenWrongOwnerEnum_whenCallSearchableValues_thenProperResponse() throws JoseException {
        // given
        assetsSupport.defaultAssetsAsPlannedStored();
        String fieldName = "id";
        String resultLimit = "100";
        String owner = "nonExistentEnumValue";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(asJson(Map.of("fieldName", fieldName, "size", resultLimit, "owner", owner)))
                .log().all()
                .when()
                .post("/api/assets/as-planned/searchable-values")
                .then()
                .log().all()
                .statusCode(400)
                .assertThat()
                .body("size()", is(1));
    }

    @Test
    void givenNotEnumTypeFieldNameAndSizeAndOwnerSupplier_whenCallSearchableValues_thenProperResponse() throws JoseException {
        // given
        assetsSupport.defaultAssetsAsPlannedStored();
        String fieldName = "id";
        String resultLimit = "100";
        String owner = "SUPPLIER";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(asJson(Map.of("fieldName", fieldName, "size", resultLimit, "owner", owner)))
                .log().all()
                .when()
                .post("/api/assets/as-planned/searchable-values")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("size()", is(1));
    }

    @Test
    void givenEnumTypeFieldNameImportState_whenCallSearchableValues_thenProperResponse() throws JoseException {
        // given
        assetsSupport.defaultAssetsAsPlannedStored();
        String fieldName = "importState";
        String resultLimit = "100";
        String owner = "OWN";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(asJson(Map.of("fieldName", fieldName, "size", resultLimit, "owner", owner)))
                .log().all()
                .when()
                .post("/api/assets/as-planned/searchable-values")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("size()", is(6));
    }

    @Test
    void givenInAssetListIsProvided_whenCallSearchableValues_thenProperResponse() throws JoseException {
        // given
        assetsSupport.defaultAssetsAsPlannedStored();
        String fieldName = "id";
        String resultLimit = "100";
        String owner = "OWN";
        String[] inAssetIds = {"urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01"};

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(asJson(Map.of("fieldName", fieldName, "size", resultLimit, "owner", owner,
                        "inAssetIds", inAssetIds)))
                .log().all()
                .when()
                .post("/api/assets/as-planned/searchable-values")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("size()", is(1));
    }

    private static Stream<Arguments> fieldNameTestProvider() {
        return Stream.of(
                Arguments.of("id", 10L, 2),
                Arguments.of("id", 1L, 1),
                Arguments.of("owner", 10L, 4),
                Arguments.of("semanticDataModel", 10L, 7),
                Arguments.of("qualityType", 10L, 5)
        );
    }
}
