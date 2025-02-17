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

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JwtAuthenticationTokenConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final String resourceClient;
    private final String resourceClientSecond;
    private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter;

    public JwtAuthenticationTokenConverter(String resourceClient, String resourceClientSecond) {
        this.resourceClient = resourceClient;
        this.resourceClientSecond = resourceClientSecond;
        this.defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    }

    private static Collection<? extends GrantedAuthority> extractRoles(final Jwt jwt, final String resourceId) {
        return JwtRolesExtractor.extract(jwt, resourceId).stream().map(Enum::name)
                .map(it -> new SimpleGrantedAuthority("ROLE_%s".formatted(it)))
                .collect(Collectors.toSet());
    }

    @Override
    public AbstractAuthenticationToken convert(@NotNull Jwt source) {
        Collection<GrantedAuthority> grantedAuthorities = defaultGrantedAuthoritiesConverter.convert(source);
        Collection<GrantedAuthority> authorities =
                Stream.concat(
                        grantedAuthorities != null ? grantedAuthorities.stream() : Stream.empty(),
                        Stream.concat(
                                extractRoles(source, resourceClient).stream(),
                                extractRoles(source, resourceClientSecond).stream()
                        )
                ).collect(Collectors.toSet());
        return new JwtAuthenticationToken(source, authorities);
    }
}
