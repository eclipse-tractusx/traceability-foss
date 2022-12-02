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

package net.catenax.traceability.investigations.adapters.rest

import io.restassured.http.ContentType
import net.catenax.traceability.IntegrationSpecification
import net.catenax.traceability.assets.domain.model.Asset
import net.catenax.traceability.common.support.AssetsSupport
import net.catenax.traceability.common.support.BpnSupport
import net.catenax.traceability.common.support.InvestigationsSupport
import net.catenax.traceability.common.support.IrsApiSupport
import net.catenax.traceability.common.support.NotificationsSupport
import net.catenax.traceability.infrastructure.jpa.investigation.InvestigationEntity
import net.catenax.traceability.infrastructure.jpa.notification.NotificationEntity
import net.catenax.traceability.investigations.domain.model.InvestigationStatus
import org.hamcrest.Matchers
import spock.lang.Unroll

import java.time.Instant

import static io.restassured.RestAssured.given
import static net.catenax.traceability.common.security.JwtRole.ADMIN
import static net.catenax.traceability.common.support.ISO8601DateTimeMatcher.isIso8601DateTime

class InvestigationsControllerIT extends IntegrationSpecification implements IrsApiSupport, AssetsSupport, InvestigationsSupport, NotificationsSupport, BpnSupport {

	def "should start investigation"() {
		given:
			List<String> partIds = [
				"urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
				"urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb", // BPN: BPNL00000003AYRE
				"urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
			]
			String description = "at least 15 characters long investigation description"

		and:
			defaultAssetsStored()

		when:
			given()
				.contentType(ContentType.JSON)
				.body(
					asJson(
						[
							partIds    : partIds,
							description: description
						]
					)
				)
				.header(jwtAuthorization(ADMIN))
				.when()
				.post("/api/investigations")
				.then()
				.statusCode(201)
				.body("id", Matchers.isA(Number.class))

		then:
			partIds.each { partId ->
				Asset asset = assetRepository().getAssetById(partId)
				assert asset
				assert asset.isUnderInvestigation()
			}

		and:
			assertNotificationsSize(2)

		and:
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
				.body("content", Matchers.hasSize(1))
	}

	def "should cancel investigation"() {
		given:
			defaultAssetsStored()

		and:
			def investigationId = given()
				.contentType(ContentType.JSON)
				.body(
					asJson(
						[
							partIds    : ["urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978"],
							description: "at least 15 characters long investigation description"
						]
					)
				)
				.header(jwtAuthorization(ADMIN))
				.when()
				.post("/api/investigations")
				.then()
				.statusCode(201)
				.extract().path("id")

		and:
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
				.body("content", Matchers.hasSize(1))

		expect:
			given()
				.header(jwtAuthorization(ADMIN))
				.contentType(ContentType.JSON)
				.when()
				.post("/api/investigations/$investigationId/cancel")
				.then()
				.statusCode(204)

		and:
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
				.body("content", Matchers.hasSize(0))
	}

	def "should approve investigation status"() {
		given:
			List<String> partIds = [
				"urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
				"urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
			]
			String description = "at least 15 characters long investigation description"

		and:
			defaultAssetsStored()

		when:
			def investigationId = given()
				.contentType(ContentType.JSON)
				.body(asJson([
					partIds    : partIds,
					description: description
				]))
				.header(jwtAuthorization(ADMIN))
				.when()
				.post("/api/investigations")
				.then()
				.statusCode(201)
				.extract().path("id")

		then:
			assertInvestigationsSize(1)

		when:
			given()
				.contentType(ContentType.JSON)
				.header(jwtAuthorization(ADMIN))
				.when()
				.post("/api/investigations/{investigationId}/approve", investigationId)
				.then()
				.statusCode(204)

		then:
			eventually {
				assertNotificationsSize(2)
				assertNotifications { NotificationEntity notification ->
					assert notification.edcUrl != null
					assert notification.contractAgreementId != null
				}
			}
	}

