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
package org.eclipse.tractusx.traceability.qualitynotification.domain.base.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.NotificationStatusTransitionNotAllowed;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@Data
public class QualityNotificationMessage {
    private final String id;
    private final String createdByName;
    private final String sendToName;
    @Builder.Default
    private final List<QualityNotificationAffectedPart> affectedParts = new ArrayList<>();
    private String notificationReferenceId;
    private String createdBy;
    private String sendTo;
    private String edcUrl;
    private String contractAgreementId;
    private String description;
    private QualityNotificationStatus notificationStatus;
    private String edcNotificationId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Instant targetDate;
    private QualityNotificationSeverity severity;
    private String messageId;
    private Boolean isInitial;
    private QualityNotificationType type;
    private String errorMessage;

    void changeStatusTo(QualityNotificationStatus to) {
        boolean transitionAllowed = notificationStatus.transitionAllowed(to);

        if (!transitionAllowed) {
            throw new NotificationStatusTransitionNotAllowed(id, notificationStatus, to);
        }
        this.notificationStatus = to;
    }


    // Important - receiver and sender will be saved in switched order
    public QualityNotificationMessage copyAndSwitchSenderAndReceiver(BPN applicationBpn) {
        final String notificationId = UUID.randomUUID().toString();
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
        return QualityNotificationMessage.builder()
                .created(LocalDateTime.now())
                .id(notificationId)
                .createdBy(senderBPN)
                .createdByName(senderName)
                .sendTo(receiverBPN)
                .sendToName(receiverName)
                .edcUrl(edcUrl)
                .contractAgreementId(contractAgreementId)
                .description(description)
                .notificationStatus(notificationStatus)
                .affectedParts(affectedParts)
                .targetDate(targetDate)
                .severity(severity)
                .edcNotificationId(edcNotificationId)
                .messageId(UUID.randomUUID().toString())
                .isInitial(false)
                .type(type)
                .errorMessage(errorMessage)
                .build();
    }
}
