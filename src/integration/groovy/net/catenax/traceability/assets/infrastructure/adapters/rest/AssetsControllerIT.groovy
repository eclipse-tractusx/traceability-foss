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
import net.catenax.traceability.common.security.KeycloakRole
import net.catenax.traceability.common.support.AssetsSupport
import net.catenax.traceability.common.support.IrsApiSupport
import org.hamcrest.Matchers
import org.springframework.http.MediaType
import spock.util.concurrent.PollingConditions

import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.not
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class AssetsControllerIT extends IntegrationSpec implements IrsApiSupport, AssetsSupport {

	def "should synchronize assets"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()
			irsApiTriggerJob()
			irsApiReturnsJobDetails()

		when:
			mvc.perform(post("/assets/sync")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJson(
					[
						globalAssetIds: ["urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb"]
					]
				))
			).andExpect(status().isOk())

		then:
			new PollingConditions(timeout: 10, initialDelay: 0.5).eventually {
				assertAssetsSize(13)
			}
	}

	def "should synchronize assets using retry"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()

		and:
			irsApiTriggerJob()

		and:
			irsApiReturnsJobInRunningAndCompleted()

		when:
			mvc.perform(post("/assets/sync")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJson(
					[
						globalAssetIds: ["urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb"]
					]
				))
			).andExpect(status().isOk())

		then:
			new PollingConditions(timeout: 15, initialDelay: 0.5).eventually {
				assertAssetsSize(13)
			}
	}

	def "should not synchronize assets when irs failed to trigger job"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()
			irsApiTriggerJobFailed()

		when:
			mvc.perform(post("/assets/sync")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJson(
					[
						globalAssetIds: ["urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb"]
					]
				))
			).andExpect(status().isOk())

		then:
			new PollingConditions(timeout: 10, initialDelay: 3).eventually {
				assertNoAssetsStored()
			}

		and:
			verifyIrsJobDetailsApiNotCalled()
	}

	def "should not synchronize assets when irs failed to return job details"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()
			irsApiTriggerJob()

		and:
			irsJobDetailsApiFailed()

		when:
			mvc.perform(post("/assets/sync")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJson(
					[
						globalAssetIds: ["urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb"]
					]
				))
			).andExpect(status().isOk())

		then:
			new PollingConditions(timeout: 10, initialDelay: 2).eventually {
				assertNoAssetsStored()
			}
	}

	def "should not synchronize assets when irs keeps returning job in running state"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()
			irsApiTriggerJob()

		and:
			irsApiReturnsJobInRunningState()

		when:
			mvc.perform(post("/assets/sync")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJson(
					[
						globalAssetIds: ["urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb"]
					]
				))
			).andExpect(status().isOk())

		then:
			new PollingConditions(timeout: 10, initialDelay: 2).eventually {
				assertNoAssetsStored()
			}
	}

	def "should return assets for authenticated user with role"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)

		expect:
			mvc.perform(get("/assets/1234").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
	}

	def "should return assets with manufacturer name"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)

		and:
			defaultAssetsStored()

		expect:
			mvc.perform(get("/assets").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.content[*].manufacturerName', not(equalTo("--"))))
	}

	def "should return assets country map"() {
		given:
			authenticatedUser()

		expect:
			mvc.perform(get("/assets/countries").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
	}

	def "should not return assets country map when user is not authorized"() {
		given:
			unauthenticatedUser()

		expect:
			mvc.perform(get("/assets/countries").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
	}

	def "should not return assets when user is not authenticated"() {
		given:
			unauthenticatedUser()

		expect:
			mvc.perform(get("/assets/1234").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
	}

	def "should get children asset"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()

		and:
			defaultAssetsStored()

		expect:
			mvc.perform(get("/assets/urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb/children/urn:uuid:587cfb38-7149-4f06-b1e0-0e9b6e98be2a")
				.contentType(MediaType.APPLICATION_JSON)
			)
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.id', Matchers.is("urn:uuid:587cfb38-7149-4f06-b1e0-0e9b6e98be2a")))
	}

	def "should return 404 when children asset is not found"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()

		and:
			defaultAssetsStored()

		expect:
			mvc.perform(get("/assets/urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb/children/unknown")
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isNotFound())
	}

	def "should get a page of assets"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()

		and:
			defaultAssetsStored()

		expect:
			mvc.perform(get("/assets")
				.queryParam("page", "2")
				.queryParam("size", "2")
				.contentType(MediaType.APPLICATION_JSON)
			)
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.page', Matchers.is(2)))
				.andExpect(jsonPath('$.pageSize', Matchers.is(2)))
	}

	def "should not update quality type for not existing asset"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)

		expect:
			mvc.perform(patch("/assets/1234")
				.contentType(MediaType.APPLICATION_JSON)
				.content(
					asJson(
						[qualityType: 'Critical']
					)
				),
			)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath('$.message', equalTo("Asset with id 1234 was not found.")))
	}

	def "should not update quality type with invalid request body"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)

		expect:
			mvc.perform(patch("/assets/1234")
				.contentType(MediaType.APPLICATION_JSON)
				.content(
					asJson(
						requestBody
					)
				),
			)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath('$.message', equalTo(errorMessage)))

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
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()

		and:
			defaultAssetsStored()

		and:
			def existingAssetId = "urn:uuid:1ae94880-e6b0-4bf3-ab74-8148b63c0640"

		expect:
			mvc.perform(get("/assets/$existingAssetId").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.qualityType', equalTo("Ok")))

		and:
			authenticatedUser(KeycloakRole.ADMIN)

			mvc.perform(patch("/assets/$existingAssetId")
				.contentType(MediaType.APPLICATION_JSON)
				.content(
					asJson(
						[qualityType: 'Critical']
					)
				),
			)
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.qualityType', equalTo("Critical")))

		and:
			authenticatedUser(KeycloakRole.ADMIN)

			mvc.perform(get("/assets/$existingAssetId").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.qualityType', equalTo("Critical")))
	}
}
