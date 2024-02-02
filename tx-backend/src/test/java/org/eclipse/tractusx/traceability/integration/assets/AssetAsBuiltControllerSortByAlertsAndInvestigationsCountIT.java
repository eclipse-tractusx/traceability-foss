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

import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AlertsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationsSupport;
import org.hamcrest.Matchers;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationSideBaseEntity.RECEIVER;
import static org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationSideBaseEntity.SENDER;
import static org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity.*;

class AssetAsBuiltControllerSortByAlertsAndInvestigationsCountIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    JpaAssetAsBuiltRepository jpaAssetAsBuiltRepository;

    @Autowired
    AlertsSupport alertsSupport;

    @Autowired
    InvestigationsSupport investigationsSupport;

    @BeforeEach
    void before() {
        assetsSupport.defaultMultipleAssetsAsBuiltStored();
    }

    @Test
    void givenAlertsForAsset_whenCallWithSortByQualityAlertsInStatusActiveDesc_thenReturnAssetsWithActiveAlertsCountInDesc() throws JoseException {
        // Given
        AssetAsBuiltEntity assetAsBuilt1 = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        alertsSupport.storeAlertWithStatusAndAssets(CREATED, List.of(assetAsBuilt1), null);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt1), null);
        alertsSupport.storeAlertWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt1), null);
        alertsSupport.storeAlertWithStatusAndAssets(CANCELED, List.of(assetAsBuilt1), null);
        alertsSupport.storeAlertWithStatusAndAssets(CLOSED, List.of(assetAsBuilt1), null);

        AssetAsBuiltEntity assetAsBuilt3 = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2cc").orElseThrow();
        alertsSupport.storeAlertWithStatusAndAssets(CREATED, List.of(assetAsBuilt3), null);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt3), null);
        alertsSupport.storeAlertWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt3), null);
        alertsSupport.storeAlertWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt3), null);
        alertsSupport.storeAlertWithStatusAndAssets(ACCEPTED, List.of(assetAsBuilt3), null);
        alertsSupport.storeAlertWithStatusAndAssets(DECLINED, List.of(assetAsBuilt3), null);
        alertsSupport.storeAlertWithStatusAndAssets(CANCELED, List.of(assetAsBuilt3), null);
        alertsSupport.storeAlertWithStatusAndAssets(CLOSED, List.of(assetAsBuilt3), null);

        // When
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "50")
                .param("filter", "owner,EQUAL,OWN,AND")
                .param("sort", "receivedQualityAlertIdsInStatusActive,desc")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/as-built")
                .then()
                .log().all()
                .statusCode(200)
                .body("content.receivedQualityAlertIdsInStatusActive", Matchers.containsInRelativeOrder(List.of(190, 191, 192, 193, 194, 195),List.of(185, 186, 187)));
    }

    @Test
    void givenAlertsForAsset_whenCallWithSortByQualityAlertsInStatusActiveAsc_thenReturnAssetsWithActiveAlertsCountInAsc() throws JoseException {
        // Given
        AssetAsBuiltEntity assetAsBuilt1 = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        alertsSupport.storeAlertWithStatusAndAssets(CREATED, List.of(assetAsBuilt1), null);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt1), null);
        alertsSupport.storeAlertWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt1), null);
        alertsSupport.storeAlertWithStatusAndAssets(CANCELED, List.of(assetAsBuilt1), null);
        alertsSupport.storeAlertWithStatusAndAssets(CLOSED, List.of(assetAsBuilt1), null);

        AssetAsBuiltEntity assetAsBuilt3 = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2cc").orElseThrow();
        alertsSupport.storeAlertWithStatusAndAssets(CREATED, List.of(assetAsBuilt3), null);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt3), null);
        alertsSupport.storeAlertWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt3), null);
        alertsSupport.storeAlertWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt3), null);
        alertsSupport.storeAlertWithStatusAndAssets(CANCELED, List.of(assetAsBuilt3), null);
        alertsSupport.storeAlertWithStatusAndAssets(CLOSED, List.of(assetAsBuilt3), null);

        // When
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "50")
                .param("filter", "owner,EQUAL,OWN,AND")
                .param("sort", "receivedQualityAlertIdsInStatusActive,asc")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/as-built")
                .then()
                .log().all()
                .statusCode(200)
                .body("content.receivedQualityAlertIdsInStatusActive", Matchers.containsInRelativeOrder(List.of(198, 199, 200),List.of(203, 204, 205, 206)));
    }

    @Test
    void givenInvestigationsForAsset_whenCallWithSortByQualityInvestigationsInStatusActiveDesc_thenReturnAssetsWithActiveInvestigationsCountInDesc() throws JoseException {
        // Given
        assetsSupport.defaultMultipleAssetsAsBuiltStored();
        AssetAsBuiltEntity assetAsBuilt1 = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        investigationsSupport.storeInvestigationWithStatusAndAssets(CREATED, List.of(assetAsBuilt1), null);
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt1), null);
        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt1), null);
        investigationsSupport.storeInvestigationWithStatusAndAssets(CANCELED, List.of(assetAsBuilt1), null);
        investigationsSupport.storeInvestigationWithStatusAndAssets(CLOSED, List.of(assetAsBuilt1), null);

        AssetAsBuiltEntity assetAsBuilt2 = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2cc").orElseThrow();
        investigationsSupport.storeInvestigationWithStatusAndAssets(CREATED, List.of(assetAsBuilt2), null);
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt2), null);
        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt2), null);
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt2), null);
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACCEPTED, List.of(assetAsBuilt2), null);
        investigationsSupport.storeInvestigationWithStatusAndAssets(DECLINED, List.of(assetAsBuilt2), null);
        investigationsSupport.storeInvestigationWithStatusAndAssets(CANCELED, List.of(assetAsBuilt2), null);
        investigationsSupport.storeInvestigationWithStatusAndAssets(CLOSED, List.of(assetAsBuilt2), null);

        // When
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "50")
                .param("filter", "owner,EQUAL,OWN,AND")
                .param("sort", "sentQualityInvestigationIdsInStatusActive,desc")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/as-built")
                .then()
                .statusCode(200)
                .body("content.sentQualityInvestigationIdsInStatusActive", Matchers.containsInRelativeOrder(List.of(),List.of()));
    }

    @Test
    void givenInvestigationsForAsset_whenCallWithSortByQualityInvestigationsInStatusActiveAsc_thenReturnAssetsWithActiveInvestigationsCountInAsc() throws JoseException {
        // Given
        assetsSupport.defaultMultipleAssetsAsBuiltStored();
        AssetAsBuiltEntity assetAsBuilt1 = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        investigationsSupport.storeInvestigationWithStatusAndAssets(CREATED, List.of(assetAsBuilt1), null,SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt1), null,SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt1), null,SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(CANCELED, List.of(assetAsBuilt1), null,SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(CLOSED, List.of(assetAsBuilt1), null,SENDER);

        AssetAsBuiltEntity assetAsBuilt2 = jpaAssetAsBuiltRepository.findById("urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978").orElseThrow();
        investigationsSupport.storeInvestigationWithStatusAndAssets(CREATED, List.of(assetAsBuilt2), null,SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt2), null,SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt2), null,SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt2), null,SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACCEPTED, List.of(assetAsBuilt2), null,SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(DECLINED, List.of(assetAsBuilt2), null,SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(CANCELED, List.of(assetAsBuilt2), null,SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(CLOSED, List.of(assetAsBuilt2), null,SENDER);

        // When
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "50")
                .param("filter", "owner,EQUAL,OWN,AND")
                .param("sort", "sentQualityInvestigationIdsInStatusActive,asc")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/assets/as-built")
                .then()
                .statusCode(200)
                .body("content.sentQualityInvestigationIdsInStatusActive", Matchers.containsInRelativeOrder(List.of(),List.of(198, 199, 200)));
    }

    private static Stream<Arguments> sortArgument() {
        return Stream.of(
                Arguments.of("sentQualityAlertIdsInStatusActive,asc", "owner,EQUAL,OWN,AND", "content.sentQualityAlertIdsInStatusActive",
                        List.of(List.of(), List.of(2,3))),
                Arguments.of("sentQualityInvestigationIdsInStatusActive,desc", "owner,EQUAL,OWN,AND", "content.sentQualityInvestigationIdsInStatusActive",
                        List.of(List.of(48,49), List.of())),
                Arguments.of("receivedQualityAlertIdsInStatusActive,asc", "owner,EQUAL,OWN,AND", "content.receivedQualityAlertIdsInStatusActive",
                        List.of(List.of(), List.of(117,118))),
                Arguments.of("receivedQualityAlertIdsInStatusActive,desc", "owner,EQUAL,OWN,AND", "content.receivedQualityAlertIdsInStatusActive",
                        List.of(List.of(163,164), List.of()))
        );
    }

    @ParameterizedTest
    @MethodSource("sortArgument")
    void givenNotificationForAsset_whenCallWithSortAndFilterArgumentEndpoint_thenReturnExpectedResponse_test(
            final String sort,
            final String filter,
            final String contentField,
            final List<Integer> expectedOrderOfNotificationItems) throws JoseException {

        final long page = 0;
        final long size = 20;

        // Given
        assetsSupport.defaultMultipleAssetsAsBuiltStored();

        final AssetAsBuiltEntity assetAsBuilt1 = jpaAssetAsBuiltRepository.findById(
                "urn:uuid:f7cf62fe-9e25-472b-9148-66ebcc291f31").orElseThrow();
        final AssetAsBuiltEntity assetAsBuilt2 = jpaAssetAsBuiltRepository.findById(
                "urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        final AssetAsBuiltEntity assetAsBuilt3 = jpaAssetAsBuiltRepository.findById(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978").orElseThrow();

        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt1), null, SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt2), null, SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt2), null, SENDER);
        IntStream
                .rangeClosed(1, 20)
                .forEach(i -> investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt3),
                        null,SENDER));

        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt1), null, RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt2), null, RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt2), null, RECEIVER);
        IntStream
                .rangeClosed(1, 20)
                .forEach(i -> investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt3),
                        null,RECEIVER));


        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt1), null, SENDER);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt2), null, SENDER);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt2), null, SENDER);
        IntStream
                .rangeClosed(1, 20)
                .forEach(i -> alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt3),
                        null,SENDER));

        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt1), null, RECEIVER);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt2), null, RECEIVER);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt2), null, RECEIVER);
        IntStream
                .rangeClosed(1, 20)
                .forEach(i -> alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt3),
                        null,RECEIVER));

        // When
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .param("page", page)
                .param("size", size)
                .param("sort", sort)
                .param("filter", filter)
                .when()
                .log().all()
                .get("/api/assets/as-built")
                .then()
                .statusCode(200)
                .log().all()
                .body(contentField, Matchers.containsInRelativeOrder(expectedOrderOfNotificationItems.toArray()));
    }
}
