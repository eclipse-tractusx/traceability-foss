/*
 *  Copyright (c) 2020 - 2022 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *       Bayerische Motoren Werke Aktiengesellschaft (BMW AG) - improvements
 *
 */

package net.catenax.traceability.infrastructure.edc.blackbox.notification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.catenax.traceability.infrastructure.edc.blackbox.policy.Policy;

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
