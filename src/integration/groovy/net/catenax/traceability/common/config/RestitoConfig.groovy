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

package net.catenax.traceability.common.config

import com.xebialabs.restito.server.StubServer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.util.SocketUtils

@TestConfiguration
class RestitoConfig {

	public static final String KEYCLOAK_TOKEN_PATH = "/auth/realms/CX-Central/protocol/openid-connect/token"

	private static final StubServer STUB_SERVER

	private static final int STUB_SERVER_PORT

	static {
		STUB_SERVER_PORT = getRandomPort()
		STUB_SERVER = new StubServer(STUB_SERVER_PORT).run()
	}

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		void initialize(ConfigurableApplicationContext configurableApplicationContext) {
			TestPropertyValues.of(
				"feign.bpnApi.url=http://127.0.0.1:$STUB_SERVER_PORT",
				"feign.irsApi.url=http://127.0.0.1:$STUB_SERVER_PORT",
				"feign.irsApi.globalAssetId=testAssetId",
				"spring.security.oauth2.client.provider.keycloak.token-uri=http://127.0.0.1:$STUB_SERVER_PORT/$KEYCLOAK_TOKEN_PATH",
			).applyTo(configurableApplicationContext.getEnvironment())
		}
	}

	static void clear() {
		STUB_SERVER.clear()
	}

	static StubServer getStubServer() {
		assert STUB_SERVER != null

		return STUB_SERVER
	}

	private static int getRandomPort() {
		int port = SocketUtils.findAvailableTcpPort()

		try (ServerSocket serverSocket = new ServerSocket(port)) {
			assert serverSocket != null
			assert serverSocket.getLocalPort() == port
		} catch (IOException e) {
			throw new Error("Port is not available", e)
		}

		return port
	}
}
