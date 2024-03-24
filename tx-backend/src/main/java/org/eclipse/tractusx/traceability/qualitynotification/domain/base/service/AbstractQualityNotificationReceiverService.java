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
package org.eclipse.tractusx.traceability.qualitynotification.domain.base.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.mapper.NotificationMessageMapper;
import org.eclipse.tractusx.traceability.common.mapper.QualityNotificationMapper;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationType;
import org.eclipse.tractusx.traceability.qualitynotification.domain.repository.QualityNotificationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotification;

@Slf4j
public abstract class AbstractQualityNotificationReceiverService implements QualityNotificationReceiverService {

    protected abstract QualityNotificationRepository getRepository();

    protected abstract NotificationMessageMapper getNotificationMessageMapper();

    protected abstract QualityNotificationMapper getQualityNotificationMapper();

    protected abstract RuntimeException getNotFoundException(String message);

    protected abstract RuntimeException getIllegalUpdateException(String message);

    @Override
    public void handleReceive(EDCNotification edcNotification, QualityNotificationType notificationType) {
        BPN investigationCreatorBPN = BPN.of(edcNotification.getSenderBPN());
        QualityNotificationMessage notification = getNotificationMessageMapper().toNotification(edcNotification, notificationType);
        QualityNotification investigation = getQualityNotificationMapper().toQualityNotification(investigationCreatorBPN, edcNotification.getInformation(), notification, notificationType);
        QualityNotificationId investigationId = getRepository().saveQualityNotificationEntity(investigation);
        log.info("Stored received edcNotification in investigation with id {}", investigationId);
    }

    @Override
    public void handleUpdate(EDCNotification edcNotification, QualityNotificationType notificationType) {
        QualityNotificationMessage notification = getNotificationMessageMapper().toNotification(edcNotification, notificationType);
        QualityNotification qualityNotification = getRepository().findByEdcNotificationId(edcNotification.getNotificationId())
                .orElseThrow(() -> getNotFoundException(edcNotification.getNotificationId()));

        switch (edcNotification.convertNotificationStatus()) {
            case ACKNOWLEDGED -> qualityNotification.acknowledge();
            case ACCEPTED -> qualityNotification.accept(edcNotification.getInformation());
            case DECLINED -> qualityNotification.decline(edcNotification.getInformation());
            case CLOSED ->
                    qualityNotification.close(BPN.of(qualityNotification.getBpn()), edcNotification.getInformation());
            default ->
                    throw getIllegalUpdateException("Failed to handle notification due to unhandled %s status".formatted(edcNotification.convertNotificationStatus()));
        }
        qualityNotification.addNotification(notification);
        getRepository().updateQualityNotificationEntity(qualityNotification);
    }
}
