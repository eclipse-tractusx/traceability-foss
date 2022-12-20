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

package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.rest

import io.restassured.http.ContentType
import org.eclipse.tractusx.traceability.IntegrationSpecification
import org.eclipse.tractusx.traceability.assets.domain.model.Asset
import org.eclipse.tractusx.traceability.assets.infrastructure.adapters.feign.irs.model.AssetsConverter
import org.eclipse.tractusx.traceability.common.support.AssetsSupport
import org.eclipse.tractusx.traceability.common.support.BpnSupport
import org.eclipse.tractusx.traceability.common.support.IrsApiSupport
import org.hamcrest.Matchers
import spock.util.concurrent.PollingConditions

import static io.restassured.RestAssured.given
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN
import static org.eclipse.tractusx.traceability.common.security.JwtRole.SUPERVISOR
import static org.eclipse.tractusx.traceability.common.security.JwtRole.USER
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.everyItem
import static org.hamcrest.Matchers.not

class AssetsControllerIT extends IntegrationSpecification implements IrsApiSupport, AssetsSupport, BpnSupport {

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
			verifyIrsApiTriggerJobCalledOnce()
	}

	def "should use cached BPNs when empty list is returned"() {
		given:
			oauth2ApiReturnsTechnicalUserToken()
			irsApiTriggerJob()
			irsApiReturnsJobDetailsWithNoBPNs()

		and:
			cachedBpnsForDefaultAssets()

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
				List<Asset> assets = assetRepository().getAssets().findAll { asset ->
					asset.manufacturerId != AssetsConverter.EMPTY_TEXT
				}
				assets.size() == 13
				assets.each { asset ->
					assert asset.manufacturerName != AssetsConverter.EMPTY_TEXT
				}
			}

		and:
			verifyOAuth2ApiCalledOnceForTechnicalUserToken()
			verifyIrsApiTriggerJobCalledOnce()
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
			verifyIrsApiTriggerJobCalledOnce()
	}

	def "should not synchronize assets when irs failed to trigger job"() {
		given:
			oauth2ApiReturnsTechnicalUserToken()
			irsApiTriggerJobFailed()

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
			new PollingConditions(timeout: 10, initialDelay: 3).eventually {
				assertNoAssetsStored()
			}

		and:
			verifyOAuth2ApiCalledOnceForTechnicalUserToken()
			verifyIrsApiTriggerJobCalledOnce()
			verifyIrsJobDetailsApiNotCalled()
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

	def "should return assets for authenticated user with role"() {
		expect:
			given()
				.header(jwtAuthorization(ADMIN))
				.when()
				.get("/api/assets/1234")
				.then()
				.statusCode(200)
	}

	def "should return assets with manufacturer name"() {
		given:
			cachedBpnsForDefaultAssets()

		and:
			defaultAssetsStored()

		expect:
			given()
				.header(jwtAuthorization(ADMIN))
				.when()
				.get("/api/assets")
				.then()
				.statusCode(200)
				.body("content.manufacturerName", everyItem(not(equalTo(AssetsConverter.EMPTY_TEXT))))
	}

	def "should return supplier assets"() {
		given:
			defaultAssetsStored()

		expect:
			given()
				.header(jwtAuthorization(ADMIN))
				.when()
				.get("/api/assets/supplier")
				.then()
				.statusCode(200)
				.body("totalItems", equalTo(12))
	}

	def "should return own assets"() {
		given:
			defaultAssetsStored()

		expect:
			given()
				.header(jwtAuthorization(ADMIN))
				.when()
				.get("/api/assets/my")
				.then()
				.statusCode(200)
				.body("totalItems", equalTo(1))
	}

	def "should return assets country map"() {
		expect:
			given()
				.header(jwtAuthorization(role))
				.when()
				.get("/api/assets/countries")
				.then()
				.statusCode(200)

		where:
			role << [USER, ADMIN, SUPERVISOR]
	}

	def "should not return assets country map when user is not authenticated"() {
		expect:
			given()
				.when()
				.get("/api/assets/countries")
				.then()
				.statusCode(401)
	}

	def "should not return assets when user is not authenticated"() {
		expect:
			given()
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
				.when()
				.get("/api/assets/urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb/children/unknown")
				.then()
				.statusCode(404)
	}

	def "should get a page of assets"() {
		given:
			defaultAssetsStored()

		expect:
			given()
				.header(jwtAuthorization(ADMIN))
				.param("page", "2")
				.param("size", "2")
				.when()
				.get("/api/assets")
				.then()
				.statusCode(200)
				.body("page", Matchers.is(2))
				.body("pageSize", Matchers.is(2))
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
				.when()
				.get("/api/assets/$existingAssetId")
				.then()
				.statusCode(200)
				.body("qualityType", equalTo("Critical"))
	}

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
							assetIds: []
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
				.body("", Matchers.hasSize(3))
	}
}
