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
import org.eclipse.tractusx.traceability.integration.common.support.*;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationSideBaseEntity.SENDER;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.*;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationSideBaseEntity.RECEIVER;
import static org.hamcrest.MatcherAssert.assertThat;

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
        alertsSupport.storeAlertWithStatusAndAssets(CREATED, List.of(assetAsBuilt1), SENDER);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt1), SENDER);
        alertsSupport.storeAlertWithStatusAndAssets(CLOSED, List.of(assetAsBuilt1), SENDER);

        AssetAsBuiltEntity assetAsBuilt3 = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2cc").orElseThrow();
        alertsSupport.storeAlertWithStatusAndAssets(CREATED, List.of(assetAsBuilt3), SENDER);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt3), SENDER);
        alertsSupport.storeAlertWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt3), SENDER);
        alertsSupport.storeAlertWithStatusAndAssets(CANCELED, List.of(assetAsBuilt3), SENDER);
        alertsSupport.storeAlertWithStatusAndAssets(CLOSED, List.of(assetAsBuilt3), SENDER);

        // When
        List<List<Integer>> notificationIdLists = given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "50")
                .param("filter", "owner,EQUAL,OWN,AND")
                .param("sort", "sentQualityAlertIdsInStatusActive,desc")
                .contentType(ContentType.JSON)
                .when()
                .log().all()
                .get("/api/assets/as-built")
                .then()
                .statusCode(200)
                .log().all()
                .extract()
                .jsonPath()
                .getList("content.sentQualityAlertIdsInStatusActive");

        // Then
        List<Integer> numberOfItems = notificationIdLists.stream()
                .mapToInt(List::size)
                .boxed()
                .collect(Collectors.toList());

        assertThat(numberOfItems, Matchers.containsInRelativeOrder(3,2));
    }

    @Test
    void givenAlertsForAsset_whenCallWithSortByQualityAlertsInStatusActiveAsc_thenReturnAssetsWithActiveAlertsCountInAsc() throws JoseException {
        // Given
        AssetAsBuiltEntity assetAsBuilt1 = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        alertsSupport.storeAlertWithStatusAndAssets(CREATED, List.of(assetAsBuilt1), SENDER);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt1), SENDER);
        alertsSupport.storeAlertWithStatusAndAssets(CLOSED, List.of(assetAsBuilt1), SENDER);

        AssetAsBuiltEntity assetAsBuilt3 = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2cc").orElseThrow();
        alertsSupport.storeAlertWithStatusAndAssets(CREATED, List.of(assetAsBuilt3), SENDER);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt3), SENDER);
        alertsSupport.storeAlertWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt3), SENDER);
        alertsSupport.storeAlertWithStatusAndAssets(CANCELED, List.of(assetAsBuilt3), SENDER);
        alertsSupport.storeAlertWithStatusAndAssets(CLOSED, List.of(assetAsBuilt3), SENDER);

        // When
        List<List<Integer>> notificationIdLists = given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "50")
                .param("filter", "owner,EQUAL,OWN,AND")
                .param("sort", "sentQualityAlertIdsInStatusActive,asc")
                .contentType(ContentType.JSON)
                .when()
                .log().all()
                .get("/api/assets/as-built")
                .then()
                .statusCode(200)
                .log().all()
                .extract()
                .jsonPath()
                .getList("content.sentQualityAlertIdsInStatusActive");


        // Then
        List<Integer> numberOfItems = notificationIdLists.stream()
                .mapToInt(List::size)
                .boxed()
                .collect(Collectors.toList());

        assertThat(numberOfItems, Matchers.containsInRelativeOrder(2, 3));
    }

    @Test
    void givenInvestigationsForAsset_whenCallWithSortByQualityInvestigationsInStatusActiveDesc_thenReturnAssetsWithActiveInvestigationsCountInDesc() throws JoseException {
        // Given
        assetsSupport.defaultMultipleAssetsAsBuiltStored();
        AssetAsBuiltEntity assetAsBuilt1 = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt1), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(DECLINED, List.of(assetAsBuilt1), RECEIVER);

        AssetAsBuiltEntity assetAsBuilt2 = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2cc").orElseThrow();
        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt2), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt2), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACCEPTED, List.of(assetAsBuilt2), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(DECLINED, List.of(assetAsBuilt2), RECEIVER);

        // When
        List<List<Integer>> notificationIdLists = given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "50")
                .param("filter", "owner,EQUAL,OWN,AND")
                .param("sort", "receivedQualityInvestigationIdsInStatusActive,desc")
                .contentType(ContentType.JSON)
                .when()
                .log().all()
                .get("/api/assets/as-built")
                .then()
                .statusCode(200)
                .log().all()
                .extract()
                .jsonPath()
                .getList("content.receivedQualityInvestigationIdsInStatusActive");

        // Then
        List<Integer> numberOfItems = notificationIdLists.stream()
                .mapToInt(List::size)
                .boxed()
                .collect(Collectors.toList());

        assertThat(numberOfItems, Matchers.containsInRelativeOrder(4, 2));
    }

    @Test
    void givenInvestigationsForAsset_whenCallWithSortByQualityInvestigationsInStatusActiveAsc_thenReturnAssetsWithActiveInvestigationsCountInAsc() throws JoseException {
        // Given
        assetsSupport.defaultMultipleAssetsAsBuiltStored();

        final AssetAsBuiltEntity assetAsBuilt1 = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb").orElseThrow();
        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt1), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(DECLINED, List.of(assetAsBuilt1), RECEIVER);

        final AssetAsBuiltEntity assetAsBuilt2 = jpaAssetAsBuiltRepository.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2cc").orElseThrow();
        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt2), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt2), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(ACCEPTED, List.of(assetAsBuilt2), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(DECLINED, List.of(assetAsBuilt2), RECEIVER);

        // When
        List<List<Integer>> notificationIdLists = given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .param("page", "0")
                .param("size", "50")
                .param("filter", "owner,EQUAL,OWN,AND")
                .param("sort", "receivedQualityInvestigationIdsInStatusActive,asc")
                .contentType(ContentType.JSON)
                .when()
                .log().all()
                .get("/api/assets/as-built")
                .then()
                .statusCode(200)
                .log().all()
                .extract()
                .jsonPath()
                .getList("content.receivedQualityInvestigationIdsInStatusActive");

        // Then
        List<Integer> numberOfItems = notificationIdLists.stream()
                .mapToInt(List::size)
                .boxed()
                .collect(Collectors.toList());

        assertThat(numberOfItems, Matchers.containsInRelativeOrder(2, 4));
    }

    private static Stream<Arguments> sortArguments() {
        return Stream.of(
                Arguments.of("sentQualityAlertIdsInStatusActive,asc", "owner,EQUAL,SUPPLIER,AND", "content.sentQualityAlertIdsInStatusActive",
                        List.of(1, 2, 20)),
                Arguments.of("receivedQualityAlertIdsInStatusActive,desc", "owner,EQUAL,SUPPLIER,AND", "content.receivedQualityAlertIdsInStatusActive",
                        List.of(20, 2, 1)),
                Arguments.of("sentQualityInvestigationIdsInStatusActive,asc", "owner,EQUAL,SUPPLIER,AND", "content.sentQualityInvestigationIdsInStatusActive",
                        List.of(1, 2, 20)),
                Arguments.of("receivedQualityInvestigationIdsInStatusActive,desc", "owner,EQUAL,SUPPLIER,AND", "content.receivedQualityInvestigationIdsInStatusActive",
                        List.of(20, 2, 1))
        );
    }

    @ParameterizedTest
    @MethodSource("sortArguments")
    void givenNotificationForAsset_whenCallWithSortAndFilterArgumentEndpoint_thenReturnExpectedResponseTest(
            final String sort,
            final String filter,
            final String contentField,
            final List<Integer> expectedOrderOfNotificationItems) throws JoseException {

        final long page = 0;
        final long size = 50;

        // Given
        assetsSupport.defaultMultipleAssetsAsBuiltStored();
        final AssetAsBuiltEntity assetAsBuilt1 = jpaAssetAsBuiltRepository.findById(
                "urn:uuid:f7cf62fe-9e25-472b-9148-66ebcc291f31").orElseThrow();
        final AssetAsBuiltEntity assetAsBuilt2 = jpaAssetAsBuiltRepository.findById(
                "urn:uuid:186359fb-4584-40e4-a59b-ed842d3d80d9").orElseThrow();
        final AssetAsBuiltEntity assetAsBuilt3 = jpaAssetAsBuiltRepository.findById(
                "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978").orElseThrow();

        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt1), SENDER);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt2), SENDER);
        alertsSupport.storeAlertWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt2), SENDER);
        IntStream
                .rangeClosed(1, 20)
                .forEach(i -> alertsSupport.storeAlertWithStatusAndAssets(SENT, List.of(assetAsBuilt3), SENDER));

        alertsSupport.storeAlertWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt1), RECEIVER);
        alertsSupport.storeAlertWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt2), RECEIVER);
        alertsSupport.storeAlertWithStatusAndAssets(ACKNOWLEDGED, List.of(assetAsBuilt2), RECEIVER);
        IntStream
                .rangeClosed(1, 20)
                .forEach(i -> alertsSupport.storeAlertWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt3), RECEIVER));

        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt1), SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt2), SENDER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt2), SENDER);
        IntStream
                .rangeClosed(1, 20)
                .forEach(i -> investigationsSupport.storeInvestigationWithStatusAndAssets(SENT, List.of(assetAsBuilt3), SENDER));

        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt1), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt2), RECEIVER);
        investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt2), RECEIVER);
        IntStream
                .rangeClosed(1, 20)
                .forEach(i -> investigationsSupport.storeInvestigationWithStatusAndAssets(RECEIVED, List.of(assetAsBuilt3),
                        RECEIVER));

        // When
        List<List<Integer>> notificationIdLists = given()
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
                .extract()
                .jsonPath()
                .getList(contentField);

        // Then
        List<Integer> numberOfItems = notificationIdLists.stream()
                .mapToInt(List::size)
                .boxed()
                .collect(Collectors.toList());

        assertThat(numberOfItems, Matchers.containsInRelativeOrder(expectedOrderOfNotificationItems.toArray()));
    }
}
