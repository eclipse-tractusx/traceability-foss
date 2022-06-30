package net.catenax.traceability

import com.xebialabs.restito.semantics.Action
import net.catenax.traceability.config.RestitoConfig

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp
import static com.xebialabs.restito.semantics.Action.header
import static com.xebialabs.restito.semantics.Action.ok
import static com.xebialabs.restito.semantics.Action.resourceContent
import static com.xebialabs.restito.semantics.Action.unauthorized
import static com.xebialabs.restito.semantics.Condition.basicAuth
import static com.xebialabs.restito.semantics.Condition.get
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

	private Action jsonResponseFromFile(String location) {
		return resourceContent(location)
	}
}
