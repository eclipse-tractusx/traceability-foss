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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.offer.ContractOffer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * DTO representing catalog containing {@link ContractOffer}s.
 */
@JsonDeserialize(
	builder = Catalog.Builder.class
)
public class Catalog {
	private final String id;
	private final List<ContractOffer> contractOffers;

	private Catalog(@NotNull String id, @NotNull List<ContractOffer> contractOffers) {
		this.id = Objects.requireNonNull(id);
		this.contractOffers = Objects.requireNonNull(contractOffers);
	}

	public String getId() {
		return id;
	}

	public List<ContractOffer> getContractOffers() {
		return contractOffers;
	}

	@JsonPOJOBuilder(withPrefix = "")
	public static class Builder {
		private String id;
		private List<ContractOffer> contractOffers;

		public static Builder newInstance() {
			return new Builder();
		}

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder contractOffers(List<ContractOffer> contractOffers) {
			this.contractOffers = contractOffers;
			return this;
		}

		public Catalog build() {
			return new Catalog(id, contractOffers);
		}

	}
}
