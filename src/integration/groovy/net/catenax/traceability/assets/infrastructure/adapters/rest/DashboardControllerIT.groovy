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
import org.springframework.http.MediaType
import spock.lang.Unroll

import static net.catenax.traceability.common.security.KeycloakRole.ADMIN
import static net.catenax.traceability.common.security.KeycloakRole.SUPERVISOR
import static net.catenax.traceability.common.security.KeycloakRole.USER
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.nullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class DashboardControllerIT extends IntegrationSpec implements AssetsSupport {

	@Unroll
	def "should return all dashboard information for user with #role role"() {
		given:
			authenticatedUser(role)
			keycloakApiReturnsToken()
			defaultAssetsStored()

		expect:
			mvc.perform(get("/dashboard").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.myItems', equalTo(1)))
				.andExpect(jsonPath('$.otherParts', equalTo(12)))

		where:
			role << [SUPERVISOR, ADMIN]
	}

	def "should return only 'my items' dashboard information for user with USER role"() {
		given:
			authenticatedUser(USER)
			keycloakApiReturnsToken()
			defaultAssetsStored()

		expect:
			mvc.perform(get("/dashboard").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.myItems', equalTo(1)))
				.andExpect(jsonPath('$.otherParts', nullValue()))
	}

	def "should not return dashboard information for user without role"() {
		given:
			authenticatedUserWithNoRole()

		expect:
			mvc.perform(get("/dashboard").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath('$.message', equalTo("User has invalid role to access the dashboard.")))
	}

	def "should return dashboard information for pending investigation"() {
		given:
			String assetId = "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978"
			authenticatedUser(ADMIN)
			defaultAssetsStored()

		when:
			mvc.perform(post("/investigations")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJson(
					[
						partIds: [assetId],
						description: "at least 15 characters long investigation description"
					]
				))
			).andExpect(status().isOk())

		then:
			mvc.perform(get("/dashboard").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.myItems', equalTo(1)))
				.andExpect(jsonPath('$.otherParts', equalTo(12)))
				.andExpect(jsonPath('$.investigations', equalTo(1)))
	}
}
