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
package org.eclipse.tractusx.traceability.integration.actuator;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalManagementPort;
import org.springframework.test.web.servlet.MockMvc;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;


class ActuatorIT extends IntegrationTestSpecification {


    @Autowired
    private MockMvc mockMvc;

    @LocalManagementPort
    private int managementPort;

    private RequestSpecification requestSpecification;

    @BeforeEach
    void setup() {
        requestSpecification = new RequestSpecBuilder()
                .setPort(managementPort)
                .build();
    }

    @Test
    public void shouldRetrieveActuatorHealthDataWithoutAuthentication() {
        given(requestSpecification)
                .when().get("/actuator/health")
                .then().statusCode(503)
                .body("status", equalTo("OUT_OF_SERVICE"))
                .body("groups", containsInAnyOrder("liveness", "readiness"));
        Assert.assertNotNull(mockMvc);
    }

    @Test
    public void shouldRetrieveActuatorHealthLivenessDataWithoutAuthentication() {
        given(requestSpecification)
                .when().get("/actuator/health/liveness")
                .then().statusCode(200)
                .body("status", equalTo("UP"));
    }

    @Test
    public void shouldRetrieveActuatorHealthReadinessDataWithoutAuthentication() {
        given(requestSpecification)
                .when().get("/actuator/health/readiness")
                .then().statusCode(200)
                .body("status", equalTo("UP"));
    }


}
