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

import com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
@Slf4j
class JwtRolesExtractor {

    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String ROLES = "roles";

    private JwtRolesExtractor() {

    }

    static Set<JwtRole> extract(Jwt jwtToken, String resourceClient) {
      log.info("Extracting roles for resource client {}", resourceClient);
        Object resourceAccess = Optional.ofNullable(jwtToken.getClaimAsMap(RESOURCE_ACCESS))
                .flatMap(it -> Optional.ofNullable(it.get(resourceClient)))
                .orElse(null);
        log.info("Resource access: {}", resourceAccess);
        if (resourceAccess instanceof LinkedTreeMap<?, ?> resourceAccessCasted) {
            Object roles = resourceAccessCasted.get(ROLES);
log.info("Roles: {}", roles);
            if (roles instanceof ArrayList<?> arrayList) {
                return arrayList.stream()
                        .map(JwtRolesExtractor::castStringOrNull)
                        .filter(Objects::nonNull)
                        .map(JwtRole::parse)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toSet());
            }
        }

        return Collections.emptySet();
    }

    private static String castStringOrNull(Object o) {
        if (o instanceof String casted) {
            return casted;
        } else {
            return null;
        }
    }
}
