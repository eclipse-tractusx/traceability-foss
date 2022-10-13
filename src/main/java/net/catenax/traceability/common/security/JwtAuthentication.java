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

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

public class JwtAuthentication {

	public static final JwtAuthentication NO_ROLES = new JwtAuthentication(Set.of());

	private final Set<JwtRole> jwtRoles;

	public JwtAuthentication(Set<JwtRole> jwtRoles) {
		this.jwtRoles = Collections.unmodifiableSet(jwtRoles);
	}

	public boolean hasRole(JwtRole jwtRole) {
		return jwtRoles.contains(jwtRole);
	}

	public boolean hasAtLeastOneRole(JwtRole... jwtRole) {
		return Arrays.stream(jwtRole)
			.map(this::hasRole)
			.filter(hasRole -> hasRole)
			.findFirst()
			.orElse(false);
	}

	@Override
	public String toString() {
		return "JwtAuthentication{" +
			"jwtRoles=" + jwtRoles +
			'}';
	}
}
