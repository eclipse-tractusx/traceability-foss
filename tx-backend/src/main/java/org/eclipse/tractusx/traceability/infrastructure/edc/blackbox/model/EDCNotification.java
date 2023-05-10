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
package org.eclipse.tractusx.traceability.infrastructure.edc.blackbox.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationAffectedPart;
import org.eclipse.tractusx.traceability.qualitynotification.domain.model.QualityNotificationStatus;

import java.time.Instant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EDCNotification(@Valid
                              @NotNull
                              @Schema(description = "Header of the EDC notification",
                                      implementation = EDCNotificationHeader.class) EDCNotificationHeader header,
                              @NotNull EDCNotificationContent content) {

    @JsonIgnore
    public String getRecipientBPN() {
        return header.recipientBPN();
    }

    @JsonIgnore
    public String getNotificationId() {
        return header.notificationId();
    }

    @JsonIgnore
    public String getSenderBPN() {
        return header.senderBPN();
    }

    @JsonIgnore
    public String getSenderAddress() {
        return header.senderAddress();
    }

    @JsonIgnore
    public String getInformation() {
        return content.information();
    }

    @JsonIgnore
    public String getSeverity() {
        return header.severity();
    }

    @JsonIgnore
    public String getMessageId() {
        return header.messageId();
    }

    @JsonIgnore
    public String getRelatedNotificationId() {
        return header.relatedNotificationId();
    }

    @JsonIgnore
    public List<QualityNotificationAffectedPart> getListOfAffectedItems() {
        return content.listOfAffectedItems().stream()
                .map(QualityNotificationAffectedPart::new).toList();
    }

    public NotificationType convertNotificationType() {
        String classification = header().classification();

        return NotificationType.fromValue(classification)
                .orElseThrow(() -> new IllegalArgumentException("%s not supported notification type".formatted(classification)));
    }

    public QualityNotificationStatus convertInvestigationStatus() {
        String investigationStatus = header().status();

        return QualityNotificationStatus.fromValue(investigationStatus)
                .orElseThrow(() -> new IllegalArgumentException("%s not supported investigation status".formatted(investigationStatus)));
    }

    @JsonIgnore
    public Instant getTargetDate() {
        if (header.targetDate() != null && !StringUtils.isBlank(header.targetDate())) {
            return Instant.parse(header.targetDate());
        }
        return null;
    }
}

