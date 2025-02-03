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
import org.eclipse.edc.spi.types.domain.edr.EndpointDataReference;
import org.eclipse.tractusx.irs.edc.client.EndpointDataReferenceStorage;
import org.eclipse.tractusx.traceability.integration.common.config.RestitoConfig;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp;
import static com.xebialabs.restito.semantics.Action.noContent;
import static com.xebialabs.restito.semantics.Action.ok;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Condition.composite;
import static com.xebialabs.restito.semantics.Condition.get;
import static com.xebialabs.restito.semantics.Condition.matchesUri;
import static com.xebialabs.restito.semantics.Condition.method;
import static com.xebialabs.restito.semantics.Condition.post;
import static com.xebialabs.restito.semantics.Condition.startsWithUri;
import static com.xebialabs.restito.semantics.Condition.withHeader;
import static org.glassfish.grizzly.http.Method.DELETE;

@Component
public class EdcSupport {

    @Autowired
    RestitoProvider restitoProvider;

    private static final String uuidRegex = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

    private static final Condition EDC_API_KEY_HEADER = withHeader("X-Api-Key", "integration-tests");
    @Autowired
    EndpointDataReferenceStorage endpointDataReferenceStorage;

    public void performSupportActionsForAsyncNotificationMessageExecutor() {
        edcWillCallCallbackController();
        edcWillReturnCatalog();
        edcWillCreateContractNegotiation();
        edcWillReturnContractNegotiationOnlyState();
        edcWillReturnContractNegotiationState();
        edcWillCreateTransferprocesses();
        edcWillReturnTransferprocessesOnlyState();
        edcWillReturnTransferprocessesState();
        edcWillSendRequest();
    }

    public void performSupportActionsForBpdmAccess() {
        edcWillReturnCatalogForBpdmGoldenRecord();
        edcWillCreateContractNegotiation();
        edcWillReturnContractNegotiationOnlyState();
        edcWillReturnContractNegotiationState();
        edcWillCreateTransferprocesses();
        edcWillReturnTransferprocessesOnlyState();
        edcWillReturnTransferprocessesState();
        edcWillCallCallbackController();
        edcWillSendRequestAndReturnBpdmResponse();
    }

