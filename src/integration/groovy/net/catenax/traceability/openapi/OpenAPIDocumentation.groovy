package net.catenax.traceability.openapi

import net.catenax.traceability.IntegrationSpec
import org.apache.commons.io.FileUtils

import static io.restassured.RestAssured.given

class OpenAPIDocumentation extends IntegrationSpec {

	private static final String DOCUMENTATION_FILENAME = "./openapi/product-traceability-foss-backend.json"

	def "should generate openapi documentation"() {
		when:
			def response = given()
				.when()
				.get("/api/v3/api-docs")

		then:
			response.then()
				.statusCode(200)

		and:
			FileUtils.writeStringToFile(new File(DOCUMENTATION_FILENAME), response.body().print())
	}
}
