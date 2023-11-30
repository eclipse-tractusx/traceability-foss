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

package org.eclipse.tractusx.traceability.integration.qualitynotification.investigation;

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;

class InvestigationControllerFilterValuesIT extends IntegrationTestSpecification {

    @ParameterizedTest
    @MethodSource("enumFieldNamesTestProvider")
    void givenEnumTypeFields_whenCallDistinctFilterValues_thenProperResponse(
            String fieldName,
            Integer resultLimit,
            List<String> expectedResult
    ) throws JoseException {
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", resultLimit.toString())
                .get("/api/investigations/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(".", Matchers.containsInRelativeOrder(expectedResult.toArray()));
    }

    private static Stream<Arguments> enumFieldNamesTestProvider() {
        return Stream.of(
                Arguments.of("status", 10, List.of(
                        "CREATED",
                        "SENT",
                        "RECEIVED",
                        "ACKNOWLEDGED",
                        "ACCEPTED",
                        "DECLINED",
                        "CANCELED",
                        "CLOSED"
                )),
                Arguments.of("channel", 200, List.of(
                        "SENDER",
                        "RECEIVER"
                )),
                Arguments.of("severity", 10, List.of(
                        "MINOR",
                        "MAJOR",
                        "CRITICAL",
                        "LIFE_THREATENING"
                )),
                Arguments.of("status", 1, List.of(
                        "CREATED",
                        "SENT",
                        "RECEIVED",
                        "ACKNOWLEDGED",
                        "ACCEPTED",
                        "DECLINED",
                        "CANCELED",
                        "CLOSED"
                )),
                Arguments.of("channel", 1, List.of(
                        "SENDER",
                        "RECEIVER"
                )),
                Arguments.of("severity", 1, List.of(
                        "MINOR",
                        "MAJOR",
                        "CRITICAL",
                        "LIFE_THREATENING"
                ))
        );
    }
}
