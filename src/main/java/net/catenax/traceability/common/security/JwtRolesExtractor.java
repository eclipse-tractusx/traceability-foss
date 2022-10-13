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

package net.catenax.traceability.common.security;

import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

class JwtRolesExtractor {

	private static final String RESOURCE_ACCESS = "resource_access";
	private static final String ROLES = "roles";

	private JwtRolesExtractor() {
	}

	static Set<JwtRole> extract(Jwt jwtToken, String resourceClient) {
		Object resourceAccess = Optional.ofNullable(jwtToken.getClaimAsMap(RESOURCE_ACCESS))
			.flatMap(it -> Optional.ofNullable(it.get(resourceClient)))
			.orElse(null);

		if (resourceAccess instanceof JSONObject resourceAccessCasted) {
			Object roles = resourceAccessCasted.get(ROLES);

			if (roles instanceof JSONArray rolesArray) {
				return rolesArray.stream()
					.map(Object::toString)
					.map(JwtRole::parse)
					.filter(Optional::isPresent)
					.map(Optional::get)
					.collect(Collectors.toSet());
			}
		}

		return Collections.emptySet();
	}
}
