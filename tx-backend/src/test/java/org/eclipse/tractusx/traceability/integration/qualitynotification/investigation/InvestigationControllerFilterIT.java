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
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;

class InvestigationControllerFilterIT extends IntegrationTestSpecification {

    @Autowired
    InvestigationNotificationsSupport investigationNotificationSupport;

    @Test
    void givenInvestigations_whenProvideNoFilter_thenReturnAll() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(14))
                .body("content", Matchers.hasSize(10));
    }

    @Test
    void givenInvestigations_whenProvideBpnFilter_thenReturnExpectedResult() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        String filter = "?filter=bpn,STARTS_WITH,BPNL00000002OTHER,OR";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations" + filter)
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(6))
                .body("content", Matchers.hasSize(6));
    }

    @Test
    void givenInvestigations_whenProvideBpnFilterAnd_thenReturnExpectedResult() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        String filter = "?filter=bpn,STARTS_WITH,BPNL00000001OWN,AND&filter=createdDate,AT_LOCAL_DATE,2023-10-10,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations" + filter)
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(2))
                .body("content", Matchers.hasSize(2));
    }

    @Test
    void givenAlerts_whenProvideDateRangeFilters_thenReturnExpectedResult() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        String filter = "?filter=createdDate,AFTER_LOCAL_DATE,2023-10-09,AND&filter=createdDate,BEFORE_LOCAL_DATE,2023-10-11,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations" + filter)
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(8))
                .body("content", Matchers.hasSize(8));
    }
}
