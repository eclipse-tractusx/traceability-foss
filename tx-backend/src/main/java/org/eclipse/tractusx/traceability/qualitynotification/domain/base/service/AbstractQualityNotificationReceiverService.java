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
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationNotFoundException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.repository.QualityNotificationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotification;

@Slf4j
public abstract class AbstractQualityNotificationReceiverService implements QualityNotificationReceiverService {

    protected abstract QualityNotificationRepository getRepository();

    protected abstract NotificationMessageMapper getNotificationMessageMapper();

    protected abstract QualityNotificationMapper getQualityNotificationMapper();

    @Override
    public void handleReceive(EDCNotification edcNotification) {
        BPN investigationCreatorBPN = BPN.of(edcNotification.getSenderBPN());
        QualityNotificationMessage notification = getNotificationMessageMapper().toNotification(edcNotification);
        QualityNotification investigation = getQualityNotificationMapper().toQualityNotification(investigationCreatorBPN, edcNotification.getInformation(), notification);
        QualityNotificationId investigationId = getRepository().saveQualityNotificationEntity(investigation);
        log.info("Stored received edcNotification in investigation with id {}", investigationId);
    }

    @Override
    public void handleUpdate(EDCNotification edcNotification) {
        QualityNotificationMessage notification = getNotificationMessageMapper().toNotification(edcNotification);
        QualityNotification qualityNotification = getRepository().findByEdcNotificationId(edcNotification.getNotificationId())
                .orElseThrow(() -> new InvestigationNotFoundException(edcNotification.getNotificationId()));

        handleNotificationStatusUpdate(edcNotification, notification, qualityNotification);
        getRepository().updateQualityNotificationEntity(qualityNotification);
    }

    public static void handleNotificationStatusUpdate(EDCNotification edcNotification, QualityNotificationMessage notification, QualityNotification alert) {
        switch (edcNotification.convertNotificationStatus()) {
            case ACKNOWLEDGED -> alert.acknowledge();
            case ACCEPTED -> alert.accept(edcNotification.getInformation());
            case DECLINED -> alert.decline(edcNotification.getInformation());
            case CLOSED -> alert.close(BPN.of(alert.getBpn()), edcNotification.getInformation());
            default ->
                    throw new InvestigationIllegalUpdate("Failed to handle notification due to unhandled %s status".formatted(edcNotification.convertNotificationStatus()));
        }
        alert.addNotification(notification);

    }
}
