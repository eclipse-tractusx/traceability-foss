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

package org.eclipse.tractusx.traceability.integration.edc.blackbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.ContentType;
import org.eclipse.tractusx.traceability.assets.infrastructure.asbuilt.model.AssetAsBuiltEntity;
import org.eclipse.tractusx.traceability.common.security.JwtRole;
import org.eclipse.tractusx.traceability.integration.IntegrationTestSpecification;
import org.eclipse.tractusx.traceability.integration.common.support.AssetsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationNotificationsSupport;
import org.eclipse.tractusx.traceability.integration.common.support.InvestigationsSupport;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotification;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationStatusBaseEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationTypeEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationEntity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.NotificationMessageEntity;
import org.jose4j.lang.JoseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;

class EdcControllerIT extends IntegrationTestSpecification {
    @Autowired
    AssetsSupport assetsSupport;
    @Autowired
    InvestigationNotificationsSupport investigationNotificationsSupport;
    @Autowired
    InvestigationsSupport investigationsSupport;

    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldRejectInvestigationNotificationOnAPICallClass_whenAppBpnEqualsSenderBpn() throws IOException, JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        String notificationJson = readFile("/testdata/edc_notification_okay.json");
        EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        // when/then
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .when()
                .post("/api/internal/qualitynotifications/receive")
                .then()
                .statusCode(400);

