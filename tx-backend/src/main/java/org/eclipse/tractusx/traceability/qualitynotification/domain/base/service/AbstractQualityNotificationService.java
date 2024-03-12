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
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.qualitynotification.application.base.service.QualityNotificationService;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.exception.SendNotificationException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationId;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.repository.QualityNotificationRepository;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Slf4j
public abstract class AbstractQualityNotificationService implements QualityNotificationService {

    private static final List<String> SUPPORTED_ENUM_FIELDS = List.of("status", "side", "notifications_severity");


    protected abstract NotificationPublisherService getNotificationPublisherService();

    protected abstract QualityNotificationRepository getQualityNotificationRepository();

    @Override
    public PageResult<QualityNotification> getNotifications(Pageable pageable, SearchCriteria searchCriteria) {
        return getQualityNotificationRepository().getNotifications(pageable, searchCriteria);
    }

    @Override
    public void update(Long notificationId, QualityNotificationStatus notificationStatus, String reason) {
        QualityNotification qualityNotification = loadOrNotFoundException(new QualityNotificationId(notificationId));

        List<QualityNotificationStatus> searchStatus = new ArrayList<>();

        switch (notificationStatus) {
            case ACKNOWLEDGED -> searchStatus.add(QualityNotificationStatus.SENT);
            case ACCEPTED, DECLINED -> searchStatus.add(QualityNotificationStatus.ACKNOWLEDGED);
            case CLOSED -> {
                searchStatus.add(QualityNotificationStatus.SENT);
                searchStatus.add(QualityNotificationStatus.ACCEPTED);
                searchStatus.add(QualityNotificationStatus.DECLINED);
            }
            default ->
                    throw new IllegalStateException("Unexpected status update: " + qualityNotification.getNotificationStatus());
        }


        List<QualityNotificationMessage> notifications = new ArrayList<>(filterNotifications(searchStatus, qualityNotification));

        notifications.forEach(notificationMessage -> {
            notificationMessage.setId(UUID.randomUUID().toString());
            notificationMessage.changeStatusTo(notificationStatus);
            notificationMessage.setDescription(reason);
        });

        qualityNotification.addNotifications(notifications);

        QualityNotification updatedQualityNotification;
        try {
            updatedQualityNotification = getNotificationPublisherService().updateNotificationPublisher(qualityNotification, notificationStatus, reason);
        } catch (SendNotificationException exception) {
            log.info("Notification status rollback", exception);
            throw new SendNotificationException(exception.getMessage());
        }

        getQualityNotificationRepository().updateQualityNotificationEntity(updatedQualityNotification);
    }

    public List<QualityNotificationMessage> filterNotifications(List<QualityNotificationStatus> searchStatus, QualityNotification qualityNotification) {

        return searchStatus.stream()
                .flatMap(status -> {
                    Set<QualityNotificationStatus> filterStatuses = getFilterStatuses(status);
                    return qualityNotification.getNotifications().stream()
                            .filter(notificationMessage -> {
                                if (status == QualityNotificationStatus.CLOSED &&
                                        (notificationMessage.getNotificationStatus() == QualityNotificationStatus.ACCEPTED ||
                                                notificationMessage.getNotificationStatus() == QualityNotificationStatus.DECLINED)) {
                                    return false; // Exclude SENT status if CLOSED and message contains ACCEPTED or DECLINED
                                }
                                return filterStatuses.contains(notificationMessage.getNotificationStatus());
                            })
                            .map(notificationMessage -> notificationMessage.copyAndSwitchSenderAndReceiver(BPN.of("BPN ABC")));
                }).toList();
    }

    private Set<QualityNotificationStatus> getFilterStatuses(QualityNotificationStatus status) {
        switch (status) {
            case SENT, ACKNOWLEDGED:
                return EnumSet.of(QualityNotificationStatus.SENT);
            case ACCEPTED:
                return EnumSet.of(QualityNotificationStatus.ACKNOWLEDGED);
            case CLOSED:
                return EnumSet.of(QualityNotificationStatus.ACCEPTED, QualityNotificationStatus.DECLINED);
            default:
                return EnumSet.noneOf(QualityNotificationStatus.class);
        }
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
            approvedInvestigation = getNotificationPublisherService().approveNotification(notification);
        } catch (SendNotificationException exception) {
            log.info("Notification status rollback", exception);
            throw new SendNotificationException(exception.getMessage());
        }
        getQualityNotificationRepository().updateQualityNotificationEntity(approvedInvestigation);
    }

    @Override
    public void cancel(Long notificationId) {
        QualityNotification qualityNotification = loadOrNotFoundException(new QualityNotificationId(notificationId));
        QualityNotification canceledQualityNotification = getNotificationPublisherService().cancelNotification(qualityNotification);

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
