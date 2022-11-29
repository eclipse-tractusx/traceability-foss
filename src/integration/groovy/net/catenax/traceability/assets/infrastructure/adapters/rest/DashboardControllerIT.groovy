/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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

package net.catenax.traceability.assets.infrastructure.adapters.rest

import io.restassured.http.ContentType
import net.catenax.traceability.IntegrationSpec
import net.catenax.traceability.common.support.AssetsSupport
import spock.lang.Unroll

import static io.restassured.RestAssured.given
import static net.catenax.traceability.common.security.JwtRole.ADMIN
import static net.catenax.traceability.common.security.JwtRole.SUPERVISOR
import static net.catenax.traceability.common.security.JwtRole.USER
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.nullValue

class DashboardControllerIT extends IntegrationSpec implements AssetsSupport {

	@Unroll
	def "should return all dashboard information for user with #role role"() {
		given:
			defaultAssetsStored()

		expect:
			given()
				.header(jwtAuthorization(role))
				.when()
				.get("/api/dashboard")
				.then()
				.statusCode(200)
				.body("myItems", equalTo(1))
				.body("otherParts", equalTo(12))

		where:
			role << [SUPERVISOR, ADMIN]
	}

	def "should return all dashboard information, if user has ADMIN and USER roles assigned"() {
		given:
			defaultAssetsStored()

		expect:
			given()
				.header(jwtAuthorization(USER, ADMIN))
				.when()
				.get("/api/dashboard")
				.then()
				.statusCode(200)
				.body("myItems", equalTo(1))
				.body("otherParts", equalTo(12))
	}

	def "should return only 'my items' dashboard information for user with USER role"() {
		given:
			defaultAssetsStored()

		expect:
			given()
				.header(jwtAuthorization(USER))
				.when()
				.get("/api/dashboard")
				.then()
				.statusCode(200)
				.body("myItems", equalTo(1))
				.body("otherParts", nullValue())
	}

	def "should not return dashboard information for user without role"() {
		expect:
			given()
				.header(jwtAuthorizationWithNoRole())
				.when()
				.get("/api/dashboard")
				.then()
				.statusCode(403)
				.body("message", equalTo("User has invalid role to access the dashboard."))
	}

	def "should return dashboard information for pending investigation"() {
		given:
			String assetId = "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978"
			defaultAssetsStored()

		when:
			given()
				.header(jwtAuthorization(ADMIN))
				.contentType(ContentType.JSON)
				.body(asJson(
					[
						partIds    : [assetId],
						description: "at least 15 characters long investigation description"
					]
				))
				.when()
				.post("/api/investigations")
				.then()
				.statusCode(200)

		then:
			given()
				.header(jwtAuthorization(ADMIN))
				.when()
				.get("/api/dashboard")
				.then()
				.statusCode(200)
				.body("myItems", equalTo(1))
				.body("otherParts", equalTo(12))
				.body("investigations", equalTo(1))
	}
}
