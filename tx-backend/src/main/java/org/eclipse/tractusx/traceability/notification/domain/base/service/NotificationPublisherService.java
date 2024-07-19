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


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.bpn.infrastructure.repository.BpnRepository;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.common.properties.TraceabilityProperties;
import org.eclipse.tractusx.traceability.notification.domain.base.exception.SendNotificationException;
import org.eclipse.tractusx.traceability.notification.domain.base.model.Notification;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationMessage;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationSide;
import org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus;
import org.eclipse.tractusx.traceability.notification.domain.base.model.exception.NotificationIllegalUpdate;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.NotificationNotFoundException;
import org.eclipse.tractusx.traceability.notification.domain.notification.model.StartNotification;
import org.springframework.stereotype.Service;

import java.time.Clock;
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


    public Notification startNotification(StartNotification startNotification) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        return Notification.startNotification(startNotification.getTitle(), clock.instant(), applicationBPN, startNotification.getDescription(), startNotification.getType(), startNotification.getSeverity(), startNotification.getTargetDate(), startNotification.getAffectedPartIds(), List.of(startNotification.getReceiverBpn()), startNotification.getReceiverBpn());
    }

    private void createMessages(Notification notification, BPN applicationBPN, AssetAsBuiltRepository assetAsBuiltRepository) {
        Map<String, List<AssetBase>> assetsAsBuiltBPNMap =
                assetAsBuiltRepository
                        .getAssetsById(notification.getAffectedPartIds())
                        .stream()
                        .filter(asset -> Objects.nonNull(asset.getManufacturerId()))
                        .collect(groupingBy(AssetBase::getManufacturerId));
        assetsAsBuiltBPNMap
                .entrySet()
                .stream()
                .map(it -> {

                    String firstReceiverBpn = notification.getInitialReceiverBpns().stream()
                            .findFirst()
                            .orElseThrow(() -> new NotificationNotFoundException("Initial receiver BPNs not found"));
                    String sendToName = getManufacturerNameByBpn(firstReceiverBpn);
                    String creator = getManufacturerNameByBpn(applicationBPN.value());
                    return NotificationMessage.create(
                            applicationBPN,
                            firstReceiverBpn,
                            notification.getNotificationType(),
                            it,
                            creator,
                            sendToName);
                })
                .forEach(notification::addNotificationMessage);
    }

    private String getManufacturerNameByBpn(String bpn) {
        return bpnRepository.findManufacturerName(bpn);
    }

    /**
     * Cancels an ongoing notification with the given BPN and ID.
     *
     * @param notification the Notification to cancel
     */
    public Notification cancelNotification(Notification notification) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        notification.cancel(applicationBPN); // Shouldn't cancel notification trigger update on other side ?
        return notification;
    }

    /**
     * Approves an ongoing notification with the given BPN and ID to the next stage.
     *
     * @param notification the Notification to send
     */
    public Notification approveNotification(Notification notification) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        notification.send(applicationBPN);

        log.info("Quality Notification starts approval process with {} notifications", notification.getNotifications().size());

        createMessages(notification, applicationBPN, assetAsBuiltRepository);

        // For each asset within investigation a notification was created before
        List<CompletableFuture<NotificationMessage>> futures =
                notification
                        .getNotifications()
                        .stream()
                        .filter(notificationMessage ->
                                notificationMessage.getNotificationStatus().name()
                                        .equals(NotificationStatus.SENT.name()))
                        .filter(notificationMessage -> StringUtils.isBlank(notificationMessage.getErrorMessage()))
                        .map(notificationMessage -> edcNotificationService.asyncNotificationMessageExecutor(notificationMessage, notification))
                        .filter(Objects::nonNull)
                        .toList();
        List<NotificationMessage> sentMessages = futures.stream()
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
    public Notification updateNotificationPublisher(Notification notification, NotificationStatus status, String reason) {
        BPN applicationBPN = traceabilityProperties.getBpn();
        validate(applicationBPN, status, notification);

        List<NotificationMessage> relevantNotifications =
                notification
                        .getNotifications()
                        .stream()
                        .filter(notificationMessage -> status.equals(notificationMessage.getNotificationStatus()))
                        .toList();

        relevantNotifications.forEach(qNotification -> {
            switch (status) {
                case ACKNOWLEDGED -> notification.acknowledge();
                case ACCEPTED -> notification.accept(reason, qNotification);
                case DECLINED -> notification.decline(reason, qNotification);
                case CLOSED -> notification.close(reason, qNotification);
                default ->
                        throw new NotificationIllegalUpdate("Transition from status '%s' to status '%s' is not allowed for notification with id '%s'".formatted(notification.getNotificationStatus().name(), status, notification.getNotificationId()));
            }
            log.info("::updateNotificationPublisher::notificationToSend {}", qNotification);
        });

        List<CompletableFuture<NotificationMessage>> futures = relevantNotifications.stream()
                .map(message -> edcNotificationService.asyncNotificationMessageExecutor(message, notification))
                .filter(Objects::nonNull)
                .toList();
        List<NotificationMessage> sentMessages = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .toList();

        if (sentMessages.isEmpty()) {
            throw new SendNotificationException("No Message was sent");
        }

        return notification;
    }

    private void validate(BPN applicationBpn, NotificationStatus status, Notification notification) {

        final boolean isInvalidAcknowledgeOrAcceptOrDecline = !NotificationSide.RECEIVER.equals(notification.getNotificationSide()) && applicationBpn.value().equals(notification.getBpn());
        final boolean isInvalidClose = NotificationStatus.CLOSED.equals(status) && !applicationBpn.value().equals(notification.getBpn());
        switch (status) {
            case ACKNOWLEDGED, ACCEPTED, DECLINED -> {
                if (isInvalidAcknowledgeOrAcceptOrDecline) {
                    throw new NotificationIllegalUpdate("Transition from status '%s' to status '%s' is not allowed for notification with BPN '%s' and application with BPN '%s'".formatted(notification.getNotificationStatus().name(), status, notification.getBpn(), applicationBpn.value()));
                }
            }
            case CLOSED -> {
                if (isInvalidClose) {
                    throw new NotificationIllegalUpdate("Transition from status '%s' to status '%s' is not allowed for notification with BPN '%s' and application with BPN '%s'".formatted(notification.getNotificationStatus().name(), status, notification.getBpn(), applicationBpn.value()));
                }
            }
            default ->
                    throw new NotificationIllegalUpdate("Unknown Transition from status '%s' to status '%s' is not allowed for notification with id '%s'".formatted(notification.getNotificationStatus().name(), status, notification.getNotificationId()));
        }
    }
}