    public void edcWillCallCallbackController() {
        Map<String, Object> additionalProperties = new HashMap<>();
        additionalProperties.put("additionalProperty1", "value1");
        EndpointDataReference endpointDataReference = EndpointDataReference.Builder
                .newInstance()
                .id("id")
                .endpoint("http://localhost:" + RestitoConfig.getStubServer().getPort() + "/endpointdatareference")
                .authKey("X-Api-Key")
                .contractId("contractId")
                .authCode("integration-tests")
                .properties(additionalProperties)
                .build();

        endpointDataReferenceStorage.put("NmYxMjk2ZmUtYmRlZS00ZTViLTk0NzktOWU0YmQyYWYyNGQ3:ZDBjZGUzYjktOWEwMS00N2QzLTgwNTgtOTU2MjgyOGY2ZDBm:YjYxMjcxM2MtNjdkNC00N2JlLWI0NjMtNDdjNjk4YTk1Mjky", endpointDataReference);
    }
    public void edcWillCreateNotificationAsset() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/management/v2/assets"),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.OK_200)
        );
    }

    public void edcWillCreateAsset() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/management/v3/assets"),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.OK_200)
        );
    }

    public void edcWillFailToCreateAsset() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/management/v3/assets"),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.SERVICE_UNAVAILABLE_503)
        );
    }

    public void edcWillRemoveNotificationAsset() {
        whenHttp(restitoProvider.stubServer()).match(
                method(DELETE),
                startsWithUri("/management/v3/assets/"),
                EDC_API_KEY_HEADER
        ).then(
                noContent()
        );
    }

    public void edcWillRemoveContractDefinition() {
        whenHttp(restitoProvider.stubServer()).match(
                method(DELETE),
                startsWithUri("/management/v2/contractdefinitions"),
                EDC_API_KEY_HEADER
        ).then(
                ok()
        );
    }

    public void edcWillReturnCatalog() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/management/v2/catalog/request"),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.OK_200),
                restitoProvider.jsonResponseFromFile("stubs/edc/post/data/contractagreements/catalog_response_200.json")
        );
    }

    public void edcWillReturnCatalogForBpdmGoldenRecord() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/management/v2/catalog/request"),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.OK_200),
                restitoProvider.jsonResponseFromFile("stubs/edc/post/data/contractagreements/catalog_response_bpdm_200.json")
        );
    }

    public void edcWillReturnOnlyOneContractAgreement() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/management/v2/contractagreements/request"),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.OK_200),
                restitoProvider.jsonResponseFromFile("stubs/edc/post/data/contractagreements/one_contractagreement_response_200.json")
        );
    }

    public void edcWillReturnContractAgreementNegotiation() {
        whenHttp(restitoProvider.stubServer()).match(
                matchesUri(Pattern.compile("/management/v2/contractagreements/[\\w]+/negotiation")),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.OK_200),
                restitoProvider.jsonResponseFromFile("stubs/edc/post/data/contractagreements/contractagreement_negotiation_response_200.json")
        );
    }

    public void edcWillReturnContractDefinitions() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/management/v2/contractdefinitions/request"),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.OK_200),
                restitoProvider.jsonResponseFromFile("stubs/edc/post/data/contractdefinitions/contractdefinitions_response_200.json")
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

    public void edcWillCreatePolicyDefinition() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/management/v2/policydefinitions"),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.OK_200)
        );
    }

    public void edcWillReturnConflictWhenCreatePolicyDefinition() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/management/v2/policydefinitions"),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.CONFLICT_409)
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
                status(HttpStatus.SERVICE_UNAVAILABLE_503)
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

    public void edcWillCreateContractNegotiation() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/management/v2/contractnegotiations"),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.OK_200),
                restitoProvider.jsonResponseFromFile("stubs/edc/post/data/contractagreements/contractnegotiation_response_200.json")
        );
    }

    public void edcWillReturnContractNegotiationOnlyState() {
        whenHttp(restitoProvider.stubServer()).match(
                matchesUri(Pattern.compile("/management/v2/contractnegotiations/" + uuidRegex + "/state")),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.OK_200),
                restitoProvider.jsonResponseFromFile("stubs/edc/post/data/contractagreements/contractnegotiationonlystate_response_200.json")
        );
    }

    public void edcWillReturnContractNegotiationState() {
        whenHttp(restitoProvider.stubServer()).match(
                matchesUri(Pattern.compile("/management/v2/contractnegotiations/" + uuidRegex)),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.OK_200),
                restitoProvider.jsonResponseFromFile("stubs/edc/post/data/contractagreements/contractnegotiationstate_response_200.json")
        );
    }

    public void edcWillCreateTransferprocesses() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/management/v2/transferprocesses"),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.OK_200),
                restitoProvider.jsonResponseFromFile("stubs/edc/post/data/contractagreements/transferprocesses_response_200.json")
        );
    }

    public void edcWillReturnTransferprocessesOnlyState() {
        whenHttp(restitoProvider.stubServer()).match(
                matchesUri(Pattern.compile("/management/v2/transferprocesses/" + uuidRegex + "/state")),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.OK_200),
                restitoProvider.jsonResponseFromFile("stubs/edc/post/data/contractagreements/transferprocessesonlystate_response_200.json")
        );
    }

    public void edcWillReturnTransferprocessesState() {
        whenHttp(restitoProvider.stubServer()).match(
                matchesUri(Pattern.compile("/management/v2/transferprocesses/" + uuidRegex)),
                EDC_API_KEY_HEADER

        ).then(
                status(HttpStatus.OK_200),
                restitoProvider.jsonResponseFromFile("stubs/edc/post/data/contractagreements/transferprocessesstate_response_200.json")
        );
    }

    public void edcWillSendRequest() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/endpointdatareference"),
                EDC_API_KEY_HEADER
        ).then(
                status(HttpStatus.OK_200)
        );
    }

    public void edcWillSendRequestAndReturnBpdmResponse() {
        whenHttp(restitoProvider.stubServer()).match(
                get("/endpointdatareference")
        ).then(
                status(HttpStatus.OK_200),
                restitoProvider.jsonResponseFromFile("stubs/bpdm/response_200.json")
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
