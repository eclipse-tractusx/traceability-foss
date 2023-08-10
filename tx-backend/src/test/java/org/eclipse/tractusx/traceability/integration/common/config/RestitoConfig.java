package org.eclipse.tractusx.traceability.integration.common.config;

import com.xebialabs.restito.server.StubServer;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class RestitoConfig {
    public static final String OAUTH2_JWK_PATH = "/auth/realms/CX-Central/protocol/openid-connect/certs";
    public static final String OAUTH2_TOKEN_PATH = "/auth/realms/CX-Central/protocol/openid-connect/token";

    private static final StubServer STUB_SERVER;
    private static final int STUB_SERVER_PORT;

    static {
        STUB_SERVER = new StubServer(1025, 65000).run();
        STUB_SERVER_PORT = STUB_SERVER.getPort();
    }

    public static void clear() {
        STUB_SERVER.clear();
    }

    public static StubServer getStubServer() {
        assert STUB_SERVER != null;

        return STUB_SERVER;
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.security.oauth2.resourceserver.jwt.jwk-set-uri=http://127.0.0.1:" + STUB_SERVER_PORT + OAUTH2_JWK_PATH,
                    "spring.security.oauth2.client.provider.OKTA.token-uri=http://127.0.0.1:" + STUB_SERVER_PORT + OAUTH2_TOKEN_PATH,
                    "feign.bpnApi.url=http://127.0.0.1:" + STUB_SERVER_PORT,
                    "feign.irsApi.url=http://127.0.0.1:" + STUB_SERVER_PORT,
                    "feign.portalApi.url=http://127.0.0.1:" + STUB_SERVER_PORT,
                    "feign.irsApi.globalAssetId=testAssetId",
                    "feign.registryApi.url=http://127.0.0.1:" + STUB_SERVER_PORT,
                    "feign.registryApi.defaultBpn=BPNL00000003AYRE",
                    "edc.provider-edc-url=http://localhost:" + STUB_SERVER_PORT,
                    "edc.callbackUrls=http://localhost:" + STUB_SERVER_PORT + "/callback/redirect"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
