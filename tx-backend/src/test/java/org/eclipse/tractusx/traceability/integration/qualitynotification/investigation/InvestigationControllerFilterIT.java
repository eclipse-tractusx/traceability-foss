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
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationNotificationsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationsSupport;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity.ACCEPTED;
import static org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity.ACKNOWLEDGED;
import static org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity.CANCELED;
import static org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity.CLOSED;
import static org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity.CREATED;
import static org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity.DECLINED;
import static org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity.RECEIVED;
import static org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity.SENT;
import static org.hamcrest.Matchers.equalTo;

class InvestigationControllerFilterIT extends IntegrationTestSpecification {

    @Autowired
    InvestigationNotificationsSupport investigationNotificationSupport;

    @Autowired
    AssetsSupport assetsSupport;

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
    void givenInvestigations_whenInvalidLocalDate_thenReturnBadRequest() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        String filter = "?filter=createdDate,AT_LOCAL_DATE,2023-10-1111111,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations" + filter)
                .then()
                .statusCode(400);
    }

    @Test
    void givenInvestigations_whenTargetDateAtLocalDate_thenExpectedResult() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        String filter = "?filter=targetDate,AT_LOCAL_DATE,2023-11-10,AND";

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
    void givenInvestigations_whenSendToFilter_thenExpectedResult() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        String filterString = "sendTo,STARTS_WITH,B,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .param("filter", filterString)
                .get("/api/investigations")
                .then()
                .statusCode(200)
                .body("page", Matchers.is(0))
                .body("pageSize", Matchers.is(10))
                .body("totalItems", Matchers.is(8))
                .body("content", Matchers.hasSize(8));
    }

    @Test
    void givenInvestigations_whenSendToFilterAndSort_thenExpectedResult() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        String filterString = "sendTo,STARTS_WITH,B,AND";
        String sortString = "sendTo,ASC";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .param("filter", filterString)
                .param("sort", sortString)
                .get("/api/investigations")
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

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .param("filter", "severity,EQUAL,CRITICAL,AND")
                .get("/api/investigations")
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

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .param("filter", "createdBy,STARTS_WITH,BPNL00000001O,AND")
                .get("/api/investigations")
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

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .param("filter", "createdByName,STARTS_WITH,Car,AND")
                .get("/api/investigations")
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

    @Test
    void givenAlerts_whenProvideDateRangeFiltersXAnd_thenReturnExpectedResult() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        String filter = "?filter=createdDate,AFTER_LOCAL_DATE,2023-10-12,AND&filter=createdDate,BEFORE_LOCAL_DATE,2023-10-08,AND";

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
    void givenNonExistingFilterField_whenGetInvestigations_thenBadRequest() throws JoseException {
        // given
        investigationNotificationSupport.defaultInvestigationsStored();
        String filter = "?filter=nonExistingField,AFTER_LOCAL_DATE,2023-10-12,AND";

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "10")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/investigations" + filter)
                .then()
                .statusCode(400);
    }

    @Test
    void givenInvestigations_whenGetInvestigationsByAssetId_thenReturnOnlyRelatedInvestigations() throws JoseException {
        // Given
        assetsSupport.defaultAssetsStored();
        AssetAsBuiltEntity assetAsBuilt = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        AssetAsBuiltEntity assetAsBuilt2 = jpaAssetAsBuiltRepository.findById("urn:uuid:7fa65f10-9dc1-49fe-818a-09c7313a4562").orElseThrow();
        investigationsSupport.storeInvestigationWithStatusAndAssets(CREATED, List.of(assetAsBuilt), null);
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt), null);
        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt), null);
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt2), null);
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACCEPTED, List.of(assetAsBuilt2), null);
        investigationsSupport.storeInvestigationWithStatusAndAssets(DECLINED, List.of(assetAsBuilt2), null);
        investigationsSupport.storeInvestigationWithStatusAndAssets(CANCELED, List.of(assetAsBuilt2), null);
        investigationsSupport.storeInvestigationWithStatusAndAssets(CLOSED, List.of(assetAsBuilt2), null);

        final String filter = "assetId,EQUAL,urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb,AND";


        // When
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .param("filter", filter)
                .get("/api/investigations")
                .then()
                .statusCode(200)
                .assertThat()
                .body("totalItems", equalTo(3));
    }
}
