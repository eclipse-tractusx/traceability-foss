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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation;

import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.message.RemoteMessage;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.offer.ContractOffer;

import java.util.Objects;

/**
 * Object that wraps the contract offer and provides additional information about e.g. protocol
 * and recipient.
 */
public class ContractOfferRequest implements RemoteMessage {

	private Type type = Type.COUNTER_OFFER;
	private String protocol;
	private String connectorId;
	private String connectorAddress;
	private String correlationId;
	private ContractOffer contractOffer;

	@Override
	public String getProtocol() {
		return protocol;
	}


	public String getConnectorAddress() {
		return connectorAddress;
	}

	public String getConnectorId() {
		return connectorId;
	}

	public String getCorrelationId() {
		return correlationId;
	}

	public Type getType() {
		return type;
	}

	public ContractOffer getContractOffer() {
		return contractOffer;
	}

	public enum Type {
		INITIAL,
		COUNTER_OFFER
	}

	public static class Builder {
		private final ContractOfferRequest contractOfferRequest;

		private Builder() {
			this.contractOfferRequest = new ContractOfferRequest();
		}

		public static Builder newInstance() {
			return new Builder();
		}

		public Builder protocol(String protocol) {
			this.contractOfferRequest.protocol = protocol;
			return this;
		}

		public Builder connectorId(String connectorId) {
			this.contractOfferRequest.connectorId = connectorId;
			return this;
		}

		public Builder connectorAddress(String connectorAddress) {
			this.contractOfferRequest.connectorAddress = connectorAddress;
			return this;
		}

		public Builder correlationId(String correlationId) {
			this.contractOfferRequest.correlationId = correlationId;
			return this;
		}

		public Builder contractOffer(ContractOffer contractOffer) {
			this.contractOfferRequest.contractOffer = contractOffer;
			return this;
		}

		public Builder type(Type type) {
			this.contractOfferRequest.type = type;
			return this;
		}

		public ContractOfferRequest build() {
			Objects.requireNonNull(contractOfferRequest.protocol, "protocol");
			Objects.requireNonNull(contractOfferRequest.connectorId, "connectorId");
			Objects.requireNonNull(contractOfferRequest.connectorAddress, "connectorAddress");
			Objects.requireNonNull(contractOfferRequest.contractOffer, "contractOffer");
			return contractOfferRequest;
		}
	}
}
