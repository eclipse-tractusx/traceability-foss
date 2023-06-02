/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.assets

import io.restassured.http.ContentType
import org.eclipse.tractusx.traceability.IntegrationSpecification
import org.eclipse.tractusx.traceability.common.support.AssetsSupport
import org.eclipse.tractusx.traceability.common.support.BpnSupport
import org.eclipse.tractusx.traceability.common.support.IrsApiSupport

import static io.restassured.RestAssured.given
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN
import static org.hamcrest.Matchers.hasSize

class AssetsControllerDetailInformationIT extends IntegrationSpecification implements IrsApiSupport, AssetsSupport, BpnSupport {

    def "should not return assets detail information when user is not authenticated"() {
        given:
        cachedBpnsForDefaultAssets()

        and:
        defaultAssetsStored()

        expect:
        given()
                .contentType(ContentType.JSON)
                .body(
                        asJson(
                                [
                                        assetIds: ["1234"]
                                ]
                        )
                )
                .when()
                .post("/api/assets/detail-information")
                .then()
                .statusCode(401)
    }

    def "should return assets detail information"() {
        given:
        cachedBpnsForDefaultAssets()

        and:
        defaultAssetsStored()

        expect:
        given()
                .contentType(ContentType.JSON)
                .body(
                        asJson(
                                [
                                        assetIds: [
                                                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978",
                                                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb",
                                                "urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"
                                        ]
                                ]
                        )
                )
                .header(jwtAuthorization(ADMIN))
                .when()
                .post("/api/assets/detail-information")
                .then()
                .statusCode(200)
                .body("", hasSize(3))
    }

}
