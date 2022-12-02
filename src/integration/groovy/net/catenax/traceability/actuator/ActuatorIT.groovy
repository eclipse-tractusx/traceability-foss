package net.catenax.traceability.actuator

import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import net.catenax.traceability.IntegrationSpecification
import org.springframework.boot.test.web.server.LocalManagementPort

import static io.restassured.RestAssured.given
import static org.hamcrest.Matchers.containsInAnyOrder
import static org.hamcrest.Matchers.equalTo

class ActuatorIT extends IntegrationSpecification {

	@LocalManagementPort
	private int managementPort

	private RequestSpecification requestSpecification

	def setup() {
		requestSpecification = new RequestSpecBuilder()
			.setPort(managementPort)
			.build()
	}

	def "should retrieve actuator health data without authentication"() {
		expect:
			given(requestSpecification)
				.when().get("/actuator/health")
				.then().statusCode(200)
				.body("status", equalTo("UP"))
				.body("groups", containsInAnyOrder("liveness", "readiness"))
	}

	def "should retrieve actuator health liveness data without authentication"() {
		expect:
			given(requestSpecification)
				.when().get("/actuator/health/liveness")
				.then().statusCode(200)
				.body("status", equalTo("UP"))
	}

	def "should retrieve actuator health readiness data without authentication"() {
		expect:
			given(requestSpecification)
				.when().get("/actuator/health/readiness")
				.then().statusCode(200)
				.body("status", equalTo("UP"))
	}
}
