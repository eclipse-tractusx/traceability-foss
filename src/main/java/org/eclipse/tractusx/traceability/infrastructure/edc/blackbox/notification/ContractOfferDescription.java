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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy.Policy;

public class ContractOfferDescription {
	private final String offerId;
	private final String assetId;
	private final String policyId;
	private final Policy policy;

	@JsonCreator
	public ContractOfferDescription(@JsonProperty("offerId") String offerId, @JsonProperty("assetId") String assetId, @JsonProperty("policyId") String policyId, @JsonProperty("policy") Policy policy) {
		this.offerId = offerId;
		this.assetId = assetId;
		this.policyId = policyId;
		this.policy = policy;
	}

	public String getOfferId() {
		return this.offerId;
	}

	public String getAssetId() {
		return this.assetId;
	}

	public String getPolicyId() {
		return this.policyId;
	}

	public Policy getPolicy() {
		return this.policy;
	}
}
