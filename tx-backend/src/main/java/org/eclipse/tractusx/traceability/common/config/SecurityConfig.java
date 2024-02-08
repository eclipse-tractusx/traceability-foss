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

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.common.security.JwtAuthenticationTokenConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.PermissionsPolicyHeaderWriter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableMethodSecurity(
        securedEnabled = true
)
@RequiredArgsConstructor
public class SecurityConfig {

    private static final long HSTS_MAX_AGE_DAYS = 365;

    private static final String PERMISSION_POLICY = "microphone=(), geolocation=(), camera=()";

    private static final String[] WHITELIST_PATHS = {
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/webjars/swagger-ui/**",
            "/qualitynotifications/receive",
            "/qualityalerts/receive",
            "/qualitynotifications/update",
            "/qualityalerts/update",
            "/callback/endpoint-data-reference",
            "/internal/endpoint-data-reference",
            "/actuator/**",
            "/registry/reload",
            "/submodel/**",
            "/irs/job/callback"

    };

    @Value("${jwt.resource-client}")
    private String resourceClient;


    @Bean
    SecurityFilterChain securityFilterChain(final HttpSecurity httpSecurity, @Value("${cors.origins}") final List<String> origins) throws Exception {

        httpSecurity.httpBasic(AbstractHttpConfigurer::disable);
        httpSecurity.formLogin(AbstractHttpConfigurer::disable);
        httpSecurity.logout(AbstractHttpConfigurer::disable);
        httpSecurity.anonymous(AbstractHttpConfigurer::disable);
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.cors(customizer -> customizer.configurationSource(corsConfigurationSource(origins)));

        httpSecurity.headers(headers -> headers.httpStrictTransportSecurity(
                httpStrictTransportSecurity ->
                        httpStrictTransportSecurity.maxAgeInSeconds(Duration.ofDays(HSTS_MAX_AGE_DAYS).toSeconds())
                                .includeSubDomains(true)
                                .preload(true)
                                .requestMatcher(AnyRequestMatcher.INSTANCE)));

        httpSecurity.headers(headers -> headers.addHeaderWriter(new ReferrerPolicyHeaderWriter(
                ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN)));

        httpSecurity.headers(headers -> headers.addHeaderWriter(new PermissionsPolicyHeaderWriter(PERMISSION_POLICY)));

        httpSecurity.authorizeHttpRequests(auth -> auth
                .requestMatchers(WHITELIST_PATHS)
                .permitAll()
                .anyRequest()
                .authenticated());

        httpSecurity.oauth2ResourceServer(server -> server.jwt(custom -> custom.jwtAuthenticationConverter(new JwtAuthenticationTokenConverter(resourceClient))))
                .oauth2Client(Customizer.withDefaults());

        return httpSecurity.build();
    }


    private CorsConfigurationSource corsConfigurationSource(List<String> origins) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
