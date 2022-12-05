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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.transfer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.negotiation.TraceCarrier;

import java.util.Map;
import java.util.Objects;

/**
 * A request to transfer data from a source to destination.
 */
@JsonTypeName("dataspaceconnector:dataflowrequest")
@JsonDeserialize(builder = DataFlowRequest.Builder.class)
public class DataFlowRequest implements Polymorphic, TraceCarrier {
	private String id;
	private String processId;

	private DataAddress sourceDataAddress;
	private DataAddress destinationDataAddress;

	private boolean trackable;

	private Map<String, String> properties = Map.of();
	private Map<String, String> traceContext = Map.of();

	private DataFlowRequest() {
	}

	/**
	 * The unique request id. Request ids are provided by the originating consumer and must be unique.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Returns the process id this request is associated with.
	 */
	public String getProcessId() {
		return processId;
	}

	/**
	 * The source address of the data.
	 */
	public DataAddress getSourceDataAddress() {
		return sourceDataAddress;
	}

	/**
	 * The target address the data is to be sent to.
	 */
	public DataAddress getDestinationDataAddress() {
		return destinationDataAddress;
	}

	/**
	 * Returns true if the request must be tracked for delivery guarantees.
	 */
	public boolean isTrackable() {
		return trackable;
	}

	/**
	 * Custom properties that are passed to the provider connector.
	 */
	public Map<String, String> getProperties() {
		return properties;
	}

	/**
	 * Trace context for this request
	 */
	@Override
	public Map<String, String> getTraceContext() {
		return traceContext;
	}

	/**
	 * A builder initialized with the current DataFlowRequest
	 */
	public Builder toBuilder() {
		return new Builder(this);
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {
		private final DataFlowRequest request;

		private Builder() {
			this(new DataFlowRequest());
		}

		private Builder(DataFlowRequest request) {
			this.request = request;
		}

		@JsonCreator
		public static Builder newInstance() {
			return new Builder();
		}

		public Builder id(String id) {
			request.id = id;
			return this;
		}

		public Builder processId(String id) {
			request.processId = id;
			return this;
		}

		public Builder destinationType(String type) {
			if (request.destinationDataAddress == null) {
				request.destinationDataAddress = DataAddress.Builder.newInstance().type(type).build();
			} else {
				request.destinationDataAddress.setType(type);
			}
			return this;
		}

		public Builder sourceDataAddress(DataAddress destination) {
			request.sourceDataAddress = destination;
			return this;
		}

		public Builder destinationDataAddress(DataAddress destination) {
			request.destinationDataAddress = destination;
			return this;
		}

		public Builder trackable(boolean value) {
			request.trackable = value;
			return this;
		}

		public Builder properties(Map<String, String> value) {
			request.properties = value == null ? null : Map.copyOf(value);
			return this;
		}

		public Builder traceContext(Map<String, String> value) {
			request.traceContext = value;
			return this;
		}

		public DataFlowRequest build() {
			Objects.requireNonNull(request.processId, "processId");
			Objects.requireNonNull(request.sourceDataAddress, "sourceDataAddress");
			Objects.requireNonNull(request.destinationDataAddress, "destinationDataAddress");
			Objects.requireNonNull(request.traceContext, "traceContext");
			return request;
		}

	}
}
