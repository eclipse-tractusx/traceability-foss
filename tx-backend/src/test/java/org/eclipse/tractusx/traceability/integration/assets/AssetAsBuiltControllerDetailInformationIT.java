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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.hamcrest.Matchers.hasSize;

class AssetAsBuiltControllerDetailInformationIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;

    @Test
    void shouldNotReturnAassetsDetailInformationWhenUserIsNotAuthenticated() {
        //GIVEN
        assetsSupport.defaultAssetsStored();

        //THEN
        given()
                .contentType(ContentType.JSON)
                .body(
                        asJson(Map.of("assetIds", List.of("1234"))
                        )
                )
                .when()
                .post("/api/assets/as-built/detail-information")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldReturnAssetsDetailInformation() throws JoseException {
        //GIVEN
        assetsSupport.defaultAssetsStored();

        //THEN
        given()
                .contentType(ContentType.JSON)
                .body(
                        asJson(Map.of("assetIds", List.of("urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978",
                                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb",
                                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"))
                        )
                )
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/assets/as-built/detail-information")
                .then()
                .statusCode(200)
                .body("", hasSize(3));
    }
}
