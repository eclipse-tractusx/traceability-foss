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
package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.cache;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryEndpointDataReferenceCache {

	private final Map<String, EndpointDataReference> store = new HashMap<>();

	private final Set<String> awaitingAgreementIds = new HashSet<>();

	public void storeAgreementId(String agreementId) {
		awaitingAgreementIds.add(agreementId);
	}

	public boolean containsAgreementId(String agreementId) {
		return awaitingAgreementIds.contains(agreementId);
	}

	public static boolean endpointDataRefTokenExpired(EndpointDataReference dataReference) {
		String token = dataReference.getAuthCode();

		if (token == null) {
			return true;
		}

		DecodedJWT jwt = JWT.decode(token);
		return !jwt.getExpiresAt().before(new Date(System.currentTimeMillis() + 30 * 1000));
	}

	public void put(String agreementId, EndpointDataReference endpointDataReference) {
		store.put(agreementId, endpointDataReference);
	}

	public EndpointDataReference get(String agreementId) {
		return store.get(agreementId);
	}

	public void remove(String agreementId) {
		store.remove(agreementId);
	}
}
