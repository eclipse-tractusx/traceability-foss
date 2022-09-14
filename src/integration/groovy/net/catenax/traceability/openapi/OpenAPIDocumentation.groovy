package net.catenax.traceability.openapi

import net.catenax.traceability.IntegrationSpec
import net.catenax.traceability.common.security.KeycloakRole
import org.apache.commons.io.FileUtils
import org.springframework.http.MediaType

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class OpenAPIDocumentation extends IntegrationSpec {

	private static final String DOCUMENTATION_FILENAME = "./openapi/product-traceability-foss-backend.json"

	def "should generate openapi documentation"() {
		given:
			authenticatedUser(KeycloakRole.ADMIN)

		expect:
			mvc.perform(get("/v3/api-docs").accept(MediaType.APPLICATION_JSON))
				.andDo(result -> {
					FileUtils.writeStringToFile(new File(DOCUMENTATION_FILENAME), result.getResponse().getContentAsString())
				})
				.andExpect(status().isOk())
	}
}
