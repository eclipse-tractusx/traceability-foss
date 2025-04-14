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
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.hamcrest.Matchers.is;

class AssetAsBuiltControllerFilterValuesIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;

    @ParameterizedTest
    @MethodSource("fieldNameTestProvider")
    void givenNotEnumTypeFieldNameAndSize_whenCallDistinctFilterValues_thenProperResponse(
            String fieldName,
            Long resultLimit,
            Integer expectedSize
    ) throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String fieldNameParam = "fieldName=" + fieldName;
        final String sizeParam = "size=" + resultLimit.toString();

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built/distinctFilterValues?" + fieldNameParam + "&" + sizeParam)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("size()", is(expectedSize));
    }

    @ParameterizedTest
    @MethodSource("fieldNameTestProviderWithStartWithParam")
    void givenNotEnumTypeFieldNameAndSizeAndStartWith_whenCallDistinctFilterValues_thenProperResponse(
            String fieldName,
            String startWith,
            Long resultLimit,
            Integer expectedSize
    ) throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", resultLimit.toString())
                .param("startsWith", startWith)
                .get("/api/assets/as-built/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("size()", is(expectedSize));
    }

    @Test
    void givenNotEnumTypeFieldNameAndSizeAndOwnerOwn_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        String fieldName = "id";
        String resultLimit = "100";
        String owner = "OWN";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", resultLimit)
                .param("owner", owner)
                .get("/api/assets/as-built/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("size()", is(2));
    }

    @Test
    void givenBusinessPartnerLowercase_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        String fieldName = "businessPartner";
        String resultLimit = "100";
        String startWith = "bpnl";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", resultLimit)
                .param("startsWith", startWith)
                .get("/api/assets/as-built/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(".", Matchers.containsInRelativeOrder(
                        "BPNL00000003AXS3",
                        "BPNL00000003AYRE",
                        "BPNL00000003B0Q0",
                        "BPNL00000003B2OM",
                        "BPNL00000003B3NX",
                        "BPNL00000003B5MJ"));
    }

    @Test
    void givenBusinessPartnerMixedCase_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        String fieldName = "businessPartner";
        String resultLimit = "100";
        String startWith = "bpNl";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", resultLimit)
                .param("startsWith", startWith)
                .get("/api/assets/as-built/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(".", Matchers.containsInRelativeOrder(
                        "BPNL00000003AXS3",
                        "BPNL00000003AYRE",
                        "BPNL00000003B0Q0",
                        "BPNL00000003B2OM",
                        "BPNL00000003B3NX",
                        "BPNL00000003B5MJ"));
    }

    @Test
    void givenNotExistentOwnerEnumValue_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        String fieldName = "id";
        String resultLimit = "100";
        String owner = "NON_EXISTENT_ENUM_VALUE";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", resultLimit)
                .param("owner", owner)
                .get("/api/assets/as-built/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(400)
                .assertThat()
                .body("size()", is(1));
    }

    @Test
    void givenIdFieldNameAndNoResultLimit_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        String fieldName = "id";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .get("/api/assets/as-built/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("size()", is(13));
    }

    @Test
    void givenNotEnumTypeFieldNameAndSizeAndOwnerSupplier_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        String fieldName = "id";
        String resultLimit = "100";
        String owner = "SUPPLIER";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", resultLimit)
                .param("owner", owner)
                .get("/api/assets/as-built/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("size()", is(11));
    }

    @Test
    void givenEnumTypeFieldNameImportState_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        String fieldName = "importState";
        String resultLimit = "100";
        String owner = "OWN";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", resultLimit)
                .param("owner", owner)
                .get("/api/assets/as-built/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("size()", is(6));
    }

    @Test
    void givenInAssetListIsProvided_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        String fieldName = "id";
        String resultLimit = "100";
        String owner = "SUPPLIER";
        String inAssetIds = "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef,urn:uuid:16bb1a7e-8ed8-48ca-a839-5f38b704fcae";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", resultLimit)
                .param("owner", owner)
                .param("inAssetIds", inAssetIds)
                .get("/api/assets/as-built/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("size()", is(1));
    }

    private static Stream<Arguments> fieldNameTestProvider() {
        return Stream.of(
                Arguments.of("id", 10L, 10),
                Arguments.of("id", 200L, 13),
                Arguments.of("owner", 10L, 4),
                Arguments.of("semanticDataModel", 10L, 7),
                Arguments.of("qualityType", 10L, 5)
        );
    }

    private static Stream<Arguments> fieldNameTestProviderWithStartWithParam() {
        return Stream.of(
                Arguments.of("id", "urn:uuid:1", 10L, 3),
                Arguments.of("owner", "O", 10L, 1),
                Arguments.of("semanticDataModel", "shouldNotFindAnything", 10L, 0),
                Arguments.of("qualityType", "M", 10L, 2)
        );
    }
}
