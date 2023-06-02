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
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.QualityNotificationStatusBaseEntity
import org.hamcrest.Matchers

import static io.restassured.RestAssured.given
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN
import static org.hamcrest.Matchers.equalTo

class AssetsControllerByIdIT extends IntegrationSpecification implements IrsApiSupport, AssetsSupport, BpnSupport {

    def "should return assets for authenticated user with role"() {
        given:
        defaultAssetsStored()

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb")
                .then()
                .statusCode(200)
    }

    def "should return asset without under investigation mark"() {
        given:
        defaultAssetsStoredWithOnGoingInvestigation(QualityNotificationStatusBaseEntity.CLOSED, false)

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb")
                .then()
                .statusCode(200)
                .body("underInvestigation", equalTo(false))
    }

    def "should return asset with under investigation mark"() {
        given:
        defaultAssetsStoredWithOnGoingInvestigation(QualityNotificationStatusBaseEntity.SENT, true)

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb")
                .then()
                .statusCode(200)
                .body("underInvestigation", equalTo(true))
    }


    def "should not return assets when user is not authenticated"() {
        expect:
        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/1234")
                .then()
                .statusCode(401)
    }

    def "should get children asset"() {
        given:
        defaultAssetsStored()

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb/children/urn:uuid:587cfb38-7149-4f06-b1e0-0e9b6e98be2a")
                .then()
                .statusCode(200)
                .body("id", Matchers.is("urn:uuid:587cfb38-7149-4f06-b1e0-0e9b6e98be2a"))
    }

    def "should return 404 when children asset is not found"() {
        given:
        defaultAssetsStored()

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb/children/unknown")
                .then()
                .statusCode(404)
    }


    def "should not update quality type for not existing asset"() {
        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(asJson(
                        [
                                qualityType: 'Critical'
                        ]
                ))
                .when()
                .patch("/api/assets/1234")
                .then()
                .statusCode(404)
                .body("message", equalTo("Asset with id 1234 was not found."))
    }

    def "should not update quality type with invalid request body"() {
        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(asJson(requestBody))
                .when()
                .patch("/api/assets/1234")
                .then()
                .statusCode(400)
                .body("message", equalTo(errorMessage))

        where:
        requestBody                                | errorMessage
        [qualityType: 'NOT_EXISTING_QUALITY_TYPE'] | "Failed to deserialize request body."
        [qualityType: 'CRITICAL']                  | "Failed to deserialize request body."
        [qualityType: '']                          | "Failed to deserialize request body."
        [qualityType: ' ']                         | "Failed to deserialize request body."
        [qualityType: null]                        | "qualityType must be present"
    }

    def "should update quality type for existing asset"() {
        given:
        defaultAssetsStored()

        and:
        def existingAssetId = "urn:uuid:1ae94880-e6b0-4bf3-ab74-8148b63c0640"

        expect:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/$existingAssetId")
                .then()
                .statusCode(200)
                .body("qualityType", equalTo("Ok"))

        and:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(asJson(
                        [
                                qualityType: 'Critical'
                        ]
                ))
                .when()
                .patch("/api/assets/$existingAssetId")
                .then()
                .statusCode(200)
                .body("qualityType", equalTo("Critical"))

        and:
        given()
                .header(jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/$existingAssetId")
                .then()
                .statusCode(200)
                .body("qualityType", equalTo("Critical"))
    }
}
