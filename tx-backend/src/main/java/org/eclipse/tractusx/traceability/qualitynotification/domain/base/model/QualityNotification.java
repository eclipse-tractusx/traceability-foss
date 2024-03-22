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
package org.eclipse.tractusx.traceability.qualitynotification.domain.base.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationStatusTransitionNotAllowed;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Data
@Builder(toBuilder = true)
@Slf4j
public class QualityNotification {
    private BPN bpn;
    private QualityNotificationId notificationId;
    private QualityNotificationStatus notificationStatus;
    private String description;
    private Instant createdAt;
    private QualityNotificationSide notificationSide;
    private QualityNotificationType notificationType;
    @Builder.Default
    private List<String> assetIds = new ArrayList<>();
    private String closeReason;
    private String acceptReason;
    private String declineReason;
    @Getter
    @Builder.Default
    private List<QualityNotificationMessage> notifications = List.of();


    // TODO cleanup afterwards
    public static QualityNotification startNotification(Instant createDate, BPN bpn, String description) { // rename to generic
        return QualityNotification.builder()
                .bpn(bpn)
                .notificationStatus(QualityNotificationStatus.CREATED)
                .notificationSide(QualityNotificationSide.SENDER)
                .description(description)
                .createdAt(createDate)
                .assetIds(Collections.emptyList())
                .build();
    }

    public static QualityNotification startNotification(Instant createDate, BPN bpn, String description, QualityNotificationType notificationType) { // rename to generic
        return QualityNotification.builder()
                .bpn(bpn)
                .notificationStatus(QualityNotificationStatus.CREATED)
                .notificationSide(QualityNotificationSide.SENDER)
                .notificationType(notificationType)
                .description(description)
                .createdAt(createDate)
                .assetIds(Collections.emptyList())
                .build();
    }

    public List<String> getAssetIds() {
        return Collections.unmodifiableList(assetIds);
    }

    public String getBpn() {
        return bpn.value();
    }

    public void cancel(BPN applicationBpn) {
        validateBPN(applicationBpn);
        changeStatusTo(QualityNotificationStatus.CANCELED);
        this.closeReason = "canceled";
    }

    public void close(BPN applicationBpn, String reason) {
        validateBPN(applicationBpn);
        changeStatusTo(QualityNotificationStatus.CLOSED);
        this.closeReason = reason;
        this.notifications.forEach(notification -> notification.setDescription(reason));
    }

    public void acknowledge() {
        changeStatusTo(QualityNotificationStatus.ACKNOWLEDGED);
    }

    public void accept(String reason) {
        changeStatusTo(QualityNotificationStatus.ACCEPTED);
        this.acceptReason = reason;
    }

    public void decline(String reason) {
        changeStatusTo(QualityNotificationStatus.DECLINED);
        this.declineReason = reason;
    }

    public void close(String reason) {
        changeStatusTo(QualityNotificationStatus.CLOSED);
        this.closeReason = reason;
    }

    public void send(BPN applicationBpn) {
        validateBPN(applicationBpn);
        changeStatusTo(QualityNotificationStatus.SENT);
    }

    private void validateBPN(BPN applicationBpn) {
        if (!applicationBpn.equals(this.bpn)) {
            throw new InvestigationIllegalUpdate("%s bpn has no permissions to update investigation with %s id.".formatted(applicationBpn.value(), notificationId.value()));
        }
    }

    private void changeStatusTo(QualityNotificationStatus to) {
        boolean transitionAllowed = notificationStatus.transitionAllowed(to);

        if (!transitionAllowed) {
            throw new InvestigationStatusTransitionNotAllowed(notificationId, notificationStatus, to);
        }
        this.notificationStatus = to;
    }

    public void addNotification(QualityNotificationMessage notification) {

        List<QualityNotificationMessage> updatedNotifications = new ArrayList<>(notifications);
        updatedNotifications.add(notification);
        notifications = Collections.unmodifiableList(updatedNotifications);

        List<String> newAssetIds = new ArrayList<>(assetIds); // create a mutable copy of assetIds
        notification.getAffectedParts().stream()
                .map(QualityNotificationAffectedPart::assetId)
                .forEach(newAssetIds::add);

        assetIds = Collections.unmodifiableList(newAssetIds); //
    }

    public void addNotifications(List<QualityNotificationMessage> notificationMessages) {
        notificationMessages.forEach(this::addNotification);
    }

    public boolean isActiveState() {
        return this.notificationStatus.isActiveState();
    }

    public List<QualityNotificationMessage> secondLatestNotifications() {

        Optional<QualityNotificationMessage> highestState = notifications.stream()
                .max(Comparator.comparing(QualityNotificationMessage::getCreated));

        if (highestState.isPresent()) {
            QualityNotificationMessage highestMessage = highestState.get();
            QualityNotificationStatus highestStatus = highestMessage.getNotificationStatus();
            log.info("Highest status found: {}", highestStatus);

            Optional<QualityNotificationMessage> secondHighestState = notifications.stream()
                    .filter(message -> !message.getNotificationStatus().equals(highestStatus))
                    .max(Comparator.comparing(QualityNotificationMessage::getCreated));

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
