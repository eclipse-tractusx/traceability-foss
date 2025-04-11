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
package org.eclipse.tractusx.traceability.common.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.properties.EdcProperties;
import org.eclipse.tractusx.traceability.common.properties.FeignDefaultProperties;
import org.eclipse.tractusx.traceability.common.properties.RegistryProperties;
import org.eclipse.tractusx.traceability.common.properties.SubmodelProperties;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RestTemplateConfiguration {

    public static final String EDC_REST_TEMPLATE = "edcRestTemplate";
    public static final String EDC_ASSET_REST_TEMPLATE = "edcDtrAssetRestTemplate";
    public static final String DIGITAL_TWIN_REGISTRY_CREATE_SHELL_REST_TEMPLATE = "digitalTwinRegistryCreateShellRestTemplate";
    public static final String EDC_NOTIFICATION_TEMPLATE = "edcNotificationTemplate";
    public static final String IRS_ADMIN_TEMPLATE = "irsAdminTemplate";
    public static final String IRS_REGULAR_TEMPLATE = "irsRegularTemplate";
    public static final String SUBMODEL_REST_TEMPLATE = "submodelRestTemplate";
    public static final String DIGITAL_TWIN_REGISTRY_REST_TEMPLATE = "digitalTwinRegistryRestTemplate";
    public static final String EDC_CLIENT_REST_TEMPLATE = "edcClientRestTemplate";

    private static final String EDC_API_KEY_HEADER_NAME = "X-Api-Key";
    private static final String IRS_API_KEY_HEADER_NAME = "X-API-KEY";

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    /* RestTemplate used by trace x for the edc contracts used within the edc provider.*/
    @Bean(EDC_REST_TEMPLATE)
    public RestTemplate edcRestTemplate(@Autowired EdcProperties edcProperties) {
        return new RestTemplateBuilder()
                .rootUri(edcProperties.getProviderEdcUrl())
                .defaultHeader(EDC_API_KEY_HEADER_NAME, edcProperties.getApiAuthKey())
                .connectTimeout(Duration.ofSeconds(10L))
                .readTimeout(Duration.ofSeconds(25L))
                .build();
    }

    @Bean(EDC_ASSET_REST_TEMPLATE)
    public RestTemplate edcDtrAssetRestTemplate(@Autowired EdcProperties edcProperties) {
        return new RestTemplateBuilder()
                .rootUri(edcProperties.getProviderEdcUrl())
                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(EDC_API_KEY_HEADER_NAME, edcProperties.getApiAuthKey())
                .connectTimeout(Duration.ofSeconds(10L))
                .readTimeout(Duration.ofSeconds(25L))
                .build();
    }

    /* RestTemplate used by the digital twin registry client library*/
    @Bean(DIGITAL_TWIN_REGISTRY_CREATE_SHELL_REST_TEMPLATE)
    public RestTemplate digitalTwinRegistryCreateShellRestTemplate(
            final RestTemplateBuilder restTemplateBuilder,
            @Autowired RegistryProperties registryProperties) {
        if (registryProperties.getOauthEnabled()) {

            return oAuthRestTemplate(restTemplateBuilder,
                    registryProperties.getOauthProviderRegistrationId())
                    .build();
        } else {
            return restTemplateBuilder.build();
        }
    }

    /* RestTemplate used by trace x for the notification transfer to the edc controlplane including edc api key*/
    @Bean(EDC_NOTIFICATION_TEMPLATE)
    public RestTemplate edcNotificationTemplate(@Autowired EdcProperties edcProperties) {
        return new RestTemplateBuilder()
                .defaultHeader(EDC_API_KEY_HEADER_NAME, edcProperties.getApiAuthKey())
                .build();
    }

    /* RestTemplate used by trace x for the irs api with the admin api key*/
    @Bean(IRS_ADMIN_TEMPLATE)
    public RestTemplate irsAdminTemplate(@Autowired TraceabilityProperties traceabilityProperties) {
        return new RestTemplateBuilder()
                .rootUri(traceabilityProperties.getIrsBase())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // Set Content-Type header
                .defaultHeader(IRS_API_KEY_HEADER_NAME, traceabilityProperties.getIrsAdminApiKey())
                .build();
    }

    /* RestTemplate used by trace x for the irs api with the regular api key*/
    @Bean(IRS_REGULAR_TEMPLATE)
    public RestTemplate irsRegularTemplate(@Autowired TraceabilityProperties traceabilityProperties) {
        return new RestTemplateBuilder()
                .rootUri(traceabilityProperties.getIrsBase())
                .defaultHeader(IRS_API_KEY_HEADER_NAME, traceabilityProperties.getRegularApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) // Set Content-Type header
                .messageConverters(customMessageConverters())
                .build();
    }

    /* RestTemplate used by trace x for the submodel server*/
    @Bean(SUBMODEL_REST_TEMPLATE)
    public RestTemplate submodelRestTemplate(@Autowired SubmodelProperties submodelProperties, @Autowired FeignDefaultProperties feignDefaultProperties,
                                             final RestTemplateBuilder restTemplateBuilder) {

        return oAuthRestTemplate(restTemplateBuilder, submodelProperties.getOauthProviderRegistrationId())
                .rootUri(submodelProperties.getBaseExternal())
                .connectTimeout(Duration.ofMillis(feignDefaultProperties.getConnectionTimeoutMillis()))
                .readTimeout(Duration.ofMillis(feignDefaultProperties.getReadTimeoutMillis()))
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
    }


    @Bean(DIGITAL_TWIN_REGISTRY_REST_TEMPLATE)
    public RestTemplate digitalTwinRegistryRestTemplate(
            final RestTemplateBuilder restTemplateBuilder,
            @Autowired RegistryProperties registryProperties) {
        if (registryProperties.getOauthEnabled()) {
            return oAuthRestTemplate(restTemplateBuilder, "keycloak").build();
        } else {
            return restTemplateBuilder.build();
        }
    }

    /* RestTemplate used by the edc client library*/
    @Bean(EDC_CLIENT_REST_TEMPLATE)
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

    private List<HttpMessageConverter<?>> customMessageConverters() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        converter.setObjectMapper(JsonMapper.builder()
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build()
                .registerModules(javaTimeModule));

        // Add the custom converter to the list of message converters
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(converter);

        return converters;
    }

}
