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
package org.eclipse.tractusx.traceability.notification.infrastructure.edc.model;

import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationAffectedPart;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSeverity;

import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

public class EDCNotificationFactory {

    private EDCNotificationFactory() {
    }

    public static EDCNotification createEdcNotification(String senderEDC, NotificationMessage notification) {
        String targetDate = null;
        if (notification.getTargetDate() != null) {
            targetDate = notification.getTargetDate().toString();
        }

        EDCNotificationHeader header = new EDCNotificationHeader(
                notification.getEdcNotificationId(),
                notification.getSentBy(),
                senderEDC,
                notification.getSentTo(),
                NotificationType.from(notification.getType()).getValue(),
                notification.getSeverity() != null ? notification.getSeverity().getRealName() : NotificationSeverity.MINOR.getRealName(),
                notification.getNotificationReferenceId(),
                notification.getNotificationStatus().name(),
                targetDate,
                notification.getMessageId()
        );

        EDCNotificationContent content = new EDCNotificationContent(
                notification.getMessage(),
                extractAssetIds(notification)
        );

        return new EDCNotification(header, content);
    }


    private static List<String> extractAssetIds(NotificationMessage notification) {
        return emptyIfNull(notification.getAffectedParts()).stream()
                .map(NotificationAffectedPart::assetId).toList();
    }
}

