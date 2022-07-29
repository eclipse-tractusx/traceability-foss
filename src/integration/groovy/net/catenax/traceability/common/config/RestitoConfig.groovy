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
