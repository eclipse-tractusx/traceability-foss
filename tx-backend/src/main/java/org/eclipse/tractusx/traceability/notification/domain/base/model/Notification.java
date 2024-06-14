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
package org.eclipse.tractusx.traceability.notification.domain.base.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.assets.domain.base.model.AssetBase;
import org.eclipse.tractusx.traceability.bpn.domain.model.BpnEdcMapping;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.notification.domain.notification.exception.InvestigationStatusTransitionNotAllowed;
import org.eclipse.tractusx.traceability.notification.domain.notification.model.EditNotification;

import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Data
@Builder(toBuilder = true)
@Slf4j
public class Notification {
    private String title;
    private BPN bpn;
    private NotificationId notificationId;
    private NotificationStatus notificationStatus;
    private String description;
    private Instant createdAt;
    private NotificationSide notificationSide;
    private NotificationType notificationType;
    @Builder.Default
    private List<String> affectedPartIds = new ArrayList<>();

    @Getter
    @Builder.Default
    private List<NotificationMessage> notifications = List.of();


    public static Notification startNotification(String title, Instant createDate, BPN bpn, String description, NotificationType notificationType) {
        return Notification.builder()
                .title(title)
                .bpn(bpn)
                .notificationStatus(NotificationStatus.CREATED)
                .notificationSide(NotificationSide.SENDER)
                .notificationType(notificationType)
                .description(description)
                .createdAt(createDate)
                .affectedPartIds(Collections.emptyList())
                .build();
    }

    public void clearNotifications() {
        notifications = new ArrayList<>();
    }

    public void createInitialNotifications(List<AssetBase> affectedParts, BPN applicationBPN, EditNotification editNotification, List<BpnEdcMapping> bpnEdcMappings) {

        if (editNotification.getReceiverBpn() != null) {
            Map.Entry<String, List<AssetBase>> receiverAssetsMap = new AbstractMap.SimpleEntry<>(editNotification.getReceiverBpn(), affectedParts);
            Optional<String> sentToName = bpnEdcMappings.stream().filter(bpnEdcMapping -> bpnEdcMapping.bpn().equals(editNotification.getReceiverBpn())).findFirst().map(BpnEdcMapping::manufacturerName);
            NotificationMessage notificationMessage = NotificationMessage.create(
                    applicationBPN,
                    editNotification.getReceiverBpn(),
                    editNotification.getDescription(),
                    editNotification.getTargetDate(),
                    editNotification.getSeverity(),
                    this.notificationType,
                    receiverAssetsMap,
                    applicationBPN.value(),
                    sentToName.orElse(null));

            this.addNotificationMessage(notificationMessage);


        } else {
            Map<String, List<AssetBase>> assetsAsBuiltBPNMap = affectedParts.stream().collect(groupingBy(AssetBase::getManufacturerId));
            assetsAsBuiltBPNMap
                    .entrySet()
                    .stream()
                    .map(receiverAssetsMapEntry -> {
                        String sentToBPN = receiverAssetsMapEntry.getKey();
                        Optional<String> sentToName = bpnEdcMappings.stream().filter(bpnEdcMapping -> bpnEdcMapping.bpn().equals(sentToBPN)).findFirst().map(BpnEdcMapping::manufacturerName);
                        return NotificationMessage.create(
                                applicationBPN,
                                sentToBPN,
                                editNotification.getDescription(),
                                editNotification.getTargetDate(),
                                editNotification.getSeverity(),
                                this.notificationType,
                                receiverAssetsMapEntry,
                                applicationBPN.value(),
                                sentToName.orElse(null));
                    })
                    .forEach(this::addNotificationMessage);
        }


    }

    public List<String> getAffectedPartIds() {
        return Collections.unmodifiableList(affectedPartIds);
    }

    public String getBpn() {
        return bpn.value();
    }

    public void cancel(BPN applicationBpn) {
        validateBPN(applicationBpn);
        changeStatusTo(NotificationStatus.CANCELED);
        this.closeReason = "canceled";
    }

    public void close(BPN applicationBpn, String reason) {
        validateBPN(applicationBpn);
        changeStatusTo(NotificationStatus.CLOSED);
        this.closeReason = reason;
        this.notifications.forEach(notification -> notification.setDescription(reason));
    }

    public void acknowledge() {
        changeStatusTo(NotificationStatus.ACKNOWLEDGED);
    }

    public void accept(String reason) {
        changeStatusTo(NotificationStatus.ACCEPTED);
        this.acceptReason = reason;
    }

    public void decline(String reason) {
        changeStatusTo(NotificationStatus.DECLINED);
        this.declineReason = reason;
    }

    public void close(String reason) {
        changeStatusTo(NotificationStatus.CLOSED);
        this.closeReason = reason;
    }

    public void send(BPN applicationBpn) {
        validateBPN(applicationBpn);
        changeStatusTo(NotificationStatus.SENT);
    }

    private void validateBPN(BPN applicationBpn) {
        if (!applicationBpn.equals(this.bpn)) {
            throw new InvestigationIllegalUpdate("%s bpn has no permissions to update investigation with %s id.".formatted(applicationBpn.value(), notificationId.value()));
        }
    }

    private void changeStatusTo(NotificationStatus to) {
        boolean transitionAllowed = notificationStatus.transitionAllowed(to);

        if (!transitionAllowed) {
            throw new InvestigationStatusTransitionNotAllowed(notificationId, notificationStatus, to);
        }
        this.notificationStatus = to;
    }

    public void addNotificationMessage(NotificationMessage notification) {

        List<NotificationMessage> updatedNotifications = new ArrayList<>(notifications);
        updatedNotifications.add(notification);
        notifications = Collections.unmodifiableList(updatedNotifications);

        List<String> newAssetIds = new ArrayList<>(affectedPartIds); // create a mutable copy of assetIds
        emptyIfNull(notification.getAffectedParts()).stream()
                .map(NotificationAffectedPart::assetId)
                .forEach(newAssetIds::add);

        affectedPartIds = Collections.unmodifiableList(newAssetIds); //
    }

    public void addNotificationMessages(List<NotificationMessage> notificationMessages) {
        notificationMessages.forEach(this::addNotificationMessage);
    }

    public boolean isActiveState() {
        return this.notificationStatus.isActiveState();
    }

    public List<NotificationMessage> secondLatestNotifications() {

        Optional<NotificationMessage> highestState = notifications.stream()
                .max(Comparator.comparing(NotificationMessage::getCreated));

        if (highestState.isPresent()) {
            NotificationMessage highestMessage = highestState.get();
            NotificationStatus highestStatus = highestMessage.getNotificationStatus();
            log.info("Highest status found: {}", highestStatus);

            Optional<NotificationMessage> secondHighestState = notifications.stream()
                    .filter(message -> !message.getNotificationStatus().equals(highestStatus))
                    .max(Comparator.comparing(NotificationMessage::getCreated));

            if (secondHighestState.isPresent()) {
                log.info("Second highest status found: {}", secondHighestState.get().getNotificationStatus());
                return notifications.stream()
                        .filter(message -> message.getNotificationStatus().equals(secondHighestState.get().getNotificationStatus()))
                        .toList();
            } else {
                log.info("No second highest status found. Returning notifications with highest status.");
                return notifications.stream()
                        .filter(message -> message.getNotificationStatus().equals(highestStatus))
                        .toList();
            }
        } else {
            log.warn("No notifications found. Returning empty list.");
            return Collections.emptyList();
        }
    }

}
