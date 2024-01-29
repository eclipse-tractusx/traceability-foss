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
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    public static final String REST_TEMPLATE = "restTemplate";
    public static final String SUBMODEL_TEMPLATE = "submodelTemplate";
    public static final String EDC_NOTIFICATION_TEMPLATE = "edcNotificationTemplate";

    private static final String EDC_API_KEY_HEADER_NAME = "X-Api-Key";

    private static final String IRS_API_KEY_HEADER_NAME = "X-API-KEY";

    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final ClientRegistrationRepository clientRegistrationRepository;


    @Bean
    @Qualifier(EDC_REST_TEMPLATE)
    public RestTemplate edcRestTemplate(@Autowired EdcProperties edcProperties) {
        return new RestTemplateBuilder()
                .rootUri(edcProperties.getProviderEdcUrl())
                .defaultHeader(EDC_API_KEY_HEADER_NAME, edcProperties.getApiAuthKey())
                .setConnectTimeout(Duration.ofSeconds(10L))
                .setReadTimeout(Duration.ofSeconds(25L))
                .build();
    }

    @Bean
    @Qualifier(EDC_NOTIFICATION_TEMPLATE)
    public RestTemplate edcNotificationTemplate(@Autowired EdcProperties edcProperties) {
        return new RestTemplateBuilder()
                .defaultHeader(EDC_API_KEY_HEADER_NAME, edcProperties.getApiAuthKey())
                .build();
    }

    @Bean
    public RestTemplate irsAdminTemplate(@Autowired TraceabilityProperties traceabilityProperties) {
        return new RestTemplateBuilder()
                .rootUri(traceabilityProperties.getIrsBase())
                .interceptors(new LoggingInterceptor())
                .defaultHeader(IRS_API_KEY_HEADER_NAME, traceabilityProperties.getAdminApiKey())
                .messageConverters(customMessageConverters())
                .build();
    }

    @Bean
    public RestTemplate irsRegularTemplate(@Autowired TraceabilityProperties traceabilityProperties) {
        return new RestTemplateBuilder()
                .rootUri(traceabilityProperties.getIrsBase())
                .interceptors(new LoggingInterceptor())
                .defaultHeader(IRS_API_KEY_HEADER_NAME, traceabilityProperties.getRegularApiKey())
                .messageConverters(customMessageConverters())
                .build();
    }

    @Bean
    @Qualifier(REST_TEMPLATE)
    public RestTemplate edcTemplate() {
        return new RestTemplateBuilder()
                .interceptors(new LoggingInterceptor())
                .build();
    }


    @Bean
    @Qualifier(SUBMODEL_TEMPLATE)
    public RestTemplate submodelRestTemplate(@Autowired TraceabilityProperties traceabilityProperties, @Autowired FeignDefaultProperties feignDefaultProperties) {

        return new RestTemplateBuilder()
                .rootUri(traceabilityProperties.getSubmodelBase())
                .interceptors(new LoggingInterceptor())
                .setConnectTimeout(Duration.ofMillis(feignDefaultProperties.getConnectionTimeoutMillis()))
                .setReadTimeout(Duration.ofMillis(feignDefaultProperties.getReadTimeoutMillis()))
                .build();
    }

    @Bean
    public RestTemplate digitalTwinRegistryRestTemplate(
            final RestTemplateBuilder restTemplateBuilder,
            @Value("${digitalTwinRegistryClient.oAuthClientId}") final String clientRegistrationId) {
        oAuthRestTemplate(restTemplateBuilder,
                clientRegistrationId).build();
        return oAuthRestTemplate(restTemplateBuilder,
                clientRegistrationId).interceptors(new LoggingInterceptor()).
                build();
    }

    @Bean
    public RestTemplate edcClientRestTemplate() {
        return new RestTemplateBuilder().interceptors(new LoggingInterceptor())
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
        converter.setObjectMapper(JsonMapper.builder()
                .configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE, true)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build()
                .registerModules(new JavaTimeModule()));

        // Add the custom converter to the list of message converters
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(converter);

        return converters;
    }

}
