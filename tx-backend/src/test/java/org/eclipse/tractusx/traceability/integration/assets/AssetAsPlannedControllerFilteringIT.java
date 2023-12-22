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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.hamcrest.Matchers.equalTo;

class AssetAsPlannedControllerFilteringIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;
    @Autowired
    JpaAssetAsPlannedRepository tempRepo;

    @BeforeEach
    void before() {
        assetsSupport.defaultAssetsAsPlannedStored();
    }

    private static Stream<Arguments> filters() {
        return Stream.of(
                Arguments.of(Set.of("owner,EQUAL,OWN"), "AND", 1),
                Arguments.of(Set.of("nameAtManufacturer,STARTS_WITH,Vehicle"), "AND", 2),
                Arguments.of(Set.of("nameAtManufacturer,STARTS_WITH,Vehicle", "owner,EQUAL,SUPPLIER"), "AND", 1),
                Arguments.of(Set.of("owner,EQUAL,SUPPLIER", "id,STARTS_WITH,urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4eb01"), "OR", 1)
        );
    }

    @ParameterizedTest
    @MethodSource("filters")
    void givenFilter_whenCallFilteredEndpoint_thenReturnExpectedResult(
            final Set<String> filters,
            final String filterOperator,
            final Integer expectedTotalItems
    ) throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .param("filter", filters.stream().toList())
                .param("filterOperator", filterOperator)
                .log().all()
                .when()
                .get("/api/assets/as-planned")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(expectedTotalItems));
    }

    @Test
    void givenNoFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
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

    private static Stream<Arguments> filterCaseInsensitive() {
        return Stream.of(
                Arguments.of(
                        "nameAtManufacturer,STARTS_WITH,vehicle model b"
                ),
                Arguments.of(
                        "nameAtManufacturer,STARTS_WITH,Vehicle Model B"

                ),
                Arguments.of(
                        "nameAtManufacturer,STARTS_WITH,VEHICLE MODEL B"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("filterCaseInsensitive")
    void testIfFilteringIsCaseInsensitive(String filter) throws JoseException {

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .param("page", 0)
                .param("size", 50)
                .param("filter", filter)
                .param("filterOperator", "AND")
                .log().all()
                .when()
                .get("/api/assets/as-planned")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(1))
                .body("content.nameAtManufacturer[0]", equalTo("Vehicle Model B"));
    }
}
