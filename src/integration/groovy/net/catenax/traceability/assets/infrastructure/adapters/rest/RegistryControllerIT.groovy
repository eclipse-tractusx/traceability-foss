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
import org.springframework.http.MediaType
import spock.util.concurrent.PollingConditions

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class RegistryControllerIT extends IntegrationSpec {

	private static final String BPN = "BPNL00000003AXS3"
	private static final String SHELL_DESCRIPTOR_ID = "urn:uuid:46e51326-0c00-4eae-85ea-d8a505b432e9"

	def "should synchronize descriptors and assets"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()

		and:
			assetShellsLookupReturnsData()
			fetchRegistryShellDescriptorsLookupReturnsData()

		and:
			irsApiTriggerJob()
			irsApiReturnsJobDetails()

		when:
			mvc.perform(get("/registry/fetch/$BPN")
				.contentType(MediaType.APPLICATION_JSON)
			).andExpect(status().isOk())

		then:
			new PollingConditions(timeout: 10, initialDelay: 0.5).eventually {
				assertDescriptorsSizeFor(SHELL_DESCRIPTOR_ID, 1)
				assertAssetsSize(13)
			}
	}

	def "should not synchronize descriptors and assets when asset shells lookup failed"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)
			keycloakApiReturnsToken()

		and:
			assetShellsLookupFailed()

		when:
			mvc.perform(get("/registry/fetch/$BPN")
				.contentType(MediaType.APPLICATION_JSON)
			).andExpect(status().isOk())

		then:
			new PollingConditions(timeout: 10, initialDelay: 2.0).eventually {
				assertNoDescriptorsStoredFor(SHELL_DESCRIPTOR_ID)
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
			mvc.perform(get("/registry/fetch/$BPN")
				.contentType(MediaType.APPLICATION_JSON)
			).andExpect(status().isOk())

		then:
			new PollingConditions(timeout: 10, initialDelay: 2.0).eventually {
				assertNoDescriptorsStoredFor(SHELL_DESCRIPTOR_ID)
				assertNoAssetsStored()
			}

		and:
			verifyIrsApiTriggerJobNotCalled()
	}
}
