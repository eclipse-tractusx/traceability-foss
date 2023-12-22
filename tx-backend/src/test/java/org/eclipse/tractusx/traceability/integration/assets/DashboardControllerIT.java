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

import static io.restassured.RestAssured.given;
import static org.eclipse.tractusx.traceability.common.security.JwtRole.ADMIN;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.http.ContentType;
import java.util.Arrays;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AlertNotificationsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.BpnSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationNotificationsSupport;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.alert.model.AlertNotificationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationNotificationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity;
import org.eclipse.tractusx.traceability.testdata.AlertTestDataFactory;
import org.eclipse.tractusx.traceability.testdata.InvestigationTestDataFactory;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DashboardControllerIT extends IntegrationTestSpecification {

    @Autowired
    BpnSupport bpnSupport;

    @Autowired
    AssetsSupport assetsSupport;

    @Autowired
    InvestigationNotificationsSupport investigationNotificationsSupport;

    @Autowired
    AlertNotificationsSupport alertNotificationsSupport;

    @Test
    void givenAssetsAndReceivedNotifications_whenGetDashboard_thenReturnResponse() throws JoseException {

        // given
        // 2 OWN & 12 SUPPLIER & 0 CUSTOMER asBuilt assets
        assetsSupport.defaultMultipleAssetsAsBuiltStored();
        // 1 OWN & 11 SUPPLIER & 0 CUSTOMER asPlanned assets
        assetsSupport.assetsAsPlannedStored("/testdata/irs_assets_as_planned_v4_long_list.json");

        // store one received investigation per status
        investigationNotificationsSupport.storedNotifications(
                Arrays.stream(NotificationStatusBaseEntity.values())
                        .map(status -> InvestigationTestDataFactory.createInvestigationTestData(
                                NotificationSideBaseEntity.RECEIVER,
                                status,
                                bpnSupport.testBpn()))
                        .toArray(InvestigationNotificationEntity[]::new)
        );
        // store one send investigation per status
        investigationNotificationsSupport.storedNotifications(
                Arrays.stream(NotificationStatusBaseEntity.values())
                        .map(status -> InvestigationTestDataFactory.createInvestigationTestData(
                                NotificationSideBaseEntity.SENDER,
                                status,
                                bpnSupport.testBpn()))
                        .toArray(InvestigationNotificationEntity[]::new)
        );

        // store one received alert per status
        alertNotificationsSupport.storedAlertNotifications(
                Arrays.stream(NotificationStatusBaseEntity.values())
                        .map(status -> AlertTestDataFactory.createAlertTestData(
                                NotificationSideBaseEntity.RECEIVER,
                                status,
                                bpnSupport.testBpn()))
                        .toArray(AlertNotificationEntity[]::new)
        );
        // store one send alert per status
        alertNotificationsSupport.storedAlertNotifications(
                Arrays.stream(NotificationStatusBaseEntity.values())
                        .map(status -> AlertTestDataFactory.createAlertTestData(
                                NotificationSideBaseEntity.SENDER,
                                status,
                                bpnSupport.testBpn()))
                        .toArray(AlertNotificationEntity[]::new)
        );

        // assert amount of given entities before
        assetsSupport.assertAssetAsBuiltSize(14);
        assetsSupport.assertAssetAsPlannedSize(12);
        investigationNotificationsSupport.assertNotificationsSize(16);
        alertNotificationsSupport.assertAlertNotificationsSize(16);

        // when/then
        given()
                .header(oAuth2Support.jwtAuthorization(ADMIN))
                .contentType(ContentType.JSON)
                .log().all()
                .when().get("/api/dashboard")
                .then()
                .log().all()
                .statusCode(200)
                .body("myParts", equalTo(3))
                .body("otherParts", equalTo(23))
                .body("investigationsReceived", equalTo(6))
                .body("alertsReceived", equalTo(6));
    }
}
