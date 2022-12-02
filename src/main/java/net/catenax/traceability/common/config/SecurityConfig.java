/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

package net.catenax.traceability.common.config;

import net.catenax.traceability.common.security.JwtAuthenticationTokenConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableGlobalMethodSecurity(
	prePostEnabled = true,
	securedEnabled = true
)
public class SecurityConfig {

	private static final String[] WHITELIST_URLS = {
		"/api/v3/api-docs/**",
		"/api/swagger-ui/**",
		"/api/swagger-ui.html",
		"/actuator/**"
	};

	@Value("${jwt.resource-client}")
	private String resourceClient;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.httpBasic().disable()
			.formLogin().disable()
			.logout().disable()
			.csrf().disable()
			.cors()
			.and()
			.anonymous().disable()
			.authorizeRequests()
			.antMatchers("/api/callback/endpoint-data-reference").permitAll()
			.antMatchers("/api/qualitynotifications/receive").permitAll()
			.antMatchers("/api/**").authenticated()
			.and()
			.oauth2Client()
			.and()
			.oauth2ResourceServer()
			.jwt()
			.jwtAuthenticationConverter(new JwtAuthenticationTokenConverter(resourceClient));

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().antMatchers(WHITELIST_URLS);
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource(@Value("${cors.origins}") List<String> origins) {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(origins);
		configuration.setAllowedMethods(List.of("*"));
		configuration.setAllowedHeaders(List.of("*"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
