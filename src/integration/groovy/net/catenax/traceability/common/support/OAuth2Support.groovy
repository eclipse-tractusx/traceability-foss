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

import io.restassured.http.Header
import net.catenax.traceability.common.security.JwtRole
import org.jose4j.jwk.JsonWebKeySet
import org.jose4j.jwk.RsaJsonWebKey
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService

trait OAuth2Support extends RsaJsonWebKeyProvider {

	@Value('${jwt.resource-client}')
	private String resourceClient

	@Autowired
	private InMemoryOAuth2AuthorizedClientService authorizedClientService

	void clearOAuth2Client() {
		authorizedClientService.removeAuthorizedClient("default", "feignClient")
	}

	Header jwtAuthorization(JwtRole... jwtRoles) {
		return new Header(HttpHeaders.AUTHORIZATION, jwtToken(jwtRoles))
	}

	Header jwtAuthorizationWithNoRole() {
		return new Header(HttpHeaders.AUTHORIZATION, jwtToken())
	}

	private String jwtToken(JwtRole... jwtRoles) {
		RsaJsonWebKey rsaJsonWebKey = rsaJsonWebKey()

		def token = new JsonWebSignatureBuilder(rsaJsonWebKey, resourceClient)
			.buildWithRoles(jwtRoles)
			.compactSerialization

		return "Bearer $token"
	}

	String jwk() {
		RsaJsonWebKey rsaJsonWebKey = rsaJsonWebKey()

		return new JsonWebKeySet(rsaJsonWebKey).toJson()
	}
}
