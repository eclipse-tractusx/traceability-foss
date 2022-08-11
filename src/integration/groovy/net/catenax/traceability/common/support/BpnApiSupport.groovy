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

package net.catenax.traceability.common.support

import com.xebialabs.restito.semantics.Action
import org.glassfish.grizzly.http.util.HttpStatus
import org.springframework.http.HttpHeaders

import java.util.regex.Pattern

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp
import static com.xebialabs.restito.semantics.Action.header
import static com.xebialabs.restito.semantics.Action.ok
import static com.xebialabs.restito.semantics.Action.resourceContent
import static com.xebialabs.restito.semantics.Action.status
import static com.xebialabs.restito.semantics.Condition.get
import static com.xebialabs.restito.semantics.Condition.matchesUri
import static com.xebialabs.restito.semantics.Condition.parameter
import static com.xebialabs.restito.semantics.Condition.withHeader
import static java.util.regex.Pattern.compile

trait BpnApiSupport implements RestitoProvider {

	void bpnApiReturnsBusinessPartnerDataFor(String... ids) {
		ids.each { it -> bpnApiReturnsBusinessPartnerDataFor(it) }
	}

	void bpnApiReturnsBusinessPartnerDataWithoutNamesFor(String... ids) {
		ids.each { it -> bpnApiReturnsBusinessPartnerDataWithoutNamesFor(it) }
	}

	void bpnApiReturnsNoBusinessPartnerDataFor(String... ids) {
		ids.each { it -> bpnApiReturnsNoBusinessPartnerDataFor(it) }
	}

	void bpnApiReturnsBusinessPartnerDataFor(String id) {
		whenHttp(stubServer()).match(
			get("/api/catena/business-partner/$id".toString()),
			parameter("idType", "BPN"),
			withHeader(HttpHeaders.AUTHORIZATION)
		)
			.then(
				ok(),
				header("Content-Type", "application/json"),
				jsonResponseFromFile("./stubs/bpn/get/business-partner/$id/response_200.json")
			)
	}

	void bpnApiReturnsBusinessPartnerDataWithoutNamesFor(String id) {
		whenHttp(stubServer()).match(
			get("/api/catena/business-partner/$id".toString()),
			parameter("idType", "BPN"),
			withHeader(HttpHeaders.AUTHORIZATION)
		)
			.then(
				ok(),
				header("Content-Type", "application/json"),
				jsonResponseFromFile("./stubs/bpn/get/business-partner/$id/response_no_names_200.json")
			)
	}

	void bpnApiReturnsNoBusinessPartnerDataFor(String id) {
		whenHttp(stubServer()).match(
			get("/api/catena/business-partner/$id".toString()),
			parameter("idType", "BPN"),
			withHeader(HttpHeaders.AUTHORIZATION)
		)
			.then(
				status(HttpStatus.NOT_FOUND_404),
				header("Content-Type", "application/json"),
				jsonResponseFromFile("./stubs/bpn/get/business-partner/$id/response_404.json")
			)
	}

	void verifyBpnApiCalledForBusinessPartnerDetails(int times) {
		verifyHttp(stubServer()).times(
			times,
			matchesUri(compile("/api/catena/business-partner/.*", Pattern.DOTALL))
		)
	}

	private Action jsonResponseFromFile(String location) {
		return resourceContent(location)
	}
}
