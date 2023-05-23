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
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;

import java.time.Instant;
import java.util.List;

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
                .senderBpnNumber("senderBPN")
                .senderManufacturerName("senderManufacturerName")
                .receiverBpnNumber("recipientBPN")
                .receiverManufacturerName("receiverManufacturerName")
                .edcUrl("senderAddress")
                .contractAgreementId("agreement")
                .description(description)
                .investigationStatus(investigationStatus)
                .affectedParts(List.of(new QualityNotificationAffectedPart("part123")))
                .targetDate(Instant.now())
                .severity(QualityNotificationSeverity.MINOR)
                .edcNotificationId("1")
                .messageId("messageId")
                .isInitial(true)
                .build();
        List<QualityNotificationMessage> notifications = List.of(notification);

        return QualityNotification.builder()
                .investigationId(investigationId)
                .bpn(bpn)
                .investigationStatus(investigationStatus)
                .investigationSide(investigationSide)
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
                .investigationId(investigationId)
                .bpn(bpn)
                .investigationStatus(investigationStatus)
                .investigationSide(investigationSide)
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
                .senderBpnNumber("senderBPN")
                .senderManufacturerName("senderManufacturerName")
                .receiverBpnNumber("recipientBPN")
                .receiverManufacturerName("receiverManufacturerName")
                .edcUrl("senderAddress")
                .contractAgreementId("agreement")
                .description(description)
                .investigationStatus(notificationInvestigationStatus)
                .affectedParts(List.of(new QualityNotificationAffectedPart("part123")))
                .severity(QualityNotificationSeverity.MINOR)
                .edcNotificationId("123")
                .messageId("messageId")
                .isInitial(true)
                .build();
        List<QualityNotificationMessage> notifications = List.of(notification);

        return QualityNotification.builder()
                .investigationId(investigationId)
                .bpn(bpn)
                .investigationStatus(investigationStatus)
                .investigationSide(investigationSide)
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
                .senderBpnNumber("senderBPN")
                .senderManufacturerName("senderManufacturerName")
                .receiverBpnNumber("recipientBPN")
                .receiverManufacturerName("receiverManufacturerName")
                .edcUrl("senderAddress")
                .contractAgreementId("agreement")
                .description(description)
                .investigationStatus(QualityNotificationStatus.ACKNOWLEDGED)
                .affectedParts(List.of(new QualityNotificationAffectedPart("part123")))
                .severity(QualityNotificationSeverity.MINOR)
                .edcNotificationId("123")
                .messageId("messageId")
                .isInitial(true)
                .build();
        List<QualityNotificationMessage> notifications = List.of(notification);

        return QualityNotification.builder()
                .investigationId(investigationId)
                .bpn(bpn)
                .investigationStatus(QualityNotificationStatus.ACKNOWLEDGED)
                .investigationSide(investigationSide)
                .description(description)
                .createdAt(createdAt)
                .assetIds(assetIds)
                .notifications(notifications)
                .build();
    }
}
