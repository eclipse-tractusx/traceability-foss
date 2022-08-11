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

package net.catenax.traceability.common.support

import net.catenax.traceability.common.security.KeycloakRole
import org.keycloak.adapters.RefreshableKeycloakSecurityContext
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount
import org.keycloak.adapters.tomcat.SimplePrincipal
import org.keycloak.representations.AccessToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken

import static java.util.Collections.emptySet

trait KeycloakSupport {

	@Value('${keycloak.resource}')
	private String resourceRealm

	@Autowired
	private InMemoryOAuth2AuthorizedClientService authorizedClientService

	void authenticatedUserWithNoRole() {
		authenticatedUser()
	}

	void authenticatedUser(KeycloakRole... keycloakRoles) {
		def token = createToken(keycloakRoles)
		def context = new RefreshableKeycloakSecurityContext(token: token)
		def principal = new SimplePrincipal(UUID.randomUUID().toString())
		def simpleKeycloakAccount = new SimpleKeycloakAccount(principal, emptySet(), context)
		def authentication = new PreAuthenticatedAuthenticationToken(principal, null, emptySet())

		authentication.setDetails(simpleKeycloakAccount)

		setAuthentication(authentication)
	}

	private AccessToken createToken(KeycloakRole... keycloakRoles) {
		def mappedRoles = keycloakRoles
			.collect { it.getDescription() }
			.toSet()

		def token = new AccessToken()
		def access = new AccessToken.Access()
		access.roles(mappedRoles)
		token.setResourceAccess([(resourceRealm): access])

		return token
	}

	void unauthenticatedUser() {
		clearAuthentication()
	}

	private setAuthentication(Authentication authentication) {
		SecurityContextHolder.getContext().setAuthentication(authentication)
	}

	void clearAuthentication() {
		SecurityContextHolder.clearContext()
		authorizedClientService.removeAuthorizedClient("keycloak", "feignClient")
	}
}
