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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.notification;

import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.v4.model.ContractOfferDescription;
import org.jetbrains.annotations.NotNull;

public class NegotiationInitiateRequestDto {

	private String connectorAddress;
	private String protocol = "ids-multipart";
	private String connectorId;
	private ContractOfferDescription offer;

	private NegotiationInitiateRequestDto() {

	}

	public String getConnectorAddress() {
		return connectorAddress;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getConnectorId() {
		return connectorId;
	}

	public @NotNull ContractOfferDescription getOffer() {
		return offer;
	}

	public static final class Builder {
		private final NegotiationInitiateRequestDto dto;

		private Builder() {
			dto = new NegotiationInitiateRequestDto();
		}

		public static Builder newInstance() {
			return new Builder();
		}

		public Builder connectorAddress(String connectorAddress) {
			dto.connectorAddress = connectorAddress;
			return this;
		}

		public Builder protocol(String protocol) {
			dto.protocol = protocol;
			return this;
		}

		public Builder connectorId(String connectorId) {
			dto.connectorId = connectorId;
			return this;
		}

		public Builder offerId(ContractOfferDescription offerId) {
			dto.offer = offerId;
			return this;
		}

		public NegotiationInitiateRequestDto build() {
			return dto;
		}
	}
}

