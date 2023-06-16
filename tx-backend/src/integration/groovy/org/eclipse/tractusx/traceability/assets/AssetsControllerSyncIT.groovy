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

class AssetsControllerSyncIT extends IntegrationSpecification implements IrsApiSupport, AssetsSupport, BpnSupport {

    def "should synchronize assets"() {
        given:
        oauth2ApiReturnsTechnicalUserToken()
        irsApiTriggerJob()
        irsApiReturnsJobDetails()

        when:
        given()
                .contentType(ContentType.JSON)
                .body(
                        asJson(
                                [
                                        globalAssetIds: ["urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb"]
                                ]
                        )
                )
                .header(jwtAuthorization(ADMIN))
                .when()
                .post("/api/assets/sync")
                .then()
                .statusCode(200)

        then:
        eventually {
            assertAssetsSize(14)
            assertHasRequiredIdentifiers()
            assertHasChildCount("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", 5)
        }

        and:
        verifyOAuth2ApiCalledOnceForTechnicalUserToken()
        verifyIrsApiTriggerJobCalledTimes(2)
    }

    def "should synchronize assets as planned"() {
        given:
        oauth2ApiReturnsTechnicalUserToken()
        irsApiTriggerJob()
        irsApiReturnsJobDetailsAsPlannedDownward()

        when:
        given()
                .contentType(ContentType.JSON)
                .body(
                        asJson(
                                [
                                        globalAssetIds: ["urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb"]
                                ]
                        )
                )
                .header(jwtAuthorization(ADMIN))
                .when()
                .post("/api/assets/sync")
                .then()
                .statusCode(200)

        then:
        eventually {
            assertAssetsSize(2)
            assertHasChildCount("urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4da01", 1)
        }

        and:
        verifyOAuth2ApiCalledOnceForTechnicalUserToken()
        verifyIrsApiTriggerJobCalledTimes(3)
    }

    def "should synchronize assets using retry"() {
        given:
        oauth2ApiReturnsTechnicalUserToken()
        and:
        irsApiTriggerJob()
        and:
        irsApiReturnsJobInRunningAndCompleted()

        when:
        given()
                .contentType(ContentType.JSON)
                .body(
                        asJson(
                                [
                                        globalAssetIds: ["urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb"]
                                ]
                        )
                )
                .header(jwtAuthorization(ADMIN))
                .when()
                .post("/api/assets/sync")
                .then()
                .statusCode(200)

        then:
        eventually {
            assertAssetsSize(14)
        }

        and:
        verifyOAuth2ApiCalledOnceForTechnicalUserToken()
        verifyIrsApiTriggerJobCalledTimes(3)
    }

    def "should not synchronize assets when irs failed to return job details"() {
        given:
        oauth2ApiReturnsTechnicalUserToken()
        irsApiTriggerJob()

        and:
        irsJobDetailsApiFailed()

        when:
        given()
                .contentType(ContentType.JSON)
                .body(
                        asJson(
                                [
                                        globalAssetIds: ["urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb"]
                                ]
                        )
                )
                .header(jwtAuthorization(ADMIN))
                .when()
                .post("/api/assets/sync")
                .then()
                .statusCode(200)

        then:
        eventually {
            assertNoAssetsStored()
        }

        and:
        verifyOAuth2ApiCalledOnceForTechnicalUserToken()
        verifyIrsApiTriggerJobCalledOnce()
    }

    def "should not synchronize assets when irs keeps returning job in running state"() {
        given:
        oauth2ApiReturnsTechnicalUserToken()
        irsApiTriggerJob()

        and:
        irsApiReturnsJobInRunningState()

        when:
        given()
                .contentType(ContentType.JSON)
                .body(
                        asJson(
                                [
                                        globalAssetIds: ["urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb"]
                                ]
                        )
                )
                .header(jwtAuthorization(ADMIN))
                .when()
                .post("/api/assets/sync")
                .then()
                .statusCode(200)

        then:
        eventually {
            assertNoAssetsStored()
        }

        and:
        verifyOAuth2ApiCalledOnceForTechnicalUserToken()
        verifyIrsApiTriggerJobCalledOnce()
    }








}
