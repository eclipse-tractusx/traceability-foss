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
import org.eclipse.tractusx.traceability.bpn.infrastructure.repository.BpnRepository;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSeverity;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Component
@RequiredArgsConstructor
public class NotificationMessageMapper {

    private final BpnRepository bpnRepository;

    /**
     * Creates a Notification object representing the notification received by the receiver for a given EDCNotification.
     *
     * @param edcNotification the EDCNotification received by the receiver
     * @return a Notification object representing the notification received by the receiver
     */
    public NotificationMessage toNotificationMessage(EDCNotification edcNotification, NotificationType type) {
        String notificationId = UUID.randomUUID().toString();
        return NotificationMessage.builder()
                .id(notificationId)
                .created(LocalDateTime.now())
                .notificationReferenceId(edcNotification.getNotificationId())
                .sentBy(edcNotification.getSenderBPN())
                .sentByName(getManufacturerName(edcNotification.getSenderBPN()))
                .type(type)
                .sentTo(edcNotification.getRecipientBPN())
                .sendToName(getManufacturerName(edcNotification.getRecipientBPN()))
                .message(edcNotification.getInformation())
                .notificationStatus(edcNotification.convertNotificationStatus())
                .affectedParts(emptyIfNull(edcNotification.getListOfAffectedItems()))
                .edcNotificationId(edcNotification.getNotificationId())
                .messageId(edcNotification.getMessageId())
                .build();

    }

    private String getManufacturerName(String senderBPN) {
        return bpnRepository.findManufacturerName(senderBPN);
    }
}
