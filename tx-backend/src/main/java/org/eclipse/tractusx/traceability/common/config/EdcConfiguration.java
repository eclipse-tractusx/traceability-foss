/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.common.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.irs.edc.client.asset.EdcAssetService;
import org.eclipse.tractusx.irs.edc.client.contract.service.EdcContractDefinitionService;
import org.eclipse.tractusx.irs.edc.client.policy.service.EdcPolicyDefinitionService;
import org.eclipse.tractusx.irs.edc.client.transformer.EdcTransformer;
import org.eclipse.tractusx.irs.registryclient.decentral.DigitalTwinRegistryCreateShellService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.util.UriTemplateHandler;

import static org.eclipse.tractusx.traceability.common.config.RestTemplateConfiguration.DIGITAL_TWIN_REGISTRY_CREATE_SHELL_REST_TEMPLATE;
import static org.eclipse.tractusx.traceability.common.config.RestTemplateConfiguration.EDC_ASSET_REST_TEMPLATE;

@Configuration
@ConfigurationPropertiesScan(basePackages = "org.eclipse.tractusx.traceability.*")
@EnableWebMvc
@EnableAsync(proxyTargetClass = true)
@EnableConfigurationProperties
@RequiredArgsConstructor
@Slf4j
@EnableJpaRepositories(basePackages = "org.eclipse.tractusx.traceability.*")
public class EdcConfiguration {

    @Value("${provisioning.registry.urlWithPathExternal}")
    String registryUrlWithPathExternal;
    @Value("${provisioning.registry.shellDescriptorUrl}")
    String shellDescriptorUrl;

    @Bean
    public EdcAssetService edcAssetService(org.eclipse.tractusx.irs.edc.client.EdcConfiguration edcConfiguration, EdcTransformer edcTransformer,@Qualifier(EDC_ASSET_REST_TEMPLATE) RestTemplate edcAssetRestTemplate) {
        return new EdcAssetService(edcTransformer, edcConfiguration, edcAssetRestTemplate);
    }

    @Bean
    public EdcPolicyDefinitionService edcPolicyDefinitionService(org.eclipse.tractusx.irs.edc.client.EdcConfiguration edcConfiguration,@Qualifier(EDC_ASSET_REST_TEMPLATE) RestTemplate edcAssetRestTemplate) {
        return new EdcPolicyDefinitionService(edcConfiguration, edcAssetRestTemplate);
    }

    @Bean
    public EdcContractDefinitionService edcContractDefinitionService(org.eclipse.tractusx.irs.edc.client.EdcConfiguration edcConfiguration,@Qualifier(EDC_ASSET_REST_TEMPLATE) RestTemplate edcAssetRestTemplate) {
        return new EdcContractDefinitionService(edcConfiguration, edcAssetRestTemplate);
    }

    @Bean
    public DigitalTwinRegistryCreateShellService dtrCreateShellService(@Qualifier(DIGITAL_TWIN_REGISTRY_CREATE_SHELL_REST_TEMPLATE) RestTemplate digitalTwinRegistryCreateShellRestTemplate) {

        UriTemplateHandler uriTemplateHandler = digitalTwinRegistryCreateShellRestTemplate.getUriTemplateHandler();
        log.info("dtrCreateShellService base URI: {}", uriTemplateHandler);
        return new DigitalTwinRegistryCreateShellService(digitalTwinRegistryCreateShellRestTemplate, registryUrlWithPathExternal + shellDescriptorUrl);
    }
}
