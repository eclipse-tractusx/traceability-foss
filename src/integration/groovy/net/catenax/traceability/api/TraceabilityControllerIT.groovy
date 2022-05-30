package net.catenax.traceability.api

import net.catenax.traceability.IntegrationSpec
import org.hamcrest.text.MatchesPattern
import org.springframework.http.MediaType

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class TraceabilityControllerIT extends IntegrationSpec {

	def "should return hello world"() {
		expect:
			mvc.perform(get("/api/hello").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().string(MatchesPattern.matchesPattern("Hello World!")));
	}
}