        // then
        investigationNotificationsSupport.assertNotificationsSize(0);
        investigationsSupport.assertInvestigationsSize(0);
    }

    @Test
    void shouldCreateAnInvestigationOnApiCallbackBadRequestBpnDoesNotMatchAppBpn() throws IOException, JoseException {
        // given
        String notificationJson = readFile("/testdata/edc_notification_wrong_bpn.json");
        EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);

        // when
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .when()
                .post("/api/internal/qualitynotifications/receive")
                .then()
                .statusCode(400);

        // then
        investigationNotificationsSupport.assertNotificationsSize(0);
        investigationsSupport.assertInvestigationsSize(0);
    }

    @Test
    void shouldAddANotificationToExistingInvestigationOnAPICallback() throws IOException, JoseException {
        // given
        assetsSupport.defaultAssetsStored();

        AssetAsBuiltEntity assetAsBuiltEntity = assetsSupport.findById("urn:uuid:d387fa8e-603c-42bd-98c3-4d87fef8d2bb");

        NotificationMessageEntity notification = NotificationMessageEntity
                .builder()
                .id("1")
                .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                .status(NotificationStatusBaseEntity.CREATED)
                .assets(List.of(assetAsBuiltEntity))
                .build();

        NotificationMessageEntity notificationSent = NotificationMessageEntity
                .builder()
                .id("2")
                .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                .status(NotificationStatusBaseEntity.SENT)
                .assets(List.of(assetAsBuiltEntity))
                .build();

        NotificationEntity investigation = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn("BPNL00000003AXB3")
                .status(NotificationStatusBaseEntity.SENT)
                .type(NotificationTypeEntity.INVESTIGATION)
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(Instant.now())
                .initialReceiverBpn("BPNL00000003AXB4")
                .build();

        NotificationEntity persistedInvestigation = investigationsSupport.storedInvestigationFullObject(investigation);

        NotificationMessageEntity notificationEntity = investigationNotificationsSupport.storedNotification(notification);
        notificationEntity.setNotification(persistedInvestigation);

        NotificationMessageEntity notificationEntitySent = investigationNotificationsSupport.storedNotification(notificationSent);
        notificationEntitySent.setNotification(persistedInvestigation);

        NotificationMessageEntity persistedNotification = investigationNotificationsSupport.storedNotification(notificationEntity);
        NotificationMessageEntity persistedNotificationSent = investigationNotificationsSupport.storedNotification(notificationEntitySent);
        List<NotificationMessageEntity> notificationEntities = new ArrayList<>();
        notificationEntities.add(persistedNotificationSent);
        notificationEntities.add(persistedNotification);
        investigation.setMessages(notificationEntities);

        investigationsSupport.storedInvestigationFullObject(investigation);

        String notificationJson = readFile("/testdata/edc_notification_okay_update.json").replaceAll("REPLACE_ME", notificationEntity.getEdcNotificationId());
        EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);


        // when
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .when()
                .post("/api/internal/qualitynotifications/update")
                .then()
                .statusCode(200);

        // then
        investigationNotificationsSupport.assertNotificationsSize(3);
        investigationsSupport.assertInvestigationsSize(1);
        investigationsSupport.assertInvestigationStatus(NotificationStatus.ACKNOWLEDGED);
    }

    @Test
    void shouldThrowBadRequestBecauseEdcNotificationMethodIsNotSupported() throws IOException, JoseException {
        // given
        assetsSupport.defaultAssetsStored();
        NotificationMessageEntity notification = NotificationMessageEntity
                .builder()
                .id("1")
                .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                .build();


        NotificationEntity investigation = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn("BPNL00000003AXS3")
                .status(NotificationStatusBaseEntity.SENT)
                .type(NotificationTypeEntity.INVESTIGATION)
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(Instant.now())
                .initialReceiverBpn("BPNL00000003AXB4")
                .build();

        NotificationEntity persistedInvestigation = investigationsSupport.storedInvestigationFullObject(investigation);

        NotificationMessageEntity notificationEntity = investigationNotificationsSupport.storedNotification(notification);
        notificationEntity.setNotification(persistedInvestigation);
        NotificationMessageEntity persistedNotification = investigationNotificationsSupport.storedNotification(notificationEntity);

        investigation.setMessages(List.of(persistedNotification));

        investigationsSupport.storedInvestigationFullObject(investigation);


        String notificationJson = readFile("/testdata/edc_notification_classification_unsupported.json").replaceAll("REPLACE_ME", notificationEntity.getEdcNotificationId());
        EDCNotification edcNotification = objectMapper.readValue(notificationJson, EDCNotification.class);


        // when
        given()
                .contentType(ContentType.JSON)
                .body(edcNotification)
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .when()
                .post("/api/internal/qualitynotifications/receive")
                .then()
                .statusCode(400);

        // then
        investigationNotificationsSupport.assertNotificationsSize(1);

    }

    @Test
    void shouldCallUpdateApiWithWrongRequestObject() throws JoseException {
        // given
        NotificationEntity investigation = NotificationEntity.builder()
                .assets(Collections.emptyList())
                .bpn("BPNL00000003AXS3")
                .type(NotificationTypeEntity.INVESTIGATION)
                .status(NotificationStatusBaseEntity.RECEIVED)
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(Instant.now())
                .initialReceiverBpn("BPNL00000003AXB4")
                .build();

        investigationsSupport.storedInvestigationFullObject(investigation);

        // when
        given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "\t\"header\": {\n" +
                        "\t\t\"notificationId\": \"notificationReferenceId\",\n" +
                        "\t\t\"senderBPN\": \"NOT_SAME_AS_APP_BPN\",\n" +
                        "\t\t\"senderAddress\": \"https://some-url.com\",\n" +
                        "\t\t\"recipientBPN\": \"NOT_SAME_AS_APP_BPN\",\n" +
                        "\t\t\"classification\": \"QM-Investigation\",\n" +
                        "\t\t\"severity\": \"CRITICAL\",\n" +
                        "\t\t\"relatedNotificationId\": \"\",\n" +
                        "\t\t\"status\": \"CLOSED\",\n" +
                        "\t\t\"targetDate\": \"\"\n" +
                        "\t},\n" +
                        "\t\"content\": {\n" +
                        "\t\t\"information\": \"Some long description\",\n" +
                        "\t\t\"listOfAffectedItems\": [\n" +
                        "\t\t\t\"urn:uuid:171fed54-26aa-4848-a025-81aaca557f37\"\n" +
                        "\t\t]\n" +
                        "\t}\n" +
                        "}")
                .header(oAuth2Support.jwtAuthorization(JwtRole.ADMIN))
                .when()
                .post("/api/internal/qualitynotifications/update")
                .then()
                .statusCode(400);

        // then
        investigationNotificationsSupport.assertNotificationsSize(0);
        investigationsSupport.assertInvestigationsSize(1);
        investigationsSupport.assertInvestigationStatus(NotificationStatus.RECEIVED);
    }

    private String readFile(final String filePath) throws IOException {
        InputStream file = EdcControllerIT.class.getResourceAsStream(filePath);
        return new String(file.readAllBytes(), StandardCharsets.UTF_8);
    }
}
