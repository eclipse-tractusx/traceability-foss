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
import org.eclipse.tractusx.traceability.common.security.JwtRole;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class AssetAsBuiltControllerCountriesIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;

    private static Stream<Arguments> roles() {
        return Stream.of(
                arguments(USER),
                arguments(SUPERVISOR),
                arguments(ADMIN)
        );
    }

    @ParameterizedTest
    @MethodSource("roles")
    void shouldReturnAssetsCountryMap(JwtRole role) throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorization(role))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/as-built/countries")
                .then()
                .statusCode(200);

    }

    @Test
    void shouldNotReturnAssetsCountryMapWhenUserIsNotAuthenticated() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/as-built/countries")
                .then()
                .statusCode(401);
    }

}
