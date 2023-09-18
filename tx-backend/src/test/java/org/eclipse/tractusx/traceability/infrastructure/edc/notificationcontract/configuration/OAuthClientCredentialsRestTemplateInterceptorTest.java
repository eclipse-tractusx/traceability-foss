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
package org.eclipse.tractusx.traceability.infrastructure.edc.notificationcontract.configuration;

import org.eclipse.tractusx.traceability.common.config.OAuthClientCredentialsRestTemplateInterceptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OAuthClientCredentialsRestTemplateInterceptorTest {

    @Mock
    private OAuth2AuthorizedClientManager manager;

    @Mock
    private ClientRegistration clientRegistration;

    @Mock
    private ClientHttpRequestExecution execution;

    @Mock
    private HttpRequest request;

    @Mock
    private OAuth2AuthorizedClient client;

    @Mock
    private OAuth2AccessToken.TokenType tokenType;

    @Mock
    private ClientHttpResponse response;

    @InjectMocks
    private OAuthClientCredentialsRestTemplateInterceptor interceptor;

    @Test
    void givenNullClient_whenIntercept_thenThrowIllegalStateException() {
        // given
        final String registrationId = "registrationId";
        final String clientName = "clientName";
        when(clientRegistration.getRegistrationId()).thenReturn(registrationId);
        when(clientRegistration.getClientName()).thenReturn(clientName);
        when(manager.authorize(any())).thenReturn(null);

        // when/then
        assertThrows(
                IllegalStateException.class,
                () -> interceptor.intercept(request, new byte[]{}, execution));
        verifyNoInteractions(execution);
    }

    @Test
    void givenClient_whenIntercept_thenExecute() throws IOException {
        // given
        final String registrationId = "registrationId";
        final String clientName = "clientName";
        OAuth2AccessToken token = new OAuth2AccessToken(
                tokenType,
                "XXX",
                Instant.now(),
                Instant.MAX
        );
        when(tokenType.getValue()).thenReturn("Bearer");
        when(clientRegistration.getRegistrationId()).thenReturn(registrationId);
        when(clientRegistration.getClientName()).thenReturn(clientName);
        when(manager.authorize(any())).thenReturn(client);
        when(request.getURI()).thenReturn(URI.create(""));
        when(request.getHeaders()).thenReturn(HttpHeaders.writableHttpHeaders(HttpHeaders.EMPTY));
        when(client.getAccessToken()).thenReturn(token);
        when(execution.execute(any(), any())).thenReturn(response);

        // when
        interceptor.intercept(request, new byte[]{}, execution);

        // then
        verify(execution, times(1)).execute(any(), any());
    }
}
