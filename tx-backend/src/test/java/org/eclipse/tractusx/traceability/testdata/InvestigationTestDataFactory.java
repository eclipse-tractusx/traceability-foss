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
import org.eclipse.tractusx.traceability.notification.domain.base.model.Notification;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationAffectedPart;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationId;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSeverity;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSide;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
                .description(description)
                .createdAt(createdAt)
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
}
