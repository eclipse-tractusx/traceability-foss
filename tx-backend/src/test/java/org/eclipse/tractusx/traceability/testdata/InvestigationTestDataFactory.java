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

import org.eclipse.tractusx.traceability.common.model.*;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.investigation.model.InvestigationNotificationEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationSideBaseEntity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.model.NotificationStatusBaseEntity;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class InvestigationTestDataFactory {
    public static QualityNotification createInvestigationTestData(QualityNotificationStatus investigationStatus, final String bpnString) {
        QualityNotificationId investigationId = new QualityNotificationId(1L);
        BPN bpn = new BPN(bpnString);
        QualityNotificationSide investigationSide = QualityNotificationSide.SENDER;
        String description = "Test Investigation";
        Instant createdAt = Instant.now();
        List<String> assetIds = List.of("asset123", "asset456");

        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .id("1")
                .notificationReferenceId("notificationId")
                .createdBy("senderBPN")
                .createdByName("senderManufacturerName")
                .sendTo("recipientBPN")
                .sendToName("receiverManufacturerName")
                .edcUrl("senderAddress")
                .contractAgreementId("agreement")
                .description(description)
                .notificationStatus(investigationStatus)
                .affectedParts(List.of(new QualityNotificationAffectedPart("part123")))
                .targetDate(Instant.now())
                .severity(QualityNotificationSeverity.MINOR)
                .edcNotificationId("1")
                .messageId("messageId")
                .isInitial(true)
                .build();
        List<QualityNotificationMessage> notifications = List.of(notification);

        return QualityNotification.builder()
                .notificationId(investigationId)
                .bpn(bpn)
                .notificationStatus(investigationStatus)
                .notificationSide(investigationSide)
                .description(description)
                .createdAt(createdAt)
                .assetIds(assetIds)
                .notifications(notifications)
                .build();
    }


    public static QualityNotification createInvestigationTestDataWithNotificationList(QualityNotificationStatus investigationStatus, String bpnString, List<QualityNotificationMessage> notifications) {
        QualityNotificationId investigationId = new QualityNotificationId(1L);
        BPN bpn = new BPN(bpnString);
        QualityNotificationSide investigationSide = QualityNotificationSide.SENDER;
        String description = "Test Investigation";
        Instant createdAt = Instant.now();
        List<String> assetIds = List.of("asset123", "asset456");

        return QualityNotification.builder()
                .notificationId(investigationId)
                .bpn(bpn)
                .notificationStatus(investigationStatus)
                .notificationSide(investigationSide)
                .description(description)
                .createdAt(createdAt)
                .assetIds(assetIds)
                .notifications(notifications)
                .build();
    }

    public static QualityNotification createInvestigationTestData(QualityNotificationStatus investigationStatus, QualityNotificationStatus notificationInvestigationStatus) {
        QualityNotificationId investigationId = new QualityNotificationId(1L);
        BPN bpn = new BPN("bpn123");
        QualityNotificationSide investigationSide = QualityNotificationSide.SENDER;
        String description = "Test Investigation";
        Instant createdAt = Instant.now();
        List<String> assetIds = List.of("asset123", "asset456");

        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .id("1")
                .notificationReferenceId("notificationId")
                .createdBy("senderBPN")
                .createdByName("senderManufacturerName")
                .sendTo("recipientBPN")
                .sendToName("receiverManufacturerName")
                .edcUrl("senderAddress")
                .contractAgreementId("agreement")
                .description(description)
                .notificationStatus(notificationInvestigationStatus)
                .affectedParts(List.of(new QualityNotificationAffectedPart("part123")))
                .severity(QualityNotificationSeverity.MINOR)
                .edcNotificationId("123")
                .messageId("messageId")
                .isInitial(true)
                .build();
        List<QualityNotificationMessage> notifications = List.of(notification);

        return QualityNotification.builder()
                .notificationId(investigationId)
                .bpn(bpn)
                .notificationStatus(investigationStatus)
                .notificationSide(investigationSide)
                .description(description)
                .createdAt(createdAt)
                .assetIds(assetIds)
                .notifications(notifications)
                .build();
    }

    public static QualityNotification createInvestigationTestData(QualityNotificationSide investigationSide) {
        QualityNotificationId investigationId = new QualityNotificationId(1L);
        BPN bpn = new BPN("bpn123");
        String description = "Test Investigation";
        Instant createdAt = Instant.now();
        List<String> assetIds = List.of("asset123", "asset456");


        QualityNotificationMessage notification = QualityNotificationMessage.builder()
                .id("1")
                .notificationReferenceId("notificationId")
                .createdBy("senderBPN")
                .createdByName("senderManufacturerName")
                .sendTo("recipientBPN")
                .sendToName("receiverManufacturerName")
                .edcUrl("senderAddress")
                .contractAgreementId("agreement")
                .description(description)
                .notificationStatus(QualityNotificationStatus.ACKNOWLEDGED)
                .affectedParts(List.of(new QualityNotificationAffectedPart("part123")))
                .severity(QualityNotificationSeverity.MINOR)
                .edcNotificationId("123")
                .messageId("messageId")
                .isInitial(true)
                .build();
        List<QualityNotificationMessage> notifications = List.of(notification);

        return QualityNotification.builder()
                .notificationId(investigationId)
                .bpn(bpn)
                .notificationStatus(QualityNotificationStatus.ACKNOWLEDGED)
                .notificationSide(investigationSide)
                .description(description)
                .createdAt(createdAt)
                .assetIds(assetIds)
                .notifications(notifications)
                .build();
    }

    public static SearchCriteria createSearchCriteria() {
        SearchCriteriaFilter searchCriteriaFilter = SearchCriteriaFilter.builder()
                                                    .key("sendToName")
                                                    .strategy(SearchStrategy.EQUAL)
                                                    .value("receiverManufacturerName")
                                                    .build();
        SearchCriteria searchCriteria = SearchCriteria.builder()
                                        .searchCriteriaFilterList(List.of(searchCriteriaFilter))
                                        .searchCriteriaOperator(SearchCriteriaOperator.AND)
                                        .build();

        return searchCriteria;
    }

    private static InvestigationEntity[] createSenderMajorityInvestigationEntitiesTestData(String senderBpn) {
        Instant now = Instant.now();

        InvestigationEntity firstInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .status(NotificationStatusBaseEntity.CREATED)
                .description("First Investigation on Asset1")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.minusSeconds(10L))
                .build();
        InvestigationEntity secondInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .status(NotificationStatusBaseEntity.SENT)
                .description("Second Investigation on Asset2")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(21L))
                .build();
        InvestigationEntity thirdInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("Third Investigation on Asset3")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now)
                .build();
        InvestigationEntity fourthInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                .description("Fourth Investigation on Asset4")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(20L))
                .build();
        InvestigationEntity fifthInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .status(NotificationStatusBaseEntity.CANCELED)
                .description("Fifth Investigation on Asset5")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(40L))
                .build();

        InvestigationEntity[] InvestigationEntities = {firstInvestigation, secondInvestigation, thirdInvestigation, fourthInvestigation, fifthInvestigation};
        return InvestigationEntities;
    }

    public static InvestigationNotificationEntity[] createSenderMajorityInvestigationNotificationEntitiesTestData(String senderBpn) {
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

        InvestigationEntity[] investigationEntities = createSenderMajorityInvestigationEntitiesTestData(senderBpn);

        InvestigationNotificationEntity[] investigationNotificationEntities = {
                InvestigationNotificationEntity
                        .builder()
                        .id("1")
                        .investigation(investigationEntities[0])
                        .status(NotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .targetDate(targetDateInNov1)
                        .sendToName("OEM1")
                        .severity(QualityNotificationSeverity.MAJOR)
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("2")
                        .investigation(investigationEntities[1])
                        .status(NotificationStatusBaseEntity.SENT)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .targetDate(targetDateInDec)
                        .sendToName("OEM1")
                        .severity(QualityNotificationSeverity.CRITICAL)
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("3")
                        .investigation(investigationEntities[2])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000002")
                        .createdBy("BPNL00000000000A")
                        .targetDate(targetDateInNov2)
                        .sendToName("OEM2")
                        .severity(QualityNotificationSeverity.LIFE_THREATENING)
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("4")
                        .investigation(investigationEntities[3])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000003")
                        .createdBy("BPNL00000000000A")
                        .targetDate(targetDateInDec)
                        .sendToName("OEM3")
                        .severity(QualityNotificationSeverity.MINOR)
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("5")
                        .investigation(investigationEntities[4])
                        .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000004")
                        .createdBy("BPNL00000000000A")
                        .targetDate(targetDateInNov1)
                        .sendToName("OEM4")
                        .severity(QualityNotificationSeverity.MINOR)
                        .build()
        };

        return investigationNotificationEntities;
    }

    private static InvestigationEntity[] createReceiverMajorityInvestigationEntitiesTestData(String senderBpn) {
        Instant now = Instant.now();

        InvestigationEntity firstInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .status(NotificationStatusBaseEntity.RECEIVED)
                .description("First Investigation on Asset1")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.minusSeconds(10L))
                .build();
        InvestigationEntity secondInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                .description("Second Investigation on Asset2")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(21L))
                .build();
        InvestigationEntity thirdInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .status(NotificationStatusBaseEntity.ACCEPTED)
                .description("Third Investigation on Asset3")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now)
                .build();
        InvestigationEntity fourthInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .status(NotificationStatusBaseEntity.CLOSED)
                .description("Fourth Investigation on Asset4")
                .side(NotificationSideBaseEntity.RECEIVER)
                .createdDate(now.plusSeconds(20L))
                .build();
        InvestigationEntity fifthInvestigation = InvestigationEntity.builder()
                .assets(Collections.emptyList())
                .bpn(senderBpn)
                .status(NotificationStatusBaseEntity.CANCELED)
                .description("Fifth Investigation on Asset5")
                .side(NotificationSideBaseEntity.SENDER)
                .createdDate(now.plusSeconds(40L))
                .build();

        InvestigationEntity[] InvestigationEntities = {firstInvestigation, secondInvestigation, thirdInvestigation, fourthInvestigation, fifthInvestigation};
        return InvestigationEntities;
    }

    public static InvestigationNotificationEntity[] createReceiverMajorityInvestigationNotificationEntitiesTestData(String senderBpn) {
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

        InvestigationEntity[] investigationEntities = createReceiverMajorityInvestigationEntitiesTestData(senderBpn);

        InvestigationNotificationEntity[] investigationNotificationEntities = {
                InvestigationNotificationEntity
                        .builder()
                        .id("1")
                        .investigation(investigationEntities[0])
                        .status(NotificationStatusBaseEntity.CREATED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .targetDate(targetDateInNov1)
                        .sendToName("OEM1")
                        .severity(QualityNotificationSeverity.MAJOR)
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("2")
                        .investigation(investigationEntities[1])
                        .status(NotificationStatusBaseEntity.RECEIVED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000001")
                        .createdBy("BPNL00000000000A")
                        .targetDate(targetDateInDec)
                        .sendToName("OEM1")
                        .severity(QualityNotificationSeverity.CRITICAL)
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("3")
                        .investigation(investigationEntities[2])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000002")
                        .createdBy("BPNL00000000000A")
                        .targetDate(targetDateInNov2)
                        .sendToName("OEM2")
                        .severity(QualityNotificationSeverity.LIFE_THREATENING)
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("4")
                        .investigation(investigationEntities[3])
                        .status(NotificationStatusBaseEntity.ACCEPTED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000003")
                        .createdBy("BPNL00000000000A")
                        .targetDate(targetDateInDec)
                        .sendToName("OEM3")
                        .severity(QualityNotificationSeverity.MINOR)
                        .build(),
                InvestigationNotificationEntity
                        .builder()
                        .id("5")
                        .investigation(investigationEntities[4])
                        .status(NotificationStatusBaseEntity.ACKNOWLEDGED)
                        .edcNotificationId("cda2d956-fa91-4a75-bb4a-8e5ba39b268a")
                        .sendTo("BPNL000000000004")
                        .createdBy("BPNL00000000000A")
                        .targetDate(targetDateInNov1)
                        .sendToName("OEM4")
                        .severity(QualityNotificationSeverity.MINOR)
                        .build()
        };

        return investigationNotificationEntities;
    }
}
