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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.qualitynotification.application.notification.service.QualityNotificationService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.notification.model.StartQualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.exception.SendNotificationException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.notification.repository.QualityNotificationRepository;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractQualityNotificationService implements QualityNotificationService {

    private final TraceabilityProperties traceabilityProperties;
    private final NotificationPublisherService notificationPublisherService;
    private static final List<String> SUPPORTED_ENUM_FIELDS = List.of("status", "side", "messages_severity");

    protected abstract QualityNotificationRepository getQualityNotificationRepository();

    protected abstract RuntimeException getNotFoundException(String message);

    @Override
    public PageResult<QualityNotification> getNotifications(Pageable pageable, SearchCriteria searchCriteria) {
        return getQualityNotificationRepository().getNotifications(pageable, searchCriteria);
    }

    @Override
    public QualityNotificationId start(StartQualityNotification startQualityNotification) {
        QualityNotification notification = notificationPublisherService.startQualityNotification(
                startQualityNotification.getTitle(),
                startQualityNotification.getPartIds(),
                startQualityNotification.getDescription(),
                startQualityNotification.getTargetDate(),
                startQualityNotification.getSeverity(),
                startQualityNotification.getType(),
                startQualityNotification.getReceiverBpn(),
                startQualityNotification.isAsBuilt());
        QualityNotificationId createdAlertId = getQualityNotificationRepository().saveQualityNotificationEntity(notification);
        log.info("Start Quality Notification {}", notification);
        return createdAlertId;
    }

    @Override
    public void update(Long notificationId, QualityNotificationStatus notificationStatus, String reason) {
        QualityNotification qualityNotification = loadOrNotFoundException(new QualityNotificationId(notificationId));

        QualityNotificationStatus previousStatus = QualityNotificationStatus.getPreviousStatus(notificationStatus);

        /* Create a copy of the latest notifications.
        As per asset there will be a notification created on start
        it is possible that several elements with the same previous state are returned.*/
        qualityNotification.getNotifications().stream()
                .filter(notificationMessage -> notificationMessage.getNotificationStatus().equals(previousStatus))
                .forEach(notificationMessage -> {
                    QualityNotificationMessage qualityNotificationMessage = notificationMessage.copyAndSwitchSenderAndReceiver(traceabilityProperties.getBpn());
                    qualityNotificationMessage.setId(UUID.randomUUID().toString());
                    qualityNotificationMessage.changeStatusTo(notificationStatus);
                    qualityNotificationMessage.setDescription(reason);
                    qualityNotification.addNotification(qualityNotificationMessage);
                });

        QualityNotification updatedQualityNotification;
        try {
            updatedQualityNotification = notificationPublisherService.updateNotificationPublisher(qualityNotification, notificationStatus, reason);
        } catch (SendNotificationException exception) {
            log.info("Notification status rollback", exception);
            throw new SendNotificationException(exception.getMessage());
        }

        getQualityNotificationRepository().updateQualityNotificationEntity(updatedQualityNotification);
    }

    @Override
    public QualityNotification find(Long id) {
        QualityNotificationId investigationId = new QualityNotificationId(id);
        return loadOrNotFoundException(investigationId);
    }

    @Override
    public void approve(Long notificationId) {
        QualityNotification notification = loadOrNotFoundException(new QualityNotificationId(notificationId));
        List<QualityNotificationMessage> createdNotifications = notification
                .getNotifications()
                .stream()
                .filter(notificationMessage -> notificationMessage.getNotificationStatus().equals(QualityNotificationStatus.CREATED))
                .map(notificationMessage -> notificationMessage.toBuilder().build())
                .toList();

        log.info("Found {} notification messages in status CREATED", createdNotifications.size());
        List<QualityNotificationMessage> approvedNotifications = new ArrayList<>(createdNotifications);
        approvedNotifications.forEach(notificationMessage -> {
            notificationMessage.setId(UUID.randomUUID().toString());
            notificationMessage.changeStatusTo(QualityNotificationStatus.SENT);
        });
        log.info("Found {} notification messages in status SENT", approvedNotifications.size());

        notification.addNotifications(approvedNotifications);
        log.info("Found {} notification messages at all", notification.getNotifications().size());
        notification.getNotifications().stream().map(notificationMessage -> notificationMessage.getNotificationStatus().name()).forEach(s -> log.info("Notification Status {} ", s));

        final QualityNotification approvedInvestigation;
        try {
            approvedInvestigation = notificationPublisherService.approveNotification(notification);
        } catch (SendNotificationException exception) {
            log.info("Notification status rollback", exception);
            throw new SendNotificationException(exception.getMessage());
        }
        getQualityNotificationRepository().updateQualityNotificationEntity(approvedInvestigation);
    }

    @Override
    public void cancel(Long notificationId) {
        QualityNotification qualityNotification = loadOrNotFoundException(new QualityNotificationId(notificationId));
        QualityNotification canceledQualityNotification = notificationPublisherService.cancelNotification(qualityNotification);

        getQualityNotificationRepository().updateQualityNotificationEntity(canceledQualityNotification);
    }

    @Override
    public List<String> getDistinctFilterValues(String fieldName, String startWith, Integer size, QualityNotificationSide side) {
        final Integer resultSize = Objects.isNull(size) ? Integer.MAX_VALUE : size;

        if (isSupportedEnumType(fieldName)) {
            return getAssetEnumFieldValues(fieldName);
        }
        return getQualityNotificationRepository().getDistinctFieldValues(fieldName, startWith, resultSize, side);
    }

    @Override
    public QualityNotification loadOrNotFoundException(QualityNotificationId investigationId) {
        return getQualityNotificationRepository().findOptionalQualityNotificationById(investigationId)
                .orElseThrow(() -> getNotFoundException(investigationId.value().toString()));
    }

    @Override
    public QualityNotification loadByEdcNotificationIdOrNotFoundException(String edcNotificationId) {
        return getQualityNotificationRepository().findByEdcNotificationId(edcNotificationId)
                .orElseThrow(() -> getNotFoundException(edcNotificationId));
    }

    private boolean isSupportedEnumType(String fieldName) {
        return SUPPORTED_ENUM_FIELDS.contains(fieldName);
    }

    private List<String> getAssetEnumFieldValues(String fieldName) {
        return switch (fieldName) {
            case "status" -> Arrays.stream(QualityNotificationStatus.values()).map(Enum::name).toList();
            case "side" -> Arrays.stream(QualityNotificationSide.values()).map(Enum::name).toList();
            case "messages_severity" ->
                    Arrays.stream(QualityNotificationSeverity.values()).map(Enum::name).toList();
            default -> null;
        };
    }
}
