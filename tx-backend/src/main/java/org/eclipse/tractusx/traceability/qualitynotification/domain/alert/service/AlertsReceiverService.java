/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.qualitynotification.domain.alert.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.service.AssetAsBuiltServiceImpl;
import org.eclipse.tractusx.traceability.common.mapper.NotificationMessageMapper;
import org.eclipse.tractusx.traceability.common.mapper.QualityNotificationMapper;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.domain.alert.model.exception.AlertNotFoundException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.AlertRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.qualitynotification.infrastructure.edc.model.EDCNotification;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class AlertsReceiverService {

    private final AlertRepository alertRepository;
    private final NotificationMessageMapper notificationMapper;
    private final AssetAsBuiltServiceImpl assetService;
    private final QualityNotificationMapper qualityNotificationMapper;

    public void handleNotificationReceive(final EDCNotification edcNotification) {
        final BPN qualityAlertCreatorBPN = BPN.of(edcNotification.getSenderBPN());
        final QualityNotificationMessage notification = notificationMapper.toNotification(edcNotification);
        final QualityNotification qualityAlert = qualityNotificationMapper.toQualityNotification(qualityAlertCreatorBPN,
                edcNotification.getInformation(), notification);
        final QualityNotificationId qualityAlertId = alertRepository.saveQualityNotificationEntity(qualityAlert);
        assetService.setAssetsAlertStatus(qualityAlert);
        log.info("Stored received edcNotification in alert with id {}", qualityAlertId);
    }

    public void handleNotificationUpdate(EDCNotification edcNotification) {
        QualityNotificationMessage notification = notificationMapper.toNotification(edcNotification);
        QualityNotification alert = alertRepository.findByEdcNotificationId(edcNotification.getNotificationId())
                .orElseThrow(() -> new AlertNotFoundException(edcNotification.getNotificationId()));

        switch (edcNotification.convertNotificationStatus()) {
            case ACKNOWLEDGED -> alert.acknowledge(notification);
            case ACCEPTED -> alert.accept(edcNotification.getInformation(), notification);
            case DECLINED -> alert.decline(edcNotification.getInformation(), notification);
            case CLOSED -> alert.close(BPN.of(alert.getBpn()), edcNotification.getInformation());
            default ->
                    throw new InvestigationIllegalUpdate("Failed to handle notification due to unhandled %s status".formatted(edcNotification.convertNotificationStatus()));
        }
        alert.addNotification(notification);
        assetService.setAssetsAlertStatus(alert);
        QualityNotificationId notificationId = alertRepository.updateQualityNotificationEntity(alert);
        log.info("Stored update edcNotification in investigation with id {}", notificationId);
    }
}
