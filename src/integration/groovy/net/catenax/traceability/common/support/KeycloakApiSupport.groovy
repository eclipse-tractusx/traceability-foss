/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

package net.catenax.traceability.common.support

import net.catenax.traceability.common.config.RestitoConfig

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp
import static com.xebialabs.restito.semantics.Action.header
import static com.xebialabs.restito.semantics.Action.ok
import static com.xebialabs.restito.semantics.Action.unauthorized
import static com.xebialabs.restito.semantics.Condition.basicAuth
import static com.xebialabs.restito.semantics.Condition.post
import static com.xebialabs.restito.semantics.Condition.startsWithUri

trait KeycloakApiSupport implements RestitoProvider {

	void keycloakApiReturnsToken() {
		whenHttp(stubServer()).match(
			post(RestitoConfig.KEYCLOAK_TOKEN_PATH),
			basicAuth("traceability-foss-integration-tests", "integration-tests")
		)
			.then(
				ok(),
				header("Content-Type", "application/json"),
				jsonResponseFromFile("./stubs/keycloak/post/auth/realms/CX-Central/protocol/openid-connect/token/response_200.json")
			)
	}

	void keycloakApiReturnsUnauthorized() {
		whenHttp(stubServer()).match(
			post(RestitoConfig.KEYCLOAK_TOKEN_PATH)
		)
			.then(
				unauthorized(),
				header("Content-Type", "application/json"),
				jsonResponseFromFile("./stubs/keycloak/post/auth/realms/CX-Central/protocol/openid-connect/token/response_401.json")
			)
	}

	void verifyKeycloakApiCalledOnceForToken() {
		verifyHttp(stubServer()).once(
			startsWithUri(RestitoConfig.KEYCLOAK_TOKEN_PATH)
		)
	}

	void verifyKeycloakApiNotCalledForToken() {
		verifyHttp(stubServer()).never(
			startsWithUri(RestitoConfig.KEYCLOAK_TOKEN_PATH)
		)
	}
}
