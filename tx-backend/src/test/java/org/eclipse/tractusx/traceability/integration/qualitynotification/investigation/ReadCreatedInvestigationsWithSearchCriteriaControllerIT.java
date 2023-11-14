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
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationNotificationsSupport;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationNotificationEntity;
import org.eclipse.tractusx.traceability.testdata.InvestigationTestDataFactory;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;

class ReadCreatedInvestigationsWithSearchCriteriaControllerIT extends IntegrationTestSpecification {

    @Autowired
    BpnSupport bpnSupport;
    @Autowired
    InvestigationNotificationsSupport investigationNotificationsSupport;

    @Test
    void givenFilterBySendToProvided_whenGetInvestigations_thenReturnCreatedInvestigationsFilteredBySendTo() throws JoseException {
        // given
        String filterString = "sendTo,EQUAL,BPNL000000000001";
        String testBpn = bpnSupport.testBpn();

        InvestigationNotificationEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .param("filter", filterString)
                .param("filterOperator", "AND")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(2))
                .body("totalItems", Matchers.is(2))
                .body("content.sendTo", Matchers.hasItems("BPNL000000000001"));
        ;
    }

    @Test
    void givenFilterByCreatedDateProvided_whenGetInvestigations_thenReturnCreatedInvestigationsFilteredByCreatedDate() throws JoseException {
        // given
        String filterString = "createdDate,AT_LOCAL_DATE,2023-11-09";
        String testBpn = bpnSupport.testBpn();

        InvestigationNotificationEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .param("filter", filterString)
                .param("filterOperator", "AND")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(3))
                .body("totalItems", Matchers.is(3));
    }

    @Test
    void givenFilterBySendToNameProvided_whenGetInvestigations_thenReturnCreatedInvestigationsFilteredBySendToName() throws JoseException {
        // given
        String filterString = "sendToName,EQUAL,OEM2";
        String testBpn = bpnSupport.testBpn();

        InvestigationNotificationEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .param("filter", filterString)
                .param("filterOperator", "AND")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.sendToName", Matchers.hasItems("OEM2"));
        ;
        ;
    }

    @Test
    void givenFilterByStatusProvided_whenGetInvestigations_thenReturnCreatedInvestigationsFilteredByStatus() throws JoseException {
        // given
        String filterString = "status,EQUAL,ACCEPTED";
        String testBpn = bpnSupport.testBpn();

        InvestigationNotificationEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .param("filter", filterString)
                .param("filterOperator", "AND")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(2))
                .body("totalItems", Matchers.is(2))
                .body("content.status", Matchers.hasItems("ACCEPTED"));
        ;
        ;
    }

    @Test
    void givenFilterBySeverityProvided_whenGetInvestigations_thenReturnCreatedInvestigationsFilteredBySeverity() throws JoseException {
        // given
        String filterString = "severity,EQUAL,3";
        String testBpn = bpnSupport.testBpn();

        InvestigationNotificationEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .param("filter", filterString)
                .param("filterOperator", "AND")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.severity", Matchers.hasItems("LIFE-THREATENING"));
        ;
        ;
    }

    @Test
    void givenFilterByCreatedByProvided_whenGetInvestigations_thenReturnCreatedInvestigationsFilteredByCreatedBy() throws JoseException {
        // given
        String filterString = "createdBy,EQUAL,BPNL00000000000A";
        String testBpn = bpnSupport.testBpn();

        InvestigationNotificationEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .param("filter", filterString)
                .param("filterOperator", "AND")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(4))
                .body("totalItems", Matchers.is(4))
                .body("content.createdBy", Matchers.hasItems("BPNL00000000000A"));
        ;
        ;
    }

    @Test
    void givenFilterByDescriptionProvided_whenGetInvestigations_thenReturnCreatedInvestigationsFilteredByDescription() throws JoseException {
        // given
        String filterString = "description,STARTS_WITH,First";
        String testBpn = bpnSupport.testBpn();

        InvestigationNotificationEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .param("filter", filterString)
                .param("filterOperator", "AND")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.description", Matchers.hasItems("First Investigation on Asset1"));
        ;
        ;
    }

    @Test
    void givenFilterByDescriptionAndSendToProvided_whenGetInvestigations_thenReturnCreatedInvestigationsFilteredByDescriptionAndSendTo() throws JoseException {
        // given
        String filterString1 = "description,STARTS_WITH,First";
        String filterString2 = "sendTo,EQUAL,BPNL000000000001";
        String testBpn = bpnSupport.testBpn();

        InvestigationNotificationEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .param("filter", filterString1)
                .param("filter", filterString2)
                .param("filterOperator", "AND")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(1))
                .body("totalItems", Matchers.is(1))
                .body("content.sendTo", Matchers.hasItems("BPNL000000000001"))
                .body("content.description", Matchers.hasItems("First Investigation on Asset1"));
    }

    @Test
    void givenFilterBySendToNameOrSendToProvided_whenGetInvestigations_thenReturnCreatedInvestigationsFilteredBySendToNameOrSendTo() throws JoseException {
        // given
        String filterString1 = "sendToName,EQUAL,OEM2";
        String filterString2 = "sendTo,EQUAL,BPNL000000000001";
        String testBpn = bpnSupport.testBpn();

        InvestigationNotificationEntity[] investigationNotificationEntities = InvestigationTestDataFactory.createInvestigationNotificationEntitiesTestData(testBpn);
        investigationNotificationsSupport.storedNotifications(investigationNotificationEntities);

        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .param("filter", filterString1)
                .param("filter", filterString2)
                .param("filterOperator", "OR")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations/created")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(3))
                .body("totalItems", Matchers.is(3))
                .body("content.sendTo", Matchers.hasItems("BPNL000000000001"))
                .body("content.sendToName", Matchers.hasItems("OEM2"));
    }
}
