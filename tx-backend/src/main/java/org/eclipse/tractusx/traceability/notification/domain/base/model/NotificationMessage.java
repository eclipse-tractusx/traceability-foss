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
package org.eclipse.tractusx.traceability.notification.domain.base.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.NotificationStatusTransitionNotAllowed;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder(toBuilder = true)
@Getter
@Setter
@Data
public class NotificationMessage {
    private String id;
    private final String createdByName;
    private final String sendToName;
    @Builder.Default
    private List<NotificationAffectedPart> affectedParts = new ArrayList<>();
    private String notificationReferenceId;
    private String createdBy;
    private String sendTo;
    private String contractAgreementId;
    private String description;
    private NotificationStatus notificationStatus;
    private String edcNotificationId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Instant targetDate;
    private NotificationSeverity severity;
    private String messageId;
    private NotificationType type;
    private String errorMessage;

    public void changeStatusTo(NotificationStatus to) {
        boolean transitionAllowed = notificationStatus.transitionAllowed(to);

        if (!transitionAllowed) {
            throw new NotificationStatusTransitionNotAllowed(id, notificationStatus, to);
        }
        this.notificationStatus = to;
    }

    public static NotificationMessage create(BPN applicationBpn, String receiverBpn, String description, Instant targetDate, NotificationSeverity severity, NotificationType notificationType, Map.Entry<String, List<AssetBase>> asset, String creator, String sendToName) {
        final String notificationId = UUID.randomUUID().toString();
        final String messageId = UUID.randomUUID().toString();
        return NotificationMessage.builder()
                .id(notificationId)
                .created(LocalDateTime.now())
                .createdBy(applicationBpn.value())
                .createdByName(creator)
                .sendTo(StringUtils.isBlank(receiverBpn) ? asset.getKey() : receiverBpn)
                .sendToName(sendToName)
                .description(description)
                .notificationStatus(NotificationStatus.CREATED)
                .affectedParts(asset.getValue().stream().map(AssetBase::getId).map(NotificationAffectedPart::new).toList())
                .targetDate(targetDate)
                .severity(severity)
                .edcNotificationId(notificationId)
                .type(notificationType)
                .messageId(messageId)
                .build();
    }



    // Important - receiver and sender will be saved in switched order
    public NotificationMessage copyAndSwitchSenderAndReceiver(BPN applicationBpn) {
        final String notificationId = UUID.randomUUID().toString();
        final String messageUUID = UUID.randomUUID().toString();
        String receiverBPN = sendTo;
        String senderBPN = createdBy;
        String receiverName;
        String senderName;

        // This is needed to make sure that the app can send a message to the receiver and not addresses itself
        if (applicationBpn.value().equals(sendTo)) {
            receiverBPN = createdBy;
            senderBPN = sendTo;
            receiverName = createdByName;
            senderName = sendToName;
        } else {
            receiverName = sendToName;
            senderName = createdByName;
        }
        return NotificationMessage.builder()
                .created(LocalDateTime.now())
                .id(notificationId)
                .createdBy(senderBPN)
                .createdByName(senderName)
                .sendTo(receiverBPN)
                .sendToName(receiverName)
                .contractAgreementId(contractAgreementId)
                .description(description)
                .notificationStatus(notificationStatus)
                .affectedParts(affectedParts)
                .edcNotificationId(edcNotificationId)
                .messageId(messageUUID)
                .severity(severity)
                .targetDate(targetDate)
                .type(type)
                .errorMessage(errorMessage)
                .build();
    }
}
