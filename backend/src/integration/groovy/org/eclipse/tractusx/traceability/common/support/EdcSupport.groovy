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

import com.xebialabs.restito.semantics.Condition
import org.glassfish.grizzly.http.util.HttpStatus

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp
import static com.xebialabs.restito.semantics.Action.noContent
import static com.xebialabs.restito.semantics.Action.status
import static com.xebialabs.restito.semantics.Condition.composite
import static com.xebialabs.restito.semantics.Condition.method
import static com.xebialabs.restito.semantics.Condition.post
import static com.xebialabs.restito.semantics.Condition.startsWithUri
import static com.xebialabs.restito.semantics.Condition.withHeader
import static org.glassfish.grizzly.http.Method.DELETE

trait EdcSupport implements RestitoProvider {

	private static final Condition EDC_API_KEY_HEADER = withHeader("X-Api-Key", "integration-tests")

	void edcWillCreateNotificationAsset() {
		whenHttp(stubServer()).match(
			post("/data/assets"),
			EDC_API_KEY_HEADER
		).then(
			status(HttpStatus.NO_CONTENT_204)
		)
	}

	void edcWillRemoveNotificationAsset() {
		whenHttp(stubServer()).match(
			method(DELETE),
			startsWithUri("/data/assets/"),
			EDC_API_KEY_HEADER
		).then(
			noContent()
		)
	}

	void edcWillFailToCreateNotificationAsset() {
		whenHttp(stubServer()).match(
			post("/data/assets"),
			EDC_API_KEY_HEADER
		).then(
			status(HttpStatus.INTERNAL_SERVER_ERROR_500)
		)
	}

	void edcNotificationAssetAlreadyExist() {
		whenHttp(stubServer()).match(
			post("/data/assets"),
			EDC_API_KEY_HEADER
		).then(
			status(HttpStatus.CONFLICT_409),
			jsonResponseFromFile("./stubs/edc/post/data/assets/response_409.json")
		)
	}

	void edcWillCreatePolicyDefinition() {
		whenHttp(stubServer()).match(
			post("/data/policydefinitions"),
			EDC_API_KEY_HEADER
		).then(
			status(HttpStatus.NO_CONTENT_204)
		)
	}

	void edcWillRemovePolicyDefinition() {
		whenHttp(stubServer()).match(
			composite(
				method(DELETE),
				startsWithUri("/data/policydefinitions/")
			),
			EDC_API_KEY_HEADER
		).then(
			noContent()
		)
	}

	void edcWillFailToCreatePolicyDefinition() {
		whenHttp(stubServer()).match(
			post("/data/policydefinitions"),
			EDC_API_KEY_HEADER
		).then(
			status(HttpStatus.INTERNAL_SERVER_ERROR_500)
		)
	}

	void edcWillCreateContractDefinition() {
		whenHttp(stubServer()).match(
			post("/data/contractdefinitions"),
			EDC_API_KEY_HEADER
		).then(
			status(HttpStatus.NO_CONTENT_204)
		)
	}

	void edcWillFailToCreateContractDefinition() {
		whenHttp(stubServer()).match(
			post("/data/contractdefinitions"),
			EDC_API_KEY_HEADER
		).then(
			status(HttpStatus.INTERNAL_SERVER_ERROR_500)
		)
	}

	void verifyCreateNotificationAssetEndpointCalledTimes(int times) {
		verifyHttp(stubServer()).times(times,
			post("/data/assets")
		)
	}

	void verifyDeleteNotificationAssetEndpointCalledTimes(int times) {
		verifyHttp(stubServer()).times(times,
			method(DELETE),
			startsWithUri("/data/assets/")
		)
	}

	void verifyCreatePolicyDefinitionEndpointCalledTimes(int times) {
			verifyHttp(stubServer()).times(times,
			post("/data/policydefinitions")
		)
	}

	void verifyDeletePolicyDefinitionEndpointCalledTimes(int times) {
		verifyHttp(stubServer()).times(times,
			method(DELETE),
			startsWithUri("/data/policydefinitions/")
		)
	}

	void verifyCreateContractDefinitionEndpointCalledTimes(int times) {
		verifyHttp(stubServer()).times(times,
			post("/data/contractdefinitions")
		)
	}

	void verifyDeleteContractDefinitionEndpointCalledTimes(int times) {
		verifyHttp(stubServer()).times(times,
			method(DELETE),
			startsWithUri("/data/contractdefinitions/")
		)
	}
}
