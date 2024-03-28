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

package org.eclipse.tractusx.traceability.integration.notification.investigation;

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationsSupport;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import notification.request.UpdateNotificationStatusRequest;

import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.SUPERVISOR;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ReceiverInvestigationsControllerIT extends IntegrationTestSpecification {

    @Autowired
    InvestigationsSupport investigationsSupport;

    @Test
    void shouldNotUpdateToAcknowledgedNonExistingInvestigation() throws JoseException {
        // given
        final long notExistingInvestigationId = 1234L;

        // when/then
        given()
                .contentType(ContentType.JSON)
                .body("""
                         {
                         "status" : "$status"
                         }
                        """.replace("$status", UpdateNotificationStatusRequest.ACKNOWLEDGED.name()))
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .when()
                .post("/api/notifications/{notExistingInvestigationId}/update", notExistingInvestigationId)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldNotUpdateToAcceptedNonExistingInvestigation() throws JoseException {
        // given
        final long notExistingInvestigationId = 1234L;

        // when
        given()
                .contentType(ContentType.JSON)
                .body("""
                         {
                         "status" : "$status",
                         "reason" : "some reason, why not"
                         }
                        """.replace("$status", UpdateNotificationStatusRequest.ACCEPTED.name()))
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .when()
                .post("/api/notifications/{notExistingInvestigationId}/update", notExistingInvestigationId)
                .then()
                .statusCode(404);
    }

    @Test
    void shouldNotUpdateToDeclinedNonExistingInvestigation() throws JoseException {
        // given
        final long notExistingInvestigationId = 1234L;

        // when
        given()
                .contentType(ContentType.JSON)
                .body("""
                         {
                         "status" : "$status",
                         "reason" : "some reason, why not"
                         }
                        """.replace("$status", UpdateNotificationStatusRequest.DECLINED.name()))
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .when()
                .post("/api/notifications/{notExistingInvestigationId}/update", notExistingInvestigationId)
                .then()
                .statusCode(404);
    }

    @ParameterizedTest
    @MethodSource("invalidRequest")
    void shouldNotUpdateWithInvalidRequest(final String request) throws JoseException {
        // given
        final long notExistingInvestigationId = 1234L;

        // when
        given()
                .contentType(ContentType.JSON)
                .body(request)
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .when()
                .post("/api/notifications/{notExistingInvestigationId}/update", Long.toString(notExistingInvestigationId))
                .then()
                .statusCode(400);
    }


    private static Stream<Arguments> invalidRequest() {
        return Stream.of(
                arguments("""
                         {
                         "status" : "$status",
                         "reason" : "No reason should be for acknowledging"
                         }
                        """.replace("$status", UpdateNotificationStatusRequest.ACKNOWLEDGED.name())),
                arguments("""
                         {
                         "status" : "$status",
                         "reason" : null
                         }
                        """.replace("$status", UpdateNotificationStatusRequest.ACCEPTED.name())),
                arguments("""
                         {
                         "status" : "$status",
                         "reason" : " "
                         }
                        """.replace("$status", UpdateNotificationStatusRequest.ACCEPTED.name())),
                arguments("""
                         {
                         "status" : "$status",
                         "reason" : null
                         }
                        """.replace("$status", UpdateNotificationStatusRequest.DECLINED.name())),
                arguments("""
                         {
                         "status" : "$status",
                         "reason" : " "
                         }
                        """.replace("$status", UpdateNotificationStatusRequest.DECLINED.name()))
        );
    }

}
