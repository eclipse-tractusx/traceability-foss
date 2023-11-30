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
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.qualitynotification.application.base.service.QualityNotificationService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.exception.SendNotificationException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.repository.QualityNotificationRepository;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

@Slf4j
public abstract class AbstractQualityNotificationService implements QualityNotificationService {

    private static final List<String> SUPPORTED_ENUM_FIELDS = List.of("status", "side", "notifications_severity");

    protected abstract NotificationPublisherService getNotificationPublisherService();

    protected abstract QualityNotificationRepository getQualityNotificationRepository();

    protected abstract AssetAsBuiltServiceImpl getAssetAsBuiltServiceImpl();

    protected abstract void setAssetStatus(QualityNotification qualityNotification);

    @Override
    public PageResult<QualityNotification> getNotifications(Pageable pageable, SearchCriteria searchCriteria) {
        return getQualityNotificationRepository().getNotifications(pageable, searchCriteria);
    }

    @Override
    public void update(Long notificationId, QualityNotificationStatus notificationStatus, String reason) {
        QualityNotification alert = loadOrNotFoundException(new QualityNotificationId(notificationId));
        QualityNotification updatedAlert;
        try {
            updatedAlert = getNotificationPublisherService().updateNotificationPublisher(alert, notificationStatus, reason);
        } catch (SendNotificationException exception) {
            log.info("Notification status rollback", exception);
            return;
        }

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
        final QualityNotification approvedInvestigation;
        try {
            approvedInvestigation = getNotificationPublisherService().approveNotification(notification);
        } catch (SendNotificationException exception) {
            log.info("Notification status rollback", exception);
            return;
        }
        getQualityNotificationRepository().updateQualityNotificationEntity(approvedInvestigation);
    }

    @Override
    public void cancel(Long notificationId) {
        QualityNotification qualityNotification = loadOrNotFoundException(new QualityNotificationId(notificationId));
        QualityNotification canceledQualityNotification = getNotificationPublisherService().cancelNotification(qualityNotification);

        setAssetStatus(canceledQualityNotification);
        getQualityNotificationRepository().updateQualityNotificationEntity(canceledQualityNotification);
    }

    @Override
    public List<String> getDistinctFilterValues(String fieldName, String startWith, Integer size, QualityNotificationSide side) {
        if (isSupportedEnumType(fieldName)) {
            return getAssetEnumFieldValues(fieldName);
        }
        return getQualityNotificationRepository().getDistinctFieldValues(fieldName, startWith, size, side);
    }

    private boolean isSupportedEnumType(String fieldName) {
        return SUPPORTED_ENUM_FIELDS.contains(fieldName);
    }

    private List<String> getAssetEnumFieldValues(String fieldName) {
        return switch (fieldName) {
            case "status" -> Arrays.stream(QualityNotificationStatus.values()).map(Enum::name).toList();
            case "side" -> Arrays.stream(QualityNotificationSide.values()).map(Enum::name).toList();
            case "notifications_severity" ->
                    Arrays.stream(QualityNotificationSeverity.values()).map(Enum::name).toList();
            default -> null;
        };
    }
}
