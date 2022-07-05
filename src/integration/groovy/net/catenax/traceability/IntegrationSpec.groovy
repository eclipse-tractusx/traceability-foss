package net.catenax.traceability

import com.xebialabs.restito.server.StubServer
import groovy.json.JsonBuilder
import net.catenax.traceability.clients.cache.bpn.BpnCache
import net.catenax.traceability.config.MailboxConfig
import net.catenax.traceability.config.OAuth2Config
import net.catenax.traceability.config.RestitoConfig
import net.catenax.traceability.config.SecurityTestConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

@AutoConfigureMockMvc
@ActiveProfiles(profiles = ["integration"])
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(
	classes = [SecurityTestConfig.class, MailboxConfig.class, RestitoConfig.class, OAuth2Config.class],
	initializers = [RestitoConfig.Initializer.class]
)
abstract class IntegrationSpec extends Specification implements KeycloakSupport, BpnApiSupport, KeycloakApiSupport {

	@Autowired
	protected MockMvc mvc

	@Autowired
	protected BpnCache bpnCache

	def cleanup() {
		RestitoConfig.clear()
		bpnCache.clear()
		clearAuthentication()
	}

	@Override
	StubServer stubServer() {
		return RestitoConfig.getStubServer()
	}

	protected String asJson(Map map) {
		return new JsonBuilder(map).toPrettyString()
	}
}
