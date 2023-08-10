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

import org.eclipse.tractusx.traceability.common.config.RestitoConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.*;
import static com.xebialabs.restito.semantics.Condition.*;
import static com.xebialabs.restito.semantics.Condition.startsWithUri;

@Component
public class OAuth2ApiSupport {
    @Autowired
    RestitoProvider restitoProvider;

    public void oauth2ApiReturnsTechnicalUserToken() {
        whenHttp(restitoProvider.stubServer()).match(
                        post(RestitoConfig.OAUTH2_TOKEN_PATH),
                        basicAuth("traceability-foss-integration-tests", "integration-tests")
                )
                .then(
                        ok(),
                        header("Content-Type", "application/json"),
                        restitoProvider.jsonResponseFromFile("./stubs/oauth/post/auth/realms/CX-Central/protocol/openid-connect/token/response_200.json")
                );
    }

    public void oauth2ApiReturnsJwkCerts(String jwk) {
        whenHttp(restitoProvider.stubServer()).match(
                        get(RestitoConfig.OAUTH2_JWK_PATH)
                )
                .then(
                        ok(),
                        header("Content-Type", "application/json"),
                        stringContent(jwk)
                );
    }

    public void oauth2ApiReturnsUnauthorized() {
        whenHttp(restitoProvider.stubServer()).match(
                        post(RestitoConfig.OAUTH2_TOKEN_PATH)
                )
                .then(
                        unauthorized(),
                        header("Content-Type", "application/json"),
                        restitoProvider.jsonResponseFromFile("./stubs/oauth/post/auth/realms/CX-Central/protocol/openid-connect/token/response_401.json")
                );
    }

    public void verifyOAuth2ApiCalledOnceForTechnicalUserToken() {
        verifyHttp(restitoProvider.stubServer()).once(
                startsWithUri(RestitoConfig.OAUTH2_TOKEN_PATH)
        );
    }

    public void verifyOAuth2ApiCalledTimesForTechnicalUserToken(int times) {
        verifyHttp(restitoProvider.stubServer()).times(times,
                startsWithUri(RestitoConfig.OAUTH2_TOKEN_PATH)
        );
    }

    public void verifyOAuth2ApiNotCalledForTechnicalUserToken() {
        verifyHttp(restitoProvider.stubServer()).never(
                startsWithUri(RestitoConfig.OAUTH2_TOKEN_PATH)
        );
    }

}
