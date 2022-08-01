package net.catenax.traceability.assets.infrastructure.adapters.rest

import net.catenax.traceability.IntegrationSpec
import org.springframework.http.MediaType
import spock.lang.Unroll

import static net.catenax.traceability.common.security.KeycloakRole.ADMIN
import static net.catenax.traceability.common.security.KeycloakRole.SUPERVISOR
import static net.catenax.traceability.common.security.KeycloakRole.USER
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.nullValue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class DashboardControllerIT extends IntegrationSpec {

	@Unroll
	def "should return all dashboard information for user with #role role"() {
		given:
			authenticatedUser(role)

		expect:
			mvc.perform(get("/dashboard").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.myItems', equalTo(13)))
				.andExpect(jsonPath('$.branchItems', equalTo(13)))

		where:
			role << [SUPERVISOR, ADMIN]
	}

	def "should return only 'my items' dashboard information for user with USER role"() {
		given:
			authenticatedUser(USER)

		expect:
			mvc.perform(get("/dashboard").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath('$.myItems', equalTo(13)))
				.andExpect(jsonPath('$.branchItems', nullValue()))
	}

	def "should not return dashboard information for user without role"() {
		given:
			authenticatedUserWithNoRole()

		expect:
			mvc.perform(get("/dashboard").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath('$.message', equalTo("User has invalid role to access the dashboard.")))
	}
}
