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
package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox;

import org.eclipse.tractusx.traceability.common.config.FeatureFlags;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.cache.EndpointDataReference;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.cache.InMemoryEndpointDataReferenceCache;
import org.eclipse.tractusx.traceability.investigations.adapters.mock.EDCProviderConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.annotations.ApiIgnore;

@Profile(FeatureFlags.NOTIFICATIONS_ENABLED_PROFILES)
@RestController
@ApiIgnore
@RequestMapping("/callback/endpoint-data-reference")
public class EdcCallbackController {

	private static final Logger log = LoggerFactory.getLogger(EdcCallbackController.class);

	private final InMemoryEndpointDataReferenceCache endpointDataReferenceCache;

	private final RestTemplate restTemplate;

	private final EDCProviderConfiguration edcProviderConfiguration;

	public EdcCallbackController(InMemoryEndpointDataReferenceCache endpointDataReferenceCache, RestTemplateBuilder restTemplateBuilder, EDCProviderConfiguration edcProviderConfiguration) {
		this.endpointDataReferenceCache = endpointDataReferenceCache;
		this.restTemplate = restTemplateBuilder.build();
		this.edcProviderConfiguration = edcProviderConfiguration;
	}

	@PostMapping
	public void receiveEdcCallback(@RequestBody EndpointDataReference dataReference) {
		String contractAgreementId = dataReference.getProperties().get("cid");
		log.info("Received EDC callback for contract: {}", contractAgreementId);

		if (endpointDataReferenceCache.containsAgreementId(contractAgreementId)) {
			log.info("Contract {} found! Processing...", contractAgreementId);
			endpointDataReferenceCache.put(contractAgreementId, dataReference);
		} else {
			log.info("Contract {} not found, forwarding message...", contractAgreementId);
			callOtherServices(dataReference);
		}
	}

	private void callOtherServices(EndpointDataReference dataReference) {
		edcProviderConfiguration.getCallbackUrls().forEach(callbackUrl -> {
			log.info("Calling callback endpoint: {}", callbackUrl);
			ResponseEntity<String> response = restTemplate.postForEntity(callbackUrl, dataReference, String.class);

			log.info("Callback response: HTTP {}", response.getStatusCode());
			log.debug("Body: {}", response.getBody());
		});
	}
}
