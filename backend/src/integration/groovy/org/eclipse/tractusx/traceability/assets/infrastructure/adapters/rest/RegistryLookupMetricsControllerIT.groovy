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
import org.eclipse.tractusx.traceability.common.support.AssetsSupport
import org.eclipse.tractusx.traceability.common.support.IrsApiSupport
import org.eclipse.tractusx.traceability.common.support.RegistrySupport
import org.eclipse.tractusx.traceability.common.support.ShellDescriptorSupport
import org.eclipse.tractusx.traceability.IntegrationSpecification
import org.hamcrest.Matchers
import spock.lang.Ignore

import static io.restassured.RestAssured.given
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN
import static org.eclipse.tractusx.traceability.common.support.ISO8601DateTimeMatcher.isIso8601DateTime

@Ignore
class RegistryLookupMetricsControllerIT extends IntegrationSpecification  implements IrsApiSupport, RegistrySupport, ShellDescriptorSupport, AssetsSupport {

	def "should return registry lookup metrics after success refresh"() {
		given:
			oauth2ApiReturnsTechnicalUserToken()
			assetShellsLookupReturnsData()
			fetchRegistryShellDescriptorsLookupReturnsData()

		and:
			irsApiTriggerJob()
			irsApiReturnsJobDetails()

		when:
			given()
				.header(jwtAuthorization(ADMIN))
				.contentType(ContentType.JSON)
				.when()
				.get("/api/registry/reload")
				.then()
				.statusCode(200)

		then:
			eventually {
				assertAssetsSize(15)
			}

		and:
			given()
				.header(jwtAuthorization(ADMIN))
				.contentType(ContentType.JSON)
				.param("page", "0")
				.param("size", "10")
				.when()
				.get("/api/metrics/registry-lookup")
				.then()
				.statusCode(200)
				.body("page", Matchers.is(0))
				.body("pageSize", Matchers.is(10))
				.body("content", Matchers.hasSize(1))
				.body("content[0].startDate", Matchers.is(isIso8601DateTime()))
				.body("content[0].registryLookupStatus", Matchers.equalTo("SUCCESSFUL"))
				.body("content[0].successShellDescriptorsFetchCount", Matchers.equalTo(1))
				.body("content[0].failedShellDescriptorsFetchCount", Matchers.equalTo(0))
				.body("content[0].shellDescriptorsFetchDelta", Matchers.equalTo(1))
				.body("content[0].endDate", Matchers.is(isIso8601DateTime()))
	}

	def "should return registry lookup metrics after asset shells lookup failed"() {
		given:
			oauth2ApiReturnsTechnicalUserToken()
			assetShellsLookupFailed()

		when:
			given()
				.header(jwtAuthorization(ADMIN))
				.contentType(ContentType.JSON)
				.when()
				.get("/api/registry/reload")
				.then()
				.statusCode(200)

		then:
			eventually {
				assertNoAssetsStored()
			}

		and:
			given()
				.header(jwtAuthorization(ADMIN))
				.contentType(ContentType.JSON)
				.param("page", "0")
				.param("size", "10")
				.when()
				.get("/api/metrics/registry-lookup")
				.then()
				.statusCode(200)
				.body("page", Matchers.is(0))
				.body("pageSize", Matchers.is(10))
				.body("content", Matchers.hasSize(1))
				.body("content[0].startDate", Matchers.is(isIso8601DateTime()))
				.body("content[0].registryLookupStatus", Matchers.equalTo("ERROR"))
				.body("content[0].successShellDescriptorsFetchCount", Matchers.equalTo(0))
				.body("content[0].failedShellDescriptorsFetchCount", Matchers.equalTo(1))
				.body("content[0].shellDescriptorsFetchDelta", Matchers.equalTo(0))
				.body("content[0].endDate", Matchers.is(isIso8601DateTime()))
	}

