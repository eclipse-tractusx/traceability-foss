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
package org.eclipse.tractusx.traceability.infrastructure.edc.notiifcationcontract

import io.restassured.http.ContentType
import org.eclipse.tractusx.traceability.IntegrationSpecification
import org.eclipse.tractusx.traceability.common.security.JwtRole
import org.eclipse.tractusx.traceability.common.support.EdcSupport
import org.hamcrest.Matchers
import spock.lang.Unroll

import static io.restassured.RestAssured.given
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN
import static org.eclipse.tractusx.traceability.common.security.JwtRole.SUPERVISOR
import static org.hamcrest.Matchers.blankString
import static org.hamcrest.Matchers.not

class EdcNotificationContractControllerIT extends IntegrationSpecification implements EdcSupport {

	def "should create edc contract"() {
		given:
			edcWillCreateNotificationAsset()
			edcWillCreatePolicyDefinition()
			edcWillCreateContractDefinition()

		expect:
			given()
				.contentType(ContentType.JSON)
				.header(jwtAuthorization(ADMIN))
				.body(
					asJson(
						[
							notificationType  : "QUALITY_INVESTIGATION",
							notificationMethod: "RECEIVE"
						]
					)
				)
				.when()
				.post("/api/edc/notification/contract")
				.then()
				.statusCode(201)
				.body("notificationAssetId", Matchers.is(not(blankString())))
				.body("accessPolicyId", Matchers.is(not(blankString())))
				.body("contractDefinitionId", Matchers.is(not(blankString())))

		and:
			verifyCreateNotificationAssetEndpointCalledTimes(1)
			verifyCreatePolicyDefinitionEndpointCalledTimes(1)
			verifyCreateContractDefinitionEndpointCalledTimes(1)

		and:
			verifyDeleteNotificationAssetEndpointCalledTimes(0)
			verifyDeletePolicyDefinitionEndpointCalledTimes(0)
			verifyDeleteContractDefinitionEndpointCalledTimes(0)
	}

	def "should not create edc contract when notification asset creation failed"() {
		given:
			edcWillFailToCreateNotificationAsset()

		expect:
			given()
				.contentType(ContentType.JSON)
				.header(jwtAuthorization(ADMIN))
				.body(
					asJson(
						[
							notificationType  : "QUALITY_INVESTIGATION",
							notificationMethod: "RECEIVE"
						]
					)
				)
				.when()
				.post("/api/edc/notification/contract")
				.then()
				.statusCode(500)
				.body("message", Matchers.is("Failed to create notification contract."))

		and:
			verifyCreateNotificationAssetEndpointCalledTimes(1)
			verifyCreatePolicyDefinitionEndpointCalledTimes(0)
			verifyCreateContractDefinitionEndpointCalledTimes(0)

		and:
			verifyDeleteNotificationAssetEndpointCalledTimes(0)
			verifyDeletePolicyDefinitionEndpointCalledTimes(0)
			verifyDeleteContractDefinitionEndpointCalledTimes(0)

	}

	def "should not create edc contract and do rollback when policy definition creation failed"() {
		given:
			edcWillCreateNotificationAsset()
			edcWillFailToCreatePolicyDefinition()

		and:
			edcWillRemoveNotificationAsset()

		expect:
			given()
				.contentType(ContentType.JSON)
				.header(jwtAuthorization(ADMIN))
				.body(
					asJson(
						[
							notificationType  : "QUALITY_INVESTIGATION",
							notificationMethod: "RECEIVE"
						]
					)
				)
				.when()
				.post("/api/edc/notification/contract")
				.then()
				.statusCode(500)
				.body("message", Matchers.is("Failed to create notification contract."))

		and:
			verifyCreateNotificationAssetEndpointCalledTimes(1)
			verifyCreatePolicyDefinitionEndpointCalledTimes(1)
			verifyCreateContractDefinitionEndpointCalledTimes(0)

		and:
			verifyDeleteNotificationAssetEndpointCalledTimes(1)
			verifyDeletePolicyDefinitionEndpointCalledTimes(0)
			verifyDeleteContractDefinitionEndpointCalledTimes(0)
	}

	def "should not create edc contract and do rollback when contract definition creation failed"() {
		given:
			edcWillCreateNotificationAsset()
			edcWillCreatePolicyDefinition()
			edcWillFailToCreateContractDefinition()

		and:
			edcWillRemovePolicyDefinition()
			edcWillRemoveNotificationAsset()

		expect:
			given()
				.contentType(ContentType.JSON)
				.header(jwtAuthorization(ADMIN))
				.body(
					asJson(
						[
							notificationType  : "QUALITY_INVESTIGATION",
							notificationMethod: "RECEIVE"
						]
					)
				)
				.when()
				.post("/api/edc/notification/contract")
				.then()
				.statusCode(500)
				.body("message", Matchers.is("Failed to create notification contract."))

		and:
			verifyCreateNotificationAssetEndpointCalledTimes(1)
			verifyCreatePolicyDefinitionEndpointCalledTimes(1)
			verifyCreateContractDefinitionEndpointCalledTimes(1)

		and:
			verifyDeleteNotificationAssetEndpointCalledTimes(1)
			verifyDeletePolicyDefinitionEndpointCalledTimes(1)
			verifyDeleteContractDefinitionEndpointCalledTimes(0)
	}

	def "should not create edc contract without authentication"() {
		expect:
			given()
				.contentType(ContentType.JSON)
				.body(
					asJson(
						[
							notificationType  : "QUALITY_INVESTIGATION",
							notificationMethod: "RECEIVE"
						]
					)
				)
				.when()
				.post("/api/edc/notification/contract")
				.then()
				.statusCode(401)
	}

	@Unroll
	def "should not create edc contract having #role role"() {
		given:
			jwtAuthorization(role)

		expect:
			given()
				.contentType(ContentType.JSON)
				.body(
					asJson(
						[
							notificationType  : "QUALITY_INVESTIGATION",
							notificationMethod: "RECEIVE"
						]
					)
				)
				.when()
				.post("/api/edc/notification/contract")
				.then()
				.statusCode(401)

		where:
			role << [JwtRole.USER, SUPERVISOR]
	}

	def "should not create edc contract with invalid request"() {
		expect:
			given()
				.contentType(ContentType.JSON)
				.header(jwtAuthorization(ADMIN))
				.body(
					asJson(
						[
							notificationType  : notificationType,
							notificationMethod: notificationMethod
						]
					)
				)
				.post("/api/edc/notification/contract")
				.then()
				.statusCode(400)

		where:
			notificationType            | notificationMethod
			null                        | null
			""                          | null
			null                        | ""
			""                          | ""
			"  "                        | "  "
			"invalid-notification-type" | "RECEIVE"
			"QUALITY_INVESTIGATION"     | "invalid-notification-method"
	}

	def "should not create edc contract for quality alert because it's not yet implemented"() {
		expect:
			given()
				.contentType(ContentType.JSON)
				.header(jwtAuthorization(ADMIN))
				.body(
					asJson(
						[
							notificationType  : "QUALITY_ALERT",
							notificationMethod: "RECEIVE"
						]
					)
				)
				.post("/api/edc/notification/contract")
				.then()
				.statusCode(500)
				.body("message", Matchers.is("Failed to create notification contract."))
	}
}
