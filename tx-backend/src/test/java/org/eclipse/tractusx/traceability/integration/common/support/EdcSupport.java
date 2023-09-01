/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.integration.common.support;

import com.xebialabs.restito.semantics.Condition;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.noContent;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Condition.composite;
import static com.xebialabs.restito.semantics.Condition.method;
import static com.xebialabs.restito.semantics.Condition.post;
import static com.xebialabs.restito.semantics.Condition.startsWithUri;
import static com.xebialabs.restito.semantics.Condition.withHeader;
import static org.glassfish.grizzly.http.Method.DELETE;

@Component
public class EdcSupport {

    @Autowired
    RestitoProvider restitoProvider;

    private static final Condition EDC_API_KEY_HEADER = withHeader("X-Api-Key", "integration-tests");

    public void edcWillCreateNotificationAsset() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/management/v2/assets"),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.OK_200)
        );
    }

    public void edcWillRemoveNotificationAsset() {
        whenHttp(restitoProvider.stubServer()).match(
                method(DELETE),
                startsWithUri("/management/v2/assets/"),
                EDC_API_KEY_HEADER
        ).then(
                noContent()
        );
    }

    public void edcWillFailToCreateNotificationAsset() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/management/v2/assets"),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.INTERNAL_SERVER_ERROR_500)
        );
    }

    void edcNotificationAssetAlreadyExist() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/management/v2/assets"),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.CONFLICT_409),
                restitoProvider.jsonResponseFromFile("./stubs/edc/post/management/v2/assets/response_409.json")
        );
    }

    public void edcWillCreatePolicyDefinition() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/management/v2/policydefinitions"),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.OK_200)
        );
    }

    public void edcWillRemovePolicyDefinition() {
        whenHttp(restitoProvider.stubServer()).match(
                composite(
                        method(DELETE),
                        startsWithUri("/management/v2/policydefinitions/")
                ),
                EDC_API_KEY_HEADER
        ).then(
                noContent()
        );
    }

    public void edcWillFailToCreatePolicyDefinition() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/management/v2/policydefinitions"),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.INTERNAL_SERVER_ERROR_500)
        );
    }

    public void edcWillCreateContractDefinition() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/management/v2/contractdefinitions"),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.OK_200)
        );
    }

    public void edcWillFailToCreateContractDefinition() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/management/v2/contractdefinitions"),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.INTERNAL_SERVER_ERROR_500)
        );
    }

    public void verifyCreateNotificationAssetEndpointCalledTimes(int times) {
        verifyHttp(restitoProvider.stubServer()).times(times,
                post("/management/v2/assets")
        );
    }

    public void verifyDeleteNotificationAssetEndpointCalledTimes(int times) {
        verifyHttp(restitoProvider.stubServer()).times(times,
                method(DELETE),
                startsWithUri("/management/v2/assets")
        );
    }

    public void verifyCreatePolicyDefinitionEndpointCalledTimes(int times) {
        verifyHttp(restitoProvider.stubServer()).times(times,
                post("/management/v2/policydefinitions")
        );
    }

    public void verifyDeletePolicyDefinitionEndpointCalledTimes(int times) {
        verifyHttp(restitoProvider.stubServer()).times(times,
                method(DELETE),
                startsWithUri("/management/v2/policydefinitions")
        );
    }

    public void verifyCreateContractDefinitionEndpointCalledTimes(int times) {
        verifyHttp(restitoProvider.stubServer()).times(times,
                post("/management/v2/contractdefinitions")
        );
    }

    public void verifyDeleteContractDefinitionEndpointCalledTimes(int times) {
        verifyHttp(restitoProvider.stubServer()).times(times,
                method(DELETE),
                startsWithUri("/management/v2/contractdefinitions")
        );
    }
}
