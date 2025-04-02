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

import common.FilterAttribute;
import common.FilterValue;
import io.restassured.http.ContentType;
import notification.request.NotificationFilter;
import notification.request.NotificationRequest;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy;
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
                .body(NotificationRequest.builder().page(0).size(10).sort(Collections.emptyList()).notificationFilter(NotificationFilter.builder().build()).build())
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

        NotificationFilter filter = NotificationFilter.builder()
                .bpn(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("BPNL00000002OTHER").strategy(SearchCriteriaStrategy.STARTS_WITH.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).sort(Collections.emptyList()).notificationFilter(filter).build())
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

        NotificationFilter filter = NotificationFilter.builder()
                .bpn(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("BPNL00000001OWN").strategy(SearchCriteriaStrategy.STARTS_WITH.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .createdDate(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("2023-10-10").strategy(SearchCriteriaStrategy.AT_LOCAL_DATE.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).sort(Collections.emptyList()).notificationFilter(filter).build())
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

        NotificationFilter filter = NotificationFilter.builder()
                .bpn(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("BPNL00000001OWN").strategy(SearchCriteriaStrategy.STARTS_WITH.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        NotificationRequest notificationRequest = NotificationRequest.builder()
                .page(0)
                .size(10)
                .sort(Collections.emptyList())
                .notificationFilter(filter).build();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(notificationRequest)
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

        NotificationFilter filter = NotificationFilter.builder()
                .createdDate(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("2023-10-1111111").strategy(SearchCriteriaStrategy.AT_LOCAL_DATE.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).sort(Collections.emptyList()).notificationFilter(filter).build())
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(400);
    }

    @Test
    void givenInvestigations_whenSendToFilter_thenExpectedResult() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();

        NotificationFilter filter = NotificationFilter.builder()
                .sendTo(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("B").strategy(SearchCriteriaStrategy.STARTS_WITH.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).sort(Collections.emptyList()).notificationFilter(filter).build())
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

        NotificationFilter filter = NotificationFilter.builder()
                .type(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("INVESTIGATION").strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).sort(Collections.emptyList()).notificationFilter(filter).build())
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
                .body(NotificationRequest.builder().page(0).size(10).sort(List.of(sortString)).notificationFilter(NotificationFilter.builder().build()).build())
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
                .body(NotificationRequest.builder().page(0).size(10).sort(List.of(sortString)).notificationFilter(NotificationFilter.builder().build()).build())
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
        String sortString = "sendTo,ASC";

        NotificationFilter filter = NotificationFilter.builder()
                .sendTo(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("B").strategy(SearchCriteriaStrategy.STARTS_WITH.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).sort(List.of(sortString)).notificationFilter(filter).build())
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

        NotificationFilter filter = NotificationFilter.builder()
                .severity(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("CRITICAL").strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).sort(List.of()).notificationFilter(filter).build())
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

        NotificationFilter filter = NotificationFilter.builder()
                .createdBy(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("BPNL00000001O").strategy(SearchCriteriaStrategy.STARTS_WITH.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).sort(List.of()).notificationFilter(filter).build())
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

        NotificationFilter filter = NotificationFilter.builder()
                .createdByName(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("Car").strategy(SearchCriteriaStrategy.STARTS_WITH.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).sort(List.of()).notificationFilter(filter).build())
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

        NotificationFilter filter = NotificationFilter.builder()
                .createdDate(FilterAttribute.builder()
                        .value(List.of(
                                FilterValue.builder().value("2023-10-09").strategy(SearchCriteriaStrategy.AFTER_LOCAL_DATE.name()).build(),
                                FilterValue.builder().value("2023-10-11").strategy(SearchCriteriaStrategy.BEFORE_LOCAL_DATE.name()).build()
                        ))
                        .operator(SearchCriteriaOperator.AND.name())
                        .build())
                .build();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).sort(List.of()).notificationFilter(filter).build())
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

        NotificationFilter filter = NotificationFilter.builder()
                .createdDate(FilterAttribute.builder()
                        .value(List.of(
                                FilterValue.builder().value("2023-10-12").strategy(SearchCriteriaStrategy.AFTER_LOCAL_DATE.name()).build(),
                                FilterValue.builder().value("2023-10-08").strategy(SearchCriteriaStrategy.BEFORE_LOCAL_DATE.name()).build()
                        ))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().page(0).size(10).sort(List.of()).notificationFilter(filter).build())
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

        NotificationFilter filter = NotificationFilter.builder()
                .assetId(FilterAttribute.builder()
                        .value(List.of(FilterValue.builder().value("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                        .operator(SearchCriteriaOperator.OR.name())
                        .build())
                .build();
        // When
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .body(NotificationRequest.builder().notificationFilter(filter).build())
                .contentType(ContentType.JSON)
                .when()
                .post("/api/notifications/filter")
                .then()
                .statusCode(200)
                .assertThat()
                .body("totalItems", equalTo(3));
    }
}
