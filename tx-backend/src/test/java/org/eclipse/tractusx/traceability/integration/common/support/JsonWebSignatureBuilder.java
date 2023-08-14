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

import org.eclipse.tractusx.traceability.common.security.JwtRole;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;

import java.util.*;

public class JsonWebSignatureBuilder {

    private final String claimsSubject;
    private final String claimsIssuer;
    private final RsaJsonWebKey rsaJsonWebKey;
    private final String resourceClient;

    JsonWebSignatureBuilder(RsaJsonWebKey rsaJsonWebKey, String resourceClient) {
        this.claimsSubject = UUID.randomUUID().toString();
        this.claimsIssuer = "http://localhost";
        this.rsaJsonWebKey = rsaJsonWebKey;
        this.resourceClient = resourceClient;
    }

    JsonWebSignature buildWithRoles(JwtRole... jwtRoles) {
        JwtClaims jwtClaims = new JwtClaims();
        jwtClaims.setJwtId(UUID.randomUUID().toString());
        jwtClaims.setIssuer(claimsIssuer);
        jwtClaims.setSubject(claimsSubject);
        jwtClaims.setIssuedAtToNow();
        jwtClaims.setExpirationTimeMinutesInTheFuture(100);

        List<String> roles = Arrays.stream(jwtRoles).map(JwtRole::getDescription).toList();
        Object resourceAccess = Map.of(resourceClient, Map.of("roles", roles));
        jwtClaims.setClaim("resource_access", resourceAccess);

        JsonWebSignature jsonWebSignature = new JsonWebSignature();
        jsonWebSignature.setPayload(jwtClaims.toJson());
        jsonWebSignature.setKey(rsaJsonWebKey.getPrivateKey());
        jsonWebSignature.setAlgorithmHeaderValue(rsaJsonWebKey.getAlgorithm());
        jsonWebSignature.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
        jsonWebSignature.setHeader("typ", "JWT");

        return jsonWebSignature;
    }
}
