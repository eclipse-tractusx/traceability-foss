package net.catenax.traceability.common.security;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class KeycloakAuthentication {

	public static final KeycloakAuthentication NO_ROLES = new KeycloakAuthentication(Set.of());

	private final Set<KeycloakRole> keycloakRoles;

	public KeycloakAuthentication(Set<KeycloakRole> keycloakRoles) {
		this.keycloakRoles = Collections.unmodifiableSet(keycloakRoles);
	}

	public boolean hasRole(KeycloakRole keycloakRole) {
		return keycloakRoles.contains(keycloakRole);
	}

	public boolean hasAtLeastOneRole(KeycloakRole... keycloakRole) {
		return Arrays.stream(keycloakRole)
			.map(this::hasRole)
			.filter(hasRole -> hasRole)
			.findFirst()
			.orElse(false);
	}

	@Override
	public String toString() {
		return "KeycloakAuthentication{" +
			"keycloakRoles=" + keycloakRoles +
			'}';
	}
}
