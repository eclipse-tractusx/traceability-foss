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

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.http.ContentType;
import java.util.stream.Stream;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.*;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

class AssetAsPlannedControllerSortAndFilterValuesIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;

    @BeforeEach
    void before() {
        // Test data contains different spellings for 'catenaXSiteId', as long as no clear spelling is defined. https://github.com/eclipse-tractusx/sldt-semantic-models/issues/470
        assetsSupport.assetsAsPlannedStored("/testdata/irs_assets_as_planned_v4_long_list.json");
    }

    private static Stream<Arguments> sortAndFilterArguments() {
        return Stream.of(
                // As long as no clear spelling for 'catenaxSiteId' is defined, test on different spellings. https://github.com/eclipse-tractusx/sldt-semantic-models/issues/470
                Arguments.of("nameAtManufacturer,asc", "catenaxSiteId,EQUAL,BPNS000004711DMY,AND",
                        new String[]{"HVModul", "OEMAHighVoltageBattery", "VehicleModelA", "ZBZELLE"}),
                Arguments.of("nameAtManufacturer,asc", "catenaxSiteId,EQUAL,BPNS000004711DMY,AND",
                        new String[]{"HVModul", "OEMAHighVoltageBattery", "VehicleModelA", "ZBZELLE"})
        );
    }

    @ParameterizedTest
    @MethodSource("sortAndFilterArguments")
    void givenSortArguments_whenCallSortAndFilterEndpoint_thenReturnExpectedResponse(
            final String sort,
            final String filter,
            final String[] expectedOrderOfIdShortItems
    ) throws JoseException {

        final long page = 0;
        final long size = 20;

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .param("page", page)
                .param("size", size)
                .param("sort", sort)
                .param("filter", filter)
                .log().all()
                .when()
                .get("/api/assets/as-planned")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(expectedOrderOfIdShortItems.length))
                .body("content.idShort", Matchers.containsInRelativeOrder(expectedOrderOfIdShortItems));
    }
}
