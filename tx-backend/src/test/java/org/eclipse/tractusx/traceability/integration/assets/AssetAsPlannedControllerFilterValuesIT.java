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
import org.jose4j.lang.JoseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.hamcrest.Matchers.is;

public class AssetAsPlannedControllerFilterValuesIT extends IntegrationTestSpecification {

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
        assetsSupport.defaultAssetsAsPlannedStored();
        final String fieldNameParam = "fieldName=" + fieldName;
        final String sizeParam = "size=" + resultLimit.toString();

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-planned/distinctFilterValues?" + fieldNameParam + "&" + sizeParam)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("size()", is(expectedSize));
    }

    private static Stream<Arguments> fieldNameTestProvider() {
        return Stream.of(
                Arguments.of("id", 10L, 2),
                Arguments.of("id", 1L, 1),
                Arguments.of("inInvestigation", 10L, 1),
                Arguments.of("owner", 10L, 4),
                Arguments.of("semanticDataModel", 10L, 5),
                Arguments.of("qualityType", 10L, 5)
        );
    }
}
