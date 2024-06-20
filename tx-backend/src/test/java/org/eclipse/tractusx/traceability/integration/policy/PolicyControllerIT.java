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
package org.eclipse.tractusx.traceability.integration.policy;

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.IrsApiSupport;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import policies.request.Payload;
import policies.request.RegisterPolicyRequest;
import policies.request.UpdatePolicyRequest;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

class PolicyControllerIT extends IntegrationTestSpecification {
    @Autowired
    IrsApiSupport irsApiSupport;

    @Test
    void shouldReturnGetPolicies() throws JoseException {
        //GIVEN
        irsApiSupport.irsApiReturnsPolicies();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/policies")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .log().all();

    }

    @Test
    void shouldReturnGetPolicyById() throws JoseException {
        // GIVEN
        String policyId = "default-policy";
        irsApiSupport.irsApiReturnsPolicies();

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/policies/" + policyId)
                .then()
                .statusCode(200)
                .log().all();
    }

    @Test
    void shouldReturnNotFoundGetPolicyById() throws JoseException {
        // GIVEN
        String policyId = "default-policy-not-exist";
        irsApiSupport.irsApiReturnsPoliciesNotFound(policyId);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .get("/api/policies/" + policyId)
                .then()
                .statusCode(404)
                .body(containsString("Policy with id: default-policy-not-exist not found."))
                .log().all();
    }

    @Test
    void shouldCreatePolicy() throws JoseException {
        // GIVEN
        irsApiSupport.irsApiCreatesPolicy();
        Payload payload = Payload.builder().build();
        RegisterPolicyRequest request = new RegisterPolicyRequest(Instant.MAX, "abc", payload);

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/policies")
                .then()
                .statusCode(200)
                .log().all();
    }


    @Test
    void shouldThorwBadRequestCreatePolicy() throws JoseException {
        // GIVEN
        irsApiSupport.irsApiCreatesPolicyBadRequest();

        // Sample data
        List<String> businessPartnerNumbers = Arrays.asList("BPN-123", "BPN-456");
        List<String> policyIds = Arrays.asList("POLICY-789", "POLICY-101");
        Instant validUntil = Instant.parse("2025-12-31T23:59:59.00Z");

        // WRONG REQUEST OBJECT
        UpdatePolicyRequest request = new UpdatePolicyRequest(businessPartnerNumbers, policyIds, validUntil);
        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/policies")
                .then()
                .statusCode(400)
                .body(containsString("Policy with id 'default-policy3342' already exists!"))
                .log().all();
    }

    @Test
    void shouldUpdatePolicy() throws JoseException {
        // GIVEN
        irsApiSupport.irsApiUpdatesPolicy();

        UpdatePolicyRequest request = new UpdatePolicyRequest(List.of("abc"), List.of("abc"), Instant.MAX);

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/api/policies")
                .then()
                .statusCode(200)
                .log().all();
    }

    @Test
    void shouldDeletePolicy() throws JoseException {
        // GIVEN
        String policyId = "policy1";
        irsApiSupport.irsApiDeletesPolicy(policyId);

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .delete("/api/policies/" + policyId)
                .then()
                .statusCode(200)
                .log().all();
    }
}
