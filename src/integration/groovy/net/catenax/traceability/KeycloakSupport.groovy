package net.catenax.traceability

import net.catenax.traceability.config.security.KeycloakRole
import org.keycloak.adapters.RefreshableKeycloakSecurityContext
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount
import org.keycloak.adapters.tomcat.SimplePrincipal
import org.keycloak.representations.AccessToken
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken

import static java.util.Collections.emptySet

trait KeycloakSupport {

	@Value('${keycloak.resource}')
	private String resourceRealm

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
	}
}
