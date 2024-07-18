/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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
package org.eclipse.tractusx.traceability.notification.domain.base.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.mapper.NotificationMapper;
import org.eclipse.tractusx.traceability.common.mapper.NotificationMessageMapper;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.notification.domain.base.model.Notification;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationId;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationType;
import org.eclipse.tractusx.traceability.notification.domain.notification.repository.NotificationRepository;
import org.eclipse.tractusx.traceability.notification.infrastructure.edc.model.EDCNotification;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Slf4j
public abstract class AbstractNotificationReceiverService implements NotificationReceiverService {

    protected abstract NotificationRepository getRepository();

    protected abstract NotificationMessageMapper getNotificationMessageMapper();

    protected abstract NotificationMapper getNotificationMapper();

    protected abstract RuntimeException getNotFoundException(String message);

    protected abstract RuntimeException getIllegalUpdateException(String message);

    protected abstract BPN getApplicationBpn();

    @Override
    public void handleReceive(EDCNotification edcNotification, NotificationType notificationType) {
        BPN investigationCreatorBPN = BPN.of(edcNotification.getSenderBPN());
        NotificationMessage notification = getNotificationMessageMapper().toNotificationMessage(edcNotification, notificationType);
        Notification investigation = getNotificationMapper().toNotification(investigationCreatorBPN, edcNotification, notification, notificationType, getApplicationBpn());
        NotificationId investigationId = getRepository().saveNotification(investigation);
        log.info("Stored received edcNotification in investigation with id {}", investigationId);
    }

    @Override
    public void handleUpdate(EDCNotification edcNotification, NotificationType notificationType) {

        Notification notification = getRepository().findByEdcNotificationId(edcNotification.getNotificationId())
                .orElseThrow(() -> getNotFoundException(edcNotification.getNotificationId()));
        NotificationMessage notificationMessage = getNotificationMessageMapper().toNotificationMessage(edcNotification, notificationType);
        emptyIfNull(notification.getNotifications()).stream().findFirst().ifPresent(notificationMessage1 -> notificationMessage.setAffectedParts(notificationMessage1.getAffectedParts()));

        switch (edcNotification.convertNotificationStatus()) {
            case ACKNOWLEDGED -> notification.acknowledge();
            case ACCEPTED -> notification.accept(edcNotification.getInformation(), notificationMessage);
            case DECLINED -> notification.decline(edcNotification.getInformation(), notificationMessage);
            case CLOSED ->
                    notification.close(BPN.of(notification.getBpn()), edcNotification.getInformation(), notificationMessage);
            default ->
                    throw getIllegalUpdateException("Failed to handle notification due to unhandled %s status".formatted(edcNotification.convertNotificationStatus()));
        }
        notification.addNotificationMessage(notificationMessage);
        getRepository().updateNotification(notification);
    }
}
