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
import org.eclipse.tractusx.traceability.common.support.BpnSupport
import org.eclipse.tractusx.traceability.common.support.InvestigationsSupport
import org.eclipse.tractusx.traceability.infrastructure.jpa.investigation.InvestigationEntity
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationSide
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus
import org.hamcrest.Matchers
import spock.lang.Unroll

import java.time.Instant

import static io.restassured.RestAssured.given
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN
import static org.eclipse.tractusx.traceability.common.support.ISO8601DateTimeMatcher.isIso8601DateTime

class ReadInvestigationsControllerIT extends IntegrationSpecification implements InvestigationsSupport, BpnSupport {

	@Unroll
	def "should not return #type investigations without authentication"() {
		expect:
			given()
				.param("page", "0")
				.param("size", "10")
				.contentType(ContentType.JSON)
				.when()
				.get("/api/investigations/$type")
				.then()
				.statusCode(401)
		where:
			type << ["created", "received"]
	}

	def "should not return investigation without authentication"() {
		expect:
			given()
				.contentType(ContentType.JSON)
				.when()
				.get("/api/investigations/123")
				.then()
				.statusCode(401)
	}

	def "should return no investigations"() {
		expect:
			given()
				.header(jwtAuthorization(ADMIN))
				.param("page", "0")
				.param("size", "10")
				.contentType(ContentType.JSON)
				.when()
				.get("/api/investigations/$type")
				.then()
				.statusCode(200)
				.body("page", Matchers.is(0))
				.body("pageSize", Matchers.is(10))
				.body("content", Matchers.hasSize(0))

		where:
			type << ["created", "received"]
	}

	def "should return created investigations sorted by creation time"() {
		given:
			Instant now = Instant.now()
			String testBpn = testBpn()

		and:
			storedInvestigations(
				new InvestigationEntity([], testBpn, InvestigationStatus.CREATED, InvestigationSide.SENDER, "", "1", now.minusSeconds(10L)),
				new InvestigationEntity([], testBpn, InvestigationStatus.CREATED, InvestigationSide.SENDER, "", "2", now.plusSeconds(21L)),
				new InvestigationEntity([], testBpn, InvestigationStatus.CREATED, InvestigationSide.SENDER, "", "3", now),
				new InvestigationEntity([], testBpn, InvestigationStatus.CREATED, InvestigationSide.SENDER, "", "4", now.plusSeconds(20L)),
				new InvestigationEntity([], testBpn, InvestigationStatus.RECEIVED, InvestigationSide.RECEIVER, "", "5", now.plusSeconds(40L))
			)

		expect:
			given()
				.header(jwtAuthorization(ADMIN))
				.param("page", "0")
				.param("size", "10")
				.contentType(ContentType.JSON)
				.when()
				.get("/api/investigations/created")
				.then()
				.statusCode(200)
				.body("page", Matchers.is(0))
				.body("pageSize", Matchers.is(10))
				.body("content", Matchers.hasSize(4))
				.body("totalItems", Matchers.is(4))
				.body("content.description", Matchers.containsInRelativeOrder("2", "4", "3", "1"))
				.body("content.createdBy", Matchers.hasItems(testBpn))
				.body("content.createdDate", Matchers.hasItems(isIso8601DateTime()))
	}

	def "should return properly paged created investigations"() {
		given:
			Instant now = Instant.now()
			String testBpn = testBpn()

		and:
			(1..100).each { it ->
				storedInvestigation(new InvestigationEntity([], testBpn, InvestigationStatus.CREATED, InvestigationSide.SENDER, "", "", now))
			}

		expect:
			given()
				.header(jwtAuthorization(ADMIN))
				.param("page", "2")
				.param("size", "10")
				.contentType(ContentType.JSON)
				.when()
				.get("/api/investigations/created")
				.then()
				.statusCode(200)
				.body("page", Matchers.is(2))
				.body("pageSize", Matchers.is(10))
				.body("content", Matchers.hasSize(10))
				.body("totalItems", Matchers.is(100))
	}

	def "should return properly paged received investigations"() {
		given:
			Instant now = Instant.now()
			String testBpn = testBpn()

		and:
			(101..200).each { it ->
				storedInvestigation(new InvestigationEntity([], testBpn, InvestigationStatus.RECEIVED, InvestigationSide.RECEIVER, "", "", now))
			}

		expect:
			given()
				.header(jwtAuthorization(ADMIN))
				.param("page", "2")
				.param("size", "10")
				.contentType(ContentType.JSON)
				.when()
				.get("/api/investigations/received")
				.then()
				.statusCode(200)
				.body("page", Matchers.is(2))
				.body("pageSize", Matchers.is(10))
				.body("content", Matchers.hasSize(10))
				.body("totalItems", Matchers.is(100))
	}

	def "should return received investigations sorted by creation time"() {
		given:
			Instant now = Instant.now()
			String testBpn = testBpn()

		and:
			storedInvestigations(
				new InvestigationEntity([], testBpn, InvestigationStatus.RECEIVED, InvestigationSide.RECEIVER, "", "1", now.minusSeconds(5L)),
				new InvestigationEntity([], testBpn, InvestigationStatus.RECEIVED, InvestigationSide.RECEIVER, "", "2", now.plusSeconds(2L)),
				new InvestigationEntity([], testBpn, InvestigationStatus.RECEIVED, InvestigationSide.RECEIVER, "", "3", now),
				new InvestigationEntity([], testBpn, InvestigationStatus.RECEIVED, InvestigationSide.RECEIVER, "", "4", now.plusSeconds(20L)),
				new InvestigationEntity([], testBpn, InvestigationStatus.CREATED, InvestigationSide.SENDER, "", "5", now.plusSeconds(40L))
			)

		expect:
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
				.body("content", Matchers.hasSize(4))
				.body("totalItems", Matchers.is(4))
				.body("content.description", Matchers.containsInRelativeOrder("4", "2", "3", "1"))
				.body("content.createdBy", Matchers.hasItems(testBpn))
				.body("content.createdDate", Matchers.hasItems(isIso8601DateTime()))
	}

	def "should not find non existing investigation"() {
		expect:
			given()
				.header(jwtAuthorization(ADMIN))
				.contentType(ContentType.JSON)
				.when()
				.get("/api/investigations/1234")
				.then()
				.statusCode(404)
				.body("message", Matchers.is("Investigation not found for 1234 id"))
	}

	def "should return investigation by id"() {
		given:
			String testBpn = testBpn()
			Long investigationId = storedInvestigation(new InvestigationEntity([], testBpn, "1", InvestigationStatus.RECEIVED, InvestigationSide.SENDER, Instant.now()))

		expect:
			given()
				.header(jwtAuthorization(ADMIN))
				.contentType(ContentType.JSON)
				.when()
				.get("/api/investigations/$investigationId")
				.then()
				.statusCode(200)
				.body("id", Matchers.is(investigationId.toInteger()))
				.body("status", Matchers.is("RECEIVED"))
				.body("description", Matchers.is("1"))
				.body("assetIds", Matchers.empty())
				.body("createdBy", Matchers.is(testBpn))
				.body("createdDate", isIso8601DateTime())
	}
}
