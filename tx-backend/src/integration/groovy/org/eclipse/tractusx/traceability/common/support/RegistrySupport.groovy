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

package org.eclipse.tractusx.traceability.common.support

import org.springframework.http.HttpHeaders

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp
import static com.xebialabs.restito.semantics.Action.header
import static com.xebialabs.restito.semantics.Action.ok
import static com.xebialabs.restito.semantics.Action.status
import static com.xebialabs.restito.semantics.Condition.post
import static com.xebialabs.restito.semantics.Condition.startsWithUri
import static com.xebialabs.restito.semantics.Condition.withHeader
import static org.glassfish.grizzly.http.util.HttpStatus.INTERNAL_SERVER_ERROR_500

trait RegistrySupport implements RestitoProvider {

	void assetShellsLookupReturnsData() {
		whenHttp(stubServer()).match(
			startsWithUri("/lookup/shells"),
			withHeader(HttpHeaders.AUTHORIZATION)
		)
			.then(
				ok(),
				header("Content-Type", "application/json"),
				jsonResponseFromFile("./stubs/registry/get/lookup/shells/response_200.json")
			)
	}

	void assetShellsLookupReturnsDataForUpdate() {
		whenHttp(stubServer()).match(
			startsWithUri("/lookup/shells"),
			withHeader(HttpHeaders.AUTHORIZATION)
		)
			.then(
				ok(),
				header("Content-Type", "application/json")
			)
			.withSequence(
				jsonResponseFromFile("./stubs/registry/get/lookup/shells/response_multiple_200.json"),
				jsonResponseFromFile("./stubs/registry/get/lookup/shells/response_update_200.json")
			)
	}

	void assetShellsLookupFailed() {
		whenHttp(stubServer()).match(
			startsWithUri("/lookup/shells"),
			withHeader(HttpHeaders.AUTHORIZATION)
		)
			.then(
				status(INTERNAL_SERVER_ERROR_500),
				header("Content-Type", "application/json")
			)
	}

	void fetchRegistryShellDescriptorsLookupReturnsData() {
		whenHttp(stubServer()).match(
			post("/registry/shell-descriptors/fetch"),
			withHeader(HttpHeaders.AUTHORIZATION)
		)
			.then(
				ok(),
				header("Content-Type", "application/json"),
				jsonResponseFromFile("./stubs/registry/post/registry/shell-descriptors/fetch/response_200.json")
			)
	}

	void fetchRegistryShellDescriptorsLookupReturnsDataForUpdate() {
		whenHttp(stubServer()).match(
			post("/registry/shell-descriptors/fetch"),
			withHeader(HttpHeaders.AUTHORIZATION)
		)
			.then(
				ok(),
				header("Content-Type", "application/json")
			)
			.withSequence(
				jsonResponseFromFile("./stubs/registry/post/registry/shell-descriptors/fetch/response_multiple_200.json"),
				jsonResponseFromFile("./stubs/registry/post/registry/shell-descriptors/fetch/response_update_200.json")
			)
	}

	void fetchRegistryShellDescriptorsLookupReturnsDataFailed() {
		whenHttp(stubServer()).match(
			post("/registry/shell-descriptors/fetch"),
			withHeader(HttpHeaders.AUTHORIZATION)
		)
			.then(
				status(INTERNAL_SERVER_ERROR_500),
				header("Content-Type", "application/json")
			)
	}

	void verifyFetchRegistryShellDescriptorsLookupNotCalled() {
		verifyHttp(stubServer()).never(
			post("/registry/shell-descriptors/fetch")
		)
	}
}
