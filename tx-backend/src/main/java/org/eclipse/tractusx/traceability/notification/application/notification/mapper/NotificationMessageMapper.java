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

package org.eclipse.tractusx.traceability.notification.application.notification.mapper;

import lombok.experimental.UtilityClass;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSeverity;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSide;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;
import notification.response.NotificationMessageResponse;
import notification.response.NotificationSeverityResponse;
import notification.response.NotificationSideResponse;
import notification.response.NotificationStatusResponse;
import notification.response.NotificationTypeResponse;

import java.util.List;

@UtilityClass
public class NotificationMessageMapper {

    public static NotificationSeverityResponse from(NotificationSeverity notificationSeverity) {
        return NotificationSeverityResponse.fromString(notificationSeverity.getRealName());
    }

    public static NotificationSideResponse from(NotificationSide side) {
        return NotificationSideResponse.valueOf(side.name());
    }

    public static NotificationStatusResponse from(NotificationStatus notificationStatus) {
        return NotificationStatusResponse.fromStringValue(notificationStatus.name());
    }

    public static NotificationTypeResponse from(NotificationType notificationType) {
        return NotificationTypeResponse.valueOf(notificationType.name());
    }

    public static List<NotificationMessageResponse> fromNotifications(List<NotificationMessage> notificationMessages) {
        return notificationMessages.stream().map(NotificationMessageMapper::fromNotification).toList();
    }

    public static NotificationMessageResponse fromNotification(NotificationMessage notificationMessage) {
        return NotificationMessageResponse
                .builder()
                .id(notificationMessage.getId())
                .notificationReferenceId(notificationMessage.getNotificationReferenceId())
                .edcNotificationId(notificationMessage.getEdcNotificationId())
                .contractAgreementId(notificationMessage.getContractAgreementId())
                .notificationReferenceId(notificationMessage.getNotificationReferenceId())
                .messageId(notificationMessage.getMessageId())
                .sendToName(notificationMessage.getSendToName())
                .status(fromStatus(notificationMessage.getNotificationStatus()))
                .sentBy(notificationMessage.getSentBy())
                .sentByName(notificationMessage.getSentByName())
                .sendTo(notificationMessage.getSentTo())
                .errorMessage(notificationMessage.getErrorMessage())
                .build();
    }

    public static NotificationStatusResponse fromStatus(NotificationStatus notificationStatus) {
        return NotificationStatusResponse.fromStringValue(notificationStatus.name());
    }
}
