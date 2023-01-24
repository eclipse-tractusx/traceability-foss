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

package org.eclipse.tractusx.traceability.investigations.adapters.rest

import io.restassured.http.ContentType
import org.eclipse.tractusx.traceability.IntegrationSpecification
import org.eclipse.tractusx.traceability.common.support.AssetsSupport
import org.eclipse.tractusx.traceability.common.support.BpnSupport
import org.eclipse.tractusx.traceability.common.support.InvestigationsSupport
import org.eclipse.tractusx.traceability.common.support.IrsApiSupport
import org.eclipse.tractusx.traceability.common.support.NotificationsSupport
import org.hamcrest.Matchers
import spock.lang.Unroll

import static io.restassured.RestAssured.given
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN

class ReceiverInvestigationsControllerIT extends IntegrationSpecification implements IrsApiSupport, AssetsSupport, InvestigationsSupport, NotificationsSupport, BpnSupport {

	def "should acknowledge received investigation"() {
		given:
			def investigationId = defaultReceivedInvestigationStored()

		when:
			given()
				.contentType(ContentType.JSON)
				.body(asJson(
					[
						status: "ACKNOWLEDGED"
					]
				))
				.header(jwtAuthorization(ADMIN))
				.when()
				.post("/api/investigations/$investigationId/update")
				.then()
				.statusCode(204)

		then:
			given()
				.header(jwtAuthorization(ADMIN))
				.param("page", "0")
				.param("size", "10")
				.contentType(ContentType.JSON)
				.when()
				.get("/api/investigations/received")
				.then()
				.statusCode(200)
				.body("page", Matchers.is(0))
				.body("pageSize", Matchers.is(10))
				.body("content", Matchers.hasSize(1))
	}

	@Unroll
	def "should #action acknowledged investigation"() {
		given:
			def investigationId = defaultAcknowledgedInvestigationStored()

		when:
			given()
				.contentType(ContentType.JSON)
				.body(json)
				.header(jwtAuthorization(ADMIN))
				.when()
				.post("/api/investigations/$investigationId/update")
				.then()
				.statusCode(204)

		then:
			given()
				.header(jwtAuthorization(ADMIN))
				.param("page", "0")
				.param("size", "10")
				.contentType(ContentType.JSON)
				.when()
				.get("/api/investigations/received")
				.then()
				.statusCode(200)
				.body("page", Matchers.is(0))
				.body("pageSize", Matchers.is(10))
				.body("content", Matchers.hasSize(1))

		where:
			action        | json
			"accept"      | asJson([status: "ACCEPTED", reason: "some long accept reason"])
			"decline"     | asJson([status: "DECLINED", reason: "some long decline reason"])
	}

	@Unroll
	def "should not #action not existing investigation"() {
		given:
			def notExistingInvestigationId = 1234

		when:
			given()
				.contentType(ContentType.JSON)
				.body(json)
				.header(jwtAuthorization(ADMIN))
				.when()
				.post("/api/investigations/$notExistingInvestigationId/update")
				.then()
				.statusCode(404)

		then:
			given()
				.header(jwtAuthorization(ADMIN))
				.param("page", "0")
				.param("size", "15")
				.contentType(ContentType.JSON)
				.when()
				.get("/api/investigations/received")
				.then()
				.statusCode(200)
				.body("page", Matchers.is(0))
				.body("pageSize", Matchers.is(15))
				.body("content", Matchers.hasSize(0))

		where:
			action        | json
			"acknowledge" | asJson([status: "ACKNOWLEDGED"])
			"accept"      | asJson([status: "ACCEPTED", reason: "some long accept reason"])
			"decline"     | asJson([status: "DECLINED", reason: "some long decline reason"])
	}

	@Unroll
	def "should not #action with invalid request"() {
		given:
			def notExistingInvestigationId = 12

		when:
			given()
				.contentType(ContentType.JSON)
				.body(json)
				.header(jwtAuthorization(ADMIN))
				.when()
				.post("/api/investigations/$notExistingInvestigationId/update")
				.then()
				.statusCode(400)

		then:
			given()
				.header(jwtAuthorization(ADMIN))
				.param("page", "0")
				.param("size", "15")
				.contentType(ContentType.JSON)
				.when()
				.get("/api/investigations/received")
				.then()
				.statusCode(200)
				.body("page", Matchers.is(0))
				.body("pageSize", Matchers.is(15))
				.body("content", Matchers.hasSize(0))

		where:
			action        | json
			"acknowledge" | asJson([status: "ACKNOWLEDGED", reason: "no reason should be allowed for acknowledging"])
			"accept"      | asJson([status: "ACCEPTED", reason: null])
			"accept"      | asJson([status: "ACCEPTED", reason: " "])
			"decline"     | asJson([status: "DECLINED", reason: null])
			"decline"     | asJson([status: "DECLINED", reason: " "])
	}


}
