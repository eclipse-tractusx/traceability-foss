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
package org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.service.asset.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EdcDataAddressProperties {

	@JsonProperty("baseUrl")
	private final String baseUrl;

	@JsonProperty("proxyMethod")
	private final boolean proxyMethod;

	@JsonProperty("method")
	private final String method;

	@JsonProperty("proxyBody")
	private final boolean proxyBody;

	@JsonProperty("type")
	private final String type;

	public EdcDataAddressProperties(String baseUrl,
									boolean proxyMethod,
									String method,
									boolean proxyBody,
									String type) {
		this.baseUrl = baseUrl;
		this.proxyMethod = proxyMethod;
		this.method = method;
		this.proxyBody = proxyBody;
		this.type = type;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public boolean getProxyMethod() {
		return proxyMethod;
	}

	public String getMethod() {
		return method;
	}

	public boolean getProxyBody() {
		return proxyBody;
	}

	public String getType() {
		return type;
	}
}
