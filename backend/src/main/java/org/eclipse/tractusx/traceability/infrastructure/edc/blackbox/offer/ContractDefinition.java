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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.offer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.asset.AssetSelectorExpression;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@JsonDeserialize(builder = ContractDefinition.Builder.class)
public class ContractDefinition {
	private String id;
	private String accessPolicyId;
	private String contractPolicyId;
	private AssetSelectorExpression selectorExpression;

	private ContractDefinition() {
	}

	public String getId() {
		return id;
	}

	@NotNull
	public String getAccessPolicyId() {
		return accessPolicyId;
	}

	@NotNull
	public String getContractPolicyId() {
		return contractPolicyId;
	}

	@NotNull
	public AssetSelectorExpression getSelectorExpression() {
		return selectorExpression;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ContractDefinition that = (ContractDefinition) o;
		return Objects.equals(id, that.id) && Objects.equals(accessPolicyId, that.accessPolicyId) && Objects.equals(contractPolicyId, that.contractPolicyId) && Objects.equals(selectorExpression, that.selectorExpression);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, accessPolicyId, contractPolicyId, selectorExpression);
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {
		private final ContractDefinition definition;

		private Builder() {
			definition = new ContractDefinition();
		}

		public static Builder newInstance() {
			return new Builder();
		}

		public Builder id(String id) {
			definition.id = id;
			return this;
		}

		public Builder accessPolicyId(String policyId) {
			definition.accessPolicyId = policyId;
			return this;
		}

		public Builder contractPolicyId(String policyId) {
			definition.contractPolicyId = policyId;
			return this;
		}

		public Builder selectorExpression(AssetSelectorExpression expression) {
			definition.selectorExpression = expression;
			return this;
		}

		public ContractDefinition build() {
			Objects.requireNonNull(definition.id);
			Objects.requireNonNull(definition.accessPolicyId);
			Objects.requireNonNull(definition.contractPolicyId);
			Objects.requireNonNull(definition.selectorExpression);
			return definition;
		}
	}
}
