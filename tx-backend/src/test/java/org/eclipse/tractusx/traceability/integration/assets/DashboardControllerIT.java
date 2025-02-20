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
import notification.request.NotificationSeverityRequest;
import notification.request.NotificationTypeRequest;
import notification.request.StartNotificationRequest;
import org.eclipse.tractusx.traceability.assets.domain.base.model.Owner;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.repository.JpaAssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.common.security.JwtRole;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.NotificationSupport;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.SUPERVISOR;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.USER;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.RECEIVED;
import static org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity.SENT;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class DashboardControllerIT extends IntegrationTestSpecification {

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    NotificationSupport notificationSupport;

    @Autowired
    JpaAssetAsBuiltRepository assetAsBuiltRepository;

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
                .log().all()
                .body("asBuiltCustomerParts", equalTo(0))
                .body("asPlannedCustomerParts", equalTo(0))
                .body("asBuiltSupplierParts", equalTo(11))
                .body("asPlannedSupplierParts", equalTo(0))
                .body("asBuiltOwnParts", equalTo(2))
                .body("asPlannedOwnParts", equalTo(0))
                .body("myPartsWithOpenAlerts", equalTo(0))
                .body("myPartsWithOpenInvestigations", equalTo(0))
                .body("supplierPartsWithOpenAlerts", equalTo(0))
                .body("customerPartsWithOpenAlerts", equalTo(0))
                .body("supplierPartsWithOpenInvestigations", equalTo(0))
                .body("customerPartsWithOpenInvestigations", equalTo(0))
                .body("receivedActiveAlerts", equalTo(0))
                .body("receivedActiveInvestigations", equalTo(0))
                .body("sentActiveAlerts", equalTo(0))
                .body("sentActiveInvestigations", equalTo(0));
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
        notificationSupport.storeAlertWithStatusAndAssets(RECEIVED, supplierAssets);
        notificationSupport.storeAlertWithStatusAndAssets(SENT, ownAssets);

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when().get("/api/dashboard")
                .then().statusCode(200)
                .log().all()
                .body("asBuiltCustomerParts", equalTo(0))
                .body("asPlannedCustomerParts", equalTo(0))
                .body("asBuiltSupplierParts", equalTo(11))
                .body("asPlannedSupplierParts", equalTo(0))
                .body("asBuiltOwnParts", equalTo(2))
                .body("asPlannedOwnParts", equalTo(0))
                .body("myPartsWithOpenAlerts", equalTo(2))
                .body("myPartsWithOpenInvestigations", equalTo(0))
                .body("supplierPartsWithOpenAlerts", equalTo(11))
                .body("customerPartsWithOpenAlerts", equalTo(0))
                .body("supplierPartsWithOpenInvestigations", equalTo(0))
                .body("customerPartsWithOpenInvestigations", equalTo(0))
                .body("receivedActiveAlerts", equalTo(2))
                .body("receivedActiveInvestigations", equalTo(0))
                .body("sentActiveAlerts", equalTo(0))
                .body("sentActiveInvestigations", equalTo(0));
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
    void givenNoRoles_whenGetDashboard_thenReturn403() throws JoseException {
        // given
        assetsSupport.defaultAssetsStored();

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorizationWithNoRole())
                .contentType(ContentType.JSON)
                .log().all()
                .when().get("/api/dashboard")
                .then().statusCode(403);
    }

    @Test
    void givenPendingInvestigation_whenGetDashboard_thenReturnPendingInvestigation() throws JoseException, JsonProcessingException {
        // given
        assetsSupport.defaultAssetsStored();
        notificationSupport.defaultReceivedInvestigationStored();
        String assetId = "urn:uuid:fe99da3d-b0de-4e80-81da-882aebcca978";
        var notificationRequest = StartNotificationRequest.builder()
                .affectedPartIds(List.of(assetId))
                .description("at least 15 characters long investigation description")
                .severity(NotificationSeverityRequest.MINOR)
                .type(NotificationTypeRequest.INVESTIGATION)
                .receiverBpn("BPNL00000003CNKC")
                .build();

        // when
        given()
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(notificationRequest))
                .when()
                .post("/api/notifications")
                .then()
                .statusCode(201);

        // then
        given()
                .header(oAuth2Support.jwtAuthorization(SUPERVISOR))
                .contentType(ContentType.JSON)
                .log().all()
                .when().get("/api/dashboard")
                .then().statusCode(200)
                .log().all()
                .body("asBuiltCustomerParts", equalTo(0))
                .body("asPlannedCustomerParts", equalTo(0))
                .body("asBuiltSupplierParts", equalTo(11))
                .body("asPlannedSupplierParts", equalTo(0))
                .body("asBuiltOwnParts", equalTo(2))
                .body("asPlannedOwnParts", equalTo(0))
                .body("myPartsWithOpenAlerts", equalTo(0))
                .body("myPartsWithOpenInvestigations", equalTo(0))
                .body("supplierPartsWithOpenAlerts", equalTo(0))
                .body("customerPartsWithOpenAlerts", equalTo(0))
                .body("supplierPartsWithOpenInvestigations", equalTo(1))
                .body("customerPartsWithOpenInvestigations", equalTo(0))
                .body("receivedActiveAlerts", equalTo(0))
                .body("receivedActiveInvestigations", equalTo(1))
                .body("sentActiveAlerts", equalTo(0))
                .body("sentActiveInvestigations", equalTo(1));
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
