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

package org.eclipse.tractusx.traceability.assets.infrastructure.adapters.rest

import io.restassured.http.ContentType
import org.eclipse.tractusx.traceability.common.support.AssetsSupport
import org.eclipse.tractusx.traceability.common.support.InvestigationsSupport
import org.eclipse.tractusx.traceability.IntegrationSpecification
import spock.lang.Unroll

import static io.restassured.RestAssured.given
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN
import static org.eclipse.tractusx.traceability.common.security.JwtRole.SUPERVISOR
import static org.eclipse.tractusx.traceability.common.security.JwtRole.USER
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.nullValue

class DashboardControllerIT extends IntegrationSpecification implements AssetsSupport, InvestigationsSupport {

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

	def "should return dashboard information for pending investigation"() {
		given:
			String assetId = "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978"

		and:
			defaultAssetsStored()
			defaultReceivedInvestigationStored()

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
				.statusCode(201)

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
