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

package org.eclipse.tractusx.traceability.testdata;

import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSeverity;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AlertTestDataFactory {
    private static NotificationEntity[] createSenderMajorityAlertEntitiesTestData(String senderBpn) {
        Instant now = Instant.now();
        String targetDateInNovString = "12:00 PM, Sun 11/9/2025";
        String targetDateInDecString = "12:00 PM, Tue 12/9/2025";
        String dateFormatter = "hh:mm a, EEE M/d/uuuu";
        Instant targetDateInNov = LocalDateTime.parse(targetDateInNovString, DateTimeFormatter.ofPattern(dateFormatter, Locale.US))
                .atZone(ZoneId.of("Europe/Berlin"))
                .toInstant();
        Instant targetDateInDec = LocalDateTime.parse(targetDateInDecString, DateTimeFormatter.ofPattern(dateFormatter, Locale.US))
                .atZone(ZoneId.of("Europe/Berlin"))
                .toInstant();

        NotificationEntity firstAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov)
                .severity(NotificationSeverityBaseEntity.MAJOR)
                .status(NotificationStatusBaseEntity.CREATED)
                .description("First Alert on Asset1")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.minusSeconds(10L))
                .build();
        NotificationEntity secondAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInDec)
                .severity(NotificationSeverityBaseEntity.CRITICAL)
                .status(NotificationStatusBaseEntity.SENT)
                .description("Second Alert on Asset2")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(21L))
                .build();
        NotificationEntity thirdAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov)
                .severity(NotificationSeverityBaseEntity.LIFE_THREATENING)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("Third Alert on Asset3")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now)
                .build();
        NotificationEntity fourthAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInDec)
                .severity(NotificationSeverityBaseEntity.MINOR)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("Fourth Alert on Asset4")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(20L))
                .build();
        NotificationEntity fifthAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov)
                .severity(NotificationSeverityBaseEntity.MINOR)
                .status(NotificationStatusBaseEntity.CANCELED)
                .description("Fifth Alert on Asset5")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(40L))
                .build();

        NotificationEntity[] alertEntities = {firstAlert, secondAlert, thirdAlert, fourthAlert, fifthAlert};
        return alertEntities;
    }

    public static NotificationMessageEntity[] createSenderMajorityAlertNotificationEntitiesTestData(String senderBpn) {
        NotificationEntity[] alertEntities = createSenderMajorityAlertEntitiesTestData(senderBpn);

        NotificationMessageEntity[] alertNotificationEntities = {
                NotificationMessageEntity
                        .builder()
                        .id("1")
                        .notification(alertEntities[0])
                        .status(NotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM1")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("2")
                        .notification(alertEntities[1])
                        .status(NotificationStatusBaseEntity.SENT)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM1")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("3")
                        .notification(alertEntities[2])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000002")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM2")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("4")
                        .notification(alertEntities[3])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000003")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM3")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("5")
                        .notification(alertEntities[4])
                        .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000004")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM4")
                        .build()
        };

        return alertNotificationEntities;
    }

    private static NotificationEntity[] createReceiverMajorityAlertEntitiesTestData(String senderBpn) {
        Instant now = Instant.now();
        String targetDateInNovString = "12:00 PM, Sun 11/9/2025";
        String targetDateInDecString = "12:00 PM, Tue 12/9/2025";
        String dateFormatter = "hh:mm a, EEE M/d/uuuu";
        Instant targetDateInNov = LocalDateTime.parse(targetDateInNovString, DateTimeFormatter.ofPattern(dateFormatter, Locale.US))
                .atZone(ZoneId.of("Europe/Berlin"))
                .toInstant();
        Instant targetDateInDec = LocalDateTime.parse(targetDateInDecString, DateTimeFormatter.ofPattern(dateFormatter, Locale.US))
                .atZone(ZoneId.of("Europe/Berlin"))
                .toInstant();

        NotificationEntity firstAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov)
                .severity(NotificationSeverityBaseEntity.MAJOR)
                .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                .description("First Alert on Asset1")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.minusSeconds(10L))
                .build();
        NotificationEntity secondAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInDec)
                .severity(NotificationSeverityBaseEntity.CRITICAL)
                .status(NotificationStatusBaseEntity.RECEIVED)
                .description("Second Alert on Asset2")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(21L))
                .build();
        NotificationEntity thirdAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov)
                .severity(NotificationSeverityBaseEntity.LIFE_THREATENING)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("Third Alert on Asset3")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now)
                .build();
        NotificationEntity fourthAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInDec)
                .severity(NotificationSeverityBaseEntity.MINOR)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("Fourth Alert on Asset4")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(20L))
                .build();
        NotificationEntity fifthAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov)
                .severity(NotificationSeverityBaseEntity.MINOR)
                .status(NotificationStatusBaseEntity.CANCELED)
                .description("Fifth Alert on Asset5")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(40L))
                .build();

        NotificationEntity[] alertEntities = {firstAlert, secondAlert, thirdAlert, fourthAlert, fifthAlert};
        return alertEntities;
    }

    public static NotificationMessageEntity[] createReceiverMajorityAlertNotificationEntitiesTestData(String senderBpn) {
        NotificationEntity[] alertEntities = createReceiverMajorityAlertEntitiesTestData(senderBpn);

        NotificationMessageEntity[] alertNotificationEntities = {
                NotificationMessageEntity
                        .builder()
                        .id("1")
                        .notification(alertEntities[0])
                        .status(NotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM1")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("2")
                        .notification(alertEntities[1])
                        .status(NotificationStatusBaseEntity.SENT)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM1")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("3")
                        .notification(alertEntities[2])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000002")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM2")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("4")
                        .notification(alertEntities[3])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000003")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM3")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("5")
                        .notification(alertEntities[4])
                        .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000004")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM4")
                        .build()
        };

        return alertNotificationEntities;
    }

    public static NotificationMessageEntity[] createExtendedReceiverAlertNotificationEntitiesTestData(String senderBpn) {
        NotificationEntity[] alertEntities = createExtendedReceiverAlertEntitiesTestData(senderBpn);

        NotificationMessageEntity[] alertNotificationEntities = {
                NotificationMessageEntity
                        .builder()
                        .id("6")
                        .notification(alertEntities[0])
                        .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM1")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("7")
                        .notification(alertEntities[1])
                        .status(NotificationStatusBaseEntity.RECEIVED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM1")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("8")
                        .notification(alertEntities[2])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000002")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM2")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("9")
                        .notification(alertEntities[3])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000003")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM3")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("10")
                        .notification(alertEntities[4])
                        .status(NotificationStatusBaseEntity.CANCELED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000004")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM4")
                        .build()
        };

        return alertNotificationEntities;
    }

    private static NotificationEntity[] createExtendedReceiverAlertEntitiesTestData(String senderBpn) {
        Instant now = Instant.now();
        String targetDateInNovString = "12:00 PM, Sun 11/9/2025";
        String targetDateInDecString = "12:00 PM, Tue 12/9/2025";
        String dateFormatter = "hh:mm a, EEE M/d/uuuu";
        Instant targetDateInNov = LocalDateTime.parse(targetDateInNovString, DateTimeFormatter.ofPattern(dateFormatter, Locale.US))
                .atZone(ZoneId.of("Europe/Berlin"))
                .toInstant();
        Instant targetDateInDec = LocalDateTime.parse(targetDateInDecString, DateTimeFormatter.ofPattern(dateFormatter, Locale.US))
                .atZone(ZoneId.of("Europe/Berlin"))
                .toInstant();

        NotificationEntity firstAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov)
                .severity(NotificationSeverityBaseEntity.MAJOR)
                .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                .description("Sixth Alert on Asset1")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.minusSeconds(100L))
                .build();
        NotificationEntity secondAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInDec)
                .severity(NotificationSeverityBaseEntity.CRITICAL)
                .status(NotificationStatusBaseEntity.RECEIVED)
                .description("Seventh Alert on Asset2")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(210L))
                .build();
        NotificationEntity thirdAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov)
                .severity(NotificationSeverityBaseEntity.LIFE_THREATENING)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("Eighth Alert on Asset3")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(1L))
                .build();
        NotificationEntity fourthAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInDec)
                .severity(NotificationSeverityBaseEntity.MINOR)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("Ninth Alert on Asset4")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(25L))
                .build();
        NotificationEntity fifthAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov)
                .severity(NotificationSeverityBaseEntity.MINOR)
                .status(NotificationStatusBaseEntity.CANCELED)
                .description("Tenth Alert on Asset5")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(80L))
                .build();


        return new NotificationEntity[]{firstAlert, secondAlert, thirdAlert, fourthAlert, fifthAlert};
    }

    public static NotificationMessageEntity[] createExtendedSenderAlertNotificationEntitiesTestData(String senderBpn) {
        NotificationEntity[] alertEntities = createExtendedSenderAlertEntitiesTestData(senderBpn);

        NotificationMessageEntity[] alertNotificationEntities = {
                NotificationMessageEntity
                        .builder()
                        .id("6")
                        .notification(alertEntities[0])
                        .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("7")
                        .notification(alertEntities[1])
                        .status(NotificationStatusBaseEntity.SENT)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM1")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("8")
                        .notification(alertEntities[2])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000002")
                        .createdBy("BPNL00000000000A")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("9")
                        .notification(alertEntities[3])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000003")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM3")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("10")
                        .notification(alertEntities[4])
                        .status(NotificationStatusBaseEntity.CANCELED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000004")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM4")
                        .build()
        };

        return alertNotificationEntities;
    }

    private static NotificationEntity[] createExtendedSenderAlertEntitiesTestData(String senderBpn) {
        Instant now = Instant.now();
        String targetDateInNovString = "12:00 PM, Sun 11/9/2025";
        String targetDateInDecString = "12:00 PM, Tue 12/9/2025";
        String dateFormatter = "hh:mm a, EEE M/d/uuuu";
        Instant targetDateInNov = LocalDateTime.parse(targetDateInNovString, DateTimeFormatter.ofPattern(dateFormatter, Locale.US))
                .atZone(ZoneId.of("Europe/Berlin"))
                .toInstant();
        Instant targetDateInDec = LocalDateTime.parse(targetDateInDecString, DateTimeFormatter.ofPattern(dateFormatter, Locale.US))
                .atZone(ZoneId.of("Europe/Berlin"))
                .toInstant();

        NotificationEntity firstAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov)
                .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                .description("Sixth Alert on Asset1")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.minusSeconds(100L))
                .build();
        NotificationEntity secondAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInDec)
                .severity(NotificationSeverityBaseEntity.CRITICAL)
                .status(NotificationStatusBaseEntity.SENT)
                .description("Seventh Alert on Asset2")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(210L))
                .build();
        NotificationEntity thirdAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("Eighth Alert on Asset3")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(1L))
                .build();
        NotificationEntity fourthAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInDec)
                .severity(NotificationSeverityBaseEntity.MINOR)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("Ninth Alert on Asset4")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(25L))
                .build();
        NotificationEntity fifthAlert = NotificationEntity.builder()
                .type(NotificationTypeEntity.ALERT)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov)
                .severity(NotificationSeverityBaseEntity.MINOR)
                .status(NotificationStatusBaseEntity.CANCELED)
                .description("Tenth Alert on Asset5")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(80L))
                .build();

        return new NotificationEntity[]{firstAlert, secondAlert, thirdAlert, fourthAlert, fifthAlert};
    }
}
