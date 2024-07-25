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

package org.eclipse.tractusx.traceability.test.tooling.rest;


import io.restassured.http.Header;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.eclipse.tractusx.traceability.test.tooling.EnvVariablesResolver;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

@Data
@Builder
@AllArgsConstructor
public class Authentication {

    private String clientId;
    private String clientSecret;
    private String keycloakHost;

    private Authentication() {
        clientId = EnvVariablesResolver.getSupervisorClientIdTracexA();
        clientSecret = EnvVariablesResolver.getSupervisorPasswordTracexA();
        keycloakHost = EnvVariablesResolver.getKeycloakHost();
    }

    public static Authentication authenticationForTracexA(){
        Authentication authentication = new Authentication();
        authentication.clientId = EnvVariablesResolver.getSupervisorClientIdTracexA();
        authentication.clientSecret = EnvVariablesResolver.getSupervisorPasswordTracexA();
        authentication.keycloakHost = EnvVariablesResolver.getKeycloakHost();
        return authentication;
    }

    public static Authentication authenticationForTracexB(){
        Authentication authentication = new Authentication();
        authentication.clientId = EnvVariablesResolver.getSupervisorClientIdTracexB();
        authentication.clientSecret = EnvVariablesResolver.getAssociationSupervisorTxBPassword();
        authentication.keycloakHost = EnvVariablesResolver.getKeycloakHost();
        return authentication;
    }

    public String obtainAccessToken() {
        final Map<String, String> oauth2Payload = new HashMap<>();
        oauth2Payload.put("grant_type", "client_credentials");
        oauth2Payload.put("client_id", clientId);
        oauth2Payload.put("client_secret", clientSecret);

        return given()
                .params(oauth2Payload)
                .header(new Header("Content-Type", "application/x-www-form-urlencoded"))
                .post(keycloakHost)
                .then()
                .extract().jsonPath().getString("access_token");
    }
}
