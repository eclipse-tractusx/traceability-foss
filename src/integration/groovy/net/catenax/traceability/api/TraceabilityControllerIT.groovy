package net.catenax.traceability.api

import net.catenax.traceability.IntegrationSpec
import org.springframework.http.MediaType

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class TraceabilityControllerIT extends IntegrationSpec {

	def "should return assets for authenticated user with role"() {
		given:
			authenticatedUser(KeycloakRole.UMA_ROLE)

		expect:
			mvc.perform(get("/api/assets/1234").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
	}

	def "should not return assets when user is not authenticated"() {
		given:
			unauthenticatedUser()

		expect:
			mvc.perform(get("/api/assets/1234").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
	}
}
