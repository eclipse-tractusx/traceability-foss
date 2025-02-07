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
package org.eclipse.tractusx.traceability.integration.common.support;

import io.restassured.http.Header;
import org.eclipse.tractusx.traceability.common.security.JwtRole;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;


@Component
public class OAuth2Support {

    @Value("${jwt.resource-client}")
    String resourceClient = null;

    @Autowired
    RsaJsonWebKeyProvider rsaJsonWebKeyProvider;

    @Autowired
    OAuth2AuthorizedClientService authorizedClientService;

    public void clearOAuth2Client() {
        authorizedClientService.removeAuthorizedClient("OKTA", "feignClient");
    }

    public Header jwtAuthorization(JwtRole... jwtRoles) throws JoseException {
        return new Header(HttpHeaders.AUTHORIZATION, jwtToken(jwtRoles));
    }

    public Header jwtAuthorizationWithNoRole() throws JoseException {
        return new Header(HttpHeaders.AUTHORIZATION, jwtToken());
    }

    public Header jwtAuthorizationWithOptionalRole(JwtRole role) throws JoseException {
        return role == null ? jwtAuthorization() : jwtAuthorization(role);
    }

    private String jwtToken(JwtRole... jwtRoles) throws JoseException {
        RsaJsonWebKey rsaJsonWebKey = rsaJsonWebKeyProvider.rsaJsonWebKey();

        String token = new JsonWebSignatureBuilder(rsaJsonWebKey, resourceClient)
                .buildWithRoles(jwtRoles)
                .getCompactSerialization();

        return "Bearer " + token;
    }

    public String jwk() throws JoseException {
        RsaJsonWebKey rsaJsonWebKey = rsaJsonWebKeyProvider.rsaJsonWebKey();

        return new JsonWebKeySet(rsaJsonWebKey).toJson();
    }
}
