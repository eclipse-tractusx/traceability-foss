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

import java.util.Arrays;

/**
 * Defines the states a contract negotiation can be in.
 */
public enum ContractNegotiationStates {

	UNSAVED(0),
	INITIAL(50),
	REQUESTING(100),
	REQUESTED(200),
	PROVIDER_OFFERING(300),
	PROVIDER_OFFERED(400),
	CONSUMER_OFFERING(500),
	CONSUMER_OFFERED(600),
	CONSUMER_APPROVING(700),
	CONSUMER_APPROVED(800),
	DECLINING(900),
	DECLINED(1000),
	CONFIRMING(1100),
	CONFIRMED(1200),
	ERROR(-1);

	private final int code;

	ContractNegotiationStates(int code) {
		this.code = code;
	}

	public static ContractNegotiationStates from(int code) {
		return Arrays.stream(values()).filter(tps -> tps.code == code).findFirst().orElse(null);
	}

	public int code() {
		return code;
	}

}
