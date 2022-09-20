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
import net.catenax.traceability.common.support.RegistrySupport
import net.catenax.traceability.common.support.ShellDescriptorSupport
import org.springframework.http.MediaType

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class RegistryControllerIT extends IntegrationSpec implements IrsApiSupport, RegistrySupport, ShellDescriptorSupport, AssetsSupport {

	def "should synchronize descriptors and assets"() {
		given:
			String syncId = "urn:uuid:46e51326-0c00-4eae-85ea-d8a505b432e9"
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()

		and:
			assetShellsLookupReturnsData()
			fetchRegistryShellDescriptorsLookupReturnsData()

		and:
			irsApiTriggerJob()
			irsApiReturnsJobDetails()

		when:
			mvc.perform(get("/registry/reload")
				.contentType(MediaType.APPLICATION_JSON)
			).andExpect(status().isOk())

		then:
			eventually {
				assertShellDescriptors()
					.hasSize(1)
					.containsShellDescriptorWithId(syncId)

				assertAssetsSize(14)
			}
	}

	def "should synchronize and update only new"() {
		given:
			String[] syncIds = ["urn:uuid:46e51326-0c00-4eae-85ea-d8a505b432e7", "urn:uuid:46e51326-0c00-4eae-85ea-d8a505b432e8"]
			String[] updateIds = ["urn:uuid:46e51326-0c00-4eae-85ea-d8a505b432e6", "urn:uuid:46e51326-0c00-4eae-85ea-d8a505b432e8"]
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()

		and:
			assetShellsLookupReturnsDataForUpdate()
			fetchRegistryShellDescriptorsLookupReturnsDataForUpdate()

		when:
			mvc.perform(get("/registry/reload")
				.contentType(MediaType.APPLICATION_JSON)
			).andExpect(status().isOk())

		then:
			eventually {
				assertShellDescriptors()
					.hasSize(2)
					.containsShellDescriptorWithId(syncIds)
			}

		and:
			verifyIrsApiTriggerJobCalledOnceFor(syncIds)

		when:
			mvc.perform(get("/registry/reload")
				.contentType(MediaType.APPLICATION_JSON)
			).andExpect(status().isOk())

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
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()

		and:
			assetShellsLookupFailed()

		when:
			mvc.perform(get("/registry/reload")
				.contentType(MediaType.APPLICATION_JSON)
			).andExpect(status().isOk())

		then:
			eventually {
				assertShellDescriptors().hasSize(0)
				assertNoAssetsStored()
			}

		and:
			verifyFetchRegistryShellDescriptorsLookupNotCalled()
			verifyIrsApiTriggerJobNotCalled()
			verifyIrsJobDetailsApiNotCalled()
	}

	def "should not synchronize descriptors and assets when fetch registry shell descriptors lookup failed"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()

		and:
			assetShellsLookupReturnsData()
			fetchRegistryShellDescriptorsLookupReturnsDataFailed()

		when:
			mvc.perform(get("/registry/reload")
				.contentType(MediaType.APPLICATION_JSON)
			).andExpect(status().isOk())

		then:
			eventually {
				assertShellDescriptors().hasSize(0)
				assertNoAssetsStored()
			}

		and:
			verifyIrsApiTriggerJobNotCalled()
	}
}
