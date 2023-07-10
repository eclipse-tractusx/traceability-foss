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
package org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.configuration;

import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class EdcRestTemplateConfiguration {

    public static final String EDC_REST_TEMPLATE = "edcRestTemplate";
    public static final String REST_TEMPLATE = "restTemplate";

    private static final String EDC_API_KEY_HEADER_NAME = "X-Api-Key";

    @Bean
    @Qualifier(EDC_REST_TEMPLATE)
	public RestTemplate edcRestTemplate(EdcProperties edcProperties) {
		return new RestTemplateBuilder()
			.rootUri(edcProperties.getProviderEdcUrl())
                .defaultHeader(EDC_API_KEY_HEADER_NAME, edcProperties.getApiAuthKey())
                .build();
    }

    @Bean
    @Qualifier(REST_TEMPLATE)
    public RestTemplate edcTemplate() {
        return new RestTemplateBuilder()
                .build();
    }

    @Bean
    @Qualifier(REST_TEMPLATE)
    public RestTemplate digitalTwinRegistryRestTemplate() {
        return new RestTemplateBuilder()
                .build();
    }

}
