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

package org.eclipse.tractusx.traceability.integration.qualitynotification.alert;

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AlertNotificationsSupport;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;


class AlertControllerFilterIT extends IntegrationTestSpecification {
    @Autowired
    AlertNotificationsSupport alertNotificationsSupport;

    @Test
    void givenAlerts_whenProvideNoFilter_thenReturnAll() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(14))
                .body("content", Matchers.hasSize(10));
    }

    @Test
    void givenAlerts_whenProvideBpnFilter_thenReturnExpectedResult() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();
        String filter = "?filter=bpn,STARTS_WITH,BPNL00000001OWN,OR";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts" + filter)
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(8))
                .body("totalItems", Matchers.is(8));
    }

    @Test
    void givenAlerts_whenProvideBpnFilterAnd_thenReturnExpectedResult() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();
        String filter = "?filter=bpn,STARTS_WITH,BPNL00000001OWN,AND&filter=createdDate,AT_LOCAL_DATE,2023-10-10,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts" + filter)
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(2))
                .body("totalItems", Matchers.is(2));
    }

    @Test
    void givenAlerts_whenProvideDateRangeFilters_thenReturnExpectedResult() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();
        String filter = "?filter=createdDate,AFTER_LOCAL_DATE,2023-10-09,AND&filter=createdDate,BEFORE_LOCAL_DATE,2023-10-11,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts" + filter)
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(8))
                .body("content", Matchers.hasSize(8));
    }

    @Test
    void givenAlerts_whenProvideDateRangeFiltersXAnd_thenReturnExpectedResult() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();
        String filter = "?filter=createdDate,AFTER_LOCAL_DATE,2023-10-13,AND&filter=createdDate,BEFORE_LOCAL_DATE,2023-10-07,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts" + filter)
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(2))
                .body("content", Matchers.hasSize(2));
    }

    @Test
    void givenNonExistingFilterField_whenGetAlerts_thenBadRequest() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();
        String filter = "?filter=nonExistingField,AFTER_LOCAL_DATE,2023-10-13,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts" + filter)
                .then()
                .statusCode(400);
    }


    @Test
    void givenInvestigations_whenInvalidLocalDate_thenReturnBadRequest() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();
        String filter = "?filter=createdDate,AT_LOCAL_DATE,2023-10-1111111,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts" + filter)
                .then()
                .statusCode(400);
    }

    @Test
    void givenInvestigations_whenTargetDateAtLocalDate_thenExpectedResult() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();
        String filter = "?filter=targetDate,AT_LOCAL_DATE,2023-11-10,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/alerts" + filter)
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(2))
                .body("content", Matchers.hasSize(2));
    }

    @Test
    void givenInvestigations_whenProvideFilterWithSeverityCritical_thenReturnAllCritical() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .param("filter", "severity,EQUAL,CRITICAL,AND")
                .get("/api/alerts")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(2))
                .body("content", Matchers.hasSize(2));
    }

    @Test
    void givenInvestigations_whenProvideFilterCreatedBy_thenReturnAllCritical() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .param("filter", "createdBy,STARTS_WITH,BPNL00000001O,AND")
                .get("/api/alerts")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(4))
                .body("content", Matchers.hasSize(4));
    }

    @Test
    void givenInvestigations_whenProvideFilterCreatedByName_thenReturnAllCritical() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .param("filter", "createdByName,STARTS_WITH,Car,AND")
                .get("/api/alerts")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(4))
                .body("content", Matchers.hasSize(4));
    }
}
