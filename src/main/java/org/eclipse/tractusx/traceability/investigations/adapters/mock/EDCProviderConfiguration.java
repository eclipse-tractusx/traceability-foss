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
package org.eclipse.tractusx.traceability.investigations.adapters.mock;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Set;

@ConfigurationProperties(prefix = "edc")
public class EDCProviderConfiguration {

	private Map<String, String> bpnProviderUrlMappings;

	private Set<String> callbackUrls;

	public Map<String, String> getBpnProviderUrlMappings() {
		return bpnProviderUrlMappings;
	}

	public void setBpnProviderUrlMappings(Map<String, String> bpnProviderUrlMappings) {
		this.bpnProviderUrlMappings = bpnProviderUrlMappings;
	}

	public Set<String> getCallbackUrls() {
		return callbackUrls;
	}

	public void setCallbackUrls(Set<String> callbackUrls) {
		this.callbackUrls = callbackUrls;
	}
}
