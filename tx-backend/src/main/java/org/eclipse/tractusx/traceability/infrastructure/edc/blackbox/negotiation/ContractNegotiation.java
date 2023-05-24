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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.agreement.ContractAgreement;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.offer.ContractOffer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.ContractNegotiationStates.UNSAVED;

/**
 * Represents a contract negotiation.
 * <p>
 * Note: This class implements the negotiation process that is started by a consumer. For some use
 * cases, it may be interesting to initiate the contract negotiation as a provider.
 *
 * <p>
 * TODO: This is only placeholder
 * TODO: Implement state transitions
 * TODO: Add error details
 */
@JsonTypeName("dataspaceconnector:contractnegotiation")
@JsonDeserialize(builder = ContractNegotiation.Builder.class)
public class ContractNegotiation implements TraceCarrier {
	private String id;
	private String correlationId;
	private String counterPartyId;
	private String counterPartyAddress;
	private String protocol;
	private Type type = Type.CONSUMER;
	private int state = UNSAVED.code();
	private int stateCount;
	private long stateTimestamp;
	private String errorDetail;
	private ContractAgreement contractAgreement;
	private List<ContractOffer> contractOffers = new ArrayList<>();
	private Map<String, String> traceContext = new HashMap<>();

	public Type getType() {
		return type;
	}

	public String getId() {
		return id;
	}

	public String getCounterPartyId() {
		return counterPartyId;
	}

	public String getCounterPartyAddress() {
		return counterPartyAddress;
	}

	/**
	 * Returns the correlation id sent by the client or null if this is a client-side negotiation.
	 */
	public String getCorrelationId() {
		return correlationId;
	}

	/**
	 * Returns the data protocol used for this negotiation.
	 *
	 * @return The protocol.
	 */
	@NotNull
	public String getProtocol() {
		return protocol;
	}

	@Override
	public Map<String, String> getTraceContext() {
		return Collections.unmodifiableMap(traceContext);
	}

	/**
	 * Returns the current negotiation state.
	 *
	 * @return The current state code.
	 */
	public int getState() {
		return state;
	}

	/**
	 * Returns the current state count.
	 *
	 * @return The current state count.
	 */
	public int getStateCount() {
		return stateCount;
	}

	/**
	 * Returns the state timestamp.
	 *
	 * @return The state timestamp.
	 */
	public long getStateTimestamp() {
		return stateTimestamp;
	}

	/**
	 * Returns all contract offers which have been part of the negotiation process.
	 *
	 * @return The contract offers.
	 */
	public List<ContractOffer> getContractOffers() {
		return contractOffers;
	}

	/**
	 * Adds a new contract offer to this negotiation.
	 *
	 * @param offer The offer to add.
	 */
	public void addContractOffer(ContractOffer offer) {
		contractOffers.add(offer);
	}

	/**
	 * Returns the error detail.
	 *
	 * @return The error detail
	 */
	public String getErrorDetail() {
		return errorDetail;
	}

	/**
	 * Sets the error detail.
	 *
	 * @param errorDetail The error detail.
	 */
	public void setErrorDetail(String errorDetail) {
		this.errorDetail = errorDetail;
	}

	/**
	 * Returns the last offer in the list of contract offers.
	 */
	public ContractOffer getLastContractOffer() {
		var size = contractOffers.size();
		if (size == 0) {
			return null;
		}
		return contractOffers.get(size - 1);
	}

	/**
	 * Returns the finalized agreement or null if the negotiation has not been confirmed.
	 */
	public ContractAgreement getContractAgreement() {
		return contractAgreement;
	}

	/**
	 * Sets the agreement for this negotiation.
	 *
	 * @param agreement the agreement.
	 */
	public void setContractAgreement(ContractAgreement agreement) {
		contractAgreement = agreement;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ContractNegotiation that = (ContractNegotiation) o;
		return state == that.state && stateCount == that.stateCount && stateTimestamp == that.stateTimestamp && Objects.equals(id, that.id) &&
			Objects.equals(correlationId, that.correlationId) && Objects.equals(counterPartyId, that.counterPartyId) &&
			Objects.equals(protocol, that.protocol) && Objects.equals(traceContext, that.traceContext) &&
			type == that.type && Objects.equals(contractAgreement, that.contractAgreement) && Objects.equals(contractOffers, that.contractOffers);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, correlationId, counterPartyId, protocol, traceContext, type, state, stateCount, stateTimestamp, contractAgreement, contractOffers);
	}

	public enum Type {
		CONSUMER, PROVIDER
	}

	/**
	 * Builder for ContractNegotiation.
	 */
	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {
		private final ContractNegotiation negotiation;

		private Builder() {
			negotiation = new ContractNegotiation();
		}

		@JsonCreator
		public static Builder newInstance() {
			return new Builder();
		}

		public Builder id(String id) {
			negotiation.id = id;
			return this;
		}

		public Builder protocol(String protocol) {
			negotiation.protocol = protocol;
			return this;
		}

		public Builder state(int state) {
			negotiation.state = state;
			return this;
		}

		public Builder stateCount(int stateCount) {
			negotiation.stateCount = stateCount;
			return this;
		}

		public Builder stateTimestamp(long stateTimestamp) {
			negotiation.stateTimestamp = stateTimestamp;
			return this;
		}

		public Builder counterPartyId(String id) {
			negotiation.counterPartyId = id;
			return this;
		}

		public Builder counterPartyAddress(String address) {
			negotiation.counterPartyAddress = address;
			return this;
		}

		public Builder correlationId(String id) {
			negotiation.correlationId = id;
			return this;
		}

		public Builder contractAgreement(ContractAgreement agreement) {
			negotiation.contractAgreement = agreement;
			return this;
		}

		//used mainly for JSON deserialization
		public Builder contractOffers(List<ContractOffer> contractOffers) {
			negotiation.contractOffers = contractOffers;
			return this;
		}

		public Builder contractOffer(ContractOffer contractOffer) {
			negotiation.contractOffers.add(contractOffer);
			return this;
		}

		public Builder type(Type type) {
			negotiation.type = type;
			return this;
		}

		public Builder errorDetail(String errorDetail) {
			negotiation.errorDetail = errorDetail;
			return this;
		}

		public Builder traceContext(Map<String, String> traceContext) {
			negotiation.traceContext = traceContext;
			return this;
		}

		public ContractNegotiation build() {
			Objects.requireNonNull(negotiation.id);
			Objects.requireNonNull(negotiation.counterPartyId);
			Objects.requireNonNull(negotiation.counterPartyAddress);
			Objects.requireNonNull(negotiation.protocol);
			if (Type.PROVIDER == negotiation.type) {
				Objects.requireNonNull(negotiation.correlationId);
			}
			return negotiation;
		}
	}
}
