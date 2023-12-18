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
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationNotificationsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationsSupport;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;

class InvestigationControllerFilterValuesIT extends IntegrationTestSpecification {

    @Autowired
    InvestigationsSupport investigationsSupport;

    @Autowired
    InvestigationNotificationsSupport investigationNotificationsSupport;

    @Test
    void givenDescriptionField_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        investigationNotificationsSupport.defaultInvestigationsStored();
        final String fieldName = "description";
        final Integer size = 200;

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", size)
                .get("/api/investigations/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(".", Matchers.containsInRelativeOrder(List.of("1", "11", "2", "22", "3", "33", "4", "44", "5", "55", "6", "7", "8").toArray()));
    }


    @Test
    void givenDescriptionFieldStartWith_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        investigationNotificationsSupport.defaultInvestigationsStored();
        final String fieldName = "description";
        final Integer size = 200;
        final String startWith = "1";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", size)
                .param("startWith", startWith)
                .get("/api/investigations/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(".", Matchers.containsInRelativeOrder(List.of("1", "11").toArray()));
    }

    @Test
    void givenDescriptionFieldStartWithAndReceiver_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        investigationNotificationsSupport.defaultInvestigationsStored();
        final String fieldName = "description";
        final Integer size = 200;
        final String startWith = "1";
        final String channel = "RECEIVER";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", size)
                .param("startWith", startWith)
                .param("channel", channel)
                .get("/api/investigations/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(".", Matchers.containsInRelativeOrder(List.of("11").toArray()));
    }

    @Test
    void givenDescriptionFieldStartWithAndSender_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        investigationNotificationsSupport.defaultInvestigationsStored();
        final String fieldName = "description";
        final Integer size = 200;
        final String startWith = "1";
        final String channel = "SENDER";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", size)
                .param("startWith", startWith)
                .param("channel", channel)
                .get("/api/investigations/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(".", Matchers.containsInRelativeOrder(List.of("1").toArray()));
    }

    @Test
    void givenBpnField_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        investigationNotificationsSupport.defaultInvestigationsStored();
        final String fieldName = "bpn";
        Integer size = 200;

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", size)
                .get("/api/investigations/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(".", Matchers.containsInRelativeOrder(List.of("BPNL00000001OWN", "BPNL00000002OTHER").toArray()));
    }

    @Test
    void givenBpnFieldStartWithCaseInsensitive1_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        investigationNotificationsSupport.defaultInvestigationsStored();
        final String fieldName = "bpn";
        final Integer size = 200;
        final String startWith = "bpnl";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", size)
                .param("startWith", startWith)
                .get("/api/investigations/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(".", Matchers.containsInRelativeOrder(List.of("BPNL00000001OWN", "BPNL00000002OTHER").toArray()));
    }

    @Test
    void givenBpnFieldStartWithCaseInsensitive2_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        investigationNotificationsSupport.defaultInvestigationsStored();
        final String fieldName = "bpn";
        final Integer size = 200;
        final String startWith = "bpNl";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", size)
                .param("startWith", startWith)
                .get("/api/investigations/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(".", Matchers.containsInRelativeOrder(List.of("BPNL00000001OWN", "BPNL00000002OTHER").toArray()));
    }

    @Test
    void givenCreatedDateField_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        investigationNotificationsSupport.defaultInvestigationsStored();
        final String fieldName = "createdDate";
        Integer size = 200;

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", size)
                .get("/api/investigations/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(".", Matchers.containsInRelativeOrder(List.of(
                        "2023-10-07 10:10:10",
                        "2023-10-08 10:10:10",
                        "2023-10-09 10:10:10",
                        "2023-10-10 10:10:10",
                        "2023-10-10 10:10:30",
                        "2023-10-11 10:10:10",
                        "2023-10-12 10:10:10",
                        "2023-10-13 10:10:10").toArray()));
    }

    @Test
    void givenCreatedDateFieldAndNoSize_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        investigationNotificationsSupport.defaultInvestigationsStored();
        final String fieldName = "createdDate";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .get("/api/investigations/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(".", Matchers.containsInRelativeOrder(List.of(
                        "2023-10-07 10:10:10",
                        "2023-10-08 10:10:10",
                        "2023-10-09 10:10:10",
                        "2023-10-10 10:10:10",
                        "2023-10-10 10:10:30",
                        "2023-10-11 10:10:10",
                        "2023-10-12 10:10:10",
                        "2023-10-13 10:10:10").toArray()));
    }

    @Test
    void givenCreatedByField_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        investigationNotificationsSupport.defaultInvestigationsStored();
        final String fieldName = "createdBy";
        Integer size = 200;

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", size)
                .get("/api/investigations/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(".", Matchers.containsInRelativeOrder(List.of("BPNL00000001OWN", "BPNL00000002OTHER").toArray()));
    }

    @Test
    void givenCreatedByFieldAndSender_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        investigationNotificationsSupport.defaultInvestigationsStored();
        final String fieldName = "createdBy";
        Integer size = 200;
        final String channel = "SENDER";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", size)
                .param("channel", channel)
                .get("/api/investigations/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(".", Matchers.containsInRelativeOrder(List.of("BPNL00000001OWN", "BPNL00000002OTHER").toArray()));
    }

    @Test
    void givenCreatedByFieldAndReceiver_whenCallDistinctFilterValues_thenProperResponse() throws JoseException {
        // given
        investigationNotificationsSupport.defaultInvestigationsStored();
        final String fieldName = "createdBy";
        Integer size = 200;
        final String channel = "RECEIVER";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .param("fieldName", fieldName)
                .param("size", size)
                .param("channel", channel)
                .get("/api/investigations/distinctFilterValues")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body(".", Matchers.containsInRelativeOrder(List.of("BPNL00000002OTHER").toArray()));
    }

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
