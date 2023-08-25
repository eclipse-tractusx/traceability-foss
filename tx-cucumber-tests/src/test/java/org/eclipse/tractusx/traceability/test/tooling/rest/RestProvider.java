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

import assets.response.AssetAsBuiltResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import org.apache.http.HttpStatus;
import org.awaitility.Duration;
import org.eclipse.tractusx.traceability.test.tooling.EnvVariablesResolver;
import org.eclipse.tractusx.traceability.test.tooling.TraceXEnvironmentEnum;
import org.eclipse.tractusx.traceability.test.tooling.rest.request.StartQualityNotificationRequest;
import org.eclipse.tractusx.traceability.test.tooling.rest.request.UpdateQualityNotificationRequest;
import org.eclipse.tractusx.traceability.test.tooling.rest.request.UpdateQualityNotificationStatusRequest;
import org.eclipse.tractusx.traceability.test.tooling.rest.response.QualityNotificationIdResponse;
import org.eclipse.tractusx.traceability.test.tooling.rest.response.QualityNotificationResponse;
import org.hamcrest.Matchers;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.DeserializationFeature.READ_ENUMS_USING_TO_STRING;
import static com.fasterxml.jackson.databind.DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.eclipse.tractusx.traceability.test.tooling.TraceXEnvironmentEnum.TRACE_X_A;
import static org.eclipse.tractusx.traceability.test.tooling.TraceXEnvironmentEnum.TRACE_X_B;

public class RestProvider {
    private String host;
    @Getter
    private TraceXEnvironmentEnum currentEnv;

    private final Authentication authentication;

    public RestProvider() {
        host = null;
        authentication = new Authentication();

        RestAssured.config = RestAssuredConfig.config().objectMapperConfig(new ObjectMapperConfig().jackson2ObjectMapperFactory(
                (type, s) -> new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .registerModule(new Jdk8Module())
                        .enable(READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)
                        .enable(READ_ENUMS_USING_TO_STRING)
                        .disable(FAIL_ON_IGNORED_PROPERTIES)
                        .disable(FAIL_ON_UNKNOWN_PROPERTIES)
                        .disable(WRITE_DATES_AS_TIMESTAMPS)
        ));
    }

    public void loginToEnvironment(TraceXEnvironmentEnum environment) {
        if (environment.equals(TRACE_X_A)) {
            host = EnvVariablesResolver.getTX_A_Host();
            currentEnv = TRACE_X_A;
        } else if (environment.equals(TRACE_X_B)) {
            host = EnvVariablesResolver.getTX_B_Host();
            currentEnv = TRACE_X_B;
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
        return given().log().body()
                .spec(getRequestSpecification())
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

    public void cancelInvestigation(
            final Long notificationId) {

        given().spec(getRequestSpecification())
                .contentType(ContentType.JSON)
                .when()
                .post("api/investigations/{notificationId}/cancel".replace(
                        "{notificationId}",
                        notificationId.toString()
                ))
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    public void closeInvestigation(final Long notificationId) {

        given().spec(getRequestSpecification())
                .contentType(ContentType.JSON)
                .when()
                .body("{\"reason\": \"stringstringstr\"}")
                .post("api/investigations/{notificationId}/close".replace(
                        "{notificationId}",
                        notificationId.toString()
                ))
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    public void updateInvestigation(final Long notificationId, UpdateQualityNotificationStatusRequest status, String reason) {
        UpdateQualityNotificationRequest requestBody = UpdateQualityNotificationRequest.builder()
                .status(status)
                .reason(reason)
                .build();


        given().spec(getRequestSpecification())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("api/investigations/{notificationId}/update".replace(
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

    public QualityNotificationResponse getInvestigation(Long investigationId) {
        return await()
                .atMost(Duration.TWO_MINUTES)
                .pollInterval(1, TimeUnit.SECONDS)
                .until(() -> given().spec(getRequestSpecification())
                        .contentType(ContentType.JSON)
                        .when()
                        .get("/api/investigations/" + investigationId)
                        .then()
                        .statusCode(HttpStatus.SC_OK)
                        .extract()
                        .body().as(QualityNotificationResponse.class), Matchers.notNullValue()
                );
    }


    private RequestSpecification getRequestSpecification() {
        final String accessToken = authentication.obtainAccessToken();

        final RequestSpecBuilder builder = new RequestSpecBuilder();
        builder.addHeader("Authorization", "Bearer " + accessToken);
        builder.setBaseUri(host);

        return builder.build();
    }

    public List<AssetAsBuiltResponse> getAssets(String ownerFilter) {
        return given().spec(getRequestSpecification())
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets?owner=" + ownerFilter + "&page=0&size=50")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .body()
                .jsonPath()
                .getList("pageResult.content", AssetAsBuiltResponse.class);
    }
}
