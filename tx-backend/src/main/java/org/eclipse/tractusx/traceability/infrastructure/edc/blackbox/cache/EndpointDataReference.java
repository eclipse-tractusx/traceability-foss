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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.cache;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Describes an endpoint serving data.
 */
@ToString
@JsonDeserialize(builder = EndpointDataReference.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EndpointDataReference {

	private final String id;
	private final String endpoint;
	private final String authKey;
	private final String authCode;
	private final Map<String, String> properties;

	private EndpointDataReference(String id, String endpoint, String authKey, String authCode, Map<String, String> properties) {
		this.id = id;
		this.endpoint = endpoint;
		this.authKey = authKey;
		this.authCode = authCode;
		this.properties = properties;
	}

	@NotNull
	public String getId() {
		return id;
	}

	@NotNull
	public String getEndpoint() {
		return endpoint;
	}

	@Nullable
	public String getAuthKey() {
		return authKey;
	}

	@Nullable
	public String getAuthCode() {
		return authCode;
	}

	@NotNull
	public Map<String, String> getProperties() {
		return properties;
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {
		private final Map<String, String> properties = new HashMap<>();
		private String id = UUID.randomUUID().toString();
		private String endpoint;
		private String authKey;
		private String authCode;

		private Builder() {
		}

		@JsonCreator
		public static Builder newInstance() {
			return new Builder();
		}

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder endpoint(String address) {
			this.endpoint = address;
			return this;
		}

		public Builder authKey(String authKey) {
			this.authKey = authKey;
			return this;
		}

		public Builder authCode(String authCode) {
			this.authCode = authCode;
			return this;
		}

		public Builder properties(Map<String, String> properties) {
			this.properties.putAll(properties);
			return this;
		}

		public EndpointDataReference build() {
			Objects.requireNonNull(endpoint, "endpoint");
			if (authKey != null) {
				Objects.requireNonNull(authCode, "authCode");
			}
			if (authCode != null) {
				Objects.requireNonNull(authKey, "authKey");
			}
			return new EndpointDataReference(id, endpoint, authKey, authCode, properties);
		}
	}
}
