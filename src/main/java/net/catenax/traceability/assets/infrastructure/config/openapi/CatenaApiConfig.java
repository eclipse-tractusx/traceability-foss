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

package net.catenax.traceability.assets.infrastructure.config.openapi;

import feign.RequestInterceptor;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

import java.util.concurrent.TimeUnit;

@Configuration
public class CatenaApiConfig {

	@Bean
	public OkHttpClient catenaApiOkHttpClient(@Autowired FeignDefaultProperties feignDefaultProperties) {
		return new OkHttpClient.Builder()
			.connectTimeout(feignDefaultProperties.getConnectionTimeoutMillis(), TimeUnit.MILLISECONDS)
			.readTimeout(feignDefaultProperties.getReadTimeoutMillis(), TimeUnit.MILLISECONDS)
			.connectionPool(
				new ConnectionPool(
					feignDefaultProperties.getMaxIdleConnections(),
					feignDefaultProperties.getKeepAliveDurationMinutes(),
					TimeUnit.MINUTES
				)
			)
			.build();
	}

	@Bean
	public AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager(
		ClientRegistrationRepository clientRegistrationRepository,
		OAuth2AuthorizedClientService authorizedClientService
	) {
		OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
			.clientCredentials()
			.build();

		AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
			new AuthorizedClientServiceOAuth2AuthorizedClientManager(
				clientRegistrationRepository,
				authorizedClientService
			);

		authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

		return authorizedClientManager;
	}

	@Bean
	public RequestInterceptor requestInterceptor(AuthorizedClientServiceOAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
		return new KeycloakAuthorizationInterceptor(oAuth2AuthorizedClientManager);
	}
}
