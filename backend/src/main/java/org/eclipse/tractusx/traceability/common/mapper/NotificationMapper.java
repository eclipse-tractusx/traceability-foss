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

import org.eclipse.tractusx.traceability.assets.domain.ports.BpnRepository;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification;
import org.eclipse.tractusx.traceability.investigations.domain.model.Notification;
import org.eclipse.tractusx.traceability.investigations.domain.model.Severity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public Notification toNotification(EDCNotification edcNotification) {
        String notificationId = UUID.randomUUID().toString();
        return new Notification(
                notificationId,
                edcNotification.getNotificationId(),
                edcNotification.getSenderBPN(),
                getManufacturerName(edcNotification.getSenderBPN()),
                edcNotification.getRecipientBPN(),
                getManufacturerName(edcNotification.getRecipientBPN()),
                edcNotification.getSenderAddress(),
                null,
                edcNotification.getInformation(),
                edcNotification.convertInvestigationStatus(),
                edcNotification.getListOfAffectedItems(),
                edcNotification.getTargetDate(),
                Severity.fromString(edcNotification.getSeverity()),
                edcNotification.getNotificationId(),
                null,
                null,
                edcNotification.getMessageId()
        );
    }

    private String getManufacturerName(String senderBPN) {
        return bpnRepository.findManufacturerName(senderBPN)
                .orElse(null);
    }
}