	def "should close investigation"() {
		given:
			List<String> partIds = [
				"urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978", // BPN: BPNL00000003AYRE
				"urn:uuid:0ce83951-bc18-4e8f-892d-48bad4eb67ef"  // BPN: BPNL00000003AXS3
			]
			String description = "at least 15 characters long investigation description"

		and:
			defaultAssetsStored()

		when:
			def investigationId = given()
				.contentType(ContentType.JSON)
				.body(asJson([
					partIds    : partIds,
					description: description
				]))
				.header(jwtAuthorization(ADMIN))
				.when()
				.post("/api/investigations")
				.then()
				.statusCode(201)
				.extract().path("id")

		then:
			assertInvestigationsSize(1)

		when:
			given()
				.contentType(ContentType.JSON)
				.header(jwtAuthorization(ADMIN))
				.when()
				.post("/api/investigations/{investigationId}/approve", investigationId)
				.then()
				.statusCode(204)

		then:
			eventually {
				assertNotificationsSize(2)
				assertNotifications { NotificationEntity notification ->
					assert notification.edcUrl != null
					assert notification.contractAgreementId != null
				}
			}

		when:
			given()
				.contentType(ContentType.JSON)
				.body(asJson([
					reason    : "Out of date",
					description: description
				]))
				.header(jwtAuthorization(ADMIN))
				.when()
				.post("/api/investigations/{investigationId}/close", investigationId)
				.then()
				.statusCode(204)

		then:
			eventually {
				assertInvestigationsSize(1)
				assertInvestigationStatus(InvestigationStatus.CLOSED)
			}
	}

	def "should not cancel not existing investigation"() {
		expect:
			given()
				.header(jwtAuthorization(ADMIN))
				.contentType(ContentType.JSON)
				.when()
				.post("/api/investigations/1/cancel")
				.then()
				.statusCode(404)
				.body("message", Matchers.is("Investigation not found for 1 id"))
	}

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

	@Unroll
	def "should not #action investigations without authentication"() {
		expect:
			given()
				.param("page", "0")
				.param("size", "10")
				.contentType(ContentType.JSON)
				.when()
				.post("/api/investigations/1/cancel")
				.then()
				.statusCode(401)

		where:
			action << ["approve", "cancel", "close"]
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
				new InvestigationEntity([], testBpn, InvestigationStatus.CREATED, "", "1", now.minusSeconds(10L)),
				new InvestigationEntity([], testBpn, InvestigationStatus.CREATED, "", "2", now.plusSeconds(21L)),
				new InvestigationEntity([], testBpn, InvestigationStatus.CREATED, "", "3", now),
				new InvestigationEntity([], testBpn, InvestigationStatus.CREATED, "", "4", now.plusSeconds(20L)),
				new InvestigationEntity([], testBpn, InvestigationStatus.RECEIVED, "", "5", now.plusSeconds(40L))
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
				storedInvestigation(new InvestigationEntity([], testBpn, InvestigationStatus.CREATED, "", "", now))
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
				storedInvestigation(new InvestigationEntity([], testBpn, InvestigationStatus.RECEIVED, "", "", now))
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
				new InvestigationEntity([], testBpn, InvestigationStatus.RECEIVED, "", "1", now.minusSeconds(5L)),
				new InvestigationEntity([], testBpn, InvestigationStatus.RECEIVED, "",  "2", now.plusSeconds(2L)),
				new InvestigationEntity([], testBpn, InvestigationStatus.RECEIVED, "", "3", now),
				new InvestigationEntity([], testBpn, InvestigationStatus.RECEIVED, "", "4", now.plusSeconds(20L)),
				new InvestigationEntity([], testBpn, InvestigationStatus.CREATED, "", "5", now.plusSeconds(40L))
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
			Long investigationId = storedInvestigation(new InvestigationEntity([], testBpn, "1", InvestigationStatus.RECEIVED, Instant.now()))

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
