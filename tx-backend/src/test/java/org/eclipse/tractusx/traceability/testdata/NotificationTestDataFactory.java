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

import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class NotificationTestDataFactory {

    public static QualityNotificationMessage createQualityNotificationMessageTestData() {
        List<QualityNotificationAffectedPart> affectedParts = List.of(new QualityNotificationAffectedPart("partId"));

        return QualityNotificationMessage.builder()
                .id("123")
                .notificationReferenceId("id123")
                .createdBy("senderBPN")
                .createdByName("senderManufacturerName")
                .sendTo("recipientBPN")
                .sendToName("receiverManufacturerName")
                .edcUrl("senderAddress")
                .contractAgreementId("agreement")
                .description("123")
                .notificationStatus(QualityNotificationStatus.ACKNOWLEDGED)
                .affectedParts(affectedParts)
                .severity(QualityNotificationSeverity.MINOR)
                .edcNotificationId("123")
                .targetDate(Instant.parse("2022-03-01T12:00:00Z"))
                .messageId("messageId")
                .isInitial(true)
                .build();
    }

    private static QualityNotificationMessage createReceivedNotificationMessageTestData() {

        return QualityNotificationMessage.builder()
                .id(UUID.randomUUID().toString())
                .created(LocalDateTime.now().minusDays(2))
                .createdBy("BPN01")
                .createdByName("Company1")
                .sendTo("BPN02")
                .sendToName("Company2")
                .severity(QualityNotificationSeverity.CRITICAL)
                .notificationStatus(QualityNotificationStatus.RECEIVED)
                .targetDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();
    }

    private static QualityNotificationMessage createAcknowledgedNotificationMessageTestData() {

        return QualityNotificationMessage.builder()
                .id(UUID.randomUUID().toString())
                .created(LocalDateTime.now().minusDays(1))
                .createdBy("BPN02")
                .createdByName("Company2")
                .sendTo("BPN01")
                .sendToName("Company1")
                .severity(QualityNotificationSeverity.CRITICAL)
                .notificationStatus(QualityNotificationStatus.ACKNOWLEDGED)
                .targetDate(Instant.now().plus(1, ChronoUnit.DAYS))
                .build();
    }

    public static QualityNotification createQualityNotificationTestData() {

        return QualityNotification.builder()
                .notificationId(new QualityNotificationId(1L))
                .notificationStatus(QualityNotificationStatus.ACKNOWLEDGED)
                .description("test")
                .notifications(List.of(createAcknowledgedNotificationMessageTestData(), createReceivedNotificationMessageTestData()))
                .createdAt(Instant.now().minus(2, ChronoUnit.DAYS))
                .assetIds(List.of("1"))
                .notificationSide(QualityNotificationSide.RECEIVER)
                .build();
    }
}
