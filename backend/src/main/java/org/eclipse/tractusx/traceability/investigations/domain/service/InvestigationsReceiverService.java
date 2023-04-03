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

package org.eclipse.tractusx.traceability.investigations.domain.service;

import org.eclipse.tractusx.traceability.common.mapper.InvestigationMapper;
import org.eclipse.tractusx.traceability.common.mapper.NotificationMapper;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.EDCNotification;
import org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model.NotificationType;
import org.eclipse.tractusx.traceability.investigations.domain.model.Investigation;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationId;
import org.eclipse.tractusx.traceability.investigations.domain.model.InvestigationStatus;
import org.eclipse.tractusx.traceability.investigations.domain.model.Notification;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.investigations.domain.ports.InvestigationsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class InvestigationsReceiverService {

    private final InvestigationsRepository repository;
    private final InvestigationsReadService investigationsReadService;
    private final NotificationMapper notificationMapper;
    private final InvestigationMapper investigationMapper;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public InvestigationsReceiverService(InvestigationsRepository repository,
                                         InvestigationsReadService investigationsReadService,
                                         NotificationMapper notificationMapper, InvestigationMapper investigationMapper) {

        this.repository = repository;
        this.investigationsReadService = investigationsReadService;
        this.notificationMapper = notificationMapper;
        this.investigationMapper = investigationMapper;
    }

    public void handleNotificationReceiverCallback(EDCNotification edcNotification) {
        validateNotificationReceiverCallback(edcNotification);
        InvestigationId investigationId;
        if (isInitialNotification(edcNotification)) {
            investigationId = createInvestigation(edcNotification);
        } else {
            investigationId = updateInvestigation(edcNotification);
        }
        logger.info("Stored received edcNotification in investigation with id {}", investigationId);
    }

    private boolean isInitialNotification(EDCNotification edcNotification) {
        return InvestigationStatus.SENT.equals(edcNotification.convertInvestigationStatus());
    }

    private void validateNotificationReceiverCallback(EDCNotification edcNotification) {
        NotificationType notificationType = edcNotification.convertNotificationType();
        if (!notificationType.equals(NotificationType.QMINVESTIGATION)) {
            throw new InvestigationIllegalUpdate("Received %s classified edc notification which is not an investigation".formatted(notificationType));
        }
    }

    private InvestigationId createInvestigation(EDCNotification edcNotification) {
        BPN investigationCreatorBPN = BPN.of(edcNotification.getSenderBPN());
        Notification notification = notificationMapper.toNotification(edcNotification);
        Investigation investigation = investigationMapper.toInvestigation(investigationCreatorBPN, edcNotification.getInformation(), notification);
        return repository.save(investigation);
    }

    private InvestigationId updateInvestigation(EDCNotification edcNotification) {
        Notification notification = notificationMapper.toNotification(edcNotification);
        Investigation investigation = investigationsReadService.loadInvestigationByEdcNotificationId(edcNotification.getNotificationId());
        switch (edcNotification.convertInvestigationStatus()) {
            case ACKNOWLEDGED -> investigation.acknowledge(notification);
            case ACCEPTED -> investigation.accept(edcNotification.getInformation(), notification);
            case DECLINED -> investigation.decline(edcNotification.getInformation(), notification);
            case CLOSED -> investigation.close(BPN.of(investigation.getBpn()), edcNotification.getInformation());
            default -> throw new InvestigationIllegalUpdate("Failed to handle notification due to unhandled %s status".formatted(edcNotification.convertInvestigationStatus()));
        }
        investigation.addNotification(notification);
        return repository.update(investigation);
    }

}
