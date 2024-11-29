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

package org.eclipse.tractusx.traceability.integration.notification.alert;

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.PageableFilterRequest;
import org.eclipse.tractusx.traceability.common.request.SearchCriteriaRequestParam;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AlertNotificationsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.AlertsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.IrsApiSupport;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.ACCEPTED;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.ACKNOWLEDGED;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.CANCELED;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.CLOSED;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.CREATED;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.DECLINED;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.RECEIVED;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.SENT;
import static org.hamcrest.Matchers.equalTo;


class AlertControllerFilterIT extends IntegrationTestSpecification {
    @Autowired
    AlertNotificationsSupport alertNotificationsSupport;

    @Autowired
    AlertsSupport alertsSupport;

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    IrsApiSupport irsApiSupport;

    @Autowired
    JpaAssetAsBuiltRepository jpaAssetAsBuiltRepository;

    @Test
    void givenAlerts_whenProvideNoFilter_thenReturnAll() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(Collections.emptyList())))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
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
        String filter = "bpn,STARTS_WITH,BPNL00000001OWN,OR";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of(filter))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("content", Matchers.hasSize(8))
                .body("totalItems", Matchers.is(8));
    }

    @Test
    void givenAlerts_whenProvideTooManyFilters_thenReturnError() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();

        OwnPageable ownPageable = new OwnPageable(0, 10, Collections.emptyList());

        ArrayList<String> filter = new ArrayList<>();
        filter.add("bpn,STARTS_WITH,BPNL00000001OWN,OR");
        PageableFilterRequest pageableFilterRequest = new PageableFilterRequest(ownPageable, new SearchCriteriaRequestParam(filter));

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(pageableFilterRequest)
                .contentType(ContentType.JSON)
                .when()
                .log()
                .uri()
                .post("/api/notifications/filter")
                .then()
                .log().all()
                .statusCode(200)
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(8));
    }

    @Test
    void givenAlerts_whenProvideBpnFilterAnd_thenReturnExpectedResult() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();
        String filter1 = "bpn,STARTS_WITH,BPNL00000001OWN,AND";
        String filter2 = "createdDate,AT_LOCAL_DATE,2023-10-10,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of(filter1, filter2))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
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
        String filter1 = "createdDate,AFTER_LOCAL_DATE,2023-10-09,AND";
        String filter2 = "createdDate,BEFORE_LOCAL_DATE,2023-10-11,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of(filter1, filter2))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
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
        String filter1 = "createdDate,AFTER_LOCAL_DATE,2023-10-13,AND";
        String filter2 = "createdDate,BEFORE_LOCAL_DATE,2023-10-07,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of(filter1, filter2))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
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
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of(filter))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(400);
    }


    @Test
    void givenAlerts_whenInvalidLocalDate_thenReturnBadRequest() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();
        String filter = "createdDate,AT_LOCAL_DATE,2023-10-1111111,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of(filter))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(400);
    }

    @Test
    void givenAlerts_whenTargetDateAtLocalDate_thenExpectedResult() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();
        String filter = "targetDate,AT_LOCAL_DATE,2023-11-10,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of(filter))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(2))
                .body("content", Matchers.hasSize(2));
    }

    @Test
    void givenAlerts_whenProvideFilterWithSeverityCritical_thenReturnAllCritical() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of("severity,EQUAL,CRITICAL,AND"))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(2))
                .body("content", Matchers.hasSize(2));
    }

    @Test
    void givenAlerts_whenProvideFilterCreatedBy_thenReturnAllCritical() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of("createdBy,STARTS_WITH,BPNL00000001O,AND"))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(4))
                .body("content", Matchers.hasSize(4));
    }

    @Test
    void givenAlerts_whenProvideFilterCreatedByName_thenReturnAllCritical() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of("createdByName,STARTS_WITH,Car,AND"))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(4))
                .body("content", Matchers.hasSize(4));
    }

    @Test
    void givenAlerts_whenProvideFilterSendTo_thenReturnExpectedResults() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();
        final String filterString = "sendTo,STARTS_WITH,B,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of(filterString))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(3))
                .body("content", Matchers.hasSize(3));
    }

    @Test
    void givenAlerts_whenProvideFilterSendToSort_thenReturnExpectedResults() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();
        final String filterString = "sendTo,STARTS_WITH,B,AND";
        String sortString = "sendTo,ASC";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, List.of(sortString)), new SearchCriteriaRequestParam(List.of(filterString))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(3))
                .body("content", Matchers.hasSize(3))
                .body("content.sendTo", Matchers.containsInRelativeOrder("TESTBPN", "TESTBPN", "TESTBPN"));
    }

    @Test
    void givenAlerts_whenGetAlertsByAssetId_thenReturnOnlyRelatedAlerts() throws JoseException {
        // Given
        assetsSupport.defaultAssetsStored();
        AssetAsBuiltEntity assetAsBuilt = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        AssetAsBuiltEntity assetAsBuilt2 = jpaAssetAsBuiltRepository.findById("urn:uuid:7fa65f10-9dc1-49fe-818a-09c7313a4562").orElseThrow();
        alertsSupport.storeAlertWithStatusAndAssets(CREATED, List.of(assetAsBuilt));
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of());
        alertsSupport.storeAlertWithStatusAndAssets(RECEIVED, List.of());
        alertsSupport.storeAlertWithStatusAndAssets(ACKNOWLEDGED, List.of());
        alertsSupport.storeAlertWithStatusAndAssets(ACCEPTED, List.of());
        alertsSupport.storeAlertWithStatusAndAssets(DECLINED, List.of(assetAsBuilt2));
        alertsSupport.storeAlertWithStatusAndAssets(CANCELED, List.of());
        alertsSupport.storeAlertWithStatusAndAssets(CLOSED, List.of(assetAsBuilt));

        final String filter = "assetId,EQUAL,urn:uuid:7fa65f10-9dc1-49fe-818a-09c7313a4562,AND";

        // When
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of(filter))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenAlerts_whenProvideFilterLongerThan1100_thenReturnHttp400() throws JoseException {
        // given
        alertNotificationsSupport.defaultAlertsStored();

        // when/then
        SearchCriteriaRequestParam searchCriteriaRequestParam = new SearchCriteriaRequestParam(List.of("description,EQUAL," +"1".repeat(1101)+",AND" ));
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), searchCriteriaRequestParam))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .log().all()
                .statusCode(400)
                .body("message", equalTo("Filter string should not be longer than 1100 characters."));
    }

}
