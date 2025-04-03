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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.xebialabs.restito.semantics.Action;
import com.xebialabs.restito.semantics.Condition;
import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.common.config.PolicyStartUpConfig;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.header;
import static com.xebialabs.restito.semantics.Action.ok;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Condition.get;
import static com.xebialabs.restito.semantics.Condition.post;
import static com.xebialabs.restito.semantics.Condition.withHeader;
import static io.restassured.RestAssured.given;

@Component
public class IrsApiSupport {

    @Autowired
    RestitoProvider restitoProvider;

    @Autowired
    PolicyStartUpConfig policyStartUpConfig;

    public void provideAcceptedPolicies() throws JsonProcessingException {
        irsApiReturnsPolicies();
        policyStartUpConfig.registerDecentralRegistryPermissions();
    }

    public void irsApiTriggerJob() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/irs/jobs"),
                withHeader(HttpHeaders.AUTHORIZATION)
        ).then(
                ok(),
                header("Content-Type", "application/json"),
                restitoProvider.jsonResponseFromFile("./stubs/irs/post/jobs/response_200.json")
        );
    }

    public void irsApiTriggerOrder() {
        whenHttp(restitoProvider.stubServer()).match(
                post("/irs/orders")
        ).then(
                ok(),
                header("Content-Type", "application/json"),
                restitoProvider.jsonResponseFromFile("./stubs/irs/post/jobs/response_200.json")
        );
    }

    public void irsJobDetailsAsPlanned() {
        whenHttp(restitoProvider.stubServer()).match(
                        get("/irs/jobs/ebb79c45-7bba-4169-bf17-SUCCESSFUL_AS_PLANNED")
                )
                .then(
                        ok(),
                        header("Content-Type", "application/json"),

                        restitoProvider.jsonResponseFromFile("./stubs/irs/get/jobs/id/response_200_downward_asPlanned.json"));

    }

    public void irsApiReturnsJobDetails() {
        whenHttp(restitoProvider.stubServer()).match(
                        get("/irs/jobs/ebb79c45-7bba-4169-bf17-3e719989ab54")
                )
                .then(
                        ok(),
                        header("Content-Type", "application/json"),
                        restitoProvider.jsonResponseFromFile("./stubs/irs/get/jobs/id/response_200.json")
                );
    }

    public void irsApiReturnsOrderAndBatchDetails() {
        whenHttp(restitoProvider.stubServer()).match(
                        get("/irs/orders/ebb79c45-7bba-4169-bf17-3e719989ab54/batches/ebb79c45-7bba-4169-bf17-3e719989ab55")
                )
                .then(
                        ok(),
                        header("Content-Type", "application/json"),
                        restitoProvider.jsonResponseFromFile("./stubs/irs/order/response_200.json")
                );
    }

    public void irsApiReturnsOrderAndBatchDetailsAsPlanned() {
        whenHttp(restitoProvider.stubServer()).match(
                        get("/irs/orders/ebb79c45-7bba-4169-bf17-3e719989ab57/batches/ebb79c45-7bba-4169-bf17-3e719989ab58")
                )
                .then(
                        ok(),
                        header("Content-Type", "application/json"),
                        restitoProvider.jsonResponseFromFile("./stubs/irs/order/response_200_asplanned.json")
                );
    }

    public void irsApiReturnsOrderAndBatchDetailsAsPlannedNoJobs() {
        whenHttp(restitoProvider.stubServer()).match(
                        get("/irs/orders/ebb79c45-7bba-4169-bf17-3e719989ab51/batches/ebb79c45-7bba-4169-bf17-3e719989ab52")
                )
                .then(
                        ok(),
                        header("Content-Type", "application/json"),
                        restitoProvider.jsonResponseFromFile("./stubs/irs/order/response_200_asplanned_nojobs.json")
                );
    }

    public void irsApiReturnsJobInRunningState() {
        whenHttp(restitoProvider.stubServer()).match(
                        get("/irs/jobs/ebb79c45-7bba-4169-bf17-3e719989ab54"),
                        withHeader(HttpHeaders.AUTHORIZATION)
                )
                .then(
                        ok(),
                        header("Content-Type", "application/json"),
                        restitoProvider.jsonResponseFromFile("./stubs/irs/get/jobs/id/running_job_response_200.json")
                );
    }

    public void irsJobDetailsApiFailed() {
        whenHttp(restitoProvider.stubServer()).match(
                        get("/irs/jobs/ebb79c45-7bba-4169-bf17-3e719989ab54"),
                        withHeader(HttpHeaders.AUTHORIZATION)
                )
                .then(
                        status(HttpStatus.INTERNAL_SERVER_ERROR_500),
                        header("Content-Type", "application/json")
                );
    }

    public void irsApiReturnsPolicies() {
        whenHttp(restitoProvider.stubServer()).match(
                Condition.get("/irs/policies")
        ).then(
                Action.status(HttpStatus.OK_200),
                Action.header("Content-Type", "application/json"),
                restitoProvider.jsonResponseFromFile("./stubs/irs/policies/response_200_get_policies.json")
        );
    }

    public void irsApiReturnsPoliciesBpdm() throws JsonProcessingException {
        whenHttp(restitoProvider.stubServer()).match(
                Condition.get("/irs/policies")
        ).then(
                Action.status(HttpStatus.OK_200),
                Action.header("Content-Type", "application/json"),
                restitoProvider.jsonResponseFromFile("./stubs/irs/policies/response_200_get_policies_bpdm.json")
        );
        policyStartUpConfig.registerDecentralRegistryPermissions();
    }

    public void irsApiReturnsPoliciesNotFound(String id) {
        whenHttp(restitoProvider.stubServer()).match(
                Condition.get("/irs/policies/" + id)
        ).then(
                Action.status(HttpStatus.NOT_FOUND_404),
                Action.header("Content-Type", "application/json"),
                restitoProvider.jsonResponseFromFile("./stubs/irs/policies/response_404_get_policyById.json")
        );
    }

    public void irsApiCreatesPolicy() {
        whenHttp(restitoProvider.stubServer()).match(
                Condition.post("/irs/policies")
        ).then(
                Action.status(HttpStatus.OK_200),
                Action.header("Content-Type", "application/json"),
                restitoProvider.jsonResponseFromFile("./stubs/irs/policies/response_200_createPolicy.json")
        );
    }

    public void irsApiCreatesPolicyBadRequest() {
        whenHttp(restitoProvider.stubServer()).match(
                Condition.post("/irs/policies")
        ).then(
                Action.status(HttpStatus.BAD_REQUEST_400),
                Action.header("Content-Type", "application/json"),
                restitoProvider.jsonResponseFromFile("./stubs/irs/policies/response_400_createPolicy.json")
        );
    }

    public void irsApiUpdatesPolicy() {
        whenHttp(restitoProvider.stubServer()).match(
                Condition.put("/irs/policies")
        ).then(
                Action.status(HttpStatus.OK_200),
                Action.header("Content-Type", "application/json"),
                Action.stringContent("{\"message\": \"Policy updated successfully.\"}")
        );
    }

    public void irsApiDeletesPolicy(String policyId) {
        whenHttp(restitoProvider.stubServer()).match(
                Condition.delete("/irs/policies/" + policyId)
        ).then(
                Action.status(HttpStatus.OK_200),
                Action.header("Content-Type", "application/json"),
                Action.stringContent("{\"message\": \"Policy deleted successfully.\"}")
        );
    }

    public void irsApiReturnsExpiredPolicy() {
        whenHttp(restitoProvider.stubServer()).match(
                Condition.get("/irs/policies")
        ).then(
                Action.status(HttpStatus.OK_200),
                Action.header("Content-Type", "application/json"),
                restitoProvider.jsonResponseFromFile("./stubs/irs/policies/response_200_get_policies_EXPIRED.json")
        );
    }

    public void irsApiReturnsMismatchingPolicy() {
        whenHttp(restitoProvider.stubServer()).match(
                Condition.get("/irs/policies")
        ).then(
                Action.status(HttpStatus.OK_200),
                Action.header("Content-Type", "application/json"),
                restitoProvider.jsonResponseFromFile("./stubs/irs/policies/response_200_get_policies_CONSTRAINTS_MISMATCHING.json")
        );
    }

    public static void sendCallback(String orderId, String batchId, String orderState, String batchState, int expectedStatus) {
        given()
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("orderId", orderId)
                .param("batchId", batchId)
                .param("orderState", orderState)
                .param("batchState", batchState)
                .get("/api/irs/order/callback")
                .then()
                .log().all()
                .statusCode(expectedStatus);
    }

}
