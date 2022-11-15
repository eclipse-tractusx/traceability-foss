/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

package net.catenax.traceability.assets.infrastructure.adapters.rest

import net.catenax.traceability.IntegrationSpec
import net.catenax.traceability.common.support.AssetsSupport
import net.catenax.traceability.common.support.IrsApiSupport
import net.catenax.traceability.common.support.RegistrySupport
import net.catenax.traceability.common.support.ShellDescriptorSupport

import static io.restassured.RestAssured.given
import static net.catenax.traceability.common.security.JwtRole.ADMIN

class RegistryControllerIT extends IntegrationSpec implements IrsApiSupport, RegistrySupport, ShellDescriptorSupport, AssetsSupport {

	def "should synchronize descriptors and assets"() {
		given:
			String syncId = "urn:uuid:46e51326-0c00-4eae-85ea-d8a505b432e9"
			oauth2ApiReturnsTechnicalUserToken()

		and:
			assetShellsLookupReturnsData()
			fetchRegistryShellDescriptorsLookupReturnsData()

		and:
			irsApiTriggerJob()
			irsApiReturnsJobDetails()

		when:
			given()
				.header(jwtAuthorization(ADMIN))
				.when()
				.get("/api/registry/reload")
				.then()
				.statusCode(200)

		then:
			eventually {
				assertShellDescriptors()
					.hasSize(1)
					.containsShellDescriptorWithId(syncId)

				assertAssetsSize(15)
			}

		and:
			verifyOAuth2ApiCalledOnceForTechnicalUserToken()
			verifyIrsApiTriggerJobCalledOnce()
	}

	def "should synchronize descriptors and assets when IRS call fails"() {
		given:
			String syncId = "urn:uuid:46e51326-0c00-4eae-85ea-d8a505b432e9"
			oauth2ApiReturnsTechnicalUserToken()

		and:
			assetShellsLookupReturnsData()
			fetchRegistryShellDescriptorsLookupReturnsData()

		and:
			irsApiTriggerJob()
			irsJobDetailsApiFailed()

		when:
			given()
				.header(jwtAuthorization(ADMIN))
				.when()
				.get("/api/registry/reload")
				.then()
				.statusCode(200)

		then:
			eventually {
				assertShellDescriptors()
					.hasSize(1)
					.containsShellDescriptorWithId(syncId)

				assertAssetsSize(1)
			}

		and:
			verifyOAuth2ApiCalledOnceForTechnicalUserToken()
			verifyIrsApiTriggerJobCalledOnce()
	}

	def "should synchronize and update only new"() {
		given:
			String[] syncIds = ["urn:uuid:46e51326-0c00-4eae-85ea-d8a505b432e7", "urn:uuid:46e51326-0c00-4eae-85ea-d8a505b432e8"]
			String[] updateIds = ["urn:uuid:46e51326-0c00-4eae-85ea-d8a505b432e6", "urn:uuid:46e51326-0c00-4eae-85ea-d8a505b432e8"]
			oauth2ApiReturnsTechnicalUserToken()

		and:
			assetShellsLookupReturnsDataForUpdate()
			fetchRegistryShellDescriptorsLookupReturnsDataForUpdate()

		when:
			given()
				.header(jwtAuthorization(ADMIN))
				.when()
				.get("/api/registry/reload")
				.then()
				.statusCode(200)

		then:
			eventually {
				assertShellDescriptors()
					.hasSize(2)
					.containsShellDescriptorWithId(syncIds)
			}

		and:
			verifyOAuth2ApiCalledOnceForTechnicalUserToken()
			verifyIrsApiTriggerJobCalledOnceFor(syncIds)

		when:
			given()
				.header(jwtAuthorization(ADMIN))
				.when()
				.get("/api/registry/reload")
				.then()
				.statusCode(200)

		then:
			eventually {
				assertShellDescriptors()
					.hasSize(2)
					.containsShellDescriptorWithId(updateIds)
			}

		and:
			verifyIrsApiTriggerJobCalledOnceFor(updateIds[0])
	}

	def "should not synchronize descriptors and assets when asset shells lookup failed"() {
		given:
			oauth2ApiReturnsTechnicalUserToken()

		and:
			assetShellsLookupFailed()

		when:
			given()
				.header(jwtAuthorization(ADMIN))
				.when()
				.get("/api/registry/reload")
				.then()
				.statusCode(200)
		then:
			eventually {
				assertShellDescriptors().hasSize(0)
				assertNoAssetsStored()
			}

		and:
			verifyOAuth2ApiCalledOnceForTechnicalUserToken()
			verifyFetchRegistryShellDescriptorsLookupNotCalled()
			verifyIrsApiTriggerJobNotCalled()
			verifyIrsJobDetailsApiNotCalled()
	}

	def "should not synchronize descriptors and assets when fetch registry shell descriptors lookup failed"() {
		given:
			oauth2ApiReturnsTechnicalUserToken()

		and:
			assetShellsLookupReturnsData()
			fetchRegistryShellDescriptorsLookupReturnsDataFailed()

		when:
			given()
				.header(jwtAuthorization(ADMIN))
				.when()
				.get("/api/registry/reload")
				.then()
				.statusCode(200)

		then:
			eventually {
				assertShellDescriptors().hasSize(0)
				assertNoAssetsStored()
			}

		and:
			verifyOAuth2ApiCalledOnceForTechnicalUserToken()
			verifyIrsApiTriggerJobNotCalled()
	}
}
