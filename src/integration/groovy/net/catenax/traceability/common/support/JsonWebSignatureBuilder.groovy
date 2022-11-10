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

import net.catenax.traceability.common.security.JwtRole
import org.jose4j.jwk.RsaJsonWebKey
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims

class JsonWebSignatureBuilder {

	private final String claimsSubject
	private final String claimsIssuer
	private final RsaJsonWebKey rsaJsonWebKey
	private final String resourceClient

	JsonWebSignatureBuilder(RsaJsonWebKey rsaJsonWebKey, String resourceClient) {
		this.claimsSubject = UUID.randomUUID().toString()
		this.claimsIssuer = "http://localhost"
		this.rsaJsonWebKey = rsaJsonWebKey
		this.resourceClient = resourceClient
	}

	JsonWebSignature buildWithRoles(JwtRole... jwtRoles) {
		JwtClaims jwtClaims = new JwtClaims()
		jwtClaims.jwtId = UUID.randomUUID().toString()
		jwtClaims.issuer = claimsIssuer
		jwtClaims.subject = claimsSubject
		jwtClaims.setIssuedAtToNow()
		jwtClaims.setExpirationTimeMinutesInTheFuture(100)

		List<String> roles = jwtRoles.collect { it.description }.toList()
		Object resourceAccess = [(resourceClient): Map.of("roles", roles)]
		jwtClaims.setClaim("resource_access", resourceAccess)

		JsonWebSignature jsonWebSignature = new JsonWebSignature()
		jsonWebSignature.payload = jwtClaims.toJson()
		jsonWebSignature.key = rsaJsonWebKey.privateKey
		jsonWebSignature.algorithmHeaderValue = rsaJsonWebKey.algorithm
		jsonWebSignature.keyIdHeaderValue = rsaJsonWebKey.keyId
		jsonWebSignature.setHeader("typ", "JWT")

		return jsonWebSignature
	}
}
