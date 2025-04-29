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
package org.eclipse.tractusx.traceability.notification.domain.base.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.domain.EnumFieldUtils;
import org.eclipse.tractusx.traceability.common.model.PageResult;
import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.notification.application.notification.service.NotificationService;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.SendNotificationException;
import org.eclipse.tractusx.traceability.notification.domain.base.model.Notification;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationId;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSide;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.NotificationSenderAndReceiverBPNEqualException;
import org.eclipse.tractusx.traceability.notification.domain.notification.model.EditNotification;
import org.eclipse.tractusx.traceability.notification.domain.notification.model.StartNotification;
import org.eclipse.tractusx.traceability.notification.domain.notification.repository.NotificationRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Transactional
public abstract class AbstractNotificationService implements NotificationService {

    private final TraceabilityProperties traceabilityProperties;
    private final NotificationPublisherService notificationPublisherService;

    private static final List<String> SUPPORTED_ENUM_FIELDS = List.of("status", "side", "severity", "type");

    protected abstract NotificationRepository getNotificationRepository();


    protected abstract RuntimeException getNotFoundException(String message);

    @Override
    public PageResult<Notification> getNotifications(Pageable pageable, SearchCriteria searchCriteria) {
        return getNotificationRepository().getNotifications(pageable, searchCriteria);
    }

    @Override
    public NotificationId start(StartNotification startNotification) {
        validateReceiverIsNotOwnBpn(startNotification.getReceiverBpn(), null);
        Notification notification = notificationPublisherService.startNotification(startNotification);
        NotificationId createdAlertId = getNotificationRepository().saveNotification(notification);
        log.info("Start Quality Notification {}", notification);
        return createdAlertId;
    }

    @Override
    public void updateStatusTransition(Long notificationId, NotificationStatus notificationStatus, String reason) {
        Notification notification = loadOrNotFoundException(new NotificationId(notificationId));

        List<NotificationMessage> messages = notification.getNotifications();
        NotificationStatus previousStatus = NotificationStatus.getPreviousStatus(notificationStatus);

        /* Create a copy of the latest notifications.
        As per asset there will be a notification created on start
        it is possible that several elements with the same previous state are returned.*/
        messages.stream()
                .filter(notificationMessage -> notificationMessage.getNotificationStatus().equals(previousStatus))
                .forEach(notificationMessage -> {
                    NotificationMessage notificationMessageSwitchedSenderAndReceiver = notificationMessage.copyAndSwitchSenderAndReceiver(traceabilityProperties.getBpn());
                    notificationMessageSwitchedSenderAndReceiver.setId(UUID.randomUUID().toString());
                    notificationMessageSwitchedSenderAndReceiver.changeStatusTo(notificationStatus);
                    notificationMessageSwitchedSenderAndReceiver.setMessage(reason);
                    notification.addNotificationMessage(notificationMessageSwitchedSenderAndReceiver);
                });

        Notification updatedNotification;
        try {
            updatedNotification = notificationPublisherService.publishNotificationByStateAndReason(notification, notificationStatus, reason);
        } catch (SendNotificationException exception) {
            log.info("Notification status rollback", exception);
            throw new SendNotificationException(exception.getMessage());
        }

        getNotificationRepository().updateNotification(updatedNotification);
    }

    @Override
    public void editNotification(EditNotification editNotification) {
        validateReceiverIsNotOwnBpn(editNotification.getReceiverBpn(), editNotification.getId());
        Notification notification = loadOrNotFoundException(new NotificationId(editNotification.getId()));

        if (editNotification.getReceiverBpn() != null) {
            notification.setInitialReceiverBpns(List.of(editNotification.getReceiverBpn()));
            notification.setSendTo(editNotification.getReceiverBpn());
        }

        notification.setTitle(editNotification.getTitle());

        if (editNotification.getDescription() != null) {
            notification.setDescription(editNotification.getDescription());
        }
        if (editNotification.getAffectedPartIds() != null) {
            notification.setAffectedPartIds(editNotification.getAffectedPartIds());
        }
        if (editNotification.getSeverity() != null){
            notification.setSeverity(editNotification.getSeverity());
        }
        if (editNotification.getTargetDate() != null){
            notification.setTargetDate(String.valueOf(editNotification.getTargetDate()));
        }

        getNotificationRepository().updateNotificationAndMessage(notification);
    }

    @Override
    public Notification find(Long id) {
        NotificationId investigationId = new NotificationId(id);
        return loadOrNotFoundException(investigationId);
    }

    @Override
    public void approve(Long notificationId) {
        Notification notification = loadOrNotFoundException(new NotificationId(notificationId));

        final Notification approvedInvestigation;
        try {
            approvedInvestigation = notificationPublisherService.publishNotificationByStateAndReason(notification, NotificationStatus.SENT, null);
        } catch (SendNotificationException exception) {
            log.info("Notification status rollback", exception);
            throw new SendNotificationException(exception.getMessage(), exception);
        }
        getNotificationRepository().updateNotification(approvedInvestigation);
    }

    @Override
    public void cancel(Long notificationId) {
        Notification notification = loadOrNotFoundException(new NotificationId(notificationId));
        Notification canceledNotification = notificationPublisherService.cancelNotification(notification);

        getNotificationRepository().updateNotification(canceledNotification);
    }

    @Override
    public List<String> getSearchableValues(String fieldName, List<String> startsWith, Integer size, NotificationSide side) {
        final Integer resultSize = Objects.isNull(size) ? Integer.MAX_VALUE : size;

        if (isSupportedEnumType(fieldName)) {
            return EnumFieldUtils.getValues(fieldName, startsWith);
        }
        return getNotificationRepository().getDistinctFieldValues(fieldName, startsWith, resultSize, side);
    }

    @Override
    public Notification loadOrNotFoundException(NotificationId investigationId) {
        return getNotificationRepository().findOptionalNotificationById(investigationId)
                .orElseThrow(() -> getNotFoundException(investigationId.value().toString()));
    }

    @Override
    public Notification loadByEdcNotificationIdOrNotFoundException(String edcNotificationId) {
        return getNotificationRepository().findByEdcNotificationId(edcNotificationId)
                .orElseThrow(() -> getNotFoundException(edcNotificationId));
    }

    private boolean isSupportedEnumType(String fieldName) {
        return SUPPORTED_ENUM_FIELDS.contains(fieldName);
    }

    private void validateReceiverIsNotOwnBpn(String bpn, Long notificationId) {
        if (traceabilityProperties.getBpn().value().equals(bpn)) {
            if (notificationId != null) {
                throw new NotificationSenderAndReceiverBPNEqualException(bpn, notificationId);
            } else {
                throw new NotificationSenderAndReceiverBPNEqualException(bpn);
            }
        }

    }

}
