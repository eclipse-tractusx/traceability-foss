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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.catalog;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.message.RemoteMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * A request for a participant's {@link Catalog}.
 */
@JsonDeserialize(builder = CatalogRequest.Builder.class)
public class CatalogRequest implements RemoteMessage {

	private final String protocol;
	private final String connectorId;
	private final String connectorAddress;

	private CatalogRequest(@NotNull String protocol, @NotNull String connectorId, @NotNull String connectorAddress) {
		this.protocol = protocol;
		this.connectorId = connectorId;
		this.connectorAddress = connectorAddress;
	}

	@NotNull
	public String getProtocol() {
		return protocol;
	}

	@NotNull
	public String getConnectorId() {
		return connectorId;
	}

	@NotNull
	public String getConnectorAddress() {
		return connectorAddress;
	}

	public static class Builder {
		private String protocol;
		private String connectorId;
		private String connectorAddress;

		private Builder() {
		}

		@JsonCreator
		public static Builder newInstance() {
			return new Builder();
		}

		public Builder protocol(String protocol) {
			this.protocol = protocol;
			return this;
		}

		public Builder connectorId(String connectorId) {
			this.connectorId = connectorId;
			return this;
		}

		public Builder connectorAddress(String connectorAddress) {
			this.connectorAddress = connectorAddress;
			return this;
		}

		public CatalogRequest build() {
			Objects.requireNonNull(protocol, "protocol");
			Objects.requireNonNull(connectorId, "connectorId");
			Objects.requireNonNull(connectorAddress, "connectorAddress");

			return new CatalogRequest(protocol, connectorId, connectorAddress);
		}
	}
}
