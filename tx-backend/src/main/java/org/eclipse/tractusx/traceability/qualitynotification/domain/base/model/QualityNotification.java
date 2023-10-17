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
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationStatusTransitionNotAllowed;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Data
@Builder
public class QualityNotification {
    private BPN bpn;
    private QualityNotificationId notificationId;
    private QualityNotificationStatus notificationStatus;
    private String description;
    // TODO date
    private Instant createdAt;
    private QualityNotificationSide notificationSide;
    @Builder.Default
    private List<String> assetIds = new ArrayList<>();
    private String closeReason;
    private String acceptReason;
    private String declineReason;
    private Map<String, QualityNotificationMessage> notifications = new HashMap<>();
    private String errorMessage;

    public static QualityNotification startNotification(Instant createDate, BPN bpn, String description) { // rename to generic
        return QualityNotification.builder()
                .bpn(bpn)
                .notificationStatus(QualityNotificationStatus.CREATED)
                .notificationSide(QualityNotificationSide.SENDER)
                .description(description)
                .createdAt(createDate)
                .assetIds(Collections.emptyList())
                .notifications(Collections.emptyList())
                .errorMessage(null)
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
        if (this.notifications != null) {
            this.notifications.values()
                    .forEach(notification -> notification.setDescription(reason));
        }

    }

    public void acknowledge(QualityNotificationMessage notification) {
        changeStatusToWithoutNotifications(QualityNotificationStatus.ACKNOWLEDGED);
        setNotificationStatusAndReasonForNotification(notification, QualityNotificationStatus.ACKNOWLEDGED, null);
        notification.setNotificationStatus(QualityNotificationStatus.ACKNOWLEDGED);
    }

    public void accept(String reason, QualityNotificationMessage notification) {
        changeStatusToWithoutNotifications(QualityNotificationStatus.ACCEPTED);
        this.acceptReason = reason;
        setNotificationStatusAndReasonForNotification(notification, QualityNotificationStatus.ACCEPTED, reason);
        notification.setNotificationStatus(QualityNotificationStatus.ACCEPTED);
        notification.setDescription(reason);
    }

    public void decline(String reason, QualityNotificationMessage notification) {
        changeStatusToWithoutNotifications(QualityNotificationStatus.DECLINED);
        this.declineReason = reason;
        setNotificationStatusAndReasonForNotification(notification, QualityNotificationStatus.DECLINED, reason);
        notification.setNotificationStatus(QualityNotificationStatus.DECLINED);
        notification.setDescription(reason);
    }

    public void close(String reason, QualityNotificationMessage notification) {
        changeStatusToWithoutNotifications(QualityNotificationStatus.CLOSED);
        this.closeReason = reason;
        setNotificationStatusAndReasonForNotification(notification, QualityNotificationStatus.CLOSED, reason);
        notification.setNotificationStatus(QualityNotificationStatus.CLOSED);
        notification.setDescription(reason);
    }

    public void send(BPN applicationBpn) {
        validateBPN(applicationBpn);
        changeStatusTo(QualityNotificationStatus.SENT);
    }

    private void setNotificationStatusAndReasonForNotification(QualityNotificationMessage notificationDomain, QualityNotificationStatus notificationStatus, String reason) {
        if (this.notifications != null) {
            for (QualityNotificationMessage notification : this.notifications.values()) {
                if (notification.getId().equals(notificationDomain.getId())) {
                    if (reason != null) {
                        notification.setDescription(reason);
                    }
                    notification.setNotificationStatus(notificationStatus);
                    break;
                }
            }
        }
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

        if (notifications != null) {
            notifications.values()
                    .forEach(notification -> notification.changeStatusTo(to));
        }

        this.notificationStatus = to;
    }

    private void changeStatusToWithoutNotifications(QualityNotificationStatus to) {
        boolean transitionAllowed = notificationStatus.transitionAllowed(to);

        if (!transitionAllowed) {
            throw new InvestigationStatusTransitionNotAllowed(notificationId, notificationStatus, to);
        }

        this.notificationStatus = to;
    }

    public List<QualityNotificationMessage> getNotifications() {
        return new ArrayList<>(notifications.values());
    }

    public void addNotification(QualityNotificationMessage notification) {
        notifications.put(notification.getId(), notification);

        List<String> newAssetIds = new ArrayList<>(assetIds); // create a mutable copy of assetIds
        notification.getAffectedParts().stream()
                .map(QualityNotificationAffectedPart::assetId)
                .forEach(newAssetIds::add);

        assetIds = Collections.unmodifiableList(newAssetIds); //
    }

    public boolean isActiveState() {
        return this.notificationStatus.isActiveState();
    }

    public static class QualityNotificationBuilder {
        public QualityNotificationBuilder notifications(List<QualityNotificationMessage> notifications) {
            this.notifications = emptyIfNull(notifications).stream()
                    .collect(Collectors.toMap(QualityNotificationMessage::getId, Function.identity()));
            return this;
        }
    }
}
