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
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.hamcrest.Matchers.equalTo;

class AssetAsPlannedControllerFilteringIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;
    @Autowired
    JpaAssetAsPlannedRepository tempRepo;

    @Test
    void givenNoFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsAsPlannedStored();

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-planned")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(2));
    }

    @Test
    void givenOwnFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsAsPlannedStored();
        final String filter = "?filter=owner,EQUAL,OWN";
        final String filterOperator = "&filterOperator=AND";
        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-planned" + filter + filterOperator)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenNameAtManufacturerFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsAsPlannedStored();
        final String filter = "?filter=nameAtManufacturer,STARTS_WITH,Vehicle";
        final String filterOperator = "&filterOperator=AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-planned" + filter + filterOperator)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(2));
    }

    @Test
    void givenNameAtManufacturerAndOwnerFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsAsPlannedStored();
        final String filter = "?filter=nameAtManufacturer,STARTS_WITH,Vehicle&filter=owner,EQUAL,SUPPLIER";
        final String filterOperator = "&filterOperator=AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-planned" + filter + filterOperator)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenSemanticDataModelAndOwnerOR_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsAsPlannedStored();
        final String filter = "?filter=owner,EQUAL,SUPPLIER&filter=id,STARTS_WITH,urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4eb01";
        final String filterOperator = "&filterOperator=OR";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-planned" + filter + filterOperator)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(1));
    }
}
