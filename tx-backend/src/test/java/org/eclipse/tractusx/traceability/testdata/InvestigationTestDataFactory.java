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

import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.notification.domain.base.model.*;
import org.eclipse.tractusx.traceability.notification.infrastructure.notification.model.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class InvestigationTestDataFactory {
    public static Notification createInvestigationTestData(NotificationStatus investigationStatus, final String bpnString) {
        NotificationId investigationId = new NotificationId(1L);
        BPN bpn = new BPN(bpnString);
        NotificationSide investigationSide = NotificationSide.SENDER;
        String description = "Test Investigation";
        Instant createdAt = Instant.now();
        List<String> assetIds = List.of("asset123", "asset456");

        NotificationMessage notification = NotificationMessage.builder()
                .id("1")
                .notificationReferenceId("notificationId")
                .sentBy("senderBPN")
                .sentByName("senderManufacturerName")
                .sentTo("recipientBPN")
                .sendToName("receiverManufacturerName")
                .contractAgreementId("agreement")
                .message(description)
                .notificationStatus(investigationStatus)
                .affectedParts(List.of(new NotificationAffectedPart("part123")))
                .edcNotificationId("1")
                .messageId("messageId")
                .build();
        List<NotificationMessage> notifications = List.of(notification);

        return Notification.builder()
                .notificationId(investigationId)
                .bpn(bpn)
                .notificationStatus(investigationStatus)
                .notificationSide(investigationSide)
                .description(description)
                .notificationType(NotificationType.INVESTIGATION)
                .severity(NotificationSeverity.MINOR)
                .createdAt(createdAt)
                .affectedPartIds(assetIds)
                .notifications(notifications)
                .build();
    }


    public static Notification createInvestigationTestDataWithNotificationList(NotificationStatus investigationStatus, String bpnString, List<NotificationMessage> notifications) {
        NotificationId investigationId = new NotificationId(1L);
        BPN bpn = new BPN(bpnString);
        NotificationSide investigationSide = NotificationSide.SENDER;
        String description = "Test Investigation";
        Instant createdAt = Instant.now();
        List<String> assetIds = List.of("asset123", "asset456");

        return Notification.builder()
                .notificationId(investigationId)
                .bpn(bpn)
                .notificationStatus(investigationStatus)
                .notificationSide(investigationSide)
                .description(description)
                .createdAt(createdAt)
                .affectedPartIds(assetIds)
                .notifications(notifications)
                .build();
    }

    public static Notification createInvestigationTestData(NotificationStatus investigationStatus, NotificationStatus notificationInvestigationStatus) {
        NotificationId investigationId = new NotificationId(1L);
        BPN bpn = new BPN("bpn123");
        NotificationSide investigationSide = NotificationSide.SENDER;
        String description = "Test Investigation";
        Instant createdAt = Instant.now();
        List<String> assetIds = List.of("asset123", "asset456");

        NotificationMessage notification = NotificationMessage.builder()
                .id("1")
                .notificationReferenceId("notificationId")
                .sentBy("senderBPN")
                .sentByName("senderManufacturerName")
                .sentTo("recipientBPN")
                .sendToName("receiverManufacturerName")
                .contractAgreementId("agreement")
                .message(description)
                .notificationStatus(notificationInvestigationStatus)
                .type(NotificationType.INVESTIGATION)
                .affectedParts(List.of(new NotificationAffectedPart("part123")))
                .edcNotificationId("123")
                .messageId("messageId")
                .build();

        NotificationMessage notification2 = NotificationMessage.builder()
                .id("1")
                .notificationReferenceId("notificationId")
                .sentBy("senderBPN")
                .sentByName("senderManufacturerName")
                .sentTo("recipientBPN")
                .sendToName("receiverManufacturerName")
                .contractAgreementId("agreement")
                .message(description)
                .notificationStatus(NotificationStatus.SENT)
                .type(NotificationType.INVESTIGATION)
                .affectedParts(List.of(new NotificationAffectedPart("part123")))
                .edcNotificationId("123")
                .messageId("messageId")
                .build();
        List<NotificationMessage> notifications = new ArrayList<>();
        notifications.add(notification);
        notifications.add(notification2);

        return Notification.builder()
                .notificationId(investigationId)
                .bpn(bpn)
                .notificationStatus(investigationStatus)
                .notificationSide(investigationSide)
                .sendTo("TESTBPN")
                .description(description)
                .createdAt(createdAt)
                .severity(NotificationSeverity.MINOR)
                .notificationType(NotificationType.INVESTIGATION)
                .affectedPartIds(assetIds)
                .notifications(notifications)
                .build();
    }

    public static Notification createInvestigationTestData(NotificationSide investigationSide) {
        NotificationId investigationId = new NotificationId(1L);
        BPN bpn = new BPN("bpn123");
        String description = "Test Investigation";
        Instant createdAt = Instant.now();
        List<String> assetIds = List.of("asset123", "asset456");


        NotificationMessage notification = NotificationMessage.builder()
                .id("1")
                .notificationReferenceId("notificationId")
                .sentBy("senderBPN")
                .sentByName("senderManufacturerName")
                .sentTo("recipientBPN")
                .sendToName("receiverManufacturerName")
                .contractAgreementId("agreement")
                .message(description)
                .notificationStatus(NotificationStatus.ACKNOWLEDGED)
                .affectedParts(List.of(new NotificationAffectedPart("part123")))
                .edcNotificationId("123")
                .messageId("messageId")
                .build();
        List<NotificationMessage> notifications = List.of(notification);

        return Notification.builder()
                .notificationId(investigationId)
                .bpn(bpn)
                .notificationStatus(NotificationStatus.ACKNOWLEDGED)
                .notificationSide(investigationSide)
                .description(description)
                .createdAt(createdAt)
                .affectedPartIds(assetIds)
                .notifications(notifications)
                .build();
    }
    //Pooja

    private static NotificationEntity[] createSenderMajorityInvestigationEntitiesTestData(String senderBpn) {
        Instant now = Instant.now();
        String targetDateInNovString1 = "12:00 PM, Sun 11/9/2025";
        String targetDateInNovString2 = "12:00 PM, Mon 11/10/2025";
        String targetDateInDecString = "12:00 PM, Tue 12/9/2025";
        String dateFormatter = "hh:mm a, EEE M/d/uuuu";
        Instant targetDateInNov1 = LocalDateTime.parse(targetDateInNovString1, DateTimeFormatter.ofPattern(dateFormatter, Locale.US))
                .atZone(ZoneId.of("Europe/Berlin"))
                .toInstant();
        Instant targetDateInNov2 = LocalDateTime.parse(targetDateInNovString2, DateTimeFormatter.ofPattern(dateFormatter, Locale.US))
                .atZone(ZoneId.of("Europe/Berlin"))
                .toInstant();
        Instant targetDateInDec = LocalDateTime.parse(targetDateInDecString, DateTimeFormatter.ofPattern(dateFormatter, Locale.US))
                .atZone(ZoneId.of("Europe/Berlin"))
                .toInstant();

        NotificationEntity firstInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov1)
                .severity(NotificationSeverityBaseEntity.MAJOR)
                .status(NotificationStatusBaseEntity.CREATED)
                .description("First Investigation on Asset1")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.minusSeconds(10L))
                .build();
        NotificationEntity secondInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInDec)
                .severity(NotificationSeverityBaseEntity.CRITICAL)
                .status(NotificationStatusBaseEntity.SENT)
                .description("Second Investigation on Asset2")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(21L))
                .build();
        NotificationEntity thirdInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov2)
                .severity(NotificationSeverityBaseEntity.LIFE_THREATENING)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("Third Investigation on Asset3")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now)
                .build();
        NotificationEntity fourthInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInDec)
                .severity(NotificationSeverityBaseEntity.MINOR)
                .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                .description("Fourth Investigation on Asset4")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(20L))
                .build();
        NotificationEntity fifthInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov1)
                .severity(NotificationSeverityBaseEntity.MINOR)
                .status(NotificationStatusBaseEntity.CANCELED)
                .description("Fifth Investigation on Asset5")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(40L))
                .build();

        NotificationEntity[] InvestigationEntities = {firstInvestigation, secondInvestigation, thirdInvestigation, fourthInvestigation, fifthInvestigation};
        return InvestigationEntities;
    }

    public static NotificationMessageEntity[] createSenderMajorityInvestigationNotificationEntitiesTestData(String senderBpn) {
        NotificationEntity[] investigationEntities = createSenderMajorityInvestigationEntitiesTestData(senderBpn);

        NotificationMessageEntity[] investigationNotificationEntities = {
                NotificationMessageEntity
                        .builder()
                        .id("1")
                        .notification(investigationEntities[0])
                        .status(NotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM1")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("2")
                        .notification(investigationEntities[1])
                        .status(NotificationStatusBaseEntity.SENT)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM1")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("3")
                        .notification(investigationEntities[2])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000002")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM2")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("4")
                        .notification(investigationEntities[3])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000003")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM3")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("5")
                        .notification(investigationEntities[4])
                        .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000004")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM4")
                        .build()
        };

        return investigationNotificationEntities;
    }

    private static NotificationEntity[] createReceiverMajorityInvestigationEntitiesTestData(String senderBpn) {
        Instant now = Instant.now();
        String targetDateInNovString1 = "12:00 PM, Sun 11/9/2025";
        String targetDateInNovString2 = "12:00 PM, Mon 11/10/2025";
        String targetDateInDecString = "12:00 PM, Tue 12/9/2025";
        String dateFormatter = "hh:mm a, EEE M/d/uuuu";
        Instant targetDateInNov1 = LocalDateTime.parse(targetDateInNovString1, DateTimeFormatter.ofPattern(dateFormatter, Locale.US))
                .atZone(ZoneId.of("Europe/Berlin"))
                .toInstant();
        Instant targetDateInNov2 = LocalDateTime.parse(targetDateInNovString2, DateTimeFormatter.ofPattern(dateFormatter, Locale.US))
                .atZone(ZoneId.of("Europe/Berlin"))
                .toInstant();
        Instant targetDateInDec = LocalDateTime.parse(targetDateInDecString, DateTimeFormatter.ofPattern(dateFormatter, Locale.US))
                .atZone(ZoneId.of("Europe/Berlin"))
                .toInstant();

        NotificationEntity firstInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov1)
                .severity(NotificationSeverityBaseEntity.MAJOR)
                .status(NotificationStatusBaseEntity.RECEIVED)
                .description("First Investigation on Asset1")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.minusSeconds(10L))
                .build();
        NotificationEntity secondInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInDec)
                .severity(NotificationSeverityBaseEntity.CRITICAL)
                .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                .description("Second Investigation on Asset2")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(21L))
                .build();
        NotificationEntity thirdInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov2)
                .severity(NotificationSeverityBaseEntity.LIFE_THREATENING)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("Third Investigation on Asset3")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now)
                .build();
        NotificationEntity fourthInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInDec)
                .severity(NotificationSeverityBaseEntity.MINOR)
                .status(NotificationStatusBaseEntity.CLOSED)
                .description("Fourth Investigation on Asset4")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(20L))
                .build();
        NotificationEntity fifthInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov1)
                .severity(NotificationSeverityBaseEntity.MINOR)
                .status(NotificationStatusBaseEntity.CANCELED)
                .description("Fifth Investigation on Asset5")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(40L))
                .build();

        NotificationEntity[] InvestigationEntities = {firstInvestigation, secondInvestigation, thirdInvestigation, fourthInvestigation, fifthInvestigation};
        return InvestigationEntities;
    }

    public static NotificationMessageEntity[] createReceiverMajorityInvestigationNotificationEntitiesTestData(String senderBpn) {
        NotificationEntity[] investigationEntities = createReceiverMajorityInvestigationEntitiesTestData(senderBpn);

        NotificationMessageEntity[] investigationNotificationEntities = {
                NotificationMessageEntity
                        .builder()
                        .id("1")
                        .notification(investigationEntities[0])
                        .status(NotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM1")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("2")
                        .notification(investigationEntities[1])
                        .status(NotificationStatusBaseEntity.RECEIVED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM1")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("3")
                        .notification(investigationEntities[2])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000002")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM2")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("4")
                        .notification(investigationEntities[3])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000003")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM3")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("5")
                        .notification(investigationEntities[4])
                        .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000004")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM4")
                        .build()
        };

        return investigationNotificationEntities;
    }

    public static NotificationMessageEntity[] createExtendedReceiverInvestigationNotificationEntitiesTestData(String senderBpn) {
        NotificationEntity[] investigationEntities = createExtendedReceiverInvestigationEntitiesTestData(senderBpn);

        NotificationMessageEntity[] investigationNotificationEntities = {
                NotificationMessageEntity
                        .builder()
                        .id("6")
                        .notification(investigationEntities[0])
                        .status(NotificationStatusBaseEntity.RECEIVED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM1")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("7")
                        .notification(investigationEntities[1])
                        .status(NotificationStatusBaseEntity.RECEIVED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM1")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("8")
                        .notification(investigationEntities[2])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000002")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM2")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("9")
                        .notification(investigationEntities[3])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000003")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM3")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("10")
                        .notification(investigationEntities[4])
                        .status(NotificationStatusBaseEntity.CANCELED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000004")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM4")
                        .build()
        };

        return investigationNotificationEntities;
    }

    private static NotificationEntity[] createExtendedReceiverInvestigationEntitiesTestData(String senderBpn) {
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

        NotificationEntity firstInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov)
                .severity(NotificationSeverityBaseEntity.MAJOR)
                .status(NotificationStatusBaseEntity.RECEIVED)
                .description("Sixth Investigation on Asset1")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.minusSeconds(100L))
                .build();
        NotificationEntity secondInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInDec)
                .severity(NotificationSeverityBaseEntity.CRITICAL)
                .status(NotificationStatusBaseEntity.RECEIVED)
                .description("Seventh Investigation on Asset2")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(210L))
                .build();
        NotificationEntity thirdInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov)
                .severity(NotificationSeverityBaseEntity.LIFE_THREATENING)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("Eighth Investigation on Asset3")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(1L))
                .build();
        NotificationEntity fourthInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInDec)
                .severity(NotificationSeverityBaseEntity.MINOR)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("Ninth Investigation on Asset4")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(25L))
                .build();
        NotificationEntity fifthInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov)
                .severity(NotificationSeverityBaseEntity.MINOR)
                .status(NotificationStatusBaseEntity.CANCELED)
                .description("Tenth Investigation on Asset5")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(80L))
                .build();

        return new NotificationEntity[]{firstInvestigation, secondInvestigation, thirdInvestigation, fourthInvestigation, fifthInvestigation};
    }

    public static NotificationMessageEntity[] createExtendedSenderInvestigationNotificationEntitiesTestData(String senderBpn) {
        NotificationEntity[] investigationEntities = createExtendedSenderInvestigationEntitiesTestData(senderBpn);

        NotificationMessageEntity[] investigationNotificationEntities = {
                NotificationMessageEntity
                        .builder()
                        .id("6")
                        .notification(investigationEntities[0])
                        .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM1")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("7")
                        .notification(investigationEntities[1])
                        .status(NotificationStatusBaseEntity.SENT)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM1")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("8")
                        .notification(investigationEntities[2])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000002")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM2")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("9")
                        .notification(investigationEntities[3])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000003")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM3")
                        .build(),
                NotificationMessageEntity
                        .builder()
                        .id("10")
                        .notification(investigationEntities[4])
                        .status(NotificationStatusBaseEntity.CANCELED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000004")
                        .createdBy("BPNL00000000000A")
                        .sendToName("OEM4")
                        .build()
        };

        return investigationNotificationEntities;
    }
    private static NotificationEntity[] createExtendedSenderInvestigationEntitiesTestData(String senderBpn) {
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

        NotificationEntity firstInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov)
                .severity(NotificationSeverityBaseEntity.MAJOR)
                .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                .description("Sixth Investigation on Asset1")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.minusSeconds(100L))
                .build();
        NotificationEntity secondInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInDec)
                .severity(NotificationSeverityBaseEntity.CRITICAL)
                .status(NotificationStatusBaseEntity.SENT)
                .description("Seventh Investigation on Asset2")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(210L))
                .build();
        NotificationEntity thirdInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov)
                .severity(NotificationSeverityBaseEntity.LIFE_THREATENING)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("Eighth Investigation on Asset3")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(1L))
                .build();
        NotificationEntity fourthInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInDec)
                .severity(NotificationSeverityBaseEntity.MINOR)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("Ninth Investigation on Asset4")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(25L))
                .build();
        NotificationEntity fifthInvestigation = NotificationEntity.builder()
                .type(NotificationTypeEntity.INVESTIGATION)
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .targetDate(targetDateInNov)
                .severity(NotificationSeverityBaseEntity.MINOR)
                .status(NotificationStatusBaseEntity.CANCELED)
                .description("Tenth Investigation on Asset5")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(80L))
                .build();

        return new NotificationEntity[]{firstInvestigation, secondInvestigation, thirdInvestigation, fourthInvestigation, fifthInvestigation};
    }
}
