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

package org.eclipse.tractusx.traceability.test.tooling.rest;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.eclipse.tractusx.traceability.test.tooling.EnvVariablesResolver;
import org.eclipse.tractusx.traceability.test.tooling.TraceXEnvironmentEnum;
import org.eclipse.tractusx.traceability.test.tooling.rest.request.StartQualityNotificationRequest;
import org.eclipse.tractusx.traceability.test.tooling.rest.response.QualityNotificationIdResponse;
import org.eclipse.tractusx.traceability.test.tooling.rest.response.QualityNotificationResponse;

import java.time.Instant;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.test.tooling.TraceXEnvironmentEnum.TRACE_X_A;
import static org.eclipse.tractusx.traceability.test.tooling.TraceXEnvironmentEnum.TRACE_X_B;

public class RestProvider {
    private String host;

    private final Authentication authentication;

    public RestProvider() {
        host = null;
        authentication = new Authentication();
    }

    public void loginToEnvironment(TraceXEnvironmentEnum environment) {
        if (environment.equals(TRACE_X_A)) {
            host = EnvVariablesResolver.getTX_A_Host();
        } else if (environment.equals(TRACE_X_B)) {
            host = EnvVariablesResolver.getTX_B_Host();
        }
        System.out.println(host);
    }

    public QualityNotificationIdResponse createInvestigation(
            List<String> partIds,
            String description,
            Instant targetDate,
            String severity) {
        final StartQualityNotificationRequest requestBody = StartQualityNotificationRequest.builder()
                .partIds(partIds)
                .description(description)
                .targetDate(targetDate)
                .severity(severity)
                .build();

        return given().spec(getRequestSpecification())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/investigations")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .as(QualityNotificationIdResponse.class);
    }

    public void approveInvestigation(
            final Long notificationId) {

        given().spec(getRequestSpecification())
                .contentType(ContentType.JSON)
                .when()
                .post("api/investigations/{notificationId}/approve".replace(
                        "{notificationId}",
                        notificationId.toString()
                ))
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    public List<QualityNotificationResponse> getReceivedNotifications() {
        return given().spec(getRequestSpecification())
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/received")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .body()
                .jsonPath().getList("content", QualityNotificationResponse.class);
    }


    private RequestSpecification getRequestSpecification() {
        final String accessToken = authentication.obtainAccessToken();

        System.out.println(" token is empty = " + accessToken.isEmpty());

        final RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.addHeader("Authorization", "Bearer " + accessToken);
        builder.setBaseUri(host);

        return builder.build();
    }
}
