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
import org.eclipse.tractusx.traceability.qualitynotification.domain.alert.model.exception.AlertNotFoundException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.alert.model.exception.StartQualityNotificationDomain;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.AlertRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.service.AbstractQualityNotificationService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.service.NotificationPublisherService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.repository.QualityNotificationRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service("alertServiceImpl")
@RequiredArgsConstructor
public class AlertServiceImpl extends AbstractQualityNotificationService {

    private final NotificationPublisherService notificationPublisherService;
    private final AlertRepository alertRepository;
    private final AssetAsBuiltServiceImpl assetService;

    @Override
    protected NotificationPublisherService getNotificationPublisherService() {
        return notificationPublisherService;
    }

    @Override
    protected QualityNotificationRepository getQualityNotificationRepository() {
        return alertRepository;
    }

    @Override
    protected AssetAsBuiltServiceImpl getAssetAsBuiltServiceImpl() {
        return assetService;
    }

    @Override
    public QualityNotificationId start(StartQualityNotificationDomain startQualityAlertDomain) {
        QualityNotification notification = notificationPublisherService.startAlert(startQualityAlertDomain.getPartIds(), startQualityAlertDomain.getDescription(), startQualityAlertDomain.getTargetDate(), startQualityAlertDomain.getSeverity(), startQualityAlertDomain.getReceiverBpn(), startQualityAlertDomain.isAsBuilt());

        QualityNotificationId createdAlertId = alertRepository.saveQualityNotificationEntity(notification);
        log.info("Start Alert {}", notification);
        return createdAlertId;
    }

    @Override
    public QualityNotification loadOrNotFoundException(QualityNotificationId notificationId) {
        return alertRepository.findOptionalQualityNotificationById(notificationId)
                .orElseThrow(() -> new AlertNotFoundException(notificationId));
    }

    @Override
    public QualityNotification loadByEdcNotificationIdOrNotFoundException(String edcNotificationId) {
        return alertRepository.findByEdcNotificationId(edcNotificationId)
                .orElseThrow(() -> new AlertNotFoundException(edcNotificationId));
    }

    @Override
    public void cancel(Long notificationId) {
        QualityNotification alert = loadOrNotFoundException(new QualityNotificationId(notificationId));
        QualityNotification canceledAlert = notificationPublisherService.cancelNotification(alert);

        assetService.setAssetsAlertStatus(canceledAlert);
        alertRepository.updateQualityNotificationEntity(canceledAlert);
    }

    @Override
    public void setAssetStatus(QualityNotification qualityNotification) {
        assetService.setAssetsAlertStatus(qualityNotification);
    }

}
