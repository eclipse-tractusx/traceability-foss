package org.eclipse.tractusx.traceability.common.security;

import com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtRolesExtractorTest {
	@Test
	@DisplayName("Extract Jwt Roles successfully")
	void testExtractJwtResourceClientRoles() {
		// given
		String resourceClient = "unit-tests";
		Set<JwtRole> roles = Set.of(JwtRole.USER, JwtRole.ADMIN, JwtRole.SUPERVISOR);
		Set<String> mappedRoles = roles.stream().map(JwtRole::getDescription).collect(Collectors.toSet());
		Jwt token = createToken(mappedRoles, resourceClient);

		// when
		Set<JwtRole> extractedRoles = JwtRolesExtractor.extract(token, resourceClient);

		// then
		assertEquals(roles, extractedRoles);
	}

	@Test
	@DisplayName("Extract Only Mapped Jwt Roles successfully")
	void testExtractOnlyMappedJwtRoles() {

		// given
		String resourceClient = "unit-tests";
		Set<JwtRole> roles = Set.of(JwtRole.USER, JwtRole.ADMIN);
		Jwt token = createToken(roles.stream().map(JwtRole::getDescription).collect(Collectors.toSet()), resourceClient);

		// when
		Set<JwtRole> extractedRoles = JwtRolesExtractor.extract(token, resourceClient);

		// then
		assertEquals(roles, extractedRoles);
	}

	@Test
	@DisplayName("Extraction empty in case of unknown Resource Client")
	void testExtractNoRolesWithUnknownResource() {
		// given
		String resourceClient = "unit-tests";
		Set<JwtRole> roles = Set.of(JwtRole.USER, JwtRole.ADMIN, JwtRole.SUPERVISOR);
		Set<String> mappedRoles = roles.stream().map(JwtRole::getDescription).collect(Collectors.toSet());
		Jwt token = createToken(mappedRoles, resourceClient);

		// when
		Set<JwtRole> extractedRoles = JwtRolesExtractor.extract(token, "unknown-resource-client");

		// then
		assertTrue(extractedRoles.isEmpty());
	}

	private Jwt createToken(Set<String> roles, String resourceClient) {
		Jwt.Builder jwtBuilder = Jwt.withTokenValue("some-value")
			.issuer(UUID.randomUUID().toString())
			.subject(UUID.randomUUID().toString())
			.issuedAt(Instant.now())
			.expiresAt(Instant.now().plus(Duration.ofSeconds(60)))
			.header("alg", "RS256")
			.header("use", "sig")
			.header("typ", "JWT");

        List rolesList = new ArrayList(roles);
        LinkedTreeMap resourceAccessMappings = new LinkedTreeMap();
        resourceAccessMappings.put("roles", rolesList);
        Map<String, LinkedTreeMap> resourceAccess = Map.of(resourceClient, resourceAccessMappings);
        jwtBuilder.claim("resource_access", resourceAccess);
		return jwtBuilder.build();
	}
}
