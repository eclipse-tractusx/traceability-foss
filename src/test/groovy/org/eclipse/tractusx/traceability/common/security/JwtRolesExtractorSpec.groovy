/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.common.security

import com.nimbusds.jose.shaded.json.JSONArray
import com.nimbusds.jose.shaded.json.JSONObject
import org.springframework.security.oauth2.jwt.Jwt
import spock.lang.Specification

import java.time.Duration
import java.time.Instant

class JwtRolesExtractorSpec extends Specification {

	def "should extract jwt resource client roles"() {
		given:
			String resourceClient = "unit-tests"

		and:
			Set<String> mappedRoles = roles
				.each { it -> it.description }
				.collect { it.description }

		and:
			Jwt token = createToken(mappedRoles, resourceClient)

		when:
			Set<JwtRole> extractedRoles = JwtRolesExtractor.extract(token, resourceClient)

		then:
			extractedRoles == roles

		where:
			_ | roles
			_ | [JwtRole.USER] as Set
			_ | [JwtRole.ADMIN] as Set
			_ | [JwtRole.SUPERVISOR] as Set
			_ | [JwtRole.USER, JwtRole.ADMIN, JwtRole.SUPERVISOR] as Set
	}

	def "should extract only mapped jwt roles"() {
		given:
			String resourceClient = "unit-tests"

		and:
			Jwt token = createToken(roles.toSet(), resourceClient)

		when:
			Set<JwtRole> extractedRoles = JwtRolesExtractor.extract(token, resourceClient)

		then:
			extractedRoles == result

		where:
			roles                                                     | result
			["Unknown"]                                               | [] as Set
			["User", "someUnknownRole1", "Admin", "someUnknownRole2"] | [JwtRole.USER, JwtRole.ADMIN] as Set

	}

	def "shouldn't not extract roles from jwt unknown resource client"() {
		given:
			Set<String> mappedRoles = roles
				.each { it -> it.description }
				.collect { it.description }

		and:
			Jwt token = createToken(mappedRoles, "unit-tests")

		when:
			Set<JwtRole> extractedRoles = JwtRolesExtractor.extract(token, "unknown-resource-client")

		then:
			extractedRoles.isEmpty()

		where:
			_ | roles
			_ | [JwtRole.USER] as Set
			_ | [JwtRole.ADMIN] as Set
			_ | [JwtRole.SUPERVISOR] as Set
			_ | [JwtRole.USER, JwtRole.ADMIN, JwtRole.SUPERVISOR] as Set
	}

	private static Jwt createToken(Set<String> roles, String resourceClient) {
		Jwt.Builder jwtBuilder = Jwt.withTokenValue("some-value")
			.issuer(UUID.randomUUID().toString())
			.subject(UUID.randomUUID().toString())
			.issuedAt(Instant.now())
			.expiresAt(Instant.now() + Duration.ofSeconds(60))
			.header("alg", "RS256")
			.header("use", "sig")
			.header("typ", "JWT")

		JSONArray jSONArray = new JSONArray()
		roles.each { it -> jSONArray.appendElement(it) }
		Object resourceAccess = [(resourceClient): new JSONObject(Map.of("roles", jSONArray))]
		jwtBuilder.claim("resource_access", resourceAccess)

		return jwtBuilder.build()
	}
}
