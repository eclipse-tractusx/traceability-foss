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

import assets.response.base.request.QualityTypeRequest;
import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.common.security.JwtRole;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.ForbiddenMatcher;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Integration test for checking correct role-based authorization of
 * the endpoints of {@link org.eclipse.tractusx.traceability.assets.application.asplanned.rest.AssetAsPlannedController}.
 */
class AssetAsPlannedControllerAuthorizationIT extends IntegrationTestSpecification {

    private static final String ROOT = "/api/assets/as-planned";

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#allRolesAllowed")
    void shouldAllowAsPlannedEndpointOnlyForSpecificRoles(JwtRole role, boolean isAllowed) throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .contentType(ContentType.JSON)
                .when()
                .get(ROOT)
                .then()
                .assertThat()
                .statusCode(new ForbiddenMatcher(isAllowed));
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#allRolesAllowed")
    void shouldAllowSyncEndpointOnlyForSpecificRoles(JwtRole role, boolean isAllowed) throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .contentType(ContentType.JSON)
                .body(asJson(Map.of("globalAssetIds", List.of("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb"))))
                .when()
                .post(ROOT + "/sync")
                .then()
                .statusCode(new ForbiddenMatcher(isAllowed));
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#allRolesAllowed")
    void shouldAllowDistinctFilterValuesEndpointOnlyForSpecificRoles(JwtRole role, boolean isAllowed) throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .contentType(ContentType.JSON)
                .param("fieldName","idShort")
                .when()
                .get(ROOT + "/distinctFilterValues")
                .then()
                .statusCode(new ForbiddenMatcher(isAllowed));
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#allRolesAllowed")
    void shouldAllowAssetIdEndpointOnlyForSpecificRoles(JwtRole role, boolean isAllowed) throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .contentType(ContentType.JSON)
                .when()
                .get(ROOT + "/123")
                .then()
                .statusCode(new ForbiddenMatcher(isAllowed));
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#allRolesAllowed")
    void shouldAllowAssetIdByChildIdEndpointOnlyForSpecificRoles(JwtRole role, boolean isAllowed) throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .contentType(ContentType.JSON)
                .when()
                .get(ROOT + "/123/children/456")
                .then()
                .statusCode(new ForbiddenMatcher(isAllowed));
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#allRolesAllowed")
    void shouldAllowUpdateAssetEndpointOnlyForSpecificRoles(JwtRole role, boolean isAllowed) throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .contentType(ContentType.JSON)
                .body(asJson(Map.of("qualityType", QualityTypeRequest.OK.getDescription())))
                .when()
                .patch(ROOT + "/123")
                .then()
                .statusCode(new ForbiddenMatcher(isAllowed));
    }

    @ParameterizedTest
    @MethodSource("org.eclipse.tractusx.traceability.integration.common.support.RoleSupport#allRolesAllowed")
    void shouldAllowDetailInformationEndpointOnlyForSpecificRoles(JwtRole role, boolean isAllowed) throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorizationWithOptionalRole(role))
                .contentType(ContentType.JSON)
                .body(asJson(Map.of("assetIds", List.of("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb"))))
                .when()
                .post(ROOT + "/detail-information")
                .then()
                .statusCode(new ForbiddenMatcher(isAllowed));
    }
}
