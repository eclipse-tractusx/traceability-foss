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

import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationType;

import java.time.Instant;
import java.util.List;

public class NotificationTestDataFactory {

    public static QualityNotificationMessage createNotificationTestData() {
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

    public static QualityNotificationMessage createNotificationTestData(QualityNotificationType qualityNotificationType) {
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
                .type(qualityNotificationType)
                .build();
    }
}
