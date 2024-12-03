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
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class AssetAsPlannedControllerAllIT extends IntegrationTestSpecification {

    @Autowired
    BpnSupport bpnSupport;

    @Autowired
    AssetsSupport assetsSupport;

    private static Stream<Arguments> owners() {
        return Stream.of(
                arguments("OWN", 1),
                arguments("CUSTOMER", 0),
                arguments("SUPPLIER", 1),
                arguments("UNKNOWN", 0));
    }

    @Test
    void shouldReturnAssetsWithManufacturerName() throws JoseException {
        //GIVEN
        bpnSupport.cachedBpnsForAsPlannedAssets();
        assetsSupport.defaultAssetsAsPlannedStored();

        //THEN
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-planned")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", is(greaterThan(0)))
                .body("content.manufacturerName", everyItem(not(equalTo(null))));
    }

    @ParameterizedTest
    @MethodSource("owners")
    void shouldReturnAssetsByOwnerFiltering(String ownerValue, int totalItemsValue) throws JoseException {
        //GIVEN
        assetsSupport.defaultAssetsAsPlannedStored();

        //THEN filter=owner,EQUAL,OWN
        final String filter = "owner,EQUAL," + ownerValue + ",AND";
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .queryParam("filter", filter)
                .when()
                .get("/api/assets/as-planned")
                .then()
                .statusCode(200)
                .body("totalItems", equalTo(totalItemsValue));
    }

    @Test
    void shouldGetPageOfAssets() throws JoseException {
        //GIVEN
        assetsSupport.defaultAssetsAsPlannedStored();

        //THEN
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .param("page", "2")
                .param("size", "2")
                .when()
                .get("/api/assets/as-planned")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(2))
                .body("pageSize", Matchers.is(2));
    }

    @Test
    void givenNonExistingSortField_whenGetAssetsAsPlanned_thenBadRequest() throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .param("page", "2")
                .param("size", "2")
                .param("sort", "nonExistingField,ASC")
                .when()
                .get("/api/assets/as-planned")
                .then()
                .statusCode(400);
    }

}
