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
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.security.JwtAuthenticationTokenConverter;
import org.eclipse.tractusx.traceability.common.security.apikey.ApiKeyAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Optional;

@Slf4j
@Configuration
@EnableMethodSecurity(
        securedEnabled = true
)
@RequiredArgsConstructor
public class SecurityConfig {
    private final Optional<ApiKeyAuthenticationFilter> apiKeyAuthenticationFilter;

    private static final String[] WHITELIST_PATHS = {
            "/swagger-ui.html",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/webjars/swagger-ui/**",
            "/internal/**",
            "/api/internal/**",
            "/actuator/**",
            "/irs/job/callback"

    };

    @Value("${jwt.resource-client}")
    private String resourceClient;


    @Bean
    @Order
    SecurityFilterChain securityFilterChain(final HttpSecurity httpSecurity) throws Exception {

        httpSecurity.httpBasic(AbstractHttpConfigurer::disable);
        httpSecurity.formLogin(AbstractHttpConfigurer::disable);
        httpSecurity.logout(AbstractHttpConfigurer::disable);
        httpSecurity.anonymous(AbstractHttpConfigurer::disable);
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.cors(Customizer.withDefaults());


        httpSecurity
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITELIST_PATHS)
                        .permitAll())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest()
                        .authenticated())
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt((jwt) -> jwt.jwtAuthenticationConverter(
                        new JwtAuthenticationTokenConverter(resourceClient)))
                )
                .oauth2Client(Customizer.withDefaults());

        //Add api key authentication filter if enabled
        apiKeyAuthenticationFilter.ifPresent(keyAuthenticationFilter -> httpSecurity
                .addFilterBefore(keyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class));

        return httpSecurity.build();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource(@Value("${cors.origins}") List<String> origins) {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
