package org.eclipse.tractusx.traceability.common.security;

import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class JwtRolesExtractorTest {
	@Test
	@DisplayName("should extract jwt resource client roles")
	void shouldExtractJwtResourceClientRoles() {
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
	@DisplayName("should extract only mapped jwt roles")
	void shouldExtractOnlyMappedJwtRoles() {
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
	@DisplayName("should not extract roles from jwt unknown resource client")
	void shouldNotExtractRolesFromJwtUnknownResourceClient() {
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

		JSONArray jsonArray = new JSONArray();
		roles.forEach(jsonArray::appendElement);

		Map<String, JSONObject> resourceAccess = Map.of(resourceClient, new JSONObject(Map.of("roles", jsonArray)));
		jwtBuilder.claim("resource_access", resourceAccess);

		return jwtBuilder.build();
	}
}
