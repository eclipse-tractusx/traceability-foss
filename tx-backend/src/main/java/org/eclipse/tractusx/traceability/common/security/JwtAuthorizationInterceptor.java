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

package org.eclipse.tractusx.traceability.common.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.Optional;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

public class JwtAuthorizationInterceptor implements RequestInterceptor {

	private final Logger logger = LoggerFactory.getLogger(JwtAuthorizationInterceptor.class);

	private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;

	public JwtAuthorizationInterceptor(OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
		this.oAuth2AuthorizedClientManager = oAuth2AuthorizedClientManager;
	}

	@Override
	public void apply(RequestTemplate template) {
		if (!template.headers().containsKey(HttpHeaders.AUTHORIZATION)) {
			OAuth2AccessToken accessToken = getAccessToken()
				.orElseThrow(() -> new TechnicalUserAuthorizationException("Couldn't obtain access token for technical user"));

			logger.debug("Injecting access token of type {}", accessToken.getTokenType());

			template.header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(accessToken.getTokenValue()));
		}
	}

	private Optional<OAuth2AccessToken> getAccessToken() {
		OAuth2AuthorizeRequest request = OAuth2AuthorizeRequest.withClientRegistrationId("OKTA")
			.principal(new AnonymousAuthenticationToken("feignClient", "feignClient", createAuthorityList("ROLE_ANONYMOUS")))
			.build();

		final OAuth2AuthorizedClient oAuth2AuthorizedClient;

		try {
			oAuth2AuthorizedClient = oAuth2AuthorizedClientManager.authorize(request);
		} catch (ClientAuthorizationException e) {
			throw new TechnicalUserAuthorizationException(e);
		}

		return Optional.ofNullable(oAuth2AuthorizedClient)
			.map(OAuth2AuthorizedClient::getAccessToken);
	}
}
