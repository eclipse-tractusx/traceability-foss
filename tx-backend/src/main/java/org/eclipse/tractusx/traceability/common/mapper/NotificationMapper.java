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
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.notification.domain.base.model.Notification;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationAffectedPart;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSeverity;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSide;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.eclipse.tractusx.traceability.common.date.DateUtil.convertInstantToString;

@RequiredArgsConstructor
@Component
public class NotificationMapper {

    /**
     * Creates an Notification object representing the notification received by the receiver for a given notification.
     *
     * @param bpn          the BPN of the notification
     * @param edcNotification  the edcNotification of the notification
     * @param notification the notification associated with the alert or investigation
     * @return an Notification object representing the notification received by the receiver
     */
    public Notification toNotification(BPN bpn, EDCNotification edcNotification, NotificationMessage notification, NotificationType notificationType, BPN applicationBPN) {

        List<String> assetIds = new ArrayList<>();
        emptyIfNull(notification.getAffectedParts()).stream()
                .map(NotificationAffectedPart::assetId)
                .forEach(assetIds::add);
        return Notification.builder()
                .bpn(bpn)
                .notificationStatus(NotificationStatus.RECEIVED)
                .notificationSide(NotificationSide.RECEIVER)
                .notificationType(notificationType)
                .description(edcNotification.getInformation())
                .createdAt(Instant.now())
                .sendTo(applicationBPN.value())
                .updatedDate(Instant.now())
                .severity(NotificationSeverity.fromString(edcNotification.getSeverity()))
                .targetDate(convertInstantToString(edcNotification.getTargetDate()))
                .affectedPartIds(assetIds)
                .notifications(List.of(notification))
                .initialReceiverBpns(List.of(applicationBPN.value()))
                .build();
    }
}
