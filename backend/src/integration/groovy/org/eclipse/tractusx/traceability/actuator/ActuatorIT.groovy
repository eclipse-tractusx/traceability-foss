/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.traceability.actuator

import io.restassured.builder.RequestSpecBuilder
import io.restassured.specification.RequestSpecification
import org.eclipse.tractusx.traceability.IntegrationSpecification
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
