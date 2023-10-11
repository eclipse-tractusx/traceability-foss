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
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.common.security.JwtRole;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AlertsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationsSupport;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import qualitynotification.base.request.QualityNotificationSeverityRequest;
import qualitynotification.base.request.StartQualityNotificationRequest;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.SUPERVISOR;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.USER;
import static org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity.RECEIVED;
import static org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity.SENT;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class DashboardControllerIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    InvestigationsSupport investigationsSupport;

    @Autowired
    JpaAssetAsBuiltRepository assetAsBuiltRepository;

    @Autowired
    AlertsSupport alertsSupport;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @ParameterizedTest
    @MethodSource("roles")
    void givenRoles_whenGetDashboard_thenReturnResponse(final List<JwtRole> roles) throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(roles.toArray(new JwtRole[0])))
                .contentType(ContentType.JSON)
                .log().all()
                .when().get("/api/dashboard")
                .then().statusCode(200)
                .body("myParts", equalTo(1))
                .body("otherParts", equalTo(12))
                .body("investigationsReceived", equalTo(0))
                .body("alertsReceived", equalTo(0))
                .body("alertsSent", equalTo(0))
                .body("myPartsWithOpenAlerts", equalTo(0))
                .body("supplierPartsWithOpenAlerts", equalTo(0));
    }

    @Test
    void givenAlertsWithAssets_whenGetDashboard_thenReturnResponse() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        List<AssetAsBuiltEntity> assets = assetAsBuiltRepository.findAll();
        List<AssetAsBuiltEntity> ownAssets = assets.stream()
                .filter(asset -> asset.getOwner().equals(Owner.OWN))
                .toList();
        List<AssetAsBuiltEntity> supplierAssets = assets.stream()
                .filter(asset -> asset.getOwner().equals(Owner.SUPPLIER))
                .toList();
        alertsSupport.storeAlertWithStatusAndAssets(RECEIVED, supplierAssets, null);
        alertsSupport.storeAlertWithStatusAndAssets(SENT, ownAssets, null);

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when().get("/api/dashboard")
                .then().statusCode(200)
                .body("myParts", equalTo(1))
                .body("otherParts", equalTo(12))
                .body("investigationsReceived", equalTo(0))
                .body("alertsReceived", equalTo(1))
                .body("alertsSent", equalTo(1))
                .body("myPartsWithOpenAlerts", equalTo(1))
                .body("supplierPartsWithOpenAlerts", equalTo(12));
    }

    @Test
    void givenNoRoles_whenGetDashboard_thenReturn401() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();

        // when/then
        given()
                .contentType(ContentType.JSON)
                .log().all()
                .when().get("/api/dashboard")
                .then().statusCode(401);
    }

    @Test
    void givenPendingInvestigation_whenGetDashboard_thenReturnPendingInvestigation() throws JoseException, JsonProcessingException {
        // given
        assetsSupport.defaultAssetsStored();
        investigationsSupport.defaultReceivedInvestigationStored();
        String assetId = "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978";
        var notificationRequest = StartQualityNotificationRequest.builder()
                .partIds(List.of(assetId))
                .description("at least 15 characters long investigation description")
                .severity(QualityNotificationSeverityRequest.MINOR)
                .isAsBuilt(true)
                .build();

        // when
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(notificationRequest))
                .when()
                .post("/api/investigations")
                .then()
                .statusCode(201);

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when().get("/api/dashboard")
                .then().statusCode(200)
                .body("myParts", equalTo(1))
                .body("otherParts", equalTo(12))
                .body("investigationsReceived", equalTo(1))
                .body("alertsReceived", equalTo(0))
                .body("alertsSent", equalTo(0))
                .body("myPartsWithOpenAlerts", equalTo(0))
                .body("supplierPartsWithOpenAlerts", equalTo(0));
    }

    private static Stream<Arguments> roles() {
        return Stream.of(
                arguments(List.of(USER)),
                arguments(List.of(ADMIN)),
                arguments(List.of(SUPERVISOR)),
                arguments(List.of(USER, ADMIN))
        );
    }
}
