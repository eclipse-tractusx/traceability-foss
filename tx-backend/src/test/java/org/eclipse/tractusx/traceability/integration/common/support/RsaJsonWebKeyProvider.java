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

import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Component;

@Component
public class RsaJsonWebKeyProvider {

    private static final RsaJsonWebKey RSA_JSON_WEB_KEY;

    static {
        try {
            RSA_JSON_WEB_KEY = RsaJwkGenerator.generateJwk(2048);
        } catch (JoseException e) {
            throw new RuntimeException(e);
        }
        RSA_JSON_WEB_KEY.setKeyId("some-key");
        RSA_JSON_WEB_KEY.setAlgorithm(AlgorithmIdentifiers.RSA_USING_SHA256);
        RSA_JSON_WEB_KEY.setUse("sig");
    }

    public RsaJsonWebKey rsaJsonWebKey() throws JoseException {
        return RSA_JSON_WEB_KEY;
    }
}
