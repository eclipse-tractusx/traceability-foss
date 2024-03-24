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
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.bpn.domain.service.BpnRepository;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.exception.SendNotificationException;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotification;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationMessage;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSeverity;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationStatus;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationType;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.exception.QualityNotificationIllegalUpdate;
import org.eclipse.tractusx.traceability.qualitynotification.domain.notification.exception.NotificationNotSupportedException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationPublisherService {

    private final TraceabilityProperties traceabilityProperties;
    private final EdcNotificationService edcNotificationService;
    private final AssetAsBuiltRepository assetAsBuiltRepository;
    private final BpnRepository bpnRepository;
    private final Clock clock;


    public QualityNotification startQualityNotification(String title, List<String> assetIds, String description, Instant targetDate, QualityNotificationSeverity severity, QualityNotificationType type, String receiverBpn, boolean isAsBuilt) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        QualityNotification notification = QualityNotification.startNotification(title, clock.instant(), applicationBPN, description, type);
        if (isAsBuilt) {
            Map<String, List<AssetBase>> assetsAsBuiltBPNMap = assetAsBuiltRepository.getAssetsById(assetIds).stream().collect(groupingBy(AssetBase::getManufacturerId));
            assetsAsBuiltBPNMap
                    .entrySet()
                    .stream()
                    .map(it -> {
                        String creator = getManufacturerNameByBpn(traceabilityProperties.getBpn().value());
                        String sendToName = getManufacturerNameByBpn(receiverBpn);
                        return QualityNotificationMessage.create(applicationBPN, receiverBpn, description, targetDate, severity, type, it, creator, sendToName);
                    })
                    .forEach(notification::addNotification);
            return notification;
        } else {
            throw new NotificationNotSupportedException();
        }
    }

    private String getManufacturerNameByBpn(String bpn) {
        return bpnRepository.findManufacturerName(bpn);
    }

    /**
     * Cancels an ongoing notification with the given BPN and ID.
     *
     * @param notification the Notification to cancel
     */
    public QualityNotification cancelNotification(QualityNotification notification) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        notification.cancel(applicationBPN); // Shouldn't cancel notification trigger update on other side ?
        return notification;
    }

    /**
     * Approves an ongoing notification with the given BPN and ID to the next stage.
     *
     * @param notification the Notification to send
     */
    public QualityNotification approveNotification(QualityNotification notification) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        notification.send(applicationBPN);

        log.info("Quality Notification starts approval process with {} notifications", notification.getNotifications().size());

        List<String> notificationStatus = notification.getNotifications().stream().map(notificationMessage -> notificationMessage.getNotificationStatus().name()).toList();
        notificationStatus.forEach(s -> log.info("Notification Status {}", s));

        // For each asset within investigation a notification was created before
        List<CompletableFuture<QualityNotificationMessage>> futures =
                notification
                        .getNotifications()
                        .stream()
                        .filter(notificationMessage ->
                                notificationMessage.getNotificationStatus().name()
                                        .equals(QualityNotificationStatus.SENT.name()))
                        .map(edcNotificationService::asyncNotificationMessageExecutor)
                        .filter(Objects::nonNull)
                        .toList();
        List<QualityNotificationMessage> sentMessages = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .toList();
        if (sentMessages.isEmpty()) {
            throw new SendNotificationException("No Message was sent");
        }
        return notification;
    }

    /**
     * Updates an ongoing notification with the given BPN, ID, status and reason.
     *
     * @param notification the Notification to update
     * @param status       the NotificationStatus of the notification to update
     * @param reason       the reason for update of the notification
     */
    public QualityNotification updateNotificationPublisher(QualityNotification notification, QualityNotificationStatus status, String reason) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        validate(applicationBPN, status, notification);

        List<QualityNotificationMessage> relevantNotifications =
                notification
                        .getNotifications()
                        .stream()
                        .filter(notificationMessage -> status.equals(notificationMessage.getNotificationStatus()))
                        .toList();

        relevantNotifications.forEach(qNotification -> {
            switch (status) {
                case ACKNOWLEDGED -> notification.acknowledge();
                case ACCEPTED -> notification.accept(reason);
                case DECLINED -> notification.decline(reason);
                case CLOSED -> notification.close(reason);
                default ->
                        throw new QualityNotificationIllegalUpdate("Transition from status '%s' to status '%s' is not allowed for notification with id '%s'".formatted(notification.getNotificationStatus().name(), status, notification.getNotificationId()));
            }
            log.info("::updateNotificationPublisher::notificationToSend {}", qNotification);
        });

        List<CompletableFuture<QualityNotificationMessage>> futures = relevantNotifications.stream()
                .map(edcNotificationService::asyncNotificationMessageExecutor)
                .filter(Objects::nonNull)
                .toList();
        List<QualityNotificationMessage> sentMessages = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .toList();

        if (sentMessages.isEmpty()) {
            throw new SendNotificationException("No Message was sent");
        }

        return notification;
    }

    private void validate(BPN applicationBpn, QualityNotificationStatus status, QualityNotification notification) {

        final boolean isInvalidAcknowledgeOrAcceptOrDecline = !QualityNotificationSide.RECEIVER.equals(notification.getNotificationSide()) && applicationBpn.value().equals(notification.getBpn());
        final boolean isInvalidClose = QualityNotificationStatus.CLOSED.equals(status) && !applicationBpn.value().equals(notification.getBpn());
        switch (status) {
            case ACKNOWLEDGED, ACCEPTED, DECLINED -> {
                if (isInvalidAcknowledgeOrAcceptOrDecline) {
                    throw new QualityNotificationIllegalUpdate("Transition from status '%s' to status '%s' is not allowed for notification with BPN '%s' and application with BPN '%s'".formatted(notification.getNotificationStatus().name(), status, notification.getBpn(), applicationBpn.value()));
                }
            }
            case CLOSED -> {
                if (isInvalidClose) {
                    throw new QualityNotificationIllegalUpdate("Transition from status '%s' to status '%s' is not allowed for notification with BPN '%s' and application with BPN '%s'".formatted(notification.getNotificationStatus().name(), status, notification.getBpn(), applicationBpn.value()));
                }
            }
            default ->
                    throw new QualityNotificationIllegalUpdate("Unknown Transition from status '%s' to status '%s' is not allowed for notification with id '%s'".formatted(notification.getNotificationStatus().name(), status, notification.getNotificationId()));
        }
    }
}

