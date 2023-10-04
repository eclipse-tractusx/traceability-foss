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
package org.eclipse.tractusx.traceability.common.mapper;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.traceability.bpn.domain.service.BpnRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationMessageMapper { // rename to QualityNotificationMessageMapper

    private final BpnRepository bpnRepository;

    /**
     * Creates a Notification object representing the notification received by the receiver for a given EDCNotification.
     *
     * @param edcNotification the EDCNotification received by the receiver
     * @return a Notification object representing the notification received by the receiver
     */
    public QualityNotificationMessage toNotification(EDCNotification edcNotification) {
        String notificationId = UUID.randomUUID().toString();
        return QualityNotificationMessage.builder()
                .id(notificationId)
                .created(LocalDateTime.now())
                .notificationReferenceId(edcNotification.getNotificationId())
                .createdBy(edcNotification.getSenderBPN())
                .createdByName(getManufacturerName(edcNotification.getSenderBPN()))
                .sendTo(edcNotification.getRecipientBPN())
                .sendToName(getManufacturerName(edcNotification.getRecipientBPN()))
                .edcUrl(edcNotification.getSenderAddress())
                .description(edcNotification.getInformation())
                .notificationStatus(edcNotification.convertNotificationStatus())
                .affectedParts(edcNotification.getListOfAffectedItems())
                .targetDate(edcNotification.getTargetDate())
                .severity(QualityNotificationSeverity.fromString(edcNotification.getSeverity()))
                .edcNotificationId(edcNotification.getNotificationId())
                .messageId(edcNotification.getMessageId())
                .isInitial(false)
                .build();

    }

    private String getManufacturerName(String senderBPN) {
        return bpnRepository.findManufacturerName(senderBPN)
                .orElse(null);
    }
}
