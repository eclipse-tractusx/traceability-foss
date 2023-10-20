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

package org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.service.AssetAsBuiltServiceImpl;
import org.eclipse.tractusx.traceability.qualitynotification.domain.alert.model.exception.StartQualityNotificationDomain;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.InvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.service.AbstractQualityNotificationService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.service.NotificationPublisherService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationNotFoundException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.repository.QualityNotificationRepository;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service("investigationServiceImpl")
public class InvestigationServiceImpl extends AbstractQualityNotificationService {

    private final NotificationPublisherService notificationPublisherService;
    private final InvestigationRepository investigationsRepository;
    private final AssetAsBuiltServiceImpl assetService;

    @Override
    protected NotificationPublisherService getNotificationPublisherService() {
        return notificationPublisherService;
    }

    @Override
    protected QualityNotificationRepository getQualityNotificationRepository() {
        return investigationsRepository;
    }

    @Override
    protected AssetAsBuiltServiceImpl getAssetAsBuiltServiceImpl() {
        return assetService;
    }

    @Override
    public QualityNotificationId start(StartQualityNotificationDomain startQualityAlertDomain) {
        QualityNotification notification = getNotificationPublisherService().startInvestigation(startQualityAlertDomain.getPartIds(), startQualityAlertDomain.getDescription(), startQualityAlertDomain.getTargetDate(), startQualityAlertDomain.getSeverity(), startQualityAlertDomain.getReceiverBpn(), startQualityAlertDomain.isAsBuilt());

        QualityNotificationId createdInvestigationId = getQualityNotificationRepository().saveQualityNotificationEntity(notification);
        log.info("Start Investigation {}", notification);
        return createdInvestigationId;
    }

    @Override
    public QualityNotification loadOrNotFoundException(QualityNotificationId investigationId) {
        return getQualityNotificationRepository().findOptionalQualityNotificationById(investigationId)
                .orElseThrow(() -> new InvestigationNotFoundException(investigationId));
    }

    @Override
    public QualityNotification loadByEdcNotificationIdOrNotFoundException(String edcNotificationId) {
        return getQualityNotificationRepository().findByEdcNotificationId(edcNotificationId)
                .orElseThrow(() -> new InvestigationNotFoundException(edcNotificationId));
    }

    @Override
    public void setAssetStatus(QualityNotification qualityNotification) {
        assetService.setAssetsInvestigationStatus(qualityNotification);
    }


}
