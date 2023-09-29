/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model;

import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSeverity;

import java.util.List;

public class EDCNotificationFactory {

    private EDCNotificationFactory() {
    }

    public static EDCNotification createEdcNotification(String senderEDC, QualityNotificationMessage notification) {
        String targetDate = null;
        if (notification.getTargetDate() != null) {
            targetDate = notification.getTargetDate().toString();
        }
        EDCNotificationHeader header = new EDCNotificationHeader(
                notification.getEdcNotificationId(),
                notification.getCreatedBy(),
                senderEDC,
                notification.getSendTo(),
                NotificationType.from(notification.getType()).getValue(),
                notification.getSeverity() != null ? notification.getSeverity().getRealName() : QualityNotificationSeverity.MINOR.getRealName(),
                notification.getNotificationReferenceId(),
                notification.getNotificationStatus().name(),
                targetDate,
                notification.getMessageId()
        );

        EDCNotificationContent content = new EDCNotificationContent(
                notification.getDescription(),
                extractAssetIds(notification)
        );

        return new EDCNotification(header, content);
    }


    private static List<String> extractAssetIds(QualityNotificationMessage notification) {
        return notification.getAffectedParts().stream()
                .map(QualityNotificationAffectedPart::assetId).toList();
    }
}

