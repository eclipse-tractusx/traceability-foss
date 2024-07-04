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

import org.eclipse.tractusx.traceability.notification.domain.base.model.Notification;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationAffectedPart;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;

import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

public class EDCNotificationFactory {

    private EDCNotificationFactory() {
    }

    public static EDCNotification createEdcNotification(String senderEDC, NotificationMessage notificationMessage, Notification notification) {

        EDCNotificationHeader header = new EDCNotificationHeader(
                notificationMessage.getEdcNotificationId(),
                notificationMessage.getSentBy(),
                senderEDC,
                notificationMessage.getSentTo(),
                NotificationType.from(notificationMessage.getType()).getValue(),
                notification.getSeverity().getRealName(),
                notificationMessage.getNotificationReferenceId(),
                notificationMessage.getNotificationStatus().name(),
                notification.getTargetDate(),
                notificationMessage.getMessageId()
        );

        String message = null;
        if (notification.getNotificationStatus() != null && notification.getNotificationStatus().equals(NotificationStatus.SENT)){
            message = notification.getDescription();
        } else{
            message = notificationMessage.getMessage();
        }
        EDCNotificationContent content = new EDCNotificationContent(
                message,
                extractAssetIds(notificationMessage)
        );

        return new EDCNotification(header, content);
    }


    private static List<String> extractAssetIds(NotificationMessage notification) {
        return emptyIfNull(notification.getAffectedParts()).stream()
                .map(NotificationAffectedPart::assetId).toList();
    }
}

