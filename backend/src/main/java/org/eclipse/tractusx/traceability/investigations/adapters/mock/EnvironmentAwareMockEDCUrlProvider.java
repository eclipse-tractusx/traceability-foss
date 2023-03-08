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

import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.eclipse.tractusx.traceability.investigations.domain.ports.EDCUrlProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

public class EnvironmentAwareMockEDCUrlProvider implements EDCUrlProvider {

	private static final Logger logger = LoggerFactory.getLogger(EnvironmentAwareMockEDCUrlProvider.class);

	private static final String ENVIRONMENT_PLACEHOLDER = "{environment}";

	private static final String DEFAULT_EDC_SENDER_URL = "https://tracex-consumer-controlplane.%s.demo.catena-x.net".formatted(ENVIRONMENT_PLACEHOLDER);

	private static final String DEFAULT_EDC_PROVIDER_URL = "https://trace-x-test-edc.%s.demo.catena-x.net".formatted(ENVIRONMENT_PLACEHOLDER);

	private final String applicationEnvironment;

	private final EdcProperties edcProperties;

	public EnvironmentAwareMockEDCUrlProvider(@Autowired Environment applicationEnvironment,
											  @Autowired EdcProperties edcProperties
	) {
		this.applicationEnvironment = Arrays.stream(applicationEnvironment.getActiveProfiles())
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("No environment found"));
		this.edcProperties = edcProperties;
	}

	@Override
	public List<String> getEdcUrls(String bpn) {
		String edcUrl = edcProperties.getBpnProviderUrlMappings().getOrDefault(bpn, defaultProviderUrl());

		logger.info("Resolved {} url for {} bpn", edcUrl, bpn);

		return List.of(edcUrl);
	}

	private String defaultProviderUrl() {
		return DEFAULT_EDC_PROVIDER_URL.replace(ENVIRONMENT_PLACEHOLDER, applicationEnvironment);
	}

	@Override
	public String getSenderUrl() {
		return DEFAULT_EDC_SENDER_URL.replace(ENVIRONMENT_PLACEHOLDER, applicationEnvironment);
	}
}
