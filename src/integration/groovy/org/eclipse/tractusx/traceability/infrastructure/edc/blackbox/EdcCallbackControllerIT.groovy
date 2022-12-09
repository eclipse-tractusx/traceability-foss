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
package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox

import io.restassured.http.ContentType
import org.eclipse.tractusx.traceability.IntegrationSpecification
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.cache.InMemoryEndpointDataReferenceCache
import org.eclipse.tractusx.traceability.investigations.adapters.mock.EDCProviderConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp
import static com.xebialabs.restito.semantics.Action.ok
import static com.xebialabs.restito.semantics.Condition.post
import static io.restassured.RestAssured.given
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN

class EdcCallbackControllerIT  extends IntegrationSpecification {

	@Autowired
	InMemoryEndpointDataReferenceCache endpointDataReferenceCache

	def "should execute callback"() {
		given:
			String contractAgreementId = "contractAgreementId"
			thereIsRedirectionEndpoint()
			endpointDataReferenceCache.storeAgreementId(contractAgreementId)

		when:
			given()
				.contentType(ContentType.JSON)
				.body(
					asJson(
						[
							id: "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb",
							endpoint: "endpoint",
							authKey: "authKey",
							authCode: "authCode",
							properties: [
							    "cid": contractAgreementId
							]
						]
					)
				)
				.header(jwtAuthorization(ADMIN))
				.when()
				.post("/api/callback/endpoint-data-reference")
				.then()
				.statusCode(200)

		then:
			endpointDataReferenceCache.get(contractAgreementId)
			verifyRedirectionNotCalled()
	}

	def "should redirect callback when no contract agreement exists"() {
		given:
			String contractAgreementId = "contractAgreementId2";
			thereIsRedirectionEndpoint()

		when:
			given()
				.contentType(ContentType.JSON)
				.body(
					asJson(
						[
							id: "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb",
							endpoint: "endpoint",
							authKey: "authKey",
							authCode: "authCode",
							properties: [
								"cid": contractAgreementId
							]
						]
					)
				)
				.header(jwtAuthorization(ADMIN))
				.when()
				.post("/api/callback/endpoint-data-reference")
				.then()
				.statusCode(200)

		then:
			endpointDataReferenceCache.get(contractAgreementId) == null
			verifyRedirectionCalledOnce()
	}

	private void thereIsRedirectionEndpoint() {
		whenHttp(stubServer()).match(
			post("/callback/redirect")
		)
		.then(ok())
	}

	private void verifyRedirectionNotCalled() {
		verifyHttp(stubServer()).never(
			post("/callback/redirect")
		)
	}

	private void verifyRedirectionCalledOnce() {
		verifyHttp(stubServer()).once(
			post("/callback/redirect")
		)
	}

	@TestConfiguration
	static class EdcTestConfig {

		@Bean
		EdcCallbackController edcCallbackController(InMemoryEndpointDataReferenceCache endpointDataReferenceCache, RestTemplateBuilder restTemplateBuilder, EDCProviderConfiguration edcProviderConfiguration) {
			return new EdcCallbackController(endpointDataReferenceCache, restTemplateBuilder, edcProviderConfiguration)
		}
	}

}
