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

package org.eclipse.tractusx.traceability.investigations.domain.model;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.eclipse.tractusx.traceability.common.model.BPN;
import org.eclipse.tractusx.traceability.investigations.domain.model.exception.NotificationStatusTransitionNotAllowed;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNullElseGet;

public class Notification {
    private final String id;
    private String notificationReferenceId;
    private String senderBpnNumber;
    private final String senderManufacturerName;
    private String receiverBpnNumber;
    private final String receiverManufacturerName;
    private String edcUrl;
    private String contractAgreementId;
    private final List<AffectedPart> affectedParts;
    private String description;
    private InvestigationStatus investigationStatus;
    private String edcNotificationId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Instant targetDate;
    private Severity severity;
    private String messageId;

    public Notification(String id,
                        String notificationReferenceId,
                        String senderBpnNumber,
                        String senderManufacturerName,
                        String receiverBpnNumber,
                        String receiverManufacturerName,
                        String edcUrl,
                        String contractAgreementId,
                        String description,
                        InvestigationStatus investigationStatus,
                        List<AffectedPart> affectedParts,
                        Instant targetDate,
                        Severity severity,
                        String edcNotificationId,
                        LocalDateTime created,
                        LocalDateTime updated,
                        String messageId) {
        this.id = id;
        this.notificationReferenceId = notificationReferenceId;
        this.senderBpnNumber = senderBpnNumber;
        this.senderManufacturerName = senderManufacturerName;
        this.receiverBpnNumber = receiverBpnNumber;
        this.receiverManufacturerName = receiverManufacturerName;
        this.edcUrl = edcUrl;
        this.contractAgreementId = contractAgreementId;
        this.description = description;
        this.investigationStatus = investigationStatus;
        this.affectedParts = requireNonNullElseGet(affectedParts, ArrayList::new);
        this.targetDate = targetDate;
        this.severity = severity;
        this.edcNotificationId = edcNotificationId;
        this.created = created;
        this.updated = updated;
        this.messageId = messageId;
    }

    void changeStatusTo(InvestigationStatus to) {
        boolean transitionAllowed = investigationStatus.transitionAllowed(to);

        if (!transitionAllowed) {
            throw new NotificationStatusTransitionNotAllowed(id, investigationStatus, to);
        }
        this.investigationStatus = to;
    }

    public void updateNotificationReferenceId(String notificationReferenceId) {
        this.notificationReferenceId = notificationReferenceId;
    }

    public boolean existOnReceiverSide() {
        return StringUtils.isNotBlank(this.getNotificationReferenceId());
    }

    public String getId() {
        return id;
    }

    public String getNotificationReferenceId() {
        return notificationReferenceId;
    }

    public String getContractAgreementId() {
        return contractAgreementId;
    }

    public void setContractAgreementId(String contractAgreementId) {
        this.contractAgreementId = contractAgreementId;
    }

    public List<AffectedPart> getAffectedParts() {
        return affectedParts;
    }

    public String getSenderBpnNumber() {
        return senderBpnNumber;
    }

    public void setSenderBpnNumber(String senderBpnNumber) {
        this.senderBpnNumber = senderBpnNumber;
    }

    public void setReceiverBpnNumber(String receiverBpnNumber) {
        this.receiverBpnNumber = receiverBpnNumber;
    }

    public String getSenderManufacturerName() {
        return senderManufacturerName;
    }

    public String getReceiverBpnNumber() {
        return receiverBpnNumber;
    }

    public String getReceiverManufacturerName() {
        return receiverManufacturerName;
    }

    public void setEdcUrl(String edcUrl) {
        this.edcUrl = edcUrl;
    }

    public String getEdcUrl() {
        return edcUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public InvestigationStatus getInvestigationStatus() {
        return investigationStatus;
    }

    public Instant getTargetDate() {
        return this.targetDate;
    }

    public void setInvestigationStatus(InvestigationStatus investigationStatus) {
        this.investigationStatus = investigationStatus;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public void setTargetDate(Instant targetDate) {
        this.targetDate = targetDate;
    }

    public void setNotificationReferenceId(String notificationReferenceId) {
        this.notificationReferenceId = notificationReferenceId;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    public String getEdcNotificationId() {
        return edcNotificationId;
    }

    public void setEdcNotificationId(String edcNotificationId) {
        this.edcNotificationId = edcNotificationId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    // Important - receiver and sender will be saved in switched order
    public Notification copyAndSwitchSenderAndReceiver(BPN applicationBpn) {
        final String notificationId = UUID.randomUUID().toString();
        String receiver = receiverBpnNumber;
        String sender = senderBpnNumber;
        String receiverManufactureName = receiverManufacturerName;
        String senderManufactureName = senderManufacturerName;

        // This is needed to make sure that the app can send a message to the receiver and not addresses itself
        if (applicationBpn.value().equals(receiverBpnNumber)) {
            receiver = senderBpnNumber;
            sender = receiverBpnNumber;
            receiverManufactureName = senderManufacturerName;
            senderManufactureName = receiverManufacturerName;
        }
        return new Notification(
                notificationId,
                null,
                sender,
                senderManufactureName,
                receiver,
                receiverManufactureName,
                edcUrl,
                contractAgreementId,
                description,
                investigationStatus,
                affectedParts,
                targetDate,
                severity,
                edcNotificationId,
                created,
                updated,
                UUID.randomUUID().toString()
        );
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", notificationReferenceId='" + notificationReferenceId + '\'' +
                ", senderBpnNumber='" + senderBpnNumber + '\'' +
                ", senderManufacturerName='" + senderManufacturerName + '\'' +
                ", receiverBpnNumber='" + receiverBpnNumber + '\'' +
                ", receiverManufacturerName='" + receiverManufacturerName + '\'' +
                ", edcUrl='" + edcUrl + '\'' +
                ", contractAgreementId='" + contractAgreementId + '\'' +
                ", affectedParts=" + affectedParts +
                ", description='" + description + '\'' +
                ", investigationStatus=" + investigationStatus +
                ", edcNotificationId='" + edcNotificationId + '\'' +
                ", created=" + created +
                ", updated=" + updated +
                ", targetDate=" + targetDate +
                ", severity=" + severity +
                ", messageId='" + messageId + '\'' +
                '}';
    }
}
