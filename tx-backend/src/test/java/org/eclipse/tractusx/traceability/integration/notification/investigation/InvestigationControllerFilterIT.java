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
import notification.response.NotificationResponse;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.request.OwnPageable;
import org.eclipse.tractusx.traceability.common.request.PageableFilterRequest;
import org.eclipse.tractusx.traceability.common.request.SearchCriteriaRequestParam;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AlertsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationNotificationsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.NotificationApiSupport;
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

class InvestigationControllerFilterIT extends IntegrationTestSpecification {

    @Autowired
    InvestigationNotificationsSupport investigationNotificationSupport;
    @Autowired
    AlertsSupport alertsSupport;

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    NotificationApiSupport notificationApiSupport;

    @Autowired
    JpaAssetAsBuiltRepository jpaAssetAsBuiltRepository;

    @Autowired
    InvestigationsSupport investigationsSupport;

    @Test
    void givenInvestigations_whenProvideNoFilter_thenReturnAll() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, Collections.emptyList()), new SearchCriteriaRequestParam(List.of())))
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
    void givenInvestigations_whenProvideBpnFilter_thenReturnExpectedResult() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        String filter = "bpn,STARTS_WITH,BPNL00000002OTHER,OR";

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
                .body("totalItems", Matchers.is(6))
                .body("content", Matchers.hasSize(6));
    }

    @Test
    void givenInvestigations_whenProvideBpnFilterAnd_thenReturnExpectedResult() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
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
                .body("totalItems", Matchers.is(2))
                .body("content", Matchers.hasSize(2));
    }

    @Test
    void givenAlerts_whenProvideTooManyFilters_thenReturnError() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();

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
    void givenInvestigations_whenInvalidLocalDate_thenReturnBadRequest() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
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
    void givenInvestigations_whenTargetDateAtLocalDate_thenExpectedResult() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
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
    void givenInvestigations_whenSendToFilter_thenExpectedResult() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        String filter = "sendTo,STARTS_WITH,B,AND";

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
                .body("totalItems", Matchers.is(8))
                .body("content", Matchers.hasSize(8));
    }

    @Test
    void givenInvestigationsAndAlerts_whenTypeFilter_thenExpectedResult() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        alertsSupport.defaultReceivedAlertStored();
        String filter = "type,EQUAL,INVESTIGATION,AND";

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
                .body("totalItems", Matchers.is(14))
                .body("content", Matchers.hasSize(10));
    }

    @Test
    void givenInvestigationsAndAlerts_whenSortByTypeAsc_thenExpectedResult() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        alertsSupport.defaultReceivedAlertStored();
        String sortString = "type,ASC";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, List.of(sortString)), new SearchCriteriaRequestParam(List.of())))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(15))
                .body("content", Matchers.hasSize(10))
                .body("content.type", Matchers.containsInRelativeOrder(
                        "ALERT",
                        "INVESTIGATION",
                        "INVESTIGATION",
                        "INVESTIGATION",
                        "INVESTIGATION",
                        "INVESTIGATION",
                        "INVESTIGATION",
                        "INVESTIGATION",
                        "INVESTIGATION"));
    }

    @Test
    void givenInvestigationsAndAlerts_whenSortByTypeDesc_thenExpectedResult() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        alertsSupport.defaultReceivedAlertStored();
        String sortString = "type,DESC";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, List.of(sortString)), new SearchCriteriaRequestParam(List.of())))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(15))
                .body("content", Matchers.hasSize(10))
                .body("content.type", Matchers.containsInRelativeOrder(
                        "INVESTIGATION",
                        "INVESTIGATION",
                        "INVESTIGATION",
                        "INVESTIGATION",
                        "INVESTIGATION",
                        "INVESTIGATION",
                        "INVESTIGATION",
                        "INVESTIGATION",
                        "INVESTIGATION"));
    }

    @Test
    void givenInvestigations_whenSendToFilterAndSort_thenExpectedResult() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        String filter = "sendTo,STARTS_WITH,B,AND";
        String sortString = "sendTo,ASC";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, List.of(sortString)), new SearchCriteriaRequestParam(List.of(filter))))
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
    void givenInvestigations_whenProvideFilterWithSeverityCritical_thenReturnAllCritical() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        String filter = "severity,EQUAL,CRITICAL,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, List.of()), new SearchCriteriaRequestParam(List.of(filter))))
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
    void givenInvestigations_whenProvideFilterCreatedBy_thenReturnAllCritical() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        String filter = "createdBy,STARTS_WITH,BPNL00000001O,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, List.of()), new SearchCriteriaRequestParam(List.of(filter))))
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
    void givenInvestigations_whenProvideFilterCreatedByName_thenReturnAllCritical() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        String filter = "createdByName,STARTS_WITH,Car,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, List.of()), new SearchCriteriaRequestParam(List.of(filter))))
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
    void givenAlerts_whenProvideDateRangeFilters_thenReturnExpectedResult() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        String filter1 = "createdDate,AFTER_LOCAL_DATE,2023-10-09,AND";
        String filter2 = "createdDate,BEFORE_LOCAL_DATE,2023-10-11,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, List.of()), new SearchCriteriaRequestParam(List.of(filter1, filter2))))
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
        investigationNotificationSupport.defaultInvestigationsStored();
        String filter1 = "createdDate,AFTER_LOCAL_DATE,2023-10-12,AND";
        String filter2 = "createdDate,BEFORE_LOCAL_DATE,2023-10-08,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, List.of()), new SearchCriteriaRequestParam(List.of(filter1, filter2))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(6))
                .body("content", Matchers.hasSize(6));
    }

    @Test
    void givenNonExistingFilterField_whenGetInvestigations_thenBadRequest() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        String filter = "nonExistingField,AFTER_LOCAL_DATE,2023-10-12,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(new PageableFilterRequest(new OwnPageable(0, 10, List.of()), new SearchCriteriaRequestParam(List.of(filter))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(400);
    }

    @Test
    void givenInvestigations_whenGetInvestigationsByAssetId_thenReturnOnlyRelatedInvestigations() throws JoseException {
        // Given
        assetsSupport.defaultAssetsStored();
        AssetAsBuiltEntity assetAsBuilt = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        AssetAsBuiltEntity assetAsBuilt2 = jpaAssetAsBuiltRepository.findById("urn:uuid:7fa65f10-9dc1-49fe-818a-09c7313a4562").orElseThrow();
        investigationsSupport.storeInvestigationWithStatusAndAssets(CREATED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt2));
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACCEPTED, List.of(assetAsBuilt2));
        investigationsSupport.storeInvestigationWithStatusAndAssets(DECLINED, List.of(assetAsBuilt2));
        investigationsSupport.storeInvestigationWithStatusAndAssets(CANCELED, List.of(assetAsBuilt2));
        investigationsSupport.storeInvestigationWithStatusAndAssets(CLOSED, List.of(assetAsBuilt2));

        final String filter = "assetId,EQUAL,urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb,AND";


        // When
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))

                .body(new PageableFilterRequest(null, new SearchCriteriaRequestParam(List.of(filter))))
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .assertThat()
                .body("totalItems", equalTo(3));
    }
}
