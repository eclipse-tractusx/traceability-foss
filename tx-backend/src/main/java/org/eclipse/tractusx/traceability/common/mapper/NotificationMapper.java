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

import org.eclipse.tractusx.traceability.assets.domain.service.repository.BpnRepository;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationSeverity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class NotificationMapper {

    private final BpnRepository bpnRepository;

    @Autowired
    public NotificationMapper(BpnRepository bpnRepository) {
        this.bpnRepository = bpnRepository;
    }

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
                .senderBpnNumber(edcNotification.getSenderBPN())
                .senderManufacturerName(getManufacturerName(edcNotification.getSenderBPN()))
                .receiverBpnNumber(edcNotification.getRecipientBPN())
                .receiverManufacturerName(getManufacturerName(edcNotification.getRecipientBPN()))
                .edcUrl(edcNotification.getSenderAddress())
                .description(edcNotification.getInformation())
                .investigationStatus(edcNotification.convertInvestigationStatus())
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
