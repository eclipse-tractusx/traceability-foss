/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model;

import lombok.Data;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.qualitynotification.application.investigation.response.InvestigationDTO;
import org.eclipse.tractusx.traceability.qualitynotification.application.investigation.response.InvestigationReason;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationIllegalUpdate;
import org.eclipse.tractusx.traceability.qualitynotification.domain.investigation.model.exception.InvestigationStatusTransitionNotAllowed;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
public class Investigation {

    private InvestigationId investigationId;
    private BPN bpn;
    private InvestigationStatus investigationStatus;
    private InvestigationSide investigationSide;
    private String description;
    private Instant createdAt;
    private List<String> assetIds;
    private Map<String, Notification> notifications;
    private String closeReason;
    private String acceptReason;
    private String declineReason;

    public Investigation(InvestigationId investigationId,
                         BPN bpn,
                         InvestigationStatus investigationStatus,
                         InvestigationSide investigationSide,
                         String closeReason,
                         String acceptReason,
                         String declineReason,
                         String description,
                         Instant createdAt,
                         List<String> assetIds,
                         List<Notification> notifications
    ) {
        this.investigationId = investigationId;
        this.bpn = bpn;
        this.investigationStatus = investigationStatus;
        this.investigationSide = investigationSide;
        this.closeReason = closeReason;
        this.acceptReason = acceptReason;
        this.declineReason = declineReason;
        this.description = description;
        this.createdAt = createdAt;
        this.assetIds = assetIds;
        this.notifications = notifications.stream()
                .collect(Collectors.toMap(Notification::getId, Function.identity()));
    }

    public static final Comparator<Investigation> COMPARE_BY_NEWEST_INVESTIGATION_CREATION_TIME = (o1, o2) -> {
        Instant o1CreationTime = o1.createdAt;
        Instant o2CreationTime = o2.createdAt;

        if (o1CreationTime.equals(o2CreationTime)) {
            return 0;
        }

        if (o1CreationTime.isBefore(o2CreationTime)) {
            return 1;
        }

        return -1;
    };

