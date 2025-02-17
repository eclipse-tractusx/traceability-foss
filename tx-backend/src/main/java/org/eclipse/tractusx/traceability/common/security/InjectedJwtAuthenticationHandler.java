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

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Optional;
import java.util.Set;

@Slf4j
public class InjectedJwtAuthenticationHandler implements HandlerMethodArgumentResolver {


    private final String resourceClient;

    public InjectedJwtAuthenticationHandler(String resourceClient) {
        this.resourceClient = resourceClient;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(InjectedJwtAuthentication.class);
    }

    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  @NotNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        Object credentials = Optional.ofNullable(SecurityContextHolder.getContext())
                .flatMap(it -> Optional.ofNullable(it.getAuthentication()))
                .flatMap(it -> Optional.ofNullable(it.getCredentials()))
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Authentication not found."));

        if (credentials instanceof Jwt jwtToken) {
            Set<JwtRole> jwtRoles = JwtRolesExtractor.extract(jwtToken, resourceClient);

            return new JwtAuthentication(jwtRoles);
        }

        log.error("Authentication not found for {} resource realm in JWT token", resourceClient);

        throw new AuthenticationCredentialsNotFoundException("Authentication not found.");
    }
}
