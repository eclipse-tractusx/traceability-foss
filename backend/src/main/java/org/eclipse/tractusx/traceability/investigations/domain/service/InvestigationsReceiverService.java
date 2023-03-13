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
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
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
    private final TraceabilityProperties traceabilityProperties;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public InvestigationsReceiverService(InvestigationsRepository repository,
                                         InvestigationsReadService investigationsReadService,
                                         NotificationMapper notificationMapper, InvestigationMapper investigationMapper, TraceabilityProperties traceabilityProperties) {

        this.repository = repository;
        this.investigationsReadService = investigationsReadService;
        this.notificationMapper = notificationMapper;
        this.investigationMapper = investigationMapper;
        this.traceabilityProperties = traceabilityProperties;
    }

    public void handleNotificationReceiverCallback(EDCNotification edcNotification) {
        logger.info("Received notification response with id {}", edcNotification.getNotificationId());

        BPN recipientBPN = BPN.of(edcNotification.getRecipientBPN());

        validateNotificationReceiverCallback(edcNotification);

        InvestigationStatus investigationStatus = edcNotification.convertInvestigationStatus();

        switch (investigationStatus) {
            case SENT -> receiveInvestigation(edcNotification, recipientBPN, investigationStatus);
            case ACKNOWLEDGED -> receiveUpdateInvestigation(edcNotification, InvestigationStatus.ACKNOWLEDGED);
            case ACCEPTED -> receiveUpdateInvestigation(edcNotification, InvestigationStatus.ACCEPTED);
            case DECLINED -> receiveUpdateInvestigation(edcNotification, InvestigationStatus.DECLINED);
            case CLOSED -> closeInvestigation(edcNotification);
            default -> throw new InvestigationIllegalUpdate("Failed to handle notification due to unhandled %s status".formatted(investigationStatus));
        }
    }

    private void validateNotificationReceiverCallback(EDCNotification edcNotification) {
        NotificationType notificationType = edcNotification.convertNotificationType();

        if (!notificationType.equals(NotificationType.QMINVESTIGATION)) {
            throw new InvestigationIllegalUpdate("Received %s classified edc notification which is not an investigation".formatted(notificationType));
        }
    }

    private void receiveInvestigation(EDCNotification edcNotification, BPN bpn, InvestigationStatus investigationStatus) {
        logger.info("receiveInvestigation with status {}", investigationStatus);
        Notification notification = notificationMapper.toReceiverNotification(edcNotification, investigationStatus);
        Investigation investigation = investigationMapper.toReceiverInvestigation(bpn, edcNotification.getInformation(), notification);
        repository.save(investigation);
        logger.info("Stored received notification in investigation {}", investigation);
    }

    private void receiveUpdateInvestigation(EDCNotification edcNotification, InvestigationStatus investigationStatus) {
        logger.info("receiveUpdateInvestigation with status {}", investigationStatus);
        Notification notification = notificationMapper.toReceiverNotification(edcNotification, investigationStatus);
        Investigation investigation = investigationsReadService.loadInvestigationByNotificationId(edcNotification.getRelatedNotificationId());

        switch (investigationStatus) {
            case ACKNOWLEDGED -> investigation.acknowledge(notification);
            case ACCEPTED -> investigation.accept("the accept reason (set by system)", notification);
            case DECLINED -> investigation.decline("the decline reason (set by system)", notification);
            default -> throw new InvestigationIllegalUpdate("Failed to handle notification due to unhandled %s status".formatted(investigationStatus));
        }
        investigation.addNotification(notification);
        InvestigationId savedInvestigation = repository.update(investigation);

        logger.info("Stored received notification in investigation {}", savedInvestigation);
    }

    private void closeInvestigation(EDCNotification edcNotification) {
        if (notificationOnMySideExists(edcNotification)) {
            logger.info("InvestigationReceiverService#closeInvestigation incoming");
            Investigation investigation = investigationsReadService.loadInvestigationByNotificationId(edcNotification.getRelatedNotificationId());
            investigation.close(traceabilityProperties.getBpn(), edcNotification.getInformation());
            repository.update(investigation);
        }


    }

    private boolean notificationOnMySideExists(EDCNotification edcNotification) {
        return edcNotification.getRelatedNotificationId() != null;
    }

}
