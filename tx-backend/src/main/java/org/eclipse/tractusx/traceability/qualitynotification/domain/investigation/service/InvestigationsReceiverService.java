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

package org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.service.AssetAsBuiltServiceImpl;
import org.eclipse.tractusx.traceability.common.mapper.NotificationMessageMapper;
import org.eclipse.tractusx.traceability.common.mapper.QualityNotificationMapper;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.InvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationNotFoundException;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotification;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class InvestigationsReceiverService {

    private final InvestigationRepository investigationsRepository;
    private final NotificationMessageMapper notificationMapper;
    private final AssetAsBuiltServiceImpl assetService;
    private final QualityNotificationMapper qualityNotificationMapper;

    public void handleNotificationReceive(EDCNotification edcNotification) {
        BPN investigationCreatorBPN = BPN.of(edcNotification.getSenderBPN());
        QualityNotificationMessage notification = notificationMapper.toNotification(edcNotification);
        QualityNotification investigation = qualityNotificationMapper.toQualityNotification(investigationCreatorBPN, edcNotification.getInformation(), notification);
        QualityNotificationId investigationId = investigationsRepository.saveQualityNotificationEntity(investigation);
        assetService.setAssetsInvestigationStatus(investigation);
        log.info("Stored received edcNotification in investigation with id {}", investigationId);
    }

    public void handleNotificationUpdate(EDCNotification edcNotification) {
        QualityNotificationMessage notification = notificationMapper.toNotification(edcNotification);
        QualityNotification investigation = investigationsRepository.findByEdcNotificationId(edcNotification.getNotificationId())
                .orElseThrow(() -> new InvestigationNotFoundException(edcNotification.getNotificationId()));

        switch (edcNotification.convertNotificationStatus()) {
            case ACKNOWLEDGED -> investigation.acknowledge(notification);
            case ACCEPTED -> investigation.accept(edcNotification.getInformation(), notification);
            case DECLINED -> investigation.decline(edcNotification.getInformation(), notification);
            case CLOSED -> investigation.close(BPN.of(investigation.getBpn()), edcNotification.getInformation());
            default -> throw new InvestigationIllegalUpdate("Failed to handle notification due to unhandled %s status".formatted(edcNotification.convertNotificationStatus()));
        }
        investigation.addNotification(notification);
        assetService.setAssetsInvestigationStatus(investigation);
        QualityNotificationId investigationId = investigationsRepository.updateQualityNotificationEntity(investigation);
        log.info("Stored update edcNotification in investigation with id {}", investigationId);
    }
}
