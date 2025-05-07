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

import assets.response.asbuilt.AssetAsBuiltResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import notification.request.EditNotificationRequest;
import notification.request.NotificationSeverityRequest;
import notification.request.StartNotificationRequest;
import notification.request.UpdateNotificationStatusRequest;
import notification.request.UpdateNotificationStatusTransitionRequest;
import notification.response.NotificationIdResponse;
import notification.response.NotificationResponse;
import org.apache.http.HttpStatus;
import org.awaitility.Durations;
import org.eclipse.tractusx.traceability.test.tooling.EnvVariablesResolver;
import org.eclipse.tractusx.traceability.test.tooling.NotificationTypeEnum;
import org.eclipse.tractusx.traceability.test.tooling.TraceXEnvironmentEnum;

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

    private Authentication authentication;

    public RestProvider() {
        host = null;

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
            authentication = Authentication.authenticationForTracexA();
            currentEnv = TRACE_X_A;
        } else if (environment.equals(TRACE_X_B)) {
            host = EnvVariablesResolver.getTX_B_Host();
            authentication = Authentication.authenticationForTracexB();
            currentEnv = TRACE_X_B;
        }
        System.out.println(host);
    }

    public NotificationIdResponse createNotification(
            List<String> partIds,
            String description,
            Instant targetDate,
            String severity,
            String receiverBpn,
            String title,
            NotificationTypeEnum notificationType) {
        final StartNotificationRequest requestBody = StartNotificationRequest.builder()
                .affectedPartIds(partIds)
                .description(description)
                .targetDate(targetDate)
                .severity(NotificationSeverityRequest.fromValue(severity))
                .type(notificationType.toRequest())
                .receiverBpn(receiverBpn)
                .title(title)
                .build();
        return given().log().body()
                .spec(getRequestSpecification())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/notifications")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .extract()
                .as(NotificationIdResponse.class);

    }

    public void editNotification(Long notificationId,
                                 List<String> partIds,
                                 String description,
                                 Instant targetDate,
                                 String severity,
                                 String title,
                                 String receiverBpn) {
        final EditNotificationRequest requestBody = EditNotificationRequest.builder()
                .affectedPartIds(partIds)
                .description(description)
                .targetDate(targetDate)
                .severity(NotificationSeverityRequest.fromValue(severity))
                .receiverBpn(receiverBpn)
                .title(title)
                .build();
        given().log().body()
                .spec(getRequestSpecification())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/api/notifications/" + notificationId + "/edit")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

    }

    public void approveNotification(final Long notificationId) {
        await()
                .atMost(Durations.FIVE_MINUTES)
                .pollInterval(10, TimeUnit.SECONDS)
                .ignoreExceptions()
                .until(() -> {
                    ValidatableResponse validatableResponse = given().spec(getRequestSpecification())
                            .contentType(ContentType.JSON)
                            .when()
                            .post("api/notifications/{notificationId}/approve".replace(
                                    "{notificationId}",
                                    notificationId.toString()
                            ))
                            .then();
                    try {
                        validatableResponse.statusCode(HttpStatus.SC_NO_CONTENT);
                        return true;
                    } catch (Exception e) {
                        System.out.println("Retry action");
                        return false;
                    }
                });

    }

    public void cancelNotification(final Long notificationId) {

        given().spec(getRequestSpecification())
                .contentType(ContentType.JSON)
                .when()
                .post("api/notifications/{notificationId}/cancel".replace(
                        "{notificationId}",
                        notificationId.toString()
                ))
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }

    public void closeNotification(final Long notificationId) {
        await()
                .atMost(Durations.FIVE_MINUTES)
                .pollInterval(10, TimeUnit.SECONDS)
                .ignoreExceptions()
                .until(() -> {
                    ValidatableResponse result = given().spec(getRequestSpecification())
                            .contentType(ContentType.JSON)
                            .when()
                            .body("{\"reason\": \"stringstringstr\"}")
                            .post("api/notifications/{notificationId}/close".replace(
                                    "{notificationId}",
                                    notificationId.toString()
                            ))
                            .then();
                    try {
                        result.statusCode(HttpStatus.SC_NO_CONTENT);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                });

    }

    public void updateNotification(final Long notificationId,
                                   UpdateNotificationStatusRequest status, String reason) {
        UpdateNotificationStatusTransitionRequest requestBody = UpdateNotificationStatusTransitionRequest.builder()
                .status(status)
                .reason(reason)
                .build();

        await()
                .atMost(Durations.FIVE_MINUTES)
                .pollInterval(10, TimeUnit.SECONDS)
                .ignoreExceptions()
                .until(() -> {
                    ValidatableResponse validatableResponse = given().spec(getRequestSpecification())
                            .contentType(ContentType.JSON)
                            .body(requestBody)
                            .when()
                            .post("api/notifications/{notificationId}/update".replace(
                                    "{notificationId}",
                                    notificationId.toString()
                            ))
                            .then()
                            .log().all();

                    try {
                        validatableResponse.statusCode(HttpStatus.SC_NO_CONTENT);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                });

    }

    public List<NotificationResponse> getReceivedNotifications() {
        return given().spec(getRequestSpecification())
                .contentType(ContentType.JSON)
                .when()
                .body("""
                        {
                          "page": 0,
                          "size": 1000,
                          "sort": ["created,desc"],
                          "notificationFilter": {
                            "channel": {
                              "value": [
                                {
                                  "value": "RECEIVER",
                                  "strategy": "OR"

                                }
                              ],
                              "operator": "EQUAL"
                            }
                          }
                        }
                        """)
                .post("/api/notifications/filter")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .body()
                .jsonPath().getList("content", NotificationResponse.class);
    }

    public NotificationResponse getNotification(Long investigationId) {
        return given().spec(getRequestSpecification())
                .contentType(ContentType.JSON)
                .when()
                .get("/api/notifications/" + investigationId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .log()
                .body()
                .extract()
                .body().as(NotificationResponse.class);
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
                .get("/api/assets/as-built?owner=" + ownerFilter + "&page=0&size=50")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .body()
                .jsonPath()
                .getList("pageResult.content", AssetAsBuiltResponse.class);
    }
}
