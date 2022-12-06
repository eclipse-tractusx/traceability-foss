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

package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.policy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * The types of {@link Policy}.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PolicyType {
	SET("set"), OFFER("offer"), CONTRACT("contract");

	@JsonProperty("@policytype")
	private String type;

	PolicyType(@JsonProperty("@policytype") String type) {
		this.type = type;
	}

	@JsonCreator
	public static PolicyType fromObject(Map<String, Object> object) {
		if (SET.type.equals(object.get("@policytype"))) {
			return SET;
		} else if (OFFER.type.equals(object.get("@policytype"))) {
			return OFFER;
		} else if (CONTRACT.type.equals(object.get("@policytype"))) {
			return CONTRACT;
		}
		throw new IllegalArgumentException("Invalid policy type");
	}

	public String getType() {
		return type;
	}
}
