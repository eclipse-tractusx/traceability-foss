package net.catenax.traceability

import org.keycloak.KeycloakPrincipal
import org.keycloak.adapters.tomcat.SimplePrincipal
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken

trait KeycloakSupport {

	enum KeycloakRole {
		OFFLINE_ACCESS_ROLE("offline_access"),
		UMA_ROLE("uma_authorization");

		private final String raw

		private KeycloakRole(String raw) {
			this.raw = raw
		}
	}

	void authenticatedUser(KeycloakRole ... roles) {
		def anAuthorities = roles
			.collect { it.raw}
			.collect { it -> new SimpleGrantedAuthority(it)}

		def token = new PreAuthenticatedAuthenticationToken(new SimplePrincipal(UUID.randomUUID().toString()), null, anAuthorities)

		setAuthentication(token)
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
