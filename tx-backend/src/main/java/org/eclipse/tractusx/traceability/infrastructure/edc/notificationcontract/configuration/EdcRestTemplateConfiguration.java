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

import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.edc.spi.types.domain.edr.EndpointDataReference;
import org.eclipse.tractusx.irs.component.assetadministrationshell.AssetAdministrationShellDescriptor;
import org.eclipse.tractusx.irs.component.assetadministrationshell.IdentifierKeyValuePair;
import org.eclipse.tractusx.irs.registryclient.decentral.DecentralDigitalTwinRegistryClient;
import org.eclipse.tractusx.irs.registryclient.decentral.DecentralDigitalTwinRegistryService;
import org.eclipse.tractusx.irs.registryclient.decentral.EndpointDataForConnectorsService;
import org.eclipse.tractusx.irs.registryclient.decentral.ShellQueryBody;
import org.eclipse.tractusx.irs.registryclient.discovery.ConnectorEndpointsService;
import org.eclipse.tractusx.traceability.infrastructure.edc.properties.EdcProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class EdcRestTemplateConfiguration {

    public static final String EDC_REST_TEMPLATE = "edcRestTemplate";
    public static final String REST_TEMPLATE = "restTemplate";

    private static final String EDC_API_KEY_HEADER_NAME = "X-Api-Key";

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final ClientRegistrationRepository clientRegistrationRepository;

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
    public RestTemplate digitalTwinRegistryRestTemplate(
            final RestTemplateBuilder restTemplateBuilder,
            @Value("${digitalTwinRegistryClient.oAuthClientId}") final String clientRegistrationId) {
        oAuthRestTemplate(restTemplateBuilder,
                clientRegistrationId).build();
        return oAuthRestTemplate(restTemplateBuilder,
                clientRegistrationId).build();
    }

    @Bean
    public RestTemplate edcClientRestTemplate() {
        return new RestTemplateBuilder()
                .build();
    }

    private RestTemplateBuilder oAuthRestTemplate(final RestTemplateBuilder restTemplateBuilder,
                                                  final String clientRegistrationId) {
        final var clientRegistration = clientRegistrationRepository.findByRegistrationId(clientRegistrationId);

        return restTemplateBuilder.additionalInterceptors(
                new OAuthClientCredentialsRestTemplateInterceptor(authorizedClientManager(), clientRegistration));
    }

        /* package */ OAuth2AuthorizedClientManager authorizedClientManager() {
        final var authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        final var authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, oAuth2AuthorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }
}
