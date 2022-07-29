package net.catenax.traceability.common.security;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public enum KeycloakRole {
	USER("User"),
	SUPERVISOR("Supervisor"),
	ADMIN("Admin");

	private final String description;

	private static final Map<String, KeycloakRole> ROLE_MAPPINGS;

	static {
		ROLE_MAPPINGS = Arrays.stream(KeycloakRole.values())
			.collect(Collectors.toMap(keycloakRole -> keycloakRole.description, keycloakRole -> keycloakRole));
	}

	KeycloakRole(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public static Optional<KeycloakRole> parse(String roleRaw) {
		return Optional.ofNullable(ROLE_MAPPINGS.get(roleRaw));
	}
}
