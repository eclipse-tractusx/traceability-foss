/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.io.IOException;

import static java.util.Objects.isNull;

/**
 * Interceptor to add Authorization header to every call done via Rest template
 */
@RequiredArgsConstructor
@Slf4j
public class OAuthClientCredentialsRestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private final OAuth2AuthorizedClientManager manager;
    private final ClientRegistration clientRegistration;

    @Override
    @NotNull
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
                                        final ClientHttpRequestExecution execution) throws IOException {
        final OAuth2AuthorizeRequest oAuth2AuthorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId(
                clientRegistration.getRegistrationId()).principal(clientRegistration.getClientName()).build();

        final OAuth2AuthorizedClient client = manager.authorize(oAuth2AuthorizeRequest);

        if (isNull(client)) {
            throw new IllegalStateException("Client credentials flow on " + clientRegistration.getRegistrationId()
                    + " failed, client is null");
        }

        log.debug("Adding Authorization header to the request {}", request.getURI());

        request.getHeaders().add(HttpHeaders.AUTHORIZATION, buildAuthorizationHeaderValue(client.getAccessToken()));
        return execution.execute(request, body);
    }

    @NotNull
    private String buildAuthorizationHeaderValue(final OAuth2AccessToken accessToken) {
        return accessToken.getTokenType().getValue() + " " + accessToken.getTokenValue();
    }
}
