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
package org.eclipse.tractusx.traceability.qualitynotification.domain.base.service;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.service.AssetAsBuiltServiceImpl;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.qualitynotification.application.base.service.QualityNotificationService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.repository.QualityNotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Slf4j
public abstract class AbstractQualityNotificationService implements QualityNotificationService {

    protected abstract NotificationPublisherService getNotificationPublisherService();
    protected abstract QualityNotificationRepository getQualityNotificationRepository();
    protected abstract AssetAsBuiltServiceImpl getAssetAsBuiltServiceImpl();

    protected abstract void setAssetStatus(QualityNotification qualityNotification);

    @Override
    public PageResult<QualityNotification> getCreated(Pageable pageable) {
        return getQualityNotificationsPageResult(pageable, QualityNotificationSide.SENDER);
    }

    @Override
    public PageResult<QualityNotification> getReceived(Pageable pageable) {
        return getQualityNotificationsPageResult(pageable, QualityNotificationSide.RECEIVER);
    }

    @Override
    public void update(Long notificationId, QualityNotificationStatus notificationStatus, String reason) {
        QualityNotification alert = loadOrNotFoundException(new QualityNotificationId(notificationId));
        QualityNotification updatedAlert = getNotificationPublisherService().updateNotificationPublisher(alert, notificationStatus, reason);

        getAssetAsBuiltServiceImpl().setAssetsAlertStatus(updatedAlert);
        getQualityNotificationRepository().updateQualityNotificationEntity(updatedAlert);
    }

    @Override
    public QualityNotification find(Long id) {
        QualityNotificationId investigationId = new QualityNotificationId(id);
        return loadOrNotFoundException(investigationId);
    }

    @Override
    public void approve(Long notificationId) {
        QualityNotification notification = loadOrNotFoundException(new QualityNotificationId(notificationId));
        final QualityNotification approvedNotification = getNotificationPublisherService().approveNotification(notification);
        getQualityNotificationRepository().updateQualityNotificationEntity(approvedNotification);
    }

    private PageResult<QualityNotification> getQualityNotificationsPageResult(Pageable pageable, QualityNotificationSide alertSide) {
        List<QualityNotification> alertData = getQualityNotificationRepository().findQualityNotificationsBySide(alertSide, pageable)
                .content();
        Page<QualityNotification> alertDataPage = new PageImpl<>(alertData, pageable, getQualityNotificationRepository().countQualityNotificationEntitiesBySide(alertSide));
        return new PageResult<>(alertDataPage);
    }

    @Override
    public void cancel(Long notificationId) {
        QualityNotification qualityNotification = loadOrNotFoundException(new QualityNotificationId(notificationId));
        QualityNotification canceledQualityNotification = getNotificationPublisherService().cancelNotification(qualityNotification);

        setAssetStatus(canceledQualityNotification);
        getQualityNotificationRepository().updateQualityNotificationEntity(canceledQualityNotification);
    }
}
