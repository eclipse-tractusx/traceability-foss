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
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.qualitynotification.application.investigation.service.InvestigationService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationNotFoundException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.repository.InvestigationRepository;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.service.NotificationPublisherService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
// TODO as this is duplicated with InvestigationServiceImpl it should be done like assetAsPlanned / assetAsBuilt with an abstract class / interface
@Slf4j
@RequiredArgsConstructor
@Service
public class InvestigationServiceImpl implements InvestigationService {

    private final NotificationPublisherService notificationPublisherService;

    private final InvestigationRepository investigationsRepository;

    private final AssetAsBuiltServiceImpl assetService;


    @Override
    public QualityNotificationId start(List<String> partIds, String description, Instant targetDate, QualityNotificationSeverity severity) {
        QualityNotification notification = notificationPublisherService.startInvestigation(partIds, description, targetDate, severity);

        QualityNotificationId createdInvestigationId = investigationsRepository.saveQualityNotificationEntity(notification);
        log.info("Start Investigation {}", notification);
        return createdInvestigationId;
    }

    @Override
    public PageResult<QualityNotification> getCreated(Pageable pageable) {
        return getInvestigationsPageResult(pageable, QualityNotificationSide.SENDER);
    }

    @Override
    public PageResult<QualityNotification> getReceived(Pageable pageable) {
        return getInvestigationsPageResult(pageable, QualityNotificationSide.RECEIVER);
    }

    @Override
    public QualityNotification find(Long id) {
        QualityNotificationId investigationId = new QualityNotificationId(id);
        return loadOrNotFoundException(investigationId);
    }

    @Override
    public QualityNotification loadOrNotFoundException(QualityNotificationId investigationId) {
        return investigationsRepository.findOptionalQualityNotificationById(investigationId)
                .orElseThrow(() -> new InvestigationNotFoundException(investigationId));
    }

    @Override
    public QualityNotification loadByEdcNotificationIdOrNotFoundException(String edcNotificationId) {
        return investigationsRepository.findByEdcNotificationId(edcNotificationId)
                .orElseThrow(() -> new InvestigationNotFoundException(edcNotificationId));
    }

    @Override
    public void approve(Long notificationId) {
        QualityNotification notification = loadOrNotFoundException(new QualityNotificationId(notificationId));
        final QualityNotification approvedInvestigation = notificationPublisherService.approveNotification(notification);
        investigationsRepository.updateQualityNotificationEntity(approvedInvestigation);
    }

    @Override
    public void cancel(Long notificationId) {
        QualityNotification investigation = loadOrNotFoundException(new QualityNotificationId(notificationId));
        QualityNotification canceledInvestigation = notificationPublisherService.cancelNotification(investigation);

        assetService.setAssetsInvestigationStatus(canceledInvestigation);
        investigationsRepository.updateQualityNotificationEntity(canceledInvestigation);
    }

    @Override
    public void update(Long investigationId, QualityNotificationStatus status, String reason) {
        QualityNotification investigation = loadOrNotFoundException(new QualityNotificationId(investigationId));
        QualityNotification updatedInvestigation = notificationPublisherService.updateNotificationPublisher(investigation, status, reason);

        assetService.setAssetsInvestigationStatus(updatedInvestigation);
        investigationsRepository.updateQualityNotificationEntity(updatedInvestigation);
    }

    private PageResult<QualityNotification> getInvestigationsPageResult(Pageable pageable, QualityNotificationSide investigationSide) {
        List<QualityNotification> investigationData = investigationsRepository.findQualityNotificationsBySide(investigationSide, pageable)
                .content()
                .stream()
                .sorted(QualityNotification.COMPARE_BY_NEWEST_QUALITY_NOTIFICATION_CREATION_TIME)
                .toList();
        Page<QualityNotification> investigationDataPage = new PageImpl<>(investigationData, pageable, investigationsRepository.countQualityNotificationEntitiesBySide(investigationSide));
        return new PageResult<>(investigationDataPage);
    }

}