    public static Investigation startInvestigation(Instant createDate, BPN bpn, String description) {
        return new Investigation(null,
                bpn,
                InvestigationStatus.CREATED,
                InvestigationSide.SENDER,
                null,
                null,
                null,
                description,
                createDate,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    private static String getSenderBPN(Collection<Notification> notifications) {
        return notifications.stream()
                .findFirst()
                .map(Notification::getSenderBpnNumber)
                .orElse(null);
    }

    private static String getReceiverBPN(Collection<Notification> notifications) {
        return notifications.stream()
                .findFirst()
                .map(Notification::getReceiverBpnNumber)
                .orElse(null);
    }

    public List<String> getAssetIds() {
        return Collections.unmodifiableList(assetIds);
    }

    public InvestigationDTO toDTO() {
        return InvestigationDTO
                .builder()
                .id(investigationId.value())
                .status(investigationStatus.name())
                .description(description)
                .createdBy(getSenderBPN(notifications.values()))
                .createdByName(getSenderName(notifications.values()))
                .createdDate(createdAt.toString())
                .assetIds(Collections.unmodifiableList(assetIds))
                .channel(investigationSide)
                .reason(new InvestigationReason(
                        closeReason,
                        acceptReason,
                        declineReason
                ))
                .sendTo(getReceiverBPN(notifications.values()))
                .sendToName(getReceiverName(notifications.values()))
                .severity(notifications.entrySet().stream().findFirst().map(Map.Entry::getValue).map(Notification::getSeverity).orElse(Severity.MINOR).getRealName())
                .targetDate(notifications.entrySet().stream().findFirst().map(Map.Entry::getValue).map(Notification::getTargetDate).map(Instant::toString).orElse(null)).build();
    }

    public String getBpn() {
        return bpn.value();
    }

    public void cancel(BPN applicationBpn) {
        validateBPN(applicationBpn);
        changeStatusTo(InvestigationStatus.CANCELED);
        this.closeReason = "canceled";
    }

    public void close(BPN applicationBpn, String reason) {
        validateBPN(applicationBpn);
        changeStatusTo(InvestigationStatus.CLOSED);
        this.closeReason = reason;
        this.notifications.values()
                .forEach(notification -> notification.setDescription(reason));
    }

    public void acknowledge(Notification notification) {
        changeStatusToWithoutNotifications(InvestigationStatus.ACKNOWLEDGED);
        setInvestigationStatusAndReasonForNotification(notification, InvestigationStatus.ACKNOWLEDGED, null);
        notification.setInvestigationStatus(InvestigationStatus.ACKNOWLEDGED);
    }

    public void accept(String reason, Notification notification) {
        changeStatusToWithoutNotifications(InvestigationStatus.ACCEPTED);
        this.acceptReason = reason;
        setInvestigationStatusAndReasonForNotification(notification, InvestigationStatus.ACCEPTED, reason);
        notification.setInvestigationStatus(InvestigationStatus.ACCEPTED);
        notification.setDescription(reason);
    }

    public void decline(String reason, Notification notification) {
        changeStatusToWithoutNotifications(InvestigationStatus.DECLINED);
        this.declineReason = reason;
        setInvestigationStatusAndReasonForNotification(notification, InvestigationStatus.DECLINED, reason);
        notification.setInvestigationStatus(InvestigationStatus.DECLINED);
        notification.setDescription(reason);
    }

    public void close(String reason, Notification notification) {
        changeStatusToWithoutNotifications(InvestigationStatus.CLOSED);
        this.closeReason = reason;
        setInvestigationStatusAndReasonForNotification(notification, InvestigationStatus.CLOSED, reason);
        notification.setInvestigationStatus(InvestigationStatus.CLOSED);
        notification.setDescription(reason);
    }

    public void send(BPN applicationBpn) {
        validateBPN(applicationBpn);
        changeStatusTo(InvestigationStatus.SENT);
    }

    private void setInvestigationStatusAndReasonForNotification(Notification notificationDomain, InvestigationStatus investigationStatus, String reason) {
        for (Notification notification : this.notifications.values()) {
            if (notification.getId().equals(notificationDomain.getId())) {
                if (reason != null) {
                    notification.setDescription(reason);
                }
                notification.setInvestigationStatus(investigationStatus);
                break;
            }
        }
    }

    private void validateBPN(BPN applicationBpn) {
        if (!applicationBpn.equals(this.bpn)) {
            throw new InvestigationIllegalUpdate("%s bpn has no permissions to update investigation with %s id.".formatted(applicationBpn.value(), investigationId.value()));
        }
    }

    private void changeStatusTo(InvestigationStatus to) {
        boolean transitionAllowed = investigationStatus.transitionAllowed(to);

        if (!transitionAllowed) {
            throw new InvestigationStatusTransitionNotAllowed(investigationId, investigationStatus, to);
        }

        notifications.values()
                .forEach(notification -> notification.changeStatusTo(to));

        this.investigationStatus = to;
    }

    private void changeStatusToWithoutNotifications(InvestigationStatus to) {
        boolean transitionAllowed = investigationStatus.transitionAllowed(to);

        if (!transitionAllowed) {
            throw new InvestigationStatusTransitionNotAllowed(investigationId, investigationStatus, to);
        }

        this.investigationStatus = to;
    }

    public List<Notification> getNotifications() {
        return new ArrayList<>(notifications.values());
    }

    public void addNotification(Notification notification) {
        notifications.put(notification.getId(), notification);

        List<String> newAssetIds = new ArrayList<>(assetIds); // create a mutable copy of assetIds
        notification.getAffectedParts().stream()
                .map(AffectedPart::assetId)
                .forEach(newAssetIds::add);

        assetIds = Collections.unmodifiableList(newAssetIds); //
    }

    private static String getSenderName(Collection<Notification> notifications) {
        return notifications.stream()
                .findFirst()
                .map(Notification::getSenderManufacturerName)
                .orElse(null);
    }

    private static String getReceiverName(Collection<Notification> notifications) {
        return notifications.stream()
                .findFirst()
                .map(Notification::getReceiverManufacturerName)
                .orElse(null);
    }
}