	def "should return registry lookup metrics after fetch registry shell descriptors lookup failed"() {
		given:
			oauth2ApiReturnsTechnicalUserToken()
			assetShellsLookupReturnsData()

		and:
			fetchRegistryShellDescriptorsLookupReturnsDataFailed()

		when:
			given()
				.header(jwtAuthorization(ADMIN))
				.contentType(ContentType.JSON)
				.when()
				.get("/api/registry/reload")
				.then()
				.statusCode(200)

		then:
			eventually {
				assertNoAssetsStored()
			}

		and:
			given()
				.header(jwtAuthorization(ADMIN))
				.contentType(ContentType.JSON)
				.param("page", "0")
				.param("size", "10")
				.when()
				.get("/api/metrics/registry-lookup")
				.then()
				.statusCode(200)
				.body("page", Matchers.is(0))
				.body("pageSize", Matchers.is(10))
				.body("content", Matchers.hasSize(1))
				.body("content[0].startDate", Matchers.is(isIso8601DateTime()))
				.body("content[0].registryLookupStatus", Matchers.equalTo("ERROR"))
				.body("content[0].successShellDescriptorsFetchCount", Matchers.equalTo(0))
				.body("content[0].failedShellDescriptorsFetchCount", Matchers.equalTo(1))
				.body("content[0].shellDescriptorsFetchDelta", Matchers.equalTo(0))
				.body("content[0].endDate", Matchers.is(isIso8601DateTime()))
	}

	def "should return registry lookup metrics with new delta"() {
		given:
			oauth2ApiReturnsTechnicalUserToken()
			assetShellsLookupReturnsData()
			fetchRegistryShellDescriptorsLookupReturnsData()

		and:
			irsApiTriggerJob()
			irsApiReturnsJobDetails()

		when:
			given()
				.header(jwtAuthorization(ADMIN))
				.contentType(ContentType.JSON)
				.when()
				.get("/api/registry/reload")
				.then()
				.statusCode(200)

		then:
			eventually {
				assertShellDescriptors().hasSize(1)
			}

		and:
			given()
				.header(jwtAuthorization(ADMIN))
				.contentType(ContentType.JSON)
				.param("page", "0")
				.param("size", "10")
				.when()
				.get("/api/metrics/registry-lookup")
				.then()
				.statusCode(200)
				.body("page", Matchers.is(0))
				.body("pageSize", Matchers.is(10))
				.body("content", Matchers.hasSize(1))
				.body("content[0].startDate", Matchers.is(isIso8601DateTime()))
				.body("content[0].registryLookupStatus", Matchers.equalTo("SUCCESSFUL"))
				.body("content[0].successShellDescriptorsFetchCount", Matchers.equalTo(1))
				.body("content[0].failedShellDescriptorsFetchCount", Matchers.equalTo(0))
				.body("content[0].shellDescriptorsFetchDelta", Matchers.equalTo(1))
				.body("content[0].endDate", Matchers.is(isIso8601DateTime()))

		when:
			assetShellsLookupReturnsDataForUpdate()
			fetchRegistryShellDescriptorsLookupReturnsDataForUpdate()

		and:
			given()
				.header(jwtAuthorization(ADMIN))
				.contentType(ContentType.JSON)
				.when()
				.get("/api/registry/reload")
				.then()
				.statusCode(200)

		then:
			eventually {
				assertShellDescriptors().hasSize(2)
			}

		and:
			given()
				.header(jwtAuthorization(ADMIN))
				.contentType(ContentType.JSON)
				.param("page", "0")
				.param("size", "10")
				.when()
				.get("/api/metrics/registry-lookup")
				.then()
				.statusCode(200)
				.body("page", Matchers.is(0))
				.body("pageSize", Matchers.is(10))
				.body("content", Matchers.hasSize(2))
				.body("content.registryLookupStatus", Matchers.containsInRelativeOrder("SUCCESSFUL"))
				.body("content[0].successShellDescriptorsFetchCount", Matchers.equalTo(2))
				.body("content[0].failedShellDescriptorsFetchCount", Matchers.equalTo(0))
				.body("content[0].shellDescriptorsFetchDelta", Matchers.equalTo(1))
				.body("content[1].successShellDescriptorsFetchCount", Matchers.equalTo(1))
				.body("content[1].failedShellDescriptorsFetchCount", Matchers.equalTo(0))
				.body("content[1].shellDescriptorsFetchDelta", Matchers.equalTo(1))
	}

	def "should not return registry lookup metrics when user is not authenticated"() {
		expect:
			given()
				.contentType(ContentType.JSON)
				.when()
				.get("/api/metrics/registry-lookup")
				.then()
				.statusCode(401)
	}

}
