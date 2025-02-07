/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.common.security.apikey;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.eclipse.tractusx.traceability.common.security.apikey.ApiKeyFeatureService.AUTH_TOKEN_HEADER_NAME;

@Slf4j
@RequiredArgsConstructor
@Component
@ConditionalOnProperty("traceability.enableApiKeyAuthentication")
public class ApiKeyAuthenticationFilter extends GenericFilterBean {
    private final TraceabilityProperties traceabilityProperties;
    private final ApiKeyFeatureService apiKeyFeatureService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        List<String> apiKeysecuredPaths = apiKeyFeatureService.getApiKeyRequestMapping();
        List<AntPathRequestMatcher> antMatchers = apiKeysecuredPaths.stream().map(path -> new AntPathRequestMatcher(path, null)).toList();
        // Check if the request-path matches any of the apiKeysecuredPaths
        Optional<Boolean> pathMatches = antMatchers.stream().map(matcher -> matcher.matches((HttpServletRequest) request)).filter(matchResult -> matchResult).findFirst();

        if (pathMatches.isEmpty()) {
            log.debug("API key is not enabled for this path: {}", request.getServletContext().getContextPath());
            chain.doFilter(request, response);
            return;
        }

        String apiKey = ((HttpServletRequest) request).getHeader(AUTH_TOKEN_HEADER_NAME);

        if (apiKey == null) {
            log.debug("No API Key provided in the request header.");
            chain.doFilter(request, response);
            return;
        }

        ApiKeyAuthentication authRequest = new ApiKeyAuthentication(apiKey, null);

        if (!authRequest.getPrincipal().equals(traceabilityProperties.getTracexAdminKey())) {
            log.debug("Invalid API Key: {}", authRequest.getPrincipal());
            chain.doFilter(request, response);
            return;
        }

        //Add authentication object to request context with admin role
        SecurityContextHolder.getContext().setAuthentication(
                new ApiKeyAuthentication(
                        (String) authRequest.getPrincipal(),
                        Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
                ));

        chain.doFilter(request, response);
    }

}
