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

package org.eclipse.tractusx.traceability.common.security;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public enum JwtRole {
	USER("User"),
	SUPERVISOR("Supervisor"),
	ADMIN("Admin");

	private final String description;

	private static final Map<String, JwtRole> ROLE_MAPPINGS;

	static {
		ROLE_MAPPINGS = Arrays.stream(JwtRole.values())
			.collect(Collectors.toMap(jwtRole -> jwtRole.description, jwtRole -> jwtRole));
	}

	JwtRole(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public static Optional<JwtRole> parse(String roleRaw) {
		return Optional.ofNullable(ROLE_MAPPINGS.get(roleRaw));
	}
}
