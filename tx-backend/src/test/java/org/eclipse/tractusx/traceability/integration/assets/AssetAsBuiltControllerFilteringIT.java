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

package org.eclipse.tractusx.traceability.integration.assets;

import assets.request.AssetFilter;
import assets.request.AssetRequest;
import common.FilterAttribute;
import common.FilterValue;
import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.assets.domain.base.model.ImportState;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.infrastructure.base.model.AssetBaseEntity;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaOperator;
import org.eclipse.tractusx.traceability.common.model.SearchCriteriaStrategy;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AlertsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationsSupport;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationSideBaseEntity.RECEIVER;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationSideBaseEntity.SENDER;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.ACCEPTED;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.ACKNOWLEDGED;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.CANCELED;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.CLOSED;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.CREATED;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.DECLINED;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.RECEIVED;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.SENT;
import static org.hamcrest.Matchers.equalTo;

class AssetAsBuiltControllerFilteringIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    JpaAssetAsBuiltRepository jpaAssetAsBuiltRepository;

    @Autowired
    AlertsSupport alertsSupport;

    @Autowired
    InvestigationsSupport investigationsSupport;

    @Test
    void givenNoFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(13));
    }

    @Test
    void givenExcludeFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=id,EXCLUDE,urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb,AND&filter=id,EXCLUDE,urn:uuid:6dafbcec-2fce-4cbb-a5a9-b3b32aa5cffc,AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .statusCode(200)
                .body("totalItems", equalTo(11));
    }

    @Test
    void givenExcludeFilterMultiFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=id,EXCLUDE,urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb,AND&filter=id,EQUAL,urn:uuid:6dafbcec-2fce-4cbb-a5a9-b3b32aa5cffc,AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .statusCode(200)
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenIdAndIdShortFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=id,STARTS_WITH,urn:uuid:1,AND&filter=idShort,STARTS_WITH,engineering,AND";
        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(2));
    }

    @Test
    void givenOwnFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=owner,EQUAL,OWN,AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(2));
    }

    @Test
    void givenManufacturerIdFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=businessPartner,EQUAL,BPNL00000003B0Q0,AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(4));
    }

    @Test
    void givenManufacturerIdAndSemanticModelIdFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=businessPartner,EQUAL,BPNL00000003B0Q0,AND&filter=semanticModelId,STARTS_WITH,NO-3404609481920549,AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenIdShortStartsWithFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=idShort,STARTS_WITH,ntier_,AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenManufacturingDateFilter_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=manufacturingDate,AT_LOCAL_DATE,2014-11-18,AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenSemanticDataModelAndManufacturingDateFilterOR_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given

        assetsSupport.defaultAssetsStored();

        AssetRequest requestBody = AssetRequest.builder().page(5).size(50).assetFilters(List.of(AssetFilter.builder()
                .manufacturingDate(
                        FilterAttribute.builder()
                                .value(List.of(FilterValue.builder()
                                        .value("2014-11-18")
                                        .strategy(SearchCriteriaStrategy.AT_LOCAL_DATE.name()).build()))
                                .operator(SearchCriteriaOperator.AND.name()).build())
                .semanticDataModel(
                        FilterAttribute.builder()
                                .value(List.of(FilterValue.builder()
                                        .value("SERIALPART")
                                        .strategy(SearchCriteriaStrategy.EQUAL.name()).build()))
                                .operator(SearchCriteriaOperator.OR.name()).build()).build())).build();

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(requestBody)
                .log().all()
                .when()
                .post("/api/assets/as-built/query")
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenSemanticDataModelAndManufacturingDateFilterAnd_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=manufacturingDate,AT_LOCAL_DATE,2014-11-18,AND&filter=semanticDataModel,EQUAL,SERIALPART,AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenSemanticDataModelAndOwnerOR_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=owner,EQUAL,SUPPLIER,AND&filter=id,STARTS_WITH,urn:uuid:f7cf62fe-9e25-472b-9148-66,OR";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenSemanticDataModelAsMultipleValuesAndOwnerOR_whenCallFilteredEndpoint_thenReturnExpectedResult() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=owner,EQUAL,SUPPLIER,AND&filter=semanticDataModel,EQUAL,SERIALPART,OR&filter=semanticDataModel,EQUAL,BATCH,OR";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(11)).extract().response();
    }

    @Test
    void givenNonExistingFilterField_whenGetAssetsAsBuilt_thenBadRequest() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=nonExistingField,EQUAL,SUPPLIER,AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(400);
    }

    @Test
    void givenAssetsWithAlerts_whenGetAssetsWithActiveAlertCountFilter_thenReturnProperAssets() throws JoseException {
        // Given
        assetsSupport.defaultAssetsStored();
        AssetAsBuiltEntity assetAsBuilt = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        alertsSupport.storeAlertWithStatusAndAssets(CREATED, List.of(assetAsBuilt));
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt));
        alertsSupport.storeAlertWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt));
        alertsSupport.storeAlertWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt));
        alertsSupport.storeAlertWithStatusAndAssets(ACCEPTED, List.of(assetAsBuilt));
        alertsSupport.storeAlertWithStatusAndAssets(DECLINED, List.of(assetAsBuilt));
        alertsSupport.storeAlertWithStatusAndAssets(CANCELED, List.of(assetAsBuilt));
        alertsSupport.storeAlertWithStatusAndAssets(CLOSED, List.of(assetAsBuilt));

        final String filter = "receivedQualityAlertIdsInStatusActive,NOTIFICATION_COUNT_EQUAL,6,AND";

        // When
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .param("filter", filter)
                .get("/api/assets/as-built")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenAssetsWithAlerts_whenGetAssetsWithActiveAlertCountFilter2_thenReturnProperAssets() throws JoseException {
        // Given
        assetsSupport.defaultAssetsStored();
        AssetAsBuiltEntity assetAsBuilt = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        AssetAsBuiltEntity assetAsBuilt1 = jpaAssetAsBuiltRepository.findById("urn:uuid:7fa65f10-9dc1-49fe-818a-09c7313a4562").orElseThrow();
        alertsSupport.storeAlertWithStatusAndAssets(CREATED, List.of(assetAsBuilt, assetAsBuilt1));
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt1));
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt));
        alertsSupport.storeAlertWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt));
        alertsSupport.storeAlertWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt));
        alertsSupport.storeAlertWithStatusAndAssets(ACCEPTED, List.of(assetAsBuilt));
        alertsSupport.storeAlertWithStatusAndAssets(DECLINED, List.of(assetAsBuilt));
        alertsSupport.storeAlertWithStatusAndAssets(CANCELED, List.of(assetAsBuilt));
        alertsSupport.storeAlertWithStatusAndAssets(CLOSED, List.of(assetAsBuilt));

        final String filter = "receivedQualityAlertIdsInStatusActive,NOTIFICATION_COUNT_EQUAL,2,AND";

        // When
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .param("filter", filter)
                .get("/api/assets/as-built")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenAssetsWithInvestigations_whenGetAssetsWithActiveSentInvestigationCountFilter_thenReturnProperAssets() throws JoseException {
        // Given
        assetsSupport.defaultAssetsStored();
        AssetAsBuiltEntity assetAsBuilt = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        investigationsSupport.storeInvestigationWithStatusAndAssets(CREATED, List.of(assetAsBuilt), SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt), SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt), SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt), SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACCEPTED, List.of(assetAsBuilt), SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(DECLINED, List.of(assetAsBuilt), SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(CANCELED, List.of(assetAsBuilt), SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(CLOSED, List.of(assetAsBuilt), SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(CREATED, List.of(assetAsBuilt), RECEIVER);

        final String filter = "sentQualityInvestigationIdsInStatusActive,NOTIFICATION_COUNT_EQUAL,6,AND";


        // When
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .param("filter", filter)
                .get("/api/assets/as-built")
                .then()
                .statusCode(200)
                .assertThat()
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenAssetsWithInvestigations_whenGetAssetsWithActiveReceivedInvestigationCountFilter_thenReturnProperAssets() throws JoseException {
        // Given
        assetsSupport.defaultAssetsStored();
        AssetAsBuiltEntity assetAsBuilt = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        investigationsSupport.storeInvestigationWithStatusAndAssets(CREATED, List.of(assetAsBuilt), SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt), SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt), SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt), SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACCEPTED, List.of(assetAsBuilt), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(DECLINED, List.of(assetAsBuilt), SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(CANCELED, List.of(assetAsBuilt), SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(CLOSED, List.of(assetAsBuilt), SENDER);

        investigationsSupport.storeInvestigationWithStatusAndAssets(CREATED, List.of(assetAsBuilt), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACCEPTED, List.of(assetAsBuilt), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(DECLINED, List.of(assetAsBuilt), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(CANCELED, List.of(assetAsBuilt), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(CLOSED, List.of(assetAsBuilt), RECEIVER);

        final String filter = "receivedQualityInvestigationIdsInStatusActive,NOTIFICATION_COUNT_EQUAL,7,AND";


        // When
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .param("filter", filter)
                .get("/api/assets/as-built")
                .then()
                .statusCode(200)
                .assertThat()
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenAssetsWithInvestigations_whenGetAssetsWithActiveInvestigationCountFilter2_thenReturnProperAssets() throws JoseException {
        // Given
        assetsSupport.defaultAssetsStored();
        AssetAsBuiltEntity assetAsBuilt = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        AssetAsBuiltEntity assetAsBuilt1 = jpaAssetAsBuiltRepository.findById("urn:uuid:7fa65f10-9dc1-49fe-818a-09c7313a4562").orElseThrow();
        investigationsSupport.storeInvestigationWithStatusAndAssets(CREATED, List.of(assetAsBuilt, assetAsBuilt1));
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt1));
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACCEPTED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(DECLINED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(CANCELED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(CLOSED, List.of(assetAsBuilt));

        final String filter = "receivedQualityInvestigationIdsInStatusActive,NOTIFICATION_COUNT_EQUAL,2,AND";


        // When
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .param("filter", filter)
                .get("/api/assets/as-built")
                .then()
                .statusCode(200)
                .assertThat()
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenAssetsWithAlerts_whenGetAssetSortedBySentActiveAlertsDesc_thenReturnProperAssets() throws JoseException {
        // Given
        assetsSupport.defaultAssetsStored();

        List<AssetAsBuiltEntity> assets = jpaAssetAsBuiltRepository.findAll();

        for (int i = 0; i < assets.size(); i++) {
            for (int j = i; j > 0; j--) {
                alertsSupport.storeAlertWithStatusAndAssets(CREATED, List.of(assets.get(i)), SENDER);
            }
        }

        final String sort = "sentQualityAlertIdsInStatusActive,ASC";


        // When
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .param("sort", sort)
                .get("/api/assets/as-built")
                .then()
                .statusCode(200)
                .assertThat()
                .body("totalItems", equalTo(13))
                .body("content.id", Matchers.containsInRelativeOrder(assets.stream().map(AssetBaseEntity::getId).toArray()));
    }

    @Test
    void givenAssetsWithInvestigations_whenGetAssetsWithActiveInvestigationCountFilter3_thenReturnProperAssets() throws JoseException {
        // Given
        assetsSupport.defaultAssetsStored();
        AssetAsBuiltEntity assetAsBuilt = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        AssetAsBuiltEntity assetAsBuilt1 = jpaAssetAsBuiltRepository.findById("urn:uuid:7fa65f10-9dc1-49fe-818a-09c7313a4562").orElseThrow();
        investigationsSupport.storeInvestigationWithStatusAndAssets(CREATED, List.of(assetAsBuilt, assetAsBuilt1));
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt1));
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACCEPTED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(DECLINED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(CANCELED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(CLOSED, List.of(assetAsBuilt));

        final String filter = "receivedQualityInvestigationIdsInStatusActive,NOTIFICATION_COUNT_EQUAL,0,AND";


        // When
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .param("filter", filter)
                .get("/api/assets/as-built")
                .then()
                .statusCode(200)
                .assertThat()
                .body("totalItems", equalTo(11));
    }

    @Test
    void givenAssetsWithInvestigations_whenGetAssetsWithActiveInvestigationCountFilter4_thenReturnProperAssets() throws JoseException {
        // Given
        assetsSupport.defaultAssetsStored();
        AssetAsBuiltEntity assetAsBuilt = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        AssetAsBuiltEntity assetAsBuilt1 = jpaAssetAsBuiltRepository.findById("urn:uuid:7fa65f10-9dc1-49fe-818a-09c7313a4562").orElseThrow();
        investigationsSupport.storeInvestigationWithStatusAndAssets(CREATED, List.of(assetAsBuilt, assetAsBuilt1));
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt1));
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACCEPTED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(DECLINED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(CANCELED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(CLOSED, List.of(assetAsBuilt));

        final String filter = "receivedQualityAlertIdsInStatusActive,NOTIFICATION_COUNT_EQUAL,0,AND";


        // When
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .param("filter", filter)
                .get("/api/assets/as-built")
                .then()
                .statusCode(200)
                .assertThat()
                .body("totalItems", equalTo(13));
    }

    @Test
    void givenAssetsWithInvestigations_whenGetAssetsWithActiveInvestigationCountFilter5_thenReturnProperAssets() throws JoseException {
        // Given
        assetsSupport.defaultAssetsStored();
        AssetAsBuiltEntity assetAsBuilt = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        AssetAsBuiltEntity assetAsBuilt1 = jpaAssetAsBuiltRepository.findById("urn:uuid:7fa65f10-9dc1-49fe-818a-09c7313a4562").orElseThrow();
        investigationsSupport.storeInvestigationWithStatusAndAssets(CREATED, List.of(assetAsBuilt, assetAsBuilt1));
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt1));
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACCEPTED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(DECLINED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(CANCELED, List.of(assetAsBuilt));
        investigationsSupport.storeInvestigationWithStatusAndAssets(CLOSED, List.of(assetAsBuilt));

        final String filter1 = "receivedQualityAlertIdsInStatusActive,NOTIFICATION_COUNT_EQUAL,0,AND";
        final String filter2 = "receivedQualityInvestigationIdsInStatusActive,NOTIFICATION_COUNT_EQUAL,2,AND";


        // When
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .param("filter", filter1)
                .param("filter", filter2)
                .get("/api/assets/as-built")
                .then()
                .statusCode(200)
                .assertThat()
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenAssetsWithInvestigations_whenGetAssetsWithActiveInvestigationCountFilter6_thenReturnProperAssets() throws JoseException {
        // Given
        assetsSupport.defaultAssetsStored();
        AssetAsBuiltEntity assetAsBuilt = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        AssetAsBuiltEntity assetAsBuilt1 = jpaAssetAsBuiltRepository.findById("urn:uuid:7fa65f10-9dc1-49fe-818a-09c7313a4562").orElseThrow();

        alertsSupport.storeAlertWithStatusAndAssets(CREATED, List.of(assetAsBuilt, assetAsBuilt1), SENDER);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt1), SENDER);

        investigationsSupport.storeInvestigationWithStatusAndAssets(CREATED, List.of(assetAsBuilt, assetAsBuilt1), SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(CREATED, List.of(assetAsBuilt, assetAsBuilt1), SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt1), SENDER);

        alertsSupport.storeAlertWithStatusAndAssets(CREATED, List.of(assetAsBuilt, assetAsBuilt1), RECEIVER);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt), RECEIVER);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt), RECEIVER);

        investigationsSupport.storeInvestigationWithStatusAndAssets(CREATED, List.of(assetAsBuilt, assetAsBuilt1), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(CREATED, List.of(assetAsBuilt, assetAsBuilt1), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt), RECEIVER);


        final String filter1 = "receivedQualityAlertIdsInStatusActive,NOTIFICATION_COUNT_EQUAL,3,AND";
        final String filter2 = "receivedQualityInvestigationIdsInStatusActive,NOTIFICATION_COUNT_EQUAL,4,AND";
        final String filter3 = "sentQualityAlertIdsInStatusActive,NOTIFICATION_COUNT_EQUAL,1,AND";
        final String filter4 = "sentQualityInvestigationIdsInStatusActive,NOTIFICATION_COUNT_EQUAL,2,AND";

        // When
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .when()
                .param("filter", filter1)
                .param("filter", filter2)
                .param("filter", filter3)
                .param("filter", filter4)
                .get("/api/assets/as-built")
                .then()
                .statusCode(200)
                .assertThat()
                .body("totalItems", equalTo(1));
    }

    @Test
    void givenAssetsWithImportStateExistent_whenCallFilteredEndpoint_thenReturnProperAssets() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        final String filter = "?filter=importState,EQUAL,PERSISTENT,AND,importNote,STARTS_WITH,A,AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("totalItems", equalTo(13));
    }

    @Test
    void givenAssetsWithImportStateTransientExistent_whenCallFilteredEndpoint_thenReturnProperAssets() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        AssetAsBuiltEntity assetAsBuilt = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        assetAsBuilt.setImportState(ImportState.TRANSIENT);
        jpaAssetAsBuiltRepository.save(assetAsBuilt);


        final String filter = "?filter=importState,EQUAL,TRANSIENT,AND";

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/assets/as-built" + filter)
                .then()
                .log().all()
                .statusCode(200)
                .body("content.importState", equalTo(List.of("TRANSIENT")));
    }
}
