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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.agreement;

import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.message.RemoteMessage;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy.Policy;

import java.util.Objects;

public class ContractAgreementRequest implements RemoteMessage {

	private String protocol;
	private String connectorId;
	private String connectorAddress;
	private String correlationId;
	private ContractAgreement contractAgreement;
	private Policy policy;

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

	public ContractAgreement getContractAgreement() {
		return contractAgreement;
	}

	public Policy getPolicy() {
		return policy;
	}

	public static class Builder {
		private final ContractAgreementRequest contractAgreementRequest;

		private Builder() {
			this.contractAgreementRequest = new ContractAgreementRequest();
		}

		public static Builder newInstance() {
			return new Builder();
		}

		public Builder protocol(String protocol) {
			this.contractAgreementRequest.protocol = protocol;
			return this;
		}

		public Builder connectorId(String connectorId) {
			this.contractAgreementRequest.connectorId = connectorId;
			return this;
		}

		public Builder connectorAddress(String connectorAddress) {
			this.contractAgreementRequest.connectorAddress = connectorAddress;
			return this;
		}

		public Builder correlationId(String correlationId) {
			this.contractAgreementRequest.correlationId = correlationId;
			return this;
		}

		public Builder contractAgreement(ContractAgreement contractAgreement) {
			this.contractAgreementRequest.contractAgreement = contractAgreement;
			return this;
		}

		public Builder policy(Policy policy) {
			this.contractAgreementRequest.policy = policy;
			return this;
		}

		public ContractAgreementRequest build() {
			Objects.requireNonNull(contractAgreementRequest.protocol, "protocol");
			Objects.requireNonNull(contractAgreementRequest.connectorId, "connectorId");
			Objects.requireNonNull(contractAgreementRequest.connectorAddress, "connectorAddress");
			Objects.requireNonNull(contractAgreementRequest.contractAgreement, "contractAgreement");
			Objects.requireNonNull(contractAgreementRequest.policy, "policy");
			Objects.requireNonNull(contractAgreementRequest.correlationId, "correlationId");
			return contractAgreementRequest;
		}
	}
}
