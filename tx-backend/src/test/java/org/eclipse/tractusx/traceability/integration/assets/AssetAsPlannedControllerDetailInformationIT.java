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

class AssetAsPlannedControllerDetailInformationIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;

    @Test
    void shouldNotReturnAassetsDetailInformationWhenUserIsNotAuthenticated() {
        //GIVEN
        assetsSupport.defaultAssetsAsPlannedStored();

        //THEN
        given()
                .contentType(ContentType.JSON)
                .body(
                        asJson(Map.of("assetIds", List.of("1234"))
                        )
                )
                .when()
                .post("/api/assets/as-planned/detail-information")
                .then()
                .statusCode(401);
    }

    @Test
    void shouldReturnAssetsDetailInformation() throws JoseException {
        //GIVEN
        assetsSupport.defaultAssetsAsPlannedStored();

        //THEN
        given()
                .contentType(ContentType.JSON)
                .body(
                        asJson(Map.of("assetIds", List.of("urn:urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01",
                                "urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4eb01"))
                        )
                )
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .when()
                .post("/api/assets/as-planned/detail-information")
                .then()
                .statusCode(200)
                .body("", hasSize(1));
    }
}
