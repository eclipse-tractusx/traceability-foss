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
import static org.eclipse.tractusx.traceability.notification.domain.base.model.NotificationStatus.CLOSED;

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
    public Notification publishNotificationByStateAndReason(Notification notification, NotificationStatus desiredStatus, String reason) {
        log.info("Starting process for desired state: {} with {} notifications", desiredStatus, notification.getNotifications().size());

        BPN applicationBPN = traceabilityProperties.getBpn();

        if (requiresMessagePreparation(desiredStatus)) {
            createMessages(notification, applicationBPN, assetAsBuiltRepository);
        }

        List<NotificationMessage> relevantNotifications =
                notification
                        .getNotifications()
                        .stream()
                        .filter(notificationMessage ->
                                desiredStatus.equals(notificationMessage.getNotificationStatus()) &&
                                        StringUtils.isBlank(notificationMessage.getErrorMessage()))
                        .toList();

        if (relevantNotifications.isEmpty()) {
            throw new NotificationIllegalUpdate("No valid notifications found for desired status: " + desiredStatus);
        }


        validate(applicationBPN, desiredStatus, notification);

        handleNotificationByStatus(notification, desiredStatus, applicationBPN);

        if (reason != null) {
            relevantNotifications.forEach(notificationMessage -> notificationMessage.setMessage(reason));
        }

        List<NotificationMessage> sentMessages = executeAsyncTasks(relevantNotifications, notification);

        if (sentMessages.isEmpty()) {
            throw new SendNotificationException("No Message was sent");
        }
        return notification;
    }

    private List<NotificationMessage> executeAsyncTasks(List<NotificationMessage> notifications, Notification notification) {
        return notifications.stream()
                .map(notificationMessage -> edcNotificationService.asyncNotificationMessageExecutor(notificationMessage, notification))
                .filter(Objects::nonNull)
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .toList();
    }

    private boolean requiresMessagePreparation(NotificationStatus status) {
        return status == NotificationStatus.SENT;
    }

    private void handleNotificationByStatus(Notification notification, NotificationStatus desiredStatus, BPN applicationBPN) {

        switch (desiredStatus) {

            case SENT -> {
                notification.send(applicationBPN);
            }
            case ACKNOWLEDGED -> notification.acknowledge();
            case ACCEPTED -> notification.accept();
            case DECLINED -> notification.decline();
            case CLOSED -> notification.close();

            default -> throw new NotificationIllegalUpdate(
                    "Transition from status '%s' to status '%s' is not allowed for notification with id '%s'"
                            .formatted(notification.getNotificationStatus().name(), desiredStatus, notification.getNotificationId()));
        }
    }

    private void validate(BPN applicationBpn, NotificationStatus status, Notification notification) {

        final boolean isInvalidAcknowledgeOrAcceptOrDecline = !NotificationSide.RECEIVER.equals(notification.getNotificationSide()) && applicationBpn.value().equals(notification.getBpn());
        final boolean isInvalidClose = CLOSED.equals(status) && !applicationBpn.value().equals(notification.getBpn());
        final boolean isInvalidSent = !applicationBpn.value().equals(notification.getBpn());

        switch (status) {
            case SENT -> throwNotificationIllegalUpdateException(applicationBpn, status, notification, isInvalidSent);
            case ACKNOWLEDGED, ACCEPTED, DECLINED ->
                    throwNotificationIllegalUpdateException(applicationBpn, status, notification, isInvalidAcknowledgeOrAcceptOrDecline);
            case CLOSED ->
                    throwNotificationIllegalUpdateException(applicationBpn, status, notification, isInvalidClose);
            default ->
                    throw new NotificationIllegalUpdate("Unknown Transition from status '%s' to status '%s' is not allowed for notification with id '%s'".formatted(notification.getNotificationStatus().name(), status, notification.getNotificationId()));
        }
    }

    private static void throwNotificationIllegalUpdateException(BPN applicationBpn, NotificationStatus status, Notification notification, boolean isInvalidSent) {
        if (isInvalidSent) {
            throw new NotificationIllegalUpdate("Transition from status '%s' to status '%s' is not allowed for notification with BPN '%s' and application with BPN '%s'".formatted(notification.getNotificationStatus().name(), status, notification.getBpn(), applicationBpn.value()));
        }
    }
}

